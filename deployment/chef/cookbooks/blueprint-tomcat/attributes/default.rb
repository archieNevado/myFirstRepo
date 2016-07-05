#<> The download url to the tomcat zip, make sure the version attribute matches. Set to nil to use the default url based on the version attribute.
default['blueprint']['tomcat']['source'] = nil
#<> The SHA-256 checksum of the tomcat installation zip
default['blueprint']['tomcat']['source_checksum'] = 'f093d033cb84e104f9f00f120dc6f3b39471d8e12be8fc250374ac9891c257b1'
#<> The version of tomcat to install
default['blueprint']['tomcat']['version'] = '7.0.69'
#<> The path to the java home for the tomcat services
default['blueprint']['tomcat']['java_home'] = '/usr/lib/jvm/java'
#<> Global jvm agent opts. Use this to instrument the jvm for monitoring
default['blueprint']['tomcat']['catalina_opts']['agent'] = ''
#<> Global jvm garbage collection flags
default['blueprint']['tomcat']['catalina_opts']['gc'] = '-XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSClassUnloadingEnabled -XX:+UseMembar'
#<> Global jvm network system properties, the defaults disable IPv4 because IPv6 loopback over localhost currently does not work behind an apache
default['blueprint']['tomcat']['catalina_opts']['network'] = '-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses'

# comment the following line in to disable asynchronous spring context initialization
#default['blueprint']['tomcat']['catalina_opts']['sprint_context_start'] = '-Dcom.coremedia.springframework.web.context.disableAsynchronousLoading=true'

# webapps rendering using libjpeg_turbo
unless node['blueprint']['libjpeg_turbo_path'].nil?
  default['blueprint']['tomcat']['cae-preview']['catalina_opts']['libjpeg'] = "-Djava.library.path=#{node['blueprint']['libjpeg_turbo_path']}"
  default['blueprint']['tomcat']['cae-live']['catalina_opts']['libjpeg'] = "-Djava.library.path=#{node['blueprint']['libjpeg_turbo_path']}"
  default['blueprint']['tomcat']['studio']['catalina_opts']['libjpeg'] = "-Djava.library.path=#{node['blueprint']['libjpeg_turbo_path']}"
end

#<> A flag to enable/disable the jmx remote connector
default['blueprint']['tomcat']['jmx_remote'] = true
#<> The download url to the jar, make sure the version attribute matches. Set to nil to use the default url based on the version attribute.
default['blueprint']['tomcat']['jmx_remote_jar_source'] = nil
#<> The SHA-256 checksum of the catalina-jmx-remote.jar
default['blueprint']['tomcat']['jmx_remote_jar_source_checksum'] = '5fe0c568afe6f24998817bfb9e6a77e1b430112a52ed914d48a1f9a401f86fb1'
#<> A flag to enable/disable remote jmx authentication
default['blueprint']['tomcat']['jmx_remote_authenticate'] = true
#<> The server name under which the rmi server is registered. Set it to localhost and create a ssh tunnel(recommended) or set it to the actual hostname and open the ports and configure security and ssl.
default['blueprint']['tomcat']['jmx_remote_server_name'] = node['fqdn']
#<> The password for the monitoring jmx role.
default['blueprint']['tomcat']['jmx_remote_monitor_password'] = 'monitor'
#<> The password for the control (modify) jmx role.
default['blueprint']['tomcat']['jmx_remote_control_password'] = 'control'

#<> Start Services can be overridden using a more specific key, i.e. default['blueprint']['tomcat']['workflow-server']['start_service'] = false
default['blueprint']['tomcat']['start_service'] = true

#<> This flag will force tomcat to kill the process using -KILL when shutdown_wait threshold is reached.
default['blueprint']['tomcat']['shutdown_force'] = true
#<> The time to wait for tomcat to shut down
default['blueprint']['tomcat']['shutdown_wait'] = 40
#<> Set to true to delete logs before start
default['blueprint']['tomcat']['clean_log_dir_on_start'] = false

#<> The failed context shutdown listener to stop tomcat if the context failed.
default['blueprint']['tomcat']['context_config']['listener']['shutdown_listener']['className'] = 'com.coremedia.tomcat.FailedContextShutdownServerListener'
#<> The global session cookie name
default['blueprint']['tomcat']['context_config']['session_cookie_name'] = 'CM_SESSIONID'

# HEAP
default['blueprint']['tomcat']['content-management-server']['heap'] = '512m'
default['blueprint']['tomcat']['workflow-server']['heap'] = '384m'
default['blueprint']['tomcat']['master-live-server']['heap'] = '384m'
default['blueprint']['tomcat']['replication-live-server']['heap'] = '256m'
default['blueprint']['tomcat']['user-changes']['heap'] = '256m'
default['blueprint']['tomcat']['elastic-worker']['heap'] = '256m'
default['blueprint']['tomcat']['content-feeder']['heap'] = '256m'
default['blueprint']['tomcat']['studio']['heap'] = '512m'
default['blueprint']['tomcat']['adobe-drive-server']['heap'] = '256m'
default['blueprint']['tomcat']['cae-preview']['heap'] = '512m'
default['blueprint']['tomcat']['cae-live']['heap'] = '512m'
default['blueprint']['tomcat']['solr']['heap'] = '256m'
default['blueprint']['tomcat']['caefeeder-preview']['heap'] = '256m'
default['blueprint']['tomcat']['caefeeder-live']['heap'] = '256m'
default['blueprint']['tomcat']['sitemanager']['heap'] = '92m'

# PERM
default['blueprint']['tomcat']['content-management-server']['perm'] = '128m'
default['blueprint']['tomcat']['workflow-server']['perm'] = '92m'
default['blueprint']['tomcat']['master-live-server']['perm'] = '92m'
default['blueprint']['tomcat']['replication-live-server']['perm'] = '92m'
default['blueprint']['tomcat']['user-changes']['perm'] = '92m'
default['blueprint']['tomcat']['elastic-worker']['perm'] = '92m'
default['blueprint']['tomcat']['content-feeder']['perm'] = '92m'
default['blueprint']['tomcat']['studio']['perm'] = '92m'
default['blueprint']['tomcat']['adobe-drive-server']['perm'] = '92m'
default['blueprint']['tomcat']['cae-preview']['perm'] = '128m'
default['blueprint']['tomcat']['cae-live']['perm'] = '128m'
default['blueprint']['tomcat']['solr']['perm'] = '92m'
default['blueprint']['tomcat']['caefeeder-preview']['perm'] = '92m'
default['blueprint']['tomcat']['caefeeder-live']['perm'] = '92m'
default['blueprint']['tomcat']['sitemanager']['perm'] = '64m'

# Port Prefixe
default['blueprint']['tomcat']['solr']['port_prefix'] = 400
default['blueprint']['tomcat']['content-management-server']['port_prefix'] = 401
default['blueprint']['tomcat']['master-live-server']['port_prefix'] = 402
default['blueprint']['tomcat']['workflow-server']['port_prefix'] = 403
default['blueprint']['tomcat']['content-feeder']['port_prefix'] = 404
default['blueprint']['tomcat']['user-changes']['port_prefix'] = 405
default['blueprint']['tomcat']['elastic-worker']['port_prefix'] = 406
default['blueprint']['tomcat']['caefeeder-preview']['port_prefix'] = 407
default['blueprint']['tomcat']['caefeeder-live']['port_prefix'] = 408
default['blueprint']['tomcat']['cae-preview']['port_prefix'] = 409
default['blueprint']['tomcat']['studio']['port_prefix'] = 410
default['blueprint']['tomcat']['adobe-drive-server']['port_prefix'] = 411
default['blueprint']['tomcat']['sitemanager']['port_prefix'] = 413
default['blueprint']['tomcat']['replication-live-server']['port_prefix'] = 420
# here its the starting port cae-live-2 will automatically have prefix 422
default['blueprint']['tomcat']['cae-live']['port_prefix'] = 421

# start_priority
default['blueprint']['tomcat']['solr']['start_priority'] = 81
default['blueprint']['tomcat']['content-management-server']['start_priority'] = 81
default['blueprint']['tomcat']['master-live-server']['start_priority'] = 81
default['blueprint']['tomcat']['workflow-server']['start_priority'] = 82
default['blueprint']['tomcat']['replication-live-server']['start_priority'] = 82
default['blueprint']['tomcat']['user-changes']['start_priority'] = 82
default['blueprint']['tomcat']['elastic-worker']['start_priority'] = 82
default['blueprint']['tomcat']['content-feeder']['start_priority'] = 82
default['blueprint']['tomcat']['studio']['start_priority'] = 83
default['blueprint']['tomcat']['adobe-drive-server']['start_priority'] = 82
default['blueprint']['tomcat']['cae-preview']['start_priority'] = 82
default['blueprint']['tomcat']['cae-live']['start_priority'] = 83
default['blueprint']['tomcat']['caefeeder-preview']['start_priority'] = 82
default['blueprint']['tomcat']['caefeeder-live']['start_priority'] = 83
default['blueprint']['tomcat']['sitemanager']['start_priority'] = 83

#<> number of live caes to install on this node
default['blueprint']['tomcat']['cae-live']['instances'] = 1
#<> There should only be one cae generating the sitemaps, by default this is cae-live-1
default['blueprint']['tomcat']['cae-live-1']['sitemap']['enabled'] = true
#<> The time when the sitemap should be created, see blueprint sitemap documentation for property blueprint.sitemap.starttime
default['blueprint']['tomcat']['cae-live-1']['sitemap']['start_time'] = '+200'

#################################
##     Common & Shared Libs    ##
#################################
default['blueprint']['tomcat']['common_libs']['coremedia-tomcat.jar'] = node['blueprint']['common_libs']['coremedia-tomcat.jar']
default['blueprint']['tomcat']['common_libs']['slf4j-api.jar'] = node['blueprint']['common_libs']['slf4j-api.jar']
default['blueprint']['tomcat']['common_libs']['jul-to-slf4j.jar'] = node['blueprint']['common_libs']['jul-to-slf4j.jar']
default['blueprint']['tomcat']['common_libs']['jcl-over-slf4j.jar'] = node['blueprint']['common_libs']['jcl-over-slf4j.jar']
default['blueprint']['tomcat']['common_libs']['slf4j-log4j12.jar'] = node['blueprint']['common_libs']['slf4j-log4j12.jar']
default['blueprint']['tomcat']['common_libs']['log4j.jar'] = node['blueprint']['common_libs']['log4j.jar']
