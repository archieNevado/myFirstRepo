include_recipe 'blueprint-base::default'
include_recipe 'blueprint-tools::_base'
coremedia_tool 'master-live-server' do
  path node['blueprint']['tools']['master-live-server']['dir']
  group_id node['blueprint']['tools']['master-live-server']['group_id']
  artifact_id node['blueprint']['tools']['master-live-server']['artifact_id']
  version node['blueprint']['tools']['master-live-server']['version']
  checksum node['blueprint']['tools']['master-live-server']['checksum']
  user node['blueprint']['user']
  group node['blueprint']['group']
  update_snapshots true
  java_home node['blueprint']['tools']['java_home']
  property_files node['blueprint']['tools']['master-live-server']['property_files']
  maven_repository_url node['blueprint']['maven_repository_url']
  nexus_url node['blueprint']['nexus_url'] if node['blueprint']['nexus_url']
  nexus_repo node['blueprint']['nexus_repo']
  sensitive true
end
