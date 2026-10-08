USE PetClinicDB;
GO

SET NOCOUNT ON;
SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    DECLARE @Customers TABLE (
        phone VARCHAR(20) PRIMARY KEY,
        full_name NVARCHAR(100) NOT NULL,
        address NVARCHAR(255) NOT NULL
    );

    INSERT INTO @Customers (phone, full_name, address) VALUES
        ('0912345001', N'Nguyễn Minh Anh', N'12 Nguyễn Trãi, Quận 1, TP.HCM'),
        ('0912345002', N'Trần Quốc Bảo', N'28 Lê Lai, Quận 1, TP.HCM'),
        ('0912345003', N'Lê Thu Hà', N'85 Võ Văn Tần, Quận 3, TP.HCM'),
        ('0912345004', N'Phạm Gia Huy', N'16 Nguyễn Huệ, Quận 1, TP.HCM'),
        ('0987654001', N'Đỗ Ngọc Linh', N'21 Trần Duy Hưng, Cầu Giấy, Hà Nội'),
        ('0987654002', N'Vũ Đức Long', N'43 Xuân Thủy, Cầu Giấy, Hà Nội'),
        ('0987654003', N'Hoàng Mai Phương', N'9 Duy Tân, Cầu Giấy, Hà Nội'),
        ('0987654004', N'Bùi Thanh Tùng', N'65 Cầu Giấy, Hà Nội');

    INSERT INTO Customer (full_name, phone, address)
    SELECT source.full_name, source.phone, source.address
    FROM @Customers AS source
    WHERE NOT EXISTS (
        SELECT 1 FROM Customer AS existing WHERE existing.phone = source.phone
    );

    INSERT INTO Service (name, price, type, unit, stock_quantity)
    SELECT source.name, source.price, source.type, source.unit, source.stock_quantity
    FROM (VALUES
        (N'Vắc-xin dại cho mèo', CAST(180000 AS DECIMAL(10,2)), N'TiemPhong', N'Liều', 25),
        (N'Thuốc tẩy giun phổ rộng', CAST(45000 AS DECIMAL(10,2)), N'Thuoc', N'Viên', 80),
        (N'Xét nghiệm máu cơ bản', CAST(320000 AS DECIMAL(10,2)), N'KhamBenh', N'Lần', 0),
        (N'Vệ sinh tai và cắt móng', CAST(90000 AS DECIMAL(10,2)), N'Spa', N'Lần', 0)
    ) AS source(name, price, type, unit, stock_quantity)
    WHERE NOT EXISTS (
        SELECT 1 FROM Service AS existing WHERE existing.name = source.name
    );

    DECLARE @HaNoiBranchId INT;
    DECLARE @HaNoiDoctorId INT;
    DECLARE @RequiredBranchCount INT;

    SELECT @RequiredBranchCount = COUNT(*)
    FROM Branch
    WHERE name IN (N'Chi nhánh Quận 1', N'Chi nhánh Cầu Giấy');

    IF @RequiredBranchCount <> 2
        THROW 51000, N'Không tìm thấy đủ hai chi nhánh dữ liệu mẫu. Hãy chạy schema.sql trước.', 1;

    SELECT @HaNoiBranchId = branch_id
    FROM Branch
    WHERE name = N'Chi nhánh Cầu Giấy';

    IF NOT EXISTS (SELECT 1 FROM Employee WHERE username = 'bacsi_hn_demo')
    BEGIN
        INSERT INTO Employee (branch_id, full_name, role, username, password)
        VALUES (@HaNoiBranchId, N'Bác sĩ Lê Hải Nam', N'BacSi', 'bacsi_hn_demo', 'PetClinic123!');
    END;

    SELECT @HaNoiDoctorId = employee_id
    FROM Employee
    WHERE username = 'bacsi_hn_demo'
      AND branch_id = @HaNoiBranchId;

    IF @HaNoiDoctorId IS NULL
        THROW 51001, N'Tài khoản bacsi_hn_demo đã tồn tại nhưng không thuộc chi nhánh Cầu Giấy.', 1;

    IF NOT EXISTS (
        SELECT 1
        FROM Employee
        WHERE username = 'bsa'
          AND role = N'BacSi'
    )
        THROW 51002, N'Không tìm thấy tài khoản bác sĩ bsa từ schema.sql.', 1;

    DECLARE @Pets TABLE (
        phone VARCHAR(20) NOT NULL,
        pet_name NVARCHAR(50) NOT NULL,
        species NVARCHAR(50) NOT NULL,
        breed NVARCHAR(50) NOT NULL,
        age INT NOT NULL,
        PRIMARY KEY (phone, pet_name)
    );

    INSERT INTO @Pets (phone, pet_name, species, breed, age) VALUES
        ('0912345001', N'Milo', N'Chó', N'Poodle', 3),
        ('0912345001', N'Mimi', N'Mèo', N'Anh lông ngắn', 2),
        ('0912345002', N'Lu', N'Chó', N'Phốc sóc', 4),
        ('0912345003', N'Bông', N'Mèo', N'Mèo ta', 1),
        ('0912345004', N'Lucky', N'Chó', N'Golden Retriever', 5),
        ('0987654001', N'Simba', N'Mèo', N'Mèo Anh lông ngắn', 2),
        ('0987654002', N'Đốm', N'Chó', N'Chó ta', 6),
        ('0987654003', N'Cookie', N'Thỏ', N'Holland Lop', 1),
        ('0987654004', N'Bắp', N'Chó', N'Corgi', 2);

    INSERT INTO Pet (customer_id, name, species, breed, age)
    SELECT customer.customer_id, source.pet_name, source.species, source.breed, source.age
    FROM @Pets AS source
    JOIN Customer AS customer ON customer.phone = source.phone
    WHERE NOT EXISTS (
        SELECT 1
        FROM Pet AS existing
        WHERE existing.customer_id = customer.customer_id
          AND existing.name = source.pet_name
    );

    DECLARE @Appointments TABLE (
        marker NVARCHAR(100) PRIMARY KEY,
        phone VARCHAR(20) NOT NULL,
        pet_name NVARCHAR(50) NOT NULL,
        branch_name NVARCHAR(100) NOT NULL,
        doctor_username VARCHAR(50) NOT NULL,
        day_offset INT NOT NULL,
        hour_of_day INT NOT NULL,
        status NVARCHAR(50) NOT NULL,
        note NVARCHAR(300) NOT NULL
    );

    INSERT INTO @Appointments VALUES
        (N'DEMO-SEED-APPT-01', '0912345001', N'Milo', N'Chi nhánh Quận 1', 'bsa', 0, 9, N'Confirmed', N'Khám định kỳ'),
        (N'DEMO-SEED-APPT-02', '0912345002', N'Lu', N'Chi nhánh Quận 1', 'bsa', 0, 10, N'Pending', N'Tái khám da liễu'),
        (N'DEMO-SEED-APPT-03', '0912345003', N'Bông', N'Chi nhánh Quận 1', 'bsa', 1, 14, N'Confirmed', N'Tiêm phòng cho mèo'),
        (N'DEMO-SEED-APPT-04', '0912345004', N'Lucky', N'Chi nhánh Quận 1', 'bsa', 2, 11, N'Pending', N'Tư vấn dinh dưỡng'),
        (N'DEMO-SEED-APPT-05', '0987654001', N'Simba', N'Chi nhánh Cầu Giấy', 'bacsi_hn_demo', 0, 15, N'Confirmed', N'Kiểm tra sức khỏe'),
        (N'DEMO-SEED-APPT-06', '0987654002', N'Đốm', N'Chi nhánh Cầu Giấy', 'bacsi_hn_demo', 2, 9, N'Pending', N'Khám tổng quát'),
        (N'DEMO-SEED-APPT-07', '0987654003', N'Cookie', N'Chi nhánh Cầu Giấy', 'bacsi_hn_demo', 4, 13, N'Confirmed', N'Kiểm tra răng'),
        (N'DEMO-SEED-APPT-08', '0987654004', N'Bắp', N'Chi nhánh Cầu Giấy', 'bacsi_hn_demo', 7, 10, N'Pending', N'Tiêm nhắc vắc-xin');

    INSERT INTO Appointment (
        customer_id, pet_id, branch_id, employee_id, appointment_date, status, notes
    )
    SELECT customer.customer_id, pet.pet_id, branch.branch_id, employee.employee_id,
           DATEADD(hour, source.hour_of_day, DATEADD(day, source.day_offset, CONVERT(DATETIME, CONVERT(DATE, GETDATE())))),
           source.status, source.marker + N' | ' + source.note
    FROM @Appointments AS source
    JOIN Customer AS customer ON customer.phone = source.phone
    JOIN Pet AS pet ON pet.customer_id = customer.customer_id AND pet.name = source.pet_name
    JOIN Branch AS branch ON branch.name = source.branch_name
    JOIN Employee AS employee ON employee.username = source.doctor_username
    WHERE NOT EXISTS (
        SELECT 1 FROM Appointment AS existing WHERE existing.notes LIKE source.marker + N' |%'
    );

    DECLARE @Records TABLE (
        marker NVARCHAR(100) PRIMARY KEY,
        phone VARCHAR(20) NOT NULL,
        pet_name NVARCHAR(50) NOT NULL,
        branch_name NVARCHAR(100) NOT NULL,
        doctor_username VARCHAR(50) NOT NULL,
        day_offset INT NOT NULL,
        diagnosis NVARCHAR(300) NOT NULL,
        revisit_offset INT NULL,
        service_name NVARCHAR(100) NOT NULL,
        quantity INT NOT NULL
    );

    INSERT INTO @Records VALUES
        (N'DEMO-SEED-RECORD-01', '0912345001', N'Milo', N'Chi nhánh Quận 1', 'bsa', -2, N'Viêm da nhẹ, cần theo dõi', 12, N'Khám sức khỏe tổng quát', 1),
        (N'DEMO-SEED-RECORD-02', '0912345002', N'Lu', N'Chi nhánh Quận 1', 'bsa', -5, N'Nhiễm ve, đã vệ sinh và điều trị', 9, N'Thuốc đặc trị rận Bio-Shampoo', 1),
        (N'DEMO-SEED-RECORD-03', '0912345003', N'Bông', N'Chi nhánh Quận 1', 'bsa', -9, N'Sức khỏe ổn định, tiêm phòng định kỳ', NULL, N'Tiêm vắc-xin 7 bệnh cho chó', 1),
        (N'DEMO-SEED-RECORD-04', '0912345004', N'Lucky', N'Chi nhánh Quận 1', 'bsa', -14, N'Thừa cân nhẹ, tư vấn chế độ ăn', 16, N'Khám sức khỏe tổng quát', 1),
        (N'DEMO-SEED-RECORD-05', '0987654001', N'Simba', N'Chi nhánh Cầu Giấy', 'bacsi_hn_demo', -3, N'Đã khám tổng quát, thể trạng tốt', 25, N'Vắc-xin dại cho mèo', 1),
        (N'DEMO-SEED-RECORD-06', '0987654002', N'Đốm', N'Chi nhánh Cầu Giấy', 'bacsi_hn_demo', -7, N'Viêm tai ngoài, kê thuốc và hẹn kiểm tra', 6, N'Vệ sinh tai và cắt móng', 1),
        (N'DEMO-SEED-RECORD-07', '0987654003', N'Cookie', N'Chi nhánh Cầu Giấy', 'bacsi_hn_demo', -11, N'Khám sức khỏe định kỳ, không phát hiện bất thường', NULL, N'Khám sức khỏe tổng quát', 1),
        (N'DEMO-SEED-RECORD-08', '0987654004', N'Bắp', N'Chi nhánh Cầu Giấy', 'bacsi_hn_demo', -18, N'Đã tiêm phòng, cần tiêm nhắc đúng lịch', 5, N'Tiêm vắc-xin 7 bệnh cho chó', 1);

    INSERT INTO MedicalRecord (
        pet_id, employee_id, branch_id, visit_date, diagnosis, notes, revisit_date
    )
    SELECT pet.pet_id, employee.employee_id, branch.branch_id,
           DATEADD(day, source.day_offset, CONVERT(DATETIME, CONVERT(DATE, GETDATE()))),
           source.diagnosis, source.marker + N' | Hồ sơ dữ liệu mẫu',
           CASE WHEN source.revisit_offset IS NULL THEN NULL
                ELSE DATEADD(day, source.revisit_offset, CONVERT(DATETIME, CONVERT(DATE, GETDATE()))) END
    FROM @Records AS source
    JOIN Customer AS customer ON customer.phone = source.phone
    JOIN Pet AS pet ON pet.customer_id = customer.customer_id AND pet.name = source.pet_name
    JOIN Branch AS branch ON branch.name = source.branch_name
    JOIN Employee AS employee ON employee.username = source.doctor_username
    WHERE NOT EXISTS (
        SELECT 1 FROM MedicalRecord AS existing WHERE existing.notes LIKE source.marker + N' |%'
    );

    INSERT INTO MedicalDetail (record_id, service_id, quantity, unit_price)
    SELECT record.record_id, service.service_id, source.quantity, service.price
    FROM @Records AS source
    JOIN MedicalRecord AS record ON record.notes LIKE source.marker + N' |%'
    JOIN Service AS service ON service.name = source.service_name
    WHERE NOT EXISTS (
        SELECT 1
        FROM MedicalDetail AS existing
        WHERE existing.record_id = record.record_id
          AND existing.service_id = service.service_id
    );

    INSERT INTO Invoice (record_id, created_date, total_amount, status, payment_method)
    SELECT record.record_id, GETDATE(),
           COALESCE(SUM(detail.quantity * detail.unit_price), 0),
           CASE WHEN source.marker = N'DEMO-SEED-RECORD-08' THEN N'Unpaid' ELSE N'Paid' END,
           CASE WHEN source.marker IN (N'DEMO-SEED-RECORD-02', N'DEMO-SEED-RECORD-05')
                THEN N'Chuyển khoản' ELSE N'Tiền mặt' END
    FROM @Records AS source
    JOIN MedicalRecord AS record ON record.notes LIKE source.marker + N' |%'
    LEFT JOIN MedicalDetail AS detail ON detail.record_id = record.record_id
    WHERE NOT EXISTS (
        SELECT 1 FROM Invoice AS existing WHERE existing.record_id = record.record_id
    )
    GROUP BY record.record_id, source.marker;

    UPDATE medical_record
    SET record_status = N'Completed'
    FROM dbo.MedicalRecord AS medical_record
    JOIN @Records AS source ON medical_record.notes LIKE source.marker + N' |%';

    DECLARE @Vaccinations TABLE (
        marker NVARCHAR(100) PRIMARY KEY,
        phone VARCHAR(20) NOT NULL,
        pet_name NVARCHAR(50) NOT NULL,
        service_name NVARCHAR(100) NOT NULL,
        branch_name NVARCHAR(100) NOT NULL,
        doctor_username VARCHAR(50) NOT NULL,
        days_ago INT NOT NULL,
        days_until_due INT NOT NULL,
        note NVARCHAR(200) NOT NULL
    );

    INSERT INTO @Vaccinations VALUES
        (N'DEMO-SEED-VACCINE-01', '0912345003', N'Bông', N'Vắc-xin dại cho mèo', N'Chi nhánh Quận 1', 'bsa', 330, 35, N'Tiêm phòng dại lần đầu'),
        (N'DEMO-SEED-VACCINE-02', '0987654001', N'Simba', N'Vắc-xin dại cho mèo', N'Chi nhánh Cầu Giấy', 'bacsi_hn_demo', 300, 5, N'Gửi nhắc lịch tiêm phòng'),
        (N'DEMO-SEED-VACCINE-03', '0987654004', N'Bắp', N'Tiêm vắc-xin 7 bệnh cho chó', N'Chi nhánh Cầu Giấy', 'bacsi_hn_demo', 330, 20, N'Lịch tiêm nhắc định kỳ');

    INSERT INTO Vaccination (
        record_id, pet_id, service_id, employee_id, branch_id,
        administered_date, next_due_date, notes
    )
    SELECT record.record_id, pet.pet_id, service.service_id, employee.employee_id,
           branch.branch_id,
           DATEADD(day, -source.days_ago, CONVERT(DATETIME, CONVERT(DATE, GETDATE()))),
           DATEADD(day, source.days_until_due, CONVERT(DATETIME, CONVERT(DATE, GETDATE()))),
           source.marker + N' | ' + source.note
    FROM @Vaccinations AS source
    JOIN Customer AS customer ON customer.phone = source.phone
    JOIN Pet AS pet ON pet.customer_id = customer.customer_id AND pet.name = source.pet_name
    JOIN Service AS service ON service.name = source.service_name
    JOIN Branch AS branch ON branch.name = source.branch_name
    JOIN Employee AS employee ON employee.username = source.doctor_username
    LEFT JOIN MedicalRecord AS record ON record.notes LIKE
        CASE source.marker
            WHEN N'DEMO-SEED-VACCINE-01' THEN N'DEMO-SEED-RECORD-03 |%'
            WHEN N'DEMO-SEED-VACCINE-02' THEN N'DEMO-SEED-RECORD-05 |%'
            ELSE N'DEMO-SEED-RECORD-08 |%'
        END
    WHERE NOT EXISTS (
        SELECT 1 FROM Vaccination AS existing WHERE existing.notes LIKE source.marker + N' |%'
    );

    DECLARE @NewReceipts TABLE (service_id INT NOT NULL, quantity INT NOT NULL);

    INSERT INTO InventoryReceipt (service_id, employee_id, import_date, quantity, import_price)
    OUTPUT inserted.service_id, inserted.quantity
        INTO @NewReceipts (service_id, quantity)
    SELECT service.service_id, employee.employee_id, CONVERT(DATETIME, '2026-09-01T09:00:00', 126),
           source.quantity, source.import_price
    FROM (VALUES
        (N'Thuốc tẩy giun phổ rộng', 40, CAST(28000 AS DECIMAL(10,2))),
        (N'Vắc-xin dại cho mèo', 15, CAST(125000 AS DECIMAL(10,2)))
    ) AS source(service_name, quantity, import_price)
    JOIN Service AS service ON service.name = source.service_name
    CROSS JOIN (SELECT TOP 1 employee_id FROM Employee WHERE username = 'letan' ORDER BY employee_id) AS employee
    WHERE NOT EXISTS (
        SELECT 1
        FROM InventoryReceipt AS existing
        WHERE existing.service_id = service.service_id
          AND existing.employee_id = employee.employee_id
          AND existing.import_date = CONVERT(DATETIME, '2026-09-01T09:00:00', 126)
          AND existing.quantity = source.quantity
    );

    UPDATE service
    SET stock_quantity = COALESCE(service.stock_quantity, 0) + totals.quantity
    FROM Service AS service
    JOIN (
        SELECT service_id, SUM(quantity) AS quantity
        FROM @NewReceipts
        GROUP BY service_id
    ) AS totals ON totals.service_id = service.service_id;

    DECLARE @NewIssues TABLE (service_id INT NOT NULL, quantity INT NOT NULL);

    INSERT INTO InventoryIssue (service_id, employee_id, issue_date, quantity, notes)
    OUTPUT inserted.service_id, inserted.quantity
        INTO @NewIssues (service_id, quantity)
    SELECT service.service_id, employee.employee_id,
           CONVERT(DATETIME, '2026-09-02T10:00:00', 126),
           source.quantity, source.marker
    FROM (VALUES
        (N'Thuốc tẩy giun phổ rộng', 3, N'DEMO-SEED-ISSUE-01')
    ) AS source(service_name, quantity, marker)
    JOIN Service AS service ON service.name = source.service_name
    CROSS JOIN (SELECT TOP 1 employee_id FROM Employee WHERE username = 'letan' ORDER BY employee_id) AS employee
    WHERE COALESCE(service.stock_quantity, 0) >= source.quantity
      AND NOT EXISTS (
          SELECT 1 FROM InventoryIssue AS existing WHERE existing.notes = source.marker
      );

    UPDATE service
    SET stock_quantity = service.stock_quantity - totals.quantity
    FROM Service AS service
    JOIN (
        SELECT service_id, SUM(quantity) AS quantity
        FROM @NewIssues
        GROUP BY service_id
    ) AS totals ON totals.service_id = service.service_id;

    COMMIT TRANSACTION;
    PRINT N'Đã thêm dữ liệu mẫu PetClinic thành công. Có thể chạy lại file này mà không tạo khách/lịch khám trùng.';
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO

-- Tài khoản mẫu để đăng nhập kiểm tra:
-- Admin: admin / admin123
-- Bác sĩ chi nhánh Quận 1: bsa / 123456
-- Lễ tân: letan / 123456
-- Bác sĩ chi nhánh Cầu Giấy mới: bacsi_hn_demo / PetClinic123!
-- Ứng dụng sẽ băm lại mật khẩu dạng cũ khi đăng nhập thành công.
