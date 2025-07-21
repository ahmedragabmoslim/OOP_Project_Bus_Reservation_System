package busbooking;

import java.math.BigDecimal;

public class Ticket {
    private int ticketId;   
    private int bookingId; 
    private String passengerName;
    private String seatNumber;
    private BigDecimal price;

    public Ticket(int bookingId, String passengerName, String seatNumber, BigDecimal price) {
        this.bookingId = bookingId;
        this.passengerName = passengerName;
        this.seatNumber = seatNumber;
        this.price = price;
    }

    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    @Override
    public String toString() {
        return "Ticket{" +
                "passengerName='" + passengerName + '\'' +
                ", seatNumber='" + seatNumber + '\'' +
                '}';
    }
}