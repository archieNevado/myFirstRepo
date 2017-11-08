# WCS configuration
#<> Convenience property to set the hostname of wcs. Do not use or set this attribute in recipes, use the concrete attributes instead.
default['blueprint']['lc3-ibm-wcs']['host'] = 'localhost'
#<> convenience property to configure a test system for local apache development against a remote system, do not set this attribute in recipes or use it in recipes, use the concrete attributes instead
default['blueprint']['lc3-ibm-wcs']['cms_host'] = 'localhost'
default['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.service.credentials.username'] = 'cmadmin'
default['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.service.credentials.password'] = 'VTJjyo0AYSnXFHI201yo'
default['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.cookie.domain'] = ".#{node['fqdn']}"
default['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.ibm.wcs.host'] = node['blueprint']['lc3-ibm-wcs']['host']
default['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.ibm.wcs.url-keyword'] = 'cm'
default['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.ibm.wcs.store.name.aurora'] = 'AuroraESite'
default['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.ibm.wcs.currency.aurora'] = 'USD'
default['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.ibm.wcs.vendor.aurora'] = 'ibm'

#<> convenience property to workaround CMS-9339
default['blueprint']['lc3-ibm-wcs']['application.properties']['blueprint.host.helios'] = "preview.#{node['blueprint']['hostname']}"

# The following properties are derived from `livecontext.ibm.wcs.host`, if you need to set them explicitly comment in the lines below
# default['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.ibm.wcs.url'] = "http://#{node['blueprint']['lc3-ibm-wcs']['host']}"
# default['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.ibm.wcs.secureUrl'] = "https://#{node['blueprint']['lc3-ibm-wcs']['host']}"
# default['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.ibm.wcs.rest.search.url'] = "http://#{node['blueprint']['lc3-ibm-wcs']['host']}:3737/search/resources"
# default['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.ibm.wcs.rest.search.secureUrl'] = "https://#{node['blueprint']['lc3-ibm-wcs']['host']}:3738/search/previewresources"
# default['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.managementtool.web.url'] = "https://#{node['blueprint']['lc3-ibm-wcs']['host']}:8000/lobtools/CoreMediaManagementCenterWrapper.html"

# set this to true to disable SSLProxyVerify, SSLProxyCheckPeerCN, SSLProxyCheckPeerName
default['blueprint']['lc3-ibm-wcs']['ssl_proxy_verify'] = true
rewrite_log_level = node['apache']['version'] == '2.4' ? 'trace1' : 0

default['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['cluster']['default']['host'] = node['blueprint']['lc3-ibm-wcs']['cms_host']
default['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['cluster']['default']['port'] = '42180'
default['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['context'] = 'blueprint'

# Commerceled-led site, the id property of the CMSite content associated with this site_id
default['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['rewrite_log_level'] = rewrite_log_level
default['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['sites']['helios']['server_name'] = "helios.#{node['blueprint']['hostname']}"
default['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['sites']['helios']['server_aliases'] = ['fragment.supplier.blueprint-box.vagrant']
default['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['sites']['helios']['default_site'] = 'aurora'
default['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['sites']['helios']['site_id'] = '99c8ef576f385bc322564d5694df6fc2'

# Content-led site
default['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['sites']['calista']['server_name'] = "calista.#{node['blueprint']['hostname']}"
default['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['sites']['calista']['server_aliases'] = ['fragment.supplier.blueprint-box.vagrant']
default['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['sites']['calista']['default_site'] = 'calista'
default['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['sites']['calista']['site_id'] = 'ced8921aa7b7f9b736b90e19afc2dd2a'

default['blueprint']['lc3-ibm-wcs']['virtual_host']['shop']['server_name'] = "shop-helios.#{node['blueprint']['hostname']}"
default['blueprint']['lc3-ibm-wcs']['virtual_host']['shop']['server_aliases'] = []
default['blueprint']['lc3-ibm-wcs']['virtual_host']['shop']['rewrite_log_level'] = rewrite_log_level

default['blueprint']['lc3-ibm-wcs']['virtual_host']['shop-preview']['server_name'] = "shop-preview-production-helios.#{node['blueprint']['hostname']}"
default['blueprint']['lc3-ibm-wcs']['virtual_host']['shop-preview']['time_travel_alias'] = "shop-preview-helios.#{node['blueprint']['hostname']}"
default['blueprint']['lc3-ibm-wcs']['virtual_host']['shop-preview']['server_aliases'] = []
default['blueprint']['lc3-ibm-wcs']['virtual_host']['shop-preview']['rewrite_log_level'] = rewrite_log_level
# there is only one preview, we need to register an alias for the shop-preview-production fragment augmentation
default['blueprint']['proxy']['virtual_host']['preview']['server_aliases']['lc3-ibm-wcs'] = 'preview-fragment.supplier.blueprint-box.vagrant'
