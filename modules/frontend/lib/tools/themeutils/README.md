# themeutils

>  The CoreMedia utils for themes including default configurations and tasks.

Install this into your workspace and you'll have access to the `themeutils`.

```shell
npm install file:lib/tools/themeutils
```

## Usage

Add the following line below your theme and bricks configuration in the theme's `Gruntfile.js`

```
  // load CoreMedia initialization
  require('@coremedia/themeutils')(grunt);
```

## Available grunt tasks

Tasks contained in this directory will be loaded by default by the index.js script which is called in each Gruntfile.js.

### Monitor

_Run this task with the `grunt monitor` command._

Watch file changes and update theme on CAE. To ensure that the theme is up to date on CAE, this task runs `remoteThemeImporter:uploadTheme` to provide the current version of the theme to CAE.

Custom values for the following options may be specified at frontend/config/env.json using the property `monitor`.

#### Options

##### target
Type: `String`
Default: `remote`

This defines, if the CAE is running local or remote.

##### livereload.host
Type: `String`
Default: `localhost`

This defines the host of the live reload server.

##### livereload.port
Type: `Number`
Default: `35729`

This defines the port the live reload server listens on.

#### Example

```json
{
  "monitor": {
    "target": "remote",
    "livereload": {
      "host": "localhost",
      "port": 9000
    }
  }
}
```

If you use custom values for the livereload options in the `env.json`, make sure that you customize the LiveReload URL in the corresponding template `Page._developerMode.ftl`.

### remoteThemeImporter

_Run this task with the `grunt remoteThemeImporter:[target]` command._

Grunt Task for remote file operations of themes.

#### Targets

The following targets may be used.

##### login

_Run this task with the `grunt remoteThemeImporter:login` command._

This task prompts to enter the URL of the Studio and the preview as well as the username and password of a Studio user who is member of the group development. After that it requests an API key and creates an `apikey.txt` file containing the API key as well as an `env.json` file containing the URLs of Studio and Preview in the config directory of the frontend workspace. If the file `env.json` is already existing, it is only being updated.

##### logout

_Run this task with the `grunt remoteThemeImporter:logout` command._

This task performs a logout of the user and removes the `apikey.txt` file.

##### whoami

_Run this task with the `grunt remoteThemeImporter:whoami` command._

This task displays the name and id of the user who created the API key contained in the `apikey.txt` file.

##### uploadTheme

_Run this task with the `grunt remoteThemeImporter:uploadTheme` command._

This task builds the theme by running grunt task `build` and uploads the previously built theme to the CAE.
