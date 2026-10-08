USE PetClinicDB;
GO

IF COL_LENGTH(N'dbo.Employee', N'account_status') IS NULL
BEGIN
    ALTER TABLE dbo.Employee
        ADD account_status NVARCHAR(20) NOT NULL
            CONSTRAINT DF_Employee_AccountStatus DEFAULT N'Active' WITH VALUES;
END;
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.check_constraints
    WHERE name = N'CK_Employee_AccountStatus'
      AND parent_object_id = OBJECT_ID(N'dbo.Employee')
)
BEGIN
    ALTER TABLE dbo.Employee
        ADD CONSTRAINT CK_Employee_AccountStatus
        CHECK (account_status IN (N'Pending', N'Active', N'Rejected'));
END;
GO
