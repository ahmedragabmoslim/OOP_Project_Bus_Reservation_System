package busbooking.service;

import busbooking.*;
import busbooking.Traveler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    public List<Traveler> getAllUsers() {
        List<Traveler> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password, email, first_name, last_name, phone_number, is_admin FROM users ORDER BY user_id ASC";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                users.add(new Traveler(
                        rs.getInt("user_id"), rs.getString("username"), rs.getString("password"),
                        rs.getString("email"), rs.getString("first_name"), rs.getString("last_name"),
                        rs.getString("phone_number"), rs.getBoolean("is_admin")
                ));
            }
        } catch (SQLException e) {
            System.err.println("UserService SQL Error getting all users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    public boolean updateUserAdminStatus(int userId, boolean isAdmin) {
        String sql = "UPDATE users SET is_admin = ? WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, isAdmin);
            pstmt.setInt(2, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("UserService SQL Error updating admin status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUserProfile(Traveler user) {
        if (user == null) return false;
        String sql = "UPDATE users SET first_name = ?, last_name = ?, phone_number = ? WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getPhoneNumber());
            pstmt.setInt(4, user.getUserId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("UserService SQL Error updating user profile: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}