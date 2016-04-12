Because Apache development is a tediuous task, You can use the `blueprint-proxy` cookbook test harness to easily start an 
apache inside a virtualbox. This apache can then be used either to run against a remote CoreMedia Blueprint test system to 
develop on the apache config itself or to be used to act as the middleman for cae, studio and wcs. 
 
 
### Develop apache config only  

To start developing apache config, you can use the `.kitchen.yml` configuration found in `cookbooks/blueprint-proxy`.  
If you want apache to run against remote systems, you simply need to configure the hosts by exporting the environment variables `CMS_HOST` and `WCS_HOST`

```bash
cd cookbooks/blueprint-proxy
export CMS_HOST=cms.dev.my.domain
export WCS_HOST=wcs.dev.my.domain
kitchen converge
```

To change rewrite rules and proxy or balancing logic, refer to the documentation of the `coremedia-proxy` and the `blueprint-proxy` cookbook. 
The rewrite rules templates are located in the [templates/default/rewrite](../cookbooks/blueprint-proxy/templates/default/rewrite) 
folder of the `blueprint-proxy` cookbook. 

### Develop against local started webapps with a boxed apache
If you want to use apache to front your locally started webapps (from IDE or with maven), you can use the `.kitchen.apache.yml` file located in the test systems root dir.

```
export KITCHEN_YAML=.kitchen.apache.yml
kitchen converge
```

In this scenario the apache will route all requests through the hostonly adapter of VirtualBox. By default this adapter has the ip `192.168.252.1`. If this is not the case
on your workstation, you need to set the environment variable `CMS_HOST` and execute `kitchen converge`.

Like in the scenario above you need to set the `WCS_HOST` environment variable to specify the hostname of the WCS system. By default this is too set to the hostonly adapter ip so 
make sure to set this correctly and reconverge the box. 

```
export WCS_HOST=my.remote.wcs.host
kitchen converge
```

You can of course set these environment variables upfront the first converge call. To verify the config by execute `kitchen list`
  
Before you now start you local tomcats using maven, you need to add and activate the following maven profile.
  
```xml
<profile>
  <id>localPreviewEnvironment</id>
  <properties>
    <cae.is.standalone>false</cae.is.standalone>
    <livecontext.ibm.wcs.host>${env.WCS_HOST}</livecontext.ibm.wcs.host>
    <livecontext.apache.wcs.host>shop-preview-production-helios.192.168.252.100.xip.io</livecontext.apache.wcs.host>
    <livecontext.apache.preview.production.wcs.host>shop-preview-production-helios.192.168.252.100.xip.io</livecontext.apache.preview.production.wcs.host>
    <livecontext.apache.preview.wcs.host>shop-preview-helios.192.168.252.100.xip.io</livecontext.apache.preview.wcs.host>
    <livecontext.apache.live.production.wcs.host>shop-helios.192.168.252.100.xip.io</livecontext.apache.live.production.wcs.host>
    <livecontext.ibm.wcs.rest.search.url>http://${env.WCS_HOST}:3737/search/resources</livecontext.ibm.wcs.rest.search.url>
    <studio.previewUrlWhitelist>*.192.168.252.100.xip.io</studio.previewUrlWhitelist>
    <studio.previewUrlPrefix>//preview-helios.192.168.252.100.xip.io</studio.previewUrlPrefix>
    <blueprint.host.helios>preview-helios.192.168.252.100.xip.io</blueprint.host.helios>
    <blueprint.host.studio.helios>studio-helios.192.168.252.100.xip.io</blueprint.host.studio.helios>
    <blueprint.site.mapping.helios>//preview-helios.192.168.252.100.xip.io</blueprint.site.mapping.helios>
    <blueprint.host.corproate>preview-corporate.192.168.252.100.xip.io</blueprint.host.corproate>
    <blueprint.site.mapping.corporate>//preview-corporate.192.168.252.100.xip.io</blueprint.site.mapping.corporate>
    <livecontext.cookie.domain>.192.168.252.100.xip.io</livecontext.cookie.domain>
  </properties>
</profile>
```  

Now start the preview using also the `development-ports` profile and `-Dinstallation.host` if your CMS backend is running remotely.

```bash
mvn tomcat7:run -Dinstallation.host=<CMS BACKEND HOST> -PlocalPreviewEnvironment,development-ports
```

For the studio you can use the same profile and execution call.