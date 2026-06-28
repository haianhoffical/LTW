/**
 * ================================================
 *  staff-script.js – [A2] JavaScript POS / Bán hàng
 *  Sun Coffee Management System
 *  Phụ trách: A2
 * ================================================
 *
 *  Chức năng chính:
 *  1. Quản lý giỏ hàng (thêm, xóa, cập nhật SL)
 *  2. Tính tổng tiền, giảm giá
 *  3. Tìm kiếm & lọc theo danh mục
 *  4. Submit đơn hàng lên backend (OrderServlet)
 *  5. Toast notification
 * ================================================
 */

/* ── GIỎ HÀNG – STATE ─────────────────────────────
 * cart: mảng chứa các món đã chọn
 * Cấu trúc mỗi item: { productId, name, price, qty }
 * ─────────────────────────────────────────────── */
let cart = [];
let currentCategory = 'all';   // Danh mục đang lọc
let searchKeyword   = '';       // Từ khóa đang tìm

/* ── THÊM MÓN VÀO GIỎ HÀNG ─────────────────────── */
/**
 * Gọi khi click vào thẻ món ăn trong menu
 * @param {number} productId  - ID sản phẩm (lấy từ data-id trên card)
 * @param {string} name       - Tên sản phẩm
 * @param {number} price      - Giá (VNĐ)
 */
function addToCart(productId, name, price) {
    // Kiểm tra xem món đã có trong giỏ chưa
    const existingItem = cart.find(item => item.productId === productId);

    if (existingItem) {
        // Nếu đã có → tăng số lượng thêm 1
        existingItem.qty += 1;
    } else {
        // Nếu chưa có → thêm mới vào giỏ
        cart.push({ productId, name, price, qty: 1 });
    }

    // Cập nhật lại giao diện giỏ hàng
    renderCart();
    showToast(`Đã thêm: ${name}`, 'toast-added');
}

/* ── CẬP NHẬT SỐ LƯỢNG MÓN ─────────────────────── */
/**
 * Gọi khi nhấn nút + hoặc – trên từng dòng trong giỏ
 * @param {number} productId - ID sản phẩm cần cập nhật
 * @param {number} delta     - Thay đổi: +1 hoặc -1
 */
function updateQty(productId, delta) {
    const item = cart.find(i => i.productId === productId);
    if (!item) return;

    item.qty += delta;

    if (item.qty <= 0) {
        // Nếu số lượng về 0 → xóa khỏi giỏ
        removeFromCart(productId);
    } else {
        renderCart();   // Cập nhật giao diện
    }
}

/* ── XÓA MÓN KHỎI GIỎ HÀNG ─────────────────────── */
/**
 * @param {number} productId - ID sản phẩm cần xóa
 */
function removeFromCart(productId) {
    cart = cart.filter(item => item.productId !== productId);
    renderCart();
    showToast('Đã xóa khỏi đơn hàng', 'toast-error');
}

/* ── XÓA TOÀN BỘ GIỎ HÀNG ──────────────────────── */
function clearCart() {
    if (cart.length === 0) return;
    if (!confirm('Bạn có chắc muốn xóa toàn bộ đơn hàng?')) return;
    cart = [];
    renderCart();
}

/* ── TÍNH TỔNG TIỀN ─────────────────────────────── */
/**
 * Tính tổng tiền chưa giảm giá
 * @returns {number} Tổng tiền (VNĐ)
 */
function getSubtotal() {
    return cart.reduce((sum, item) => sum + item.price * item.qty, 0);
}

/**
 * Tính tổng sau giảm giá
 * Đọc giá trị từ ô input #discount-amount
 * @returns {number} Tổng tiền cuối cùng
 */
function getTotal() {
    const subtotal = getSubtotal();
    const discount = parseFloat(document.getElementById('discount-amount')?.value) || 0;
    return Math.max(0, subtotal - discount);   // Không âm
}

/* ── RENDER GIỎ HÀNG ──────────────────────────────
 * Vẽ lại toàn bộ giao diện giỏ hàng mỗi khi cart thay đổi
 * ─────────────────────────────────────────────── */
function renderCart() {
    const cartContainer = document.getElementById('cart-items');
    const emptyMsg      = document.getElementById('cart-empty');
    const payBtn        = document.getElementById('pay-btn');
    const subtotalEl    = document.getElementById('subtotal');
    const totalEl       = document.getElementById('grand-total');
    const itemCountEl   = document.getElementById('cart-item-count');

    if (!cartContainer) return;   // Chỉ chạy ở trang POS

    if (cart.length === 0) {
        // Giỏ trống → ẩn danh sách, hiện thông báo trống
        cartContainer.innerHTML = '';
        if (emptyMsg)   emptyMsg.classList.remove('hidden');
        if (payBtn)     payBtn.disabled = true;
    } else {
        // Có món → ẩn thông báo trống, vẽ danh sách
        if (emptyMsg) emptyMsg.classList.add('hidden');
        if (payBtn)   payBtn.disabled = false;

        cartContainer.innerHTML = cart.map(item => `
            <div class="cart-item" data-product-id="${item.productId}">
                <div>
                    <div class="cart-item-name">${item.name}</div>
                    <!-- Giá đơn vị × số lượng -->
                    <div class="cart-item-price">
                        ${formatVND(item.price)} × ${item.qty}
                        = <strong style="color:var(--coffee)">${formatVND(item.price * item.qty)}</strong>
                    </div>
                </div>
                <div class="qty-controls">
                    <!-- Nút giảm SL -->
                    <button class="qty-btn" onclick="updateQty(${item.productId}, -1)"
                            title="Giảm số lượng">−</button>
                    <span class="qty-number">${item.qty}</span>
                    <!-- Nút tăng SL -->
                    <button class="qty-btn" onclick="updateQty(${item.productId}, +1)"
                            title="Tăng số lượng">+</button>
                    <!-- Nút xóa hẳn khỏi giỏ -->
                    <button class="qty-btn remove" onclick="removeFromCart(${item.productId})"
                            title="Xóa khỏi đơn">✕</button>
                </div>
            </div>
        `).join('');
    }

    // Cập nhật phần tổng tiền
    if (itemCountEl) itemCountEl.textContent = cart.reduce((s, i) => s + i.qty, 0);
    if (subtotalEl)  subtotalEl.textContent   = formatVND(getSubtotal());
    if (totalEl)     totalEl.textContent      = formatVND(getTotal());
}

/* ── SUBMIT ĐƠN HÀNG LÊN BACKEND ────────────────── */
/**
 * Khi nhân viên nhấn nút "Thanh toán"
 * Gửi POST request đến OrderServlet
 */
function submitOrder() {
    if (cart.length === 0) {
        alert('Giỏ hàng đang trống!');
        return;
    }

    const tableId  = document.getElementById('table-select')?.value || 1;
    const discount = parseFloat(document.getElementById('discount-amount')?.value) || 0;
    const note     = document.getElementById('order-note')?.value || '';
    const total    = getTotal();

    // Chuẩn bị dữ liệu gửi lên backend (A1 xử lý OrderServlet)
    const orderData = {
        tableId:  tableId,
        discount: discount,
        total:    total,
        note:     note,
        // Danh sách chi tiết đơn hàng
        items: cart.map(item => ({
            productId: item.productId,
            quantity:  item.qty,
            unitPrice: item.price,
            subtotal:  item.price * item.qty
        }))
    };

    // Gửi dữ liệu bằng hidden form về OrderServlet
    submitOrderForm(orderData);
}

/**
 * Tạo hidden form và submit lên OrderServlet
 * Dùng cách này thay AJAX để tương thích với JSP/Servlet
 * @param {Object} orderData - Dữ liệu đơn hàng
 */
function submitOrderForm(orderData) {
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '../OrderServlet';   // Đường dẫn tới backend A1

    // Thêm các field ẩn vào form
    addHiddenField(form, 'action',   'createOrder');
    addHiddenField(form, 'tableId',  orderData.tableId);
    addHiddenField(form, 'discount', orderData.discount);
    addHiddenField(form, 'total',    orderData.total);
    addHiddenField(form, 'note',     orderData.note);
    // Gửi danh sách món dưới dạng JSON (A1 parse lại)
    addHiddenField(form, 'itemsJson', JSON.stringify(orderData.items));

    document.body.appendChild(form);
    form.submit();
}

/**
 * Helper tạo input hidden cho form
 * @param {HTMLFormElement} form
 * @param {string} name
 * @param {*} value
 */
function addHiddenField(form, name, value) {
    const input = document.createElement('input');
    input.type  = 'hidden';
    input.name  = name;
    input.value = value;
    form.appendChild(input);
}

/* ── TÌM KIẾM MÓN ───────────────────────────────── */
/**
 * Gọi từ sự kiện oninput trên thanh tìm kiếm
 * Lọc các card theo tên sản phẩm (không phân biệt hoa thường)
 * @param {string} keyword - Từ khóa người dùng nhập
 */
function searchMenu(keyword) {
    searchKeyword = keyword.toLowerCase().trim();
    filterMenuItems();
}

/* ── LỌC THEO DANH MỤC ──────────────────────────── */
/**
 * Gọi khi click nút danh mục (Tất cả, Nóng, Đá, Đặc biệt)
 * @param {string} categoryId - 'all' hoặc ID danh mục từ JSTL
 * @param {HTMLElement} btn   - Nút đang được click
 */
function filterByCategory(categoryId, btn) {
    currentCategory = categoryId;

    // Bỏ active tất cả nút, đặt active cho nút được click
    document.querySelectorAll('.category-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');

    filterMenuItems();
}

/**
 * Lọc kết hợp cả danh mục và từ khóa tìm kiếm
 * Ẩn/hiện các card sản phẩm
 */
function filterMenuItems() {
    const cards = document.querySelectorAll('.menu-item-wrapper');

    cards.forEach(card => {
        const name     = card.dataset.name?.toLowerCase()    || '';
        const category = card.dataset.category || 'all';

        const matchCat    = currentCategory === 'all' || category === currentCategory;
        const matchSearch = !searchKeyword || name.includes(searchKeyword);

        // Hiện nếu thỏa cả 2 điều kiện
        card.style.display = (matchCat && matchSearch) ? '' : 'none';
    });
}

/* ── FORMAT TIỀN VNĐ ─────────────────────────────── */
/**
 * Định dạng số sang tiền Việt Nam
 * VD: 35000 → "35.000 ₫"
 * @param {number} amount
 * @returns {string}
 */
function formatVND(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency', currency: 'VND'
    }).format(amount);
}

/* ── TOAST NOTIFICATION ──────────────────────────── */
/**
 * Hiển thị thông báo nổi góc dưới phải
 * @param {string} message - Nội dung thông báo
 * @param {string} type    - Class CSS: 'toast-added' | 'toast-error'
 */
function showToast(message, type = 'toast-added') {
    // Xóa toast cũ nếu còn hiển thị
    document.querySelectorAll('.toast').forEach(t => t.remove());

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `<span>${type === 'toast-added' ? '✅' : '❌'}</span> ${message}`;
    document.body.appendChild(toast);

    // Hiện lên với animation
    requestAnimationFrame(() => toast.classList.add('show'));

    // Tự ẩn sau 2.5 giây
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 400);
    }, 2500);
}

/* ── CẬP NHẬT KHI ĐỔI GIẢM GIÁ ─────────────────── */
/**
 * Gọi từ oninput trên ô giảm giá
 * Tính lại tổng tiền
 */
function onDiscountChange() {
    const totalEl = document.getElementById('grand-total');
    if (totalEl) totalEl.textContent = formatVND(getTotal());
}

/* ── KHỞI TẠO KHI TRANG LOAD ────────────────────── */
document.addEventListener('DOMContentLoaded', function () {
    console.log('[staff-script.js] POS trang đã sẵn sàng');

    // Nếu backend có truyền message thành công/lỗi qua session
    // Sẽ tự hiển thị toast sau khi submit đơn
    const successMsg = document.getElementById('success-message');
    const errorMsg   = document.getElementById('error-message');
    if (successMsg && successMsg.dataset.msg) {
        showToast(successMsg.dataset.msg, 'toast-added');
    }
    if (errorMsg && errorMsg.dataset.msg) {
        showToast(errorMsg.dataset.msg, 'toast-error');
    }

    // Khởi tạo hiển thị giỏ hàng rỗng
    renderCart();
});
