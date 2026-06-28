package com.cafe.model;

/**
 * OrderDetail.java – Chi tiết hóa đơn (1 dòng = 1 sản phẩm trong đơn)
 *
 * SQL: CREATE TABLE order_details (
 *   detail_id  INT AUTO_INCREMENT PRIMARY KEY,
 *   order_id   INT NOT NULL,
 *   product_id INT NOT NULL,
 *   quantity   INT NOT NULL,
 *   unit_price DECIMAL(12,0) NOT NULL,
 *   subtotal   DECIMAL(12,0) NOT NULL,
 *   FOREIGN KEY (order_id)   REFERENCES orders(order_id),
 *   FOREIGN KEY (product_id) REFERENCES products(product_id)
 * );
 */
public class OrderDetail {
    private int    detailId;
    private int    orderId;
    private int    productId;
    private int    quantity;
    private double unitPrice;  // Giá lúc đặt – cố định dù sản phẩm đổi giá sau
    private double subtotal;   // = quantity * unitPrice
    private String productName;// join với products để hiển thị hóa đơn

    public OrderDetail() {}
    public OrderDetail(int orderId, int productId, int quantity, double unitPrice) {
        this.orderId = orderId; this.productId = productId;
        this.quantity = quantity; this.unitPrice = unitPrice;
        this.subtotal = quantity * unitPrice;
    }

    public int    getDetailId()                    { return detailId; }
    public void   setDetailId(int v)               { this.detailId = v; }
    public int    getOrderId()                     { return orderId; }
    public void   setOrderId(int v)                { this.orderId = v; }
    public int    getProductId()                   { return productId; }
    public void   setProductId(int v)              { this.productId = v; }
    public int    getQuantity()                    { return quantity; }
    public void   setQuantity(int v)               { this.quantity = v; }
    public double getUnitPrice()                   { return unitPrice; }
    public void   setUnitPrice(double v)           { this.unitPrice = v; }
    public double getSubtotal()                    { return subtotal; }
    public void   setSubtotal(double v)            { this.subtotal = v; }
    public String getProductName()                 { return productName; }
    public void   setProductName(String v)         { this.productName = v; }
}
