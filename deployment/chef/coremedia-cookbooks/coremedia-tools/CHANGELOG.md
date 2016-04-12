coremedia-tools Cookbook CHANGELOG
======================
This file is used to list changes made in each version of the coremedia-tools cookbook.

1.0.3
--------
- add `checksum` parameter to tools definition to check SHA-256 checksum on download.

1.0.2
--------
- by using the parameters `nexus_url` and `nexus_repo` artifacts can now be resolved using metaversions like `RELEASE`, `LATEST` and
  the standard `-SNAPSHOT` semantic. If both `nexus_repo` and `repository_url` are defined, nexus rest will be preferred. Requires 
  the `coremedia_maven` cookbook in version `>= 2.0.2`.
  
1.0.1
--------
- remove chef resource clone warnings

1.0.0
--------
- update to `coremedia_maven` `~> 2.0`
- parameter `update_snapshots` has been removed, it is not necessary any more. The `coremedia_maven` cookbook now takes 
care of deleting the `extract_to` directory on changed artifact.

0.1.1
--------
- fix update of snapshot or release versions

0.1.0
--------
- initial release
