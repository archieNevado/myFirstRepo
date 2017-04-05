<a name="coremedia.blueprint.$.fn.responsiveImages"></a>

## coremedia.blueprint.$.fn.responsiveImages()
Picks a suitable image from a given set of images regarding given dimensions and the maximum size needed.

**Kind**: global function  
**Summary**: Responsive Image Resizer jQuery Plugin  
**Version**: 1.6  
**Copyright**: CoreMedia AG  
**Example**  
###### Usage```javascript$(".cm-image--responsive").responsiveImage();```###### HTML```html<img src="image3x1.jpg" class="cm-image--responsive" data-cm-responsive-image="[ {   "name" : "3x1",   "ratioWidth" : 3,   "ratioHeight" : 1,   "linksForWidth" : {"320": "image3x1_small.jpg", "640": "image_medium.jpg", "1024": "image_large.jpg"} }, {   "name" : "2x1",   "ratioWidth" : 2,   "ratioHeight" : 1,   "linksForWidth" : {"200": "image2x1_small.jpg", "400": "image2x1_other.jpg"} }]" />```Deprecated legacy format:```html<img src="image3x1.jpg" class="cm-image--responsive" data-cm-responsive-image="{   "3x1" : {"320": "image3x1_small.jpg", "640": "image_medium.jpg", "1024": "image_large.jpg"},   "2x1" : {"200": "image2x1_small.jpg", "400": "image2x1_other.jpg"}}" />```
