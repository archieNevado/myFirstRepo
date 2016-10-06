# CoreMedia Blueprint

## Corporate Theme

The Corporate Theme provides a modern, appealing, highly visual theme. It demonstrates the capability to build 
localizable, multi-national, non-commerce websites.

Based on a fully responsive, mobile-first design paradigm, it leverages the Twitter Bootstrap Grid and Design framework 
for easy customization and adaptation by frontend developers. It scales from mobile via tablet to desktop viewport sizes 
and uses the CoreMedia Adaptive and Responsive Image Framework to dynamically deliver the right image sizes in the right 
aspect ratios and crops. The responsive navigation visualizes 3 levels, even though the navigation structure can be 
arbitrary deeply nested. The floating header and the footer can be configured and re-ordered in content settings.

### Used tools and libraries

- [Twitter Bootstrap](http://getbootstrap.com) 
- [Sass](http://sass-lang.com/) as CSS preprocessor
- [Freemarker](http://freemarker.org/) as template language
- [Grunt](http://gruntjs.com/) as build tool

### Installation

Navigate to this folder ```modules/frontend/themes/corporate-theme``` and install all modules using [npm](https://www.npmjs.com/):

```$ npm install```

### Available Grunt commands

#### ```grunt build``` (Compile CSS and JavaScript)

Generates and copy all webresources, like compiled CSS, fonts, images, JavaScript files and templates to 
the ```target/resources/themes/corporate``` directory for deployment and development.

#### ```grunt watch``` (Watch)

Watches the Sass, Javascript and Freemarker source files and automatically recompiles them whenever you save a change. 
If you have openend a page of this theme in your browser, it will automatically reload too.
