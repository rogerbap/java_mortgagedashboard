-- =============================================================================
-- Mortgage Loan Dashboard - Complete Database Schema
-- Database: Oracle 19c+
-- =============================================================================

-- =============================================================================
-- SEQUENCES
-- =============================================================================

CREATE SEQUENCE user_sequence 
    START WITH 1 
    INCREMENT BY 1 
    NOCACHE 
    NOCYCLE;

CREATE SEQUENCE loan_sequence 
    START WITH 1 
    INCREMENT BY 1 
    NOCACHE 
    NOCYCLE;

CREATE SEQUENCE condition_sequence 
    START WITH 1 
    INCREMENT BY 1 
    NOCACHE 
    NOCYCLE;

CREATE SEQUENCE document_sequence 
    START WITH 1 
    INCREMENT BY 1 
    NOCACHE 
    NOCYCLE;

CREATE SEQUENCE status_history_sequence 
    START WITH 1 
    INCREMENT BY 1 
    NOCACHE 
    NOCYCLE;

-- =============================================================================
-- USERS TABLE
-- =============================================================================

CREATE TABLE users (
    id                     NUMBER PRIMARY KEY,
    first_name            VARCHAR2(100) NOT NULL,
    last_name             VARCHAR2(100) NOT NULL,
    email                 VARCHAR2(255) NOT NULL UNIQUE,
    password              VARCHAR2(255) NOT NULL,
    phone                 VARCHAR2(20),
    role                  VARCHAR2(20) NOT NULL,
    active                NUMBER(1) DEFAULT 1 NOT NULL,
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT chk_users_role CHECK (role IN ('BORROWER', 'LOAN_OFFICER', 'PROCESSOR', 'UNDERWRITER', 'MANAGER')),
    CONSTRAINT chk_users_active CHECK (active IN (0, 1))
);

-- =============================================================================
-- LOANS TABLE
-- =============================================================================

CREATE TABLE loans (
    id                     NUMBER PRIMARY KEY,
    loan_number           VARCHAR2(50) UNIQUE NOT NULL,
    borrower_id           NUMBER NOT NULL,
    loan_officer_id       NUMBER NOT NULL,
    processor_id          NUMBER,
    underwriter_id        NUMBER,
    
    -- Borrower Information
    borrower_first_name   VARCHAR2(100) NOT NULL,
    borrower_last_name    VARCHAR2(100) NOT NULL,
    borrower_email        VARCHAR2(255) NOT NULL,
    borrower_phone        VARCHAR2(20),
    borrower_ssn          VARCHAR2(11) NOT NULL,
    borrower_dob          DATE NOT NULL,
    
    -- Co-Borrower Information (Optional)
    co_borrower_first_name VARCHAR2(100),
    co_borrower_last_name  VARCHAR2(100),
    co_borrower_email      VARCHAR2(255),
    co_borrower_phone      VARCHAR2(20),
    co_borrower_ssn        VARCHAR2(11),
    co_borrower_dob        DATE,
    
    -- Property Information
    property_address      VARCHAR2(500) NOT NULL,
    property_city         VARCHAR2(100) NOT NULL,
    property_state        VARCHAR2(2) NOT NULL,
    property_zip          VARCHAR2(10) NOT NULL,
    property_type         VARCHAR2(50) NOT NULL,
    property_value        NUMBER(12,2) NOT NULL,
    
    -- Loan Details
    loan_type             VARCHAR2(50) NOT NULL,
    loan_purpose          VARCHAR2(50) NOT NULL,
    loan_amount           NUMBER(12,2) NOT NULL,
    interest_rate         NUMBER(5,3),
    loan_term_months      NUMBER(3) NOT NULL,
    down_payment          NUMBER(12,2) NOT NULL,
    
    -- Financial Information
    borrower_income       NUMBER(12,2) NOT NULL,
    co_borrower_income    NUMBER(12,2) DEFAULT 0,
    monthly_debt          NUMBER(12,2) DEFAULT 0,
    credit_score          NUMBER(3),
    debt_to_income_ratio  NUMBER(5,2),
    
    -- Status and Tracking
    status                VARCHAR2(50) NOT NULL,
    substatus             VARCHAR2(100),
    priority              VARCHAR2(20) DEFAULT 'NORMAL',
    application_date      DATE DEFAULT SYSDATE NOT NULL,
    expected_closing_date DATE,
    actual_closing_date   DATE,
    
    -- Timestamps
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    -- Foreign Keys
    CONSTRAINT fk_loans_borrower FOREIGN KEY (borrower_id) REFERENCES users(id),
    CONSTRAINT fk_loans_officer FOREIGN KEY (loan_officer_id) REFERENCES users(id),
    CONSTRAINT fk_loans_processor FOREIGN KEY (processor_id) REFERENCES users(id),
    CONSTRAINT fk_loans_underwriter FOREIGN KEY (underwriter_id) REFERENCES users(id),
    
    -- Check Constraints
    CONSTRAINT chk_loans_property_type CHECK (property_type IN ('SINGLE_FAMILY', 'TOWNHOUSE', 'CONDOMINIUM', 'MULTI_FAMILY', 'MANUFACTURED')),
    CONSTRAINT chk_loans_loan_type CHECK (loan_type IN ('CONVENTIONAL', 'FHA', 'VA', 'USDA', 'JUMBO')),
    CONSTRAINT chk_loans_loan_purpose CHECK (loan_purpose IN ('PURCHASE', 'REFINANCE', 'CASH_OUT_REFINANCE')),
    CONSTRAINT chk_loans_status CHECK (status IN ('PROSPECT', 'APPLICATION', 'PROCESSING', 'UNDERWRITING', 'APPROVED', 'CONDITIONS', 'CLEAR_TO_CLOSE', 'FUNDED', 'DENIED', 'WITHDRAWN', 'CANCELED')),
    CONSTRAINT chk_loans_priority CHECK (priority IN ('LOW', 'NORMAL', 'HIGH', 'URGENT')),
    CONSTRAINT chk_loans_amounts CHECK (loan_amount > 0 AND property_value > 0 AND down_payment >= 0),
    CONSTRAINT chk_loans_dates CHECK (application_date <= SYSDATE)
);

-- =============================================================================
-- LOAN CONDITIONS TABLE
-- =============================================================================

CREATE TABLE loan_conditions (
    id                    NUMBER PRIMARY KEY,
    loan_id              NUMBER NOT NULL,
    condition_type       VARCHAR2(50) NOT NULL,
    title                VARCHAR2(255) NOT NULL,
    description          CLOB NOT NULL,
    category             VARCHAR2(100) NOT NULL,
    severity             VARCHAR2(20) DEFAULT 'MEDIUM' NOT NULL,
    status               VARCHAR2(50) DEFAULT 'OUTSTANDING' NOT NULL,
    assigned_to_id       NUMBER,
    
    -- Tracking
    created_by_id        NUMBER NOT NULL,
    due_date             DATE,
    completed_date       DATE,
    notes                CLOB,
    
    -- Timestamps
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    -- Foreign Keys
    CONSTRAINT fk_conditions_loan FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE,
    CONSTRAINT fk_conditions_assigned FOREIGN KEY (assigned_to_id) REFERENCES users(id),
    CONSTRAINT fk_conditions_created_by FOREIGN KEY (created_by_id) REFERENCES users(id),
    
    -- Check Constraints
    CONSTRAINT chk_conditions_type CHECK (condition_type IN ('PRIOR_TO_DOCS', 'PRIOR_TO_FUNDING', 'POST_CLOSING')),
    CONSTRAINT chk_conditions_severity CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    CONSTRAINT chk_conditions_status CHECK (status IN ('OUTSTANDING', 'IN_PROGRESS', 'COMPLETED', 'WAIVED', 'N/A'))
);

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
    
    -- Timestamps
    uploaded_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    reviewed_at          TIMESTAMP,
    
    -- Foreign Keys
    CONSTRAINT fk_documents_loan FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE,
    CONSTRAINT fk_documents_condition FOREIGN KEY (condition_id) REFERENCES loan_conditions(id),
    CONSTRAINT fk_documents_uploaded_by FOREIGN KEY (uploaded_by_id) REFERENCES users(id),
    CONSTRAINT fk_documents_reviewed_by FOREIGN KEY (reviewed_by_id) REFERENCES users(id),
    
    -- Check Constraints
    CONSTRAINT chk_documents_status CHECK (status IN ('RECEIVED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'NEEDS_REVISION'))
);

-- =============================================================================
-- LOAN STATUS HISTORY TABLE
-- =============================================================================

CREATE TABLE loan_status_history (
    id                    NUMBER PRIMARY KEY,
    loan_id              NUMBER NOT NULL,
    from_status          VARCHAR2(50),
    to_status            VARCHAR2(50) NOT NULL,
    changed_by_id        NUMBER NOT NULL,
    change_reason        VARCHAR2(500),
    notes                CLOB,
    changed_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    -- Foreign Keys
    CONSTRAINT fk_status_history_loan FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE,
    CONSTRAINT fk_status_history_changed_by FOREIGN KEY (changed_by_id) REFERENCES users(id)
);

-- =============================================================================
-- INDEXES FOR PERFORMANCE
-- =============================================================================

-- Users table indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_active ON users(active);

-- Loans table indexes
CREATE INDEX idx_loans_loan_number ON loans(loan_number);
CREATE INDEX idx_loans_borrower_id ON loans(borrower_id);
CREATE INDEX idx_loans_loan_officer_id ON loans(loan_officer_id);
CREATE INDEX idx_loans_processor_id ON loans(processor_id);
CREATE INDEX idx_loans_underwriter_id ON loans(underwriter_id);
CREATE INDEX idx_loans_status ON loans(status);
CREATE INDEX idx_loans_priority ON loans(priority);
CREATE INDEX idx_loans_application_date ON loans(application_date);
CREATE INDEX idx_loans_expected_closing ON loans(expected_closing_date);
CREATE INDEX idx_loans_borrower_name ON loans(borrower_first_name, borrower_last_name);

-- Loan conditions table indexes
CREATE INDEX idx_conditions_loan_id ON loan_conditions(loan_id);
CREATE INDEX idx_conditions_status ON loan_conditions(status);
CREATE INDEX idx_conditions_assigned_to ON loan_conditions(assigned_to_id);
CREATE INDEX idx_conditions_due_date ON loan_conditions(due_date);
CREATE INDEX idx_conditions_severity ON loan_conditions(severity);

-- Documents table indexes
CREATE INDEX idx_documents_loan_id ON documents(loan_id);
CREATE INDEX idx_documents_condition_id ON documents(condition_id);
CREATE INDEX idx_documents_type ON documents(document_type);
CREATE INDEX idx_documents_status ON documents(status);
CREATE INDEX idx_documents_uploaded_by ON documents(uploaded_by_id);

-- Loan status history table indexes
CREATE INDEX idx_status_history_loan ON loan_status_history(loan_id);
CREATE INDEX idx_status_history_changed_date ON loan_status_history(changed_at);
CREATE INDEX idx_status_history_changed_by ON loan_status_history(changed_by_id);
CREATE INDEX idx_status_history_to_status ON loan_status_history(to_status);

-- =============================================================================
-- TRIGGERS FOR AUTOMATIC TIMESTAMP UPDATES
-- =============================================================================

-- Users table update trigger
CREATE OR REPLACE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END trg_users_updated_at;
/

-- Loans table update trigger
CREATE OR REPLACE TRIGGER trg_loans_updated_at
    BEFORE UPDATE ON loans
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END trg_loans_updated_at;
/

-- Loan conditions table update trigger
CREATE OR REPLACE TRIGGER trg_conditions_updated_at
    BEFORE UPDATE ON loan_conditions
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END trg_conditions_updated_at;
/

-- =============================================================================
-- COMMENTS FOR DOCUMENTATION
-- =============================================================================

COMMENT ON TABLE users IS 'System users with role-based access control';
COMMENT ON TABLE loans IS 'Main loan applications with borrower and property information';
COMMENT ON TABLE loan_conditions IS 'Conditions that must be satisfied for loan approval';
COMMENT ON TABLE documents IS 'Documents uploaded for loans and conditions';
COMMENT ON TABLE loan_status_history IS 'Audit trail of loan status changes';

-- Column comments for important fields
COMMENT ON COLUMN users.role IS 'User role: BORROWER, LOAN_OFFICER, PROCESSOR, UNDERWRITER, MANAGER';
COMMENT ON COLUMN loans.status IS 'Loan status in processing pipeline';
COMMENT ON COLUMN loans.loan_number IS 'Unique loan identifier in format LN{YYMMDD}{9999}';
COMMENT ON COLUMN loans.borrower_ssn IS 'Social Security Number - should be encrypted in production';

-- Commit the schema creation
COMMIT;