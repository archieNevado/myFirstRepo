# CoreMedia Blueprint

## Aurora Theme

The Aurora Theme provides a modern, appealing, highly visual theme. It demonstrates the capability to build localizable, 
multi-national, experience-driven e-Commerce websites. Integration with IBM WebSphere Commerce ships out of the box.

Based on a fully responsive, mobile-first design paradigm, it leverages the Masonry dynamic grid framework. It
scales from mobile via tablet to desktop viewport sizes and uses the CoreMedia Adaptive and Responsive Image
Framework to dynamically deliver the right image sizes in the right aspect ratios and crops for each viewport.

This theme integrates the fragment-based approach seamless into Aurora B2C and B2B store examples.

### Installation

Navigate to this folder ```modules/frontend/themes/aurora-theme``` and install all modules using [npm](https://www.npmjs.com/):

```$ npm install```

### Available Grunt commands

#### ```grunt build``` (Compile CSS and JavaScript)

Generates and copy all webresources, like compiled CSS, fonts, images and JavaScript files to 
the ```target/resources/themes/aurora``` directory for deployment and development.

#### ```grunt watch``` (Watch)

Watches the CSS, Javascript source files and automatically recompiles them whenever you save a change. 
If you have openend a page of this theme in your browser, it will automatically reload too.

### Supported Browsers

See [browserlist configuration](package.json) in package.json (supportedBrowsers).
