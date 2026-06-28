
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Nếu đã đăng nhập rồi → chuyển hướng thẳng --%>
<c:if test="${not empty sessionScope.user}">
    <c:choose>
        <c:when test="${sessionScope.user.role == 'admin'}">
            <c:redirect url="/StatisticServlet"/>
        </c:when>
        <c:otherwise>
            <c:redirect url="/staff/order-pos.jsp"/>
        </c:otherwise>
    </c:choose>
</c:if>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập – Sun Coffee</title>

    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=Nunito:wght@400;600;700;800&display=swap"
          rel="stylesheet">
    <!-- CSS của staff (chứa style trang login) -->
    <link rel="stylesheet" href="assets/css/staff-style.css">
</head>
<body class="login-page">

<!-- ═══ CARD ĐĂNG NHẬP ══════════════════════════════ -->
<div class="login-card">

    <!-- Header card: Logo + tên -->
    <div class="login-card-header">
        <div class="login-logo">☕</div>
        <div class="login-brand">Sun Coffee</div>
        <div class="login-subtitle">Hệ thống quản lý cửa hàng</div>
    </div>

    <!-- Body: Form đăng nhập -->
    <div class="login-card-body">

        <%-- Hiện thông báo lỗi nếu A1 (LoginServlet) trả về --%>
        <c:if test="${not empty error}">
            <div class="login-error">
                ❌ ${error}
            </div>
        </c:if>

        <%-- Hiện thông báo thành công (VD: đăng xuất thành công) --%>
        <c:if test="${not empty message}">
            <div style="
                background:rgba(25,135,84,.1); color:#146c43;
                border:1px solid rgba(25,135,84,.25);
                border-radius:8px; padding:.7rem .9rem;
                font-size:.82rem; margin-bottom:1rem;
                display:flex; align-items:center; gap:.5rem;
            ">
                ✅ ${message}
            </div>
        </c:if>

        <%-- Form POST tới LoginServlet (A1) --%>
        <form class="login-form" action="LoginServlet" method="post"
              onsubmit="return validateLoginForm()">

            <!-- Tên đăng nhập -->
            <div class="form-group">
                <label class="form-label" for="username">
                    Tên đăng nhập <span style="color:#dc3545">*</span>
                </label>
                <%-- Giữ lại giá trị nhập nếu lỗi đăng nhập (A1 trả về) --%>
                <input type="text"
                       id="username"
                       name="username"
                       class="form-control"
                       placeholder="Nhập tên đăng nhập"
                       value="${param.username}"
                       autocomplete="username"
                       required>
                <span class="invalid-feedback" id="err-username"></span>
            </div>

            <!-- Mật khẩu -->
            <div class="form-group">
                <label class="form-label" for="password">
                    Mật khẩu <span style="color:#dc3545">*</span>
                </label>
                <div class="password-wrapper">
                    <input type="password"
                           id="password"
                           name="password"
                           class="form-control"
                           placeholder="Nhập mật khẩu"
                           autocomplete="current-password"
                           required>
                    <!-- Nút hiện/ẩn mật khẩu -->
                    <button type="button" class="btn-toggle-pass"
                            onclick="togglePassword()"
                            title="Hiện/Ẩn mật khẩu">👁️</button>
                </div>
                <span class="invalid-feedback" id="err-password"></span>
            </div>

            <!-- Nút đăng nhập -->
            <button type="submit" class="login-btn">
                🔑 Đăng nhập
            </button>

        </form>

        <!-- Ghi chú tài khoản demo (XÓA khi deploy thật) -->
        <div style="
            margin-top:1.25rem; padding:.75rem;
            background:#fff8ee; border-radius:8px;
            border:1px solid #e8d5c4;
            font-size:.75rem; color:#7A5C45;
        ">
            <strong>💡 Tài khoản demo:</strong><br>
            Admin: <code>admin</code> / <code>admin123</code><br>
            Nhân viên: <code>staff</code> / <code>staff123</code>
        </div>
    </div>
</div>

<!-- JS validate form đăng nhập (frontend check trước khi gửi backend) -->
<script>
    /**
     * Validate form trước khi submit về LoginServlet
     * @returns {boolean} false nếu có lỗi → dừng submit
     */
    function validateLoginForm() {
        let valid = true;

        const username = document.getElementById('username');
        const password = document.getElementById('password');
        const errU = document.getElementById('err-username');
        const errP = document.getElementById('err-password');

        // Reset lỗi cũ
        username.classList.remove('is-invalid');
        password.classList.remove('is-invalid');
        errU.textContent = '';
        errP.textContent = '';

        // Kiểm tra tên đăng nhập
        if (!username.value.trim()) {
            username.classList.add('is-invalid');
            errU.textContent = 'Vui lòng nhập tên đăng nhập';
            username.style.display = 'block';
            errU.style.display = 'block';
            valid = false;
        }

        // Kiểm tra mật khẩu
        if (!password.value) {
            password.classList.add('is-invalid');
            errP.textContent = 'Vui lòng nhập mật khẩu';
            errP.style.display = 'block';
            valid = false;
        }

        return valid;
    }

    /** Bật/tắt hiện mật khẩu */
    function togglePassword() {
        const input = document.getElementById('password');
        const btn   = document.querySelector('.btn-toggle-pass');
        if (input.type === 'password') {
            input.type = 'text';
            btn.textContent = '🙈';
        } else {
            input.type = 'password';
            btn.textContent = '👁️';
        }
    }
</script>
</body>
</html>
