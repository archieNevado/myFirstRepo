=begin
#<
This recipe installs virtual hosts for the CoreMedia Blueprint Studio.
#>
=end

include_recipe 'coremedia-proxy'

coremedia_proxy_webapp 'sfcc-studio' do
  server_name node['blueprint']['lc3-sfcc']['virtual_host']['studio']['server_name']
  server_aliases node['blueprint']['lc3-sfcc']['virtual_host']['studio']['server_aliases']
  servlet_context node['blueprint']['lc3-sfcc']['virtual_host']['studio']['context']
  cluster('default' => { 'host' => node['blueprint']['lc3-sfcc']['virtual_host']['studio']['host'], 'port' => node['blueprint']['lc3-sfcc']['virtual_host']['studio']['port'] })
  rewrite_log_level node['blueprint']['lc3-sfcc']['virtual_host']['studio']['rewrite_log_level']
  proxy_template 'proxy/studio.erb'
  sfcc_host node['blueprint']['lc3-sfcc']['host']
  proxy_template_cookbook 'blueprint-lc3-sfcc'
  rewrite_template_cookbook 'blueprint-proxy'
  rewrite_template 'rewrite/studio.erb'
end
