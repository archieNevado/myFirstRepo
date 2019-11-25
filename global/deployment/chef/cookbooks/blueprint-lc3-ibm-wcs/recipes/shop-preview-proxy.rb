=begin
#<
This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Preview Shop.
#>
=end

include_recipe 'coremedia-proxy'

coremedia_proxy_webapp 'ibm-wcs-commerce-shop-preview' do
  server_name node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop-preview']['server_name']
  server_aliases node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop-preview']['server_aliases']
  time_travel_alias node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop-preview']['time_travel_alias']
  wcs_host node['blueprint']['lc3-ibm-wcs']['host']
  rewrite_log_level node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop-preview']['rewrite_log_level']
  rewrite_template 'rewrite/shop-preview.erb'
  proxy_template 'proxy/shop-preview.erb'
  proxy_template_cookbook 'blueprint-lc3-ibm-wcs'
  site_server_name node['blueprint']['proxy']['virtual_host']['preview']['server_name']
  # set additional headers, see templates/default/proxy/shop-preview.erb
  headers node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop-preview']['headers']
  ssl_proxy_verify node['blueprint']['lc3-ibm-wcs']['ssl_proxy_verify']
end
