import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    // ─────────────────────────────────────────────
    //  CREATE TABLE (run once on startup)
    // ─────────────────────────────────────────────
    public void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS doctors ("
                + "doctor_id      INT AUTO_INCREMENT PRIMARY KEY, "
                + "full_name      VARCHAR(100) NOT NULL, "
                + "specialization VARCHAR(100) NOT NULL, "
                + "contact_number VARCHAR(20)  NOT NULL, "
                + "availability   VARCHAR(10)  NOT NULL DEFAULT 'AVAILABLE'"
                + ")";

        String sqlAssign = "CREATE TABLE IF NOT EXISTS doctor_assignments ("
                + "assignment_id  INT AUTO_INCREMENT PRIMARY KEY, "
                + "patient_name   VARCHAR(100) NOT NULL, "
                + "doctor_id      INT NOT NULL, "
                + "assigned_at    DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)"
                + ")";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            stmt.execute(sqlAssign);
        } catch (SQLException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    //  ADD DOCTOR
    // ─────────────────────────────────────────────
    public boolean addDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (full_name, specialization, contact_number, availability) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, doctor.getFullName());
            ps.setString(2, doctor.getSpecialization());
            ps.setString(3, doctor.getContactNumber());
            ps.setString(4, doctor.getAvailability());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("[ERROR]: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────
    //  GET ALL DOCTORS
    // ─────────────────────────────────────────────
    public List<Doctor> getAllDoctors() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY doctor_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Doctor(
                        rs.getInt("doctor_id"),
                        rs.getString("full_name"),
                        rs.getString("specialization"),
                        rs.getString("contact_number"),
                        rs.getString("availability")
                ));
            }
        } catch (SQLException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────
    //  GET DOCTORS BY SPECIALIZATION
    // ─────────────────────────────────────────────
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE specialization = ? AND availability = 'AVAILABLE'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, specialization.toUpperCase());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Doctor(
                        rs.getInt("doctor_id"),
                        rs.getString("full_name"),
                        rs.getString("specialization"),
                        rs.getString("contact_number"),
                        rs.getString("availability")
                ));
            }
        } catch (SQLException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────
    //  GET DOCTOR BY ID
    // ─────────────────────────────────────────────
    public Doctor getDoctorById(int id) {
        String sql = "SELECT * FROM doctors WHERE doctor_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Doctor(
                        rs.getInt("doctor_id"),
                        rs.getString("full_name"),
                        rs.getString("specialization"),
                        rs.getString("contact_number"),
                        rs.getString("availability")
                );
            }
        } catch (SQLException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
        return null;
    }

    // ─────────────────────────────────────────────
    //  ASSIGN DOCTOR TO PATIENT
    // ─────────────────────────────────────────────
    public boolean assignDoctorToPatient(String patientName, int doctorId) {
        String sqlAssign = "INSERT INTO doctor_assignments (patient_name, doctor_id) VALUES (?, ?)";
        String sqlUpdate = "UPDATE doctors SET availability = 'BUSY' WHERE doctor_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // transaction

            try (PreparedStatement ps1 = conn.prepareStatement(sqlAssign);
                 PreparedStatement ps2 = conn.prepareStatement(sqlUpdate)) {

                ps1.setString(1, patientName.toUpperCase());
                ps1.setInt(2, doctorId);
                ps1.executeUpdate();

                ps2.setInt(1, doctorId);
                ps2.executeUpdate();

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("[ERROR]: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.out.println("[ERROR]: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────
    //  VIEW ALL ASSIGNMENTS
    // ─────────────────────────────────────────────
    public void viewAssignments() {
        String sql = "SELECT da.assignment_id, da.patient_name, d.full_name AS doctor_name, "
                   + "d.specialization, da.assigned_at "
                   + "FROM doctor_assignments da "
                   + "JOIN doctors d ON da.doctor_id = d.doctor_id "
                   + "ORDER BY da.assigned_at DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            System.out.println("+------+----------------------+-------------------------+--------------------+---------------------+");
            System.out.printf("| %-4s | %-20s | %-23s | %-18s | %-19s |%n",
                    "ID", "PATIENT NAME", "DOCTOR NAME", "SPECIALIZATION", "ASSIGNED AT");
            System.out.println("+------+----------------------+-------------------------+--------------------+---------------------+");

            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                System.out.printf("| %-4d | %-20s | %-23s | %-18s | %-19s |%n",
                        rs.getInt("assignment_id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getString("specialization"),
                        rs.getString("assigned_at"));
            }

            if (!hasRows) {
                System.out.println("|                         NO ASSIGNMENTS FOUND                                               |");
            }
            System.out.println("+------+----------------------+-------------------------+--------------------+---------------------+");

        } catch (SQLException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    //  GET DISTINCT SPECIALIZATIONS
    // ─────────────────────────────────────────────
    public List<String> getAllSpecializations() {
        List<String> specs = new ArrayList<>();
        String sql = "SELECT DISTINCT specialization FROM doctors ORDER BY specialization";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            while (rs.next()) {
                specs.add(rs.getString("specialization"));
            }
        } catch (SQLException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
        return specs;
    }
}
