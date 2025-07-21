package busbooking;

import java.time.LocalDateTime;

public class Booking {
    private int bookingId;
    private int userId;
    private int tripId;
    private int numberOfTickets;
    private LocalDateTime bookingDateTime;
    private Trip associatedTrip;

    public Booking(int userId, int tripId, int numberOfTickets, LocalDateTime bookingDateTime) {
        this.userId = userId;
        this.tripId = tripId;
        this.numberOfTickets = numberOfTickets;
        this.bookingDateTime = bookingDateTime;
    }

    public Booking(int bookingId, int userId, int tripId, int numberOfTickets, LocalDateTime bookingDateTime) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.tripId = tripId;
        this.numberOfTickets = numberOfTickets;
        this.bookingDateTime = bookingDateTime;
    }

    public Booking(int bookingId, int userId, int tripId, int numberOfTickets, LocalDateTime bookingDateTime, Trip associatedTrip) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.tripId = tripId;
        this.numberOfTickets = numberOfTickets;
        this.bookingDateTime = bookingDateTime;
        this.associatedTrip = associatedTrip;
    }

    public int getBookingId() { return bookingId; }
    public int getUserId() { return userId; }
    public int getTripId() { return tripId; }
    public int getNumberOfTickets() { return numberOfTickets; }
    public LocalDateTime getBookingDateTime() { return bookingDateTime; }
    public Trip getAssociatedTrip() { return associatedTrip; }

    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setTripId(int tripId) { this.tripId = tripId; }
    public void setNumberOfTickets(int numberOfTickets) { this.numberOfTickets = numberOfTickets; }
    public void setBookingDateTime(LocalDateTime bookingDateTime) { this.bookingDateTime = bookingDateTime; }
    public void setAssociatedTrip(Trip associatedTrip) { this.associatedTrip = associatedTrip; }

    @Override
    public String toString() {
        String tripInfo = "Trip ID: " + this.tripId;
        if (associatedTrip != null) {
            tripInfo = (associatedTrip.getDepartureStation() != null ? associatedTrip.getDepartureStation() : "N/A") +
                    " to " + (associatedTrip.getArrivalStation() != null ? associatedTrip.getArrivalStation() : "N/A") +
                    " on " + (associatedTrip.getDepartureDate() != null ? Utils.formatDate(associatedTrip.getDepartureDate()) : "N/A");
        }
        return "Booking{" +
                "bookingId=" + bookingId +
                ", userId=" + userId +
                ", tripInfo=" + tripInfo +
                ", tickets=" + numberOfTickets +
                '}';
    }
}