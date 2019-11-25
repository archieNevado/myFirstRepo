=begin
#<
This configures the CoreMedia Blueprint Studio. To install the Studio, the `blueprint-tomcat::studio` has to be listed after this recipe
#>
=end

node.default['blueprint']['webapps']['studio']['application.properties']['studio.previewUrlWhitelist'] = "*.#{node['blueprint']['hostname']}, #{node['blueprint']['lc3-ibm-wcs']['host']}, #{node['blueprint']['lc3-ibm-wcs']['host']}:8000"
node.default['blueprint']['webapps']['studio']['application.properties']['livecontext.apache.wcs.host'] = "shop-preview-production-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps']['studio']['application.properties']['livecontext.apache.preview.production.wcs.host'] = "shop-preview-production-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps']['studio']['application.properties']['livecontext.apache.preview.wcs.host'] = "shop-preview-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps']['studio']['application.properties']['livecontext.apache.live.production.wcs.host'] = "shop-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps']['studio']['application.properties']['livecontext.ibm.contract.preview.credentials.username'] = 'preview'
node.default['blueprint']['webapps']['studio']['application.properties']['livecontext.ibm.contract.preview.credentials.password'] = 'passw0rd'
# inject wcs configuration
node['blueprint']['lc3-ibm-wcs']['application.properties'].each_pair do |k, v|
  node.default['blueprint']['webapps']['studio']['application.properties'][k] = v
end
