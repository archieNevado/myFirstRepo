#<> An array of schemas create when using the schema recipe
default['blueprint']['mysql']['schemas'] = []

#<> mysql version to use
default['blueprint']['mysql']['version'] = '5.7'
#<> inital root password for mysql db
default['blueprint']['mysql']['initial_root_password'] = 'coremedia'
#<> port to be used by mysql
default['blueprint']['mysql']['port'] = '3306'
#<> mysql socket file path
default['blueprint']['mysql']['socket'] = '/var/lib/mysql/mysql.sock'
