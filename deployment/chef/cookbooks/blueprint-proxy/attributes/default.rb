rewrite_log_level = node['apache']['version'] == '2.4' ? 'trace1' : 0
#<> convenience property to configure a test system for local apache development against a remote system, do not set this attribute in recipes or use it in recipes, use the concrete attributes instead
default['blueprint']['proxy']['cms_host'] = 'localhost'

# set this to true to disable SSLProxyVerify, SSLProxyCheckPeerCN, SSLProxyCheckPeerName
default['blueprint']['proxy']['ssl_proxy_verify'] = true # TODO: move this to the shop config as it is only used in the shop proxy config
default['blueprint']['proxy']['virtual_host']['studio']['host'] = node['blueprint']['proxy']['cms_host']
default['blueprint']['proxy']['virtual_host']['studio']['port'] = '41080'
default['blueprint']['proxy']['virtual_host']['studio']['context'] = 'studio'
default['blueprint']['proxy']['virtual_host']['studio']['server_name'] = "studio.#{node['blueprint']['hostname']}"
default['blueprint']['proxy']['virtual_host']['studio']['rewrite_log_level'] = rewrite_log_level
default['blueprint']['proxy']['virtual_host']['studio']['server_aliases'] = %W(studio-helios.#{node['blueprint']['hostname']} studio-corporate.#{node['blueprint']['hostname']})

default['blueprint']['proxy']['virtual_host']['preview']['cluster']['default']['host'] = node['blueprint']['proxy']['cms_host']
default['blueprint']['proxy']['virtual_host']['preview']['cluster']['default']['port'] = '40980'
default['blueprint']['proxy']['virtual_host']['preview']['context'] = 'blueprint'
default['blueprint']['proxy']['virtual_host']['preview']['rewrite_log_level'] = rewrite_log_level
default['blueprint']['proxy']['virtual_host']['preview']['sites']['helios']['server_name'] = "preview-helios.#{node['blueprint']['hostname']}"
default['blueprint']['proxy']['virtual_host']['preview']['sites']['helios']['server_aliases'] = ['preview-fragment.supplier.blueprint-box.vagrant']
default['blueprint']['proxy']['virtual_host']['preview']['sites']['helios']['default_site'] = 'corporate'
default['blueprint']['proxy']['virtual_host']['preview']['sites']['corporate']['server_name'] = "preview-corporate.#{node['blueprint']['hostname']}"
default['blueprint']['proxy']['virtual_host']['preview']['sites']['corporate']['default_site'] = 'corporate'

default['blueprint']['proxy']['virtual_host']['delivery']['cluster']['default']['host'] = node['blueprint']['proxy']['cms_host']
default['blueprint']['proxy']['virtual_host']['delivery']['cluster']['default']['port'] = '42180'
default['blueprint']['proxy']['virtual_host']['delivery']['context'] = 'blueprint'
default['blueprint']['proxy']['virtual_host']['delivery']['rewrite_log_level'] = rewrite_log_level
default['blueprint']['proxy']['virtual_host']['delivery']['sites']['helios']['server_name'] = "helios.#{node['blueprint']['hostname']}"
default['blueprint']['proxy']['virtual_host']['delivery']['sites']['helios']['server_aliases'] = ['fragment.supplier.blueprint-box.vagrant']
default['blueprint']['proxy']['virtual_host']['delivery']['sites']['helios']['default_site'] = 'corporate'
default['blueprint']['proxy']['virtual_host']['delivery']['sites']['helios']['sitemap_site_name'] = 'Corporate'
default['blueprint']['proxy']['virtual_host']['delivery']['sites']['corporate']['server_name'] = "corporate.#{node['blueprint']['hostname']}"
default['blueprint']['proxy']['virtual_host']['delivery']['sites']['corporate']['default_site'] = 'corporate'
default['blueprint']['proxy']['virtual_host']['delivery']['sites']['corporate']['sitemap_site_name'] = 'Corporate'

default['blueprint']['proxy']['virtual_host']['sitemanager']['host'] = node['blueprint']['proxy']['cms_host']
default['blueprint']['proxy']['virtual_host']['sitemanager']['port'] = '41380'
default['blueprint']['proxy']['virtual_host']['sitemanager']['context'] = 'editor-webstart'
default['blueprint']['proxy']['virtual_host']['sitemanager']['server_name'] = "sitemanager.#{node['blueprint']['hostname']}"
default['blueprint']['proxy']['virtual_host']['sitemanager']['server_aliases'] = ["editor.#{node['blueprint']['hostname']}"]
default['blueprint']['proxy']['virtual_host']['sitemanager']['cms_ior_url'] = "http://#{node['blueprint']['proxy']['cms_host']}:41080/coremedia/ior"
default['blueprint']['proxy']['virtual_host']['sitemanager']['wfs_ior_url'] = "http://#{node['blueprint']['proxy']['cms_host']}:43080/workflow/ior"
default['blueprint']['proxy']['virtual_host']['sitemanager']['rewrite_log_level'] = rewrite_log_level

default['blueprint']['proxy']['virtual_host']['shop']['server_name'] = "shop-helios.#{node['blueprint']['hostname']}"
default['blueprint']['proxy']['virtual_host']['shop']['server_aliases'] = []
default['blueprint']['proxy']['virtual_host']['shop']['rewrite_log_level'] = rewrite_log_level

default['blueprint']['proxy']['virtual_host']['shop-preview']['server_name'] = "shop-preview-production-helios.#{node['blueprint']['hostname']}"
default['blueprint']['proxy']['virtual_host']['shop-preview']['time_travel_alias'] = "shop-preview-helios.#{node['blueprint']['hostname']}"
# default['blueprint']['proxy']['virtual_host']['shop-preview']['wcs_tools_alias_map']["shop-tools.#{node['blueprint']['hostname']}"] = 'lobtools'
# default['blueprint']['proxy']['virtual_host']['shop-preview']['wcs_tools_alias_map']["shop-admin.#{node['blueprint']['hostname']}"] = 'webapp/wcs/admin/servlet/ToolsLogon?XMLFile=adminconsole.AdminConsoleLogon'
# default['blueprint']['proxy']['virtual_host']['shop-preview']['wcs_tools_alias_map']["shop-orgadmin.#{node['blueprint']['hostname']}"] = 'webapp/wcs/orgadmin/servlet/ToolsLogon?XMLFile=buyerconsole.BuyAdminConsoleLogon&storeId=0'
default['blueprint']['proxy']['virtual_host']['shop-preview']['server_aliases'] = []
default['blueprint']['proxy']['virtual_host']['shop-preview']['rewrite_log_level'] = rewrite_log_level

default['blueprint']['proxy']['virtual_host']['adobe-drive-server']['host'] = node['blueprint']['proxy']['cms_host']
default['blueprint']['proxy']['virtual_host']['adobe-drive-server']['port'] = '41180'
default['blueprint']['proxy']['virtual_host']['adobe-drive-server']['context'] = 'drive'
default['blueprint']['proxy']['virtual_host']['adobe-drive-server']['server_name'] = "drive.#{node['blueprint']['hostname']}"
default['blueprint']['proxy']['virtual_host']['adobe-drive-server']['rewrite_log_level'] = rewrite_log_level
