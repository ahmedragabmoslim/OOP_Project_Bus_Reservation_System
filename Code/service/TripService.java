package busbooking.service;

import busbooking.*;
import busbooking.Trip;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class TripService {

    public List<Trip> searchTrips(String departureStation, String arrivalStation, LocalDate departureDate) {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT t.trip_id, t.departure_station, t.arrival_station, t.departure_date, " +
                "t.departure_time, t.arrival_time, t.bus_number, t.price, t.total_seats, " +
                "b.model as bus_model " +
                "FROM trips t " +
                "JOIN buses b ON t.bus_number = b.bus_number " +
                "WHERE t.departure_station LIKE ? AND t.arrival_station LIKE ? AND t.departure_date = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + departureStation + "%");
            pstmt.setString(2, "%" + arrivalStation + "%");
            pstmt.setDate(3, Date.valueOf(departureDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LocalTime depTime = (rs.getTime("departure_time") != null) ? rs.getTime("departure_time").toLocalTime() : null;
                    LocalTime arrTime = (rs.getTime("arrival_time") != null) ? rs.getTime("arrival_time").toLocalTime() : null;
                    trips.add(new Trip(
                            rs.getInt("trip_id"), rs.getString("departure_station"), rs.getString("arrival_station"),
                            rs.getDate("departure_date").toLocalDate(), depTime, arrTime,
                            rs.getString("bus_number"), rs.getBigDecimal("price"), rs.getInt("total_seats"),
                            rs.getString("bus_model")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("TripService SQL Error searching trips: " + e.getMessage());
            e.printStackTrace();
        }
        return trips;
    }

    public boolean addTrip(Trip trip) {
        if (trip == null) return false;
        String sql = "INSERT INTO trips (departure_station, arrival_station, departure_date, " +
                "departure_time, arrival_time, bus_number, price, total_seats) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, trip.getDepartureStation());
            pstmt.setString(2, trip.getArrivalStation());
            pstmt.setDate(3, Date.valueOf(trip.getDepartureDate()));
            pstmt.setTime(4, trip.getDepartureTime() != null ? Time.valueOf(trip.getDepartureTime()) : null);
            pstmt.setTime(5, trip.getArrivalTime() != null ? Time.valueOf(trip.getArrivalTime()) : null);
            pstmt.setString(6, trip.getBusNumber());
            pstmt.setBigDecimal(7, trip.getPrice());
            pstmt.setInt(8, trip.getTotalSeatsAvailable());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        trip.setTripId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("TripService SQL Error adding trip: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Trip> getAllTripsWithDetails() {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT t.trip_id, t.departure_station, t.arrival_station, t.departure_date, " +
                "t.departure_time, t.arrival_time, t.bus_number, t.price, t.total_seats, " +
                "b.model as bus_model " +
                "FROM trips t " +
                "JOIN buses b ON t.bus_number = b.bus_number " +
                "ORDER BY t.departure_date DESC, t.departure_time DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                LocalTime depTime = (rs.getTime("departure_time") != null) ? rs.getTime("departure_time").toLocalTime() : null;
                LocalTime arrTime = (rs.getTime("arrival_time") != null) ? rs.getTime("arrival_time").toLocalTime() : null;
                trips.add(new Trip(
                        rs.getInt("trip_id"), rs.getString("departure_station"), rs.getString("arrival_station"),
                        rs.getDate("departure_date").toLocalDate(), depTime, arrTime,
                        rs.getString("bus_number"), rs.getBigDecimal("price"), rs.getInt("total_seats"),
                        rs.getString("bus_model")
                ));
            }
        } catch (SQLException e) {
            System.err.println("TripService SQL Error getting all trips: " + e.getMessage());
            e.printStackTrace();
        }
        return trips;
    }

    public boolean updateTrip(Trip trip) {
        if (trip == null || trip.getTripId() <= 0) return false;
        String sql = "UPDATE trips SET departure_station = ?, arrival_station = ?, departure_date = ?, " +
                "departure_time = ?, arrival_time = ?, bus_number = ?, price = ?, total_seats = ? " +
                "WHERE trip_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, trip.getDepartureStation());
            pstmt.setString(2, trip.getArrivalStation());
            pstmt.setDate(3, Date.valueOf(trip.getDepartureDate()));
            pstmt.setTime(4, trip.getDepartureTime() != null ? Time.valueOf(trip.getDepartureTime()) : null);
            pstmt.setTime(5, trip.getArrivalTime() != null ? Time.valueOf(trip.getArrivalTime()) : null);
            pstmt.setString(6, trip.getBusNumber());
            pstmt.setBigDecimal(7, trip.getPrice());
            pstmt.setInt(8, trip.getTotalSeatsAvailable());
            pstmt.setInt(9, trip.getTripId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("TripService SQL Error updating trip: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteTrip(int tripId) throws SQLException {
        if (tripId <= 0) return false;
        String sql = "DELETE FROM trips WHERE trip_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tripId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("TripService SQL Error deleting trip (ID: " + tripId + "): " + e.getMessage() + " SQLState: " + e.getSQLState());
            throw e;
        }
    }
}