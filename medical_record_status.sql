USE PetClinicDB;
GO

IF COL_LENGTH(N'dbo.MedicalRecord', N'record_status') IS NULL
BEGIN
    ALTER TABLE dbo.MedicalRecord
        ADD record_status NVARCHAR(20) NOT NULL
            CONSTRAINT DF_MedicalRecord_Status DEFAULT N'InProgress' WITH VALUES;
END;
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.check_constraints
    WHERE name = N'CK_MedicalRecord_Status'
      AND parent_object_id = OBJECT_ID(N'dbo.MedicalRecord')
)
BEGIN
    ALTER TABLE dbo.MedicalRecord
        ADD CONSTRAINT CK_MedicalRecord_Status
        CHECK (record_status IN (N'InProgress', N'Completed'));
END;
GO

UPDATE medical_record
SET record_status = N'Completed'
FROM dbo.MedicalRecord AS medical_record
WHERE EXISTS (
    SELECT 1 FROM dbo.Invoice AS invoice
    WHERE invoice.record_id = medical_record.record_id
);
GO
