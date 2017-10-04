name 'blueprint-base'
maintainer 'The Authors'
maintainer_email 'you@example.com'
license 'all_rights'
description 'Installs/Configures blueprint-base'
long_description IO.read(File.join(File.dirname(__FILE__), 'README.md'))
version '1.0.0'

depends 'sysctl', '~> 0.6.0'
depends 'ulimit', '~> 0.3.2'
depends 'chef_handler', '~> 1.2.0'
# fixing the compat_resource cookbook that provides a compatibility layer for older chef versions and their concepts.
# here it provides mainly the compatibility layer for the old LWRP dsl which has been replaced in 12.5
depends 'compat_resource', '~> 12.16.3'
