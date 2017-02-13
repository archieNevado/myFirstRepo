How-To
========

## Configure http proxy

In the `.kitchen.yml` file, comment out the lines starting with `http_proxy` `https_proxy` and `no_proxy`:

```yaml
provisioner:
  name: chef_solo
  require_chef_omnibus: "12.8.1"
  chef_omnibus_install_options: -d /tmp/vagrant-cache/chef-omnibus
  solo_rb:
    environment: development
# Uncomment the following lines to configure a proxy
#    http_proxy: http://192.168.252.200:8123
#    https_proxy: http://192.168.252.200:8123
#    no_proxy: localhost,127.0.0.1
```


## Use local Maven artifacts

To use local artifacts, make sure you've installed the artifacts into your Maven repository and the path of this repository 
is matched by the `synced_folders` settings in your kitchen file, that is, if your Maven repository is located at `C:/myMavenRepo` the 
kitchen file should look like this:

```yaml
synced_folders:
  - ["C:/myMavenRepo", "/maven-repo"]
```   

The default should work fine for all users that keep their Maven repo at the default location `~/.m2/repository`.
      
## Use remote maven artifacts

To use remote Maven artifacts, you need to set the attribute `node['blueprint']['maven_repository_url']` and set the version attributes
correctly. To set the version for all artifacts, use the convenience attribute `node['blueprint']['default_version']`.
     
```yaml
suites:
- name: default
  run_list:
    - recipe[foo::bar]
  attributes:
    blueprint:
      maven_repository_url: 'http://my-nexus-repo.com/repo'
      default_version: '6.5.2'
```

## Use Nexus artifacts

One caveat of using Chefs remote_file resource to retrieve artifacts is using metaversions. Metaversions are versions where 
a part or the hole version string needs to be resolved to produce the final version. There are three different metaversions in
Maven:
 
* `X-SNAPSHOT` where `SNAPSHOT` gets resolved to the latest timestamp the artifact was deployed to.
* `LATEST` will be resolved to the latest deployed artifact. This can be either a `SNAPSHOT` or a fix release version.
* `RELEASE` will be resolved to the latest deployed fix release version. 

To use these metaversions, you need a Nexus and its REST API. Configure `node['blueprint']['nexus_url']` to your Nexus URL and
`node['blueprint']['nexus_repo']` to the repo name from which all artifacts are to be retrieved from. When you retrieve a mix of
snapshot and release version within the same Chef run, you need to configure a group repo in Nexus that groups your snapshot, your release
and your mirrors under one umbrella URL. If you do not want to do so, you need to set the repo name on each Chef resource that retrieves
an artifact using the `coremedia_maven` resource.
 
For convenience, each webapp, tool, common_lib or shared_lib attribute hash allows to override the global repo name using the `nexus_repo` key.

i.e.

```ruby
# set this in the environment recipe of the blueprint cookbook, i.e. staging.rb
node.default['blueprint']['nexus_url'] = 'http://mynexus.mycompany.org/nexus'
# this will set the default repo to be the releases repo. In development environment or a qa environment, this can be something different.
node.default['blueprint']['nexus_repo'] = 'releases'
# this will use the latest release of the content-management-server
node.default['blueprint']['webapps']['content-management-server']['version'] = 'RELEASE'
# this will set the central mirror repo, here named central for the common lib log4j.jar
node.default['blueprint']['common_libs']['log4j.jar']['nexus_repo'] = 'central'
```

## Use remote content artifact

To use a remote content artifact, you need to configure the `node['blueprint']['dev']['content']['content_zip`]` attribute, i.e.:

```yaml
suites:
- name: default
  run_list:
    - recipe[foo::bar]
  attributes:
    blueprint:
      dev:
        content:
          content_zip: http://my-jenkins.de/job/myjob/ws/test-data/target/content-users.zip
```

## Enable debugging

To enable debugging for a service, simply set the debug flag for it. The port will be determined by the services three digit
prefix and `06` as suffix. If, for example, you want to enable debugging for the studio, you need to set the debug flag to true and
then use this `40906` to connect via jdwp. 

```yaml
suites:
- name: default
  run_list:
    - recipe[foo::bar]
  attributes:
    blueprint:
      tomcat:
        studio:
          debug: true 
```
    
## Set log level

You can overwrite the log configuration provided by the artifact using Chef. For development purposes, you can set the 
level in the `.kitchen.yml` file. You can set it globally or for each webapp i.e. :

```yaml
suites:
- name: default
  run_list:
    - recipe[foo::bar]
  attributes:
    blueprint:
      tomcat:
        logback_config:
          logger:
            com.coremedia: warn
        studio:
          logback_config:
            logger:
              com.coremedia: info
              com.coremedia.ui: debug
```
    
## Change name of Virtual Box VMs

In order to change the names of the Virtual Box VMs, you need to change either the name of the test suite (default is `default`) or the name
of the platforms (default is `centos6-vagrant` or `centos7-vagrant`). You cannot set the name directly, as it is calculated from platform and suite name.

## Configure Licenses

In order to configure licenses, you need to set the `cap.server.license` property on all deployed content servers. To do that, you need to 
configure the attributes section in the `.kitchen.yml` file like shown in the following snippet:

```yaml
suites:
- name: default
  run_list:
    - recipe[foo::bar]
  attributes:
    blueprint:
      webapps:
        content-management-server:
          application.properties: 
            cap.server.license: /shared/prod-license.zip
        master-live-server:
          application.properties: 
            cap.server.license: /shared/pub-license.zip            
        replication-live-server:
          application.properties: 
            cap.server.license: /shared/prod-license.zip
```

If you use `/shared` as the location for the licenses, you can simply create a `<WORKSPACE_ROOT>/test-system/build` folder and place the licenses there. By default this folder is
shared with you guest machine under the path `/shared`.

## Build RPMs

Please use the kitchen setup below [blueprint-dev-tooling](cookbooks/blueprint-dev-tooling/DEVELOPMENT.md)


## Working with an external database box

Because CoreMedia Blueprint content is quite huge, it can be useful to import content only once and still be able to destroy the
applications box. You can achieve this, by first starting a database box and then the test system box. Configured correctly, the
content will then be only on the database box. This is how its done:

1. Open a new shell 

```bash
cd cookbooks/blueprint-mysql 
# or for postgresql cd cookbooks/blueprint-postgresql
kitchen converge
```

2. Open another shell
The IP of the database box is 192.168.252.110 so we need to set the attribute `node['blueprint']['dev']['db']['host']`. Edit the `.kitchen.yml` and configure the attribute.

```yaml
  attributes:
    blueprint:
      dev:
        db:
          host: '192.168.252.110'   
          type: 'mysql' # or 'postgresql'
```

Now start the test system from this shell like usually.

3. When you destroyed the application box and you want to recreate it without the content, simply set the attribute 
'node['blueprint']['dev']['content']['content_zip']` to an empty string, otherwise the setup will download the artifact and will import it again.

```yaml
  attributes:
    blueprint:
      dev:
        content:
          content_zip: ''

```

> Remember, you need to use virtualbox to stop and start the database box. Kitchen has no commands to start and stop existing images.
