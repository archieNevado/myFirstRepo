=begin
#<
This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Live Shop.
#>
=end

include_recipe 'coremedia-proxy'

coremedia_proxy_webapp 'ibm-wcs-commerce-shop' do
  server_name node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop']['server_name']
  server_aliases node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop']['server_aliases']
  wcs_host node['blueprint']['lc3-ibm-wcs']['host']
  rewrite_log_level node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop']['rewrite_log_level']
  rewrite_template 'rewrite/shop.erb'
  proxy_template 'proxy/shop.erb'
  proxy_template_cookbook 'blueprint-lc3-ibm-wcs'
  site_server_name node['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['sites']['helios']['server_name']
  # set additional headers, see templates/default/proxy/shop.erb
  headers node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop']['headers']
  ssl_proxy_verify node['blueprint']['lc3-ibm-wcs']['ssl_proxy_verify']
end
