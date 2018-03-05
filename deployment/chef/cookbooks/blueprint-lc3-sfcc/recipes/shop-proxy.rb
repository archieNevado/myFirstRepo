=begin
#<
This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext SFCC Live Shop.
#>
=end
include_recipe 'coremedia-proxy'

coremedia_proxy_webapp 'sfcc-commerce-shop' do
  server_name node['blueprint']['lc3-sfcc']['virtual_host']['shop']['server_name']
  server_aliases node['blueprint']['lc3-sfcc']['virtual_host']['shop']['server_aliases']
  time_travel_alias node['blueprint']['lc3-sfcc']['virtual_host']['shop']['time_travel_alias']
  sfcc_host node['blueprint']['lc3-sfcc']['host']
  rewrite_log_level node['blueprint']['lc3-sfcc']['virtual_host']['shop']['rewrite_log_level']
  rewrite_template 'rewrite/shop.erb'
  proxy_template 'proxy/shop.erb'
  proxy_template_cookbook 'blueprint-lc3-sfcc'
  site_server_name node['blueprint']['lc3-sfcc']['virtual_host']['delivery']['sites']['sitegenesis']['server_name']
  headers node['blueprint']['lc3-sfcc']['virtual_host']['shop']['headers']
  ssl_proxy_verify node['blueprint']['lc3-sfcc']['ssl_proxy_verify']
end
