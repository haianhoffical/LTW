package com.cafe.util;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * FormatUtil.java – [CHUNG] Tiện ích format tiền & ngày tháng
 * Dùng khi cần hiển thị dữ liệu trong JSP hoặc Servlet.
 */
public class FormatUtil {

    /** Format số sang tiền VNĐ: 35000 → "35.000 ₫" */
    public static String formatVND(double amount) {
        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return fmt.format(amount);
    }

    /** Format ngày giờ: "2024-01-15T10:30:00" → "15/01/2024 10:30" */
    public static String formatDateTime(LocalDateTime dt) {
        if (dt == null) return "";
        return dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /** Format chỉ ngày: → "15/01/2024" */
    public static String formatDate(LocalDateTime dt) {
        if (dt == null) return "";
        return dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /** Rút gọn chuỗi nếu quá dài: "Cà phê sữa đá thơm ngon..." → "Cà phê sữa đá th..." */
    public static String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "...";
    }
}
