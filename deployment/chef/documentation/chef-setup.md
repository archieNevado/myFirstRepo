The Cookbooks
----------------
The Blueprint Chef Setup consists of four different types of cookbooks:

- Thirdparty cookbooks.
- CoreMedia library cookbooks, prefixed with `coremedia`
- CoreMedia Blueprint Site cookbooks, prefixed with `blueprint`. 
- CoreMedia Blueprint cookbooks for development purposes, prefixed with `blueprint-dev`.

### Thirdparty Cookbooks

All thirdparty cookbooks are retrieved from the official supermarket site http://supermarket.chef.io and vendored by us to the
`cookbooks` folder. In general, you should not modify files in theese cookbooks, they will be updated with each release
of the Blueprint if necessary. This way the cookbooks will be tested by our test infrastructure. However, the version constraints
to thirdparty cookbooks are mostly using the pessimistic `~>` version constraint, therefore updating thirdparty cookbooks within the
constrained version range, can be done easily using berkshelf. 

If you want to manage thirdparty cookbooks yourself, you simply need to remove the cookbook from the `thirdparty-cookbooks` 
folder and update the `Berksfile.lock` using `berks update`. Be advised, that the cookbook is now retrieved from 
the chef supermarket site. To readd it to your VCS, execute `berks vendor temp-cookbooks` and move the updated cookbook 
back to `cookbooks`. The temporary `temp-cookbooks` directory can now be deleted and by calling `berks   

### CoreMedia Cookbooks

There are cookbooks coremedia provides, which have library character and do not have any dependencies to the CoreMedia Blueprint.
Those cookbooks must be treated like thirdparty cookbooks, except they are not available from the public supermarket. The sources
of these cookbooks are available as git repositories, you can fork and change. We highly recommend not to do this except to provide
pull requests our origin repo. We will gladly review, apply your changes and release a new version, which is then immediatly available as a 
tag. In future releases we may provide access to our private supermarket, but till then, you need to define an entry within the `Berksfile` 
using a git source with a tag reference, i.e. 

```ruby
cookbook 'coremedia_tomcat', :git => 'https://github.com/coremedia-contributions/coremedia-tomcat-cookbook', :tag => 'v2.1.0'
```
For further possibilities of defining a cookbook dependency, please review the official Berkshelf documentation [here](http://berkshelf.com/).

#### Documentation Links

* [coremedia_maven](./coremedia-cookbooks/coremedia_maven/README.md)
* [coremedia_tomcat](./coremedia-cookbooks/coremedia_tomcat/README.md)
* [coremedia-proxy](./coremedia-cookbooks/coremedia-proxy/README.md)

### Blueprint Cookbooks

All cookbooks below the `cookbooks` folder are cookbooks, that have explicit knowledge about the CoreMedia Blueprint setup and
therefore are immanent subject to change in your project. Because of that, you should not treat these cookbooks as libraries where easy migration
is your top priority. These cookbooks are yours, you should change them to fit your projects needs.

Some of these cookbooks are `wrapper` cookbooks. They just wrap a thirdparty cookbook by setting attributes and include the original
thirdparty cookbooks recipe. This way the the attributes are versioned within your cookbook and underly your release process. An example for
this type is the `blueprint-mongodb` cookbook.
  
Other cookbooks are `application` cookbooks. They use resources or definitions provided by thirdparty cookbooks to create infrastructure. 
The `blueprint-mysql` cookbook is of such type. It just creates a single MySQL instance, configures it and provides another recipe to create the
schemata for the blueprint. 
 
Wrapper and application cookbooks that provide infrastructure for the CoreMedia Blueprint applications are rudimentary by design, to only
provide the minimum infrastructure needed. In most cases you will need to add functionality to recipes of these cookbooks or add
password security, in case you are using chef server. 
 
The heart of the blueprint cookbooks however are the `blueprint-tomcat` and `blueprint-proxy` cookbooks, they will be the cookbooks you will have to 
change continuously when your project gets released in a continously fashion: 

* The `blueprint-tomcat` cookbook contains all the recipes to install
CoreMedia Blueprint applications tomcats and therfore also contain most of the applications configuration properties.

* The `blueprint-proxy` cookbook contains recipes to install an configure Apache HTTPD server and configure the virtual hosts for the CoreMedia Blueprint including
the rewrite logic and some basic balancing logic.

* The `blueprint` cookbook is the environment cookbook, containing one recipe for each environment. Use these recipes to set environment specific attributes, but attributes only.

#### The Blueprint-dev-tooling Cookbook

Within the `cookbooks` folder, there is also a `blueprint-dev-tooling` cookbook, which provides convenience recipes for development setups. This cookbook is neither
intended to be used in production nor is it intended to be used as it is. This is a `developer` or `ci` cookbook, it just makes things easier for the local development
 or for a simple single instance test system. Because of this nature, it contains the content recipe to upload content during a chef run. Uploading content
with chef in production is a bad idea. Idempotency is nearly impossible and import and publication may take a lot of time making chef run durations vary too.


#### Documentation Links

* [blueprint](./cookbooks/blueprint/README.md)
* [blueprint-base](./cookbooks/blueprint-base/README.md)
* [blueprint-yum](./cookbooks/blueprint-yum/README.md)
* [blueprint-mongodb](./cookbooks/blueprint-mongodb/README.md)
* [blueprint-postgresql](./cookbooks/blueprint-postgresql/README.md)
* [blueprint-mysql](./cookbooks/blueprint-mysql/README.md)
* [blueprint-tools](./cookbooks/blueprint-tools/README.md)
* [blueprint-tomcat](./cookbooks/blueprint-tomcat/README.md)
* [blueprint-proxy](./cookbooks/blueprint-proxy/README.md)
* [blueprint-dev-tooling](./cookbooks/blueprint-dev-tooling/README.md)

The Roles
--------------

The `role` folder contains a possible set of chef roles. Each role represents a logical component in a CoreMedia Blueprint System. 
It is best practise, that roles only contain a runlist but no attributes. By nature roles are not versioned, making it 
hard to stage changes. Therefore, maintain attributes in the application cookbooks or the environment cookbooks recipes. 

The Environments
---------------

There are four environments, `kitchen`, `development`, `staging` and `production`. Environments should not containt attributes as well, as they are
not versioned. Staging new attributes using cookbooks is always the preferred way.

The config folder
-----------------

Below the config folder, you will find additional configuration files for CI and local development tooling. 

The .chef folder
----------------

The `.chef` folder is the default location for configuration files to the chef command line clients.

The nodes folder
----------------

This folder contains json node configuration files for chef-solo.

Cookbook Development
--------------------

### File Layout

This chef repository has development tooling integrated. This includes:

* A `Gemfile` to setup the developement environment with [bundler](http://bundler.io/).
* A `.rubocop.yml` file to configure [rubocop](https://github.com/bbatsov/rubocop).
* A `.kitchen.yml` test kitchen configuration for local testing.
* A rake file to:
      * run linting (Rubocop and Foodcritic) 
      * generate the documentation (knife-cookbook-doc)
* A `CHANGELOG.md` file, that contains the changelog.                 
         
### Prerequisites & Preparation

Since you already installed the Chef Development Kit, the following tools are available:

* test kitchen
* rubocop
* chefspec
* foodcritic
* spork

In Addition to provide other tools for the rake integration, you need to install bundler `chef gem install bundler` once. In any cookbook you 
then need to install the required gems by running `bundle install`. From this point on all command line tools need to be done using the
bundler context using `bundle exec <gem cmd>`.

### Development Cycle

1. Start developing in any blueprint cookbook and change directory to that cookbook.
2. Run linting `bundle exec rake style`
3. Run chefspec tests if available `bundle exec test:unit`
4. Converge a box `kitchen converge <instance>` 
5. Run tests in the box `kitchen verify <instance>`
6. Restart with 1.

After one instance is fine, you should rerun kitchen from scratch `kitchen test` and restart cycle for all failed instances. With `kitchen list` you can see the status.

Before committing, you should regenerate the documentation using `bundle exec rake doc`. For a documentation how to use the annotations, see [knife-cookbooc-doc](https://github.com/realityforge/knife-cookbook-doc).

CI Tooling
---------------

To setup a cookbook CI, we recommend to use jenkins with test-kitchen and a driver + infrastructure combination, that allows to start each ci run
on fresh state. Possible solutions to this problem are:
  
* kitchen-docker driver and a jenkins node as a docker host ( this works well for cookbooks with small impact, blueprint-tomcat may not a good choice)
* kitchen-ec2 or any other cloud provider 

For more complex cookbooks or complete test systems, we recommend:

* kitchen-ssh driver for remote provisioning 
* kitchen-localhost driver for local provisioning

in combination with a reset to snapshot solution i.e. jenkins vsphere plugin. 

In case your jenkins slave are chef managed as well, you need to install berkshelf and the kitchen gems securely. For this purpose, we provide you with the 
`Gemfile` and the `Gemfile.lock`. The ci setup we have implemented at CoreMedia, uses the kitchen-ssh driver, configured in a separate `.kitchen.ssh.yml` file. 

To make things easy we provide a `config/ci-setenv` script you only need to insource in your jenkins shell step, before you run your cookbook ci. An example shell step would look like:

```bash
. ${WORKSPACE)/deployment/chef/config/ci-setenv
bundle exec rake style kitchen_syntax
# now you can run a suite
bundle exec kitchen converge <TEST SUITE>
```

An example for a kitchen platform using the `kitchen-ssh` looks like the following snippet. 
  
```yaml
- name: ssh
  driver:
    name: ssh
    hostname: my.testsystem
    port: 22
    username: my-yser
#   ssh_key: 
    password: my-password
    sudo: true
  provisioner:
    name: chef_solo
    require_chef_omnibus: false    # make sure chef is installed before you provision
```  
  
A platform using the `kitchen-localhost` driver may look like this:
  
```yaml
- name: local
  driver:
    name: localhost
    clean_up_on_destroy: false
  provisioner:
    name: chef_solo
    require_chef_omnibus: false    # make sure chef is installed before you provision
```
  
