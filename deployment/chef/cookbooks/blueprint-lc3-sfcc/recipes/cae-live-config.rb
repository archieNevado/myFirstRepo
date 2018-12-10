node.default['blueprint']['webapps']['cae-live']['application.properties']['livecontext.sfcc.host'] = node['blueprint']['lc3-sfcc']['host']
node.default['blueprint']['webapps']['cae-live']['application.properties']['livecontext.apache.sfcc.host'] = "shop-sitegenesis.#{node['blueprint']['hostname']}"

# inject SFCC configuration
node['blueprint']['lc3-sfcc']['application.properties'].each_pair do |k, v|
  node.default['blueprint']['webapps']['cae-live']['application.properties'][k] = v
end
