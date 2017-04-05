=begin
#<
This recipe installs virtual hosts for the CoreMedia Blueprint Candy Preview to your local CAE.
#>
=end
include_recipe 'blueprint-proxy::candy-proxy'

%w(preview studio-preview).each do |candy_setup|
  node.force_default['blueprint']['proxy']['candy_properties'][candy_setup]['blueprint.site.mapping.corporate'] = "//candy-preview.#{node['blueprint']['hostname']}"
end
