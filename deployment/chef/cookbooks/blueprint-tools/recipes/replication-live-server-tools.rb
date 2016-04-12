include_recipe 'blueprint-base::default'
coremedia_tool 'replication-live-server' do
  path node['blueprint']['tools']['replication-live-server']['dir']
  group_id node['blueprint']['tools']['replication-live-server']['group_id']
  artifact_id node['blueprint']['tools']['replication-live-server']['artifact_id']
  version node['blueprint']['tools']['replication-live-server']['version']
  checksum node['blueprint']['tools']['replication-live-server']['checksum']
  user node['blueprint']['user']
  group node['blueprint']['group']
  update_snapshots true
  java_home node['blueprint']['tools']['java_home']
  property_files node['blueprint']['tools']['replication-live-server']['property_files']
  maven_repository_url node['blueprint']['maven_repository_url']
  nexus_url node['blueprint']['nexus_url'] if node['blueprint']['nexus_url']
  nexus_repo node['blueprint']['nexus_repo']
  sensitive true
end
