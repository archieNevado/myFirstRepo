# CoreMedia Blueprint

## generic-templates brick

This brick adds support for rendering beans in the following views:
- `detail`: Includes the all media items in a carousel, title, text, tags, related items and externally displayed date. 
- `hero`: Includes the first media item, teaser title, teaser text and a call-to-action button. 
- `teaser`: Includes the first media item, teaser title, teaser text and a call-to-action button. 

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

To use the included sass files, you need to load them explicitly in your sass file by adding:
```
     // Load brick scss file for detail viewl
     @import "../../../../../lib/bricks/generic-templates/sass/details";
     // Load brick scss file for hero teasers
     @import "../../../../../lib/bricks/generic-templates/sass/hero";
     // Load brick scss file for popups
     @import "../../../../../lib/bricks/generic-templates/sass/popup";
     // Load brick scss file for tags
     @import "../../../../../lib/bricks/generic-templates/sass/tags";
     // Load brick scss file for teasers
     @import "../../../../../lib/bricks/generic-templates/sass/teaser";
     // Load brick scss file for videos
     @import "../../../../../lib/bricks/generic-templates/sass/video";
```

To configure the loaded scss you can set variables before loading the corresponding scss files.
Have a look at e.g [_teaser.scss](sass/_teaser.scss) to get a list of available variables.
To override a default value, copy the variable to your sass file and set the value.
```
     $cm-teasable-prefix: "cm-teasable";
     // Load brick scss file for teasers
     @import "../../../../../lib/bricks/generic-templates/sass/teaser";
```


#### Using the Detail View

The detail view [template](./templates/com.coremedia.blueprint.common.contentbeans/CMTeasable.detail.ftl) works with all types and subtypes of type com.coremedia.blueprint.common.contentbeans.CMTeasable.
The following special views exist:

- CMGallery.detail.ftl
- CMImageMap.detail.ftl (as part of the [imagemap brick](../image-maps/README.md))
- CMProduct.detail.ftl
- CMTeasable.detail.ftl
- CMVideo.detail.ftl

To render an item in the detail view include the following code into your template:
```
    <@cm.include self=self view="detail" />
```
To configure the behavior of the template you can add the following parameters to the ```cm.include```

|Parameter Name|Type|Default|Description|
|---|---|---|---|
|additionalClass|String|```"cm-details"```|An additional css class that will be added to the div of the detail view.|
|carouselParams|Map|```{}```|A map of parameters to be passed into the templates being used to render each slide.|
|itemsClass|String|```""```|An additional css class that will be added to each slide.|
|relatedView|String|```related```|The view to include the container of the related items with.|
|renderDate|Boolean|```true```|Whether to display the externally displayed date or not.|
|renderRelated|Boolean|```true```|Whether to display related items or not.|
|renderTags|Boolean|```true```|Whether to display tags or not.|
|viewItems|String|```asTeaser```|The view for each related item to be rendered with.|


#### Using the Hero View

The hero view [template](./templates/com.coremedia.blueprint.common.contentbeans/CMTeasable.hero.ftl) works with all types and subtypes of type com.coremedia.blueprint.common.contentbeans.CMTeasable.
The following special views exist:

- CMHTML.hero.ftl
- CMImageMap.hero.ftl (as part of the [imagemap brick](../image-maps/README.md))
- CMProductTeaser.hero.ftl
- CMTeasable.hero.ftl
- CMVideo.hero.ftl

To render an item in the detail view include the following code into your template:
```
    <@cm.include self=self view="hero" />
```
To configure the behavior of the template you can add the following parameters to the ```cm.include```

|Parameter Name|Type|Default|Description|
|---|---|---|---|
|additionalClass|String|```"cm-hero"```|An additional css class that will be added to the div of the view.|
|renderCTA|Boolean|```true```|Whether to display the call to action button or not.|
|renderDimmer|Boolean|```true```|Whether to display a dimmer on top of the image or not.|
|renderEmptyImage|Boolean|```true```|Whether to display an emtpy media element if no media has been linked or not.|
|renderTeaserText|Boolean|```true```|Whether to display the teaser text or not.|


#### Using the Teaser View

The teaser view [template](./templates/com.coremedia.blueprint.common.contentbeans/CMTeasable.teaser.ftl) works with all types and subtypes of type com.coremedia.blueprint.common.contentbeans.CMTeasable.
The following special views exist:

- CategoryInSite.teaser.ftl (as part of the [livecontext brick](../livecontext/README.md))
- CMDownload.teaser.ftl
- CMGallery.teaser.ftl
- CMHTML.teaser.ftl
- CMSpinner.teaser.ftl
- CMImageMap.teaser.ftl (as part of the [imagemap brick](../image-maps/README.md))
- CMProductTeaser.teaser.ftl
- CMTeasable.teaser.ftl
- CMVideo.teaser.ftl
- LiveContextExternalChannel.teaser.ftl (as part of the [livecontext brick](../livecontext/README.md))
- ProductInSite.teaser.ftl (as part of the [livecontext brick](../livecontext/README.md))


To render an item in the detail view include the following code into your template:
```
    <@cm.include self=self view="teaser" />
```
To configure the behavior of the template you can add the following parameters to the ```cm.include```

|Parameter Name|Type|Default|Description|
|---|---|---|---|
|additionalClass|String|```"cm-teasable"```|An additional css class that will be added to the div of the view.|
|limitAspectRatiosKey|String|```"teaser"```|An string suffix for a settings based aspect ration lookup to be used for the responsive image.|
|limitAspectRatios|List|```Falls back to a settings lookup. If settings are empty: []```|A list of aspect ratios that can be used for the responsive image.|
|renderCTA|Boolean|```true```|Whether to display the call to action button or not.|
|renderDimmer|Boolean|```true```|Whether to display a dimmer on top of the image or not.|
|renderEmptyImage|Boolean|```true```|Whether to display an emtpy media element if no media has been linked or not.|
|renderLink|Boolean|```true```|Whether to render a link or not.|
|renderTeaserText|Boolean|```true```|Whether to display the teaser text or not.|
|renderTeaserTitle|Boolean|```true```|Whether to display the teaser title or not.|