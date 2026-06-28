package com.cafe.controller;
import com.cafe.dao.UserDAO;
import com.cafe.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;

/**
 * LoginServlet.java – [A1] Xử lý đăng nhập
 * POST /LoginServlet ← login.jsp gửi form username + password
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    /** GET: Chuyển về trang login */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("login.jsp").forward(req, resp);
    }

    /** POST: Xác thực tài khoản */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // Validate không để trống (frontend đã check, backend check lại)
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập đầy đủ thông tin!");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
            return;
        }

        try {
            // Truy vấn database qua UserDAO
            User user = userDAO.login(username.trim(), password);

            if (user != null) {
                // Đăng nhập thành công → lưu vào session
                HttpSession session = req.getSession();
                session.setAttribute("user", user);
                session.setMaxInactiveInterval(3600); // Session hết hạn sau 1 giờ

                // Phân quyền: admin → thống kê, staff → POS
                if (user.isAdmin()) {
                    resp.sendRedirect(req.getContextPath() + "/StatisticServlet");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/staff/order-pos.jsp");
                }
            } else {
                // Sai tài khoản/mật khẩu
                req.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
                req.getRequestDispatcher("login.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }
}
