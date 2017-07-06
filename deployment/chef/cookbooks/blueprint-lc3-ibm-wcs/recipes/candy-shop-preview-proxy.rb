include_recipe 'coremedia-proxy::default'
include_recipe 'blueprint-base::default'

coremedia_proxy_webapp 'candy-ibm-wcs-commerce-shop-preview' do
  server_name "candy-#{node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop-preview']['server_name']}"
  time_travel_alias "candy-#{node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop-preview']['time_travel_alias']}"
  wcs_host node['blueprint']['lc3-ibm-wcs']['host']
  rewrite_log_level node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop-preview']['rewrite_log_level']
  rewrite_template 'rewrite/shop-preview.erb'
  proxy_template 'proxy/shop-candy.erb'
  proxy_template_cookbook 'blueprint-lc3-ibm-wcs'
  site_server_name node['blueprint']['proxy']['virtual_host']['preview']['server_name']
  headers ['SetEnvIf Remote_Addr "(.*)" devaddr=$1', 'RequestHeader set X-FragmentHostDevelopment http://%{devaddr}e:40980/blueprint/servlet/service/fragment/']
  ssl_proxy_verify false
end

%w(preview studio-preview).each do |candy_setup|
  node.force_default['blueprint']['proxy']['candy_properties'][candy_setup]['livecontext.apache.wcs.host'] = "candy-shop-preview-production-helios.#{node['blueprint']['hostname']}"
  node.force_default['blueprint']['proxy']['candy_properties'][candy_setup]['livecontext.apache.preview.production.wcs.host'] = "candy-shop-preview-production-helios.#{node['blueprint']['hostname']}"
  node.force_default['blueprint']['proxy']['candy_properties'][candy_setup]['livecontext.apache.preview.wcs.host'] = "candy-shop-preview-helios.#{node['blueprint']['hostname']}"
  node.force_default['blueprint']['proxy']['candy_properties'][candy_setup]['livecontext.apache.live.production.wcs.host'] = "candy-shop-helios.#{node['blueprint']['hostname']}"
  node.force_default['blueprint']['proxy']['candy_properties'][candy_setup]['blueprint.site.mapping.helios'] = "//candy-preview.#{node['blueprint']['hostname']}"
end
