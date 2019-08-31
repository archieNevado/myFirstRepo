tools container
===============

This tools container does:
* restore users - entrypoint `import-user`
* import content - entrypoint `import-content`
* upload default workflows - entrypoint `import-default-workflows`
* upload custom workflow, next arg is the path to a custom workflow definitions. To upload multiple workflows execute this step multiple times.
* publish content - entrypoint `publish-content`.
* export content - entrypoint `export-content`, next arg is the list of exported paths, quote it as one arg!.

To achieve this, we make use of some entrypoint script logic and volumes. Importing, restoring and uploading
will be done only once. To ensure this, we query the content- and workflow-repository using `cm` tools.
If you want to reimport everything, you can use the environment variable toggle described
below.

Directories that can be mounted as Volumes
------------------------------------------

`/coremedia/import/content` - all content mounted below this path will be imported.
`/coremedia/import/users` - all user files mounted below this path will be restored.
`/coremedia/export` - the dir to export the content to.

Environment Variables
---------------------

* `SKIP_CONTENT` - if set to `true`, the whole import chain will be skipped.
* `FORCE_REIMPORT_CONTENT` - if set to `true`, marker files will be ignored and content reimported.
* `BLOB_STORAGE_URL` - if set to a http url, the process expects the blobs to be provided there for import.
* `CONTENT_ARCHIVE_URL` - if set to a http url pointing to a `content-users.zip`
as produced by the `test-data` maven module from the Blueprint workspace.


General purpose tools
---------------------

Using the default entrypoint all default tools can be used, i.e.

```
docker-compose run --rm management-tools /coremedia/tools/bin/cm dump
```

Customize the entrypoint chain in docker-compose
------------------------------------------------

Just add entrypoint list to your `management-tools` service, i.e.

```yaml

version: "3"

services:
  management-tools:
    ...
    command:
      - import-content
      - import-user
      - export-content
      - "PATH1 PATH2 PATH3"
```
