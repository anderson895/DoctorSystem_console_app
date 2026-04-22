import java.util.List;
import java.util.Scanner;

public class DoctorSystem {

    private static final Scanner sc  = new Scanner(System.in);
    private static final DoctorDAO dao = new DoctorDAO();

    // ══════════════════════════════════════════════
    //  MAIN
    // ══════════════════════════════════════════════
    public static void main(String[] args) {
        dao.createTableIfNotExists();
        boolean running = true;

        printHeader();

        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> addDoctor();
                case "2" -> assignDoctorToPatient();
                case "3" -> viewDoctorList();
                case "4" -> viewBySpecialization();
                case "5" -> viewAssignments();
                case "6" -> {
                    System.out.println();
                    System.out.println("[SYSTEM]: Exiting Doctor & Assignment System. Goodbye!");
                    running = false;
                }
                default  -> System.out.println("[ERROR]: Invalid option. Please enter 1-6.");
            }
        }
        sc.close();
    }

    // ══════════════════════════════════════════════
    //  PRINT HEADER
    // ══════════════════════════════════════════════
    private static void printHeader() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║      DOCTOR & ASSIGNMENT SYSTEM          ║");
        System.out.println("║         Member: Paz                      ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    // ══════════════════════════════════════════════
    //  PRINT MENU
    // ══════════════════════════════════════════════
    private static void printMenu() {
        System.out.println();
        System.out.println("--- MAIN MENU ---");
        System.out.println("[1] ADD DOCTOR");
        System.out.println("[2] ASSIGN DOCTOR TO PATIENT");
        System.out.println("[3] VIEW ALL DOCTORS");
        System.out.println("[4] VIEW DOCTORS BY SPECIALIZATION");
        System.out.println("[5] VIEW ASSIGNMENTS");
        System.out.println("[6] EXIT");
        System.out.print("Select Option: ");
    }

    // ══════════════════════════════════════════════
    //  1. ADD DOCTOR
    // ══════════════════════════════════════════════
    private static void addDoctor() {
        System.out.println();
        System.out.println("--- ADD DOCTOR ---");

        System.out.print("Enter Doctor's Full Name: ");
        String name = sc.nextLine().trim();

        System.out.println("Select Specialization:");
        System.out.println("  [1] GENERAL MEDICINE");
        System.out.println("  [2] CARDIOLOGY");
        System.out.println("  [3] PEDIATRICS");
        System.out.println("  [4] ORTHOPEDICS");
        System.out.println("  [5] NEUROLOGY");
        System.out.println("  [6] DERMATOLOGY");
        System.out.println("  [7] EMERGENCY MEDICINE");
        System.out.println("  [8] OTHER (Type manually)");
        System.out.print("Choice: ");
        String specChoice = sc.nextLine().trim();

        String specialization;
        switch (specChoice) {
            case "1" -> specialization = "GENERAL MEDICINE";
            case "2" -> specialization = "CARDIOLOGY";
            case "3" -> specialization = "PEDIATRICS";
            case "4" -> specialization = "ORTHOPEDICS";
            case "5" -> specialization = "NEUROLOGY";
            case "6" -> specialization = "DERMATOLOGY";
            case "7" -> specialization = "EMERGENCY MEDICINE";
            default  -> {
                System.out.print("Enter Specialization: ");
                specialization = sc.nextLine().trim();
            }
        }

        System.out.print("Enter Contact Number: ");
        String contact = sc.nextLine().trim();

        // Validate inputs
        if (name.isEmpty() || specialization.isEmpty() || contact.isEmpty()) {
            System.out.println("[ERROR]: All fields are required.");
            return;
        }

        Doctor doctor = new Doctor(name, specialization, contact, "AVAILABLE");
        boolean success = dao.addDoctor(doctor);

        System.out.println();
        if (success) {
            System.out.println("[SYSTEM]: DOCTOR SUCCESSFULLY SAVED TO XAMPP.");
            System.out.println("Check phpMyAdmin to see the result in ALL CAPS!");
        } else {
            System.out.println("[ERROR]: Failed to save doctor.");
        }
    }

    // ══════════════════════════════════════════════
    //  2. ASSIGN DOCTOR TO PATIENT
    // ══════════════════════════════════════════════
    private static void assignDoctorToPatient() {
        System.out.println();
        System.out.println("--- ASSIGN DOCTOR TO PATIENT ---");

        System.out.print("Enter Patient Name: ");
        String patientName = sc.nextLine().trim();

        if (patientName.isEmpty()) {
            System.out.println("[ERROR]: Patient name cannot be empty.");
            return;
        }

        // Show available specializations
        List<String> specs = dao.getAllSpecializations();
        if (specs.isEmpty()) {
            System.out.println("[ERROR]: No doctors registered yet. Please add doctors first.");
            return;
        }

        System.out.println();
        System.out.println("Available Specializations:");
        for (int i = 0; i < specs.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, specs.get(i));
        }
        System.out.print("Select Specialization: ");
        String specInput = sc.nextLine().trim();

        int specIndex;
        try {
            specIndex = Integer.parseInt(specInput) - 1;
            if (specIndex < 0 || specIndex >= specs.size()) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("[ERROR]: Invalid selection.");
            return;
        }

        String chosenSpec = specs.get(specIndex);

        // Show available doctors for that specialization
        List<Doctor> availDoctors = dao.getDoctorsBySpecialization(chosenSpec);
        if (availDoctors.isEmpty()) {
            System.out.println("[ERROR]: No AVAILABLE doctors found for " + chosenSpec + ".");
            return;
        }

        System.out.println();
        System.out.println("Available Doctors (" + chosenSpec + "):");
        System.out.println("+------+---------------------------+");
        System.out.printf("| %-4s | %-25s |%n", "ID", "DOCTOR NAME");
        System.out.println("+------+---------------------------+");
        for (Doctor d : availDoctors) {
            System.out.printf("| %-4d | %-25s |%n", d.getDoctorId(), d.getFullName());
        }
        System.out.println("+------+---------------------------+");

        System.out.print("Enter Doctor ID to Assign: ");
        String idInput = sc.nextLine().trim();

        int doctorId;
        try {
            doctorId = Integer.parseInt(idInput);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR]: Invalid Doctor ID.");
            return;
        }

        // Validate the doctor ID belongs to available list
        boolean validId = availDoctors.stream()
                .anyMatch(d -> d.getDoctorId() == doctorId);

        if (!validId) {
            System.out.println("[ERROR]: Doctor ID not found or not available.");
            return;
        }

        Doctor assignedDoctor = dao.getDoctorById(doctorId);
        boolean success = dao.assignDoctorToPatient(patientName, doctorId);

        System.out.println();
        if (success) {
            System.out.println("[SYSTEM]: ASSIGNMENT SUCCESSFULLY SAVED TO XAMPP.");
            System.out.printf("Patient %-20s --> Dr. %s (%s)%n",
                    patientName.toUpperCase(),
                    assignedDoctor.getFullName(),
                    assignedDoctor.getSpecialization());
            System.out.println("Check phpMyAdmin to see the result in ALL CAPS!");
        } else {
            System.out.println("[ERROR]: Failed to assign doctor.");
        }
    }

    // ══════════════════════════════════════════════
    //  3. VIEW ALL DOCTORS
    // ══════════════════════════════════════════════
    private static void viewDoctorList() {
        System.out.println();
        System.out.println("--- DOCTOR LIST ---");

        List<Doctor> doctors = dao.getAllDoctors();

        System.out.println("+------+---------------------------+----------------------+-----------------+------------+");
        System.out.printf("| %-4s | %-25s | %-20s | %-15s | %-10s |%n",
                "ID", "FULL NAME", "SPECIALIZATION", "CONTACT", "STATUS");
        System.out.println("+------+---------------------------+----------------------+-----------------+------------+");

        if (doctors.isEmpty()) {
            System.out.println("|                          NO DOCTORS REGISTERED YET                                  |");
        } else {
            for (Doctor d : doctors) {
                System.out.println(d.toString());
            }
        }
        System.out.println("+------+---------------------------+----------------------+-----------------+------------+");
        System.out.println("Total Doctors: " + doctors.size());
    }

    // ══════════════════════════════════════════════
    //  4. VIEW BY SPECIALIZATION
    // ══════════════════════════════════════════════
    private static void viewBySpecialization() {
        System.out.println();
        System.out.println("--- VIEW DOCTORS BY SPECIALIZATION ---");

        List<String> specs = dao.getAllSpecializations();
        if (specs.isEmpty()) {
            System.out.println("[ERROR]: No doctors found.");
            return;
        }

        System.out.println("Registered Specializations:");
        for (int i = 0; i < specs.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, specs.get(i));
        }
        System.out.print("Select Specialization: ");
        String input = sc.nextLine().trim();

        int idx;
        try {
            idx = Integer.parseInt(input) - 1;
            if (idx < 0 || idx >= specs.size()) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("[ERROR]: Invalid selection.");
            return;
        }

        String spec = specs.get(idx);
        List<Doctor> doctors = dao.getDoctorsBySpecialization(spec);

        System.out.println();
        System.out.println("--- " + spec + " (AVAILABLE ONLY) ---");
        System.out.println("+------+---------------------------+-----------------+");
        System.out.printf("| %-4s | %-25s | %-15s |%n", "ID", "FULL NAME", "CONTACT");
        System.out.println("+------+---------------------------+-----------------+");

        if (doctors.isEmpty()) {
            System.out.println("|          NO AVAILABLE DOCTORS FOR THIS SPECIALIZATION          |");
        } else {
            for (Doctor d : doctors) {
                System.out.printf("| %-4d | %-25s | %-15s |%n",
                        d.getDoctorId(), d.getFullName(), d.getContactNumber());
            }
        }
        System.out.println("+------+---------------------------+-----------------+");
    }

    // ══════════════════════════════════════════════
    //  5. VIEW ASSIGNMENTS
    // ══════════════════════════════════════════════
    private static void viewAssignments() {
        System.out.println();
        System.out.println("--- DOCTOR ASSIGNMENTS ---");
        dao.viewAssignments();
    }
}
