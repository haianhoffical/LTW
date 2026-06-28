package com.cafe.model;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order.java – Model hóa đơn
 *
 * SQL: CREATE TABLE orders (
 *   order_id     INT AUTO_INCREMENT PRIMARY KEY,
 *   user_id      INT NOT NULL,
 *   table_id     INT NOT NULL,
 *   total_amount DECIMAL(12,0) NOT NULL,
 *   discount     DECIMAL(12,0) DEFAULT 0,
 *   note         VARCHAR(255),
 *   status       ENUM('pending','completed','cancelled') DEFAULT 'pending',
 *   created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
 *   FOREIGN KEY (user_id) REFERENCES users(user_id)
 * );
 */
public class Order {
    private int           orderId;
    private int           userId;
    private int           tableId;
    private double        totalAmount;
    private double        discount;
    private String        note;
    private String        status;       // pending | completed | cancelled
    private LocalDateTime createdAt;
    private List<OrderDetail> orderDetails;
    private String        staffName;    // join với users để hiển thị

    public Order() {}
    public Order(int userId, int tableId, double totalAmount, double discount, String note) {
        this.userId = userId; this.tableId = tableId;
        this.totalAmount = totalAmount; this.discount = discount;
        this.note = note; this.status = "pending";
    }

    public int    getOrderId()                          { return orderId; }
    public void   setOrderId(int v)                     { this.orderId = v; }
    public int    getUserId()                           { return userId; }
    public void   setUserId(int v)                      { this.userId = v; }
    public int    getTableId()                          { return tableId; }
    public void   setTableId(int v)                     { this.tableId = v; }
    public double getTotalAmount()                      { return totalAmount; }
    public void   setTotalAmount(double v)              { this.totalAmount = v; }
    public double getDiscount()                         { return discount; }
    public void   setDiscount(double v)                 { this.discount = v; }
    public String getNote()                             { return note; }
    public void   setNote(String v)                     { this.note = v; }
    public String getStatus()                           { return status; }
    public void   setStatus(String v)                   { this.status = v; }
    public LocalDateTime getCreatedAt()                 { return createdAt; }
    public void   setCreatedAt(LocalDateTime v)         { this.createdAt = v; }
    public List<OrderDetail> getOrderDetails()          { return orderDetails; }
    public void   setOrderDetails(List<OrderDetail> v)  { this.orderDetails = v; }
    public String getStaffName()                        { return staffName; }
    public void   setStaffName(String v)                { this.staffName = v; }
}
