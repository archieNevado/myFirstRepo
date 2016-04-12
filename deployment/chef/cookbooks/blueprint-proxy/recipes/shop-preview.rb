=begin
#<
This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Preview Shop.
#>
=end

include_recipe 'coremedia-proxy'

coremedia_proxy_webapp 'commerce-shop-preview' do
  server_name node['blueprint']['proxy']['virtual_host']['shop-preview']['server_name']
  server_aliases node['blueprint']['proxy']['virtual_host']['shop-preview']['server_aliases']
  time_travel_alias node['blueprint']['proxy']['virtual_host']['shop-preview']['time_travel_alias']
  wcs_host node['blueprint']['wcs']['host']
  rewrite_log_level node['blueprint']['proxy']['virtual_host']['shop-preview']['rewrite_log_level']
  rewrite_template 'rewrite/shop.erb'
  proxy_template 'proxy/shop.erb'
  proxy_template_cookbook 'blueprint-proxy'
  site_server_name node['blueprint']['proxy']['virtual_host']['preview']['sites']['helios']['server_name']
  preview true
  # set additional headers, see templates/default/proxy/shop.erb
  headers node['blueprint']['proxy']['virtual_host']['shop-preview']['headers']
end
