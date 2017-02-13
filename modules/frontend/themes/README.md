# CoreMedia Blueprint Frontend

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

- [Aurora](aurora-theme/README.md)
- [Corporate](corporate-theme/README.md)

### Default Npm Scripts

All available themes include two [npm-scripts](https://docs.npmjs.com/misc/scripts).

- `production`: Generate theme.zip for deployment including all styles, resourcebundles and templates.
- `start`: Watch file changes and update theme on a remote or local CAE.

### Default Grunt Tasks

All available themes include three registered tasks to be run via grunt. Every theme should include at least the task `production`. This task is called via `mvn`.

- `development`: Copy and generate all sources files into the target folder for local development.
- `production`: Run `development`, postprocessing like `eslint`, `postcss` and create templates.jar and theme.zip for deployment.
- `monitor`: Watch file changes and update theme on remote or local CAE.

### Availabe Grunt Tasks

List all available (global) grunt tasks like clean, copy, sass for usage in themes.

```$ grunt availabletasks```
