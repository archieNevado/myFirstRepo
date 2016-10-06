# CoreMedia Blueprint

## Themes

Themes are skins for websites. They include

- CSS (Sass)
- JavaScript
- Freemarker Templates
- other static files like fonts, images and more
- and optionally third-party frameworks and plugins like e.g. bootstrap

A CoreMedia Blueprint Theme should also include following files, as they depend on [npm](https://www.npmjs.com/) and [grunt](http://gruntjs.com/)

- [package.json](https://docs.npmjs.com/getting-started/using-a-package.json)
- [Gruntfile.js](http://gruntjs.com/getting-started#the-gruntfile)
- Theme descriptor (CoreMedia xml file used by theme-importer)

### Available Themes

- [Corporate](corporate-theme/README.md)
- [Perfect Chef](perfectchef-theme/README.md)
- [Aurora](aurora-theme/README.md)

An empty example-theme is located in [examples/frontend/example-theme](../../../examples/frontend/example-theme/README.md)

### Availabe Grunt Tasks

Following grunt tasks are predefined and globally available. 

- `clean`: Cleans target folder for a theme
- `copy`: Copy all files to target folder
    - `copy:main`
    - `copy:templates`
- `compress`: Generates and copy templates.jar and theme.zip to target folder
    - `compress:templates`
    - `compress:theme`
- `jshint`: Calls [jshint](http://jshint.com/) for all javascript files 
- `postcss`: Calls [autoprefixer](https://github.com/postcss/autoprefixer) for all CSS files and generates sourcemaps
- `watch`: Watch Task with livereload for development

Just add following lines to your Gruntfile.js to make them avaialbe for your tasks.

```
    // import coremedia utils to simply reuse all available grunt tasks.
    var utils = require('@coremedia/utils');
    
    // load all available tasks
    utils.loadGruntTasks(grunt);
    
    // load all available default configs
    utils.loadGruntConfigs(grunt);
```

List all available grunt tasks

```$ grunt availabletasks```
