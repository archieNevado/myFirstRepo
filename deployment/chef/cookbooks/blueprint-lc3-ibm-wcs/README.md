# Description

This is the wrapper cookbook to deploy CoreMedia Blueprint
Livecontext for IBM Websphere Commerce.

To enable WCS deployment overwrite the default roles with the ones found here below
`roles`.

# Requirements

## Platform:

*No platforms defined*

## Cookbooks:

* blueprint-base
* blueprint-tomcat
* blueprint-proxy
* coremedia-proxy (~> 1.0.0)

# Attributes

* `node['blueprint']['lc3-ibm-wcs']['host']` - Convenience property to set the hostname of wcs. Do not use or set this attribute in recipes, use the concrete attributes instead. Defaults to `localhost`.
* `node['blueprint']['lc3-ibm-wcs']['cms_host']` - convenience property to configure a test system for local apache development against a remote system, do not set this attribute in recipes or use it in recipes, use the concrete attributes instead. Defaults to `localhost`.
* `node['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.service.credentials.username']` -  Defaults to `cmadmin`.
* `node['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.service.credentials.password']` -  Defaults to `VTJjyo0AYSnXFHI201yo`.
* `node['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.cookie.domain']` -  Defaults to `.#{node['fqdn']}`.
* `node['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.ibm.wcs.host']` -  Defaults to `node['blueprint']['lc3-ibm-wcs']['host']`.
* `node['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.ibm.wcs.url-keyword']` -  Defaults to `cm`.
* `node['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.ibm.wcs.store.name.aurora']` -  Defaults to `AuroraESite`.
* `node['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.ibm.wcs.currency.aurora']` -  Defaults to `USD`.
* `node['blueprint']['lc3-ibm-wcs']['application.properties']['livecontext.ibm.wcs.vendor.aurora']` -  Defaults to `ibm`.
* `node['blueprint']['lc3-ibm-wcs']['application.properties']['blueprint.host.calista']` - convenience property to workaround CMS-9339. Defaults to `preview.#{node['blueprint']['hostname']}`.
* `node['blueprint']['lc3-ibm-wcs']['application.properties']['blueprint.host.helios']` -  Defaults to `preview.#{node['blueprint']['hostname']}`.
* `node['blueprint']['lc3-ibm-wcs']['ssl_proxy_verify']` -  Defaults to `true`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['cluster']['default']['host']` -  Defaults to `node['blueprint']['lc3-ibm-wcs']['cms_host']`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['cluster']['default']['port']` -  Defaults to `42180`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['context']` -  Defaults to `blueprint`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['rewrite_log_level']` -  Defaults to `trace1`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['sites']['helios']['server_name']` -  Defaults to `helios.#{node['blueprint']['hostname']}`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['sites']['helios']['server_aliases']` -  Defaults to `[ ... ]`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['sites']['helios']['default_site']` -  Defaults to `aurora`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['sites']['helios']['site_id']` -  Defaults to `99c8ef576f385bc322564d5694df6fc2`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['sites']['calista']['server_name']` -  Defaults to `calista.#{node['blueprint']['hostname']}`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['sites']['calista']['server_aliases']` -  Defaults to `[ ... ]`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['sites']['calista']['default_site']` -  Defaults to `calista`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['delivery']['sites']['calista']['site_id']` -  Defaults to `ced8921aa7b7f9b736b90e19afc2dd2a`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop']['server_name']` -  Defaults to `shop-helios.#{node['blueprint']['hostname']}`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop']['server_aliases']` -  Defaults to `[ ... ]`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop']['rewrite_log_level']` -  Defaults to `trace1`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop-preview']['server_name']` -  Defaults to `shop-preview-production-helios.#{node['blueprint']['hostname']}`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop-preview']['time_travel_alias']` -  Defaults to `shop-preview-helios.#{node['blueprint']['hostname']}`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop-preview']['server_aliases']` -  Defaults to `[ ... ]`.
* `node['blueprint']['lc3-ibm-wcs']['virtual_host']['shop-preview']['rewrite_log_level']` -  Defaults to `trace1`.
* `node['blueprint']['proxy']['virtual_host']['preview']['server_aliases']['lc3-ibm-wcs']` -  Defaults to `preview-fragment.supplier.blueprint-box.vagrant`.

# Recipes

* blueprint-lc3-ibm-wcs::cae-live-config
* [blueprint-lc3-ibm-wcs::cae-preview-config](#blueprint-lc3-ibm-wcscae-preview-config) - This recipe installs and configures the CoreMedia Blueprint Preview CAE.
* blueprint-lc3-ibm-wcs::candy-shop-preview-proxy
* [blueprint-lc3-ibm-wcs::delivery-proxy](#blueprint-lc3-ibm-wcsdelivery-proxy) - This recipe installs virtual hosts for the CoreMedia Blueprint Live CAE.
* blueprint-lc3-ibm-wcs::overview
* [blueprint-lc3-ibm-wcs::shop-preview-proxy](#blueprint-lc3-ibm-wcsshop-preview-proxy) - This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Preview Shop.
* [blueprint-lc3-ibm-wcs::shop-proxy](#blueprint-lc3-ibm-wcsshop-proxy) - This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Live Shop.
* [blueprint-lc3-ibm-wcs::studio-config](#blueprint-lc3-ibm-wcsstudio-config) - This configures the CoreMedia Blueprint Studio.
* [blueprint-lc3-ibm-wcs::test-data-config](#blueprint-lc3-ibm-wcstest-data-config)

## blueprint-lc3-ibm-wcs::cae-preview-config

This recipe installs and configures the CoreMedia Blueprint Preview CAE.

## blueprint-lc3-ibm-wcs::delivery-proxy

This recipe installs virtual hosts for the CoreMedia Blueprint Live CAE.

## blueprint-lc3-ibm-wcs::shop-preview-proxy

This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Preview Shop.

## blueprint-lc3-ibm-wcs::shop-proxy

This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Live Shop.

## blueprint-lc3-ibm-wcs::studio-config

This configures the CoreMedia Blueprint Studio. To install the Studio, the `blueprint-tomcat::studio` has to be listed after this recipe

## blueprint-lc3-ibm-wcs::test-data-config

This recipe sets properties only necessary if the test content is being used

# Author

Author:: Your Name (<your_name@domain.com>)
