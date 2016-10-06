# CoreMedia Blueprint

## generic-templates brick

This brick adds support for rendering beans in the following views:
- `teaser`: Includes the first media item, teaser title, teaser text and a call-to-action button. 
- `hero`: Includes the first media item, teaser title, teaser text and a call-to-action button. 

### Usage

Just add following line to your Gruntfile.js to load the brick.

```
    // Load the "generic-template" brick
    utils.loadBrick(grunt, "generic-templates");
```

If you want to load multiple bricks, you may also use the following syntax:
```
     // Load the "generic-template" brick
    utils.loadBricks(grunt, [..., "generic-templates", ...]);
```

To use the included sass files, you need to load the explicitly in your sass file by adding:
```
     // Load brick scss file for teasers
     @import "../../../../../lib/bricks/generic-templates/sass/teaser";
     // Load brick scss file for hero teasers
     @import "../../../../../lib/bricks/generic-templates/sass/hero";
```
To configure the loaded scss you can set variables before loading the corresponsing scss files.
Have a look at e.g [_teaser.scss](sass/_teaser.scss) to get a list of available variables.
To override a default value, copy the variable to your sass file and set the value.
```
     $cm-teasable-prefix: "cm-teasable";
     // Load brick scss file for teasers
     @import "../../../../../lib/bricks/generic-templates/sass/teaser";
```
