# CoreMedia Blueprint

## Twitter Bootstrap Brick for version 3.3.7

This brick adds support for [Twitter Bootstrap](http://getbootstrap.com/). By loading this brick it will extend your theme with:

- bootstrap.js
- bootstrap-carousel-swipe.js

In addition, this brick comes with templates for rendering items as a:

- [Carousel](http://getbootstrap.com/javascript/#carousel) 
- [Twitter Bootstrap Grid](http://getbootstrap.com/css/#grid)
  

### Usage

#### Loading the Brick

Just add following config to your Gruntfile.js to load the brick.

```
    // load bricks into theme
    bricks: {
      src: [
        'bootstrap',
        ...
      ]
    }
```

#### Using the Carousel

The carousel [template](./templates/com.coremedia.blueprint.common.layout/Container.asCarousel.ftl) works with all content items of type com.coremedia.blueprint.common.layout.Container. To render the items as a carousel include the following code into your Container/Collection template
```
    <@cm.include self=self view="asCarousel" />
```
To configure the behavior of the template you can add the following parameters to the ```cm.include```

|Parameter Name|Type|Default|Description|
|---|---|---|---|
|additionalClass|String|```""```|An additional css class that will be added to the outer div of the carousel.|
|carouselItemParams|Map|```{}```|A map of parameters to be passed into the templates being used to render each slide.|
|controlIcon|String|```chevron```|The control icon to be used for sliding. Any [Glyphicons](http://getbootstrap.com/components/#glyphicons) may be used.|
|displayControls|Boolean|```true```|Whether to display the controls for the carousel or not.|
|displayPagination|Boolean|```false```|Whether to display a pagination or not.|
|modifier|String|```""```|A modifier for the carousel controls.|
|viewItemCssClass|String|```""```|An additional css class that will be added to each slide of the carousel.|
|viewItems|String|```asTeaser```|The view to be used to include each slide.|

#### Using the Grid

The grid [template](./templates/com.coremedia.blueprint.common.layout/Container.asGrid.ftl) works with all content items of type com.coremedia.blueprint.common.layout.Container. To render the items as a grid include the following code into your Container/Collection template
```
 <@cm.include self=self view="asGrid" />
```
To configure the behavior of the template you can add the following parameters to the ```cm.include```

|Parameter Name|Type|Default|Description|
|---|---|---|---|
|addRows|Boolean|```true```|Whether to render rows inbetween a certain amout of items or not.|
|additionalClass|String|```""```|An additional css class that will be added to the outer div of the grid.|
|center|Boolean|```true```|Whether to center items or not.|
|columnCssClass|String|```""```|An additional css class that will be added to each item.|
|itemsPerMobileRow|Integer|```1```|Amount of items to be displayed next to each other in mobile view.|
|itemsPerRow|Integer|```3```|Amount of items to be displayed next to each other.|
|modifier|String|```""```|A modifier for the carousel controls.|
|viewItemCssClass|String|```""```|An additional css class that will be added to each item of the grid.|
|viewItems|String|```asTeaser```|The view to be used to include each item.|
