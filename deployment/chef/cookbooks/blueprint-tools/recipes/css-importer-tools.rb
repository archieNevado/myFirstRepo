include_recipe 'blueprint-base::default'
include_recipe 'blueprint-tools::_base'
coremedia_tool 'css-importer' do
  path node['blueprint']['tools']['css-importer']['dir']
  group_id node['blueprint']['tools']['css-importer']['group_id']
  artifact_id node['blueprint']['tools']['css-importer']['artifact_id']
  version node['blueprint']['tools']['css-importer']['version']
  checksum node['blueprint']['tools']['css-importer']['checksum']
  user node['blueprint']['user']
  group node['blueprint']['group']
  update_snapshots true
  java_home node['blueprint']['tools']['java_home']
  property_files node['blueprint']['tools']['css-importer']['property_files']
  maven_repository_url node['blueprint']['maven_repository_url']
  nexus_url node['blueprint']['nexus_url'] if node['blueprint']['nexus_url']
  nexus_repo node['blueprint']['nexus_repo']
  sensitive true
end
