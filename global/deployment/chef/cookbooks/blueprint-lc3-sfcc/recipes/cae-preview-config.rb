=begin
#<
This recipe installs and configures the CoreMedia Blueprint Preview CAE.
#>
=end
node.default['blueprint']['webapps']['cae-preview']['application.properties']['livecontext.sfcc.host'] = node['blueprint']['lc3-sfcc']['host']
node.default['blueprint']['webapps']['cae-preview']['application.properties']['livecontext.apache.sfcc.host'] = "shop-preview-sitegenesis.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps']['cae-preview']['application.properties']['livecontext.sfcc.storefront.url'] = "https://shop-preview-sitegenesis.#{node['blueprint']['hostname']}/on/demandware.store"

# inject wcs configuration
node['blueprint']['lc3-sfcc']['application.properties'].each_pair do |k, v|
  node.default['blueprint']['webapps']['cae-preview']['application.properties'][k] = v
end
