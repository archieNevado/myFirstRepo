<#--
  Renders a given mapping of attributes and their values to be used in a html-tag.
  If an attribute has an empty value or the value is cm.UNDEFINED it will be omitted.
  The rendered output will always have a leading space and all attributes are rendered in a single line.

  @param attr contains a mapping of attribute names to their corresponding values.

  Example:
  <#assign attr={"style": "display: none;", "id": "exampleId"}/>
  <div class="example"<@utils.optionalAttributes attr/>></div>

  Note:
  For now uses the deprecated function to avoid code duplication as we cannot depend from "cae-viewservices-impl" to
  the frontend workspace.
-->
<#macro optionalAttributes attr><@cm.optionalAttributes nameValues=attr /></#macro>

<#--
  Renders a given mapping of attributes and their values to be used in a html-tag.

  @param attr contains a mapping of attribute names to their corresponding values.
  @param ignore contains a list of attribute names to ignore (e.g. when passing attr from a different source and some
                attributes should not be written)

  Example:
  <#if self.alt?has_content>
    <#assign alt=self.alt />
  </#if>
  <#assign imageLink=bp.uncroppedImageLink(self)/>
  <#assign attributes += {"alt": alt, "src": imageLink}/>
  <img src="#" class="cm-image" <@renderAttr attributes/>>

  Note:
  For now uses the deprecated function to avoid code duplication as we cannot depend from "cae-base-lib" to
  the frontend workspace.
-->
<#macro renderAttr attr={} ignore=[]>
  <@bp.renderAttr attr=attr ignore=ignore />
</#macro>

<#--
  Wraps a given content with a tag name and attributes if a condition results to "true". If the condition is "false"
  the tag gets omitted and only the nested content gets rendered.

  @param condition a boolean value which defines if the content is wrapped with a tag or not.
  @param tagName the name of the tag as a string, e.g. "a", "div", ...
  @param attr contains a mapping of attribute names to their corresponding values which are attached to the tag.

  Example:
  <@utils.optionalTag condition=calculateIfNeeded() tagName="div" attr={ "class": "wrapper" }>
    <img src="hello.jpg"/>
  </@utils.optionalTag>
-->
<#macro optionalTag condition tagName="div" attr={}>
  <#if condition><${tagName} <@renderAttr attr />></#if>
    <#nested>
  <#if condition></${tagName}></#if>
</#macro>

<#--
  Wraps the given nested content in a link tag with a given href and other additional attributes. The link can be opened
  in a new tab (optionally). If the given href is empty only the nested content will be rendered.

  @param href contains a string representing the href of the link tag
  @param openInNewTab a boolean value which defines if the link should be opened in a new tab
  @param attr contains a mapping of attribute names to their corresponding values which are attached to the link.

  Example:
  <@utils.optionalLink href=calculateLink() openInNewTab=true attr={ "class": "link" }>
    <img src="hello.jpg"/>
  </@utils.optionalLink>
-->
<#macro optionalLink href openInNewTab=false attr={}>
  <#local target=openInNewTab?then("_blank", "_self") />
  <#local rel=openInNewTab?then("noopener", "") />
  <@optionalTag condition=href?has_content tagName="a" attr={
    "href": href,
    "target": target,
    "rel": rel
  } + attr>
    <#nested>
  </@optionalTag>
</#macro>

<#--
  Renders a time-tag and converts a date to a localized String.

  @param date the date to render
  @param cssClass a CSS class to attach to the time-tag
  @param metadata preview metadata

  Example:
  <@renderDate date=self.externallyDisplayedDate.time
               cssClass="cm-detail__time"
               metadata=["properties.externallyDisplayedDate"] />

  Note:
  For now uses the deprecated function to avoid code duplication as we cannot depend from "cae-base-lib" to
  the frontend workspace.
-->
<#macro renderDate date cssClass="" metadata=[]>
  <@bp.renderDate contentDate=date cssClass=cssClass metadata=metadata />
</#macro>


<#--
  Renders given text with line breaks as <br>

  @param text The text as String.

  Example:
  <#if teaserText?has_content>
    <p>
      <@bp.renderWithLineBreaks teaserText/>
    </p>
  </#if>
-->
<#macro renderWithLineBreaks text>
  <@bp.renderWithLineBreaks text=text />
</#macro>
