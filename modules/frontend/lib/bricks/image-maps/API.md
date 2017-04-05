## Modules

<dl>
<dt><a href="#module_coremedia.blueprint.imagemap">coremedia.blueprint.imagemap</a> : <code>object</code></dt>
<dd><p>Imagemap functionality</p>
</dd>
<dt><a href="#module_coremedia.blueprint.quickInfo">coremedia.blueprint.quickInfo</a> : <code>object</code></dt>
<dd><p>Quickinfo functionality</p>
</dd>
</dl>

<a name="module_coremedia.blueprint.imagemap"></a>

## coremedia.blueprint.imagemap : <code>object</code>
Imagemap functionality


* [coremedia.blueprint.imagemap](#module_coremedia.blueprint.imagemap) : <code>object</code>
    * [~coordsConverter](#module_coremedia.blueprint.imagemap..coordsConverter) : <code>object</code>
    * [~calculateBoundingBox(coordsAsPoints)](#module_coremedia.blueprint.imagemap..calculateBoundingBox) ⇒ <code>object</code>
    * [~update($imagemap, [newRatio])](#module_coremedia.blueprint.imagemap..update)
    * [~init($imagemap)](#module_coremedia.blueprint.imagemap..init)
    * [~converterMap](#module_coremedia.blueprint.imagemap..converterMap) : <code>object</code>

<a name="module_coremedia.blueprint.imagemap..coordsConverter"></a>

### coremedia.blueprint.imagemap~coordsConverter : <code>object</code>
Maps possible values for the attribute shape of the HTML map element to converter functions (both directions).

**Kind**: inner property of <code>[coremedia.blueprint.imagemap](#module_coremedia.blueprint.imagemap)</code>  
**Properties**

| Name | Type |
| --- | --- |
| coordsTo | <code>converterMap</code> | 
| toCoords | <code>converterMap</code> | 

<a name="module_coremedia.blueprint.imagemap..calculateBoundingBox"></a>

### coremedia.blueprint.imagemap~calculateBoundingBox(coordsAsPoints) ⇒ <code>object</code>
Calculates a bounding box for given points.Points are objects with 2 properties: x and y representing the point.

**Kind**: inner method of <code>[coremedia.blueprint.imagemap](#module_coremedia.blueprint.imagemap)</code>  

| Param | Type |
| --- | --- |
| coordsAsPoints | <code>Array</code> | 

<a name="module_coremedia.blueprint.imagemap..update"></a>

### coremedia.blueprint.imagemap~update($imagemap, [newRatio])
Recalculates all areas of the imagemap for the actual dimensions the imagemap has.

**Kind**: inner method of <code>[coremedia.blueprint.imagemap](#module_coremedia.blueprint.imagemap)</code>  

| Param | Type | Description |
| --- | --- | --- |
| $imagemap | <code>jQuery</code> | The imagemap element to update. |
| [newRatio] | <code>number</code> | If there was an aspect ratio switch, this is the new ratio to be used. |

<a name="module_coremedia.blueprint.imagemap..init"></a>

### coremedia.blueprint.imagemap~init($imagemap)
Initializes an imagemap element.

**Kind**: inner method of <code>[coremedia.blueprint.imagemap](#module_coremedia.blueprint.imagemap)</code>  

| Param | Type | Description |
| --- | --- | --- |
| $imagemap | <code>jQuery</code> | The imagemap element to initialize. |

<a name="module_coremedia.blueprint.imagemap..converterMap"></a>

### coremedia.blueprint.imagemap~converterMap : <code>object</code>
**Kind**: inner typedef of <code>[coremedia.blueprint.imagemap](#module_coremedia.blueprint.imagemap)</code>  
**Properties**

| Name | Type |
| --- | --- |
| rect | <code>function</code> | 
| circle | <code>function</code> | 
| poly | <code>function</code> | 
| rectangle | <code>function</code> | 
| circ | <code>function</code> | 
| polygon | <code>function</code> | 
| default | <code>function</code> | 

<a name="module_coremedia.blueprint.quickInfo"></a>

## coremedia.blueprint.quickInfo : <code>object</code>
Quickinfo functionality


* [coremedia.blueprint.quickInfo](#module_coremedia.blueprint.quickInfo) : <code>object</code>
    * [~EVENT_QUICKINFO_CHANGED](#module_coremedia.blueprint.quickInfo..EVENT_QUICKINFO_CHANGED) : <code>string</code>
    * [~show($quickinfo)](#module_coremedia.blueprint.quickInfo..show)
    * [~hide($quickinfo)](#module_coremedia.blueprint.quickInfo..hide)
    * [~toggle($quickinfo)](#module_coremedia.blueprint.quickInfo..toggle)
    * [~groupHide($quickinfo, group)](#module_coremedia.blueprint.quickInfo..groupHide)
    * [~closeQuickInfo($quickinfo)](#module_coremedia.blueprint.quickInfo..closeQuickInfo)
    * [~toggleQuickInfo($button, $config)](#module_coremedia.blueprint.quickInfo..toggleQuickInfo)

<a name="module_coremedia.blueprint.quickInfo..EVENT_QUICKINFO_CHANGED"></a>

### coremedia.blueprint.quickInfo~EVENT_QUICKINFO_CHANGED : <code>string</code>
name of the event to be triggered if quick info has changed

**Kind**: inner property of <code>[coremedia.blueprint.quickInfo](#module_coremedia.blueprint.quickInfo)</code>  
<a name="module_coremedia.blueprint.quickInfo..show"></a>

### coremedia.blueprint.quickInfo~show($quickinfo)
Opens a quickinfo.

**Kind**: inner method of <code>[coremedia.blueprint.quickInfo](#module_coremedia.blueprint.quickInfo)</code>  

| Param | Type | Description |
| --- | --- | --- |
| $quickinfo | <code>jQuery</code> | The quickinfo to be opened. |

<a name="module_coremedia.blueprint.quickInfo..hide"></a>

### coremedia.blueprint.quickInfo~hide($quickinfo)
Hides a quickinfo.

**Kind**: inner method of <code>[coremedia.blueprint.quickInfo](#module_coremedia.blueprint.quickInfo)</code>  

| Param | Type | Description |
| --- | --- | --- |
| $quickinfo | <code>jQuery</code> | The quickinfo to be hidden. |

<a name="module_coremedia.blueprint.quickInfo..toggle"></a>

### coremedia.blueprint.quickInfo~toggle($quickinfo)
Opens a quickinfo, if it is hidden or hides a quickinfo, if it is shown.

**Kind**: inner method of <code>[coremedia.blueprint.quickInfo](#module_coremedia.blueprint.quickInfo)</code>  

| Param | Type | Description |
| --- | --- | --- |
| $quickinfo | <code>jQuery</code> | The quickinfo to be toggled. |

<a name="module_coremedia.blueprint.quickInfo..groupHide"></a>

### coremedia.blueprint.quickInfo~groupHide($quickinfo, group)
Hides a quickinfo, if it is in the given group.

**Kind**: inner method of <code>[coremedia.blueprint.quickInfo](#module_coremedia.blueprint.quickInfo)</code>  

| Param | Type | Description |
| --- | --- | --- |
| $quickinfo | <code>jQuery</code> | The quickinfo to be hidden, if it is in the given group. |
| group | <code>string</code> | The given group. |

<a name="module_coremedia.blueprint.quickInfo..closeQuickInfo"></a>

### coremedia.blueprint.quickInfo~closeQuickInfo($quickinfo)
Hides a quickinfo, if user clicked close button.

**Kind**: inner method of <code>[coremedia.blueprint.quickInfo](#module_coremedia.blueprint.quickInfo)</code>  

| Param | Type | Description |
| --- | --- | --- |
| $quickinfo | <code>jQuery</code> | The quickinfo to be hidden. |

<a name="module_coremedia.blueprint.quickInfo..toggleQuickInfo"></a>

### coremedia.blueprint.quickInfo~toggleQuickInfo($button, $config)
Opens a quickinfo, if it is hidden or hides a quickinfo, if it is shown.

**Kind**: inner method of <code>[coremedia.blueprint.quickInfo](#module_coremedia.blueprint.quickInfo)</code>  

| Param | Type | Description |
| --- | --- | --- |
| $button | <code>jQuery</code> | The button clicked to toggle the quickinfo. |
| $config | <code>jQuery</code> | The given config. |

