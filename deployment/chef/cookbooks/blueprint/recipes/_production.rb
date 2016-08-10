# You cannot use the convenience property node['blueprint']['wcs']['host'] here, it is being used in attribute files as well. chicken egg problem.
# we may move these properties from blueprint-base attribute files to the environments default recipe to clear things up.
wcs_host = 'YOUR WCS HOST'
node.default['blueprint']['wcs']['application.properties']['livecontext.ibm.wcs.url'] = "http://#{wcs_host}"
node.default['blueprint']['wcs']['application.properties']['livecontext.ibm.wcs.secureUrl'] = "https://#{wcs_host}"
node.default['blueprint']['wcs']['application.properties']['livecontext.ibm.wcs.host'] = wcs_host
node.default['blueprint']['wcs']['application.properties']['livecontext.ibm.wcs.rest.search.url'] = "http://#{wcs_host}:3737/search/resources"
node.default['blueprint']['wcs']['application.properties']['livecontext.ibm.wcs.rest.search.secureUrl'] = "https://#{wcs_host}:3738/search/previewresources"

node.default['blueprint']['webapps']['content-management-server']['application.properties']['sql.store.driver'] = 'com.mysql.jdbc.Driver'
node.default['blueprint']['webapps']['content-management-server']['application.properties']['sql.store.url'] = 'jdbc:mysql://localhost:3306/cm_management'
node.default['blueprint']['webapps']['content-management-server']['application.properties']['sql.store.dbProperties'] = 'corem/mysql'

node.default['blueprint']['webapps']['master-live-server']['application.properties']['sql.store.driver'] = 'com.mysql.jdbc.Driver'
node.default['blueprint']['webapps']['master-live-server']['application.properties']['sql.store.url'] = 'jdbc:mysql://localhost:3306/cm_master'
node.default['blueprint']['webapps']['master-live-server']['application.properties']['sql.store.dbProperties'] = 'corem/mysql'

node.default['blueprint']['webapps']['workflow-server']['application.properties']['sql.store.driver'] = node['blueprint']['webapps']['master-live-server']['application.properties']['sql.store.driver']
node.default['blueprint']['webapps']['workflow-server']['application.properties']['sql.store.url'] = node['blueprint']['webapps']['master-live-server']['application.properties']['sql.store.url']
node.default['blueprint']['webapps']['workflow-server']['application.properties']['sql.store.dbProperties'] = node['blueprint']['webapps']['master-live-server']['application.properties']['sql.store.dbProperties']

node.default['blueprint']['webapps']['replication-live-server']['application.properties']['sql.store.driver'] = 'com.mysql.jdbc.Driver'
node.default['blueprint']['webapps']['replication-live-server']['application.properties']['sql.store.url'] = 'jdbc:mysql://localhost:3306/cm_replication'
node.default['blueprint']['webapps']['replication-live-server']['application.properties']['sql.store.dbProperties'] = 'corem/mysql'

node.default['blueprint']['webapps']['caefeeder-preview']['application.properties']['jdbc.driver'] = 'com.mysql.jdbc.Driver'
node.default['blueprint']['webapps']['caefeeder-preview']['application.properties']['jdbc.url'] = 'jdbc:mysql://localhost:3306/cm_mcaefeeder'

node.default['blueprint']['webapps']['caefeeder-live']['application.properties']['jdbc.driver'] = 'com.mysql.jdbc.Driver'
node.default['blueprint']['webapps']['caefeeder-live']['application.properties']['jdbc.url'] = 'jdbc:mysql://localhost:3306/cm_caefeeder'

# REPO CONFIGURATION
node.default['blueprint']['maven_repository_url'] = 'http://your.maven.repo'
# set your nexus url and repo here, if you want to use the nexus rest api.
node.default['blueprint']['nexus_url'] = nil
# there should be a repo that groups your snapshot, your releases and a central mirror repo together, we call it public but you may use an arbitrary name
node.default['blueprint']['nexus_repo'] = 'public'
# VERSIONS
node.default['blueprint']['webapps']['content-management-server']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['webapps']['master-live-server']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['webapps']['workflow-server']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['webapps']['replication-live-server']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['webapps']['caefeeder-preview']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['webapps']['caefeeder-live']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['webapps']['content-feeder']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['webapps']['elastic-worker']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['webapps']['user-changes']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['webapps']['studio']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['webapps']['cae-preview']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['webapps']['cae-live']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['webapps']['sitemanager']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['webapps']['adobe-drive-server']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['webapps']['solr']['version'] = '4.10.4'
node.default['blueprint']['webapps']['solr']['config_zip_version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['tools']['caefeeder-preview']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['tools']['caefeeder-live']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['tools']['content-management-server']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['tools']['master-live-server']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['tools']['workflow-server']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['tools']['theme-importer']['version'] = 'ENTER CONCRETE VERSION HERE'
node.default['blueprint']['tools']['replication-live-server']['version'] = 'ENTER CONCRETE VERSION HERE'
