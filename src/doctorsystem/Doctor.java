public class Doctor {
    private int doctorId;
    private String fullName;
    private String specialization;
    private String contactNumber;
    private String availability; // AVAILABLE / BUSY

    // Constructor for adding new doctor
    public Doctor(String fullName, String specialization, String contactNumber, String availability) {
        this.fullName = fullName.toUpperCase();
        this.specialization = specialization.toUpperCase();
        this.contactNumber = contactNumber;
        this.availability = availability.toUpperCase();
    }

    // Constructor with ID (from DB)
    public Doctor(int doctorId, String fullName, String specialization, String contactNumber, String availability) {
        this.doctorId = doctorId;
        this.fullName = fullName;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.availability = availability;
    }

    // Getters
    public int getDoctorId()         { return doctorId; }
    public String getFullName()      { return fullName; }
    public String getSpecialization(){ return specialization; }
    public String getContactNumber() { return contactNumber; }
    public String getAvailability()  { return availability; }

    // Setters
    public void setAvailability(String availability) {
        this.availability = availability.toUpperCase();
    }

    @Override
    public String toString() {
        return String.format("| %-4d | %-25s | %-20s | %-15s | %-10s |",
                doctorId, fullName, specialization, contactNumber, availability);
    }
}
