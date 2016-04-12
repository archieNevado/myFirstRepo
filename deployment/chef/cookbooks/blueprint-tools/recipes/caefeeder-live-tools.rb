include_recipe 'blueprint-base::default'
coremedia_tool 'caefeeder-live' do
  path node['blueprint']['tools']['caefeeder-live']['dir']
  group_id node['blueprint']['tools']['caefeeder-live']['group_id']
  artifact_id node['blueprint']['tools']['caefeeder-live']['artifact_id']
  version node['blueprint']['tools']['caefeeder-live']['version']
  checksum node['blueprint']['tools']['caefeeder-live']['checksum']
  user node['blueprint']['user']
  group node['blueprint']['group']
  update_snapshots true
  java_home node['blueprint']['tools']['java_home']
  property_files node['blueprint']['tools']['caefeeder-live']['property_files']
  maven_repository_url node['blueprint']['maven_repository_url']
  nexus_url node['blueprint']['nexus_url'] if node['blueprint']['nexus_url']
  nexus_repo node['blueprint']['nexus_repo']
  sensitive true
end
