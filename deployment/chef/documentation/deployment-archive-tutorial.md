Deployment Archive
==================

If you plan to use `chef-solo`, at first you should try out the 
`deployment-archive.zip` this maven module creates. You'll find it below
the target directory after building the workspace. It contains:

* a maven repository with all artifacts required for deployment
* the chef repo with all cookbooks, roles, environments in place
* a deploy script with an installer like prompt     

The deployment archive is created by maven from the [chef module](./../chef/pom.xml),
it contains:
 * a maven repo with all artifacts required for deployment
 * the chef repo with all cookbooks 
 * bootstrap scripts
 
The archive will be created at `blueprint/deployment/chef/target/deployment-archive.zip`.

```
|- maven-repo
|- chef-repo
| |- .chef
| |   `-solo.rb
| |- cookbooks
| |  |- blueprint
| |  |- blueprint-base
| |  |- ..
| |  `- blueprint-yum
| |- coremedia-cookbooks
| |  |- coremedia-proxy
| |  |- .. 
| |  `- coremedia-tomcat
| |- thirdparty-cookbooks
| |  |- mysql
| |  |- ..
| |  `- mongodb3
| |- environments
| |- nodes
| |  `-single.json
| `- roles 
|    |- base.json
|    |- delivery.json
|    |- ..
|    `- studio.json
`- deploy.sh
```      
In case the directories `coremedia-cookbooks` and `thirdparty-cookbooks` should be missing, please make sure to run `vendor-cookbooks.sh` in `bin/release/` before building the workspace.
 
You can extract the archive anywhere on a server and run the `deploy.sh` script. The
only prerequisites are `chef` and `java`.

Proceed
-------

* build the workspace
* copy the deployment archive to a slave
* extract the archive
* configure the `nodes/single.json`
* run `./deploy.sh`

Example
-------
1. create a new directory and change to it.
2. create a `Vagrantfile` with the following content:

        Vagrant.configure(2) do |config|
          config.vm.box_url = 'https://atlas.hashicorp.com/coremedia/boxes/base7'
          config.vm.box = "coremedia/base7"
          config.vm.synced_folder '../../chef/target', '/deployment-archive'
          config.vm.network "private_network", :ip => '192.168.252.100'
          config.vm.provider "virtualbox" do |v|
            v.memory = '8192'
          end
        end
3. fire up the vagrant box:

        vagrant up

4. login and install chef:
  
        vagrant ssh
        sudo su -
        mkdir /var/tmp/deploy
        # download the chef rpm
        curl -L -o /var/tmp/deploy/chef-12.8.1-1.el7.x86_64.rpm https://packages.chef.io/stable/el/7/chef-12.8.1-1.el7.x86_64.rpm
        # install the chef rpm
        rpm -Uvh /var/tmp/deploy/chef-12.8.1-1.el7.x86_64.rpm
  
5. extract the deployment archive:

        vagrant ssh
        sudo su - 
        yum install -y unzip
        unzip /deployment-archive/deployment-archive.zip -d /var/tmp/deploy
          
6. configure the `single.json` node file, especially WCS host and cookie domain.
 
7. fire it up:

        vagrant ssh
        sudo su -
        cd /var/tmp/deploy
        ./deploy.sh
