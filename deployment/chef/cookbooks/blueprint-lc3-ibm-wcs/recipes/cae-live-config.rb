node.default['blueprint']['webapps']['cae-live']['application.properties']['livecontext.apache.wcs.host'] = "shop-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps']['cae-live']['application.properties']['livecontext.apache.preview.production.wcs.host'] = "shop-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps']['cae-live']['application.properties']['livecontext.apache.preview.wcs.host'] = "shop-helios.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps']['cae-live']['application.properties']['livecontext.apache.live.production.wcs.host'] = "shop-helios.#{node['blueprint']['hostname']}"

# inject wcs configuration
node['blueprint']['lc3-ibm-wcs']['application.properties'].each_pair do |k, v|
  node.default['blueprint']['webapps']['cae-live']['application.properties'][k] = v
end
