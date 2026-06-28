<%--
  =============================================
   staff/order-pos.jsp – [A2] Màn hình Bán hàng / POS
   Sun Coffee Management System
   Phụ trách: A2
  
   Data nhận từ backend:
   - ${products}   : List<Product>  – A1 truyền qua OrderServlet/ProductServlet
   - ${categories} : List<Category> – B1 CategoryDAO cung cấp
   - ${user}       : User (session) – A1 LoginServlet đặt
  
   Submit tới: OrderServlet (A1) – action=createOrder
  =============================================
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- Chặn truy cập nếu chưa đăng nhập (AuthenticationFilter A1 cũng kiểm tra) --%>
<c:if test="${empty sessionScope.user}">
    <c:redirect url="../login.jsp"/>
</c:if>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bán hàng – Sun Coffee</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=Nunito:wght@400;600;700;800&display=swap"
          rel="stylesheet">
    <!-- CSS giao diện POS -->
    <link rel="stylesheet" href="../assets/css/staff-style.css">
</head>
<body>

<!-- ═══ HEADER NHÂN VIÊN ════════════════════════════ -->
<header class="staff-header">
    <div class="staff-header-brand">
        ☕ <span>Sun Coffee</span>
        <span style="font-size:.7rem;opacity:.6;margin-left:.25rem">| Bán hàng</span>
    </div>
    <div class="staff-header-user">
        <!-- Tên nhân viên đang đăng nhập từ session (A1) -->
        <span>👤 ${sessionScope.user.fullName}</span>
        <!-- Nút đăng xuất -->
        <form action="../LogoutServlet" method="post" style="display:inline">
            <button type="submit" style="
                background:rgba(255,255,255,.15); border:1px solid rgba(255,255,255,.3);
                color:#fff; padding:.3rem .7rem; border-radius:6px;
                font-size:.74rem; cursor:pointer; font-family:var(--font-body);
            ">🚪 Đăng xuất</button>
        </form>
    </div>
</header>

<!-- ═══ BỐ CỤC POS: MENU (TRÁI) + ĐƠN HÀNG (PHẢI) ═ -->
<div class="pos-layout">

    <!-- ══ CỘT TRÁI: Danh sách món ══════════════════ -->
    <div class="menu-panel">

        <!-- Thanh tìm kiếm + lọc danh mục -->
        <div class="menu-search-bar">
            <!-- Ô tìm kiếm gọi hàm searchMenu() trong staff-script.js -->
            <input type="text"
                   class="menu-search-input"
                   placeholder="🔍 Tìm tên món..."
                   oninput="searchMenu(this.value)">

            <!-- Nút lọc "Tất cả" -->
            <button class="category-btn active"
                    onclick="filterByCategory('all', this)">
                Tất cả
            </button>

            <%-- Render nút danh mục từ database – B1 CategoryDAO cung cấp --%>
            <c:forEach var="cat" items="${categories}">
                <button class="category-btn"
                        onclick="filterByCategory('${cat.categoryId}', this)">
                    ${cat.categoryName}
                </button>
            </c:forEach>
        </div>

        <!-- Lưới sản phẩm -->
        <div class="menu-grid" id="menu-grid">

            <%-- Vòng lặp qua danh sách sản phẩm từ backend (B1 ProductDAO) --%>
            <c:forEach var="product" items="${products}">
                <%--
                    data-name, data-category dùng để filter/search bằng JS
                    (staff-script.js đọc các data-* này)
                --%>
                <div class="menu-item-wrapper"
                     data-name="${product.productName}"
                     data-category="${product.categoryId}">

                    <div class="menu-item-card ${product.status == 'inactive' ? 'out-of-stock' : ''}"
                         onclick="addToCart(${product.productId}, '${product.productName}', ${product.price})"
                         title="${product.productName}">

                        <!-- Ảnh sản phẩm -->
                        <c:choose>
                            <c:when test="${not empty product.image}">
                                <img src="../assets/images/${product.image}"
                                     alt="${product.productName}"
                                     class="menu-item-img"
                                     onerror="this.style.display='none'; this.nextElementSibling.style.display='flex'">
                                <div class="menu-item-img-placeholder" style="display:none">☕</div>
                            </c:when>
                            <c:otherwise>
                                <div class="menu-item-img-placeholder">☕</div>
                            </c:otherwise>
                        </c:choose>

                        <!-- Thông tin sản phẩm -->
                        <div class="menu-item-body">
                            <div class="menu-item-name">${product.productName}</div>
                            <div class="menu-item-price">
                                <%-- Format giá VNĐ bằng JSTL fmt --%>
                                <fmt:formatNumber value="${product.price}" type="currency"
                                                  currencySymbol="₫" maxFractionDigits="0"/>
                            </div>
                        </div>

                    </div>

                    <%-- Badge "Hết hàng" cho món không hoạt động --%>
                    <c:if test="${product.status == 'inactive'}">
                        <div class="out-of-stock-badge">Hết hàng</div>
                    </c:if>

                </div>
            </c:forEach>

            <%-- Hiện thông báo nếu chưa có sản phẩm nào --%>
            <c:if test="${empty products}">
                <div style="grid-column:1/-1; text-align:center; padding:3rem; color:var(--muted)">
                    <div style="font-size:3rem;margin-bottom:.75rem">☕</div>
                    <p>Chưa có sản phẩm nào.<br>Admin hãy thêm sản phẩm vào thực đơn.</p>
                </div>
            </c:if>

        </div>
    </div>

    <!-- ══ CỘT PHẢI: Đơn hàng hiện tại ═════════════ -->
    <div class="order-panel">

        <!-- Header đơn hàng -->
        <div class="order-panel-header">
            <h3>🛒 Đơn hàng
                <span id="cart-item-count"
                      style="background:var(--gold);color:var(--coffee-dark);
                             width:20px;height:20px;border-radius:50%;
                             display:inline-flex;align-items:center;justify-content:center;
                             font-size:.7rem;font-weight:800;margin-left:.35rem">0</span>
            </h3>
            <!-- Nút xóa đơn -->
            <button onclick="clearCart()" style="
                background:none; border:1px solid var(--border);
                border-radius:6px; padding:.3rem .65rem;
                font-size:.75rem; color:var(--muted); cursor:pointer;
            " title="Xóa toàn bộ đơn">🗑️ Xóa đơn</button>
        </div>

        <!-- Chọn số bàn -->
        <div class="table-selector">
            <label>🪑 Bàn số:</label>
            <select id="table-select" class="table-select">
                <c:forEach begin="1" end="15" var="i">
                    <option value="${i}">Bàn ${i}</option>
                </c:forEach>
            </select>
        </div>

        <!-- Danh sách món trong giỏ (staff-script.js render vào đây) -->
        <div class="cart-items" id="cart-items"></div>

        <!-- Trạng thái giỏ trống -->
        <div class="cart-empty" id="cart-empty">
            <div class="empty-icon">🛒</div>
            <p>Chưa có món nào<br><small>Nhấn vào món bên trái để thêm</small></p>
        </div>

        <!-- Footer: Tổng tiền + Thanh toán -->
        <div class="order-panel-footer">

            <!-- Dòng tạm tính -->
            <div class="total-row">
                <span>Tạm tính:</span>
                <span id="subtotal">0 ₫</span>
            </div>

            <!-- Ô giảm giá (nhập tay) -->
            <div class="discount-row">
                <label>Giảm giá:</label>
                <input type="number"
                       id="discount-amount"
                       class="discount-input"
                       placeholder="0"
                       min="0"
                       oninput="onDiscountChange()">
                <span style="font-size:.78rem;color:var(--muted)">₫</span>
            </div>

            <!-- Ghi chú đơn hàng -->
            <div style="margin-bottom:.5rem">
                <input type="text"
                       id="order-note"
                       style="width:100%;padding:.38rem .65rem;
                              border:2px solid var(--border);border-radius:8px;
                              font-size:.8rem;font-family:var(--font-body)"
                       placeholder="💬 Ghi chú (ít đường, không đá,...)">
            </div>

            <hr class="total-divider">

            <!-- Tổng cuối -->
            <div class="grand-total">
                <span>Tổng cộng:</span>
                <span class="amount" id="grand-total">0 ₫</span>
            </div>

            <!-- Nút Thanh toán – gọi submitOrder() trong staff-script.js -->
            <button class="pay-btn" id="pay-btn"
                    onclick="submitOrder()" disabled>
                💳 Thanh toán
            </button>

            <!-- Nút Xóa đơn nhỏ hơn -->
            <button class="clear-btn" onclick="clearCart()">
                Xóa toàn bộ đơn hàng
            </button>

        </div>
    </div>

</div><!-- end .pos-layout -->

<!-- JS cho trang POS -->
<script src="../assets/js/staff-script.js"></script>
</body>
</html>
