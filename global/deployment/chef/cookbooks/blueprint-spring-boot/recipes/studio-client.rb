include_recipe 'blueprint-base'

# if you want to change this name make sure to change it in the proxy config in blueprint-proxy too
service_name = 'studio-client'
service_group = node['blueprint']['group']
service_owner = node['blueprint']['user']
service_dir = "#{node['blueprint']['base_dir']}/#{service_name}"

directory service_dir do
  owner service_owner
  group service_group
  recursive true
end

coremedia_maven "#{service_dir}/studio-resources.jar" do
  group_id node['blueprint']['apps'][service_name]['group_id']
  artifact_id node['blueprint']['apps'][service_name]['artifact_id']
  version node['blueprint']['apps'][service_name]['version']
  packaging 'jar'
  repository_url node['blueprint']['maven_repository_url']
  nexus_url node['blueprint']['nexus_url'] if node['blueprint']['nexus_url']
  nexus_repo node['blueprint']['nexus_repo'] if node['blueprint']['nexus_repo']
  group service_group
  owner service_owner
  extract_to "#{service_dir}/www"
  extract_force_clean true
end
