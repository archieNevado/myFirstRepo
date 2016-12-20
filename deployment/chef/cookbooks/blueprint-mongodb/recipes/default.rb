# Cookbook Name:: blueprint-mongodb
# Recipe:: default
#
# Copyright (c) 2016 Coremedia, All Rights Reserved.

# https://supermarket.chef.io/cookbooks/mongodb3#readme

require 'chef/version_constraint'
# releasever = Chef::VersionConstraint.new('< 7.0.0').include?(node['platform_version']) ? '6' : '7'

log 'add service script to disable Transparent Huge Pages (THP)'
cookbook_file '/etc/init.d/disable-transparent-hugepages' do
  source 'disable-transparent-hugepages'
  owner 'root'
  group 'root'
  mode 0755
end

# log 'start service'
service 'disable-transparent-hugepages' do
  action [:enable, :start]
  supports restart: false, reload: false, status: false
end

# log 'set local limits for mongod'
include_recipe 'ulimit::default'

include_recipe 'mongodb3'

pkg_major_version = node['mongodb3']['version'].to_f # eg. 3.0, 3.2

r = resources("yum_repository[mongodb-org-#{pkg_major_version}]")
r.mirrorlist node['mongodb']['yum']['mirrorlist']
r.repositoryid 'mongodb'

chef_gem 'chef-rewind' do
  compile_time true
end
require 'chef/rewind'

# Reload systemctl for RHEL 7+ after modifying the init file.
execute 'mongodb-systemctl-daemon-reload' do
  command 'systemctl daemon-reload'
  action :nothing
end

# because on amazon images, there may be no systemctl, although the platform may be rhel and the version can be greater than 7 (ie 2015.x.x), we need to ignore failures here
rewind 'execute[mongodb-systemctl-daemon-reload]' do
  ignore_failure true
end

# remove original service
unwind 'service[mongod]'

# include own service
service 'mongod3' do
  service_name 'mongod'
  case node['platform']
  when 'ubuntu'
    if node['platform_version'].to_f >= 14.04
      provider Chef::Provider::Service::Upstart
    end
  end
  supports start: true, stop: true, restart: true, status: true
  action [:enable, :start]
  subscribes :restart, "template[#{node['mongodb3']['mongod']['config_file']}]", :immediately
  subscribes :restart, "template[#{node['mongodb3']['config']['mongod']['security']['keyFile']}", :immediately
end
