-- =============================================================================
-- Migration V1: Create Initial Schema
-- Description: Creates basic users and loans tables with core functionality
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

-- =============================================================================
-- CORE FUNCTIONS
-- =============================================================================

-- Generate unique loan number
CREATE OR REPLACE FUNCTION generate_loan_number RETURN VARCHAR2 IS
    v_loan_number VARCHAR2(50);
    v_sequence_num NUMBER;
    v_date_part VARCHAR2(6);
BEGIN
    -- Get current date in YYMMDD format
    v_date_part := TO_CHAR(SYSDATE, 'YYMMDD');
    
    -- Get next sequence number for today
    SELECT NVL(MAX(SUBSTR(loan_number, 9, 4)), 0) + 1
    INTO v_sequence_num
    FROM loans
    WHERE SUBSTR(loan_number, 3, 6) = v_date_part;
    
    -- Format: LN + YYMMDD + 4-digit sequence
    v_loan_number := 'LN' || v_date_part || LPAD(v_sequence_num, 4, '0');
    
    RETURN v_loan_number;
END generate_loan_number;
/

-- Update loan status with history tracking
CREATE OR REPLACE PROCEDURE update_loan_status(
    p_loan_id IN NUMBER,
    p_new_status IN VARCHAR2,
    p_substatus IN VARCHAR2 DEFAULT NULL,
    p_changed_by_id IN NUMBER,
    p_change_reason IN VARCHAR2 DEFAULT NULL,
    p_notes IN CLOB DEFAULT NULL
) IS
    v_current_status VARCHAR2(50);
BEGIN
    -- Get current status
    SELECT status INTO v_current_status
    FROM loans
    WHERE id = p_loan_id;
    
    -- Update loan status
    UPDATE loans
    SET status = p_new_status,
        substatus = p_substatus,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = p_loan_id;
    
    -- Insert status history record
    INSERT INTO loan_status_history (
        id, loan_id, from_status, to_status, changed_by_id, 
        change_reason, notes, changed_at
    ) VALUES (
        status_history_sequence.NEXTVAL, p_loan_id, v_current_status, p_new_status, 
        p_changed_by_id, p_change_reason, p_notes, CURRENT_TIMESTAMP
    );
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END update_loan_status;
/

-- =============================================================================
-- INITIAL DATA
-- =============================================================================

-- Insert initial admin user (password: 'password123' BCrypt encoded)
INSERT INTO users (id, first_name, last_name, email, password, phone, role, active, created_at, updated_at) 
VALUES (user_sequence.NEXTVAL, 'System', 'Administrator', 'admin@lender.com', 
        '$2a$10$N.zmdr8f4z/9.z6c8dHXNeqw8LnG5EmXUIi5WIzY8BLYKgGbZ4RO6', 
        '816-555-0000', 'MANAGER', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =============================================================================
-- COMMENTS FOR DOCUMENTATION
-- =============================================================================

COMMENT ON TABLE users IS 'System users with role-based access control';
COMMENT ON TABLE loans IS 'Main loan applications with borrower and property information';
COMMENT ON TABLE loan_status_history IS 'Audit trail of loan status changes';

-- Column comments for important fields
COMMENT ON COLUMN users.role IS 'User role: BORROWER, LOAN_OFFICER, PROCESSOR, UNDERWRITER, MANAGER';
COMMENT ON COLUMN loans.status IS 'Loan status in processing pipeline';
COMMENT ON COLUMN loans.loan_number IS 'Unique loan identifier in format LN{YYMMDD}{9999}';
COMMENT ON COLUMN loans.borrower_ssn IS 'Social Security Number - should be encrypted in production';

-- Commit the migration
COMMIT;