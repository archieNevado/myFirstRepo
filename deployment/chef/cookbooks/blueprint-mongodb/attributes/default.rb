#<> define ulimits for mongod
default['ulimit'] = {
  users: {
    mongod: {
      filehandle_limit: 655_36,
      process_limit: 655_36
    }
  }
}
#<> overwrite mirrorlist
default['mongodb']['yum']['mirrorlist'] = 'https://s3-eu-west-1.amazonaws.com/mirrors.coremedia.com/repo.mongodb.org/yum/redhat/$releasever/mongodb-org/3.2/$basearch/mirror'

#<> fixed version
default['mongodb3']['version'] = '3.2.5'

#<> fixed configuration
# default['mongodb3']['config']['mongod']['net']['bindIp'] = '127.0.0.1'
default['mongodb3']['config']['mongod']['net']['http']['RESTInterfaceEnabled'] = true
default['mongodb3']['config']['mongod']['security']['authorization'] = nil
default['mongodb3']['config']['mongod']['storage']['mmapv1']['smallFiles'] = nil
# default['mongodb3']['config']['mongod']['storage']['journal']['enabled'] =  true
