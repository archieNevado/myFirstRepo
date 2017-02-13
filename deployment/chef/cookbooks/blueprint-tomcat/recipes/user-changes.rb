=begin
#<
This recipe installs and configures the CoreMedia Blueprint User Changes Webapp.
#>
=end
include_recipe 'blueprint-tomcat::_base'
service_name = 'user-changes'
node.default['blueprint']['webapps'][service_name]['application.properties']['repository.url'] = "#{cm_webapp_url('content-management-server')}/ior"

blueprint_tomcat_service service_name
