# Description

This is the application cookbook to deploy CoreMedia Blueprint Tools. This cookbook uses the tools definition of the `coremedia-tools` cookbook.

# Requirements

## Platform:

*No platforms defined*

## Cookbooks:

* blueprint-base
* coremedia-tools (~> 1.0)

# Attributes

* `node['blueprint']['tools']['java_home']` -  Defaults to `/usr/lib/jvm/java`.
* `node['blueprint']['tools']['content-management-server']['dir']` -  Defaults to `#{node['blueprint']['base_dir']}/content-management-server-tools`.
* `node['blueprint']['tools']['content-management-server']['property_files']['capclient.properties']['cap.client.server.ior.url']` -  Defaults to `http://localhost:40180/coremedia/ior`.
* `node['blueprint']['tools']['content-management-server']['property_files']['capclient.properties']['cap.client.timezone.default']` -  Defaults to `Europe/Berlin`.
* `node['blueprint']['tools']['css-importer']['dir']` -  Defaults to `#{node['blueprint']['base_dir']}/css-importer-tools`.
* `node['blueprint']['tools']['css-importer']['property_files']['css-importer.properties']['import.multiResultGeneratorFactory.property.inbox']` -  Defaults to `#{node['blueprint']['base_dir']}/css-importer-inbox`.
* `node['blueprint']['tools']['css-importer']['property_files']['css-importer.properties']['import.transformer.10.property.sourcepath']` -  Defaults to `#{node['blueprint']['base_dir']}/css-importer-inbox`.
* `node['blueprint']['tools']['css-importer']['property_files']['css-importer.properties']['import.transformer.10.property.targetpath']` -  Defaults to `/Themes`.
* `node['blueprint']['tools']['css-importer']['property_files']['capclient.properties']` -  Defaults to `node['blueprint']['tools']['content-management-server']['property_files']['capclient.properties']`.
* `node['blueprint']['tools']['master-live-server']['dir']` -  Defaults to `#{node['blueprint']['base_dir']}/master-live-server-tools`.
* `node['blueprint']['tools']['master-live-server']['property_files']['capclient.properties']['cap.client.server.ior.url']` -  Defaults to `http://localhost:40280/coremedia/ior`.
* `node['blueprint']['tools']['master-live-server']['property_files']['capclient.properties']['cap.client.timezone.default']` -  Defaults to `Europe/Berlin`.
* `node['blueprint']['tools']['replication-live-server']['dir']` -  Defaults to `#{node['blueprint']['base_dir']}/replication-live-server-tools`.
* `node['blueprint']['tools']['replication-live-server']['property_files']['capclient.properties']['cap.client.server.ior.url']` -  Defaults to `http://localhost:40280/coremedia/ior`.
* `node['blueprint']['tools']['replication-live-server']['property_files']['capclient.properties']['cap.client.timezone.default']` -  Defaults to `Europe/Berlin`.
* `node['blueprint']['tools']['workflow-server']['dir']` -  Defaults to `#{node['blueprint']['base_dir']}/workflow-server-tools`.
* `node['blueprint']['tools']['workflow-server']['property_files']['capclient.properties']` -  Defaults to `node['blueprint']['tools']['content-management-server']['property_files']['capclient.properties']`.
* `node['blueprint']['tools']['caefeeder-preview']['dir']` -  Defaults to `#{node['blueprint']['base_dir']}/caefeeder-preview-tools`.
* `node['blueprint']['tools']['caefeeder-preview']['property_files']['resetcaefeeder.properties']['jdbc.driver']` -  Defaults to `com.mysql.jdbc.Driver`.
* `node['blueprint']['tools']['caefeeder-preview']['property_files']['resetcaefeeder.properties']['jdbc.url']` -  Defaults to `jdbc:mysql://localhost:3306/cm_mcaefeeder`.
* `node['blueprint']['tools']['caefeeder-preview']['property_files']['resetcaefeeder.properties']['jdbc.user']` -  Defaults to `cm_mcaefeeder`.
* `node['blueprint']['tools']['caefeeder-preview']['property_files']['resetcaefeeder.properties']['jdbc.password']` -  Defaults to `cm_mcaefeeder`.
* `node['blueprint']['tools']['caefeeder-live']['dir']` -  Defaults to `#{node['blueprint']['base_dir']}/caefeeder-live-tools`.
* `node['blueprint']['tools']['caefeeder-live']['property_files']['resetcaefeeder.properties']['jdbc.driver']` -  Defaults to `com.mysql.jdbc.Driver`.
* `node['blueprint']['tools']['caefeeder-live']['property_files']['resetcaefeeder.properties']['jdbc.url']` -  Defaults to `jdbc:mysql://localhost:3306/cm_caefeeder`.
* `node['blueprint']['tools']['caefeeder-live']['property_files']['resetcaefeeder.properties']['jdbc.user']` -  Defaults to `cm_caefeeder`.
* `node['blueprint']['tools']['caefeeder-live']['property_files']['resetcaefeeder.properties']['jdbc.password']` -  Defaults to `cm_caefeeder`.

# Recipes

* blueprint-tools::caefeeder-live-tools
* blueprint-tools::caefeeder-preview-tools
* blueprint-tools::content-management-server-tools
* blueprint-tools::css-importer-tools
* [blueprint-tools::default](#blueprint-toolsdefault) - This recipe installs all tools.
* blueprint-tools::master-live-server-tools
* blueprint-tools::replication-live-server-tools
* blueprint-tools::workflow-server-tools

## blueprint-tools::default

This recipe installs all tools.

# Author

Author:: Your Name (<your_name@domain.com>)
