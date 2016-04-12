=begin
#<
This definition installs CoreMedia Blueprint tools.

@param path The path to install the tool. Defaults to `/opt/<name of definition>`
@param group_id The maven groupId of the tools artifact.
@param artifact_id The maven artifactId of the tools artifact.
@param version The maven version of the tools artifact.
@param checksum The SHA-256 checksum of the tools artifact.
@param java_home The JAVA_HOME to use for the tool.
@param jvm_args Extra JVM args to set for tool executions.
@param sensitive Set to true to disable template logging.
@param property_files A hash where the name of a property file below `properties/corem` can map to a hash of key value pairs representing the properties of that file. If present the file will be rendered with the hashes content.
@param user The user to install the tool for.
@param group The group to install the tool for.
@param log_dir The directory a symlink for the logs should be created for.
@param nexus_url The nexus base url if you want to retrieve artifacts using the rest API of nexus. This allows versions like `LATEST`, `RELEASE` and `x-SNAPSHOT` to be resolved correctly. (optional)
@param nexus_repo The nexus repo name. Use the part of the url not the numeric id. (optional defaults to `releases`)

@section Examples

```ruby
coremedia_tool 'content-management-server-tools' do
  group_id  'com.coremedia.blueprint'
  artifact_id 'content-management-server-tools'
  version '7.5-SNAPSHOT'
  user 'coremedia'
  group 'coremedia'
  path "/opt/coremedia/content-management-server-tools"
  property_files ('capclient.properties' => { 'repository.url' => 'http://localhost/40080/coremedia/ior' })
end
```

#>
=end

define :coremedia_tool, :java_home => '$JAVA_HOME', :jvm_args => [], :sensitive => false, :property_files => {} do
  fail("Argument group_id missing on coremedia_tool[#{params[:name]}]") unless params[:group_id]
  fail("Argument artifact_id missing on coremedia_tool[#{params[:name]}]") unless params[:artifact_id]
  fail("Argument version missing on coremedia_tool[#{params[:name]}]") unless params[:version]
  params[:path] ||= "/opt/#{params[:name]}"
  params[:user] ||= params[:name]
  params[:group] ||= params[:user]
  params[:property_files]
  params[:log_dir] ||= "/var/log/#{params[:name]}"
  tools_zip = "/var/tmp/#{params[:name]}-#{params[:artifact_id]}-#{params[:version]}.zip"

  directory params[:path] do
    owner params[:user]
    group params[:group]
    mode 0775
  end

  directory "#{params[:path]}/var" do
    owner params[:user]
    group params[:group]
    mode 0775
  end

  directory "#{params[:path]}/var/logs" do
    path "#{params[:path]}/var/logs"
    owner params[:user]
    group params[:group]
    mode 0775
  end

  coremedia_maven tools_zip do
    extract_to params[:path]
    extract_force_clean true
    group_id params[:group_id]
    artifact_id params[:artifact_id]
    version params[:version]
    packaging 'zip'
    checksum params[:checksum]
    repository_url params[:maven_repository_url]
    nexus_url params[:nexus_url] if params[:nexus_url]
    nexus_repo params[:nexus_repo] if params[:nexus_repo]
    owner params[:user]
    group params[:group]
  end

  %w(cm common.sh).each do |executable_file|
    file "#{params[:path]}/bin/#{executable_file}" do
      owner params[:user]
      group params[:group]
      mode 0775
    end
  end

  unless params[:property_files].empty?
    directory "#{params[:path]}/properties" do
      owner params[:user]
      group params[:group]
    end

    directory "#{params[:path]}/properties/corem" do
      owner params[:user]
      group params[:group]
    end

    params[:property_files].keys.select { |key| key =~ /.*\.properties/ }.each do |properties_file|
      template "#{params[:path]}/properties/corem/#{properties_file}" do
        source 'properties.erb'
        cookbook 'coremedia-tools'
        owner params[:user]
        group params[:group]
        variables(:props => params[:property_files][properties_file])
        sensitive params[:senstive]
      end
    end
  end

  template "#{params[:path]}/bin/pre-config.jpif" do
    source 'pre-config.jpif.erb'
    cookbook 'coremedia-tools'
    variables(:java_home => params[:java_home],
              :jvm_args => params[:jvm_args])
    owner params[:user]
    group params[:group]
  end

  link params[:log_dir] do
    to "#{params[:path]}/var/logs"
  end
end
