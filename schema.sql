-- 1. Tạo Database
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'PetClinicDB')
BEGIN
    CREATE DATABASE PetClinicDB;
END
GO

USE PetClinicDB;
GO

-- 2. Bảng Chi nhánh (Branch)
CREATE TABLE Branch (
    branch_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    address NVARCHAR(255),
    phone VARCHAR(20)
);

-- 3. Bảng Khách hàng (Customer)
CREATE TABLE Customer (
    customer_id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(100) NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    address NVARCHAR(255)
);

-- 4. Bảng Thú cưng (Pet)
CREATE TABLE Pet (
    pet_id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT NOT NULL,
    name NVARCHAR(50) NOT NULL,
    species NVARCHAR(50), -- Chó, Mèo...
    breed NVARCHAR(50),   -- Giống
    age INT,
    photo VARBINARY(MAX) NULL,
    CONSTRAINT FK_Pet_Customer FOREIGN KEY (customer_id) 
        REFERENCES Customer(customer_id) ON DELETE CASCADE
);

-- 5. Bảng Nhân viên (Employee)
CREATE TABLE Employee (
    employee_id INT IDENTITY(1,1) PRIMARY KEY,
    branch_id INT NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    role NVARCHAR(50) NOT NULL, -- Admin, BacSi, NhanVien
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    account_status NVARCHAR(20) NOT NULL
        CONSTRAINT DF_Employee_AccountStatus DEFAULT N'Active'
        CONSTRAINT CK_Employee_AccountStatus CHECK (account_status IN (N'Pending', N'Active', N'Rejected')),
    photo VARBINARY(MAX) NULL,
    CONSTRAINT FK_Employee_Branch FOREIGN KEY (branch_id) 
        REFERENCES Branch(branch_id)
);

-- 6. Bảng Dịch vụ & Thuốc (Service & Inventory)
CREATE TABLE Service (
    service_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    type NVARCHAR(50) NOT NULL, -- KhamBenh, Spa, TiemPhong, Thuoc
    unit NVARCHAR(20),          -- Liều, Viên, Lần, Chai...
    stock_quantity INT DEFAULT 0 -- Số lượng tồn kho (dành cho Thuốc)
);

-- 7. Bảng Lịch hẹn (Appointment)
CREATE TABLE Appointment (
    appointment_id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT NOT NULL,
    pet_id INT NOT NULL,
    branch_id INT NOT NULL,
    employee_id INT, -- Bác sĩ chỉ định (nếu có)
    appointment_date DATETIME NOT NULL,
    status NVARCHAR(50) DEFAULT N'Pending', -- Pending, Confirmed, Completed, Cancelled
    notes NVARCHAR(MAX),
    CONSTRAINT FK_App_Customer FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
    CONSTRAINT FK_App_Pet FOREIGN KEY (pet_id) REFERENCES Pet(pet_id),
    CONSTRAINT FK_App_Branch FOREIGN KEY (branch_id) REFERENCES Branch(branch_id),
    CONSTRAINT FK_App_Employee FOREIGN KEY (employee_id) REFERENCES Employee(employee_id)
);

-- 8. Bảng Phiếu khám bệnh (MedicalRecord)
CREATE TABLE MedicalRecord (
    record_id INT IDENTITY(1,1) PRIMARY KEY,
    pet_id INT NOT NULL,
    employee_id INT NOT NULL,
    branch_id INT NOT NULL,
    visit_date DATETIME DEFAULT GETDATE(),
    diagnosis NVARCHAR(MAX),
    notes NVARCHAR(MAX),
    revisit_date DATETIME, -- Ngày hẹn tái khám / tiêm nhắc lại
    CONSTRAINT FK_Record_Pet FOREIGN KEY (pet_id) REFERENCES Pet(pet_id),
    CONSTRAINT FK_Record_Employee FOREIGN KEY (employee_id) REFERENCES Employee(employee_id),
    CONSTRAINT FK_Record_Branch FOREIGN KEY (branch_id) REFERENCES Branch(branch_id)
);

-- 9. Bảng Chi tiết phiếu khám (MedicalDetail - Kê đơn thuốc & Dịch vụ)
CREATE TABLE MedicalDetail (
    detail_id INT IDENTITY(1,1) PRIMARY KEY,
    record_id INT NOT NULL,
    service_id INT NOT NULL,
    quantity INT DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    CONSTRAINT FK_Detail_Record FOREIGN KEY (record_id) REFERENCES MedicalRecord(record_id) ON DELETE CASCADE,
    CONSTRAINT FK_Detail_Service FOREIGN KEY (service_id) REFERENCES Service(service_id)
);

-- 10. Bảng Hóa đơn (Invoice)
CREATE TABLE Invoice (
    invoice_id INT IDENTITY(1,1) PRIMARY KEY,
    record_id INT NOT NULL,
    created_date DATETIME DEFAULT GETDATE(),
    total_amount DECIMAL(10,2) DEFAULT 0,
    status NVARCHAR(20) DEFAULT N'Paid', -- Unpaid, Paid
    payment_method NVARCHAR(50) DEFAULT N'Tiền mặt', -- Tiền mặt, Chuyển khoản
    CONSTRAINT FK_Invoice_Record FOREIGN KEY (record_id) REFERENCES MedicalRecord(record_id)
);

-- 11. Bảng Nhập kho (InventoryReceipt - Nâng cao)
CREATE TABLE InventoryReceipt (
    receipt_id INT IDENTITY(1,1) PRIMARY KEY,
    service_id INT NOT NULL,
    employee_id INT NOT NULL,
    import_date DATETIME DEFAULT GETDATE(),
    quantity INT NOT NULL,
    import_price DECIMAL(10,2) NOT NULL,
    CONSTRAINT FK_Receipt_Service FOREIGN KEY (service_id) REFERENCES Service(service_id),
    CONSTRAINT FK_Receipt_Employee FOREIGN KEY (employee_id) REFERENCES Employee(employee_id)
);
GO

-- THÊM DỮ LIỆU MẪU BAN ĐẦU
INSERT INTO Branch (name, address, phone) VALUES 
(N'Chi nhánh Quận 1', N'123 Nguyễn Huệ, Q.1, TP.HCM', '0901234567'),
(N'Chi nhánh Cầu Giấy', N'45 Cầu Giấy, Hà Nội', '0907654321');

INSERT INTO Employee (branch_id, full_name, role, username, password) VALUES 
(1, N'Quản trị viên', 'Admin', 'admin', 'admin123'),
(1, N'Bác sĩ Nguyễn Văn A', 'BacSi', 'bsa', '123456'),
(1, N'Lễ tân Trần Thị C', 'NhanVien', 'letan', '123456');

INSERT INTO Service (name, price, type, unit, stock_quantity) VALUES
(N'Khám sức khỏe tổng quát', 150000, N'KhamBenh', N'Lần', 0),
(N'Tiêm vắc-xin 7 bệnh cho chó', 250000, N'TiemPhong', N'Liều', 50),
(N'Tắm spa & Cắt tỉa lông', 200000, N'Spa', N'Lần', 0),
(N'Thuốc đặc trị rận Bio-Shampoo', 85000, N'Thuoc', N'Chai', 30);
GO

-- Bổ sung theo dõi tiêm phòng và xuất kho; các bảng này cho phép chạy lại phần bổ sung.
IF OBJECT_ID(N'dbo.Vaccination', N'U') IS NULL
BEGIN
    CREATE TABLE Vaccination (
        vaccination_id INT IDENTITY(1,1) PRIMARY KEY,
        record_id INT NULL,
        pet_id INT NOT NULL,
        service_id INT NOT NULL,
        employee_id INT NOT NULL,
        branch_id INT NOT NULL,
        administered_date DATETIME NOT NULL,
        next_due_date DATETIME NOT NULL,
        notes NVARCHAR(500),
        CONSTRAINT FK_Vaccination_Record FOREIGN KEY (record_id) REFERENCES MedicalRecord(record_id),
        CONSTRAINT FK_Vaccination_Pet FOREIGN KEY (pet_id) REFERENCES Pet(pet_id),
        CONSTRAINT FK_Vaccination_Service FOREIGN KEY (service_id) REFERENCES Service(service_id),
        CONSTRAINT FK_Vaccination_Employee FOREIGN KEY (employee_id) REFERENCES Employee(employee_id),
        CONSTRAINT FK_Vaccination_Branch FOREIGN KEY (branch_id) REFERENCES Branch(branch_id)
    );
END;
GO

IF OBJECT_ID(N'dbo.InventoryIssue', N'U') IS NULL
BEGIN
    CREATE TABLE InventoryIssue (
        issue_id INT IDENTITY(1,1) PRIMARY KEY,
        service_id INT NOT NULL,
        employee_id INT NOT NULL,
        issue_date DATETIME NOT NULL DEFAULT GETDATE(),
        quantity INT NOT NULL CHECK (quantity > 0),
        notes NVARCHAR(500),
        CONSTRAINT FK_Issue_Service FOREIGN KEY (service_id) REFERENCES Service(service_id),
        CONSTRAINT FK_Issue_Employee FOREIGN KEY (employee_id) REFERENCES Employee(employee_id)
    );
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_MedicalRecord_RevisitDate'
               AND object_id = OBJECT_ID(N'dbo.MedicalRecord'))
    CREATE INDEX IX_MedicalRecord_RevisitDate ON MedicalRecord(revisit_date);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_Vaccination_NextDueDate'
               AND object_id = OBJECT_ID(N'dbo.Vaccination'))
    CREATE INDEX IX_Vaccination_NextDueDate ON Vaccination(next_due_date);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_Appointment_DateStatus'
               AND object_id = OBJECT_ID(N'dbo.Appointment'))
    CREATE INDEX IX_Appointment_DateStatus ON Appointment(appointment_date, status);
GO