=begin
#<
This recipe configures the CoreMedia Blueprint Studio.
#>
=end

node.default['blueprint']['webapps']['studio']['application.properties']['studio.previewUrlWhitelist'] = "*.#{node['blueprint']['hostname']}, http://#{node['blueprint']['lc3-sfcc']['host']}, https://#{node['blueprint']['lc3-sfcc']['host']}"

# inject sfcc configuration
node['blueprint']['lc3-sfcc']['application.properties'].each_pair do |k, v|
  node.default['blueprint']['webapps']['studio']['application.properties'][k] = v
end
