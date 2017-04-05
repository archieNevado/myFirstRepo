## Modules

<dl>
<dt><a href="#module_logger">logger</a></dt>
<dd><p>Coremedia Logger module.</p>
<p>The Logger is disabled by default.</p>
<p>usage:
1) enable logging:
setLevel(LEVEL.ALL);</p>
<p>2) print to log:
log(&quot;log this&quot;);
debug(&quot;debug this&quot;);
info(&quot;info this&quot;);
warn(&quot;warn this&quot;);
error(&quot;error this&quot;);</p>
<p>3) disable logging:
setLevel(LEVEL.OFF);</p>
</dd>
</dl>

## Functions

<dl>
<dt><a href="#log">log(...args)</a></dt>
<dd><p>Prints a log to the JavaScript console.</p>
</dd>
<dt><a href="#info">info(...args)</a></dt>
<dd><p>Prints an informative logging information to the JavaScript console.</p>
</dd>
<dt><a href="#warn">warn(...args)</a></dt>
<dd><p>Prints an warning to the JavaScript console.</p>
</dd>
<dt><a href="#error">error(...args)</a></dt>
<dd><p>Prints an error to the JavaScript console.</p>
</dd>
<dt><a href="#setLevel">setLevel(level)</a> ⇒ <code><a href="#LEVEL">LEVEL</a></code></dt>
<dd><p>Sets the logging level.</p>
</dd>
<dt><a href="#setPrefix">setPrefix(prefix)</a> ⇒ <code>string</code></dt>
<dd><p>Sets the prefix for all console outputs.</p>
</dd>
<dt><a href="#getPrefix">getPrefix()</a> ⇒ <code>string</code></dt>
<dd><p>Returns the prefix for all console outputs.</p>
</dd>
</dl>

<a name="module_logger"></a>

## logger
Coremedia Logger module.The Logger is disabled by default.usage:1) enable logging:setLevel(LEVEL.ALL);2) print to log:log("log this");debug("debug this");info("info this");warn("warn this");error("error this");3) disable logging:setLevel(LEVEL.OFF);

<a name="LEVEL"></a>

## LEVEL : <code>enum</code>
Logging LevelsThe logging levels are cumulative. If you for example set the logging level to WARN all warnings, errors and fatals are logged.OFF - nothing is loggedERROR - errors are loggedWARN - warnings are loggedINFO - infos are loggedLOG - log messages are loggedALL - everything is logged

**Kind**: global enum  
**Read only**: true  
<a name="log"></a>

## log(...args)
Prints a log to the JavaScript console.

**Kind**: global function  

| Param | Type | Description |
| --- | --- | --- |
| ...args | <code>object</code> | a) A list of JavaScript objects which are appended together in the order listed and output. b) A JavaScript string containing zero or more substitution strings followed by JavaScript objects with which to replace substitution strings within the first parameter. This gives you additional control over the format of the output. |

<a name="info"></a>

## info(...args)
Prints an informative logging information to the JavaScript console.

**Kind**: global function  

| Param | Type | Description |
| --- | --- | --- |
| ...args | <code>object</code> | a) A list of JavaScript objects which are appended together in the order listed and output. b) A JavaScript string containing zero or more substitution strings followed by JavaScript objects with which to replace substitution strings within the first parameter. This gives you additional control over the format of the output. |

<a name="warn"></a>

## warn(...args)
Prints an warning to the JavaScript console.

**Kind**: global function  

| Param | Type | Description |
| --- | --- | --- |
| ...args | <code>object</code> | a) A list of JavaScript objects which are appended together in the order listed and output. b) A JavaScript string containing zero or more substitution strings followed by JavaScript objects with which to replace substitution strings within the first parameter. This gives you additional control over the format of the output. |

<a name="error"></a>

## error(...args)
Prints an error to the JavaScript console.

**Kind**: global function  

| Param | Type | Description |
| --- | --- | --- |
| ...args | <code>object</code> | a) A list of JavaScript objects which are appended together in the order listed and output. b) A JavaScript string containing zero or more substitution strings followed by JavaScript objects with which to replace substitution strings within the first parameter. This gives you additional control over the format of the output. |

<a name="setLevel"></a>

## setLevel(level) ⇒ <code>[LEVEL](#LEVEL)</code>
Sets the logging level.

**Kind**: global function  

| Param | Type | Description |
| --- | --- | --- |
| level | <code>[LEVEL](#LEVEL)</code> | Level to be set. |

<a name="setPrefix"></a>

## setPrefix(prefix) ⇒ <code>string</code>
Sets the prefix for all console outputs.

**Kind**: global function  

| Param | Type |
| --- | --- |
| prefix | <code>string</code> | 

<a name="getPrefix"></a>

## getPrefix() ⇒ <code>string</code>
Returns the prefix for all console outputs.

**Kind**: global function  
