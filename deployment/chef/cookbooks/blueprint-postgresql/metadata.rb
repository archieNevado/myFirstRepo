name 'blueprint-postgresql'
maintainer 'The Authors'
maintainer_email 'you@example.com'
license 'all_rights'
description 'Installs/Configures blueprint-postgresql'
long_description 'Installs/Configures blueprint-postgresql'
version '1.0.0'

depends 'blueprint-base'
depends 'blueprint-yum'
depends 'yum-pgdg', '~> 2.0.1'
depends 'postgresql', '= 6.1.1'

# fix transitive dependencies
depends 'build-essential', '= 8.1.1'
depends 'openssl', '= 8.1.2'
depends 'seven_zip', '= 3.0.0'