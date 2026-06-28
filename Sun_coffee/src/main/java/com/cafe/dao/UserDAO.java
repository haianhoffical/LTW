package com.cafe.dao;
import com.cafe.connection.DBConnection;
import com.cafe.model.User;
import java.sql.*;

/** UserDAO.java – [A1] SQL xử lý tài khoản */
public class UserDAO {

    /** Tìm user theo username + password để đăng nhập */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password); // TODO: dùng BCrypt.checkpw() khi production
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null; // Không tìm thấy → sai thông tin
    }

    /** Tìm user theo ID (dùng trong filter kiểm tra session) */
    public User findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    /** Map ResultSet → User object */
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setFullName(rs.getString("full_name"));
        u.setRole(rs.getString("role"));
        return u;
    }
}
