# Bundling CoreMedia Plugins

Plugins are a way to extend the CoreMedia applications in a more loosely coupled way compared to Blueprint Extensions.
For more information see section [Application Plugins in the Blueprint Developer Manual](https://documentation.coremedia.com/cmcc-11/current/webhelp/coremedia-en/content/ApplicationPlugins.html).

CoreMedia provides some plugins, for instance the https://github.com/CoreMedia/content-hub-adapter-filesystem
plugin, which adds a filesystem backed content hub adapter to Studio.  Have a look at this plugin as an example.
Go to the https://github.com/CoreMedia/content-hub-adapter-filesystem/releases
page, and pick the latest version of the plugin.  The release consists of several artifacts, one of which
is a JSON file named after the plugin: `content-hub-adapter-filesystem-2.0.0.json`.  This is the 
_plugin-descriptor_, which every plugin must provide.

To bundle plugins with your application Docker images, you can provide plugin-descriptors in the
`plugin-descriptors` directory directly or reference them as URLs in `plugin-descriptors.json`.

A plugin consists of one or more _application-plugins_ for different applications of the CoreMedia system. The _application-plugins_ are the ZIP files of the plugin's artifacts. In the filesystem adapter example, you find _application-plugins_ for the _studio-client_ and _studio-server_ applications.

The plugin-descriptor maps the application-plugins links to the Blueprint applications.
To add the referenced application-plugins to the respective application workspaces, execute either the `sync` goal
of the `coremedia-plugins-maven-plugin` or just run a `mvn generate-resources` in `workspace-configuration/plugins`.

This will add the links of the application-plugins to the `plugins.json` files of the corresponding applications in the application workspaces.

In a particular project, you usually do not add or remove plugins very frequently, and this step is not 
part of the standard build turnaround.  Therefore, it is recommended that you commit the changed
`plugins.json` files in your version control system, rather than generating them over and over again.
(If you are familiar with the CoreMedia Extension Tool, you will recognize this process.)

The `plugins.json` files are then used in the build process of the distinct applications to download the application-plugins and add them to the Docker images.

So, the recommended workflow to bundle a plugin would be:
1. Go to `workspace-configuration/plugins`.
2. Add the URL of the plugin-descriptor to the `plugin-descriptors.json` file.
3. Execute `mvn generate-resources`.
4. Commit the changes.
5. Build the applications.

To remove a bundled plugin, simply remove the descriptor and re-run `mvn generate-resources` in `workspace-configuration/plugins`.
