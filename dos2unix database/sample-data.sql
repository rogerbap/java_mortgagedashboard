-- =============================================================================
-- Mortgage Loan Dashboard - Sample Data
-- =============================================================================

-- Clear existing data (for development reset)
DELETE FROM loan_status_history;
DELETE FROM documents;
DELETE FROM loan_conditions;
DELETE FROM loans;
DELETE FROM users;

-- Reset sequences
ALTER SEQUENCE user_sequence RESTART START WITH 1;
ALTER SEQUENCE loan_sequence RESTART START WITH 1;
ALTER SEQUENCE condition_sequence RESTART START WITH 1;
ALTER SEQUENCE document_sequence RESTART START WITH 1;
ALTER SEQUENCE status_history_sequence RESTART START WITH 1;

-- =============================================================================
-- SAMPLE USERS DATA
-- =============================================================================

-- System Administrator
INSERT INTO users (id, first_name, last_name, email, password, phone, role, active, created_at, updated_at) 
VALUES (user_sequence.NEXTVAL, 'System', 'Administrator', 'admin@lender.com', 
        '$2a$10$N.zmdr8f4z/9.z6c8dHXNeqw8LnG5EmXUIi5WIzY8BLYKgGbZ4RO6', 
        '816-555-0000', 'MANAGER', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Loan Officers
INSERT INTO users (id, first_name, last_name, email, password, phone, role, active, created_at, updated_at) 
VALUES (user_sequence.NEXTVAL, 'Sarah', 'Johnson', 'sarah.johnson@lender.com', 
        '$2a$10$N.zmdr8f4z/9.z6c8dHXNeqw8LnG5EmXUIi5WIzY8BLYKgGbZ4RO6', 
        '816-555-0001', 'LOAN_OFFICER', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (id, first_name, last_name, email, password, phone, role, active, created_at, updated_at) 
VALUES (user_sequence.NEXTVAL, 'Michael', 'Davis', 'michael.davis@lender.com', 
        '$2a$10$N.zmdr8f4z/9.z6c8dHXNeqw8LnG5EmXUIi5WIzY8BLYKgGbZ4RO6', 
        '816-555-0002', 'LOAN_OFFICER', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Processors
INSERT INTO users (id, first_name, last_name, email, password, phone, role, active, created_at, updated_at) 
VALUES (user_sequence.NEXTVAL, 'Jennifer', 'Wilson', 'jennifer.wilson@lender.com', 
        '$2a$10$N.zmdr8f4z/9.z6c8dHXNeqw8LnG5EmXUIi5WIzY8BLYKgGbZ4RO6', 
        '816-555-0003', 'PROCESSOR', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (id, first_name, last_name, email, password, phone, role, active, created_at, updated_at) 
VALUES (user_sequence.NEXTVAL, 'Robert', 'Martinez', 'robert.martinez@lender.com', 
        '$2a$10$N.zmdr8f4z/9.z6c8dHXNeqw8LnG5EmXUIi5WIzY8BLYKgGbZ4RO6', 
        '816-555-0004', 'PROCESSOR', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Underwriters
INSERT INTO users (id, first_name, last_name, email, password, phone, role, active, created_at, updated_at) 
VALUES (user_sequence.NEXTVAL, 'Lisa', 'Anderson', 'lisa.anderson@lender.com', 
        '$2a$10$N.zmdr8f4z/9.z6c8dHXNeqw8LnG5EmXUIi5WIzY8BLYKgGbZ4RO6', 
        '816-555-0005', 'UNDERWRITER', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (id, first_name, last_name, email, password, phone, role, active, created_at, updated_at) 
VALUES (user_sequence.NEXTVAL, 'David', 'Thompson', 'david.thompson@lender.com', 
        '$2a$10$N.zmdr8f4z/9.z6c8dHXNeqw8LnG5EmXUIi5WIzY8BLYKgGbZ4RO6', 
        '816-555-0006', 'UNDERWRITER', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Borrowers
INSERT INTO users (id, first_name, last_name, email, password, phone, role, active, created_at, updated_at) 
VALUES (user_sequence.NEXTVAL, 'John', 'Smith', 'john.smith@email.com', 
        '$2a$10$N.zmdr8f4z/9.z6c8dHXNeqw8LnG5EmXUIi5WIzY8BLYKgGbZ4RO6', 
        '816-555-1001', 'BORROWER', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (id, first_name, last_name, email, password, phone, role, active, created_at, updated_at) 
VALUES (user_sequence.NEXTVAL, 'Emily', 'Brown', 'emily.brown@email.com', 
        '$2a$10$N.zmdr8f4z/9.z6c8dHXNeqw8LnG5EmXUIi5WIzY8BLYKgGbZ4RO6', 
        '816-555-1002', 'BORROWER', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (id, first_name, last_name, email, password, phone, role, active, created_at, updated_at) 
VALUES (user_sequence.NEXTVAL, 'James', 'Miller', 'james.miller@email.com', 
        '$2a$10$N.zmdr8f4z/9.z6c8dHXNeqw8LnG5EmXUIi5WIzY8BLYKgGbZ4RO6', 
        '816-555-1003', 'BORROWER', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (id, first_name, last_name, email, password, phone, role, active, created_at, updated_at) 
VALUES (user_sequence.NEXTVAL, 'Jessica', 'Garcia', 'jessica.garcia@email.com', 
        '$2a$10$N.zmdr8f4z/9.z6c8dHXNeqw8LnG5EmXUIi5WIzY8BLYKgGbZ4RO6', 
        '816-555-1004', 'BORROWER', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =============================================================================
-- SAMPLE LOANS DATA
-- =============================================================================

-- Loan 1: In Processing
INSERT INTO loans (
    id, loan_number, borrower_id, loan_officer_id, processor_id, underwriter_id,
    borrower_first_name, borrower_last_name, borrower_email, borrower_phone, borrower_ssn, borrower_dob,
    property_address, property_city, property_state, property_zip, property_type, property_value,
    loan_type, loan_purpose, loan_amount, interest_rate, loan_term_months, down_payment,
    borrower_income, monthly_debt, credit_score, debt_to_income_ratio,
    status, substatus, priority, application_date, expected_closing_date,
    created_at, updated_at
) VALUES (
    loan_sequence.NEXTVAL, 'LN240915001', 8, 2, 4, NULL,
    'John', 'Smith', 'john.smith@email.com', '816-555-1001', '123-45-6789', DATE '1985-03-15',
    '1234 Oak Street', 'Kansas City', 'MO', '64111', 'SINGLE_FAMILY', 350000,
    'CONVENTIONAL', 'PURCHASE', 280000, 6.75, 360, 70000,
    85000, 2500, 740, 28.50,
    'PROCESSING', 'Initial Document Review', 'NORMAL', DATE '2024-09-01', DATE '2024-10-15',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Loan 2: Under Review with Conditions
INSERT INTO loans (
    id, loan_number, borrower_id, loan_officer_id, processor_id, underwriter_id,
    borrower_first_name, borrower_last_name, borrower_email, borrower_phone, borrower_ssn, borrower_dob,
    co_borrower_first_name, co_borrower_last_name, co_borrower_email, co_borrower_phone, co_borrower_ssn, co_borrower_dob,
    property_address, property_city, property_state, property_zip, property_type, property_value,
    loan_type, loan_purpose, loan_amount, interest_rate, loan_term_months, down_payment,
    borrower_income, co_borrower_income, monthly_debt, credit_score, debt_to_income_ratio,
    status, substatus, priority, application_date, expected_closing_date,
    created_at, updated_at
) VALUES (
    loan_sequence.NEXTVAL, 'LN240915002', 9, 2, 4, 6,
    'Emily', 'Brown', 'emily.brown@email.com', '816-555-1002', '987-65-4321', DATE '1988-07-22',
    'Michael', 'Brown', 'michael.brown@email.com', '816-555-1005', '456-78-9012', DATE '1986-12-08',
    '5678 Maple Avenue', 'Overland Park', 'KS', '66204', 'TOWNHOUSE', 425000,
    'CONVENTIONAL', 'PURCHASE', 382500, 6.50, 360, 42500,
    95000, 72000, 3200, 750, 25.75,
    'CONDITIONS', 'Pending Income Verification', 'HIGH', DATE '2024-08-25', DATE '2024-10-20',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Loan 3: VA Loan - Clear to Close
INSERT INTO loans (
    id, loan_number, borrower_id, loan_officer_id, processor_id, underwriter_id,
    borrower_first_name, borrower_last_name, borrower_email, borrower_phone, borrower_ssn, borrower_dob,
    property_address, property_city, property_state, property_zip, property_type, property_value,
    loan_type, loan_purpose, loan_amount, interest_rate, loan_term_months, down_payment,
    borrower_income, monthly_debt, credit_score, debt_to_income_ratio,
    status, substatus, priority, application_date, expected_closing_date,
    created_at, updated_at
) VALUES (
    loan_sequence.NEXTVAL, 'LN240915003', 10, 3, 5, 7,
    'James', 'Miller', 'james.miller@email.com', '816-555-1003', '555-66-7777', DATE '1982-11-30',
    '9999 Pine Ridge Drive', 'Liberty', 'MO', '64068', 'SINGLE_FAMILY', 275000,
    'VA', 'PURCHASE', 275000, 6.25, 360, 0,
    68000, 1800, 680, 26.47,
    'CLEAR_TO_CLOSE', 'Final Conditions Satisfied', 'URGENT', DATE '2024-08-15', DATE '2024-09-30',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Loan 4: FHA Loan - Application Stage
INSERT INTO loans (
    id, loan_number, borrower_id, loan_officer_id, processor_id, underwriter_id,
    borrower_first_name, borrower_last_name, borrower_email, borrower_phone, borrower_ssn, borrower_dob,
    property_address, property_city, property_state, property_zip, property_type, property_value,
    loan_type, loan_purpose, loan_amount, interest_rate, loan_term_months, down_payment,
    borrower_income, monthly_debt, credit_score, debt_to_income_ratio,
    status, substatus, priority, application_date, expected_closing_date,
    created_at, updated_at
) VALUES (
    loan_sequence.NEXTVAL, 'LN240915004', 11, 3, NULL, NULL,
    'Jessica', 'Garcia', 'jessica.garcia@email.com', '816-555-1004', '888-99-0000', DATE '1990-05-18',
    '2468 Cedar Lane', 'Independence', 'MO', '64055', 'CONDOMINIUM', 180000,
    'FHA', 'PURCHASE', 173100, 6.875, 360, 6900,
    52000, 1200, 620, 31.15,
    'APPLICATION', 'Initial Application Review', 'NORMAL', DATE '2024-09-10', DATE '2024-11-05',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- =============================================================================
-- SAMPLE LOAN CONDITIONS DATA
-- =============================================================================

-- Conditions for Loan 1 (John Smith)
INSERT INTO loan_conditions (
    id, loan_id, condition_type, title, description, category, severity, status, 
    assigned_to_id, created_by_id, due_date, notes, created_at, updated_at
) VALUES (
    condition_sequence.NEXTVAL, 1, 'PRIOR_TO_DOCS', 'Income Verification Required', 
    'Please provide 2023 and 2024 tax returns and most recent 2 pay stubs to verify stated income.', 
    'Income Documentation', 'HIGH', 'OUTSTANDING', 4, 4, DATE '2024-09-20', 
    'Borrower contacted via email on 09/15/2024', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

INSERT INTO loan_conditions (
    id, loan_id, condition_type, title, description, category, severity, status, 
    assigned_to_id, created_by_id, due_date, notes, created_at, updated_at
) VALUES (
    condition_sequence.NEXTVAL, 1, 'PRIOR_TO_DOCS', 'Bank Statements', 
    'Provide 2 months of complete bank statements for all accounts showing deposits and withdrawals.', 
    'Asset Documentation', 'MEDIUM', 'IN_PROGRESS', 4, 4, DATE '2024-09-18', 
    'Partial statements received, waiting for complete set', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Conditions for Loan 2 (Emily Brown)
INSERT INTO loan_conditions (
    id, loan_id, condition_type, title, description, category, severity, status, 
    assigned_to_id, created_by_id, due_date, completed_date, notes, created_at, updated_at
) VALUES (
    condition_sequence.NEXTVAL, 2, 'PRIOR_TO_DOCS', 'Employment Verification', 
    'Written verification of employment for both borrower and co-borrower including salary, start date, and employment status.', 
    'Employment Verification', 'HIGH', 'COMPLETED', 4, 6, DATE '2024-09-10', DATE '2024-09-12', 
    'VOE received for both borrowers', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

INSERT INTO loan_conditions (
    id, loan_id, condition_type, title, description, category, severity, status, 
    assigned_to_id, created_by_id, due_date, notes, created_at, updated_at
) VALUES (
    condition_sequence.NEXTVAL, 2, 'PRIOR_TO_FUNDING', 'Property Appraisal', 
    'Full property appraisal required to confirm property value and condition meets lending standards.', 
    'Property Documentation', 'CRITICAL', 'IN_PROGRESS', 6, 6, DATE '2024-09-25', 
    'Appraisal ordered 09/13/2024, pending completion', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- =============================================================================
-- SAMPLE DOCUMENTS DATA
-- =============================================================================

-- Documents for Loan 1
INSERT INTO documents (
    id, loan_id, condition_id, document_type, document_name, file_name, file_path, 
    file_size, mime_type, status, uploaded_by_id, uploaded_at
) VALUES (
    document_sequence.NEXTVAL, 1, 1, 'TAX_RETURN', '2023 Tax Return', 'smith_2023_tax_return.pdf', 
    '/uploads/loans/LN240915001/tax_returns/', 2048576, 'application/pdf', 
    'RECEIVED', 8, CURRENT_TIMESTAMP
);

INSERT INTO documents (
    id, loan_id, condition_id, document_type, document_name, file_name, file_path, 
    file_size, mime_type, status, uploaded_by_id, reviewed_by_id, review_notes, uploaded_at, reviewed_at
) VALUES (
    document_sequence.NEXTVAL, 1, 2, 'BANK_STATEMENT', 'Chase Checking - August 2024', 'chase_checking_aug_2024.pdf', 
    '/uploads/loans/LN240915001/bank_statements/', 1536000, 'application/pdf', 
    'APPROVED', 8, 4, 'Statements complete and show sufficient funds', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- =============================================================================
-- SAMPLE LOAN STATUS HISTORY DATA
-- =============================================================================

-- Status history for Loan 1
INSERT INTO loan_status_history (id, loan_id, from_status, to_status, changed_by_id, change_reason, changed_at)
VALUES (status_history_sequence.NEXTVAL, 1, NULL, 'APPLICATION', 2, 'Initial application submitted', DATE '2024-09-01');

INSERT INTO loan_status_history (id, loan_id, from_status, to_status, changed_by_id, change_reason, changed_at)
VALUES (status_history_sequence.NEXTVAL, 1, 'APPLICATION', 'PROCESSING', 4, 'Assigned to processor for document review', DATE '2024-09-03');

-- Status history for Loan 2
INSERT INTO loan_status_history (id, loan_id, from_status, to_status, changed_by_id, change_reason, changed_at)
VALUES (status_history_sequence.NEXTVAL, 2, NULL, 'APPLICATION', 2, 'Initial application submitted', DATE '2024-08-25');

INSERT INTO loan_status_history (id, loan_id, from_status, to_status, changed_by_id, change_reason, changed_at)
VALUES (status_history_sequence.NEXTVAL, 2, 'APPLICATION', 'CONDITIONS', 4, 'Conditional approval - pending documentation', DATE '2024-09-10');

-- Commit all sample data
COMMIT;

-- Display summary of inserted data
SELECT 'Users' as table_name, COUNT(*) as record_count FROM users
UNION ALL
SELECT 'Loans', COUNT(*) FROM loans
UNION ALL
SELECT 'Loan Conditions', COUNT(*) FROM loan_conditions
UNION ALL  
SELECT 'Documents', COUNT(*) FROM documents
UNION ALL
SELECT 'Status History', COUNT(*) FROM loan_status_history;
