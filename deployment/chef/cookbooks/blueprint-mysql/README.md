# Description

This is the wrapper cookbook to install mysql.


# Requirements

## Platform:

*No platforms defined*

## Cookbooks:

* mysql (~> 8.0.2)
* yum-mysql-community (~> 0.3.0)
* blueprint-base

# Attributes

* `node['blueprint']['mysql']['schemas']` - An array of schemas create when using the schema recipe. Defaults to `[ ... ]`.
* `node['blueprint']['mysql']['innodb_buffer_pool_size_mb']` - the innodb buffer pool size in megabytes. Defaults to `512`.
* `node['blueprint']['mysql']['log_dir']` - The directory to log to. Defaults to `/var/log/mysql`.
* `node['blueprint']['mysql']['slow_query_log']` - Toggle to activate slow query log. Defaults to `false`.
* `node['blueprint']['mysql']['general_log']` - Toggle to activate general logging of all queries. Defaults to `false`.
* `node['blueprint']['mysql']['version']` - mysql version to use. Defaults to `5.7`.
* `node['blueprint']['mysql']['initial_root_password']` - inital root password for mysql db. Defaults to `coremedia`.
* `node['blueprint']['mysql']['port']` - port to be used by mysql. Defaults to `3306`.
* `node['blueprint']['mysql']['socket']` - mysql socket file path. Defaults to `/var/lib/mysql/mysql.sock`.

# Recipes

* [blueprint-mysql::default](#blueprint-mysqldefault) - This recipe installs and configures mysql and creates schemas for CoreMedia Blueprint.
* [blueprint-mysql::schemas](#blueprint-mysqlschemas) - This recipe installs and configures mysql client and creates schemas.
* [blueprint-mysql::server](#blueprint-mysqlserver) - This recipe installs and configures mysql.

## blueprint-mysql::default

This recipe installs and configures mysql and creates schemas for CoreMedia Blueprint.

## blueprint-mysql::schemas

This recipe installs and configures mysql client and creates schemas.

## blueprint-mysql::server

This recipe installs and configures mysql.

# Author

Author:: Your Name (<your_name@domain.com>)
