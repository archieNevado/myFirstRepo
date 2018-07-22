#<> Convenience property to set the hostname of sfcc. Do not use or set this attribute in recipes, use the concrete attributes instead.
default['blueprint']['lc3-sfcc']['host'] = 'localhost'
default['blueprint']['lc3-sfcc']['application.properties']['livecontext.sfcc.host'] = node['blueprint']['lc3-sfcc']['host']
default['blueprint']['lc3-sfcc']['application.properties']['livecontext.sfcc.vendorVersion'] = '17.8'
default['blueprint']['lc3-sfcc']['application.properties']['livecontext.sfcc.ocapi.protocol'] = 'https'
default['blueprint']['lc3-sfcc']['application.properties']['livecontext.sfcc.ocapi.version'] = 'v17_8'
default['blueprint']['lc3-sfcc']['application.properties']['livecontext.sfcc.ocapi.dataBasePath'] = '/s/-/dw/data/'
default['blueprint']['lc3-sfcc']['application.properties']['livecontext.sfcc.ocapi.metaBasePath'] = '/s/-/dw/meta/'
default['blueprint']['lc3-sfcc']['application.properties']['livecontext.sfcc.ocapi.shopBasePath'] = '/s/{storeId}/dw/shop/'
default['blueprint']['lc3-sfcc']['application.properties']['livecontext.sfcc.oauth.clientId'] = 'clientId'
default['blueprint']['lc3-sfcc']['application.properties']['livecontext.sfcc.oauth.clientPassword'] = 'clientPassword'

default['blueprint']['lc3-sfcc']['virtual_host']['delivery']['cluster']['default']['host'] = node['blueprint']['lc3-sfcc']['cms_host']
default['blueprint']['lc3-sfcc']['virtual_host']['delivery']['cluster']['default']['port'] = '42180'
default['blueprint']['lc3-sfcc']['virtual_host']['delivery']['context'] = 'blueprint'
default['blueprint']['lc3-sfcc']['virtual_host']['delivery']['rewrite_log_level'] = 'trace1'
default['blueprint']['lc3-sfcc']['virtual_host']['delivery']['sites']['sitegenesis']['server_name'] = "sitegenesis.#{node['blueprint']['hostname']}"
default['blueprint']['lc3-sfcc']['virtual_host']['delivery']['sites']['sitegenesis']['default_site'] = 'sitegenesishomepage'
#<> The id property of the CMSite content associated with this site
default['blueprint']['lc3-sfcc']['virtual_host']['delivery']['sites']['sitegenesis']['site_id'] = 'SFCC-sitegenesis-UK-Site-ID'

default['blueprint']['lc3-sfcc']['virtual_host']['shop-preview']['server_name'] = "shop-preview-sitegenesis.#{node['blueprint']['hostname']}"
default['blueprint']['lc3-sfcc']['virtual_host']['shop-preview']['time_travel_alias'] = "shop-preview-sitegenesis.#{node['blueprint']['hostname']}"
default['blueprint']['lc3-sfcc']['virtual_host']['shop-preview']['server_aliases'] = []
default['blueprint']['lc3-sfcc']['virtual_host']['shop-preview']['rewrite_log_level'] = 'trace1'

default['blueprint']['lc3-sfcc']['virtual_host']['shop']['server_name'] = "shop-sitegenesis.#{node['blueprint']['hostname']}"
default['blueprint']['lc3-sfcc']['virtual_host']['shop']['time_travel_alias'] = "shop-sitegenesis.#{node['blueprint']['hostname']}"
default['blueprint']['lc3-sfcc']['virtual_host']['shop']['server_aliases'] = []
default['blueprint']['lc3-sfcc']['virtual_host']['shop']['rewrite_log_level'] = 'trace1'

# set this to true to disable SSLProxyVerify, SSLProxyCheckPeerCN, SSLProxyCheckPeerName
default['blueprint']['lc3-sfcc']['ssl_proxy_verify'] = true

# set salesforce host application_property studioUrlWhiteList for candy setup
default['blueprint']['proxy']['candy_properties']['studio']['studio.previewUrlWhitelist'] = "http://localhost:40980,*.coremedia.vm:40980,*.coremedia.vm,*.coremedia.com,*.#{node['blueprint']['hostname']}, http://#{node['blueprint']['lc3-sfcc']['host']}, https://#{node['blueprint']['lc3-sfcc']['host']}"
default['blueprint']['proxy']['candy_properties']['studio-preview']['studio.previewUrlWhitelist'] = "http://localhost:40980,*.coremedia.vm:40980,*.coremedia.vm,*.coremedia.com,*.#{node['blueprint']['hostname']},http://#{node['blueprint']['lc3-sfcc']['host']},https://#{node['blueprint']['lc3-sfcc']['host']}"
