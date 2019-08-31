USE [cm_master]
GO
DROP SCHEMA [cm_master]
GO

USE [cm_master]
GO
DROP USER [cm_master]
GO

USE [master]
GO
DROP LOGIN [cm_master]
GO

EXEC msdb.dbo.sp_delete_database_backuphistory @database_name = N'cm_master'
GO
USE [master]
GO
ALTER DATABASE [cm_master] SET SINGLE_USER WITH ROLLBACK IMMEDIATE
GO
USE [master]
GO
DROP DATABASE [cm_master]
GO
