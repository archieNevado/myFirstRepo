=begin
#<
This recipe installs and configures mongodb.
#>
=end
require 'chef/version_constraint'
releasever = Chef::VersionConstraint.new('< 7.0.0').include?(node['platform_version']) ? '6' : '7'

yum_repository 'mongodb-repo' do
  description 'MongoDB RPM Repository'
  baseurl node['blueprint']['mongodb']['yum']['baseurl']
  mirrorlist node['blueprint']['mongodb']['yum']['mirrorlist']
  gpgcheck false
  enabled true
  exclude node['blueprint']['mongodb']['yum']['exclude']
  enablegroups node['blueprint']['mongodb']['yum']['enablegroups']
  failovermethod 'priority'
  http_caching node['blueprint']['mongodb']['yum']['http_caching']
  include_config node['blueprint']['mongodb']['yum']['include_config']
  includepkgs node['blueprint']['mongodb']['yum']['includepkgs']
  max_retries node['blueprint']['mongodb']['yum']['max_retries']
  metadata_expire node['blueprint']['mongodb']['yum']['metadata_expire']
  mirror_expire node['blueprint']['mongodb']['yum']['mirror_expire']
  priority node['blueprint']['mongodb']['yum']['priority']
  proxy node['blueprint']['mongodb']['yum']['proxy']
  proxy_username node['blueprint']['mongodb']['yum']['proxy_username']
  proxy_password node['blueprint']['mongodb']['yum']['proxy_password']
  repositoryid 'mongodb'
  timeout node['blueprint']['mongodb']['yum']['timeout']
  action :create
end

# this disables the installation of the 10gen_repo recipe and allows you to define a custom mirror via blueprint-yum
node.force_default['mongodb']['install_method'] = 'custom-repo'
node.force_default['mongodb']['package_name'] = 'mongodb-org'
node.force_default['mongodb']['package_version'] = "3.2.1-1.el#{releasever}"
node.force_default['mongodb']['config']['rest'] = true
node.force_default['mongodb']['config']['smallfiles'] = true

chef_gem 'chef-rewind' do
  compile_time true
end
require 'chef/rewind'

# things we just want for the first run
unless ::File.exist?(node['mongodb']['dbconfig_file'])
  # do not restart mongodb on first run, configuration is taken place before service start
  node.default['mongodb']['reload_action'] = 'nothing'
end

include_recipe 'mongodb'
# patch systemd service
if node['platform_family'] == 'rhel' && node['platform'] != 'amazon' && node['platform_version'].to_i >= 7
  init_file = File.join(node['mongodb']['init_dir'], "#{node['mongodb']['default_init_name']}")
  r = resources("template[#{init_file}]")
  r.cookbook 'blueprint-mongodb'
end

# because on amazon images, there may be no systemctl, although the platform may be rhel and the version can be greater than 7 (ie 2015.x.x), we need to ignore failures here
rewind 'execute[mongodb-systemctl-daemon-reload]' do
  ignore_failure true
end
