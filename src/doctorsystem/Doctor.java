public class Doctor {
    private int    id;
    private String name;
    private int    age;
    private String gender;
    private String specialization;
    private String contact;
    private String availability;

    // Constructor for new doctor (no ID yet)
    public Doctor(String name, int age, String gender,
                  String specialization, String contact, String availability) {
        this.name           = name.toUpperCase();
        this.age            = age;
        this.gender         = gender.toUpperCase();
        this.specialization = specialization.toUpperCase();
        this.contact        = contact;
        this.availability   = availability.toUpperCase();
    }

    // Constructor from DB (has ID)
    public Doctor(int id, String name, int age, String gender,
                  String specialization, String contact, String availability) {
        this.id             = id;
        this.name           = name;
        this.age            = age;
        this.gender         = gender;
        this.specialization = specialization;
        this.contact        = contact;
        this.availability   = availability;
    }

    public int    getId()             { return id; }
    public String getName()           { return name; }
    public int    getAge()            { return age; }
    public String getGender()         { return gender; }
    public String getSpecialization() { return specialization; }
    public String getContact()        { return contact; }
    public String getAvailability()   { return availability; }
    public void   setAvailability(String a) { this.availability = a.toUpperCase(); }
}
