Configure the deployment
------------------------
    
* Configure blobstore URLs and extra serverimport args (alternative to downlowd blobs).
  In your `blueprint/deployment/chef/.kitchen.local.yml` add the following yaml config:

```yaml
suites:
- name: default
  attributes:
    blueprint:
      dev:
        content:
          serverimport_extra_args: "-bloburl http://storage.coremedia.vm/cms/blueprint/test-data/blobs/ --blobreferences --threads 5"
```

Using the blobstore and the blobreferences flag, keeps the blobs in the blobstore and lets the cae dowload them 
only on request. This saves time and space on the disk.

* Configure repo mirrors (optional). In your `blueprint/deployment/chef/.kitchen.local.yml` merge the following yaml snippet:

```yaml
suites:
- name: default
  attributes:
    yum:
      base:
        mirrorlist: https://s3-eu-west-1.amazonaws.com/mirrors.coremedia.com/CentOS/$releasever/os/$basearch/mirrorlist.txt
      updates:
        mirrorlist: https://s3-eu-west-1.amazonaws.com/mirrors.coremedia.com/CentOS/$releasever/updates/$basearch/mirrorlist.txt
      pgdg:
        mirrorlist: https://s3-eu-west-1.amazonaws.com/mirrors.coremedia.com/yum.pgrpms.org/9.3/rhel-$releasever-$basearch/mirror
      epel:
        mirrorlist: https://s3-eu-west-1.amazonaws.com/mirrors.coremedia.com/dl.fedoraproject.org/pub/epel/$releasever/$basearch/mirrorlist.txt
      mysql55-community:
        mirrorlist: https://s3-eu-west-1.amazonaws.com/mirrors.coremedia.com/repo.mysql.com/mysql-5.5-community/el/$releasever/$basearch/mirrorlist.txt
    blueprint:
      mongodb:
        yum:
          mirrorlist: https://s3-eu-west-1.amazonaws.com/mirrors.coremedia.com/repo.mongodb.org/yum/redhat/$releasever/mongodb-org/3.2/$basearch/mirror
```
  
* Configure the shared WCS instance. In your `blueprint/deployment/chef/.kitchen.local.yml` change the default value of the wcs ip to
`wcs.example.com`. i.e. :
  
```yaml
# from 
# wcs_host = ENV.fetch('WCS_HOST', '192.168.252.1')
# to
wcs_host = ENV.fetch('WCS_HOST', 'wcs.example.com')
```

Alternatively export the environment variable `WCS_HOST`.
  
```bash
export WCS_HOST=wcs.example.com
```
      
Launch the box  
--------------
    
* Change to dir `blueprint/deployment/chef`
* Run:
    
```bash
kitchen converge
```


Only include what ypu need
--------------------------

To save time, you should always reduce the set of applications to deploy to the minimum you need for your setup. 
You can control this, by changing the runlist in your kitchen file.

```yaml
  run_list:
    # install database
    - role[<%= database_type %>]
    - role[mongodb]
    - role[solr-master]
    - role[management]
    - role[publication]
    - role[studio]
    - role[studio-proxy]
    - role[delivery]
    - role[delivery-proxy]
    - recipe[blueprint-dev-tooling::overview]
    # comment out to skip content import
    - recipe[blueprint-dev-tooling::content]
```

The roles are defined in the `roles/<role>.json`. 
> the roles are defined as JSON files, because chef-server can only handle JSON roles.

Work with the box
-----------------

### Change JVM and Tomcat Options

To change Tomcat or JVM options, the `blueprint-tomcat` cookbook is your friend. To see what possible attributes can be set, visit 
the cookbooks [documentation](./../chef/cookbooks/blueprint-tomcat/README.md). 

Let's say we want to change the memory settings, in the  [blueprint-tomcat attributes](./../chef/cookbooks/blueprint-tomcat/attributes/default.rb),
you will find entries like: 

```
default['blueprint']['tomcat']['content-management-server']['heap'] = '512m'
default['blueprint']['tomcat']['workflow-server']['heap'] = '384m'
default['blueprint']['tomcat']['master-live-server']['heap'] = '384m'
default['blueprint']['tomcat']['replication-live-server']['heap'] = '256m'
default['blueprint']['tomcat']['user-changes']['heap'] = '256m'
default['blueprint']['tomcat']['elastic-worker']['heap'] = '256m'
default['blueprint']['tomcat']['content-feeder']['heap'] = '256m'
default['blueprint']['tomcat']['studio']['heap'] = '512m'
default['blueprint']['tomcat']['adobe-drive-server']['heap'] = '256m'
default['blueprint']['tomcat']['cae-preview']['heap'] = '768m'
default['blueprint']['tomcat']['cae-live']['heap'] = '768m'
default['blueprint']['tomcat']['solr']['heap'] = '256m'
default['blueprint']['tomcat']['caefeeder-preview']['heap'] = '256m'
default['blueprint']['tomcat']['caefeeder-live']['heap'] = '256m'
default['blueprint']['tomcat']['sitemanager']['heap'] = '92m'
```

These are the default values for the attributes. If you want to evolve this cookbook, you can set your values there right away.
If you want to set values specific to an environment, i.e. :
* The kitchen (virtualbox) environment
* The development (jenkins) environment
* The staging environment
* The production environment

You can set these in the recipes of the [environment cookbook](./../chef/cookbooks/blueprint/README.md), i.e. in the
`blueprint/deployment/chef/cookbooks/blueprint/recipes/_kitchen.rb`. 

> If you wonder why these recipes are prefixed with an underscore. They are not intended to be listed in any runlist directly.
> To enable the environment cookbook pattern, the `blueprint/deployment/chef/cookbooks/blueprint/recipes/default.rb` recipe should
> be included i.e. `include_recipe 'blueprint'` and the environment should be set in your `solo.rb` or as a parameter, when calling the chef-client.
  
To change or set any JVM options, you can either use global attributes for all services or individual ones. To set global options:
  
```
default['blueprint']['tomcat']['catalina_opts']['superfast_opts'] = '-XX:superFastGC'
```

where `superfast_opts` is here an arbitrary key to make the hash entry not anonymous.

> In Chef you should be careful with using arrays as the structure to define multiple values. Arrays can only be overridden but not extended

To set service specific options, here for the `content-management-server`, write your attributes like:

```
default['blueprint']['tomcat']['content-management-server']['catalina_opts']['superfast_opts'] = '-XX:superFastGC'
```

#### Example

Change the heap memory for one service and rerun the converge. As you can see only the affected service gets restarted.


Change Tomcat Version
---------------------

To change the Tomcat version, you need to set at least three attributes. I.e. if we want to update to `7.0.70` set the following attributes.

```
default['blueprint']['tomcat']['source'] = 'http://mirror.coremedia.vm/archive.apache.org/dist/tomcat/tomcat-7/v7.0.70/bin/apache-tomcat-7.0.70.zip'
default['blueprint']['tomcat']['source_checksum'] = '1f4653c94935a5c7a4baaffda0b10c366ca44ff9e949cd150be5255868545742'
default['blueprint']['tomcat']['version'] = '7.0.70'
```

> The checksum is a `sha256`. 

In addition to the Tomcat Zip file, if we want to use remote jmx, we should also always change the version of the `catalina_jmx_remote` jar too.

```
default['blueprint']['tomcat']['jmx_remote_jar_source'] = 'http://mirror.coremedia.vm/archive.apache.org/dist/tomcat/tomcat-7/v7.0.70/bin/extras/catalina-jmx-remote.jar'
default['blueprint']['tomcat']['jmx_remote_jar_source_checksum'] = '5693bd6c705814bcf42ffa972e23e8b398cd1ac25652adee6eabab3db114b657'
```

[//]: # TODO: currently it is not possible to set individual tomcat versions and only update versions for some webapps.
 
Now rerun `converge` and you will see that all tomcats are being updated.

Change Logging
--------------

To change logging, you simply need to set some attributes or if ypu want a more sophisticated logging configuration, you need to change the logback configuration template.

1. To change logging, add the following to chef or to your kitchen file. Again, you can set global values or service specific ones. We now want to change the logging for the
`cae-preview` only:


```
default['blueprint']['tomcat']['cae-preview']['logback_config']['logger']['com.coremedia'] = warn
```

or in kitchen yaml syntax:

```yaml
blueprint:
  tomcat:
    cae-preview:
      logback_config:
        logger:
          com.coremedia: warn
```

rerun `converge` and look into the box to see the difference:

```
kitchen login
less /var/log/coremedia/cae-preview/blueprint.log
less /opt/coremedia/cae-preview/logback.xml
```

Change Application Properties
-----------------------------

To change any application property, you need to set them as hash entries below

```                                           
default['blueprint']['webapps']['<SERVICE_NAME>']['application.properties']['<PROPERTY_KEY>'] = '<PROPERTY_VALUE>'
```

Any property set here will override its default from the webapps `WEB-INF/application.properties` file or any other loaded Spring property.

How this is being used can be seen in the service recipes below `blueprint/deployment/chef/cookbooks/blueprint-tomcat/recipes/`.


Update a Webapps Version
------------------------

Artifact versions are being set for each webapp or during development using one convenience attribute. To update a specific webapp, i.e. the `studio`, set

```
default['blueprint']['webapps']['studio']['version'] = '1604.5-11'
```

rerun `converge` , you will see the run will fail and the old webapp will still run.

Now lets change the version of that artifact. 

1. Replace all `${project.version}` usages in the `blueprint/modules/studio/studio-webapp/pom.xml` with `${project.parent.version}`
2. Add a `<version>1604.5-11</version>` below the `<artifactId>` element. 
3. Build the Studio WAR file

now rerun `converge`. 

> To make this individual version setup work, i had to replace all `${project.version}` usages in the dependency management of the root pom with `1-SNAPSHOT`
> It is no good idea to use `${project.version}` in dependency management entries at all. 
> I will fix it in future releases. 





