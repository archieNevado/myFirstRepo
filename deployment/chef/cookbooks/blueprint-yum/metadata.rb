name 'blueprint-yum'
maintainer ''
maintainer_email ''
license 'all_rights'
description 'Configures yum repositories'
long_description IO.read(File.join(File.dirname(__FILE__), 'README.md'))
version '1.0.0'
depends 'yum', '~> 3.8'
depends 'yum-centos', '~> 0.4.5'

supports 'redhat'
supports 'centos'
supports 'amazon'
