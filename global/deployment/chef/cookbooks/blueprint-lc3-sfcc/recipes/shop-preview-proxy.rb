=begin
#<
This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext SFCC Preview Shop.
#>
=end
include_recipe 'coremedia-proxy'

coremedia_proxy_webapp 'sfcc-commerce-shop-preview' do
  server_name node['blueprint']['lc3-sfcc']['virtual_host']['shop-preview']['server_name']
  server_aliases node['blueprint']['lc3-sfcc']['virtual_host']['shop-preview']['server_aliases']
  time_travel_alias node['blueprint']['lc3-sfcc']['virtual_host']['shop-preview']['time_travel_alias']
  sfcc_host node['blueprint']['lc3-sfcc']['host']
  rewrite_log_level node['blueprint']['lc3-sfcc']['virtual_host']['shop-preview']['rewrite_log_level']
  rewrite_template 'rewrite/shop.erb'
  proxy_template 'proxy/shop.erb'
  proxy_template_cookbook 'blueprint-lc3-sfcc'
  site_server_name node['blueprint']['proxy']['virtual_host']['preview']['server_name']
  preview true
  headers node['blueprint']['lc3-sfcc']['virtual_host']['shop-preview']['headers']
end
