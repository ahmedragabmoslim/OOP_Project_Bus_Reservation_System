package busbooking.service;

import busbooking.Bus;
import busbooking.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BusService {

    public Bus getBusByNumber(String busNumber) {
        Bus bus = null;
        String sql = "SELECT bus_number, model, total_seats FROM buses WHERE bus_number = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, busNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    bus = new Bus(
                            rs.getString("bus_number"),
                            rs.getString("model"),
                            rs.getInt("total_seats")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("BusService SQL Error getting bus by number: " + e.getMessage());
            e.printStackTrace(); 
        }
        return bus;
    }

    public boolean addBus(Bus bus) {
        if (bus == null) return false;
        
        if (getBusByNumber(bus.getBusNumber()) != null) {
            System.err.println("BusService Error: Bus with number '" + bus.getBusNumber() + "' already exists.");
            return false;
        }

        String sql = "INSERT INTO buses (bus_number, model, total_seats) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bus.getBusNumber());
            pstmt.setString(2, bus.getModel());
            pstmt.setInt(3, bus.getTotalSeats());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("BusService SQL Error adding bus: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Bus> getAllBuses() {
        List<Bus> buses = new ArrayList<>();
        String sql = "SELECT bus_number, model, total_seats FROM buses ORDER BY bus_number ASC";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                buses.add(new Bus(
                        rs.getString("bus_number"),
                        rs.getString("model"),
                        rs.getInt("total_seats")
                ));
            }
        } catch (SQLException e) {
            System.err.println("BusService SQL Error getting all buses: " + e.getMessage());
            e.printStackTrace();
        }
        return buses;
    }

    public boolean updateBus(Bus bus) {
        if (bus == null) return false;
        String sql = "UPDATE buses SET model = ?, total_seats = ? WHERE bus_number = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bus.getModel());
            pstmt.setInt(2, bus.getTotalSeats());
            pstmt.setString(3, bus.getBusNumber());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("BusService SQL Error updating bus: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteBus(String busNumber) throws SQLException {
        if (busNumber == null || busNumber.isEmpty()) return false;
        String sql = "DELETE FROM buses WHERE bus_number = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, busNumber);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("BusService SQL Error deleting bus (busNumber: " + busNumber + "): " + e.getMessage() + " SQLState: " + e.getSQLState());
            throw e;
        }
    }
}