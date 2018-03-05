=begin
#<
This recipe installs and configures the CoreMedia Blueprint Preview CAE.
#>
=end

# inject wcs configuration
node['blueprint']['lc3-sfcc']['application.properties'].each_pair do |k, v|
  node.default['blueprint']['webapps']['cae-preview']['application.properties'][k] = v
end
