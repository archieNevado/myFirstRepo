=begin
#<
This recipe installs and configures the CoreMedia Blueprint Master Live Server.
#>
=end
include_recipe 'blueprint-tomcat::_base'
service_name = 'master-live-server'

node.default['blueprint']['webapps'][service_name]['application.properties']['cap.server.http.port'] = "#{node['blueprint']['tomcat'][service_name]['port_prefix']}80"
node.default['blueprint']['webapps'][service_name]['application.properties']['cap.server.ORBServerHost'] = node['blueprint']['hostname']
node.default['blueprint']['webapps'][service_name]['application.properties']['cap.server.ORBServerPort'] = "#{node['blueprint']['tomcat'][service_name]['port_prefix']}83"

blueprint_tomcat_service service_name
