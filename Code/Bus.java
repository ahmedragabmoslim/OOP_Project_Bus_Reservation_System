package busbooking;

public class Bus {
    private String busNumber;
    private String model;
    private int totalSeats;

    public Bus(String busNumber, String model, int totalSeats) {
        this.busNumber = busNumber;
        this.model = model;
        this.totalSeats = totalSeats;
    }

    public String getBusNumber() { return busNumber; }
    public String getModel() { return model; }
    public int getTotalSeats() { return totalSeats; }

    public void setModel(String model) { this.model = model; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    @Override
    public String toString() {
        return "Bus{" +
                "busNumber='" + busNumber + '\'' +
                ", model='" + model + '\'' +
                ", totalSeats=" + totalSeats +
                '}';
    }
}