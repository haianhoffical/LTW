package com.cafe.filter;
import com.cafe.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * AuthenticationFilter.java – Kiểm tra session và phân quyền
 * Chặn truy cập /admin/* và /staff/* nếu chưa đăng nhập.
 * Chặn truy cập /admin/* nếu không phải admin.
 */
@WebFilter(urlPatterns = {"/admin/*", "/staff/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Lấy user từ session (LoginServlet đặt)
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        String path = req.getRequestURI();

        if (user == null) {
            // Chưa đăng nhập → về trang login
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        if (path.contains("/admin/") && !user.isAdmin()) {
            // Nhân viên cố vào trang admin → từ chối
            resp.sendRedirect(req.getContextPath() + "/staff/order-pos.jsp");
            return;
        }

        // Đã đăng nhập và có quyền → tiếp tục
        chain.doFilter(request, response);
    }
}
