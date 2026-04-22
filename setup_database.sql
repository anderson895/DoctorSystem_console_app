-- =====================================================
--   DOCTOR & ASSIGNMENT SYSTEM - SQL SETUP SCRIPT
--   Run this in phpMyAdmin or MySQL before starting
-- =====================================================

-- 1. Create the database
CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

-- 2. Create doctors table
CREATE TABLE IF NOT EXISTS doctors (
    doctor_id      INT AUTO_INCREMENT PRIMARY KEY,
    full_name      VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20)  NOT NULL,
    availability   VARCHAR(10)  NOT NULL DEFAULT 'AVAILABLE'
);

-- 3. Create doctor_assignments table
CREATE TABLE IF NOT EXISTS doctor_assignments (
    assignment_id  INT AUTO_INCREMENT PRIMARY KEY,
    patient_name   VARCHAR(100) NOT NULL,
    doctor_id      INT NOT NULL,
    assigned_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)
);

-- 4. Sample data (optional)
INSERT INTO doctors (full_name, specialization, contact_number, availability) VALUES
('DR. JUAN DELA CRUZ',    'GENERAL MEDICINE',   '09171234567', 'AVAILABLE'),
('DR. MARIA SANTOS',      'CARDIOLOGY',          '09281234567', 'AVAILABLE'),
('DR. PEDRO REYES',       'PEDIATRICS',          '09351234567', 'AVAILABLE'),
('DR. ANA GARCIA',        'NEUROLOGY',           '09461234567', 'AVAILABLE'),
('DR. CARLOS MENDOZA',    'EMERGENCY MEDICINE',  '09571234567', 'AVAILABLE');

-- =====================================================
--   VERIFY
-- =====================================================
SELECT * FROM doctors;
