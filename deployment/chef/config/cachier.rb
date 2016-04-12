#
# Configures the vagrant cachier plugin. See https://github.com/fgrehm/vagrant-cachier for the official plugin documentation
#
Vagrant.configure("2") do |c|
  if Vagrant.has_plugin?("vagrant-cachier")
    c.cache.auto_detect = true
    # change this scope to :machine if you do not want to share cached packages between vagrant instances
    # caching packages between machines makes sense at least to only download the chef installer once.
    # by default remote_file resources are not cached, so blueprint application artifacts are not cached.
    c.cache.scope = :box
  end
end
