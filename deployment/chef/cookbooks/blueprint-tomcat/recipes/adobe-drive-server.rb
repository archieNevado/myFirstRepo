=begin
#<
This recipe installs and configures the CoreMedia Blueprint Adobe Drive Server.
#>
=end
include_recipe 'blueprint-tomcat::_base'
service_name = 'adobe-drive-server'
cache_dir = "#{node['blueprint']['cache_dir']}/#{service_name}"
node.default['blueprint']['webapps'][service_name]['application.properties']['repository.url'] = "#{cm_webapp_url('content-management-server')}/ior"
# The path where the transformed blobs should be saved persistently. If not set, then the feature is deactivated,
# and all transformed blobs are saved in memory
node.default['blueprint']['webapps'][service_name]['application.properties']['com.coremedia.transform.blobCache.basePath'] = "#{cache_dir}/persistent-transformed-blobcache"

blueprint_tomcat_service service_name
