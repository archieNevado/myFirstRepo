=begin
#<
This recipe installs and configures the CoreMedia Blueprint Master Live Server.
#>
=end

service_name = 'replication-live-server'

node.default['blueprint']['webapps'][service_name]['application.properties']['cap.server.http.port'] = "#{node['blueprint']['tomcat'][service_name]['port_prefix']}80"
node.default['blueprint']['webapps'][service_name]['application.properties']['replicator.publicationIorUrl'] = "#{cm_webapp_url('master-live-server')}/ior"
node.default['blueprint']['webapps'][service_name]['application.properties']['cap.server.ORBServerHost'] = node['blueprint']['hostname']

blueprint_tomcat_service service_name

# in case cae-live and replicator are installed on the same node we set the default
node.force_default['blueprint']['webapps']['cae-live']['application.properties']['repository.url'] = "#{cm_webapp_url('replication-live-server')}/ior"
