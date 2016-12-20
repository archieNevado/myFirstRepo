=begin
#<
This recipe installs and configures the CoreMedia Blueprint Studio.
#>
=end

service_name = 'studio'
cache_dir = "#{node['blueprint']['cache_dir']}/#{service_name}"

node.default['blueprint']['webapps'][service_name]['application.properties']['repository.url'] = "#{cm_webapp_url('content-management-server')}/ior"
node.default['blueprint']['webapps'][service_name]['application.properties']['solr.url'] = cm_webapp_url('solr')
node.default['blueprint']['webapps'][service_name]['application.properties']['solr.collection.cae'] = 'preview'
node.default['blueprint']['webapps'][service_name]['application.properties']['solr.collection.content'] = 'studio'
node.default['blueprint']['webapps'][service_name]['application.properties']['elastic.solr.url'] = cm_webapp_url('solr')
node.default['blueprint']['webapps'][service_name]['application.properties']['repository.heapCacheSize'] = 100 * 1024 * 1024
node.default['blueprint']['webapps'][service_name]['application.properties']['repository.blobCacheSize'] = 10 * 1024 * 1024 * 1024
node.default['blueprint']['webapps'][service_name]['application.properties']['repository.blobStreamingSizeThreshold'] = -1
node.default['blueprint']['webapps'][service_name]['application.properties']['repository.blobStreamingThreads'] = -1
node.default['blueprint']['webapps'][service_name]['application.properties']['repository.maxCachedBlobSize'] = -1
node.default['blueprint']['webapps'][service_name]['application.properties']['studio.previewUrlPrefix'] = "//preview-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['studio.previewUrlWhitelist'] = "*.#{node['blueprint']['hostname']}, #{node['blueprint']['wcs']['host']}, #{node['blueprint']['wcs']['host']}:8000"
node.default['blueprint']['webapps'][service_name]['application.properties']['es.cae.http.host'] = node['blueprint']['hostname']
node.default['blueprint']['webapps'][service_name]['application.properties']['es.cae.protocol'] = 'http'
node.default['blueprint']['webapps'][service_name]['application.properties']['blueprint.host.studio.helios'] = "studio-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['blueprint.host.corporate'] = "preview-corporate.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['link.urlPrefixType'] = 'live'
node.default['blueprint']['webapps'][service_name]['application.properties']['blueprint.site.mapping.helios'] = "http://preview-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['blueprint.site.mapping.corporate'] = "http://preview-corporate.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['blueprint.host.studio.corporate'] = "studio-corporate.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['blueprint.host.helios'] = "preview-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['externalpreview.restUrl'] = "http://preview-helios.#{node['blueprint']['hostname']}/blueprint/servlet/service/externalpreview"
node.default['blueprint']['webapps'][service_name]['application.properties']['externalpreview.previewUrl'] = "https://preview-helios.#{node['blueprint']['hostname']}/blueprint/externalpreview"
node.default['blueprint']['webapps'][service_name]['application.properties']['externalpreview.urlPrefix'] = "https://preview-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['livecontext.apache.wcs.host'] = "shop-preview-production-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['livecontext.apache.preview.production.wcs.host'] = "shop-preview-production-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['livecontext.apache.preview.wcs.host'] = "shop-preview-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['livecontext.apache.live.production.wcs.host'] = "shop-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['livecontext.ibm.contract.preview.credentials.username'] = 'preview'
node.default['blueprint']['webapps'][service_name]['application.properties']['livecontext.ibm.contract.preview.credentials.password'] = 'passw0rd'
node.default['blueprint']['webapps'][service_name]['application.properties']['toolbox.jmx.url'] = 'service:jmx:rmi://localhost:40998/jndi/rmi://localhost:40999/jmxrmi'
node.default['blueprint']['webapps'][service_name]['application.properties']['toolbox.authorized_groups'] = ''
node.default['blueprint']['webapps'][service_name]['application.properties']['repository.blobCachePath'] = '${catalina.home}/temp'
# The path where the transformed blobs should be saved persistently. If not set, then the feature is deactivated,
# and all transformed blobs are saved in memory
node.default['blueprint']['webapps'][service_name]['application.properties']['com.coremedia.transform.blobCache.basePath'] = "#{cache_dir}/persistent-transformed-blobcache"

node.override['blueprint']['webapps'][service_name]['application.properties']['repository.blobCachePath'] = cache_dir
# inject wcs configuration
node['blueprint']['wcs']['application.properties'].each_pair do |k, v|
  node.default['blueprint']['webapps'][service_name]['application.properties'][k] = v
end

blueprint_tomcat_service service_name

tomcat = cm_tomcat(service_name)
directory cache_dir do
  owner tomcat.user
  group tomcat.group
  subscribes :create, tomcat, :immediately
end
