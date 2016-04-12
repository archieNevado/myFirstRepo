=begin
#<
This recipe installs and configures mysql.
#>
=end

mysql_service 'default' do
  port '3306'
  version '5.5'
  initial_root_password 'coremedia'
  # somehow the matching does not work very well, this fixes to SystemV but there are more providers, see https://github.com/chef-cookbooks/mysql#providers
  provider Chef::Provider::MysqlServiceSysvinit
  # use the default socket file path so the client can connect without further configurations. If you need to install multiple instance you need to configure that too.
  socket '/var/lib/mysql/mysql.sock'
  action [:create, :start]
end

mysql_config 'tuning' do
  source 'my_conf.erb'
  notifies :restart, 'mysql_service[default]', :immediate
  action :create
end
