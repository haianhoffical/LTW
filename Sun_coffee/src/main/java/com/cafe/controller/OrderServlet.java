package com.cafe.controller;
import com.cafe.dao.OrderDAO;
import com.cafe.dao.ProductDAO;
import com.cafe.model.*;
import com.google.gson.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.*;

/**
 * OrderServlet.java – [A1] Xử lý tạo đơn hàng từ POS
 * POST /OrderServlet?action=createOrder ← staff-script.js submit
 */
@WebServlet("/OrderServlet")
public class OrderServlet extends HttpServlet {

    private OrderDAO   orderDAO   = new OrderDAO();
    private ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        if ("createOrder".equals(action)) {
            createOrder(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/staff/order-pos.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // GET: load danh sách sản phẩm để hiển thị POS
        try {
            ProductDAO pDAO = new ProductDAO();
            com.cafe.dao.CategoryDAO cDAO = new com.cafe.dao.CategoryDAO();
            req.setAttribute("products",   pDAO.getAllProducts());
            req.setAttribute("categories", cDAO.getAllCategories());
            req.getRequestDispatcher("/staff/order-pos.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        }
    }

    /** Tạo đơn hàng mới từ dữ liệu POS */
    private void createOrder(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            // Lấy thông tin người dùng từ session (A1 LoginServlet đặt)
            HttpSession session = req.getSession(false);
            User user = (User) session.getAttribute("user");

            int    tableId  = Integer.parseInt(req.getParameter("tableId"));
            double discount = Double.parseDouble(req.getParameter("discount"));
            double total    = Double.parseDouble(req.getParameter("total"));
            String note     = req.getParameter("note");
            String itemsJson= req.getParameter("itemsJson"); // JSON từ staff-script.js

            // Tạo Order object
            Order order = new Order(user.getUserId(), tableId, total, discount, note);
            int orderId = orderDAO.createOrder(order);

            if (orderId > 0) {
                // Parse JSON danh sách món và lưu từng OrderDetail
                JsonArray items = JsonParser.parseString(itemsJson).getAsJsonArray();
                for (JsonElement el : items) {
                    JsonObject item = el.getAsJsonObject();
                    OrderDetail detail = new OrderDetail(
                        orderId,
                        item.get("productId").getAsInt(),
                        item.get("quantity").getAsInt(),
                        item.get("unitPrice").getAsDouble()
                    );
                    orderDAO.addOrderDetail(detail);
                }
                // Thông báo thành công → quay lại POS
                session.setAttribute("successMessage", "✅ Tạo đơn hàng #" + orderId + " thành công!");
                resp.sendRedirect(req.getContextPath() + "/OrderServlet");
            } else {
                session.setAttribute("errorMessage", "❌ Tạo đơn thất bại!");
                resp.sendRedirect(req.getContextPath() + "/OrderServlet");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/staff/order-pos.jsp");
        }
    }
}
