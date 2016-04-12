=begin
#<
This recipe installs and configures the CoreMedia Blueprint Live CAEFeeder.
#>
=end

service_name = 'caefeeder-live'
cache_dir = "#{node['blueprint']['cache_dir']}/#{service_name}"

node.default['blueprint']['webapps'][service_name]['application.properties']['repository.url'] = "#{cm_webapp_url('master-live-server')}/ior"
node.default['blueprint']['webapps'][service_name]['application.properties']['feeder.solr.url'] = "#{cm_webapp_url('solr')}/live"
node.override['blueprint']['webapps'][service_name]['application.properties']['repository.blobCachePath'] = cache_dir

blueprint_tomcat_service service_name

tomcat = cm_tomcat(service_name)
directory cache_dir do
  owner tomcat.user
  group tomcat.group
  subscribes :create, tomcat, :immediately
end
