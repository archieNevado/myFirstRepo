# CoreMedia Blueprint

## Call To Action Button as a Brick

This brick encapsulates the rendering of a Call To Action Button.
  

### Usage

#### Loading the Brick

Just add following line to your Gruntfile.js to load the brick.

```
    // Load the "cta" brick
    utils.loadBrick(grunt, "cta");
```

If you want to load multiple bricks, you may also use the following syntax:
```
    utils.loadBricks(grunt, [..., "cta", ...]);
```

#### Using the Call To Action Partial

The Call To Action Button [template](./templates/com.coremedia.blueprint.common.contentbeans/CMTeasable._callToAction.ftl) works with all content items of type com.coremedia.blueprint.common.contentbeans.CMTeasable.
To render Call To Action Button include the following code into your CMTeasable template
```
    <@cm.include self=self view="_callToAction" />
```
To configure the behavior of the template you can add the following parameters to the ```cm.include```

|Parameter Name|Type|Default|Description|
|---|---|---|---|
|additionalClass|String|```""```|An additional css class that will be added to the Call To Action Button.|