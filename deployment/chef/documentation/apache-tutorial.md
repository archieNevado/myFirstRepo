Because Apache development is a tedious task, you can use the `blueprint-proxy` cookbook test harness to easily start an 
Apache inside a Virtualbox. This Apache server can then be used either to run against a remote CoreMedia Blueprint test system to 
develop on the Apache config itself or to be used to act as the middleman for CAE, Studio and WCS. 
 
 
### Develop Apache config only  

To start developing Apache config, you can use the `.kitchen.yml` configuration found in `cookbooks/blueprint-proxy`.  
If you want Apache to run against the remote systems, you simply need to configure the hosts by exporting the environment variables `CMS_HOST` and `WCS_HOST`

```bash
cd cookbooks/blueprint-proxy
export CMS_HOST=cms.dev.my.domain
export WCS_HOST=wcs.dev.my.domain
kitchen converge default
```

To change rewrite rules and proxy or balancing logic, refer to the documentation of the `coremedia-proxy` and the `blueprint-proxy` cookbook. 
The rewrite rules templates are located in the [templates/default/rewrite](../cookbooks/blueprint-proxy/templates/default/rewrite) 
folder of the `blueprint-proxy` cookbook. 
