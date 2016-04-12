=begin
#<
This recipe installs and configures the CoreMedia Blueprint Adobe Drive Server.
#>
=end
include_recipe 'blueprint-tomcat::_base'
service_name = 'adobe-drive-server'
node.default['blueprint']['webapps'][service_name]['application.properties']['repository.url'] = "#{cm_webapp_url('content-management-server')}/ior"
blueprint_tomcat_service service_name
