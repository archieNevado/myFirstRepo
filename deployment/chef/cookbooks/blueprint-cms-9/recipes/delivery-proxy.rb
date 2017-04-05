=begin
#<
This recipe installs virtual hosts for the CoreMedia Blueprint Live CAE.
#>
=end

include_recipe 'coremedia-proxy'

if node.deep_fetch('blueprint', 'tomcat', 'cae-live', 'instances')
  node.rm_default('blueprint', 'cms-9', 'virtual_host', 'delivery', 'cluster', 'default')
  (1..node['blueprint']['tomcat']['cae-live']['instances']).to_a.each do |i|
    node.default['blueprint']['cms-9']['virtual_host']['delivery']['cluster']["cae-live-#{i}"]['host'] = node['fqdn']
    node.default['blueprint']['cms-9']['virtual_host']['delivery']['cluster']["cae-live-#{i}"]['port'] = "#{node['blueprint']['tomcat']["cae-live-#{i}"]['port_prefix']}80"
  end
end

node['blueprint']['cms-9']['virtual_host']['delivery']['sites'].keys.each do |site|
  server_name = node['blueprint']['cms-9']['virtual_host']['delivery']['sites'][site]['server_name']
  server_aliases = node['blueprint']['cms-9']['virtual_host']['delivery']['sites'][site]['server_aliases']
  default_site = node['blueprint']['cms-9']['virtual_host']['delivery']['sites'][site]['default_site']
  sitemap_site_name = node['blueprint']['cms-9']['virtual_host']['delivery']['sites'][site]['sitemap_site_name']

  coremedia_proxy_webapp "delivery-#{site}" do
    server_name server_name
    server_aliases server_aliases
    servlet_context node['blueprint']['cms-9']['virtual_host']['delivery']['context']
    default_servlet 'servlet'
    cluster node['blueprint']['cms-9']['virtual_host']['delivery']['cluster']
    default_site default_site
    sitemap_site_name sitemap_site_name
    rewrite_template 'rewrite/delivery.erb'
    rewrite_log_level node['blueprint']['cms-9']['virtual_host']['delivery']['rewrite_log_level']
    rewrite_template_cookbook 'blueprint-proxy'
  end
end
