-- =====================================================
--  DOCTOR & ASSIGNMENT SYSTEM - SQL SETUP SCRIPT
--  Member: Paz, Marnelli C.  |  Age: 21
--  Run this in phpMyAdmin BEFORE running the Java app
-- =====================================================

CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

-- 1. doctors table  (columns in ALL CAPS)
DROP TABLE IF EXISTS doctor_assignments;
DROP TABLE IF EXISTS doctors;

CREATE TABLE doctors (
    ID             INT AUTO_INCREMENT PRIMARY KEY,
    NAME           VARCHAR(100) NOT NULL,
    AGE            INT          NOT NULL,
    GENDER         VARCHAR(10)  NOT NULL,
    SPECIALIZATION VARCHAR(100) NOT NULL,
    CONTACT        VARCHAR(20)  NOT NULL,
    AVAILABILITY   VARCHAR(10)  NOT NULL DEFAULT 'AVAILABLE'
);

-- 2. doctor_assignments table
CREATE TABLE doctor_assignments (
    ID           INT AUTO_INCREMENT PRIMARY KEY,
    PATIENT_NAME VARCHAR(100) NOT NULL,
    DOCTOR_ID    INT NOT NULL,
    DOCTOR_NAME  VARCHAR(100) NOT NULL,
    ASSIGNED_AT  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (DOCTOR_ID) REFERENCES doctors(ID)
);

-- =====================================================
--  SAMPLE DATA
-- =====================================================
INSERT INTO doctors (NAME, AGE, GENDER, SPECIALIZATION, CONTACT, AVAILABILITY) VALUES
('PAZ, MARNELLI C.',         21, 'F', 'GENERAL MEDICINE',    '09171112222', 'AVAILABLE'),
('SANTOS, JUAN MIGUEL A.',   35, 'M', 'CARDIOLOGY',           '09282223333', 'AVAILABLE'),
('REYES, MARIA ELENA B.',    42, 'F', 'PEDIATRICS',           '09393334444', 'AVAILABLE'),
('DELA CRUZ, PEDRO L.',      38, 'M', 'EMERGENCY MEDICINE',   '09454445555', 'AVAILABLE'),
('GARCIA, ANNA ROSE T.',     45, 'F', 'NEUROLOGY',            '09565556666', 'AVAILABLE');

-- =====================================================
--  VERIFY
-- =====================================================
SELECT * FROM doctors;
