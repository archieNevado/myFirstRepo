Deployment Archive
==================

If you plan to use `chef-solo`, at first you should try out the 
`deployment-archive.zip` this Maven module creates. You'll find it below
the target directory after building the workspace. It contains:

* A Maven repository with all artifacts required for deployment
* The Chef repo with all cookbooks, roles, environments in place
* A deploy script with an installer like prompt     

The deployment archive is created by Maven from the [chef module](./../chef/pom.xml),
it contains:
 * A Maven repo with all artifacts required for deployment
 * The Chef repo with all cookbooks 
 * Bootstrap scripts
 * The `content-users.zip`
 
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
|- content-users.zip
`- deploy.sh
```      
In case the directories `coremedia-cookbooks` and `thirdparty-cookbooks` are missing, make sure to run `vendor-cookbooks.sh` in `bin/release/` before building the workspace.
 
You can extract the archive anywhere on a server and run the `deploy.sh` script. The
only prerequisites are `chef` and `java`. The latter can be either installed manually or by accepting Oracles License agreement. The `java_se::default` recipe will install it for you. 
The cookbook is included in the `thirdparty-cookbooks` directory and the recipe is included in the runlist of the example `single.json` but you need to remove the disclaimer from that file
first, otherwise the chef run will fail because of a malformed json syntax. 
            
            ==== ACCEPT AND REMOVE  ===
            ==== By adding java_se to a run list (recipe[java_se]) or a cookbook (include_recipe 'java_se')
            ==== you are accepting the Oracle Binary Code License Agreement for Java SE.

Proceed
-------

* Build the workspace
* Copy the deployment archive to a slave
* Extract the archive
* Configure the `nodes/single.json` file
* Run `./deploy.sh`

Example
-------
1. Create a new directory and change to it.
2. Create a `Vagrantfile` with the following content:

        Vagrant.configure(2) do |config|
          config.vm.box_url = 'https://atlas.hashicorp.com/coremedia/boxes/base7'
          config.vm.box = "coremedia/base7"
          config.vm.network "private_network", :ip => '192.168.252.100'
          config.vm.provider "virtualbox" do |v|
            v.memory = '8192'
          end
        end
3. Start the vagrant box:

        vagrant up

4. Copy the deployment archive to the slave

        scp <BLUEPRINT_WORKSPACE_ROOT>/deployment/chef/target/deployment-archive.zip root:vagrant@192.168.252.100:/var/tmp

4. Login and install Chef:
  
        vagrant ssh
        sudo su -
        mkdir /var/tmp/deploy
        # download the chef rpm
        curl -L -o /var/tmp/deploy/chef-12.8.1-1.el7.x86_64.rpm https://packages.chef.io/stable/el/7/chef-12.8.1-1.el7.x86_64.rpm
        # install the chef rpm
        rpm -Uvh /var/tmp/deploy/chef-12.8.1-1.el7.x86_64.rpm
  
5. Extract the deployment archive:

        vagrant ssh
        sudo su - 
        yum install -y unzip
        unzip /var/tmp/deployment-archive.zip -d /var/tmp/deploy
          
6. Configure the `single.json` node file, especially WCS host and cookie domain.
 
7. Fire it up:

        vagrant ssh
        sudo su -
        cd /var/tmp/deploy
        ./deploy.sh
