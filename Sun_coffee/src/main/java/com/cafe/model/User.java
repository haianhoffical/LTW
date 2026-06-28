package com.cafe.model;

/**
 * User.java – [A1] Model tài khoản đăng nhập
 * Mapping với bảng `users` trong database.
 *
 * SQL tạo bảng (thống nhất cả nhóm):
 * CREATE TABLE users (
 *   user_id    INT AUTO_INCREMENT PRIMARY KEY,
 *   username   VARCHAR(50)  NOT NULL UNIQUE,
 *   password   VARCHAR(255) NOT NULL,
 *   full_name  VARCHAR(100) NOT NULL,
 *   role       ENUM('admin','staff') DEFAULT 'staff',
 *   created_at DATETIME DEFAULT CURRENT_TIMESTAMP
 * );
 * INSERT INTO users (username, password, full_name, role)
 * VALUES ('admin','admin123','Quản trị viên','admin'),
 *        ('staff','staff123','Nhân viên A','staff');
 */
public class User {
    private int    userId;
    private String username;
    private String password;   // TODO: hash BCrypt khi production
    private String fullName;
    private String role;       // "admin" | "staff"

    public User() {}

    public User(int userId, String username, String password, String fullName, String role) {
        this.userId   = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role     = role;
    }

    // Getters & Setters
    public int    getUserId()              { return userId; }
    public void   setUserId(int userId)    { this.userId = userId; }
    public String getUsername()            { return username; }
    public void   setUsername(String u)    { this.username = u; }
    public String getPassword()            { return password; }
    public void   setPassword(String p)    { this.password = p; }
    public String getFullName()            { return fullName; }
    public void   setFullName(String fn)   { this.fullName = fn; }
    public String getRole()                { return role; }
    public void   setRole(String role)     { this.role = role; }

    /** Kiểm tra quyền admin */
    public boolean isAdmin() { return "admin".equals(this.role); }
}
