=begin
#<
This recipe installs and configures the CoreMedia Blueprint Solr Search Engine.

#>
=end

# include base recipe to make sure all dirs are set up
include_recipe 'blueprint-tomcat::_base'
service_name = 'solr'
# we cannot directly use the helper method in a definitions body, otherwise it gets evaluated too early
start_service = cm_tomcat_default(service_name, 'start_service')

# currently all files within the solr-config zip are prefixed with solr-home, therfore we need to extract the
# solr-home dir first to a separate folder and then move it on change.
solr_home = "#{node['blueprint']['base_dir']}/solr-home"
# temporary directory to store the new solr home configuration
solr_home_new = "#{node['blueprint']['cache_dir']}/solr-home-new"
# webapp is exploded so we set it in the webapp context config
node.force_default['blueprint']['webapps']['solr']['context_config']['env_entries']['solr/home']['value'] = solr_home
blueprint_tomcat_service service_name do
  skip_lifecycle true
end

webapp = cm_webapp(service_name)
tomcat = cm_tomcat(service_name)

coremedia_maven "#{node['blueprint']['cache_dir']}/solr-config.zip" do
  group_id node['blueprint']['webapps'][service_name]['config_zip_group_id']
  artifact_id node['blueprint']['webapps'][service_name]['config_zip_artifact_id']
  version node['blueprint']['webapps'][service_name]['config_zip_version']
  repository_url node['blueprint']['maven_repository_url']
  nexus_url node['blueprint']['nexus_url'] if node['blueprint']['nexus_url']
  nexus_repo node['blueprint']['nexus_repo']
  packaging 'zip'
  extract_to solr_home_new
  extract_force_clean true
  owner tomcat.user
  group tomcat.group
  # notify  tomcat to restart because of new solr config
  notifies :update, tomcat, :immediately
end

classes_dir = directory "#{webapp.path}/WEB-INF/classes" do
  owner service_name
  group node['blueprint']['group']
end

template "#{classes_dir.path}/log4j.properties" do
  source 'properties.erb'
  owner service_name
  group node['blueprint']['group']
  variables(:props => { 'log4j.rootLogger' => 'WARN, file',
                        'log4j.appender.file' => 'org.apache.log4j.RollingFileAppender',
                        'log4j.appender.file.MaxFileSize' => '4MB',
                        'log4j.appender.file.MaxBackupIndex' => '9',
                        'log4j.appender.file.File' => "#{tomcat.path}/current/logs/solr.log",
                        'log4j.appender.file.layout' => 'org.apache.log4j.PatternLayout',
                        'log4j.appender.file.layout.ConversionPattern' => '%d{yyyy-MM-dd HH:mm:ss.SSS} [%-5p] %C - %m%n',
                        'log4j.logger.com.coremedia' => 'INFO',
                        'log4j.logger.org.apache.solr.update.LoggingInfoStream' => 'OFF'
  })
  notifies :update, webapp, :immediately
end

template "#{classes_dir.path}/logging.properties" do
  source 'properties.erb'
  owner service_name
  group node['blueprint']['group']
  variables(:props => { 'handlers' => 'org.slf4j.bridge.SLF4JBridgeHandler' })
  notifies :update, webapp, :immediately
end

# this definition will shutdown and redeploy only on changes, this allows us to reinstall solr home when solr is down.
coremedia_tomcat_service_lifecycle "#{service_name}-shutdown only on change" do
  start_service false
  tomcat tomcat
  undeploy_unmanaged false
  webapps [webapp]
end

# TODO: keep indices on delete
execute 'update-solr-home' do
  command "rm -rf #{solr_home} && mv #{solr_home_new}/solr-home #{solr_home} && rm -rf #{solr_home_new}"
  user tomcat.user
  group tomcat.group
  only_if { ::File.exist?("#{solr_home_new}/solr-home") }
end

# start the tomcat again
coremedia_tomcat_service_lifecycle service_name do
  tomcat tomcat
  webapps [webapp]
  undeploy_unmanaged false
  start_service start_service
end
