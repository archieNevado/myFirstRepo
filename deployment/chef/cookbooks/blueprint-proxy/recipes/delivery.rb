=begin
#<
This recipe installs virtual hosts for the CoreMedia Blueprint Live CAE.
#>
=end

include_recipe 'coremedia-proxy'

node['blueprint']['proxy']['virtual_host']['delivery']['sites'].keys.each do |site|
  server_name = node['blueprint']['proxy']['virtual_host']['delivery']['sites'][site]['server_name']
  server_aliases = node['blueprint']['proxy']['virtual_host']['delivery']['sites'][site]['server_aliases']
  default_site = node['blueprint']['proxy']['virtual_host']['delivery']['sites'][site]['default_site']
  sitemap_site_name = node['blueprint']['proxy']['virtual_host']['delivery']['sites'][site]['sitemap_site_name']

  coremedia_proxy_webapp "delivery-#{site}" do
    server_name server_name
    server_aliases server_aliases
    servlet_context node['blueprint']['proxy']['virtual_host']['delivery']['context']
    default_servlet 'servlet'
    cluster node['blueprint']['proxy']['virtual_host']['delivery']['cluster']
    default_site default_site
    sitemap_site_name sitemap_site_name
    rewrite_template 'rewrite/delivery.erb'
    rewrite_log_level node['blueprint']['proxy']['virtual_host']['delivery']['rewrite_log_level']
  end
end
