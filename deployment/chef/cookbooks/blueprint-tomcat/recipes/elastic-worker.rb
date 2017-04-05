=begin
#<
This recipe installs and configures the CoreMedia Blueprint Elastic Worker.
#>
=end
include_recipe 'blueprint-tomcat::_base'
service_name = 'elastic-worker'

node.default['blueprint']['webapps'][service_name]['application.properties']['repository.url'] = "#{cm_webapp_url('content-management-server')}/ior"
node.default['blueprint']['webapps'][service_name]['application.properties']['elastic.solr.url'] = cm_webapp_url('solr')
node.default['blueprint']['webapps'][service_name]['application.properties']['elastic.social.mail.smtp.server'] = 'localhost'
node.default['blueprint']['webapps'][service_name]['application.properties']['elastic.social.mail.smtp.port'] = 25

blueprint_tomcat_service service_name
