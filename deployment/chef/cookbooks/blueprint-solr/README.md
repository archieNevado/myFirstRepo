# Description

This is an application cookbook. It provides recipes to install:

* solr (>=6.0.0)

# Requirements

## Platform:

* redhat (~> 7.0)
* centos (~> 7.0)
* amazon (~> 7.0)

## Cookbooks:

* java_se (~> 8.112.0)
* coremedia_maven (~> 2.0)

# Attributes

* `node['blueprint']['solr']['version']` - define solr version to use. Defaults to `6.6.0`.
* `node['blueprint']['solr']['url']` - define solr download url. Defaults to `http://archive.apache.org/dist/lucene/solr/#{node['blueprint']['solr']['version']}/solr-#{node['blueprint']['solr']['version']}.tgz`.
* `node['blueprint']['solr']['checksum']` - define artifact checksum. Defaults to `6b1d1ed0b74aef320633b40a38a790477e00d75b56b9cdc578533235315ffa1e`.
* `node['blueprint']['solr']['solr_home']` - define solr home. Defaults to `/opt/coremedia/solr-home`.
* `node['blueprint']['solr']['solr_data_dir']` - define solr index data directory. Defaults to `/var/coremedia/solr-data`.
* `node['blueprint']['solr']['dir']` - define solr dir. Defaults to `/opt/solr`.
* `node['blueprint']['solr']['port']` - define solr port. Defaults to `40080`.
* `node['blueprint']['solr']['jmx_port']` - define solr JMX RMI port. Defaults to `40099`.
* `node['blueprint']['solr']['jmx_enable']` - define whether Solr should activate the JMX RMI connector to allow remote JMX client applications to connect. Defaults to `false`.
* `node['blueprint']['solr']['pid_dir']` - define solr pid dir. Defaults to `/var/run/solr/`.
* `node['blueprint']['solr']['log_dir']` - define solr log dir. Defaults to `/var/log/solr/`.
* `node['blueprint']['solr']['user']` - define solr user. Defaults to `solr`.
* `node['blueprint']['solr']['group']` - define solr group. Defaults to `solr`.
* `node['blueprint']['solr']['install_java']` - set to true to install java. Defaults to `false`.
* `node['blueprint']['solr']['java_home']` - set to the java home used for solr. Defaults to `/usr/lib/jvm/java`.
* `node['blueprint']['solr']['java_mem']` - define solr_java options. Defaults to `-Xms128M -Xmx512M`.
* `node['blueprint']['solr']['config_zip_group_id']` - define the maven group id for solr-config.zip. Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['solr']['config_zip_artifact_id']` - define the maven artifact id for solr-config.zip. Defaults to `solr-config`.
* `node['blueprint']['solr']['config_zip_version']` - define the maven version for solr-config.zip. Defaults to `node['blueprint']['default_version'] ? node['blueprint']['default_version'] : '1-SNAPSHOT`.
* `node['blueprint']['maven_repository_url']` - The repository url from which the webapp artifacts are downloaded. Defaults to `file://localhost/maven-repo/`.

# Recipes

* blueprint-solr::default

# Author

Author:: Daniel Zabel (<daniel.zabel@coremedia.com>)

Author:: Angelina Velinska (<angelina.velinska@coremedia.com>)
