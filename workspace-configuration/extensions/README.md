# Managing CoreMedia Extensions

## Getting Started

`cd` into this directory (`workspace-configuration/extensions`)

OR add `-f workspace-configuration/extensions` to every `mvn` command documented here.

For a list of all extension tool "goals" (commands) and their description, call
```
mvn extensions:help
```

For a detailed man page of some goal, including their `-D` options, call
```
mvn extensions:help -Dgoal=... -Ddetail
```

For specific use cases, find the mvn command lines below.

> NOTE: The command `mvn extensions:sync` modifies source files in your workspace. You should only call it when all
your work has been committed and your workspace is in a clean state.

## Listing All Available CoreMedia Extension Points
```
mvn extensions:list-extension-points -q
```

## Listing All Available CoreMedia Extensions
```
mvn extensions:list -q
```

The output indicates inactive extensions by a hash prefix ("#").

## Verifying Extension Consistency

In a clean Git repository state, run 
```
mvn extensions:sync
```
There should be no changed files afterwards.

## Disabling Extensions

```
mvn extensions:sync -Ddisable=<extension1>,<extension2>,...
```

Disables the listed extensions by removing all their (aggregator) modules from their aggregator POM
and removing their dependencies from the corresponding extension points. No files are deleted.

## Removing Extensions

```
mvn extensions:sync -Dremove=<extension1>,<extension2>,...
```

Removes the listed extensions by removing all their (aggregator) modules from their aggregator POM
and removing their dependencies from the corresponding extension points, and deletes all their source
files.

After removing an extension, it cannot be enabled again (only by restoring a previous state via Git).

Even if you want to get rid of an extension for good, using this command is discouraged because
deleting files leads to merge conflicts when updating to a new Blueprint Git state.

## Enabling Extensions

```
mvn extensions:sync -Denable=<extension1>,<extension2>,...
```

Enables the listed extensions by adding all their (aggregator) modules to their aggregator POM
and adding their dependencies to the corresponding extension points.

Note that this only works for disabled extensions, not for extensions that have been removed.

You can even enable "new" extensions, given they are placed at the specified locations. Each workspace
has a folder `modules/extensions` with subfolders that are named like the extension they contribute to.
You can also place a "centralized" extension below the top-level `modules/extensions` folder.
For details, see the help text of goal `sync`, especially its option `enable`, and the Blueprint
Developer Manual, section "Implementing a Custom Extension".

## Managing a Set of Extensions

```
mvn extensions:sync -DextensionsFile=<extensionsFilePathAndName>
```

Emulating the "task input file" of the previous extensions tool, this option allows to specify a file containing
the set of extensions to enable (or disable). All other extensions that are present in the workspace, but not
mentioned in the `extensionsFile`, are disabled or even removed, when the flag `-Dprune` is added to the
command (see "Removing Extensions").
