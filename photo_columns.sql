USE PetClinicDB;
GO

IF COL_LENGTH(N'dbo.Pet', N'photo') IS NULL
    ALTER TABLE dbo.Pet ADD photo VARBINARY(MAX) NULL;
GO

IF COL_LENGTH(N'dbo.Employee', N'photo') IS NULL
    ALTER TABLE dbo.Employee ADD photo VARBINARY(MAX) NULL;
GO
