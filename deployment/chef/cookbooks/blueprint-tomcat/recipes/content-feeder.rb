=begin
#<
This recipe installs and configures the CoreMedia Blueprint Contentfeeder.
#>
=end

service_name = 'content-feeder'

node.default['blueprint']['webapps'][service_name]['application.properties']['repository.url'] = "#{cm_webapp_url('content-management-server')}/ior"
node.default['blueprint']['webapps'][service_name]['application.properties']['feeder.solr.url'] = "#{cm_webapp_url('solr')}/studio"

blueprint_tomcat_service service_name
