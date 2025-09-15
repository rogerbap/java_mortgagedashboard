-- =============================================================================
-- Mortgage Loan Dashboard - Stored Procedures and Functions
-- =============================================================================

-- =============================================================================
-- LOAN MANAGEMENT PROCEDURES
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

-- Calculate debt-to-income ratio
CREATE OR REPLACE FUNCTION calculate_dti_ratio(
    p_monthly_income IN NUMBER,
    p_co_borrower_income IN NUMBER DEFAULT 0,
    p_monthly_debt IN NUMBER,
    p_proposed_payment IN NUMBER
) RETURN NUMBER IS
    v_total_income NUMBER;
    v_total_debt NUMBER;
    v_dti_ratio NUMBER;
BEGIN
    v_total_income := p_monthly_income + NVL(p_co_borrower_income, 0);
    v_total_debt := p_monthly_debt + p_proposed_payment;
    
    IF v_total_income > 0 THEN
        v_dti_ratio := ROUND((v_total_debt / v_total_income) * 100, 2);
    ELSE
        v_dti_ratio := 0;
    END IF;
    
    RETURN v_dti_ratio;
END calculate_dti_ratio;
/

-- =============================================================================
-- CONDITION MANAGEMENT PROCEDURES
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

-- =============================================================================
-- DOCUMENT MANAGEMENT PROCEDURES
-- =============================================================================

-- Upload document
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
    p_document_id OUT NUMBER
) IS
BEGIN
    INSERT INTO documents (
        id, loan_id, condition_id, document_type, document_name,
        file_name, file_path, file_size, mime_type, status,
        uploaded_by_id, uploaded_at
    ) VALUES (
        document_sequence.NEXTVAL, p_loan_id, p_condition_id, p_document_type, 
        p_document_name, p_file_name, p_file_path, p_file_size, p_mime_type,
        'RECEIVED', p_uploaded_by_id, CURRENT_TIMESTAMP
    ) RETURNING id INTO p_document_id;
    
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
BEGIN
    UPDATE documents
    SET status = p_status,
        reviewed_by_id = p_reviewed_by_id,
        review_notes = p_review_notes,
        reviewed_at = CURRENT_TIMESTAMP
    WHERE id = p_document_id;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END review_document;
/

-- =============================================================================
-- REPORTING AND ANALYTICS PROCEDURES
-- =============================================================================

-- Get loan pipeline summary
CREATE OR REPLACE PROCEDURE get_pipeline_summary(
    p_user_id IN NUMBER DEFAULT NULL,
    p_role IN VARCHAR2 DEFAULT NULL,
    p_result_cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_result_cursor FOR
        SELECT 
            status,
            COUNT(*) as loan_count,
            SUM(loan_amount) as total_amount,
            AVG(SYSDATE - application_date) as avg_days_in_status
        FROM loans l
        WHERE (p_user_id IS NULL OR 
               (p_role = 'LOAN_OFFICER' AND l.loan_officer_id = p_user_id) OR
               (p_role = 'PROCESSOR' AND l.processor_id = p_user_id) OR
               (p_role = 'UNDERWRITER' AND l.underwriter_id = p_user_id) OR
               (p_role IN ('MANAGER', 'ADMIN')))
        GROUP BY status
        ORDER BY 
            CASE status
                WHEN 'PROSPECT' THEN 1
                WHEN 'APPLICATION' THEN 2
                WHEN 'PROCESSING' THEN 3
                WHEN 'UNDERWRITING' THEN 4
                WHEN 'APPROVED' THEN 5
                WHEN 'CONDITIONS' THEN 6
                WHEN 'CLEAR_TO_CLOSE' THEN 7
                WHEN 'FUNDED' THEN 8
                ELSE 9
            END;
END get_pipeline_summary;
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
            u.last_name as assigned_to_last_name
        FROM loan_conditions lc
        JOIN loans l ON lc.loan_id = l.id
        LEFT JOIN users u ON lc.assigned_to_id = u.id
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

-- Get loan performance metrics
CREATE OR REPLACE PROCEDURE get_performance_metrics(
    p_start_date IN DATE DEFAULT TRUNC(SYSDATE, 'MM'),
    p_end_date IN DATE DEFAULT SYSDATE,
    p_result_cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_result_cursor FOR
        SELECT 
            'Total Applications' as metric_name,
            COUNT(*) as metric_value,
            TO_CHAR(COUNT(*)) as display_value
        FROM loans
        WHERE application_date BETWEEN p_start_date AND p_end_date
        
        UNION ALL
        
        SELECT 
            'Funded Loans',
            COUNT(*),
            TO_CHAR(COUNT(*))
        FROM loans
        WHERE status = 'FUNDED'
        AND actual_closing_date BETWEEN p_start_date AND p_end_date
        
        UNION ALL
        
        SELECT 
            'Total Funded Volume',
            SUM(loan_amount),
            TO_CHAR(SUM(loan_amount), '$999,999,999')
        FROM loans
        WHERE status = 'FUNDED'
        AND actual_closing_date BETWEEN p_start_date AND p_end_date
        
        UNION ALL
        
        SELECT 
            'Average Processing Time (Days)',
            AVG(actual_closing_date - application_date),
            TO_CHAR(ROUND(AVG(actual_closing_date - application_date), 1))
        FROM loans
        WHERE status = 'FUNDED'
        AND actual_closing_date BETWEEN p_start_date AND p_end_date
        
        UNION ALL
        
        SELECT 
            'Pull-through Rate (%)',
            ROUND((COUNT(CASE WHEN status = 'FUNDED' THEN 1 END) / COUNT(*)) * 100, 2),
            TO_CHAR(ROUND((COUNT(CASE WHEN status = 'FUNDED' THEN 1 END) / COUNT(*)) * 100, 2)) || '%'
        FROM loans
        WHERE application_date BETWEEN p_start_date AND p_end_date;
END get_performance_metrics;
/

-- =============================================================================
-- USER WORKLOAD AND ASSIGNMENT PROCEDURES
-- =============================================================================

-- Get user workload
CREATE OR REPLACE PROCEDURE get_user_workload(
    p_user_id IN NUMBER,
    p_result_cursor OUT SYS_REFCURSOR
) IS
    v_user_role VARCHAR2(20);
BEGIN
    -- Get user role
    SELECT role INTO v_user_role FROM users WHERE id = p_user_id;
    
    OPEN p_result_cursor FOR
        SELECT 
            l.status,
            COUNT(*) as loan_count,
            COUNT(CASE WHEN l.priority = 'URGENT' THEN 1 END) as urgent_count,
            COUNT(CASE WHEN l.priority = 'HIGH' THEN 1 END) as high_priority_count,
            AVG(SYSDATE - l.application_date) as avg_age_days
        FROM loans l
        WHERE (
            (v_user_role = 'LOAN_OFFICER' AND l.loan_officer_id = p_user_id) OR
            (v_user_role = 'PROCESSOR' AND l.processor_id = p_user_id) OR
            (v_user_role = 'UNDERWRITER' AND l.underwriter_id = p_user_id)
        )
        AND l.status NOT IN ('FUNDED', 'DENIED', 'WITHDRAWN', 'CANCELED')
        GROUP BY l.status
        ORDER BY 
            CASE l.status
                WHEN 'PROSPECT' THEN 1
                WHEN 'APPLICATION' THEN 2
                WHEN 'PROCESSING' THEN 3
                WHEN 'UNDERWRITING' THEN 4
                WHEN 'APPROVED' THEN 5
                WHEN 'CONDITIONS' THEN 6
                WHEN 'CLEAR_TO_CLOSE' THEN 7
                ELSE 8
            END;
END get_user_workload;
/

-- Auto-assign processor to loan
CREATE OR REPLACE PROCEDURE auto_assign_processor(
    p_loan_id IN NUMBER,
    p_assigned_processor_id OUT NUMBER
) IS
    v_min_workload NUMBER;
BEGIN
    -- Find processor with minimum current workload
    SELECT user_id INTO p_assigned_processor_id
    FROM (
        SELECT u.id as user_id, COUNT(l.id) as workload
        FROM users u
        LEFT JOIN loans l ON u.id = l.processor_id 
            AND l.status IN ('PROCESSING', 'CONDITIONS')
        WHERE u.role = 'PROCESSOR' 
        AND u.active = 1
        GROUP BY u.id
        ORDER BY COUNT(l.id) ASC
    )
    WHERE ROWNUM = 1;
    
    -- Assign processor to loan
    UPDATE loans
    SET processor_id = p_assigned_processor_id,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = p_loan_id;
    
    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_assigned_processor_id := NULL;
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END auto_assign_processor;
/

-- =============================================================================
-- VALIDATION AND BUSINESS RULE PROCEDURES
-- =============================================================================

-- Validate loan application completeness
CREATE OR REPLACE FUNCTION validate_loan_completeness(
    p_loan_id IN NUMBER
) RETURN VARCHAR2 IS
    v_missing_fields CLOB := '';
    v_loan loans%ROWTYPE;
BEGIN
    SELECT * INTO v_loan FROM loans WHERE id = p_loan_id;
    
    -- Check required borrower information
    IF v_loan.borrower_first_name IS NULL THEN
        v_missing_fields := v_missing_fields || 'Borrower First Name; ';
    END IF;
    
    IF v_loan.borrower_last_name IS NULL THEN
        v_missing_fields := v_missing_fields || 'Borrower Last Name; ';
    END IF;
    
    IF v_loan.borrower_email IS NULL THEN
        v_missing_fields := v_missing_fields || 'Borrower Email; ';
    END IF;
    
    IF v_loan.borrower_ssn IS NULL THEN
        v_missing_fields := v_missing_fields || 'Borrower SSN; ';
    END IF;
    
    -- Check property information
    IF v_loan.property_address IS NULL THEN
        v_missing_fields := v_missing_fields || 'Property Address; ';
    END IF;
    
    IF v_loan.property_value IS NULL OR v_loan.property_value <= 0 THEN
        v_missing_fields := v_missing_fields || 'Property Value; ';
    END IF;
    
    -- Check loan information
    IF v_loan.loan_amount IS NULL OR v_loan.loan_amount <= 0 THEN
        v_missing_fields := v_missing_fields || 'Loan Amount; ';
    END IF;
    
    IF v_loan.borrower_income IS NULL OR v_loan.borrower_income <= 0 THEN
        v_missing_fields := v_missing_fields || 'Borrower Income; ';
    END IF;
    
    -- Return result
    IF LENGTH(v_missing_fields) > 0 THEN
        RETURN 'INCOMPLETE: Missing fields: ' || v_missing_fields;
    ELSE
        RETURN 'COMPLETE';
    END IF;
    
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 'ERROR: Loan not found';
    WHEN OTHERS THEN
        RETURN 'ERROR: ' || SQLERRM;
END validate_loan_completeness;
/

-- Check loan approval eligibility
CREATE OR REPLACE FUNCTION check_approval_eligibility(
    p_loan_id IN NUMBER
) RETURN VARCHAR2 IS
    v_loan loans%ROWTYPE;
    v_dti_ratio NUMBER;
    v_ltv_ratio NUMBER;
    v_issues VARCHAR2(1000) := '';
BEGIN
    SELECT * INTO v_loan FROM loans WHERE id = p_loan_id;
    
    -- Calculate ratios
    v_dti_ratio := v_loan.debt_to_income_ratio;
    v_ltv_ratio := (v_loan.loan_amount / v_loan.property_value) * 100;
    
    -- Check DTI ratio
    IF v_dti_ratio > 45 THEN
        v_issues := v_issues || 'DTI ratio too high (' || v_dti_ratio || '%); ';
    END IF;
    
    -- Check credit score
    IF v_loan.credit_score IS NOT NULL AND v_loan.credit_score < 620 THEN
        v_issues := v_issues || 'Credit score too low (' || v_loan.credit_score || '); ';
    END IF;
    
    -- Check LTV ratio based on loan type
    IF v_loan.loan_type = 'CONVENTIONAL' AND v_ltv_ratio > 97 THEN
        v_issues := v_issues || 'LTV ratio too high for conventional loan (' || v_ltv_ratio || '%); ';
    ELSIF v_loan.loan_type = 'FHA' AND v_ltv_ratio > 96.5 THEN
        v_issues := v_issues || 'LTV ratio too high for FHA loan (' || v_ltv_ratio || '%); ';
    END IF;
    
    -- Return result
    IF LENGTH(v_issues) > 0 THEN
        RETURN 'ISSUES: ' || v_issues;
    ELSE
        RETURN 'ELIGIBLE';
    END IF;
    
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 'ERROR: Loan not found';
    WHEN OTHERS THEN
        RETURN 'ERROR: ' || SQLERRM;
END check_approval_eligibility;
/

-- =============================================================================
-- ADVANCED REPORTING PROCEDURES
-- =============================================================================

-- Get loans by status with details
CREATE OR REPLACE PROCEDURE get_loans_by_status(
    p_status IN VARCHAR2,
    p_user_id IN NUMBER DEFAULT NULL,
    p_user_role IN VARCHAR2 DEFAULT NULL,
    p_result_cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_result_cursor FOR
        SELECT 
            l.id,
            l.loan_number,
            l.borrower_first_name,
            l.borrower_last_name,
            l.property_address,
            l.property_city,
            l.property_state,
            l.loan_amount,
            l.loan_type,
            l.priority,
            l.application_date,
            l.expected_closing_date,
            lo.first_name as loan_officer_first_name,
            lo.last_name as loan_officer_last_name,
            p.first_name as processor_first_name,
            p.last_name as processor_last_name,
            u.first_name as underwriter_first_name,
            u.last_name as underwriter_last_name,
            (SELECT COUNT(*) FROM loan_conditions lc 
             WHERE lc.loan_id = l.id AND lc.status = 'OUTSTANDING') as outstanding_conditions,
            SYSDATE - l.application_date as days_in_pipeline
        FROM loans l
        LEFT JOIN users lo ON l.loan_officer_id = lo.id
        LEFT JOIN users p ON l.processor_id = p.id
        LEFT JOIN users u ON l.underwriter_id = u.id
        WHERE l.status = p_status
        AND (p_user_id IS NULL OR 
             (p_user_role = 'LOAN_OFFICER' AND l.loan_officer_id = p_user_id) OR
             (p_user_role = 'PROCESSOR' AND l.processor_id = p_user_id) OR
             (p_user_role = 'UNDERWRITER' AND l.underwriter_id = p_user_id) OR
             (p_user_role IN ('MANAGER', 'ADMIN')))
        ORDER BY 
            CASE l.priority
                WHEN 'URGENT' THEN 1
                WHEN 'HIGH' THEN 2
                WHEN 'NORMAL' THEN 3
                WHEN 'LOW' THEN 4
            END,
            l.application_date ASC;
END get_loans_by_status;
/

-- Get condition summary for all loans
CREATE OR REPLACE PROCEDURE get_conditions_summary(
    p_result_cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_result_cursor FOR
        SELECT 
            lc.severity,
            lc.status,
            COUNT(*) as condition_count,
            COUNT(CASE WHEN lc.due_date < SYSDATE THEN 1 END) as overdue_count
        FROM loan_conditions lc
        GROUP BY lc.severity, lc.status
        ORDER BY 
            CASE lc.severity
                WHEN 'CRITICAL' THEN 1
                WHEN 'HIGH' THEN 2
                WHEN 'MEDIUM' THEN 3
                WHEN 'LOW' THEN 4
            END,
            CASE lc.status
                WHEN 'OUTSTANDING' THEN 1
                WHEN 'IN_PROGRESS' THEN 2
                WHEN 'COMPLETED' THEN 3
                WHEN 'WAIVED' THEN 4
                WHEN 'N/A' THEN 5
            END;
END get_conditions_summary;
/

-- Commit all procedures and functions
COMMIT;