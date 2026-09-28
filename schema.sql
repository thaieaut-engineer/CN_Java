-- 1. Tạo Database
CREATE DATABASE PetClinicDB;
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
    CONSTRAINT FK_Pet_Customer FOREIGN KEY (customer_id) 
        REFERENCES Customer(customer_id) ON DELETE CASCADE
);

-- 5. Bảng Nhân viên (Employee)
CREATE TABLE Employee (
    employee_id INT IDENTITY(1,1) PRIMARY KEY,
    branch_id INT NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    role NVARCHAR(50), -- Admin, BacSi, NhanVien
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    CONSTRAINT FK_Employee_Branch FOREIGN KEY (branch_id) 
        REFERENCES Branch(branch_id)
);

-- 6. Bảng Dịch vụ & Thuốc (Service)
CREATE TABLE Service (
    service_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    type NVARCHAR(50) -- KhamBenh, Spa, TiemPhong, Thuoc
);

-- 7. Bảng Phiếu khám bệnh (MedicalRecord)
CREATE TABLE MedicalRecord (
    record_id INT IDENTITY(1,1) PRIMARY KEY,
    pet_id INT NOT NULL,
    employee_id INT NOT NULL,
    branch_id INT NOT NULL,
    visit_date DATETIME DEFAULT GETDATE(),
    diagnosis NVARCHAR(MAX),
    notes NVARCHAR(MAX),
    CONSTRAINT FK_Record_Pet FOREIGN KEY (pet_id) REFERENCES Pet(pet_id),
    CONSTRAINT FK_Record_Employee FOREIGN KEY (employee_id) REFERENCES Employee(employee_id),
    CONSTRAINT FK_Record_Branch FOREIGN KEY (branch_id) REFERENCES Branch(branch_id)
);

-- 8. Bảng Hóa đơn (Invoice)
CREATE TABLE Invoice (
    invoice_id INT IDENTITY(1,1) PRIMARY KEY,
    record_id INT NOT NULL,
    created_date DATETIME DEFAULT GETDATE(),
    total_amount DECIMAL(10,2) DEFAULT 0,
    status NVARCHAR(20) DEFAULT N'Unpaid', -- Unpaid, Paid
    CONSTRAINT FK_Invoice_Record FOREIGN KEY (record_id) REFERENCES MedicalRecord(record_id)
);
GO

-- Thêm dữ liệu mẫu
INSERT INTO Branch (name, address, phone) VALUES 
(N'Chi nhánh Quận 1', N'123 Nguyễn Huệ, Q.1, TP.HCM', '0901234567'),
(N'Chi nhánh Cầu Giấy', N'45 Cầu Giấy, Hà Nội', '0907654321');

INSERT INTO Employee (branch_id, full_name, role, username, password) VALUES 
(1, N'Quản trị viên', 'Admin', 'admin', 'admin123'),
(1, N'Bác sĩ Nguyễn Văn A', 'BacSi', 'bsa', '123456');
GO