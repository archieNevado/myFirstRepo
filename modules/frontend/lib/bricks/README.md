# CoreMedia Blueprint

## Bricks

Bricks are reusable frontend modules for your theme. Mostly they include templates, some styles and javascript functions.

### Available bricks

- **[bootstrap](bootstrap/README.md)**: 
  Bootstrap 3 based on sass with carousel swipe plugin
- **[cta](cta/README.md)**: 
  Simple Call-To-Action button
- **[download-portal](download-portal/README.md)**: 
  CoreMedia Asset Management Download Portal Extension
- **[elastic-social](elastic-social/README.md)**: 
  CoreMedia Elastic Social Extension
- **[fragment-scenario](fragment-scenario/README.md)**: templates rendering externally requested fragment. e.g. used for the IBM blended hybrid or augmentation scenario
- **[generic-templates](generic-templates/README.md)**: This brick adds support for rendering items in various occurrences, like detail, hero and teaser view.
- **image-maps**: Image Maps Feature
- **[livecontext](livecontext/README.md)**: CoreMedia LiveContext Extension
- **pdp-augmentation**: Product Detail Page enhancement for CoreMedia LiveContext Extension
- **preview**: Studio preview templates and styles
- **responsive-images**: CoreMedia Adaptive and Responsive Image Framework
- **shoppable-video**: Shoppable Video Feature

### Usage

Just add following config to your theme `Gruntfile.js` to get the templates and javascript files. Don't forget to add them to your theme descriptor.

```
  grunt.initConfig({
    ...
    // load bricks into theme
    bricks: {
      src: [
        '<name-of-the-brick>',
        ...
      ]
    },
    ...
  });
  
  // load CoreMedia initialization
  require('../../lib/tools/grunt/scripts/init')(grunt);
```

If a brick has sass files, you need to import them to your themes sass file explicitly.

**Notice:** _Don't forget to add Javascript files and templates of the used bricks to the theme descriptor file of your 
theme. See bricks description in README files._
