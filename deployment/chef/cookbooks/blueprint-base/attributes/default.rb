#<> The basic blueprint user, i.e. for tool execution
default['blueprint']['user'] = 'coremedia'
#<> The basic blueprint group
default['blueprint']['group'] = 'coremedia'
#<> The base dir to install all apps to
default['blueprint']['base_dir'] = '/opt/coremedia'
#<> The directory to log files to
default['blueprint']['log_dir'] = '/var/log/coremedia'
#<> The directory for coremedia caches
default['blueprint']['cache_dir'] = '/var/cache/coremedia'
#<> The directory for coremedia temporary files
default['blueprint']['temp_dir'] = '/var/tmp/coremedia'
#<> The repository url from which the webapp artifacts are downloaded
default['blueprint']['maven_repository_url'] = 'file://localhost/maven-repo/'
#<> The nexus base url. Set this to use the nexus REST API to retrieve artifacts. Using it allows you to resolve versions like `RELEASE`, `LATEST` and `X-SNAPSHOT` correctly.
default['blueprint']['nexus_url'] = nil
#<> The nexus repo name to retrieve the artifacts via REST API.
default['blueprint']['nexus_repo'] = 'public'
#<> Convenience property to set the hostname used for public available urls in case of all components installed on the same host. Do not use or set this attribute in recipes, use the concrete attributes instead.
default['blueprint']['hostname'] = node['fqdn']

#<> Convenience property to set the version of all coremedia artifacts. For individual versions you can still set a version attribute beside each artifact_id attribute. Do not use or set this attribute in recipes, use the concrete attributes instead.
default['blueprint']['default_version'] = '1-SNAPSHOT'

# Make sure to keep solr version here and in blueprint in sync
default['blueprint']['webapps']['solr']['group_id'] = 'org.apache.solr'
default['blueprint']['webapps']['solr']['artifact_id'] = 'solr'
default['blueprint']['webapps']['solr']['version'] = '4.10.4'
default['blueprint']['webapps']['solr']['checksum'] = 'a956cdc35b4448a0c814bd8b55473a12bb50236e0e538793f0285c32f2fd8a6f'
default['blueprint']['webapps']['solr']['explode'] = true
default['blueprint']['webapps']['solr']['config_zip_group_id'] = 'com.coremedia.blueprint'
default['blueprint']['webapps']['solr']['config_zip_artifact_id'] = 'solr-config'
default['blueprint']['webapps']['solr']['config_zip_version'] = node['blueprint']['default_version']
default['blueprint']['webapps']['solr']['context'] = 'solr'

default['blueprint']['webapps']['content-management-server']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['webapps']['content-management-server']['artifact_id'] = 'content-management-server-webapp'
default['blueprint']['webapps']['content-management-server']['version'] = node['blueprint']['default_version']
default['blueprint']['webapps']['content-management-server']['context'] = 'coremedia'
default['blueprint']['webapps']['content-management-server']['explode'] = true

default['blueprint']['webapps']['content-feeder']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['webapps']['content-feeder']['artifact_id'] = 'content-feeder-webapp'
default['blueprint']['webapps']['content-feeder']['version'] = node['blueprint']['default_version']
default['blueprint']['webapps']['content-feeder']['context'] = 'contentfeeder'

default['blueprint']['webapps']['workflow-server']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['webapps']['workflow-server']['artifact_id'] = 'workflow-server-webapp'
default['blueprint']['webapps']['workflow-server']['version'] = node['blueprint']['default_version']
default['blueprint']['webapps']['workflow-server']['context'] = 'workflow'
default['blueprint']['webapps']['workflow-server']['explode'] = true

default['blueprint']['webapps']['user-changes']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['webapps']['user-changes']['artifact_id'] = 'user-changes-webapp'
default['blueprint']['webapps']['user-changes']['version'] = node['blueprint']['default_version']
default['blueprint']['webapps']['user-changes']['context'] = 'user-changes'

default['blueprint']['webapps']['elastic-worker']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['webapps']['elastic-worker']['artifact_id'] = 'elastic-worker-webapp'
default['blueprint']['webapps']['elastic-worker']['version'] = node['blueprint']['default_version']
default['blueprint']['webapps']['elastic-worker']['context'] = 'elastic-worker'

default['blueprint']['webapps']['caefeeder-preview']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['webapps']['caefeeder-preview']['artifact_id'] = 'caefeeder-preview-webapp'
default['blueprint']['webapps']['caefeeder-preview']['version'] = node['blueprint']['default_version']
default['blueprint']['webapps']['caefeeder-preview']['context'] = 'caefeeder'

default['blueprint']['webapps']['cae-preview']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['webapps']['cae-preview']['artifact_id'] = 'cae-preview-webapp'
default['blueprint']['webapps']['cae-preview']['version'] = node['blueprint']['default_version']
default['blueprint']['webapps']['cae-preview']['context'] = 'blueprint'

default['blueprint']['webapps']['studio']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['webapps']['studio']['artifact_id'] = 'studio-webapp'
default['blueprint']['webapps']['studio']['version'] = node['blueprint']['default_version']
default['blueprint']['webapps']['studio']['context'] = 'studio'

default['blueprint']['webapps']['sitemanager']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['webapps']['sitemanager']['artifact_id'] = 'editor-webstart-webapp'
default['blueprint']['webapps']['sitemanager']['version'] = node['blueprint']['default_version']
default['blueprint']['webapps']['sitemanager']['context'] = 'editor-webstart'
default['blueprint']['webapps']['sitemanager']['explode'] = true

default['blueprint']['webapps']['master-live-server']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['webapps']['master-live-server']['artifact_id'] = 'master-live-server-webapp'
default['blueprint']['webapps']['master-live-server']['version'] = node['blueprint']['default_version']
default['blueprint']['webapps']['master-live-server']['context'] = 'coremedia'
default['blueprint']['webapps']['master-live-server']['explode'] = true

default['blueprint']['webapps']['replication-live-server']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['webapps']['replication-live-server']['artifact_id'] = 'replication-live-server-webapp'
default['blueprint']['webapps']['replication-live-server']['version'] = node['blueprint']['default_version']
default['blueprint']['webapps']['replication-live-server']['context'] = 'coremedia'
default['blueprint']['webapps']['replication-live-server']['explode'] = true

default['blueprint']['webapps']['caefeeder-live']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['webapps']['caefeeder-live']['artifact_id'] = 'caefeeder-live-webapp'
default['blueprint']['webapps']['caefeeder-live']['version'] = node['blueprint']['default_version']
default['blueprint']['webapps']['caefeeder-live']['context'] = 'caefeeder'

default['blueprint']['webapps']['cae-live']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['webapps']['cae-live']['artifact_id'] = 'cae-live-webapp'
default['blueprint']['webapps']['cae-live']['version'] = node['blueprint']['default_version']
default['blueprint']['webapps']['cae-live']['context'] = 'blueprint'

#### Tools
default['blueprint']['tools']['content-management-server']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['tools']['content-management-server']['artifact_id'] = 'cms-tools-application'
default['blueprint']['tools']['content-management-server']['version'] = node['blueprint']['default_version']

default['blueprint']['tools']['workflow-server']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['tools']['workflow-server']['artifact_id'] = 'wfs-tools-application'
default['blueprint']['tools']['workflow-server']['version'] = node['blueprint']['default_version']

default['blueprint']['tools']['caefeeder-preview']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['tools']['caefeeder-preview']['artifact_id'] = 'caefeeder-tools-application'
default['blueprint']['tools']['caefeeder-preview']['version'] = node['blueprint']['default_version']

default['blueprint']['tools']['master-live-server']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['tools']['master-live-server']['artifact_id'] = 'mls-tools-application'
default['blueprint']['tools']['master-live-server']['version'] = node['blueprint']['default_version']

default['blueprint']['tools']['caefeeder-live']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['tools']['caefeeder-live']['artifact_id'] = 'caefeeder-tools-application'
default['blueprint']['tools']['caefeeder-live']['version'] = node['blueprint']['default_version']

default['blueprint']['tools']['replication-live-server']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['tools']['replication-live-server']['artifact_id'] = 'rls-tools-application'
default['blueprint']['tools']['replication-live-server']['version'] = node['blueprint']['default_version']

default['blueprint']['tools']['theme-importer']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['tools']['theme-importer']['artifact_id'] = 'theme-importer-application'
default['blueprint']['tools']['theme-importer']['version'] = node['blueprint']['default_version']

# common libs
default['blueprint']['common_libs']['coremedia-tomcat.jar']['group_id'] = 'com.coremedia.blueprint'
default['blueprint']['common_libs']['coremedia-tomcat.jar']['artifact_id'] = 'coremedia-tomcat'
default['blueprint']['common_libs']['coremedia-tomcat.jar']['version'] = node['blueprint']['default_version']

default['blueprint']['common_libs']['slf4j-api.jar']['group_id'] = 'org.slf4j'
default['blueprint']['common_libs']['slf4j-api.jar']['artifact_id'] = 'slf4j-api'
default['blueprint']['common_libs']['slf4j-api.jar']['version'] = '1.7.12'
default['blueprint']['common_libs']['slf4j-api.jar']['checksum'] = '0aee9a77a4940d72932b0d0d9557793f872e66a03f598e473f45e7efecdccf99'

default['blueprint']['common_libs']['jul-to-slf4j.jar']['group_id'] = 'org.slf4j'
default['blueprint']['common_libs']['jul-to-slf4j.jar']['artifact_id'] = 'jul-to-slf4j'
default['blueprint']['common_libs']['jul-to-slf4j.jar']['version'] = '1.7.12'
default['blueprint']['common_libs']['jul-to-slf4j.jar']['checksum'] = '00a25fcbec155c28152c2252aeadb0ace7947a519fe5d858dbb988de412b8529'

default['blueprint']['common_libs']['jcl-over-slf4j.jar']['group_id'] = 'org.slf4j'
default['blueprint']['common_libs']['jcl-over-slf4j.jar']['artifact_id'] = 'jcl-over-slf4j'
default['blueprint']['common_libs']['jcl-over-slf4j.jar']['version'] = '1.7.12'
default['blueprint']['common_libs']['jcl-over-slf4j.jar']['checksum'] = '702d1b805b9fa319609f7bdd7022017919be2280b4116666aaae5a01c0b50158'

default['blueprint']['common_libs']['slf4j-log4j12.jar']['group_id'] = 'org.slf4j'
default['blueprint']['common_libs']['slf4j-log4j12.jar']['artifact_id'] = 'slf4j-log4j12'
default['blueprint']['common_libs']['slf4j-log4j12.jar']['version'] = '1.7.12'
default['blueprint']['common_libs']['slf4j-log4j12.jar']['checksum'] = '84b96c9ab58313f44321bda0602408e79f33613c05a379b99a0000b24c4e6c3c'

default['blueprint']['common_libs']['log4j.jar']['group_id'] = 'log4j'
default['blueprint']['common_libs']['log4j.jar']['artifact_id'] = 'log4j'
default['blueprint']['common_libs']['log4j.jar']['version'] = '1.2.17'
default['blueprint']['common_libs']['log4j.jar']['checksum'] = '1d31696445697720527091754369082a6651bd49781b6005deb94e56753406f9'

##################
## JAAS CONFIG  ##
##################
# CROWD
default['blueprint']['jaas']['crowd']['enabled'] = false
default['blueprint']['jaas']['crowd']['properties']['crowd.domain'] = 'crowd'
default['blueprint']['jaas']['crowd']['properties']['crowd.expiration'] = '3599'
default['blueprint']['jaas']['crowd']['properties']['crowd.contentgroups'] = true
default['blueprint']['jaas']['crowd']['properties']['crowd.livegroups'] = false
default['blueprint']['jaas']['crowd']['properties']['crowd.admingroups'] = false
default['blueprint']['jaas']['crowd']['properties']['application.name'] = 'blueprint'
default['blueprint']['jaas']['crowd']['properties']['application.password'] = 'secret'
default['blueprint']['jaas']['crowd']['properties']['application.login.url'] = 'https://sso.coremedia.com:8443/crowd/console/'
default['blueprint']['jaas']['crowd']['properties']['crowd.server.url'] = 'https://sso.coremedia.com:8443/crowd/rest/'
default['blueprint']['jaas']['crowd']['properties']['crowd.base.url'] = 'https://sso.coremedia.com:8443/crowd/'
default['blueprint']['jaas']['crowd']['properties']['session.isauthenticated'] = 'session.isauthenticated'
default['blueprint']['jaas']['crowd']['properties']['session.tokenkey'] = 'session.tokenkey'
default['blueprint']['jaas']['crowd']['properties']['session.validationinterval'] = 2
default['blueprint']['jaas']['crowd']['properties']['session.lastvalidation'] = 'session.lastvalidation'
#LDAP
default['blueprint']['jaas']['ldap']['enabled'] = false
default['blueprint']['jaas']['ldap']['host'] = 'my.ldap.host'
default['blueprint']['jaas']['ldap']['port'] = 1212
default['blueprint']['jaas']['ldap']['domain'] = 'mydomain'
#CAS
default['blueprint']['jaas']['cas']['enabled'] = false
default['blueprint']['jaas']['cas']['validator_url'] = 'http://'
default['blueprint']['jaas']['cas']['cap_service_url'] = 'http://'

#<> The path to the libjpeg turbo installation to increase image transformation performance
default['blueprint']['libjpeg_turbo_path'] = '/opt/libjpeg-turbo/lib64'
