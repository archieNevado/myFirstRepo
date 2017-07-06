=begin
#<
This recipe sets properties only necessary if the test content is being used
#>
=end

node.default['blueprint']['webapps']['cae-live']['application.properties']['blueprint.site.mapping.corporate'] = "//corporate.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps']['cae-preview']['application.properties']['blueprint.site.mapping.corporate'] = "//preview.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps']['studio']['application.properties']['blueprint.site.mapping.corporate'] = "//preview.#{node['blueprint']['hostname']}"

# candy config for the generated candy properties
node.force_default['blueprint']['proxy']['candy_properties']['preview']['blueprint.site.mapping.corporate'] = "//candy-preview.#{node['blueprint']['hostname']}"
node.force_default['blueprint']['proxy']['candy_properties']['studio-preview']['blueprint.site.mapping.corporate'] = "//candy-preview.#{node['blueprint']['hostname']}"