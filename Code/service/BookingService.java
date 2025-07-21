package busbooking.service;

import busbooking.Booking;
import busbooking.*;
import busbooking.Trip;
import busbooking.Traveler;
import busbooking.dto.BookingDetailView;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BookingService {

    public int getTotalTicketsBookedForTrip(int tripId) {
        int totalBookedTickets = 0;
        String sql = "SELECT SUM(number_of_tickets) AS total_booked FROM bookings WHERE trip_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tripId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totalBookedTickets = rs.getInt("total_booked");
                }
            }
        } catch (SQLException e) {
            System.err.println("BookingService SQL Error getting total booked tickets: " + e.getMessage());
            e.printStackTrace();
        }
        return totalBookedTickets;
    }

    public Booking createBooking(Traveler user, Trip trip, List<String> selectedSeats) {
        if (user == null || trip == null || selectedSeats == null || selectedSeats.isEmpty()) {
            System.err.println("BookingService: Invalid parameters for createBooking.");
            return null;
        }

        Booking newBooking = null;
        int numberOfTickets = selectedSeats.size();
        String sql = "INSERT INTO bookings (user_id, trip_id, number_of_tickets, booking_date_time) VALUES (?, ?, ?, ?)";

        Connection conn = null; 
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, user.getUserId());
                pstmt.setInt(2, trip.getTripId());
                pstmt.setInt(3, numberOfTickets);
                pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int bookingId = generatedKeys.getInt(1);
                            newBooking = new Booking(bookingId, user.getUserId(), trip.getTripId(), numberOfTickets, LocalDateTime.now(), trip);
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                if (conn != null) conn.rollback(); 
                throw e; 
            }
        } catch (SQLException e) {
            System.err.println("BookingService SQL Error creating booking: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); 
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return newBooking;
    }

    public List<Booking> getBookingsForUser(int userId) {
        List<Booking> userBookings = new ArrayList<>();
        String sql = "SELECT " +
                "b.booking_id, b.user_id, b.trip_id, b.number_of_tickets, b.booking_date_time, " +
                "t.departure_station, t.arrival_station, t.departure_date, t.departure_time, t.arrival_time, " +
                "t.bus_number AS trip_bus_number, t.price AS trip_price, t.total_seats AS trip_total_seats, " +
                "bs.model AS bus_model " +
                "FROM bookings b " +
                "JOIN trips t ON b.trip_id = t.trip_id " +
                "JOIN buses bs ON t.bus_number = bs.bus_number " +
                "WHERE b.user_id = ? " +
                "ORDER BY b.booking_date_time DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LocalTime depTime = rs.getTime("departure_time") != null ? rs.getTime("departure_time").toLocalTime() : null;
                    LocalTime arrTime = rs.getTime("arrival_time") != null ? rs.getTime("arrival_time").toLocalTime() : null;
                    Trip trip = new Trip(
                            rs.getInt("trip_id"), rs.getString("departure_station"), rs.getString("arrival_station"),
                            rs.getDate("departure_date").toLocalDate(), depTime, arrTime,
                            rs.getString("trip_bus_number"), rs.getBigDecimal("trip_price"), rs.getInt("trip_total_seats"),
                            rs.getString("bus_model")
                    );
                    Booking booking = new Booking(
                            rs.getInt("booking_id"), rs.getInt("user_id"), rs.getInt("trip_id"),
                            rs.getInt("number_of_tickets"), rs.getTimestamp("booking_date_time").toLocalDateTime(),
                            trip
                    );
                    userBookings.add(booking);
                }
            }
        } catch (SQLException e) {
            System.err.println("BookingService SQL Error fetching user bookings: " + e.getMessage());
            e.printStackTrace();
        }
        return userBookings;
    }

    public List<BookingDetailView> getAllBookingsWithDetails() {
        List<BookingDetailView> allBookings = new ArrayList<>();
        String sql = "SELECT " +
                "bk.booking_id, bk.number_of_tickets, bk.booking_date_time, " +
                "u.user_id, u.username, u.email, u.first_name, u.last_name, u.phone_number, u.is_admin, " +
                "t.trip_id, t.departure_station, t.arrival_station, t.departure_date, t.departure_time, t.arrival_time, " +
                "t.bus_number AS trip_bus_number, t.price AS trip_price, t.total_seats AS trip_total_seats, " +
                "bs.model AS bus_model " +
                "FROM bookings bk " +
                "JOIN users u ON bk.user_id = u.user_id " +
                "JOIN trips t ON bk.trip_id = t.trip_id " +
                "JOIN buses bs ON t.bus_number = bs.bus_number " +
                "ORDER BY bk.booking_date_time DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Traveler user = new Traveler(
                        rs.getInt("user_id"), rs.getString("username"), "********",
                        rs.getString("email"), rs.getString("first_name"), rs.getString("last_name"),
                        rs.getString("phone_number"), rs.getBoolean("is_admin")
                );
                LocalTime depTime = rs.getTime("departure_time") != null ? rs.getTime("departure_time").toLocalTime() : null;
                LocalTime arrTime = rs.getTime("arrival_time") != null ? rs.getTime("arrival_time").toLocalTime() : null;
                Trip trip = new Trip(
                        rs.getInt("trip_id"), rs.getString("departure_station"), rs.getString("arrival_station"),
                        rs.getDate("departure_date").toLocalDate(), depTime, arrTime,
                        rs.getString("trip_bus_number"), rs.getBigDecimal("trip_price"), rs.getInt("trip_total_seats"),
                        rs.getString("bus_model")
                );
                Booking bookingCore = new Booking(
                        rs.getInt("booking_id"), user.getUserId(), trip.getTripId(),
                        rs.getInt("number_of_tickets"), rs.getTimestamp("booking_date_time").toLocalDateTime()
                );
                allBookings.add(new BookingDetailView(bookingCore, user, trip));
            }
        } catch (SQLException e) {
            System.err.println("BookingService SQL Error fetching all bookings with details: " + e.getMessage());
            e.printStackTrace();
        }
        return allBookings;
    }
}