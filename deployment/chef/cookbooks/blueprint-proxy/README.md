# Description

This is an application cookbook, it provides recipes to set up a webserver (apache httpd) for the CoreMedia Blueprint stack.
# Requirements

## Platform:

*No platforms defined*

## Cookbooks:

* blueprint-base
* coremedia-proxy (~> 0.2)

# Attributes

* `node['blueprint']['proxy']['cms_host']` - convenience property to configure a test system for local apache development against a remote system, do not set this attribute in recipes or use it in recipes, use the concrete attributes instead. Defaults to `localhost`.
* `node['blueprint']['proxy']['ssl_proxy_verify']` -  Defaults to `true # TODO: move this to the shop config as it is only used in the shop proxy config`.
* `node['blueprint']['proxy']['virtual_host']['studio']['host']` -  Defaults to `node['blueprint']['proxy']['cms_host']`.
* `node['blueprint']['proxy']['virtual_host']['studio']['port']` -  Defaults to `41080`.
* `node['blueprint']['proxy']['virtual_host']['studio']['context']` -  Defaults to `studio`.
* `node['blueprint']['proxy']['virtual_host']['studio']['server_name']` -  Defaults to `studio.#{node['blueprint']['hostname']}`.
* `node['blueprint']['proxy']['virtual_host']['studio']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['proxy']['virtual_host']['studio']['server_aliases']` -  Defaults to `%W(studio-helios.#{node['blueprint']['hostname']} studio-corporate.#{node['blueprint']['hostname']})`.
* `node['blueprint']['proxy']['virtual_host']['preview']['cluster']['default']['host']` -  Defaults to `node['blueprint']['proxy']['cms_host']`.
* `node['blueprint']['proxy']['virtual_host']['preview']['cluster']['default']['port']` -  Defaults to `40980`.
* `node['blueprint']['proxy']['virtual_host']['preview']['context']` -  Defaults to `blueprint`.
* `node['blueprint']['proxy']['virtual_host']['preview']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['proxy']['virtual_host']['preview']['sites']['helios']['server_name']` -  Defaults to `preview-helios.#{node['blueprint']['hostname']}`.
* `node['blueprint']['proxy']['virtual_host']['preview']['sites']['helios']['server_aliases']` -  Defaults to `[ ... ]`.
* `node['blueprint']['proxy']['virtual_host']['preview']['sites']['helios']['default_site']` -  Defaults to `perfectchef`.
* `node['blueprint']['proxy']['virtual_host']['preview']['sites']['corporate']['server_name']` -  Defaults to `preview-corporate.#{node['blueprint']['hostname']}`.
* `node['blueprint']['proxy']['virtual_host']['preview']['sites']['corporate']['default_site']` -  Defaults to `corporate`.
* `node['blueprint']['proxy']['virtual_host']['delivery']['cluster']['default']['host']` -  Defaults to `node['blueprint']['proxy']['cms_host']`.
* `node['blueprint']['proxy']['virtual_host']['delivery']['cluster']['default']['port']` -  Defaults to `42180`.
* `node['blueprint']['proxy']['virtual_host']['delivery']['context']` -  Defaults to `blueprint`.
* `node['blueprint']['proxy']['virtual_host']['delivery']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['proxy']['virtual_host']['delivery']['sites']['helios']['server_name']` -  Defaults to `helios.#{node['blueprint']['hostname']}`.
* `node['blueprint']['proxy']['virtual_host']['delivery']['sites']['helios']['server_aliases']` -  Defaults to `[ ... ]`.
* `node['blueprint']['proxy']['virtual_host']['delivery']['sites']['helios']['default_site']` -  Defaults to `perfectchef`.
* `node['blueprint']['proxy']['virtual_host']['delivery']['sites']['helios']['sitemap_site_name']` -  Defaults to `PerfectChef`.
* `node['blueprint']['proxy']['virtual_host']['delivery']['sites']['corporate']['server_name']` -  Defaults to `corporate.#{node['blueprint']['hostname']}`.
* `node['blueprint']['proxy']['virtual_host']['delivery']['sites']['corporate']['default_site']` -  Defaults to `corporate`.
* `node['blueprint']['proxy']['virtual_host']['delivery']['sites']['corporate']['sitemap_site_name']` -  Defaults to `Corporate`.
* `node['blueprint']['proxy']['virtual_host']['sitemanager']['host']` -  Defaults to `node['blueprint']['proxy']['cms_host']`.
* `node['blueprint']['proxy']['virtual_host']['sitemanager']['port']` -  Defaults to `41380`.
* `node['blueprint']['proxy']['virtual_host']['sitemanager']['context']` -  Defaults to `editor-webstart`.
* `node['blueprint']['proxy']['virtual_host']['sitemanager']['server_name']` -  Defaults to `sitemanager.#{node['blueprint']['hostname']}`.
* `node['blueprint']['proxy']['virtual_host']['sitemanager']['server_aliases']` -  Defaults to `[ ... ]`.
* `node['blueprint']['proxy']['virtual_host']['sitemanager']['cms_ior_url']` -  Defaults to `http://#{node['blueprint']['proxy']['cms_host']}:41080/coremedia/ior`.
* `node['blueprint']['proxy']['virtual_host']['sitemanager']['wfs_ior_url']` -  Defaults to `http://#{node['blueprint']['proxy']['cms_host']}:43080/workflow/ior`.
* `node['blueprint']['proxy']['virtual_host']['sitemanager']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['proxy']['virtual_host']['shop']['server_name']` -  Defaults to `shop-helios.#{node['blueprint']['hostname']}`.
* `node['blueprint']['proxy']['virtual_host']['shop']['server_aliases']` -  Defaults to `[ ... ]`.
* `node['blueprint']['proxy']['virtual_host']['shop']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['proxy']['virtual_host']['shop-preview']['server_name']` -  Defaults to `shop-preview-production-helios.#{node['blueprint']['hostname']}`.
* `node['blueprint']['proxy']['virtual_host']['shop-preview']['time_travel_alias']` -  Defaults to `shop-preview-helios.#{node['blueprint']['hostname']}`.
* `node['blueprint']['proxy']['virtual_host']['shop-preview']['server_aliases']` -  Defaults to `[ ... ]`.
* `node['blueprint']['proxy']['virtual_host']['shop-preview']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['proxy']['virtual_host']['adobe-drive-server']['host']` -  Defaults to `node['blueprint']['proxy']['cms_host']`.
* `node['blueprint']['proxy']['virtual_host']['adobe-drive-server']['port']` -  Defaults to `41180`.
* `node['blueprint']['proxy']['virtual_host']['adobe-drive-server']['context']` -  Defaults to `drive`.
* `node['blueprint']['proxy']['virtual_host']['adobe-drive-server']['server_name']` -  Defaults to `drive.#{node['blueprint']['hostname']}`.
* `node['blueprint']['proxy']['virtual_host']['adobe-drive-server']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.

# Recipes

* [blueprint-proxy::adobe-drive-server](#blueprint-proxyadobe-drive-server) - This recipe installs virtual hosts for the CoreMedia Blueprint Adobe Drive Server.
* [blueprint-proxy::default](#blueprint-proxydefault) - This recipe wraps all recipes of this cookbook for apache running in front of tomcat.
* [blueprint-proxy::delivery](#blueprint-proxydelivery) - This recipe installs virtual hosts for the CoreMedia Blueprint Live CAE.
* [blueprint-proxy::preview](#blueprint-proxypreview) - This recipe installs virtual hosts for the CoreMedia Blueprint Preview CAE.
* [blueprint-proxy::shop-preview](#blueprint-proxyshop-preview) - This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Preview Shop.
* [blueprint-proxy::shop](#blueprint-proxyshop) - This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Live Shop.
* [blueprint-proxy::sitemanager](#blueprint-proxysitemanager) - This recipe installs virtual hosts for the CoreMedia Blueprint Sitemanager.
* [blueprint-proxy::studio](#blueprint-proxystudio) - This recipe installs virtual hosts for the CoreMedia Blueprint Studio.

## blueprint-proxy::adobe-drive-server

This recipe installs virtual hosts for the CoreMedia Blueprint Adobe Drive Server.

## blueprint-proxy::default

This recipe wraps all recipes of this cookbook for apache running in front of tomcat. To work with websphere add websphere recipe before this recipe.

## blueprint-proxy::delivery

This recipe installs virtual hosts for the CoreMedia Blueprint Live CAE.

## blueprint-proxy::preview

This recipe installs virtual hosts for the CoreMedia Blueprint Preview CAE.

## blueprint-proxy::shop-preview

This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Preview Shop.

## blueprint-proxy::shop

This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Live Shop.

## blueprint-proxy::sitemanager

This recipe installs virtual hosts for the CoreMedia Blueprint Sitemanager.

## blueprint-proxy::studio

This recipe installs virtual hosts for the CoreMedia Blueprint Studio.

# Author

Author:: Your Name (<your_name@domain.com>)
