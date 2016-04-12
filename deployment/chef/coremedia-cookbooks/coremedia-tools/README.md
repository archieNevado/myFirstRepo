# Description

This is a library cookbook to install CoreMedia Tools. It provides a definition to install arbitrary tools created with
the `coremedia-application-maven-plugin` based on the `application-runtime` artifact of CoreMedia.

# Filesystem layout

```
<log_dir> --> <path>/var/logs
<path>
    |-bin
    |-lib
    `-var/logs
```

# Requirements

## Platform:

*No platforms defined*

## Cookbooks:

* coremedia_maven (~> 2.0)

# Attributes

*No attributes defined*

# Recipes

*No recipes defined*

# Definitions

* [coremedia_tool](#coremedia_tool) - This definition installs CoreMedia Blueprint tools.

## coremedia_tool

This definition installs CoreMedia Blueprint tools.

### Parameters

- path: The path to install the tool. Defaults to `/opt/<name of definition>`.
- group_id: The maven groupId of the tools artifact..
- artifact_id: The maven artifactId of the tools artifact..
- version: The maven version of the tools artifact..
- checksum: The SHA-256 checksum of the tools artifact..
- java_home: The JAVA_HOME to use for the tool.. Defaults to: $JAVA_HOME
- jvm_args: Extra JVM args to set for tool executions.. Defaults to: []
- sensitive: Set to true to disable template logging.. Defaults to: nil
- property_files: A hash where the name of a property file below `properties/corem` can map to a hash of key value pairs representing the properties of that file. If present the file will be rendered with the hashes content.. Defaults to: {}
- user: The user to install the tool for..
- group: The group to install the tool for..
- log_dir: The directory a symlink for the logs should be created for..
- nexus_url: The nexus base url if you want to retrieve artifacts using the rest API of nexus. This allows versions like `LATEST`, `RELEASE` and `x-SNAPSHOT` to be resolved correctly. (optional).
- nexus_repo: The nexus repo name. Use the part of the url not the numeric id. (optional defaults to `releases`).

### Examples

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
# Author

Author:: Felix Simmendinger (<felix.simmendinger@coremedia.com>)
