# Description

This is the wrapper cookbook to install mongodb3.

# Requirements

## Platform:

*No platforms defined*

## Cookbooks:

* ulimit (~> 0.3.0)
* mongodb3 (~> 5.2.0)

# Attributes

* `node['ulimit']` - define ulimits for mongod. Defaults to `{ ... }`.
* `node['mongodb']['yum']['mirrorlist']` - overwrite mirrorlist. Defaults to `https://s3-eu-west-1.amazonaws.com/mirrors.coremedia.com/repo.mongodb.org/yum/redhat/$releasever/mongodb-org/3.2/$basearch/mirror`.
* `node['mongodb3']['version']` - fixed version. Defaults to `3.2.5`.
* `node['mongodb3']['config']['mongod']['net']['bindIp']` - fixed configuration. Defaults to `127.0.0.1`.
* `node['mongodb3']['config']['mongod']['net']['http']['RESTInterfaceEnabled']` -  Defaults to `true`.
* `node['mongodb3']['config']['mongod']['security']['authorization']` -  Defaults to `nil`.
* `node['mongodb3']['config']['mongod']['storage']['mmapv1']['smallFiles']` -  Defaults to `nil`.

# Recipes

* blueprint-mongodb::default

# Author

Author:: Bodo Schulz (<bodo.schulz@coremedia.com>)

