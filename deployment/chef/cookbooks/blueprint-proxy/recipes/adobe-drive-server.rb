=begin
#<
This recipe installs virtual hosts for the CoreMedia Blueprint Adobe Drive Server.
#>
=end

include_recipe 'coremedia-proxy'

coremedia_proxy_webapp 'adobe-drive-server' do
  server_name node['blueprint']['proxy']['virtual_host']['adobe-drive-server']['server_name']
  server_aliases node['blueprint']['proxy']['virtual_host']['adobe-drive-server']['server_aliases']
  servlet_context node['blueprint']['proxy']['virtual_host']['adobe-drive-server']['context']
  cluster('default' => { 'host' => node['blueprint']['proxy']['virtual_host']['adobe-drive-server']['host'], 'port' => node['blueprint']['proxy']['virtual_host']['adobe-drive-server']['port'] })
  rewrite_log_level node['blueprint']['proxy']['virtual_host']['adobe-drive-server']['rewrite_log_level']
end
