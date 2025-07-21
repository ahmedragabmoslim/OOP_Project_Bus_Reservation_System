package busbooking;

public class Seat {
    private String seatNumber; 
    private boolean isBooked;
    private boolean isSelected; 

    public enum SeatStatus { AVAILABLE, BOOKED, SELECTED, AISLE, UNAVAILABLE }
    private SeatStatus status;


    public Seat(String seatNumber) {
        this.seatNumber = seatNumber;
        this.status = SeatStatus.AVAILABLE; 
    }

    public Seat(String seatNumber, SeatStatus status) {
        this.seatNumber = seatNumber;
        this.status = status;
    }


    public String getSeatNumber() {
        return seatNumber;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "seatNumber='" + seatNumber + '\'' +
                ", status=" + status +
                '}';
    }
}