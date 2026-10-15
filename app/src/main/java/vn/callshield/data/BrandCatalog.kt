package vn.callshield.data

data class BrandCandidate(
    val brand: String,
    val category: String,
    val phone: String,
    val note: String,
    val source: String = "Cần xác minh trên website chính thức"
)
object BrandCatalog {
    val candidates = listOf(
        // 1. VIỄN THÔNG - INTERNET
        BrandCandidate(
            "Viettel", "Viễn thông", "198",
            "Tổng đài dịch vụ; không mặc định là spam"
        ),
        BrandCandidate(
            "Viettel", "Viễn thông", "18008098",
            "Tư vấn dịch vụ; cần xác minh phạm vi hỗ trợ"
        ),
        BrandCandidate(
            "VNPT", "Viễn thông", "18001091",
            "Số tham khảo; xác minh đầu số hiện hành"
        ),
        BrandCandidate(
            "MobiFone", "Viễn thông", "18001090",
            "Tổng đài dịch vụ"
        ),
        BrandCandidate(
            "FPT Telecom", "Internet / truyền hình", "19006600",
            "Tư vấn và hỗ trợ dịch vụ"
        ),
        // 2. NGÂN HÀNG
        BrandCandidate(
            "VPBank", "Ngân hàng", "02466716600",
            "Số điện thoại cần đối chiếu với trang liên hệ VPBank",
            "https://cskh.vpbank.com.vn/contact"
        ),
        BrandCandidate(
            "VPBank", "Ngân hàng", "1900545415",
            "Tổng đài tham khảo; xác minh số hiện hành"
        ),
        BrandCandidate(
            "Vietcombank", "Ngân hàng", "1900545413",
            "Tổng đài tham khảo; cân nhắc trước khi chặn"
        ),
        BrandCandidate(
            "BIDV", "Ngân hàng", "19009247",
            "Tổng đài tham khảo; cân nhắc trước khi chặn"
        ),
        BrandCandidate(
            "Techcombank", "Ngân hàng", "1800588822",
            "Tổng đài tham khảo; cân nhắc trước khi chặn"
        ),
        BrandCandidate(
            "ACB", "Ngân hàng", "1900545486",
            "Tổng đài tham khảo; cần xác minh"
        ),
        // 3. VAY TIÊU DÙNG - TÀI CHÍNH - TÍN DỤNG
        BrandCandidate(
            "FE CREDIT", "Vay tiêu dùng", "19006535",
            "Tổng đài khoản vay",
            "https://www.fecredit.com.vn/cau-hoi-thuong-gap/"
        ),
        BrandCandidate(
            "FE CREDIT", "Thẻ tín dụng", "19006939",
            "Tổng đài thẻ tín dụng",
            "https://www.fecredit.com.vn/cau-hoi-thuong-gap/"
        ),
        BrandCandidate(
            "FE CREDIT", "Vay tiêu dùng", "1900234588",
            "Đầu số được công bố trước đây; cần kiểm tra lại",
            "https://www.fecredit.com.vn/tin-tuc-khuyen-mai/tin-tuc/thong-bao-them-dau-so-cham-soc-khach-hang-tu-fe-credit/"
        ),
        BrandCandidate(
            "Home Credit", "Vay tiêu dùng", "1900633633",
            "Tổng đài sản phẩm vay và dịch vụ khác",
            "https://www.homecredit.vn/"
        ),
        BrandCandidate(
            "Home Credit", "Thẻ / PayLater", "1900633999",
            "Tổng đài thẻ và Home PayLater",
            "https://www.homecredit.vn/"
        ),
        BrandCandidate(
            "Übank", "Ngân hàng số", "1900633533",
            "Số hỗ trợ tham khảo; xác minh trước khi chặn",
            "https://www.fecredit.com.vn/cau-hoi-thuong-gap/"
        ),
        // 4. BẤT ĐỘNG SẢN - TƯ VẤN DỰ ÁN
        BrandCandidate(
            "Vinhomes", "Bất động sản", "1900232389",
            "Tổng đài chính thức; không đồng nghĩa spam",
            "https://vinhomes.vn/vi/contact"
        ),
        BrandCandidate(
            "Vinhomes", "Bất động sản", "02439749350",
            "Số điện thoại doanh nghiệp được công bố",
            "https://vinhomes.vn/vi/contact"
        ),
        BrandCandidate(
            "Novaland", "Bất động sản", "1900636666",
            "Tổng đài chăm sóc khách hàng",
            "https://www.novaland.com.vn/lien-he"
        ),
        BrandCandidate(
            "Novaland", "Bất động sản", "0906353838",
            "Số liên hệ doanh nghiệp; không tự động coi là số sale",
            "https://www.novaland.com.vn/lien-he"
        ),
        BrandCandidate(
            "Đất Xanh", "Bất động sản", "18001023",
            "Số tham khảo; cần xác minh"
        ),
        BrandCandidate(
            "Vinhomes / đại lý", "Bất động sản",
            "0921900000",
            "Số được công bố trên website đơn vị phân phối; không mặc định là số của Vinhomes",
            "https://vinhome.com.vn/lien-he/"
        ),
        // 5. THƯƠNG MẠI ĐIỆN TỬ - MUA SẮM
        BrandCandidate(
            "Shopee", "Thương mại điện tử", "19001221",
            "Số tham khảo cũ; cần xác minh trước khi chặn"
        ),
        BrandCandidate(
            "Lazada", "Thương mại điện tử", "19001007",
            "Số tham khảo cũ; cần xác minh trước khi chặn"
        ),
        BrandCandidate(
            "Tiki", "Thương mại điện tử", "19006035",
            "Số tham khảo; cần xác minh"
        ),
        BrandCandidate(
            "Thế Giới Di Động", "Bán lẻ / điện tử", "18001060",
            "Chăm sóc khách hàng; xác minh trạng thái hiện hành"
        ),
        BrandCandidate(
            "Điện Máy Xanh", "Bán lẻ / điện tử", "18001061",
            "Chăm sóc khách hàng; xác minh trạng thái hiện hành"
        ),
        BrandCandidate(
            "Bách Hóa Xanh", "Bán lẻ / tiêu dùng", "19001908",
            "Số tham khảo; cần xác minh"
        ),
        BrandCandidate(
            "FPT Shop", "Bán lẻ / điện tử", "18006601",
            "Tư vấn / hỗ trợ bán lẻ"
        ),
        BrandCandidate(
            "CellphoneS", "Bán lẻ / điện tử", "18002097",
            "Tư vấn / hỗ trợ; cần xác minh"
        ),
        BrandCandidate(
            "Pico", "Bán lẻ / điện tử", "19006619",
            "Số tham khảo; cần xác minh"
        ),
        BrandCandidate(
            "Nguyễn Kim", "Bán lẻ / điện tử", "18006800",
            "Số tham khảo; cần xác minh"
        ),
        // 6. GIAO HÀNG - GỌI XE - LOGISTICS
        BrandCandidate(
            "Grab", "Gọi xe / giao nhận", "02871098590",
            "Số tham khảo; cần xác minh theo dịch vụ"
        ),
        BrandCandidate(
            "Be", "Gọi xe / giao nhận", "1900232345",
            "Số tham khảo; cần xác minh"
        ),
        BrandCandidate(
            "Giao Hàng Tiết Kiệm", "Giao nhận", "19006092",
            "Số tham khảo; cần xác minh"
        ),
        BrandCandidate(
            "J&T Express", "Giao nhận", "19001088",
            "Số tham khảo; cần xác minh"
        ),
        BrandCandidate(
            "VNPost", "Bưu chính", "1900545481",
            "Số tham khảo; cần xác minh"
        ),
        // 7. MỸ PHẨM - LÀM ĐẸP
        BrandCandidate(
            "Hasaki", "Mỹ phẩm / làm đẹp", "18006324",
            "Số tham khảo; cần xác minh"
        ),
        BrandCandidate(
            "Guardian", "Mỹ phẩm / bán lẻ", "18006008",
            "Số tham khảo; cần xác minh"
        ),
        BrandCandidate(
            "Avon", "Mỹ phẩm", "18001700",
            "Số tham khảo; cần xác minh"
        ),
        BrandCandidate(
            "Thế Giới SkinFood", "Mỹ phẩm / làm đẹp", "18006010",
            "Số tham khảo; cần xác minh"
        )
    )
}