Development Application Configuration
--------------------------------------

The `default` folder contains `application.properties` files for each web application started with `mvn tomcat7:run`. By
default it is expected that all services and its dependencies run locally on the developers workstation. 

The property files are being passed to the Tomcat instances by using the `propertieslocations` system property. 

### Virtualized Setup
If you like to virtualize the whole or parts of your deployment, it is recommended to use Test Kitchen to bootstrap a VirtualBox. 
You'll find the setup below the `<WORKSPACE_ROOT>/deployment/chef` folder. By default, the system will be deployed with 
an IP: `192.168.252.100` and as a hostname the http://xip.io dns and the generic hostname `192.168.252.100.xip.io` is used. 
To start webapps like studio, preview-cae or live-cae against that virtualized system, all you need to do is set the Java 
system property `installation.host` to that generic hostname.

```
mvn tomcat7:run -Dinstallation.host=192.168.252.100.xip.io
```

or even simpler using the inbuilt `kitchen` profile

```
mvn tomcat7:run -Pkitchen
```

### Using a remote system
To start webapps like studio, preview-cae or live-cae against that virtualized system, all you need to do is set the Java 
system property `installation.host` to your servers name or IP address or create a Maven profile in your 
`~/.m2/settings.xml` setting that property.

```
<profile>
  <id>ci-system</id>
  <properties>
    <installation.host>my-ci-system.my.domain</installation.host>
  </properties>
</profile>
```

### Configuring IBM WCS Instance 
####(Only relevant for CoreMedia LiveContext 3 for IBM WebSphere Commerce customers)

To configure the hostname of the IBM WCS instance, the property `wcs.host` needs to be defined, too. Use the previously created
profile to keep remote CMS/WCS pair configuration together.

```
<profile>
  <id>ci-system</id>
  <properties>
    <installation.host>my-ci-system.my.domain</installation.host>
    <wcs.host>my-ci-wcs-system.my.domain</wcs.host>
  </properties>
</profile>
```


### Running multiple applications locally

If you want to run multiple applications locally and need them to talk to each other, you need to set more host properties.
Currently the following properties are provided and by default set to `${installation.host}`:

* `solr.host`
* `content-management-server.host`
* `workflow-server.host`
* `master-live-server.host`
* `studio.host`
* `cae-preview.host`
* `cae-live.host`
* `db.host`
* `mongoDb.host`
* `proxy.host`

The `application.properties` files in this folder, make use of these host properties and the host properties are passed into the Tomcat plugins execution as system properties,
allowing you to configure the hosts from the command line.

Most likely, the only host properties you need to set are `cae-preview.host` and `proxy.host`, but there are always scenarios, where you need to
run some applications locally and only keep the databases remotely or virtualized.

If, for example, you want to run studio and preview locally but use the rest from your CI system, all you need to do is:

```
# start studio
mvn -f modules/studio/studio-webapp/pom.xml tomcat7:run -Pci-system -Dcae-preview.host=localhost
# start preview cae
mvn -f modules/cae/cae-preview-webapp/pom.xml tomcat7:run -Pci-system
```

### Adding an own set of properties

If the default does not fit your requirements, because your development scenario is very special, you can always add
profiles to load a complete different set of property files. Doing this can be achieved, by 

1. copying the default property set below `development-properties` to a new directory
2. modify them properties in the copied files to your needs
3. define the property `development-properties.dir` to the _absolute_ path of that directory. 

```
<profile>
  <id>ci-system</id>
  <properties>
    <installation.host>my-ci-system.my.domain</installation.host>
    <wcs.host>my-ci-wcs-system.my.domain</wcs.host>
    <development-properties.dir>/Users/myName/dev/ci-system-properties</development-properties.dir>
  </properties>
</profile>
```
