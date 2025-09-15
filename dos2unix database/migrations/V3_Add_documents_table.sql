-- =============================================================================
-- Migration V3: Add Documents Table
-- Description: Adds document management functionality for loans and conditions
-- =============================================================================

-- =============================================================================
-- CREATE SEQUENCE FOR DOCUMENTS
-- =============================================================================

CREATE SEQUENCE document_sequence 
    START WITH 1 
    INCREMENT BY 1 
    NOCACHE 
    NOCYCLE;

-- =============================================================================
-- DOCUMENTS TABLE
-- =============================================================================

CREATE TABLE documents (
    id                    NUMBER PRIMARY KEY,
    loan_id              NUMBER NOT NULL,
    condition_id         NUMBER,
    document_type        VARCHAR2(100) NOT NULL,
    document_name        VARCHAR2(255) NOT NULL,
    file_name            VARCHAR2(500) NOT NULL,
    file_path            VARCHAR2(1000) NOT NULL,
    file_size            NUMBER,
    mime_type            VARCHAR2(100),
    
    -- Status and Tracking
    status               VARCHAR2(50) DEFAULT 'RECEIVED' NOT NULL,
    uploaded_by_id       NUMBER NOT NULL,
    reviewed_by_id       NUMBER,
    review_notes         CLOB,
    
    -- Version Control
    version_number       NUMBER(3) DEFAULT 1 NOT NULL,
    is_current_version   NUMBER(1) DEFAULT 1 NOT NULL,
    replaced_by_id       NUMBER, -- Reference to newer version
    
    -- Metadata
    document_tags        VARCHAR2(1000), -- Comma-separated tags
    is_required          NUMBER(1) DEFAULT 0 NOT NULL,
    expiration_date      DATE,
    
    -- Timestamps
    uploaded_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    reviewed_at          TIMESTAMP,
    
    -- Foreign Keys
    CONSTRAINT fk_documents_loan FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE,
    CONSTRAINT fk_documents_condition FOREIGN KEY (condition_id) REFERENCES loan_conditions(id),
    CONSTRAINT fk_documents_uploaded_by FOREIGN KEY (uploaded_by_id) REFERENCES users(id),
    CONSTRAINT fk_documents_reviewed_by FOREIGN KEY (reviewed_by_id) REFERENCES users(id),
    CONSTRAINT fk_documents_replaced_by FOREIGN KEY (replaced_by_id) REFERENCES documents(id),
    
    -- Check Constraints
    CONSTRAINT chk_documents_status CHECK (status IN ('RECEIVED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'NEEDS_REVISION', 'EXPIRED')),
    CONSTRAINT chk_documents_is_current CHECK (is_current_version IN (0, 1)),
    CONSTRAINT chk_documents_is_required CHECK (is_required IN (0, 1)),
    CONSTRAINT chk_documents_version CHECK (version_number > 0)
);

-- =============================================================================
-- DOCUMENT CATEGORIES LOOKUP TABLE
-- =============================================================================

CREATE TABLE document_categories (
    id                   NUMBER PRIMARY KEY,
    category_name        VARCHAR2(100) NOT NULL UNIQUE,
    category_code        VARCHAR2(20) NOT NULL UNIQUE,
    description          VARCHAR2(500),
    is_required_default  NUMBER(1) DEFAULT 0 NOT NULL,
    loan_types           VARCHAR2(200), -- Comma-separated loan types where this applies
    sort_order           NUMBER(3) DEFAULT 999,
    active               NUMBER(1) DEFAULT 1 NOT NULL,
    
    CONSTRAINT chk_doc_cat_required CHECK (is_required_default IN (0, 1)),
    CONSTRAINT chk_doc_cat_active CHECK (active IN (0, 1))
);

-- =============================================================================
-- INDEXES FOR DOCUMENTS
-- =============================================================================

CREATE INDEX idx_documents_loan_id ON documents(loan_id);
CREATE INDEX idx_documents_condition_id ON documents(condition_id);
CREATE INDEX idx_documents_type ON documents(document_type);
CREATE INDEX idx_documents_status ON documents(status);
CREATE INDEX idx_documents_uploaded_by ON documents(uploaded_by_id);
CREATE INDEX idx_documents_reviewed_by ON documents(reviewed_by_id);
CREATE INDEX idx_documents_uploaded_at ON documents(uploaded_at);
CREATE INDEX idx_documents_current_version ON documents(is_current_version, loan_id);
CREATE INDEX idx_documents_expiration ON documents(expiration_date);

-- Indexes for document categories
CREATE INDEX idx_doc_categories_code ON document_categories(category_code);
CREATE INDEX idx_doc_categories_active ON document_categories(active);

-- =============================================================================
-- DOCUMENT MANAGEMENT PROCEDURES
-- =============================================================================

-- Upload document with version control
CREATE OR REPLACE PROCEDURE upload_document(
    p_loan_id IN NUMBER,
    p_condition_id IN NUMBER DEFAULT NULL,
    p_document_type IN VARCHAR2,
    p_document_name IN VARCHAR2,
    p_file_name IN VARCHAR2,
    p_file_path IN VARCHAR2,
    p_file_size IN NUMBER DEFAULT NULL,
    p_mime_type IN VARCHAR2 DEFAULT NULL,
    p_uploaded_by_id IN NUMBER,
    p_document_tags IN VARCHAR2 DEFAULT NULL,
    p_is_required IN NUMBER DEFAULT 0,
    p_expiration_date IN DATE DEFAULT NULL,
    p_document_id OUT NUMBER
) IS
    v_version_number NUMBER := 1;
    v_previous_doc_id NUMBER;
BEGIN
    -- Check if this is a new version of existing document
    SELECT NVL(MAX(version_number), 0) + 1, MAX(id)
    INTO v_version_number, v_previous_doc_id
    FROM documents
    WHERE loan_id = p_loan_id
    AND document_type = p_document_type
    AND NVL(condition_id, -1) = NVL(p_condition_id, -1);
    
    -- Mark previous versions as not current
    IF v_version_number > 1 THEN
        UPDATE documents
        SET is_current_version = 0
        WHERE loan_id = p_loan_id
        AND document_type = p_document_type
        AND NVL(condition_id, -1) = NVL(p_condition_id, -1)
        AND is_current_version = 1;
    END IF;
    
    -- Insert new document
    INSERT INTO documents (
        id, loan_id, condition_id, document_type, document_name,
        file_name, file_path, file_size, mime_type, status,
        uploaded_by_id, version_number, is_current_version,
        document_tags, is_required, expiration_date, uploaded_at
    ) VALUES (
        document_sequence.NEXTVAL, p_loan_id, p_condition_id, p_document_type, 
        p_document_name, p_file_name, p_file_path, p_file_size, p_mime_type,
        'RECEIVED', p_uploaded_by_id, v_version_number, 1,
        p_document_tags, p_is_required, p_expiration_date, CURRENT_TIMESTAMP
    ) RETURNING id INTO p_document_id;
    
    -- Update replaced_by_id in previous version
    IF v_previous_doc_id IS NOT NULL AND v_version_number > 1 THEN
        UPDATE documents 
        SET replaced_by_id = p_document_id
        WHERE id = v_previous_doc_id;
    END IF;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END upload_document;
/

-- Review document
CREATE OR REPLACE PROCEDURE review_document(
    p_document_id IN NUMBER,
    p_status IN VARCHAR2,
    p_reviewed_by_id IN NUMBER,
    p_review_notes IN CLOB DEFAULT NULL
) IS
    v_condition_id NUMBER;
BEGIN
    -- Get condition ID if document is linked to a condition
    SELECT condition_id INTO v_condition_id
    FROM documents 
    WHERE id = p_document_id;
    
    -- Update document status
    UPDATE documents
    SET status = p_status,
        reviewed_by_id = p_reviewed_by_id,
        review_notes = p_review_notes,
        reviewed_at = CURRENT_TIMESTAMP
    WHERE id = p_document_id;
    
    -- If document is approved and linked to a condition, update condition status
    IF p_status = 'APPROVED' AND v_condition_id IS NOT NULL THEN
        UPDATE loan_conditions
        SET status = CASE 
            WHEN status = 'OUTSTANDING' THEN 'IN_PROGRESS'
            ELSE status
        END,
        notes = NVL(notes, '') || 
               CASE WHEN notes IS NOT NULL THEN '; ' ELSE '' END ||
               'Document approved: ' || TO_CHAR(CURRENT_TIMESTAMP, 'YYYY-MM-DD HH24:MI:SS'),
        updated_at = CURRENT_TIMESTAMP
        WHERE id = v_condition_id;
    END IF;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END review_document;
/

-- Get documents for a loan
CREATE OR REPLACE PROCEDURE get_loan_documents(
    p_loan_id IN NUMBER,
    p_current_version_only IN NUMBER DEFAULT 1,
    p_result_cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_result_cursor FOR
        SELECT 
            d.id,
            d.document_type,
            d.document_name,
            d.file_name,
            d.file_path,
            d.file_size,
            d.mime_type,
            d.status,
            d.version_number,
            d.is_current_version,
            d.document_tags,
            d.is_required,
            d.expiration_date,
            d.uploaded_at,
            d.reviewed_at,
            uploader.first_name as uploaded_by_first_name,
            uploader.last_name as uploaded_by_last_name,
            reviewer.first_name as reviewed_by_first_name,
            reviewer.last_name as reviewed_by_last_name,
            d.review_notes,
            lc.title as condition_title,
            lc.status as condition_status
        FROM documents d
        LEFT JOIN users uploader ON d.uploaded_by_id = uploader.id
        LEFT JOIN users reviewer ON d.reviewed_by_id = reviewer.id
        LEFT JOIN loan_conditions lc ON d.condition_id = lc.id
        WHERE d.loan_id = p_loan_id
        AND (p_current_version_only = 0 OR d.is_current_version = 1)
        ORDER BY d.is_required DESC, d.document_type, d.version_number DESC;
END get_loan_documents;
/

-- Get pending document reviews
CREATE OR REPLACE PROCEDURE get_pending_document_reviews(
    p_reviewer_id IN NUMBER DEFAULT NULL,
    p_result_cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_result_cursor FOR
        SELECT 
            d.id,
            d.document_name,
            d.document_type,
            d.uploaded_at,
            l.loan_number,
            l.borrower_first_name,
            l.borrower_last_name,
            l.status as loan_status,
            uploader.first_name as uploaded_by_first_name,
            uploader.last_name as uploaded_by_last_name,
            CASE 
                WHEN d.expiration_date < SYSDATE THEN 'EXPIRED'
                WHEN d.uploaded_at < SYSDATE - 3 THEN 'URGENT'
                WHEN d.uploaded_at < SYSDATE - 1 THEN 'HIGH'
                ELSE 'NORMAL'
            END as review_priority,
            ROUND(SYSDATE - d.uploaded_at, 1) as days_waiting
        FROM documents d
        JOIN loans l ON d.loan_id = l.id
        LEFT JOIN users uploader ON d.uploaded_by_id = uploader.id
        WHERE d.status = 'RECEIVED'
        AND d.is_current_version = 1
        AND (p_reviewer_id IS NULL OR 
             p_reviewer_id IN (l.processor_id, l.underwriter_id, l.loan_officer_id))
        ORDER BY 
            CASE 
                WHEN d.expiration_date < SYSDATE THEN 1
                WHEN d.uploaded_at < SYSDATE - 3 THEN 2
                WHEN d.uploaded_at < SYSDATE - 1 THEN 3
                ELSE 4
            END,
            d.uploaded_at ASC;
END get_pending_document_reviews;
/

-- Get document statistics
CREATE OR REPLACE PROCEDURE get_document_statistics(
    p_loan_id IN NUMBER DEFAULT NULL,
    p_start_date IN DATE DEFAULT TRUNC(SYSDATE, 'MM'),
    p_end_date IN DATE DEFAULT SYSDATE,
    p_result_cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_result_cursor FOR
        SELECT 
            'Total Documents Uploaded' as metric_name,
            COUNT(*) as metric_value,
            'documents' as metric_unit
        FROM documents d
        WHERE (p_loan_id IS NULL OR d.loan_id = p_loan_id)
        AND d.uploaded_at BETWEEN p_start_date AND p_end_date
        
        UNION ALL
        
        SELECT 
            'Documents Approved',
            COUNT(*),
            'documents'
        FROM documents d
        WHERE (p_loan_id IS NULL OR d.loan_id = p_loan_id)
        AND d.status = 'APPROVED'
        AND d.reviewed_at BETWEEN p_start_date AND p_end_date
        
        UNION ALL
        
        SELECT 
            'Documents Pending Review',
            COUNT(*),
            'documents'
        FROM documents d
        WHERE (p_loan_id IS NULL OR d.loan_id = p_loan_id)
        AND d.status = 'RECEIVED'
        AND d.is_current_version = 1
        
        UNION ALL
        
        SELECT 
            'Documents Rejected',
            COUNT(*),
            'documents'
        FROM documents d
        WHERE (p_loan_id IS NULL OR d.loan_id = p_loan_id)
        AND d.status = 'REJECTED'
        AND d.reviewed_at BETWEEN p_start_date AND p_end_date
        
        UNION ALL
        
        SELECT 
            'Average Review Time (Hours)',
            ROUND(AVG((d.reviewed_at - d.uploaded_at) * 24), 2),
            'hours'
        FROM documents d
        WHERE (p_loan_id IS NULL OR d.loan_id = p_loan_id)
        AND d.reviewed_at IS NOT NULL
        AND d.reviewed_at BETWEEN p_start_date AND p_end_date;
END get_document_statistics;
/

-- =============================================================================
-- POPULATE DOCUMENT CATEGORIES
-- =============================================================================

INSERT INTO document_categories (id, category_name, category_code, description, is_required_default, loan_types, sort_order, active) VALUES
(1, 'Income Documentation', 'INCOME', 'Documents verifying borrower income and employment', 1, 'ALL', 10, 1);

INSERT INTO document_categories (id, category_name, category_code, description, is_required_default, loan_types, sort_order, active) VALUES
(2, 'Asset Documentation', 'ASSETS', 'Bank statements and asset verification documents', 1, 'ALL', 20, 1);

INSERT INTO document_categories (id, category_name, category_code, description, is_required_default, loan_types, sort_order, active) VALUES
(3, 'Property Documentation', 'PROPERTY', 'Property appraisal, survey, and inspection documents', 1, 'ALL', 30, 1);

INSERT INTO document_categories (id, category_name, category_code, description, is_required_default, loan_types, sort_order, active) VALUES
(4, 'Credit Documentation', 'CREDIT', 'Credit reports and explanation letters', 1, 'ALL', 40, 1);

INSERT INTO document_categories (id, category_name, category_code, description, is_required_default, loan_types, sort_order, active) VALUES
(5, 'Government Documentation', 'GOVERNMENT', 'VA, FHA, USDA specific documents', 1, 'VA,FHA,USDA', 50, 1);

INSERT INTO document_categories (id, category_name, category_code, description, is_required_default, loan_types, sort_order, active) VALUES
(6, 'Legal Documentation', 'LEGAL', 'Purchase agreements, title documents, legal disclosures', 1, 'ALL', 60, 1);

INSERT INTO document_categories (id, category_name, category_code, description, is_required_default, loan_types, sort_order, active) VALUES
(7, 'Insurance Documentation', 'INSURANCE', 'Homeowner insurance, PMI, and related insurance documents', 1, 'ALL', 70, 1);

INSERT INTO document_categories (id, category_name, category_code, description, is_required_default, loan_types, sort_order, active) VALUES
(8, 'Miscellaneous', 'MISC', 'Other supporting documents', 0, 'ALL', 999, 1);

-- =============================================================================
-- TRIGGERS FOR AUTOMATIC PROCESSING
-- =============================================================================

-- Trigger to automatically mark expired documents
CREATE OR REPLACE TRIGGER trg_documents_expiration_check
    BEFORE INSERT OR UPDATE ON documents
    FOR EACH ROW
BEGIN
    -- Check if document is expired
    IF :NEW.expiration_date IS NOT NULL AND :NEW.expiration_date < SYSDATE THEN
        :NEW.status := 'EXPIRED';
    END IF;
END trg_documents_expiration_check;
/

-- =============================================================================
-- SAMPLE DOCUMENTS DATA
-- =============================================================================

-- Add sample documents for existing loans if they exist
INSERT INTO documents (
    id, loan_id, condition_id, document_type, document_name, file_name, file_path, 
    file_size, mime_type, status, uploaded_by_id, is_required, uploaded_at
) 
SELECT 
    document_sequence.NEXTVAL,
    l.id,
    NULL,
    'TAX_RETURN',
    l.borrower_last_name || ' - 2023 Tax Return',
    LOWER(l.borrower_last_name) || '_2023_tax_return.pdf',
    '/uploads/loans/' || l.loan_number || '/tax_returns/',
    2048576,
    'application/pdf',
    'RECEIVED',
    l.borrower_id,
    1,
    CURRENT_TIMESTAMP
FROM loans l 
WHERE l.id IN (
    SELECT id FROM (
        SELECT id, ROWNUM as rn FROM loans ORDER BY id
    ) WHERE rn <= 3  -- Apply to first 3 sample loans
)
AND NOT EXISTS (
    SELECT 1 FROM documents d 
    WHERE d.loan_id = l.id 
    AND d.document_type = 'TAX_RETURN'
);

-- Add bank statement documents
INSERT INTO documents (
    id, loan_id, condition_id, document_type, document_name, file_name, file_path, 
    file_size, mime_type, status, uploaded_by_id, reviewed_by_id, review_notes, is_required, uploaded_at, reviewed_at
) 
SELECT 
    document_sequence.NEXTVAL,
    l.id,
    NULL,
    'BANK_STATEMENT',
    l.borrower_last_name || ' - Bank Statement Aug 2024',
    LOWER(l.borrower_last_name) || '_bank_statement_aug_2024.pdf',
    '/uploads/loans/' || l.loan_number || '/bank_statements/',
    1536000,
    'application/pdf',
    'APPROVED',
    l.borrower_id,
    l.processor_id,
    'Complete statements showing sufficient funds for down payment and closing costs',
    1,
    CURRENT_TIMESTAMP - 2,
    CURRENT_TIMESTAMP - 1
FROM loans l 
WHERE l.id IN (
    SELECT id FROM (
        SELECT id, ROWNUM as rn FROM loans ORDER BY id
    ) WHERE rn <= 2  -- Apply to first 2 sample loans
)
AND l.processor_id IS NOT NULL
AND NOT EXISTS (
    SELECT 1 FROM documents d 
    WHERE d.loan_id = l.id 
    AND d.document_type = 'BANK_STATEMENT'
);

-- =============================================================================
-- UPDATE COMMENTS
-- =============================================================================

COMMENT ON TABLE documents IS 'Documents uploaded for loans and conditions';
COMMENT ON TABLE document_categories IS 'Categories for organizing document types';
COMMENT ON COLUMN documents.status IS 'Document review status: RECEIVED, UNDER_REVIEW, APPROVED, REJECTED, NEEDS_REVISION, EXPIRED';
COMMENT ON COLUMN documents.is_current_version IS 'Flag indicating if this is the current version of the document';
COMMENT ON COLUMN documents.version_number IS 'Version number for document versioning';
COMMENT ON COLUMN documents.replaced_by_id IS 'Reference to the newer version that replaced this document';
COMMENT ON COLUMN documents.document_tags IS 'Comma-separated tags for document organization and search';
COMMENT ON COLUMN documents.is_required IS 'Flag indicating if this document is required for loan approval';

-- Commit the migration
COMMIT;