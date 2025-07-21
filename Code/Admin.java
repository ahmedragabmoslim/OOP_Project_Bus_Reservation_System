package busbooking;

import busbooking.service.BusService;
import busbooking.service.TripService;
import busbooking.service.UserService;
import busbooking.service.BookingService;
import java.sql.SQLException;
import java.util.List;

public class Admin {
    private Traveler adminUser;
    private BusService busService;
    private TripService tripService;
    private UserService userService;
    private BookingService bookingService;

    public Admin(Traveler adminUser) {
        if (adminUser == null || !adminUser.isAdmin()) {
            throw new IllegalArgumentException("User provided to Admin constructor is not an admin or is null.");
        }
        this.adminUser = adminUser;
        this.busService = new BusService();
        this.tripService = new TripService();
        this.userService = new UserService();
        this.bookingService = new BookingService();
    }

    public Traveler getAdminUser() {
        return adminUser;
    }

    public boolean addBus(Bus bus) {
        return busService.addBus(bus);
    }

    public List<Bus> getAllBuses() {
        return busService.getAllBuses();
    }

    public boolean updateBus(Bus bus) {
        return busService.updateBus(bus);
    }

    public boolean deleteBus(String busNumber) throws SQLException {
        return busService.deleteBus(busNumber);
    }

    public boolean addTrip(Trip trip) {
        return tripService.addTrip(trip);
    }

    public List<Trip> getAllTripsWithDetails() {
        return tripService.getAllTripsWithDetails();
    }

    public boolean updateTrip(Trip trip) {
        return tripService.updateTrip(trip);
    }

    public boolean deleteTrip(int tripId) throws SQLException {
        return tripService.deleteTrip(tripId);
    }

    public List<Traveler> getAllUsers() {
        return userService.getAllUsers();
    }

    public boolean updateUserAdminStatus(int userId, boolean isAdminFlag) {
        if (userId == adminUser.getUserId() && !isAdminFlag) {
            System.err.println("Admin cannot remove their own admin status via this method if they are the one invoking it.");
            return false;
        }
        return userService.updateUserAdminStatus(userId, isAdminFlag);
    }

    public List<busbooking.dto.BookingDetailView> getAllBookingsWithDetails() {
        return bookingService.getAllBookingsWithDetails();
    }
}