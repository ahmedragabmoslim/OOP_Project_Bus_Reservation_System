package busbooking.dto;

import busbooking.Booking;
import busbooking.Traveler;
import busbooking.Trip;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class BookingDetailView {
    private int bookingId;
    private LocalDateTime bookingDateTime;
    private int numberOfTickets;

    private int userId;
    private String userFullName;
    private String userEmail;

    private int tripId;
    private String tripDepartureStation;
    private String tripArrivalStation;
    private LocalDate tripDepartureDate;
    private LocalTime tripDepartureTime;
    private String tripBusNumber;
    private String tripBusModel;
    private BigDecimal tripPricePerTicket;


    public BookingDetailView(Booking booking, Traveler user, Trip trip) {
        this.bookingId = booking.getBookingId();
        this.bookingDateTime = booking.getBookingDateTime();
        this.numberOfTickets = booking.getNumberOfTickets();

        if (user != null) {
            this.userId = user.getUserId();
            this.userFullName = user.getFullName();
            this.userEmail = user.getEmail();
        }

        if (trip != null) {
            this.tripId = trip.getTripId();
            this.tripDepartureStation = trip.getDepartureStation();
            this.tripArrivalStation = trip.getArrivalStation();
            this.tripDepartureDate = trip.getDepartureDate();
            this.tripDepartureTime = trip.getDepartureTime();
            this.tripBusNumber = trip.getBusNumber();
            this.tripBusModel = trip.getBusModel();
            this.tripPricePerTicket = trip.getPrice();
        }
    }

    // Getters
    public int getBookingId() { return bookingId; }
    public LocalDateTime getBookingDateTime() { return bookingDateTime; }
    public int getNumberOfTickets() { return numberOfTickets; }
    public int getUserId() { return userId; }
    public String getUserFullName() { return userFullName; }
    public String getUserEmail() { return userEmail; }
    public int getTripId() { return tripId; }
    public String getTripDepartureStation() { return tripDepartureStation; }
    public String getTripArrivalStation() { return tripArrivalStation; }
    public LocalDate getTripDepartureDate() { return tripDepartureDate; }
    public LocalTime getTripDepartureTime() { return tripDepartureTime; }
    public String getTripBusNumber() { return tripBusNumber; }
    public String getTripBusModel() { return tripBusModel; }
    public BigDecimal getTripPricePerTicket() { return tripPricePerTicket; }
}