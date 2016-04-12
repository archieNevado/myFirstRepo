=begin
#<
This recipe installs and configures the CoreMedia Blueprint Master Live Server.
#>
=end

service_name = 'master-live-server'

node.default['blueprint']['webapps'][service_name]['application.properties']['cap.server.http.port'] = "#{node['blueprint']['tomcat'][service_name]['port_prefix']}80"
node.default['blueprint']['webapps'][service_name]['application.properties']['cap.server.ORBServerHost'] = node['blueprint']['hostname']

blueprint_tomcat_service service_name
