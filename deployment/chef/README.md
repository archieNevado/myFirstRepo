Test System Setup
=================

This is the starting point for setting up a local system with Chef Development Kit, Kitchen, Vagrant and Virtual Box.

Refer to the CoreMedia Deployment Guide for a detailed description
([pdf for the current release](https://documentation.coremedia.com/dxp/current/manuals/deployment-en/deployment-en.pdf)).

Prerequisites
-------------

Concrete versions used during Release QA are mentioned in the Prerequisites chapter
of the CoreMedia DXP manual (e.g. for the [current release](https://documentation.coremedia.com/dxp/current/manuals/coremedia-en/webhelp/content/Prerequisites.html)).

1. Install Virtual Box:
      - Download the installer from the official [site](https://www.virtualbox.org/wiki/Downloads)
2. Install Chef Development Kit:  
      - Follow the official [instructions](https://docs.chef.io/install_dk.html).
      - It is recommended to make the chef-dk ruby to your systems default.
3. Install Vagrant:
      - Download and execute the installer from the official [site](https://www.vagrantup.com/).
4. Install Vagrant plugins 
      - `vagrant-cachier` by executing `vagrant plugin install vagrant-cachier`.
5. Build the workspace and make sure the servers have licenses inbuilt. For an alternative way to deploy licenses, see [HOWTO](#configure-licenses).

Start the System
----------------

To see which setups are available type in `kitchen list`. You will then see a list of possible instances to run. 
By default only one instance will be listed. To enable centos7 box comment in the platform in the `.kitchen.yml` file.

```
Instance                 Driver   Provisioner  Verifier  Transport  Last Action
default-centos6-vagrant  Vagrant  ChefSolo     Busser    Ssh        <Not Created>
```

Choose an instance and run it using `kitchen converge <INSTANCE NAME>`. With only one instance you can omit the instance parameter.
Other available commands are:

- destroy
- verify (install test harness and run tests)
- test (destroy running box, converge it and the run tests and destroy if successfull)

For a more comprehensive description, you should visit http://kitchen.ci/.

When the box has converged and is running use the links from the Overview page accessible at <http://overview."hostname">

[Chef Setup](./documentation/chef-setup.md)
[Development how-to](./documentation/how-to.md)                                                                                                      
                                                                                                     
Tutorials
---------

Below the folder `documentation` you will find several tutorials `*-tutorial.md`. 

* [apache](./documentation/apache-tutorial.md) - This tutorial is for developers that need to change apache rewrite rules or need to add new virtualhost definitions.
* [apache-box](./documentation/apache-box-tutorial.md) - This tutorial is for Studio or CAE developers to quickly setup their system.
* [boxed-system](./documentation/boxed-system-tutorial.md) - This tutorial is for developers that want to change basics in the Chef deployment.
* [deployment-archive](./documentation/deployment-archive-tutorial.md) - This tutorial is for developers that want to simply deploy a system with `chef-solo`.  
                                                                                                     