name 'blueprint-tomcat'
maintainer 'The Authors'
maintainer_email 'you@example.com'
license 'all_rights'
description 'Installs/Configures blueprint'
long_description IO.read(File.join(File.dirname(__FILE__), 'README.md'))
version '1.0.0'

depends 'blueprint-base'
depends 'coremedia_tomcat', '~> 2.1.0'
depends 'coremedia_maven', '~> 2.0.4'
depends 'chef-sugar', '~> 3.0'
depends 'java_se', '~> 8.144.0'
