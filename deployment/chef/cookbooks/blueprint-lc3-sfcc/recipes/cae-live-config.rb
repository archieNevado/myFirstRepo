# inject SFCC configuration
node['blueprint']['lc3-sfcc']['application.properties'].each_pair do |k, v|
  node.default['blueprint']['webapps']['cae-live']['application.properties'][k] = v
end
