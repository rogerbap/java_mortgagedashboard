-- =============================================================================
-- Migration V2: Add Loan Conditions
-- Description: Adds loan conditions management functionality
-- =============================================================================

-- =============================================================================
-- CREATE SEQUENCE FOR CONDITIONS
-- =============================================================================

CREATE SEQUENCE condition_sequence 
    START WITH 1 
    INCREMENT BY 1 
    NOCACHE 
    NOCYCLE;

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
-- INDEXES FOR LOAN CONDITIONS
-- =============================================================================

CREATE INDEX idx_conditions_loan_id ON loan_conditions(loan_id);
CREATE INDEX idx_conditions_status ON loan_conditions(status);
CREATE INDEX idx_conditions_assigned_to ON loan_conditions(assigned_to_id);
CREATE INDEX idx_conditions_due_date ON loan_conditions(due_date);
CREATE INDEX idx_conditions_severity ON loan_conditions(severity);
CREATE INDEX idx_conditions_created_by ON loan_conditions(created_by_id);
CREATE INDEX idx_conditions_type ON loan_conditions(condition_type);

-- =============================================================================
-- TRIGGER FOR LOAN CONDITIONS TIMESTAMP UPDATES
-- =============================================================================

CREATE OR REPLACE TRIGGER trg_conditions_updated_at
    BEFORE UPDATE ON loan_conditions
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END trg_conditions_updated_at;
/

-- =============================================================================
-- CONDITIONS MANAGEMENT PROCEDURES
-- =============================================================================

-- Create loan condition
CREATE OR REPLACE PROCEDURE create_loan_condition(
    p_loan_id IN NUMBER,
    p_condition_type IN VARCHAR2,
    p_title IN VARCHAR2,
    p_description IN CLOB,
    p_category IN VARCHAR2,
    p_severity IN VARCHAR2 DEFAULT 'MEDIUM',
    p_assigned_to_id IN NUMBER DEFAULT NULL,
    p_created_by_id IN NUMBER,
    p_due_date IN DATE DEFAULT NULL,
    p_condition_id OUT NUMBER
) IS
BEGIN
    INSERT INTO loan_conditions (
        id, loan_id, condition_type, title, description, category, 
        severity, status, assigned_to_id, created_by_id, due_date,
        created_at, updated_at
    ) VALUES (
        condition_sequence.NEXTVAL, p_loan_id, p_condition_type, p_title, 
        p_description, p_category, p_severity, 'OUTSTANDING', 
        p_assigned_to_id, p_created_by_id, p_due_date,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO p_condition_id;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END create_loan_condition;
/

-- Update condition status
CREATE OR REPLACE PROCEDURE update_condition_status(
    p_condition_id IN NUMBER,
    p_status IN VARCHAR2,
    p_notes IN CLOB DEFAULT NULL,
    p_completed_by_id IN NUMBER DEFAULT NULL
) IS
BEGIN
    UPDATE loan_conditions
    SET status = p_status,
        notes = NVL(p_notes, notes),
        completed_date = CASE WHEN p_status = 'COMPLETED' THEN CURRENT_TIMESTAMP ELSE completed_date END,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = p_condition_id;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END update_condition_status;
/

-- Get conditions summary for a loan
CREATE OR REPLACE PROCEDURE get_loan_conditions_summary(
    p_loan_id IN NUMBER,
    p_result_cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_result_cursor FOR
        SELECT 
            condition_type,
            COUNT(*) as total_conditions,
            COUNT(CASE WHEN status = 'COMPLETED' THEN 1 END) as completed_conditions,
            COUNT(CASE WHEN status = 'OUTSTANDING' THEN 1 END) as outstanding_conditions,
            COUNT(CASE WHEN status = 'IN_PROGRESS' THEN 1 END) as in_progress_conditions,
            COUNT(CASE WHEN due_date < SYSDATE AND status NOT IN ('COMPLETED', 'WAIVED', 'N/A') THEN 1 END) as overdue_conditions,
            COUNT(CASE WHEN severity = 'CRITICAL' AND status NOT IN ('COMPLETED', 'WAIVED', 'N/A') THEN 1 END) as critical_outstanding
        FROM loan_conditions
        WHERE loan_id = p_loan_id
        GROUP BY condition_type
        ORDER BY 
            CASE condition_type
                WHEN 'PRIOR_TO_DOCS' THEN 1
                WHEN 'PRIOR_TO_FUNDING' THEN 2
                WHEN 'POST_CLOSING' THEN 3
                ELSE 4
            END;
END get_loan_conditions_summary;
/

-- Get overdue conditions report
CREATE OR REPLACE PROCEDURE get_overdue_conditions(
    p_assigned_to_id IN NUMBER DEFAULT NULL,
    p_result_cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_result_cursor FOR
        SELECT 
            lc.id,
            lc.title,
            lc.severity,
            lc.due_date,
            SYSDATE - lc.due_date as days_overdue,
            l.loan_number,
            l.borrower_first_name,
            l.borrower_last_name,
            u.first_name as assigned_to_first_name,
            u.last_name as assigned_to_last_name,
            creator.first_name as created_by_first_name,
            creator.last_name as created_by_last_name
        FROM loan_conditions lc
        JOIN loans l ON lc.loan_id = l.id
        LEFT JOIN users u ON lc.assigned_to_id = u.id
        LEFT JOIN users creator ON lc.created_by_id = creator.id
        WHERE lc.status IN ('OUTSTANDING', 'IN_PROGRESS')
        AND lc.due_date < SYSDATE
        AND (p_assigned_to_id IS NULL OR lc.assigned_to_id = p_assigned_to_id)
        ORDER BY lc.due_date ASC, 
                CASE lc.severity
                    WHEN 'CRITICAL' THEN 1
                    WHEN 'HIGH' THEN 2
                    WHEN 'MEDIUM' THEN 3
                    WHEN 'LOW' THEN 4
                END;
END get_overdue_conditions;
/

-- Get conditions by user assignment
CREATE OR REPLACE PROCEDURE get_user_assigned_conditions(
    p_user_id IN NUMBER,
    p_status_filter IN VARCHAR2 DEFAULT NULL,
    p_result_cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_result_cursor FOR
        SELECT 
            lc.id,
            lc.title,
            lc.description,
            lc.condition_type,
            lc.category,
            lc.severity,
            lc.status,
            lc.due_date,
            lc.created_at,
            l.loan_number,
            l.borrower_first_name,
            l.borrower_last_name,
            l.status as loan_status,
            creator.first_name as created_by_first_name,
            creator.last_name as created_by_last_name
        FROM loan_conditions lc
        JOIN loans l ON lc.loan_id = l.id
        LEFT JOIN users creator ON lc.created_by_id = creator.id
        WHERE lc.assigned_to_id = p_user_id
        AND (p_status_filter IS NULL OR lc.status = p_status_filter)
        ORDER BY 
            CASE lc.severity
                WHEN 'CRITICAL' THEN 1
                WHEN 'HIGH' THEN 2
                WHEN 'MEDIUM' THEN 3
                WHEN 'LOW' THEN 4
            END,
            NVL(lc.due_date, DATE '9999-12-31'),
            lc.created_at;
END get_user_assigned_conditions;
/

-- Bulk assign conditions to user
CREATE OR REPLACE PROCEDURE bulk_assign_conditions(
    p_condition_ids IN VARCHAR2, -- Comma-separated condition IDs
    p_assigned_to_id IN NUMBER,
    p_assigned_by_id IN NUMBER
) IS
    v_condition_id NUMBER;
    v_start_pos NUMBER := 1;
    v_end_pos NUMBER;
    v_id_string VARCHAR2(10);
BEGIN
    -- Parse comma-separated condition IDs
    WHILE v_start_pos <= LENGTH(p_condition_ids) LOOP
        v_end_pos := INSTR(p_condition_ids, ',', v_start_pos);
        
        IF v_end_pos = 0 THEN
            v_end_pos := LENGTH(p_condition_ids) + 1;
        END IF;
        
        v_id_string := TRIM(SUBSTR(p_condition_ids, v_start_pos, v_end_pos - v_start_pos));
        
        IF v_id_string IS NOT NULL THEN
            v_condition_id := TO_NUMBER(v_id_string);
            
            -- Update condition assignment
            UPDATE loan_conditions
            SET assigned_to_id = p_assigned_to_id,
                notes = NVL(notes, '') || 
                       CASE WHEN notes IS NOT NULL THEN '; ' ELSE '' END ||
                       'Assigned by user ID ' || p_assigned_by_id || ' on ' || 
                       TO_CHAR(CURRENT_TIMESTAMP, 'YYYY-MM-DD HH24:MI:SS'),
                updated_at = CURRENT_TIMESTAMP
            WHERE id = v_condition_id;
        END IF;
        
        v_start_pos := v_end_pos + 1;
    END LOOP;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END bulk_assign_conditions;
/

-- Get conditions dashboard summary
CREATE OR REPLACE PROCEDURE get_conditions_dashboard(
    p_user_id IN NUMBER DEFAULT NULL,
    p_user_role IN VARCHAR2 DEFAULT NULL,
    p_result_cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_result_cursor FOR
        SELECT 
            'Critical Outstanding' as metric_name,
            COUNT(*) as metric_value,
            'conditions' as metric_unit
        FROM loan_conditions lc
        JOIN loans l ON lc.loan_id = l.id
        WHERE lc.severity = 'CRITICAL' 
        AND lc.status IN ('OUTSTANDING', 'IN_PROGRESS')
        AND (p_user_id IS NULL OR 
             (p_user_role = 'LOAN_OFFICER' AND l.loan_officer_id = p_user_id) OR
             (p_user_role = 'PROCESSOR' AND l.processor_id = p_user_id) OR
             (p_user_role = 'UNDERWRITER' AND l.underwriter_id = p_user_id) OR
             (p_user_role IN ('MANAGER', 'ADMIN')))
        
        UNION ALL
        
        SELECT 
            'Overdue Conditions',
            COUNT(*),
            'conditions'
        FROM loan_conditions lc
        JOIN loans l ON lc.loan_id = l.id
        WHERE lc.due_date < SYSDATE 
        AND lc.status IN ('OUTSTANDING', 'IN_PROGRESS')
        AND (p_user_id IS NULL OR 
             (p_user_role = 'LOAN_OFFICER' AND l.loan_officer_id = p_user_id) OR
             (p_user_role = 'PROCESSOR' AND l.processor_id = p_user_id) OR
             (p_user_role = 'UNDERWRITER' AND l.underwriter_id = p_user_id) OR
             (p_user_role IN ('MANAGER', 'ADMIN')))
        
        UNION ALL
        
        SELECT 
            'My Assigned Conditions',
            COUNT(*),
            'conditions'
        FROM loan_conditions lc
        WHERE lc.assigned_to_id = p_user_id
        AND lc.status IN ('OUTSTANDING', 'IN_PROGRESS')
        
        UNION ALL
        
        SELECT 
            'Completed This Week',
            COUNT(*),
            'conditions'
        FROM loan_conditions lc
        JOIN loans l ON lc.loan_id = l.id
        WHERE lc.status = 'COMPLETED'
        AND lc.completed_date >= TRUNC(SYSDATE, 'IW') -- Start of current week
        AND (p_user_id IS NULL OR 
             (p_user_role = 'LOAN_OFFICER' AND l.loan_officer_id = p_user_id) OR
             (p_user_role = 'PROCESSOR' AND l.processor_id = p_user_id) OR
             (p_user_role = 'UNDERWRITER' AND l.underwriter_id = p_user_id) OR
             (p_user_role IN ('MANAGER', 'ADMIN')));
END get_conditions_dashboard;
/

-- =============================================================================
-- SAMPLE CONDITIONS DATA
-- =============================================================================

-- Add some standard condition templates for existing loans if they exist
INSERT INTO loan_conditions (
    id, loan_id, condition_type, title, description, category, severity, status, 
    created_by_id, due_date, notes, created_at, updated_at
) 
SELECT 
    condition_sequence.NEXTVAL, 
    l.id, 
    'PRIOR_TO_DOCS', 
    'Income Verification Required', 
    'Please provide 2023 and 2024 tax returns and most recent 2 pay stubs to verify stated income.', 
    'Income Documentation', 
    'HIGH', 
    'OUTSTANDING', 
    1,  -- Admin user
    l.application_date + 5, -- Due in 5 days from application
    'Standard income verification requirement',
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
FROM loans l 
WHERE l.id IN (
    SELECT id FROM (
        SELECT id, ROWNUM as rn FROM loans ORDER BY id
    ) WHERE rn <= 2  -- Apply to first 2 loans if they exist
)
AND NOT EXISTS (
    SELECT 1 FROM loan_conditions lc 
    WHERE lc.loan_id = l.id 
    AND lc.title = 'Income Verification Required'
);

-- Add asset verification condition
INSERT INTO loan_conditions (
    id, loan_id, condition_type, title, description, category, severity, status, 
    created_by_id, due_date, notes, created_at, updated_at
) 
SELECT 
    condition_sequence.NEXTVAL, 
    l.id, 
    'PRIOR_TO_DOCS', 
    'Asset Verification', 
    'Provide 2 months of complete bank statements for all accounts listed on application.', 
    'Asset Documentation', 
    'MEDIUM', 
    'OUTSTANDING', 
    1,  -- Admin user
    l.application_date + 7, -- Due in 7 days from application
    'Standard asset verification requirement',
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
FROM loans l 
WHERE l.id IN (
    SELECT id FROM (
        SELECT id, ROWNUM as rn FROM loans ORDER BY id
    ) WHERE rn <= 2  -- Apply to first 2 loans if they exist
)
AND NOT EXISTS (
    SELECT 1 FROM loan_conditions lc 
    WHERE lc.loan_id = l.id 
    AND lc.title = 'Asset Verification'
);

-- =============================================================================
-- UPDATE COMMENTS
-- =============================================================================

COMMENT ON TABLE loan_conditions IS 'Conditions that must be satisfied for loan approval';
COMMENT ON COLUMN loan_conditions.condition_type IS 'Timing of when condition must be satisfied: PRIOR_TO_DOCS, PRIOR_TO_FUNDING, POST_CLOSING';
COMMENT ON COLUMN loan_conditions.severity IS 'Priority level: LOW, MEDIUM, HIGH, CRITICAL';
COMMENT ON COLUMN loan_conditions.status IS 'Current status: OUTSTANDING, IN_PROGRESS, COMPLETED, WAIVED, N/A';
COMMENT ON COLUMN loan_conditions.category IS 'Categorization for grouping similar conditions (e.g., Income Documentation, Asset Documentation)';
COMMENT ON COLUMN loan_conditions.assigned_to_id IS 'User responsible for completing this condition';
COMMENT ON COLUMN loan_conditions.created_by_id IS 'User who created this condition';

-- Commit the migration
COMMIT;