Vagrant.require_version ">= 1.8.0"
Vagrant.configure("2") do |c|
  c.vm.provider "virtualbox" do |v|
    v.linked_clone = true
  end
end