# Description

This is the wrapper cookbook to install mongodb.



# Requirements

## Platform:

*No platforms defined*

## Cookbooks:

* mongodb (~> 0.16.2)
* blueprint-yum

# Attributes

* `node['blueprint']['mongodb']['yum']['baseurl']` - The baseurl for the mongodb rpm repository. Defaults to `http://repo.mongodb.org/yum/redhat/$releasever/mongodb-org/3.2/x86_64`.
* `node['blueprint']['mongodb']['yum']['mirrorlist']` - The mirrorlist url for the mongodb rpm repository. Defaults to `nil`.
* `node['blueprint']['mongodb']['yum']['exclude']` -  Defaults to `nil`.
* `node['blueprint']['mongodb']['yum']['enablegroups']` -  Defaults to `nil`.
* `node['blueprint']['mongodb']['yum']['http_caching']` -  Defaults to `all`.
* `node['blueprint']['mongodb']['yum']['include_config']` -  Defaults to `nil`.
* `node['blueprint']['mongodb']['yum']['includepkgs']` -  Defaults to `nil`.
* `node['blueprint']['mongodb']['yum']['max_retries']` -  Defaults to `2`.
* `node['blueprint']['mongodb']['yum']['metadata_expire']` -  Defaults to `nil`.
* `node['blueprint']['mongodb']['yum']['mirror_expire']` -  Defaults to `nil`.
* `node['blueprint']['mongodb']['yum']['priority']` -  Defaults to `1`.
* `node['blueprint']['mongodb']['yum']['proxy']` -  Defaults to `nil`.
* `node['blueprint']['mongodb']['yum']['proxy_username']` -  Defaults to `nil`.
* `node['blueprint']['mongodb']['yum']['proxy_password']` -  Defaults to `nil`.
* `node['blueprint']['mongodb']['yum']['timeout']` -  Defaults to `30`.

# Recipes

* [blueprint-mongodb::default](#blueprint-mongodbdefault) - This recipe installs and configures mongodb.

## blueprint-mongodb::default

This recipe installs and configures mongodb.

# Author

Author:: Your Name (<your_name@domain.com>)
