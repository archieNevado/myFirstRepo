=begin
#<
This recipe installs and configures postgresql.
#>
=end

include_recipe 'blueprint-postgresql::_base'
include_recipe 'postgresql::server'
