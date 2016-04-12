#<> The baseurl for the mongodb rpm repository
default['blueprint']['mongodb']['yum']['baseurl'] = "http://repo.mongodb.org/yum/redhat/$releasever/mongodb-org/3.2/x86_64"
#<> The mirrorlist url for the mongodb rpm repository
default['blueprint']['mongodb']['yum']['mirrorlist'] = nil
default['blueprint']['mongodb']['yum']['exclude'] = nil
default['blueprint']['mongodb']['yum']['enablegroups'] = nil
default['blueprint']['mongodb']['yum']['http_caching'] = 'all'
default['blueprint']['mongodb']['yum']['include_config'] = nil
default['blueprint']['mongodb']['yum']['includepkgs'] = nil
default['blueprint']['mongodb']['yum']['max_retries'] = '2'
default['blueprint']['mongodb']['yum']['metadata_expire'] = nil
default['blueprint']['mongodb']['yum']['mirror_expire'] = nil
default['blueprint']['mongodb']['yum']['priority'] = '1'
default['blueprint']['mongodb']['yum']['proxy'] = nil
default['blueprint']['mongodb']['yum']['proxy_username'] = nil
default['blueprint']['mongodb']['yum']['proxy_password'] = nil
default['blueprint']['mongodb']['yum']['timeout'] = '30'
