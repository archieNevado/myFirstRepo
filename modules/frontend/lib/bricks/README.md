# CoreMedia Blueprint

## Bricks

Bricks are reusable frontend modules for your theme. Mostly they include templates, some styles and javascript functions.

### Available bricks

- `bootstrap`: Bootstrap 3 based on sass with carousel swipe plugin
- `cta`: simple Call-To-Action button
- `elastic-social`: CoreMedia Elastic Social Extension
- [generic-templates](generic-templates/README.md): basic templates for teaser, hero teaser and detailview
- [fragment-scenario](fragment-scenario/README.md): templates rendering externally requested fragment. e.g. used for the IBM blended hybrid or augmentation scenario
- `image-maps`: Image Maps
- `preview`: templates for Studio preview
- `responsive-images`: CoreMedia Adaptive and Responsive Image Framework

### Usage

Just add following line to your theme to get the templates and javascript files. Don't forget to add them to your theme 
descriptor.

```
    // import coremedia utils to simply reuse all available grunt tasks.
    var utils = require('@coremedia/utils');
    
    // load a single brick 
    utils.loadBrick(grunt, "name-of-the-brick");
    
    // load multiple bricks 
    utils.loadBricks(grunt, ["name-of-the-brick-foo", "name-of-the-brick-bar"]);
```

If a brick has sass files, you need to import them to your themes sass file explicitly.
