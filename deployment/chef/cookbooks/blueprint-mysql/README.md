# Description

This is the wrapper cookbook to install mysql.



# Requirements

## Platform:

*No platforms defined*

## Cookbooks:

* mysql (~> 6.1.0)
* blueprint-base

# Attributes

* `node['blueprint']['mysql']['schemas']` - An array of schemas create when using the schema recipe. Defaults to `[ ... ]`.

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
