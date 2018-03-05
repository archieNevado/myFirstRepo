# Description

This is the wrapper cookbook to deploy CoreMedia Blueprint
Livecontext for Salesforce Commerce Cloud.

# Requirements

## Platform:

*No platforms defined*

## Cookbooks:

* blueprint-base
* blueprint-tomcat
* blueprint-proxy
* coremedia-proxy (~> 0.3.2)

# Attributes

* `node['blueprint']['lc3-sfcc']['host']` - Convenience property to set the hostname of SFCC. Do not use or set this attribute in recipes, use the concrete attributes instead. Defaults to `localhost`.
* `node['blueprint']['lc3-sfcc']['application.properties']['livecontext.sfcc.host']` -  Defaults to `localhost`.
* `node['blueprint']['lc3-sfcc']['application.properties']['livecontext.sfcc.vendorVersion']` -  Defaults to `17.8`.
* `node['blueprint']['lc3-sfcc']['application.properties']['livecontext.sfcc.ocapi.protocol']` -  Defaults to `https`.
* `node['blueprint']['lc3-sfcc']['application.properties']['livecontext.sfcc.ocapi.version']` -  Defaults to `v17_8`.
* `node['blueprint']['lc3-sfcc']['application.properties']['livecontext.sfcc.ocapi.dataBasePath']` -  Defaults to `/s/-/dw/data/`.
* `node['blueprint']['lc3-sfcc']['application.properties']['livecontext.sfcc.ocapi.metaBasePath']` -  Defaults to `/s/-/dw/meta/`.
* `node['blueprint']['lc3-sfcc']['application.properties']['livecontext.sfcc.ocapi.shopBasePath']` -  Defaults to `/s/-/dw/shop/`.
* `node['blueprint']['lc3-sfcc']['application.properties']['livecontext.sfcc.oauth.clientId']` -  Defaults to `clientId`.
* `node['blueprint']['lc3-sfcc']['application.properties']['livecontext.sfcc.oauth.clientPassword']` -  Defaults to `clientPassword`.
* `node['blueprint']['lc3-sfcc']['virtual_host']['delivery']['cluster']['default']['host']` -  Defaults to `node['blueprint']['lc3-sfcc']['cms_host']`.
* `node['blueprint']['lc3-sfcc']['virtual_host']['delivery']['cluster']['default']['port']` -  Defaults to `42180`.
* `node['blueprint']['lc3-sfcc']['virtual_host']['delivery']['context']` -  Defaults to `blueprint`.
* `node['blueprint']['lc3-sfcc']['virtual_host']['delivery']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['lc3-sfcc']['virtual_host']['delivery']['sites']['sitegenesis']['server_name']` -  Defaults to `sitegenesis.#{node['blueprint']['hostname']}`.
* `node['blueprint']['lc3-sfcc']['virtual_host']['delivery']['sites']['sitegenesis']['default_site']` -  Defaults to `sitegenesishomepage`.
* `node['blueprint']['lc3-sfcc']['virtual_host']['delivery']['sites']['sitegenesis']['site_id']` - The id property of the CMSite content associated with this site. Defaults to `sitegenesis-UK-Site-ID`.
* `node['blueprint']['lc3-sfcc']['virtual_host']['shop-preview']['server_name']` -  Defaults to `shop-preview-sitegenesis.#{node['blueprint']['hostname']}`.
* `node['blueprint']['lc3-sfcc']['virtual_host']['shop-preview']['time_travel_alias']` -  Defaults to `shop-preview-sitegenesis.#{node['blueprint']['hostname']}`.
* `node['blueprint']['lc3-sfcc']['virtual_host']['shop-preview']['server_aliases']` -  Defaults to `[ ... ]`.
* `node['blueprint']['lc3-sfcc']['virtual_host']['shop-preview']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['lc3-sfcc']['virtual_host']['shop']['server_name']` -  Defaults to `shop-sitegenesis.#{node['blueprint']['hostname']}`.
* `node['blueprint']['lc3-sfcc']['virtual_host']['shop']['time_travel_alias']` -  Defaults to `shop-sitegenesis.#{node['blueprint']['hostname']}`.
* `node['blueprint']['lc3-sfcc']['virtual_host']['shop']['server_aliases']` -  Defaults to `[ ... ]`.
* `node['blueprint']['lc3-sfcc']['virtual_host']['shop']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['lc3-sfcc']['ssl_proxy_verify']` -  Defaults to `true`.

# Recipes

* [blueprint-lc3-sfcc::cae-live-config](#blueprint-lc3-sfcccae-live-config) - This recipe configures the CoreMedia Blueprint Live CAE.
* [blueprint-lc3-sfcc::cae-preview-config](#blueprint-lc3-sfcccae-preview-config) - This recipe configures the CoreMedia Blueprint Preview CAE.
* blueprint-lc3-sfcc::candy-shop-preview-proxy
* [blueprint-lc3-sfcc::delivery-proxy](#blueprint-lc3-sfccdelivery-proxy) - This recipe installs virtual hosts for the CoreMedia Blueprint Live CAE.
* blueprint-lc3-sfcc::overview
* [blueprint-lc3-sfcc::shop-preview-proxy](#blueprint-lc3-sfccshop-preview-proxy) - This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext SFCC Preview Shop.
* [blueprint-lc3-sfcc::shop-proxy](#blueprint-lc3-sfccshop-proxy) - This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext SFCC Live Shop.
* [blueprint-lc3-sfcc::studio-config](#blueprint-lc3-sfccstudio-config) - This recipe configures the CoreMedia Blueprint Studio.
* [blueprint-lc3-sfcc::studio-proxy](#blueprint-lc3-sfccstudio-proxy) - This recipe installs virtual hosts for the CoreMedia Blueprint Studio.
* [blueprint-lc3-sfcc::test-data-config](#blueprint-lc3-sfcctest-data-config)

## blueprint-lc3-sfcc::cae-live-config

This recipe configures the CoreMedia Blueprint Live CAE.

## blueprint-lc3-sfcc::cae-preview-config

This recipe configures the CoreMedia Blueprint Preview CAE.

## blueprint-lc3-sfcc::delivery-proxy

This recipe installs virtual hosts for the CoreMedia Blueprint Live CAE.

## blueprint-lc3-sfcc::shop-preview-proxy

This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext SFCC Preview Shop.

## blueprint-lc3-sfcc::shop-proxy

This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext SFCC Live Shop.

## blueprint-lc3-sfcc::studio-config

This recipe configures the CoreMedia Blueprint Studio.

## blueprint-lc3-sfcc::studio-proxy

This recipe installs virtual hosts for the CoreMedia Blueprint Studio.

## blueprint-lc3-sfcc::test-data-config

This recipe sets properties only necessary if the test content is being used

# Author

Author:: Your Name (<your_name@domain.com>)
