=begin
#<
This recipe sets properties only necessary if the test content is being used
#>
=end

node.default['blueprint']['webapps']['cae-live']['application.properties']['blueprint.site.mapping.corporate'] = "//corporate.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps']['cae-preview']['application.properties']['blueprint.site.mapping.corporate'] = "//preview.#{node['blueprint']['hostname']}"
# TODO: why http: and not protocol relative, this smells like local dev
node.default['blueprint']['webapps']['studio']['application.properties']['blueprint.site.mapping.corporate'] = "http://preview.#{node['blueprint']['hostname']}"
