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
- [Theme descriptor (CoreMedia xml file used by theme-importer)](DESCRIPTOR.md)

### Available Themes

- [Corporate](corporate-theme/README.md)
- [Perfect Chef](perfectchef-theme/README.md)
- [Aurora](aurora-theme/README.md)

An empty example-theme is located in [examples/frontend/example-theme](../../../examples/frontend/example-theme/README.md)

### Default Grunt Tasks

All available themes include three registered tasks to be run via grunt. Every theme should include at least the task `production`.
This task is called via `npm postinstall`.

- `development`: Copy and generate all sources files into the target folder for local development.
- `production`: Run `development`, postprocessing like `eslint`, `postcss` and create templates.jar and theme.zip for deployment.
- `import`: Run `production` and import the theme to your CMS.


### Availabe Grunt Tasks

Following grunt tasks are predefined and globally available. 

- `clean`: Clean target folder for a theme
- `compress`: Generate and copy templates.jar and theme.zip to target folder
    - `compress:templates`
    - `compress:theme`
- `copy`: Copy all files to target folder including enabled bricks
- `eslint`: Call [eslint](http://eslint.org/) for all javascript files 
- `postcss`: Call [autoprefixer](https://github.com/postcss/autoprefixer) for all CSS files and generates sourcemaps
- `shell:import_themes`: Import Themes via ThemeImporter to your CMS
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
