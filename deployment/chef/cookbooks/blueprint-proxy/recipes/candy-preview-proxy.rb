include_recipe 'coremedia-proxy::default'
include_recipe 'blueprint-base::default'

coremedia_proxy_webapp 'candy-preview' do
  server_name "candy-preview.#{node['blueprint']['hostname']}"
  server_aliases server_aliases
  # local context
  servlet_context 'blueprint'
  default_servlet 'servlet'
  live_servlet_context node['blueprint']['proxy']['virtual_host']['preview']['live_servlet_context']
  local_webapp_port '40980'
  proxy_template 'proxy/candy.erb'
  proxy_template_cookbook 'blueprint-proxy'
  rewrite_template 'rewrite/preview.erb'
  rewrite_template_cookbook 'blueprint-proxy'
  default_site node['blueprint']['proxy']['virtual_host']['preview']['default_site']
  rewrite_log_level node['blueprint']['proxy']['virtual_host']['preview']['rewrite_log_level']
  ssl_proxy_verify false
end

preview_jmx_url = 'service:jmx:rmi://localhost:40998/jndi/rmi://localhost:40999/cae-preview'
mongo_client_uri = "mongodb://#{node['fqdn']}:27017/"

node.default['blueprint']['proxy']['candy_properties']['preview'] = node['blueprint']['webapps']['cae-preview']['application.properties'] if node['blueprint']['webapps']['cae-preview']['application.properties']
node.default['blueprint']['proxy']['candy_properties']['preview']['management.server.remote.url'] = preview_jmx_url
node.default['blueprint']['proxy']['candy_properties']['preview']['cae.developer.mode'] = true
node.default['blueprint']['proxy']['candy_properties']['preview']['cae.use.local.resources'] = true
node.default['blueprint']['proxy']['candy_properties']['preview']['view.debug.enabled'] = true
node.default['blueprint']['proxy']['candy_properties']['preview']['viewdispatcher.cache.enabled'] = true
node.default['blueprint']['proxy']['candy_properties']['preview']['mongoDb.clientURI'] = mongo_client_uri
node.default['blueprint']['proxy']['candy_properties']['preview']['repository.blobCachePath'] = ''
node.default['blueprint']['proxy']['candy_properties']['preview']['com.coremedia.transform.blobCache.basePath'] = ''
