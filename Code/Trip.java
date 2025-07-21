package busbooking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class Trip {
    private int tripId;
    private String departureStation;
    private String arrivalStation;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String busNumber;
    private BigDecimal price;
    private int totalSeatsAvailable;
    private String busModel;

    public Trip(String departureStation, String arrivalStation, LocalDate departureDate,
                LocalTime departureTime, LocalTime arrivalTime, String busNumber,
                BigDecimal price, int totalSeatsAvailable, String busModel) {
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.busNumber = busNumber;
        this.price = price;
        this.totalSeatsAvailable = totalSeatsAvailable;
        this.busModel = busModel;
    }

    public Trip(int tripId, String departureStation, String arrivalStation, LocalDate departureDate,
                LocalTime departureTime, LocalTime arrivalTime, String busNumber,
                BigDecimal price, int totalSeatsAvailable, String busModel) {
        this(departureStation, arrivalStation, departureDate, departureTime, arrivalTime, busNumber, price, totalSeatsAvailable, busModel);
        this.tripId = tripId;
    }

    public int getTripId() { return tripId; }
    public String getDepartureStation() { return departureStation; }
    public String getArrivalStation() { return arrivalStation; }
    public LocalDate getDepartureDate() { return departureDate; }
    public LocalTime getDepartureTime() { return departureTime; }
    public LocalTime getArrivalTime() { return arrivalTime; }
    public String getBusNumber() { return busNumber; }
    public BigDecimal getPrice() { return price; }
    public int getTotalSeatsAvailable() { return totalSeatsAvailable; }
    public String getBusModel() { return busModel; }


    public void setTripId(int tripId) { this.tripId = tripId; }
    public void setDepartureStation(String departureStation) { this.departureStation = departureStation; }
    public void setArrivalStation(String arrivalStation) { this.arrivalStation = arrivalStation; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setTotalSeatsAvailable(int totalSeatsAvailable) { this.totalSeatsAvailable = totalSeatsAvailable; }
    public void setBusModel(String busModel) { this.busModel = busModel; }

    @Override
    public String toString() {
        return "Trip{" +
                "tripId=" + tripId +
                ", from='" + departureStation + '\'' +
                ", to='" + arrivalStation + '\'' +
                ", date=" + departureDate +
                ", time=" + departureTime +
                ", busModel='" + busModel + '\'' +
                ", price=" + price +
                '}';
    }
}