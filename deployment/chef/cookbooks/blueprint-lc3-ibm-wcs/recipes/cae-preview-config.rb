=begin
#<
This recipe installs and configures the CoreMedia Blueprint Preview CAE.
#>
=end

node.default['blueprint']['webapps']['cae-preview']['application.properties']['livecontext.apache.wcs.host'] = "shop-preview-production-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps']['cae-preview']['application.properties']['livecontext.apache.preview.production.wcs.host'] = "shop-preview-production-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps']['cae-preview']['application.properties']['livecontext.apache.preview.wcs.host'] = "shop-preview-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps']['cae-preview']['application.properties']['livecontext.apache.live.production.wcs.host'] = "shop-helios.#{node['blueprint']['hostname']}"

# inject wcs configuration
node['blueprint']['lc3-ibm-wcs']['application.properties'].each_pair do |k, v|
  node.default['blueprint']['webapps']['cae-preview']['application.properties'][k] = v
end
