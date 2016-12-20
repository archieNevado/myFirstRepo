## Modules

<dl>
<dt><a href="#module_utils">utils</a></dt>
<dd><p>Coremedia utils module.</p>
</dd>
</dl>

## Functions

<dl>
<dt><a href="#breakpoint">breakpoint()</a></dt>
<dd><p>Polyfill for window.matchMedia</p>
</dd>
<dt><a href="#domReady">domReady(f)</a></dt>
<dd><p>Executes the callback function, if the DOM is ready for JavaScript code to execute.</p>
</dd>
<dt><a href="#requestAnimFrame">requestAnimFrame()</a></dt>
<dd><p>Shim for window.requestAnimationFrame with setTimeout fallback.</p>
</dd>
</dl>

## Typedefs

<dl>
<dt><a href="#readyCallback">readyCallback</a> : <code>function</code></dt>
<dd></dd>
</dl>

<a name="module_utils"></a>

## utils
Coremedia utils module.

<a name="breakpoint"></a>

## breakpoint()
Polyfill for window.matchMedia

**Kind**: global function  
**See**: https://github.com/paulirish/matchMedia.js/  
<a name="domReady"></a>

## domReady(f)
Executes the callback function, if the DOM is ready for JavaScript code to execute.

**Kind**: global function  

| Param | Type | Description |
| --- | --- | --- |
| f | <code>[readyCallback](#readyCallback)</code> | Callback function to be executed. |

<a name="requestAnimFrame"></a>

## requestAnimFrame()
Shim for window.requestAnimationFrame with setTimeout fallback.

**Kind**: global function  
**See**: http://www.paulirish.com/2011/requestanimationframe-for-smart-animating  
<a name="readyCallback"></a>

## readyCallback : <code>function</code>
**Kind**: global typedef  
