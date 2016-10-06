# CoreMedia Blueprint

## livecontext brick

This brick adds support for rendering livecontext contentbeans

### Usage

Just add following line to your Gruntfile.js to load the brick.

```
    // Load the "livecontext" brick
    utils.loadBrick(grunt, "livecontext");
```

If you want to load multiple bricks, you may also use the following syntax:
```
    utils.loadBricks(grunt, [..., "livecontext", ...]);
```