include_recipe 'coremedia-proxy::default'
include_recipe 'blueprint-base::default'

coremedia_proxy_webapp 'candy-sfcc-commerce-shop-preview' do
  server_name "candy-#{node['blueprint']['lc3-sfcc']['virtual_host']['shop-preview']['server_name']}"
  time_travel_alias "candy-#{node['blueprint']['lc3-sfcc']['virtual_host']['shop-preview']['time_travel_alias']}"
  sfcc_host node['blueprint']['lc3-sfcc']['host']
  rewrite_log_level node['blueprint']['lc3-sfcc']['virtual_host']['shop-preview']['rewrite_log_level']
  rewrite_template 'rewrite/shop.erb'
  proxy_template 'proxy/shop.erb'
  proxy_template_cookbook 'blueprint-lc3-sfcc'
  site_server_name node['blueprint']['proxy']['virtual_host']['preview']['server_name']
  preview true
  headers [%q(
  SetEnvIf Remote_Addr "(.*)" devaddr=$1
  RequestHeader set X-FragmentHostDevelopment http://%{devaddr}e:40980/blueprint/servlet/
)]
  ssl_proxy_verify false
end

%w(preview studio-preview).each do |candy_setup|
  node.force_default['blueprint']['proxy']['candy_properties'][candy_setup]['blueprint.site.mapping.sitegenesis'] = "https://candy-preview.#{node['blueprint']['hostname']}"
  node.force_default['blueprint']['proxy']['candy_properties'][candy_setup]['livecontext.apache.sfcc.host'] = "candy-shop-preview-sitegenesis.#{node['blueprint']['hostname']}"
  node.force_default['blueprint']['proxy']['candy_properties'][candy_setup]['livecontext.sfcc.storeFrontUrl'] = "https://candy-shop-preview-sitegenesis.#{node['blueprint']['hostname']}/s/SiteGenesisGlobal/home?preview=true"
end
