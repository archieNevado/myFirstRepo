=begin
#<
This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Live Shop.
#>
=end

include_recipe 'coremedia-proxy'

coremedia_proxy_webapp 'commerce-shop' do
  server_name node['blueprint']['proxy']['virtual_host']['shop']['server_name']
  server_aliases node['blueprint']['proxy']['virtual_host']['shop']['server_aliases']
  wcs_host node['blueprint']['wcs']['host']
  rewrite_log_level node['blueprint']['proxy']['virtual_host']['shop']['rewrite_log_level']
  rewrite_template 'rewrite/shop.erb'
  proxy_template 'proxy/shop.erb'
  proxy_template_cookbook 'blueprint-proxy'
  site_server_name node['blueprint']['proxy']['virtual_host']['delivery']['sites']['helios']['server_name']
  headers node['blueprint']['proxy']['virtual_host']['shop']['headers']
end
