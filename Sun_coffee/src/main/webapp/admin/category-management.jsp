<%-- admin/category-management.jsp – [B2] Quản lý danh mục --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty sessionScope.user || sessionScope.user.role != 'admin'}"><c:redirect url="../login.jsp"/></c:if>
<c:set var="activePage" value="category"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1.0">
    <title>Quản lý danh mục – Sun Coffee</title>
    <c:set var="pageTitle" value="Quản lý danh mục"/>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=Nunito:wght@400;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="../assets/css/admin-style.css">
</head>
<body>
<div class="app-wrapper">
<%@ include file="../components/header.jsp" %>
<%@ include file="../components/sidebar-admin.jsp" %>

<main class="main-content">
    <div class="page-header d-flex justify-between align-center flex-wrap gap-1">
        <div>
            <h2>📂 Quản lý danh mục</h2>
            <p>Thêm, sửa, xóa danh mục sản phẩm</p>
        </div>
        <button class="btn btn-primary" onclick="openModal('modal-add-cat')">➕ Thêm danh mục</button>
    </div>

    <!-- Tìm kiếm -->
    <div class="filter-bar">
        <div class="search-box">
            <span class="search-icon">🔍</span>
            <input class="search-input" placeholder="Tìm tên danh mục..."
                   oninput="searchTable(this.value,'cat-table',0)">
        </div>
    </div>

    <!-- Bảng danh mục -->
    <div class="card">
        <div class="card-header">
            <h3>Danh sách danh mục (${fn:length(categories)} danh mục)</h3>
        </div>
        <div class="table-wrapper">
            <table class="data-table" id="cat-table">
                <thead>
                    <tr><th>#</th><th>Tên danh mục</th><th>Mô tả</th><th>Số sản phẩm</th><th>Thao tác</th></tr>
                </thead>
                <tbody>
                <c:forEach var="cat" items="${categories}" varStatus="st">
                    <tr>
                        <td class="text-muted">${st.count}</td>
                        <td class="fw-bold">
                            <span style="display:inline-flex;align-items:center;gap:.5rem">
                                <span style="width:32px;height:32px;border-radius:50%;background:linear-gradient(135deg,var(--coffee),var(--coffee-mid));
                                      display:inline-flex;align-items:center;justify-content:center;color:#fff;font-size:.85rem;font-weight:800">
                                    ${fn:substring(cat.categoryName,0,1)}
                                </span>
                                ${cat.categoryName}
                            </span>
                        </td>
                        <td style="color:var(--muted)">${cat.description}</td>
                        <td>
                            <%-- Số sản phẩm trong danh mục do B1 StatisticServlet / ProductDAO đếm --%>
                            <span class="badge badge-coffee">${cat.productCount} món</span>
                        </td>
                        <td>
                            <div class="d-flex gap-1">
                                <button class="btn btn-ghost btn-sm"
                                    onclick="fillEditCategoryForm({id:${cat.categoryId},name:'${cat.categoryName}',description:'${cat.description}'})">
                                    ✏️ Sửa
                                </button>
                                <form id="del-cat-${cat.categoryId}" action="../CategoryServlet" method="post" style="display:none">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="categoryId" value="${cat.categoryId}">
                                </form>
                                <button class="btn btn-danger btn-sm"
                                    onclick="confirmDelete('del-cat-${cat.categoryId}','${cat.categoryName}')">
                                    🗑️ Xóa
                                </button>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty categories}">
                    <tr><td colspan="5" style="text-align:center;padding:2rem;color:var(--muted)">Chưa có danh mục nào.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</main>
<%@ include file="../components/footer.jsp" %>
</div>

<!-- Modal Thêm danh mục -->
<div class="modal-overlay" id="modal-add-cat" onclick="closeOnBackdrop(event,'modal-add-cat')">
    <div class="modal-box">
        <div class="modal-header">
            <h3>➕ Thêm danh mục</h3>
            <button class="modal-close" onclick="closeModal('modal-add-cat')">✕</button>
        </div>
        <div class="modal-body">
            <form id="form-add-cat" action="../CategoryServlet" method="post"
                  onsubmit="return validateCategoryForm('form-add-cat')">
                <input type="hidden" name="action" value="add">
                <div class="form-group">
                    <label class="form-label">Tên danh mục <span class="required">*</span></label>
                    <input type="text" name="categoryName" class="form-control" placeholder="VD: Coffee Đặc Biệt">
                    <div class="invalid-feedback"></div>
                </div>
                <div class="form-group">
                    <label class="form-label">Mô tả</label>
                    <textarea name="description" class="form-control" rows="2" placeholder="Mô tả danh mục..."></textarea>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-ghost" onclick="closeModal('modal-add-cat')">Hủy</button>
                    <button type="submit" class="btn btn-primary">💾 Lưu</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal Sửa danh mục -->
<div class="modal-overlay" id="modal-edit-category" onclick="closeOnBackdrop(event,'modal-edit-category')">
    <div class="modal-box">
        <div class="modal-header">
            <h3>✏️ Sửa danh mục</h3>
            <button class="modal-close" onclick="closeModal('modal-edit-category')">✕</button>
        </div>
        <div class="modal-body">
            <form id="form-edit-category" action="../CategoryServlet" method="post"
                  onsubmit="return validateCategoryForm('form-edit-category')">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="categoryId">
                <div class="form-group">
                    <label class="form-label">Tên danh mục <span class="required">*</span></label>
                    <input type="text" name="categoryName" class="form-control">
                    <div class="invalid-feedback"></div>
                </div>
                <div class="form-group">
                    <label class="form-label">Mô tả</label>
                    <textarea name="description" class="form-control" rows="2"></textarea>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-ghost" onclick="closeModal('modal-edit-category')">Hủy</button>
                    <button type="submit" class="btn btn-primary">💾 Lưu thay đổi</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="../assets/js/admin-script.js"></script>
</body>
</html>
