import java.util.List;
import java.util.Scanner;

public class DoctorSystem {

    static final Scanner   sc  = new Scanner(System.in);
    static final DoctorDAO dao = new DoctorDAO();

    static final String DIVIDER = "==============================================";
    static final String THIN    = "----------------------------------------------";

    // ══════════════════════════════════════════════
    //  MAIN
    // ══════════════════════════════════════════════
    public static void main(String[] args) {
        dao.createTablesIfNotExists();

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = sc.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1" -> addDoctor();
                case "2" -> assignDoctor();
                case "3" -> viewAllDoctors();
                case "4" -> viewBySpecialization();
                case "5" -> viewAssignments();
                case "6" -> {
                    System.out.println(DIVIDER);
                    System.out.println("  Exiting... Thank you!");
                    System.out.println(DIVIDER);
                    running = false;
                }
                default -> System.out.println("[ERROR]: Invalid option. Enter 1-6.");
            }
        }
        sc.close();
    }

    // ──────────────────────────────────────────────
    //  MAIN MENU
    // ──────────────────────────────────────────────
    static void printMainMenu() {
        System.out.println();
        System.out.println(DIVIDER);
        System.out.println("    --- DOCTOR & ASSIGNMENT SYSTEM ---");
        System.out.println(DIVIDER);
        System.out.println("  [1] ADD DOCTOR");
        System.out.println("  [2] ASSIGN DOCTOR TO PATIENT");
        System.out.println("  [3] VIEW ALL DOCTORS");
        System.out.println("  [4] VIEW DOCTORS BY SPECIALIZATION");
        System.out.println("  [5] VIEW ASSIGNMENTS");
        System.out.println("  [6] EXIT");
        System.out.println(THIN);
        System.out.print("  Select Option: ");
    }

    // ══════════════════════════════════════════════
    //  1. ADD DOCTOR
    // ══════════════════════════════════════════════
    static void addDoctor() {
        System.out.println(DIVIDER);
        System.out.println("    --- ADD DOCTOR ---");
        System.out.println(THIN);

        System.out.print("  Enter Full Name (Last, First M.I.): ");
        String name = sc.nextLine().trim();

        System.out.print("  Enter Age: ");
        String ageStr = sc.nextLine().trim();

        System.out.print("  Enter Gender (M/F): ");
        String gender = sc.nextLine().trim().toUpperCase();

        System.out.println();
        System.out.println("  Select Specialization:");
        System.out.println("  [1] GENERAL MEDICINE     [2] CARDIOLOGY");
        System.out.println("  [3] PEDIATRICS           [4] ORTHOPEDICS");
        System.out.println("  [5] NEUROLOGY            [6] DERMATOLOGY");
        System.out.println("  [7] EMERGENCY MEDICINE   [8] OB-GYNECOLOGY");
        System.out.println("  [9] OTHER (type manually)");
        System.out.print("  Choice: ");
        String specChoice = sc.nextLine().trim();

        String specialization = switch (specChoice) {
            case "1" -> "GENERAL MEDICINE";
            case "2" -> "CARDIOLOGY";
            case "3" -> "PEDIATRICS";
            case "4" -> "ORTHOPEDICS";
            case "5" -> "NEUROLOGY";
            case "6" -> "DERMATOLOGY";
            case "7" -> "EMERGENCY MEDICINE";
            case "8" -> "OB-GYNECOLOGY";
            default  -> {
                System.out.print("  Enter Specialization: ");
                yield sc.nextLine().trim();
            }
        };

        System.out.print("  Enter Contact Number: ");
        String contact = sc.nextLine().trim();

        System.out.print("  Is Doctor Available? (YES/NO): ");
        String availInput = sc.nextLine().trim().toUpperCase();
        String availability = availInput.equals("YES") ? "AVAILABLE" : "BUSY";

        // ── Validate ──────────────────────────────
        if (name.isEmpty() || ageStr.isEmpty() || gender.isEmpty()
                || specialization.isEmpty() || contact.isEmpty()) {
            System.out.println();
            System.out.println("[ERROR]: All fields are required.");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 120) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("[ERROR]: Invalid age entered.");
            return;
        }

        if (!gender.equals("M") && !gender.equals("F")) {
            System.out.println("[ERROR]: Gender must be M or F.");
            return;
        }

        // ── Save ──────────────────────────────────
        Doctor doctor = new Doctor(name, age, gender, specialization, contact, availability);
        boolean saved = dao.addDoctor(doctor);

        System.out.println();
        System.out.println(THIN);
        if (saved) {
            System.out.println("  [SYSTEM]: DATA SUCCESSFULLY SAVED TO XAMPP.");
            System.out.println("  Check phpMyAdmin to see the result in ALL CAPS!");
        } else {
            System.out.println("  [ERROR]: Failed to save. Check your DB connection.");
        }
        System.out.println(DIVIDER);
    }

    // ══════════════════════════════════════════════
    //  2. ASSIGN DOCTOR TO PATIENT
    // ══════════════════════════════════════════════
    static void assignDoctor() {
        System.out.println(DIVIDER);
        System.out.println("    --- ASSIGN DOCTOR TO PATIENT ---");
        System.out.println(THIN);

        System.out.print("  Enter Patient Name (Last, First M.I.): ");
        String patientName = sc.nextLine().trim();

        if (patientName.isEmpty()) {
            System.out.println("[ERROR]: Patient name cannot be empty.");
            return;
        }

        List<String> specs = dao.getAllSpecializations();
        if (specs.isEmpty()) {
            System.out.println("[ERROR]: No doctors registered yet.");
            return;
        }

        System.out.println();
        System.out.println("  Select Specialization Needed:");
        for (int i = 0; i < specs.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, specs.get(i));
        }
        System.out.print("  Choice: ");
        String specInput = sc.nextLine().trim();

        int specIdx;
        try {
            specIdx = Integer.parseInt(specInput) - 1;
            if (specIdx < 0 || specIdx >= specs.size()) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("[ERROR]: Invalid selection.");
            return;
        }

        String chosenSpec = specs.get(specIdx);
        List<Doctor> available = dao.getAvailableBySpec(chosenSpec);

        if (available.isEmpty()) {
            System.out.println("[ERROR]: No AVAILABLE doctors for " + chosenSpec + ".");
            return;
        }

        System.out.println();
        System.out.println("  Available Doctors - " + chosenSpec + ":");
        System.out.println("  " + THIN);
        System.out.printf("  %-4s  %-30s  %-3s  %-3s%n", "ID", "NAME", "AGE", "SEX");
        System.out.println("  " + THIN);
        for (Doctor d : available) {
            System.out.printf("  %-4d  %-30s  %-3d  %-3s%n",
                    d.getId(), d.getName(), d.getAge(), d.getGender());
        }
        System.out.println("  " + THIN);

        System.out.print("  Enter Doctor ID to Assign: ");
        String idInput = sc.nextLine().trim();

        int doctorId;
        try {
            doctorId = Integer.parseInt(idInput);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR]: Invalid Doctor ID.");
            return;
        }

        boolean validId = available.stream().anyMatch(d -> d.getId() == doctorId);
        if (!validId) {
            System.out.println("[ERROR]: Doctor ID not found or not available.");
            return;
        }

        Doctor chosen = dao.getDoctorById(doctorId);
        boolean saved  = dao.assignDoctor(patientName, doctorId, chosen.getName());

        System.out.println();
        System.out.println(THIN);
        if (saved) {
            System.out.println("  [SYSTEM]: DATA SUCCESSFULLY SAVED TO XAMPP.");
            System.out.println("  Check phpMyAdmin to see the result in ALL CAPS!");
        } else {
            System.out.println("  [ERROR]: Failed to save assignment.");
        }
        System.out.println(DIVIDER);
    }

    // ══════════════════════════════════════════════
    //  3. VIEW ALL DOCTORS
    // ══════════════════════════════════════════════
    static void viewAllDoctors() {
        System.out.println(DIVIDER);
        System.out.println("    --- DOCTOR LIST ---");
        System.out.println(THIN);

        List<Doctor> list = dao.getAllDoctors();

        String line = "+-----+------------------------------+-----+--------+---------------------+--------------+-----------+";
        System.out.println(line);
        System.out.printf("| %-3s | %-28s | %-3s | %-6s | %-19s | %-12s | %-9s |%n",
                "ID", "NAME", "AGE", "GENDER", "SPECIALIZATION", "CONTACT", "STATUS");
        System.out.println(line);

        if (list.isEmpty()) {
            System.out.println("|                              NO DOCTORS REGISTERED YET                                              |");
        } else {
            for (Doctor d : list) {
                System.out.printf("| %-3d | %-28s | %-3d | %-6s | %-19s | %-12s | %-9s |%n",
                        d.getId(), d.getName(), d.getAge(), d.getGender(),
                        d.getSpecialization(), d.getContact(), d.getAvailability());
            }
        }
        System.out.println(line);
        System.out.println("  Total Doctors: " + list.size());
        System.out.println(DIVIDER);
    }

    // ══════════════════════════════════════════════
    //  4. VIEW BY SPECIALIZATION
    // ══════════════════════════════════════════════
    static void viewBySpecialization() {
        System.out.println(DIVIDER);
        System.out.println("    --- VIEW BY SPECIALIZATION ---");
        System.out.println(THIN);

        List<String> specs = dao.getAllSpecializations();
        if (specs.isEmpty()) {
            System.out.println("[ERROR]: No doctors found.");
            return;
        }

        System.out.println("  Registered Specializations:");
        for (int i = 0; i < specs.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, specs.get(i));
        }
        System.out.print("  Choice: ");
        String input = sc.nextLine().trim();

        int idx;
        try {
            idx = Integer.parseInt(input) - 1;
            if (idx < 0 || idx >= specs.size()) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("[ERROR]: Invalid selection.");
            return;
        }

        String spec    = specs.get(idx);
        List<Doctor> doctors = dao.getAvailableBySpec(spec);

        System.out.println();
        System.out.println("  " + spec + " - AVAILABLE DOCTORS");
        String line = "+-----+------------------------------+-----+--------+--------------+";
        System.out.println("  " + line);
        System.out.printf("  | %-3s | %-28s | %-3s | %-6s | %-12s |%n",
                "ID", "NAME", "AGE", "GENDER", "CONTACT");
        System.out.println("  " + line);

        if (doctors.isEmpty()) {
            System.out.println("  |           NO AVAILABLE DOCTORS FOR THIS SPECIALIZATION             |");
        } else {
            for (Doctor d : doctors) {
                System.out.printf("  | %-3d | %-28s | %-3d | %-6s | %-12s |%n",
                        d.getId(), d.getName(), d.getAge(), d.getGender(), d.getContact());
            }
        }
        System.out.println("  " + line);
        System.out.println(DIVIDER);
    }

    // ══════════════════════════════════════════════
    //  5. VIEW ASSIGNMENTS
    // ══════════════════════════════════════════════
    static void viewAssignments() {
        System.out.println(DIVIDER);
        System.out.println("    --- DOCTOR ASSIGNMENTS ---");
        System.out.println(THIN);
        dao.viewAllAssignments();
        System.out.println(DIVIDER);
    }
}
