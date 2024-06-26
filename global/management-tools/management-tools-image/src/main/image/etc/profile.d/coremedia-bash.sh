cat <<EOF > /dev/stdout
=================================================================
To reconfigure the tools in the development setup against either:

 * content-management-server and cae-feeder-preview
 * master-live-server and cae-feeder-live
 * replication-live-server

use one of the following bash aliases:

 * tools-context dev/management
 * tools-context dev/master
 * tools-context dev/replication

 You can find the tools below /coremedia/tools, i.e. run:

 /coremedia/tools/bin/cm dump -u admin -p admin 1
 =================================================================
EOF

function tools-context() {
  CONFD_PREFIX=$1 /coremedia/confd
}
