# CoreMedia Blueprint

## Example Theme

This theme is an empty example with all mandatory files and configs to start a new theme.

### Installation

Copy this folder to ```modules/frontend/themes```, navigate to this folder ```modules/frontend/themes/example-theme``` 
and install all modules using [npm](https://www.npmjs.com/):

```$ npm install```

### Configuration

Rename the folder to your needs, and change the theme name in [package.json](package.json), and in the 
[theme-descriptor](example-theme.xml).

### Available Grunt commands

#### ```grunt build``` (Compile CSS and JavaScript and generate theme zip file)

Generates and copy all webresources, like compiled CSS, fonts, images and JavaScript files to 
the ```target/resources/themes/example``` directory for deployment and development.

#### ```grunt watch``` (Watch)

Watches the CSS, Javascript source files and automatically recompiles them whenever you save a change. 
If you have openend a page of this theme in your browser, it will automatically reload too.
