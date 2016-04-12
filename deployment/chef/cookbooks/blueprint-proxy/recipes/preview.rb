=begin
#<
This recipe installs virtual hosts for the CoreMedia Blueprint Preview CAE.
#>
=end
include_recipe 'coremedia-proxy'

live_servlet_context = node['blueprint']['proxy']['virtual_host']['delivery']['context']
node['blueprint']['proxy']['virtual_host']['preview']['sites'].keys.each do |site|
  server_name = node['blueprint']['proxy']['virtual_host']['preview']['sites'][site]['server_name']
  server_aliases = node['blueprint']['proxy']['virtual_host']['preview']['sites'][site]['server_aliases']
  default_site = node['blueprint']['proxy']['virtual_host']['preview']['sites'][site]['default_site']

  coremedia_proxy_webapp "preview-#{site}" do
    server_name server_name
    server_aliases server_aliases
    default_servlet 'servlet'
    servlet_context node['blueprint']['proxy']['virtual_host']['preview']['context']
    live_servlet_context live_servlet_context
    cluster node['blueprint']['proxy']['virtual_host']['preview']['cluster']
    default_site default_site
    rewrite_template 'rewrite/preview.erb'
    rewrite_log_level node['blueprint']['proxy']['virtual_host']['preview']['rewrite_log_level']
  end
end
