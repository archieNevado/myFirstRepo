# CoreMedia Blueprint

## Perfectchef Theme

The Perfectchef Theme provides a modern, appealing, highly visual theme. It demonstrates the capability
to build localizable, multi-national, experience-driven e-Commerce websites. Integration with IBM WebSphere Commerce
ships out of the box.

Based on a fully responsive, mobile-first design paradigm, it leverages the Bootstrap grid framework. It
scales from mobile via tablet to desktop viewport sizes and uses the CoreMedia Adaptive and Responsive Image
Framework to dynamically deliver the right image sizes in the right aspect ratios and crops for each viewport.

### Used tools and libraries

- [Freemarker](http://freemarker.org/) as template language
- [Grunt](http://gruntjs.com/) as build tool

### Installation

Navigate to this folder ```modules/frontend/themes/perfectchef-theme``` and install all modules using [npm](https://www.npmjs.com/):

```$ npm install```

### Available Grunt commands

#### ```grunt build``` (Compile CSS and JavaScript)

Generates and copy all webresources, like CSS, fonts, images, JavaScript files and templates to 
the ```target/resources/themes/perfectchef``` directory for deployment and development.

#### ```grunt watch``` (Watch)

Watches the CSS, Javascript and Freemarker source files and automatically recompiles them whenever you save a change. 
If you have openend a page of this theme in your browser, it will automatically reload too.
