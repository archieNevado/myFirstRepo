USE [cm_management]
GO
DROP SCHEMA [cm_management]
GO

USE [cm_management]
GO
DROP USER [cm_management]
GO

USE [master]
GO
DROP LOGIN [cm_management]
GO

EXEC msdb.dbo.sp_delete_database_backuphistory @database_name = N'cm_management'
GO
USE [master]
GO
ALTER DATABASE [cm_management] SET SINGLE_USER WITH ROLLBACK IMMEDIATE
GO
USE [master]
GO
DROP DATABASE [cm_management]
GO
