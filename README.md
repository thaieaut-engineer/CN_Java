Thành viên:
Quàng Duy Thái (Leader)
Trương Hoài Sơn (Member)
Trần Long Vũ (secretary)
Lê Nguyễn Nam Anh (Member)

1. Yêu cầu & Tính năng hệ thống
Quản lý thông tin
Khách hàng & Thú cưng: Quản lý thông tin chủ nuôi, danh sách thú cưng (loài, giống, tuổi, tiền sử bệnh).

Chi nhánh phòng khám: Quản lý danh sách chi nhánh, thông tin bác sĩ và nhân viên theo chi nhánh.

Dịch vụ & Thuốc: Quản lý danh mục khám chữa bệnh, dịch vụ spa/chăm sóc, bảng giá, danh mục thuốc và vật tư y tế.

Nghiệp vụ chính
Lịch hẹn & Tiếp nhận: Đặt lịch khám/spa, phân công bác sĩ, tiếp nhận bệnh nhân.

Khám bệnh & Đơn thuốc: Ghi nhận chẩn đoán, kê đơn thuốc, chỉ định dịch vụ chăm sóc.

Hóa đơn & Thanh toán: Tính tiền dịch vụ, thuốc và xuất hóa đơn.

Thống kê - Báo cáo: Doanh thu theo chi nhánh, thống kê số lượng dịch vụ và lượt khám.

2. Kiến trúc dự án (MVC Pattern)

src/
├── config/
│   └── DatabaseConnection.java    # Kết nối MySQL bằng JDBC
├── model/
│   ├── Customer.java
│   ├── Pet.java
│   ├── Employee.java
│   └── MedicalRecord.java
├── dao/                           # Data Access Object (Thao tác với DB)
│   ├── CustomerDAO.java
│   ├── PetDAO.java
│   └── MedicalRecordDAO.java
├── view/                          # Giao diện Java Swing
│   ├── LoginFrame.java
│   ├── MainFrame.java
│   ├── PetManagementPanel.java
│   └── MedicalRecordPanel.java
└── main/
    └── Main.java                  # Điểm khởi chạy ứng dụng

3. Thư viện & Công cụ cần thiết
JDK: Java SE 11 trở lên.

Thư viện kết nối MySQL: mysql-connector-j (tải qua Maven hoặc file .jar).

Giao diện FlatLaf (Tùy chọn): Giúp giao diện Swing hiện đại và đẹp mắt hơn.


--Mẫu kiến trúc Phân tầng (3-Tier / N-Tier Architecture)
Hệ thống được tổ chức theo 4 tầng chính để bảo đảm tính đóng gói, dễ bảo trì và dễ mở rộng:

+-----------------------------------------------------------------------+
|                         PRESENTATION LAYER                            |
|  - Swing UI (Frames, Panels, Dialogs)                                 |
|  - Custom Components & Event Listeners                                |
+-----------------------------------------------------------------------+
                                  |
                                  v
+-----------------------------------------------------------------------+
|                           BUSINESS LAYER                              |
|  - Services / Controllers (Xử lý logic nghiệp vụ, Validation)         |
|  - Session Manager & Authentication (Phân quyền người dùng)            |
+-----------------------------------------------------------------------+
                                  |
                                  v
+-----------------------------------------------------------------------+
|                         DATA ACCESS LAYER (DAL)                       |
|  - DAOs & Interfaces (CRUD, SQL Queries)                              |
|  - Transaction Management & Connection Pool                           |
+-----------------------------------------------------------------------+
                                  |
                                  v
+-----------------------------------------------------------------------+
|                           DATABASE LAYER                              |
|  - MySQL Database (Tables, Foreign Keys, Indexes, Views, Triggers)    |
+-----------------------------------------------------------------------+


--Cấu trúc thư mục dự án (Directory Structure)
Plaintext
src/
├── app/
│   └── MainApp.java                    # Điểm khởi chạy (Main Class & LookAndFeel)
│
├── config/                             # Cấu hình hệ thống
│   ├── DatabaseConnection.java         # Quản lý kết nối JDBC / HikariCP
│   └── AppConfig.java                  # Đọc cấu hình từ file properties
│
├── entity/ / model/                    # Các DTO / Entity đại diện cho bảng DB
│   ├── Branch.java
│   ├── Customer.java
│   ├── Pet.java
│   ├── Employee.java
│   ├── Service.java
│   ├── MedicalRecord.java
│   ├── MedicalDetail.java              # Chi tiết dịch vụ/thuốc sử dụng
│   └── Invoice.java
│
├── dao/                                # Tầng truy xuất dữ liệu
│   ├── BaseDAO.java                    # Interface CRUD chung (Generic)
│   ├── BranchDAO.java
│   ├── CustomerDAO.java
│   ├── PetDAO.java
│   ├── EmployeeDAO.java
│   ├── ServiceDAO.java
│   ├── MedicalRecordDAO.java
│   └── InvoiceDAO.java
│
├── service/                            # Tầng xử lý nghiệp vụ (Business Logic)
│   ├── AuthService.java                # Đăng nhập, phân quyền (Admin, Bác sĩ, Lễ tân)
│   ├── CustomerService.java             # Quản lý khách hàng & lịch sử thú cưng
│   ├── MedicalService.java              # Khám bệnh, kê đơn, tính tổng tiền
│   ├── BillingService.java              # Xuất hóa đơn & thanh toán
│   └── ReportService.java               # Thống kê doanh thu theo chi nhánh
│
├── ui/ / view/                         # Tầng giao diện Swing
│   ├── common/                         # Thành phần chung
│   │   ├── BaseFrame.java
│   │   ├── SidebarPanel.java
│   │   └── HeaderPanel.java
│   ├── auth/                           # Màn hình đăng nhập
│   │   └── LoginDialog.java
│   ├── modules/                        # Màn hình các chức năng
│   │   ├── CustomerPanel.java
│   │   ├── PetPanel.java
│   │   ├── MedicalRecordPanel.java
│   │   ├── InvoicePanel.java
│   │   └── ReportPanel.java
│   └── components/                     # Control tự định nghĩa (Custom Button, Table Model)
│       └── CustomTable.java
│
└── util/                               # Các tiện ích phụ trợ
    ├── DateUtil.java                   # Format ngày tháng
    ├── PasswordUtil.java               # Mã hóa mật khẩu (BCrypt)
    ├── PDFExporter.java                # Xuất hóa đơn ra file PDF
    └── ValidationUtil.java             # Kiểm tra dữ liệu vào (SĐT, Email, Số âm)


--Chi tiết chức năng từng tầng
A. Tầng dữ liệu (Entity / Model Layer)
Đại diện cho các thực thể trong cơ sở dữ liệu.

Chứa các thuộc tính (fields), hàm khởi tạo (constructors), Getter/Setter.

B. Tầng truy xuất (Data Access Layer - DAO)
Nhiệm vụ: Thực hiện trực tiếp các câu lệnh SQL (SELECT, INSERT, UPDATE, DELETE) thông qua JDBC PreparedStatement.

Sử dụng Generic DAO Interface để tái sử dụng mã nguồn:

C. Tầng nghiệp vụ (Business Service Layer)
Nhiệm vụ: Đứng giữa UI và DAO để xử lý các quy tắc nghiệp vụ phức tạp.
Ví dụ nghiệp vụ:
Tự động tính tiền hóa đơn: $Tổng = \sum(Giá\ dịch\ vụ) + \sum(Số\ lượng \times Đơn\ giá\ thuốc) - Giảm\ giá$.
Xử lý Transaction: Khi tạo hóa đơn, việc trừ kho thuốc và cập nhật trạng thái lịch khám phải thành công đồng thời (Rollback nếu có lỗi).
Phân quyền người dùng: Nhân viên chỉ xem/sửa chi nhánh của mình; Quản lý/Admin xem được tất cả chi nhánh.

D. Tầng giao diện (Presentation Layer - Swing UI)
Sử dụng CardLayout hoặc JTabbedPane để chuyển đổi mượt mà giữa các màn hình quản lý.
Kết hợp với thư viện FlatLaf để hiện đại hóa giao diện chuẩn phẳng.
Sử dụng SwingWorker đối với các tác vụ nặng (tải danh sách lớn, xuất file PDF) để tránh tình trạng đơ/treo giao diện (UI Freeze).