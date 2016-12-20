=begin
#<
This recipe installs and configures mysql.
#>
=end

include_recipe 'blueprint-mysql::_base'

mysql_service 'default' do
  port node['blueprint']['mysql']['port']
  version node['blueprint']['mysql']['version']
  initial_root_password node['blueprint']['mysql']['initial_root_password']
  socket node['blueprint']['mysql']['socket']
  action [:create, :start]
end

mysql_config 'tuning' do
  source 'my_conf.erb'
  notifies :restart, 'mysql_service[default]', :immediate
  action :create
end
