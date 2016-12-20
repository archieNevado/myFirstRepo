=begin
#<
This recipe installs and configures the CoreMedia Blueprint Content Feeder.
#>
=end

service_name = 'content-feeder'

node.default['blueprint']['webapps'][service_name]['application.properties']['repository.url'] = "#{cm_webapp_url('content-management-server')}/ior"
node.default['blueprint']['webapps'][service_name]['application.properties']['solr.url'] = cm_webapp_url('solr')
node.default['blueprint']['webapps'][service_name]['application.properties']['solr.collection.content'] = 'studio'

blueprint_tomcat_service service_name
