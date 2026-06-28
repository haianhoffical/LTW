package com.cafe.controller;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;

/**
 * LogoutServlet.java – [A1] Xử lý đăng xuất
 * POST /LogoutServlet ← nút "Đăng xuất" trên header
 */
@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        // Hủy session hiện tại
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // Chuyển về trang login kèm thông báo
        resp.sendRedirect(req.getContextPath() + "/login.jsp?message=Đã đăng xuất thành công!");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        doPost(req, resp); // Hỗ trợ cả GET
    }
}
