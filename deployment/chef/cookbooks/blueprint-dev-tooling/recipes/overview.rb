=begin
#<
creates a simple overview page with all necessary links at `overview.<hostname>` for dev systems
#>
=end
include_recipe 'blueprint-base::default'
include_recipe 'coremedia-proxy'

www_dir = directory "#{node['blueprint']['base_dir']}/overview" do
  owner node['blueprint']['user']
  group node['blueprint']['group']
end

template "#{www_dir.path}/index.html" do
  source 'overview.html.erb'
  owner node['blueprint']['user']
  group node['blueprint']['group']
end

template "#{www_dir.path}/jmx-java-mission-control.xml" do
  source 'jmx-jmc.xml.erb'
  owner node['blueprint']['user']
  group node['blueprint']['group']
end

web_app '_overview' do
  application_name 'overview'
  server_name "overview.#{node['blueprint']['hostname']}"
  template 'overview_vhost.conf.erb'
  docroot www_dir.path
end

# restart it immediately and not in delayed phase so we can use the overview before the content import has been finished.
log 'restart apache immediatel' do
  notifies :restart, 'service[apache2]', :immediately
end
