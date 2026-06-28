package com.cafe.dao;
import com.cafe.connection.DBConnection;
import com.cafe.model.Order;
import com.cafe.model.OrderDetail;
import java.sql.*;
import java.util.*;

/** OrderDAO.java – [A1] SQL xử lý đơn hàng và chi tiết */
public class OrderDAO {

    /** Tạo đơn hàng mới, trả về orderId vừa tạo */
    public int createOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders(user_id,table_id,total_amount,discount,note,status) VALUES(?,?,?,?,?,'pending')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getUserId());
            ps.setInt(2, order.getTableId());
            ps.setDouble(3, order.getTotalAmount());
            ps.setDouble(4, order.getDiscount());
            ps.setString(5, order.getNote());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1); // Trả về orderId mới
        }
        return -1;
    }

    /** Thêm từng dòng chi tiết đơn hàng */
    public void addOrderDetail(OrderDetail detail) throws SQLException {
        String sql = "INSERT INTO order_details(order_id,product_id,quantity,unit_price,subtotal) VALUES(?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detail.getOrderId());
            ps.setInt(2, detail.getProductId());
            ps.setInt(3, detail.getQuantity());
            ps.setDouble(4, detail.getUnitPrice());
            ps.setDouble(5, detail.getSubtotal());
            ps.executeUpdate();
        }
    }

    /** Lấy tất cả đơn hàng (kèm tên nhân viên) */
    public List<Order> getAllOrders() throws SQLException {
        String sql = "SELECT o.*, u.full_name AS staff_name FROM orders o JOIN users u ON o.user_id=u.user_id ORDER BY o.created_at DESC";
        List<Order> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    /** Cập nhật trạng thái đơn hàng */
    public void updateStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET status=? WHERE order_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt("order_id"));
        o.setUserId(rs.getInt("user_id"));
        o.setTableId(rs.getInt("table_id"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setDiscount(rs.getDouble("discount"));
        o.setNote(rs.getString("note"));
        o.setStatus(rs.getString("status"));
        try { o.setStaffName(rs.getString("staff_name")); } catch(Exception ignored) {}
        return o;
    }
}
