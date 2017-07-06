# Grunt tasks

Tasks contained in this directory will be loaded by default by the init.js script which is called in each Gruntfile.js. 

## Available tasks

### Monitor

_Run this task with the `grunt monitor` command._

Watch file changes and update theme on CAE. To ensure that the theme is up to date on CAE, this task runs `remoteThemeImporter:uploadTheme` to provide the current version of the theme to CAE.

Options may be specified according to the grunt [Configuring tasks](http://gruntjs.com/configuring-tasks) guide.

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

##### livereload.key
Type: `String`

This defines the key file content of a certificate to be used for running live reload server over ssl.

##### livereload.cert
Type: `String`

This defines the crt file content of a certificate to be used for running live reload server over ssl.

### remoteThemeImporter

_Run this task with the `grunt remoteThemeImporter:[target]` command._

Grunt Task for remote file operations of themes.

#### Targets

The following targets may be used.

##### login

_Run this task with the `grunt remoteThemeImporter:login` command._

This task prompts to enter the URL of the Studio, the username and password. After that it requests an API key from the 
server and creates a `cmconfig.json` file in the root directory of the frontend workspace containing the URL of the Studio and the API key. 

##### logout

_Run this task with the `grunt remoteThemeImporter:logout` command._

This task performs a logout of the user and removes the `cmconfig.json` file.

##### whoami

_Run this task with the `grunt remoteThemeImporter:whoami` command._

This task displays the name and id of the user who created the API key contained in the `cmconfig.json` file.

##### uploadTheme

_Run this task with the `grunt remoteThemeImporter:uploadTheme` command._

This task builds the theme by running grunt task `production` and uploads the previously built theme to the CAE.

