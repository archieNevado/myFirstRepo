# CoreMedia Blueprint

## fragment-scenario brick

This brick adds support for rendering external requested fragments. The typically usecase for this brick is the commerce 
led or hybrid scenario, where CoreMedia delivers fragments for an eCommerce system.

### Usage

Just add following config to your Gruntfile.js to load the brick.

```
    // load bricks into theme
    bricks: {
      src: [
        'fragment-scenario',
        ...
      ]
    }
```


To use the included sass files, you need to load them explicitly in your sass file by adding:
```
     // Load brick scss file for detail viewl
     @import "../../../../../lib/bricks/fragment-scenario/sass/fragments";
```
