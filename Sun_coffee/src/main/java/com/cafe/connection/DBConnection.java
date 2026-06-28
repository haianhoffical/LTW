package com.cafe.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection.java – [CHUNG] Kết nối MySQL Database
 * ======================================================
 * Dùng Singleton Pattern: chỉ tạo 1 Connection duy nhất.
 * Tất cả DAO đều gọi DBConnection.getConnection() để lấy kết nối.
 *
 */
public class DBConnection {

    // ═══ THÔNG TIN KẾT NỐI – CHỈNH CHO PHÙ HỢP ═══
    private static final String DB_NAME  = "sun_coffee";  // Tên database MySQL
    private static final String URL      = "jdbc:mysql://localhost:3306/" + DB_NAME
                                         + "?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String USER     = "root";         // Username MySQL
    private static final String PASSWORD = "123456";       // Password MySQL – đổi cho phù hợp máy mình

    // Instance kết nối duy nhất (Singleton)
    private static Connection connection = null;

    /** Constructor private – không cho tạo object bên ngoài */
    private DBConnection() {}

    /**
     * Lấy Connection tới database.
     * Tự động tạo mới nếu chưa có hoặc đã bị đóng.
     *
     * @return java.sql.Connection
     * @throws SQLException nếu không kết nối được
     */
    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");   // Load MySQL Driver
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] ✅ Kết nối database '" + DB_NAME + "' thành công!");
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("❌ Không tìm thấy MySQL Driver: " + e.getMessage());
        }
        return connection;
    }

    /** Đóng kết nối khi tắt ứng dụng */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Đã đóng kết nối.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
