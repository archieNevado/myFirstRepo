include_recipe 'blueprint-base::default'
include_recipe 'blueprint-tools::_base'
coremedia_tool 'caefeeder-preview' do
  path node['blueprint']['tools']['caefeeder-preview']['dir']
  group_id node['blueprint']['tools']['caefeeder-preview']['group_id']
  artifact_id node['blueprint']['tools']['caefeeder-preview']['artifact_id']
  version node['blueprint']['tools']['caefeeder-preview']['version']
  checksum node['blueprint']['tools']['caefeeder-preview']['checksum']
  user node['blueprint']['user']
  group node['blueprint']['group']
  update_snapshots true
  java_home node['blueprint']['tools']['java_home']
  property_files node['blueprint']['tools']['caefeeder-preview']['property_files']
  maven_repository_url node['blueprint']['maven_repository_url']
  nexus_url node['blueprint']['nexus_url'] if node['blueprint']['nexus_url']
  nexus_repo node['blueprint']['nexus_repo']
  sensitive true
end
