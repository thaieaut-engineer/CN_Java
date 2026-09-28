ĐỀ TÀI: XÂY DỰNG ỨNG DỤNG DESKTOP QUẢN LÝ CHUỖI PHÒNG KHÁM THÚ Y VÀ CHĂM SÓC VẬT NUÔI
THÀNH VIÊN NHÓM

STT

	

Họ và tên

	

Vai trò




1

	

Quàng Duy Thái

	

Nhóm trưởng (Leader)




2

	

Trương Hoài Sơn

	

Thành viên (Member)




3

	

Trần Long Vũ

	

Thư ký (Secretary)




4

	

Lê Nguyễn Nam Anh

	

Thành viên (Member)

1. TỔNG QUAN VÀ YÊU CẦU HỆ THỐNG
1.1. Giới thiệu đề tài

Hệ thống quản lý chuỗi phòng khám thú y và chăm sóc vật nuôi được xây dựng nhằm hỗ trợ các hoạt động quản lý, vận hành tại các chi nhánh phòng khám thú y. Hệ thống cho phép quản lý tập trung thông tin khách hàng, thú cưng, nhân viên, dịch vụ, thuốc, lịch hẹn, hồ sơ khám bệnh và hóa đơn thanh toán.

Ứng dụng được phát triển dưới dạng phần mềm desktop sử dụng Java Swing để xây dựng giao diện người dùng, JDBC để kết nối và thao tác với cơ sở dữ liệu, kết hợp với MySQL để lưu trữ và quản lý dữ liệu.

Hệ thống hướng đến việc nâng cao hiệu quả quản lý, giảm thiểu sai sót trong quá trình xử lý dữ liệu, hỗ trợ theo dõi lịch sử khám chữa bệnh của thú cưng và cung cấp các báo cáo thống kê phục vụ công tác quản lý tại từng chi nhánh.

1.2. Yêu cầu chức năng
1.2.1. Quản lý khách hàng và thú cưng

Quản lý thông tin khách hàng: họ tên, số điện thoại, địa chỉ và các thông tin liên hệ cần thiết.

Quản lý danh sách thú cưng thuộc từng khách hàng.

Lưu trữ thông tin thú cưng bao gồm tên, loài, giống, tuổi và các thông tin liên quan.

Theo dõi lịch sử khám bệnh, chẩn đoán, đơn thuốc và quá trình điều trị của từng thú cưng.

Hỗ trợ thêm, sửa, xóa và tìm kiếm thông tin khách hàng, thú cưng.

1.2.2. Quản lý chi nhánh và nhân viên

Quản lý thông tin các chi nhánh trong hệ thống.

Quản lý thông tin nhân viên làm việc tại từng chi nhánh.

Phân loại nhân viên theo chức vụ như quản trị viên, bác sĩ thú y và nhân viên lễ tân.

Quản lý thông tin tài khoản đăng nhập và quyền truy cập của từng nhóm người dùng.

Hỗ trợ tìm kiếm và tra cứu thông tin nhân viên theo chi nhánh.

1.2.3. Quản lý dịch vụ, thuốc và vật tư y tế

Quản lý danh mục dịch vụ khám chữa bệnh, tiêm phòng, spa và chăm sóc thú cưng.

Quản lý danh mục thuốc và vật tư y tế sử dụng trong quá trình khám chữa bệnh.

Quản lý giá dịch vụ, đơn giá thuốc và các thông tin liên quan.

Theo dõi số lượng thuốc, vật tư tồn kho và tình trạng sử dụng.

Hỗ trợ tìm kiếm, cập nhật và quản lý danh mục dịch vụ, thuốc.

1.2.4. Quản lý lịch hẹn và tiếp nhận

Tiếp nhận yêu cầu đặt lịch khám bệnh hoặc sử dụng dịch vụ chăm sóc thú cưng.

Quản lý thông tin lịch hẹn, thời gian khám, khách hàng và thú cưng.

Phân công bác sĩ phụ trách theo lịch hẹn.

Theo dõi trạng thái lịch hẹn như đã đặt, đã xác nhận, đang khám, hoàn thành hoặc đã hủy.

Hỗ trợ tra cứu lịch hẹn theo ngày, chi nhánh hoặc bác sĩ.

1.2.5. Quản lý khám bệnh và đơn thuốc

Lập phiếu khám bệnh cho từng thú cưng.

Ghi nhận thông tin khám, triệu chứng, chẩn đoán và hướng điều trị.

Lưu trữ lịch sử khám bệnh theo từng thú cưng.

Hỗ trợ bác sĩ kê đơn thuốc và chỉ định các dịch vụ cần thiết.

Theo dõi thông tin thuốc, số lượng sử dụng và hướng dẫn điều trị.

1.2.6. Quản lý hóa đơn và thanh toán

Tạo hóa đơn dựa trên các dịch vụ và thuốc được sử dụng trong quá trình khám chữa bệnh.

Tự động tính tổng chi phí của hóa đơn.

Hỗ trợ cập nhật trạng thái thanh toán.

Quản lý lịch sử hóa đơn theo khách hàng, thú cưng và chi nhánh.

Hỗ trợ in hoặc xuất hóa đơn thành tệp PDF.

Công thức tính tổng tiền hóa đơn:

Tổng ti
e
^
ˋ
n=
	​

Tổng ti
e
^
ˋ
n dịch vụ
+Tổng ti
e
^
ˋ
n thu
o
^
ˊ
c
−Giảm gi
a
ˊ
	​


Trong đó:

Tổng ti
e
^
ˋ
n dịch vụ
Tổng ti
e
^
ˋ
n thu
o
^
ˊ
c
	​

=∑Đơn gi
a
ˊ
 dịch vụ
=∑(S
o
^
ˊ
 lượng×Đơn gi
a
ˊ
)
	​

1.2.7. Thống kê và báo cáo

Thống kê doanh thu theo từng chi nhánh.

Thống kê doanh thu theo ngày, tháng và năm.

Thống kê số lượng lượt khám và lịch hẹn.

Thống kê số lượng dịch vụ đã sử dụng.

Hỗ trợ tổng hợp dữ liệu phục vụ công tác quản lý và đánh giá hoạt động kinh doanh.

1.3. Yêu cầu phi chức năng

Ngoài các yêu cầu về chức năng, hệ thống cần đáp ứng những yêu cầu phi chức năng sau:

Hiệu năng: Các thao tác tìm kiếm, truy vấn và cập nhật dữ liệu cần được thực hiện trong thời gian hợp lý.

Tính dễ sử dụng: Giao diện trực quan, bố cục rõ ràng, dễ thao tác đối với nhân viên phòng khám.

Tính ổn định: Hạn chế lỗi phát sinh trong quá trình sử dụng, đảm bảo dữ liệu được lưu trữ chính xác.

Tính bảo mật: Kiểm soát quyền truy cập theo vai trò người dùng; mật khẩu cần được mã hóa trước khi lưu vào cơ sở dữ liệu.

Tính toàn vẹn dữ liệu: Sử dụng khóa chính, khóa ngoại và các ràng buộc dữ liệu để đảm bảo tính nhất quán.

Khả năng mở rộng: Kiến trúc phần mềm cho phép bổ sung chức năng mới mà không ảnh hưởng lớn đến các thành phần hiện có.

Khả năng bảo trì: Mã nguồn được tổ chức theo từng tầng, giúp dễ dàng kiểm tra, sửa lỗi và nâng cấp.

2. KIẾN TRÚC HỆ THỐNG
2.1. Mô hình kiến trúc

Hệ thống được thiết kế theo mô hình kiến trúc phân tầng (N-Tier Architecture), kết hợp với mô hình MVC (Model – View – Controller) nhằm tách biệt giao diện, nghiệp vụ và thao tác dữ liệu.

Kiến trúc hệ thống gồm bốn tầng chính:

1. PRESENTATION LAYER

Java Swing – Frames, Panels, Dialogs

Hiển thị dữ liệu và tiếp nhận thao tác người dùng

2. BUSINESS LAYER

Service – Xử lý nghiệp vụ

Kiểm tra dữ liệu, phân quyền, tính toán và điều phối nghiệp vụ

3. DATA ACCESS LAYER

DAO – JDBC – PreparedStatement

Thực hiện truy vấn và cập nhật dữ liệu

4. DATABASE LAYER

MySQL Database

Lưu trữ bảng, quan hệ, chỉ mục và dữ liệu hệ thống

2.1.1. Tầng giao diện (Presentation Layer)

Tầng giao diện được xây dựng bằng Java Swing, chịu trách nhiệm hiển thị thông tin và tiếp nhận các thao tác từ người dùng.

Các thành phần chính bao gồm:

Các cửa sổ chính (JFrame).

Các bảng dữ liệu (JTable).

Các biểu mẫu nhập liệu.

Các hộp thoại thông báo và xác nhận.

Các thành phần điều hướng giữa những chức năng.

Giao diện có thể sử dụng CardLayout hoặc JTabbedPane để chuyển đổi giữa các màn hình. Thư viện FlatLaf có thể được tích hợp nhằm cải thiện giao diện và mang lại phong cách hiện đại hơn.

Đối với các tác vụ mất nhiều thời gian như tải dữ liệu lớn hoặc xuất báo cáo, hệ thống sử dụng SwingWorker để tránh làm giao diện bị treo.

2.1.2. Tầng nghiệp vụ (Business Layer)

Tầng nghiệp vụ chịu trách nhiệm xử lý các quy tắc hoạt động của hệ thống, nằm giữa tầng giao diện và tầng truy xuất dữ liệu.

Các nhiệm vụ chính:

Kiểm tra tính hợp lệ của dữ liệu đầu vào.

Xử lý đăng nhập và phân quyền người dùng.

Quản lý quy trình tiếp nhận và khám bệnh.

Tính toán chi phí dịch vụ, thuốc và hóa đơn.

Kiểm tra điều kiện thanh toán.

Điều phối các thao tác liên quan đến nhiều bảng dữ liệu.

Tổng hợp dữ liệu phục vụ thống kê và báo cáo.

Ví dụ, khi lập hóa đơn, tầng nghiệp vụ tiếp nhận thông tin dịch vụ và thuốc đã sử dụng, tính tổng tiền, kiểm tra dữ liệu và yêu cầu tầng DAO lưu thông tin vào cơ sở dữ liệu.

2.1.3. Tầng truy xuất dữ liệu (Data Access Layer)

Tầng truy xuất dữ liệu được xây dựng theo mô hình DAO (Data Access Object), sử dụng JDBC để giao tiếp với MySQL.

Các nhiệm vụ chính:

Thực hiện các câu lệnh SQL như SELECT, INSERT, UPDATE và DELETE.

Truy xuất dữ liệu từ các bảng trong cơ sở dữ liệu.

Chuyển đổi dữ liệu truy vấn thành các đối tượng Model/Entity.

Sử dụng PreparedStatement để truyền tham số và hạn chế nguy cơ SQL Injection.

Quản lý giao dịch khi cần thực hiện nhiều thao tác dữ liệu liên quan.

Việc tổ chức riêng các lớp DAO giúp giảm sự phụ thuộc giữa giao diện và cơ sở dữ liệu, đồng thời hỗ trợ tái sử dụng mã nguồn.

2.1.4. Tầng cơ sở dữ liệu (Database Layer)

Tầng cơ sở dữ liệu sử dụng MySQL để lưu trữ và quản lý dữ liệu của toàn hệ thống.

Cơ sở dữ liệu bao gồm các nhóm dữ liệu chính:

Thông tin chi nhánh và nhân viên.

Thông tin khách hàng và thú cưng.

Danh mục dịch vụ, thuốc và vật tư y tế.

Lịch hẹn và hồ sơ khám bệnh.

Chi tiết dịch vụ, thuốc được sử dụng.

Hóa đơn và thông tin thanh toán.

Các bảng được liên kết thông qua khóa chính và khóa ngoại nhằm đảm bảo tính toàn vẹn dữ liệu. Ngoài ra, có thể sử dụng chỉ mục, khung nhìn (View) và thủ tục lưu trữ (Stored Procedure) khi cần thiết.

2.2. Mô hình MVC

Mô hình MVC được áp dụng để phân chia chương trình thành ba thành phần chính:

Thành phần

	

Vai trò

	

Ví dụ




Model

	

Đại diện cho dữ liệu và thực thể trong hệ thống

	

Customer, Pet, Invoice




View

	

Hiển thị giao diện và dữ liệu cho người dùng

	

LoginFrame, CustomerPanel




Controller

	

Tiếp nhận sự kiện, điều phối xử lý giữa View và Model

	

Các lớp xử lý sự kiện hoặc Controller

Trong đó, tầng Service đảm nhiệm phần lớn logic nghiệp vụ, còn DAO chịu trách nhiệm truy xuất dữ liệu. Cách tổ chức này giúp chương trình có cấu trúc rõ ràng, dễ kiểm thử và bảo trì.

3. CẤU TRÚC THƯ MỤC DỰ ÁN

Dự án được tổ chức theo các package trong thư mục mã nguồn src, đảm bảo mỗi nhóm lớp đảm nhiệm một chức năng riêng biệt.

src/
├── app/
│   └── MainApp.java
│
├── config/
│   ├── DatabaseConnection.java
│   └── AppConfig.java
│
├── entity/
│   ├── Branch.java
│   ├── Customer.java
│   ├── Pet.java
│   ├── Employee.java
│   ├── Service.java
│   ├── Appointment.java
│   ├── MedicalRecord.java
│   ├── MedicalDetail.java
│   ├── Medicine.java
│   └── Invoice.java
│
├── dao/
│   ├── BaseDAO.java
│   ├── BranchDAO.java
│   ├── CustomerDAO.java
│   ├── PetDAO.java
│   ├── EmployeeDAO.java
│   ├── ServiceDAO.java
│   ├── AppointmentDAO.java
│   ├── MedicalRecordDAO.java
│   ├── MedicineDAO.java
│   └── InvoiceDAO.java
│
├── service/
│   ├── AuthService.java
│   ├── CustomerService.java
│   ├── PetService.java
│   ├── AppointmentService.java
│   ├── MedicalService.java
│   ├── BillingService.java
│   └── ReportService.java
│
├── ui/
│   ├── common/
│   │   ├── BaseFrame.java
│   │   ├── SidebarPanel.java
│   │   └── HeaderPanel.java
│   │
│   ├── auth/
│   │   └── LoginDialog.java
│   │
│   ├── modules/
│   │   ├── DashboardPanel.java
│   │   ├── BranchPanel.java
│   │   ├── CustomerPanel.java
│   │   ├── PetPanel.java
│   │   ├── EmployeePanel.java
│   │   ├── ServicePanel.java
│   │   ├── AppointmentPanel.java
│   │   ├── MedicalRecordPanel.java
│   │   ├── MedicinePanel.java
│   │   ├── InvoicePanel.java
│   │   └── ReportPanel.java
│   │
│   └── components/
│       └── CustomTable.java
│
└── util/
    ├── DateUtil.java
    ├── PasswordUtil.java
    ├── PDFExporter.java
    └── ValidationUtil.java
3.1. Mô tả các package

Package

	

Chức năng




app

	

Chứa lớp khởi chạy ứng dụng và thiết lập giao diện ban đầu.




config

	

Quản lý cấu hình hệ thống và kết nối cơ sở dữ liệu.




entity

	

Chứa các lớp đại diện cho thực thể dữ liệu.




dao

	

Thực hiện các thao tác truy vấn và cập nhật dữ liệu.




service

	

Xử lý logic nghiệp vụ và điều phối hoạt động hệ thống.




ui

	

Chứa giao diện Java Swing và các thành phần hiển thị.




util

	

Chứa các hàm tiện ích dùng chung.

4. CHI TIẾT THIẾT KẾ CÁC THÀNH PHẦN
4.1. Tầng Entity/Model

Tầng Entity đại diện cho các đối tượng dữ liệu tương ứng với những thực thể trong cơ sở dữ liệu.

Mỗi lớp Entity thường bao gồm:

Các thuộc tính tương ứng với những trường dữ liệu.

Hàm khởi tạo không tham số và có tham số.

Các phương thức Getter và Setter.

Các phương thức hỗ trợ biểu diễn hoặc xử lý dữ liệu khi cần thiết.

Ví dụ, lớp Customer đại diện cho khách hàng, bao gồm mã khách hàng, họ tên, số điện thoại và địa chỉ.

4.2. Tầng DAO

Tầng DAO đảm nhiệm việc giao tiếp trực tiếp với cơ sở dữ liệu thông qua JDBC.

Các phương thức cơ bản bao gồm:

findAll(): Lấy danh sách dữ liệu.

findById(id): Tìm kiếm theo mã.

insert(entity): Thêm dữ liệu.

update(entity): Cập nhật dữ liệu.

delete(id): Xóa dữ liệu.

Để tăng khả năng tái sử dụng, hệ thống có thể xây dựng BaseDAO dưới dạng Generic Interface, định nghĩa các thao tác CRUD dùng chung cho những lớp DAO cụ thể.

Các lớp như CustomerDAO, PetDAO và MedicalRecordDAO sẽ triển khai những phương thức phù hợp với từng loại dữ liệu.

4.3. Tầng Service

Tầng Service xử lý các nghiệp vụ trước khi yêu cầu DAO thao tác với cơ sở dữ liệu.

Một số nghiệp vụ tiêu biểu:

Nghiệp vụ quản lý khách hàng và thú cưng: Kiểm tra dữ liệu đầu vào, đảm bảo thông tin hợp lệ và liên kết thú cưng với đúng chủ nuôi.

Nghiệp vụ khám bệnh: Kiểm tra lịch hẹn, tiếp nhận thú cưng, lưu kết quả khám, chẩn đoán và đơn thuốc.

Nghiệp vụ thanh toán: Tổng hợp chi phí dịch vụ và thuốc, áp dụng giảm giá nếu có, sau đó tạo hóa đơn.

Nghiệp vụ quản lý kho: Kiểm tra số lượng thuốc tồn kho và cập nhật số lượng sau khi thuốc được sử dụng.

Nghiệp vụ phân quyền: Kiểm soát chức năng được phép sử dụng dựa trên vai trò và chi nhánh của nhân viên.

Đối với các nghiệp vụ liên quan đến nhiều thao tác dữ liệu, hệ thống sử dụng transaction để đảm bảo các thao tác được thực hiện đồng bộ. Nếu xảy ra lỗi, giao dịch sẽ được rollback nhằm tránh dữ liệu ở trạng thái không nhất quán.

Ví dụ, khi hoàn tất một hóa đơn có sử dụng thuốc, hệ thống cần đảm bảo việc lưu hóa đơn, lưu chi tiết hóa đơn và cập nhật tồn kho được xử lý trong cùng một giao dịch phù hợp.

4.4. Tầng giao diện Java Swing

Tầng giao diện được thiết kế theo hướng trực quan, dễ sử dụng và phù hợp với nghiệp vụ quản lý phòng khám.

Các thành phần chính gồm:

LoginDialog: Màn hình đăng nhập.

DashboardPanel: Màn hình tổng quan.

CustomerPanel: Quản lý khách hàng.

PetPanel: Quản lý thú cưng.

AppointmentPanel: Quản lý lịch hẹn.

MedicalRecordPanel: Quản lý hồ sơ khám bệnh.

InvoicePanel: Quản lý hóa đơn.

ReportPanel: Thống kê và báo cáo.

Các màn hình có thể được tổ chức bằng CardLayout hoặc JTabbedPane để chuyển đổi giữa những chức năng.

Hệ thống ưu tiên sử dụng các Layout Manager như BorderLayout, GridBagLayout và GridLayout thay vì đặt tọa độ cố định. Điều này giúp các thành phần giao diện tự điều chỉnh khi người dùng thay đổi kích thước cửa sổ.

5. CÔNG NGHỆ VÀ CÔNG CỤ SỬ DỤNG

Công nghệ/Công cụ

	

Mục đích




Java SE 11 trở lên

	

Ngôn ngữ và nền tảng phát triển ứng dụng.




Java Swing

	

Xây dựng giao diện desktop.




JDBC

	

Kết nối và thao tác với cơ sở dữ liệu.




MySQL

	

Lưu trữ và quản lý dữ liệu.




MySQL Connector/J

	

Thư viện kết nối Java với MySQL.




NetBeans IDE

	

Môi trường phát triển và chạy chương trình.




FlatLaf

	

Tùy chọn giao diện hiện đại cho Swing.




Maven

	

Quản lý thư viện và cấu hình quá trình build.




BCrypt

	

Mã hóa mật khẩu trước khi lưu trữ.




PDF library

	

Hỗ trợ xuất hóa đơn và báo cáo PDF.

5.1. Kết nối cơ sở dữ liệu

Hệ thống sử dụng JDBC để thiết lập kết nối giữa ứng dụng Java và MySQL. Lớp DatabaseConnection chịu trách nhiệm tạo và quản lý kết nối cơ sở dữ liệu.

Các lớp DAO sử dụng kết nối này để thực hiện truy vấn thông qua PreparedStatement, hỗ trợ truyền tham số an toàn và hạn chế nguy cơ SQL Injection.

5.2. Bảo mật và kiểm soát dữ liệu

Hệ thống áp dụng một số biện pháp bảo mật cơ bản:

Mã hóa mật khẩu bằng BCrypt trước khi lưu vào cơ sở dữ liệu.

Phân quyền theo vai trò người dùng.

Sử dụng PreparedStatement cho các câu truy vấn có tham số.

Kiểm tra dữ liệu đầu vào trước khi lưu.

Sử dụng transaction đối với các nghiệp vụ cập nhật nhiều dữ liệu liên quan.

6. ĐỊNH HƯỚNG MỞ RỘNG

Trong các giai đoạn tiếp theo, hệ thống có thể được mở rộng theo những hướng sau:

Bổ sung chức năng quản lý kho thuốc và cảnh báo thuốc sắp hết hạn.

Phát triển chức năng đặt lịch trực tuyến cho khách hàng.

Bổ sung biểu đồ doanh thu và thống kê trực quan.

Hỗ trợ sao lưu và phục hồi dữ liệu.

Tối ưu hiệu năng truy vấn khi số lượng dữ liệu tăng.

Mở rộng hệ thống để hỗ trợ nhiều chi nhánh và nhiều nhóm người dùng.

Bổ sung chức năng xuất báo cáo theo nhiều định dạng.

Lưu ý khi áp dụng vào dự án thực tế

Nội dung trên mô tả hệ thống theo phạm vi chức năng dự kiến. Để tài liệu khớp với chương trình thực tế, nhóm cần lưu ý ba điểm:

Cơ sở dữ liệu: Script ban đầu của bạn có 8 bảng, nhưng các chức năng lịch hẹn, chi tiết khám, đơn thuốc và quản lý tồn kho có thể cần bổ sung bảng như Appointment, MedicalDetail, Medicine và InvoiceDetail.

Công nghệ: Nếu nhóm sử dụng MySQL thì cần chạy script bằng MySQL Server/MySQL Workbench và kết nối qua MySQL Connector/J. Không sử dụng cú pháp SQL Server như IDENTITY(1,1) trong script MySQL.

Mức độ hoàn thiện: Các chức năng như BCrypt, xuất PDF, phân quyền theo chi nhánh, transaction và thống kê chỉ nên được ghi nhận là đã triển khai khi nhóm thực sự hoàn thành và kiểm thử chúng. Nếu chưa, hãy trình bày trong phần định hướng phát triển hoặc chức năng dự kiến.