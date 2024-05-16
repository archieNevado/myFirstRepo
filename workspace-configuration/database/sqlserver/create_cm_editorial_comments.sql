CREATE DATABASE [cm_editorial_comments]
GO
ALTER DATABASE [cm_editorial_comments] SET COMPATIBILITY_LEVEL = 100
GO
ALTER DATABASE [cm_editorial_comments] SET ANSI_NULL_DEFAULT OFF
GO
ALTER DATABASE [cm_editorial_comments] SET ANSI_NULLS OFF
GO
ALTER DATABASE [cm_editorial_comments] SET ANSI_PADDING OFF
GO
ALTER DATABASE [cm_editorial_comments] SET ANSI_WARNINGS OFF
GO
ALTER DATABASE [cm_editorial_comments] SET ARITHABORT OFF
GO
ALTER DATABASE [cm_editorial_comments] SET AUTO_CLOSE OFF
GO
ALTER DATABASE [cm_editorial_comments] SET AUTO_CREATE_STATISTICS ON
GO
ALTER DATABASE [cm_editorial_comments] SET AUTO_SHRINK OFF
GO
ALTER DATABASE [cm_editorial_comments] SET AUTO_UPDATE_STATISTICS ON
GO
ALTER DATABASE [cm_editorial_comments] SET CURSOR_CLOSE_ON_COMMIT OFF
GO
ALTER DATABASE [cm_editorial_comments] SET CURSOR_DEFAULT GLOBAL
GO
ALTER DATABASE [cm_editorial_comments] SET CONCAT_NULL_YIELDS_NULL OFF
GO
ALTER DATABASE [cm_editorial_comments] SET NUMERIC_ROUNDABORT OFF
GO
ALTER DATABASE [cm_editorial_comments] SET QUOTED_IDENTIFIER OFF
GO
ALTER DATABASE [cm_editorial_comments] SET RECURSIVE_TRIGGERS OFF
GO
ALTER DATABASE [cm_editorial_comments] SET DISABLE_BROKER
GO
ALTER DATABASE [cm_editorial_comments] SET AUTO_UPDATE_STATISTICS_ASYNC OFF
GO
ALTER DATABASE [cm_editorial_comments] SET DATE_CORRELATION_OPTIMIZATION OFF
GO
ALTER DATABASE [cm_editorial_comments] SET PARAMETERIZATION SIMPLE
GO
ALTER DATABASE [cm_editorial_comments] SET READ_WRITE
GO
ALTER DATABASE [cm_editorial_comments] SET RECOVERY FULL
GO
ALTER DATABASE [cm_editorial_comments] SET MULTI_USER
GO
ALTER DATABASE [cm_editorial_comments] SET PAGE_VERIFY CHECKSUM
GO
USE [cm_editorial_comments]
GO
IF NOT EXISTS (SELECT name FROM sys.filegroups WHERE is_default=1 AND name = N'PRIMARY') ALTER DATABASE [cm_editorial_comments] MODIFY FILEGROUP [PRIMARY] DEFAULT
GO

USE [master]
GO
CREATE LOGIN [cm_editorial_comments] WITH PASSWORD=N'cm_editorial_comments', DEFAULT_DATABASE=[cm_editorial_comments], CHECK_EXPIRATION=OFF, CHECK_POLICY=OFF
GO
USE [cm_editorial_comments]
GO
CREATE USER [cm_editorial_comments] FOR LOGIN [cm_editorial_comments]
GO
USE [cm_editorial_comments]
GO
ALTER USER [cm_editorial_comments] WITH DEFAULT_SCHEMA=[cm_editorial_comments]
GO
USE [cm_editorial_comments]
GO
EXEC sp_addrolemember N'db_datareader', N'cm_editorial_comments'
GO
USE [cm_editorial_comments]
GO
EXEC sp_addrolemember N'db_datawriter', N'cm_editorial_comments'
GO
USE [cm_editorial_comments]
GO
EXEC sp_addrolemember N'db_ddladmin', N'cm_editorial_comments'
GO

USE [cm_editorial_comments]
GO
CREATE SCHEMA [cm_editorial_comments] AUTHORIZATION [cm_editorial_comments]
GO
USE [cm_editorial_comments]
GO
ALTER USER [cm_editorial_comments] WITH DEFAULT_SCHEMA=[cm_editorial_comments]
GO
