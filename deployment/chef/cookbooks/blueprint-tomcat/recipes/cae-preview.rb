=begin
#<
This recipe installs and configures the CoreMedia Blueprint Preview CAE.
#>
=end

service_name = 'cae-preview'
cache_dir = "#{node['blueprint']['cache_dir']}/#{service_name}"

node.default['blueprint']['webapps'][service_name]['application.properties']['repository.url'] = "#{cm_webapp_url('content-management-server')}/ior"
node.default['blueprint']['webapps'][service_name]['application.properties']['solr.search.url'] = "#{cm_webapp_url('solr')}/preview"
node.default['blueprint']['webapps'][service_name]['application.properties']['elastic.solr.url'] = cm_webapp_url('solr')
node.default['blueprint']['webapps'][service_name]['application.properties']['repository.heapCacheSize'] = 100 * 1024 * 1024
node.default['blueprint']['webapps'][service_name]['application.properties']['repository.blobCacheSize'] = 10 * 1024 * 1024 * 1024
node.default['blueprint']['webapps'][service_name]['application.properties']['repository.blobStreamingSizeThreshold'] = -1
node.default['blueprint']['webapps'][service_name]['application.properties']['repository.blobStreamingThreads'] = -1
node.default['blueprint']['webapps'][service_name]['application.properties']['repository.maxCachedBlobSize'] = -1
node.default['blueprint']['webapps'][service_name]['application.properties']['livecontext.apache.wcs.host'] = "shop-preview-production-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['livecontext.apache.preview.production.wcs.host'] = "shop-preview-production-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['livecontext.apache.preview.wcs.host'] = "shop-preview-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['livecontext.apache.live.production.wcs.host'] = "shop-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['blueprint.host.studio.helios'] = "studio-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['blueprint.host.helios'] = "preview-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['blueprint.host.corporate'] = "preview-corporate.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['link.urlPrefixType'] = 'preview'
node.default['blueprint']['webapps'][service_name]['application.properties']['blueprint.site.mapping.helios'] = "//preview-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps'][service_name]['application.properties']['blueprint.site.mapping.corporate'] = "//preview-corporate.#{node['blueprint']['hostname']}"
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
