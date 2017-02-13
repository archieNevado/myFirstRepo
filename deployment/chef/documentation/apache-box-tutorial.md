Apache Box Tutorial
===================

The boxed apache setup, spins up a VirtualBox containing only an Apache httpd server. This lightweight box can 
 be used to route all traffic, so we can:

* Develop rewrite rules, add sites, change cookie settings using a remote CMS and WCS system.
* Share a WCS and still load fragments in the PDP from our CAE
* Use SSL and fqdn URLs with tenants

Because the provisioning of Apache is quite simple, it's fast too. The roundtrip for
controlled and repeatable infrastructure-as-code changes is less then 20 seconds.

Prerequisites
-------------

* Make sure there is no vbox already running on ip `192.168.252.100`. If so, shut it down.
* Create a profile `example` in your Maven `~/.m2/settings.xml` file to be used throughout the tutorial. 
  Make sure to configure the correct values for your development setup. Lets say your 
  WCS instance is running on node `wcs.example.com` and your cms instance on `cms.example.com`. 

```
<profile>
  <id>example</id>
  <properties>
    <installation.host>cms.example.com</installation.host>
    <wcs.host>wcs.example.com</wcs.host>
  </properties>
</profile>
```

> You can name the profile to `apache-box`, as this is the name used in the profiles of the 
> `cae-preview-webapp`, `studio-webapp` and `cae-live-webapp` Maven modules with additional configuration.

Start the Engines
-----------------

All the work for this setup is done in the `deployment/chef/cookbooks/blueprint-proxy` cookbook, please change to 
that directory. 

1. Make sure all boxes are stopped. Run `kitchen list` to see that no instance is already created. The output should look like:
      
      --------------------------------------------
         WCS_HOST:     192.168.252.1
         CMS_HOST:     192.168.252.1
         LOCAL_HOST:   localhost
        --------------------------------------------
        Instance                     Driver   Provisioner  Verifier  Transport  Last Action
        default-centos6-vagrant      Vagrant  ChefSolo     Busser    Ssh        <Not Created>
        development-centos7-vagrant  Vagrant  ChefSolo     Busser    Ssh        <Not Created>    
    
2. Set the environment variables correctly. Let's say your workstations DNS is `workstation.example.com`. 
We do not need to set `CMS_HOST` here as `192.168.252.1` is always the hostonly adapter. :
on linux:
 
        export WCS_HOST=wcs.example.com
        export LOCAL_HOST=workstation.example.com
on windows:
   
        set CMS_HOST=cms.example.com
        set WCS_HOST=wcs.example.com
        set LOCAL_HOST=workstation.example.com
        
3. Fire up the box:
        
        kitchen converge development
      
4. After the box has provisioned, try out the CAE or Studio URLs, you should not get any answers yet.
The WCS URLs however should work except for the CAE fragments:

   * [Studio](http://studio.192.168.252.100.xip.io)
   * [Corporate CAE Preview](http://preview-corporate.192.168.252.100.xip.io)
   * [Corporate CAE Live](http://corporate.192.168.252.100.xip.io)
   * [Aurora Shop Preview](http://shop-preview-production-helios.192.168.252.100.xip.io)   
   * [Aurora Shop Live](http://shop-helios.192.168.252.100.xip.io)

5. Open a shell, change to `modules/cae/cae-preview-webapp` and start the preview CAE:
     
        mvn tomcat7:run -Pexample,apache-box

    > It is important, that you use the profile `apache-box`, as this is the name used in the Maven modules.
        
6. Open a shell, change to `modules/studio/studio-webapp` and start the studio:
     
        mvn tomcat7:run -Pexample,apache-box

7. Open a shell, change to `modules/cae/cae-live-webapp` and start the live CAE:
     
        mvn tomcat7:run -Pexample,apache-box

8. Now you should be able to open studio, preview and live CAE. Fragments and PDP images should
now be loaded from your local CAEs.

Troubleshooting
---------------

Make sure, that your workstation is reachable inside the network. Disable your firewall, 
or open your CAE and Studio ports.
