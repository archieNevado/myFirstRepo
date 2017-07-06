name 'blueprint-lc3-ibm-wcs'
maintainer 'The Authors'
maintainer_email 'you@example.com'
license 'all_rights'
description 'Installs/Configures CoreMedia LiveContext for IBM Websphere Commerce'
long_description IO.read(File.join(File.dirname(__FILE__), 'README.md'))
version '1.0.0'

depends 'blueprint-base'
depends 'blueprint-tomcat'
depends 'blueprint-proxy'
depends 'coremedia-proxy', '~> 0.3.2'
