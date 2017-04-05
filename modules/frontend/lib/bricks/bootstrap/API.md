## Members

<dl>
<dt><a href="#back-to-top button">back-to-top button</a></dt>
<dd><p>The back-to-top button enables the user to scroll to the top of the current page.</p>
</dd>
</dl>

## Functions

<dl>
<dt><a href="#coremedia.blueprint.$.fn.cmCarousel">coremedia.blueprint.$.fn.cmCarousel()</a></dt>
<dd><p>jQuery Plugin that hooks into Bootstrap carousel functionality via the <code>slid.bs.carousel</code> to add
Responsive Image Resizer Plugin and pagination.</p>
</dd>
</dl>

<a name="back-to-top button"></a>

## back-to-top button
The back-to-top button enables the user to scroll to the top of the current page.

**Kind**: global variable  
**Summary**: The back-to-top button enables the user to scroll to the top of the page.  
**Example**  
```html<a id="back-to-top" href="#" class="btn btn-primary cm-back-to-top" role="button"  ...```CoreMedia will automatically find and initialize a back-to-top button for any element that contains the id.Auto-initialization is not supported for a back-to-top button that is added to the DOM after jQuery's ready eventhas fired.
<a name="coremedia.blueprint.$.fn.cmCarousel"></a>

## coremedia.blueprint.$.fn.cmCarousel()
jQuery Plugin that hooks into Bootstrap carousel functionality via the `slid.bs.carousel` to addResponsive Image Resizer Plugin and pagination.

**Kind**: global function  
**Summary**: jQuery Plugin that hooks for Bootstrap carousel  
**Example**  
CoreMedia carousels can be automatically initialized simply by adding the data attribute `data-cm-carousel`to your carousel container element.```html<div class="cm-carousel carousel slide" data-cm-carousel='{"interval":"6000"}' ...```CoreMedia will automatically find and initialize a carousel for any element that contains this data attribute.If you do not want this behavior then do not add the data attribute to your carousel and instead initalize thecarousel programmatically by invoking the method on the carousel container element:```javascript$('[data-cm-carousel]').cmCarousel();```Auto-initialization is not supported for carousels that are added to the DOM after jQuery's ready event hasfired. In this case you will need to programatically initialize your carousel by invoking the method as shownabove.The carousel can be configured by assigning an object with the following properties to the `data-cm-carousel`attribute.| Name | Type | Default | Description || --- | --- | --- | --- || pause | <code>boolean</code> | <code>false</code> | Pause the carousel from sliding, if needed. || interval | <code>number</code> | <code>5000</code> | Interval used for each sliding. |
