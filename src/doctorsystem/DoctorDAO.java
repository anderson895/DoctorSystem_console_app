import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    // ─────────────────────────────────────────────
    //  CREATE TABLES
    // ─────────────────────────────────────────────
    public void createTablesIfNotExists() {
        String createDoctors =
            "CREATE TABLE IF NOT EXISTS doctors (" +
            "  ID             INT AUTO_INCREMENT PRIMARY KEY, " +
            "  NAME           VARCHAR(100) NOT NULL, " +
            "  AGE            INT          NOT NULL, " +
            "  GENDER         VARCHAR(10)  NOT NULL, " +
            "  SPECIALIZATION VARCHAR(100) NOT NULL, " +
            "  CONTACT        VARCHAR(20)  NOT NULL, " +
            "  AVAILABILITY   VARCHAR(10)  NOT NULL DEFAULT 'AVAILABLE'" +
            ")";

        String createAssignments =
            "CREATE TABLE IF NOT EXISTS doctor_assignments (" +
            "  ID           INT AUTO_INCREMENT PRIMARY KEY, " +
            "  PATIENT_NAME VARCHAR(100) NOT NULL, " +
            "  DOCTOR_ID    INT NOT NULL, " +
            "  DOCTOR_NAME  VARCHAR(100) NOT NULL, " +
            "  ASSIGNED_AT  DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "  FOREIGN KEY (DOCTOR_ID) REFERENCES doctors(ID)" +
            ")";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt   = conn.createStatement()) {
            stmt.execute(createDoctors);
            stmt.execute(createAssignments);
        } catch (SQLException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    //  ADD DOCTOR
    // ─────────────────────────────────────────────
    public boolean addDoctor(Doctor d) {
        String sql = "INSERT INTO doctors " +
                     "(NAME, AGE, GENDER, SPECIALIZATION, CONTACT, AVAILABILITY) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setInt   (2, d.getAge());
            ps.setString(3, d.getGender());
            ps.setString(4, d.getSpecialization());
            ps.setString(5, d.getContact());
            ps.setString(6, d.getAvailability());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("[ERROR]: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────
    //  ASSIGN DOCTOR TO PATIENT
    // ─────────────────────────────────────────────
    public boolean assignDoctor(String patientName, int doctorId, String doctorName) {
        String sqlInsert = "INSERT INTO doctor_assignments " +
                           "(PATIENT_NAME, DOCTOR_ID, DOCTOR_NAME) VALUES (?, ?, ?)";
        String sqlUpdate = "UPDATE doctors SET AVAILABILITY = 'BUSY' WHERE ID = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlInsert);
                 PreparedStatement ps2 = conn.prepareStatement(sqlUpdate)) {
                ps1.setString(1, patientName.toUpperCase());
                ps1.setInt   (2, doctorId);
                ps1.setString(3, doctorName);
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
    //  GET ALL DOCTORS
    // ─────────────────────────────────────────────
    public List<Doctor> getAllDoctors() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY ID";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt   = conn.createStatement();
             ResultSet rs     = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Doctor(
                    rs.getInt   ("ID"),
                    rs.getString("NAME"),
                    rs.getInt   ("AGE"),
                    rs.getString("GENDER"),
                    rs.getString("SPECIALIZATION"),
                    rs.getString("CONTACT"),
                    rs.getString("AVAILABILITY")
                ));
            }
        } catch (SQLException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────
    //  GET AVAILABLE DOCTORS BY SPECIALIZATION
    // ─────────────────────────────────────────────
    public List<Doctor> getAvailableBySpec(String spec) {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors " +
                     "WHERE SPECIALIZATION = ? AND AVAILABILITY = 'AVAILABLE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, spec.toUpperCase());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Doctor(
                    rs.getInt   ("ID"),
                    rs.getString("NAME"),
                    rs.getInt   ("AGE"),
                    rs.getString("GENDER"),
                    rs.getString("SPECIALIZATION"),
                    rs.getString("CONTACT"),
                    rs.getString("AVAILABILITY")
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
        String sql = "SELECT * FROM doctors WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Doctor(
                    rs.getInt   ("ID"),
                    rs.getString("NAME"),
                    rs.getInt   ("AGE"),
                    rs.getString("GENDER"),
                    rs.getString("SPECIALIZATION"),
                    rs.getString("CONTACT"),
                    rs.getString("AVAILABILITY")
                );
            }
        } catch (SQLException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
        return null;
    }

    // ─────────────────────────────────────────────
    //  GET ALL SPECIALIZATIONS
    // ─────────────────────────────────────────────
    public List<String> getAllSpecializations() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT SPECIALIZATION FROM doctors ORDER BY SPECIALIZATION";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt   = conn.createStatement();
             ResultSet rs     = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(rs.getString("SPECIALIZATION"));
        } catch (SQLException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────
    //  VIEW ALL ASSIGNMENTS
    // ─────────────────────────────────────────────
    public void viewAllAssignments() {
        String sql = "SELECT * FROM doctor_assignments ORDER BY ID";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt   = conn.createStatement();
             ResultSet rs     = stmt.executeQuery(sql)) {

            String line = "+-----+------------------------------+----------+------------------------------+---------------------+";
            System.out.println(line);
            System.out.printf("| %-3s | %-28s | %-8s | %-28s | %-19s |%n",
                    "ID", "PATIENT NAME", "DOCTOR ID", "DOCTOR NAME", "ASSIGNED AT");
            System.out.println(line);

            boolean empty = true;
            while (rs.next()) {
                empty = false;
                System.out.printf("| %-3d | %-28s | %-8d | %-28s | %-19s |%n",
                    rs.getInt   ("ID"),
                    rs.getString("PATIENT_NAME"),
                    rs.getInt   ("DOCTOR_ID"),
                    rs.getString("DOCTOR_NAME"),
                    rs.getString("ASSIGNED_AT"));
            }
            if (empty) {
                System.out.println("|                         NO ASSIGNMENTS RECORDED YET                                         |");
            }
            System.out.println(line);

        } catch (SQLException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
    }
}
