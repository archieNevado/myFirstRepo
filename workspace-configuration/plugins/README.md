# Bundling CoreMedia Plugins

Plugins are a way to extend the CoreMedia applications in a more loosely coupled way compared to Blueprint Extensions.
For more information see section *Application Plugins* in the Blueprint Developer Manual.

To bundle plugins with your application Docker images, you can provide plugin-descriptors in the
`plugin-descriptors` directory directly or reference them as URLs in `plugin-descriptors.json`.

To add the referenced application-plugins to the respective application workspaces, you can execute the script `sync-plugins.groovy`
directly or via `mvn generate-resources` using the provided pom.

This will add the links to the plugins for the separate applications to the `plugins.json` files in the application workspaces.

The `plugins.json` files are then used in the build process of the distinct applications and will be added to the Docker images.

So, the recommended workflow to bundle a plugin would be:
1. Go to `workspace-configuration/plugins`.
2. Add the URL of the plugin-descriptor to the `plugin-descriptors.json` file.
3. Execute `mvn generate-resources`.
4. Commit the changes.
5. Build the applications.

To remove a bundled plugin, simply remove the descriptor and re-run `mvn generate-resources` in `workspace-configuration/plugins`.
