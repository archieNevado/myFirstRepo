CREATE SCHEMA cm_management CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE USER 'cm_management'@'localhost' IDENTIFIED BY 'cm_management';
CREATE USER 'cm_management'@'%' IDENTIFIED BY 'cm_management';
GRANT ALL PRIVILEGES ON cm_management.* TO 'cm_management'@'%', 'cm_management'@'localhost';

CREATE SCHEMA cm_master CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE USER 'cm_master'@'localhost' IDENTIFIED BY 'cm_master';
CREATE USER 'cm_master'@'%' IDENTIFIED BY 'cm_master';
GRANT ALL PRIVILEGES ON cm_master.* TO 'cm_master'@'%', 'cm_master'@'localhost';

CREATE SCHEMA cm_caefeeder CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE USER 'cm_caefeeder'@'localhost' IDENTIFIED BY 'cm_caefeeder';
CREATE USER 'cm_caefeeder'@'%' IDENTIFIED BY 'cm_caefeeder';
GRANT ALL PRIVILEGES ON cm_caefeeder.* TO 'cm_caefeeder'@'%', 'cm_caefeeder'@'localhost';

CREATE SCHEMA cm_mcaefeeder CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE USER 'cm_mcaefeeder'@'localhost' IDENTIFIED BY 'cm_mcaefeeder';
CREATE USER 'cm_mcaefeeder'@'%' IDENTIFIED BY 'cm_mcaefeeder';
GRANT ALL PRIVILEGES ON cm_mcaefeeder.* TO 'cm_mcaefeeder'@'%', 'cm_mcaefeeder'@'localhost';

CREATE SCHEMA cm_replication CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE USER 'cm_replication'@'localhost' IDENTIFIED BY 'cm_replication';
CREATE USER 'cm_replication'@'%' IDENTIFIED BY 'cm_replication';
GRANT ALL PRIVILEGES ON cm_replication.* TO 'cm_replication'@'%', 'cm_replication'@'localhost';

CREATE SCHEMA cm_editorial_comments CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE USER 'cm_editorial_comments'@'localhost' IDENTIFIED BY 'cm_editorial_comments';
CREATE USER 'cm_editorial_comments'@'%' IDENTIFIED BY 'cm_editorial_comments';
GRANT ALL PRIVILEGES ON cm_editorial_comments.* TO 'cm_editorial_comments'@'%', 'cm_editorial_comments'@'localhost';
