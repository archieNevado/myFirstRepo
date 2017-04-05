#<> An array of schemas create when using the schema recipe
default['blueprint']['mysql']['schemas'] = []

#<> the innodb buffer pool size in megabytes
default['blueprint']['mysql']['innodb_buffer_pool_size_mb'] = 512
#<> The directory to log to
default['blueprint']['mysql']['log_dir'] = '/var/log/mysql'
#<> Toggle to activate slow query log
default['blueprint']['mysql']['slow_query_log'] = false
#<> Toggle to activate general logging of all queries
default['blueprint']['mysql']['general_log'] = false

#<> mysql version to use
default['blueprint']['mysql']['version'] = '5.7'
#<> inital root password for mysql db
default['blueprint']['mysql']['initial_root_password'] = 'coremedia'
#<> port to be used by mysql
default['blueprint']['mysql']['port'] = '3306'
#<> mysql socket file path
default['blueprint']['mysql']['socket'] = '/var/lib/mysql/mysql.sock'
