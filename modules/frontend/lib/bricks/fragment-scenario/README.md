# CoreMedia Blueprint

## fragment-scenario brick

This brick adds support for rendering external requested fragments

### Usage

Just add following line to your Gruntfile.js to load the brick.

```
    // Load the "fragment-scenario" brick
    utils.loadBrick(grunt, "fragment-scenario");
```

If you want to load multiple bricks, you may also use the following syntax:
```
    utils.loadBricks(grunt, [..., "fragment-scenario", ...]);
```