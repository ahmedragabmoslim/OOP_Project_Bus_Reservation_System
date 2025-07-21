package busbooking;

public class Traveler {
    private int userId;
    private String username;
    private String password; // In a real application, this should be a hashed password
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean isAdmin;

    public Traveler(int userId, String username, String password, String email, String firstName, String lastName, String phoneNumber, boolean isAdmin) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = (phoneNumber == null) ? "" : phoneNumber;
        this.isAdmin = isAdmin;
    }

    public Traveler(String username, String password, String email, String firstName, String lastName, String phoneNumber, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = (phoneNumber == null) ? "" : phoneNumber;
        this.isAdmin = isAdmin;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""); }
    public String getPhoneNumber() { return phoneNumber; }
    public boolean isAdmin() { return isAdmin; }

    public void setUserId(int userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = (phoneNumber == null) ? "" : phoneNumber; }
    public void setAdmin(boolean admin) { this.isAdmin = admin; }

    @Override
    public String toString() {
        return "Traveler{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}