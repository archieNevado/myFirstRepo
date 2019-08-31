<#ftl strip_whitespace=true>
<#-- @ftlvariable name="blueprintFreemarkerFacade" type="com.coremedia.blueprint.cae.web.taglib.BlueprintFreemarkerFacade" -->

<#-- -------------------------------------------------------------------------------------------------------------------
 *
 * Please check the section "CoreMedia FreeMarker API" in chapter "Reference" in the "Frontend Developer Guide" for
 * details and examples for the following directives.
 * Any changes, additions or removals need to be documented in the manual.
 *
 ------------------------------------------------------------------------------------------------------------------- -->

<#-- SET ASPECT RATIOS -->
<#function responsiveImageLinksData picture aspectRatios=[]>
  <#return blueprintFreemarkerFacade.responsiveImageLinksData(picture, cmpage, aspectRatios)>
</#function>

<#-- GET IMAGE LINK -->
<#function getBiggestImageLink picture aspectRatio="">
  <#return blueprintFreemarkerFacade.getLinkForBiggestImageWithRatio(picture, cmpage, aspectRatio)>
</#function>


<#-- --- DEPRECATED ------------------------------------------------------------------------------------------------ -->

<#--
 * Renders responsive images.
 *
 * @param self The item to render an image for
 * @param view (optional) the view to render the image with
 * @param classPrefix Set the classPrefix.
 * @param metadata (optional) if metadata shall be rendered it contains the value of param data for preview.metadata, default to ["properties.pictures"]
 * @param displayEmptyImage (optional) if set to true, a special div is rendered as a placeholder for a non existing image
 * @param background (optional) renders the image as a background attribute of a div instead of a standard img tag
 * @param additionalAttr (optional) additional attributes to be handed over to the responsive image renderer
 *
 * DEPRECATED
 -->
<#macro responsiveImage self classPrefix view="media" metadata=["properties.pictures"] displayEmptyImage=true background=false additionalAttr={} classSuffix="picture">
  <#local cssClassBox>${classPrefix}__${classSuffix}-box</#local>
  <#local cssClassImage>${classPrefix}__${classSuffix}</#local>
  <#if self?has_content && !cm.isUndefined(self)>
    <@cm.include self=self view=view params={
    "classBox": cssClassBox,
    "classMedia": cssClassImage,
    "metadata": metadata,
    "additionalAttr": additionalAttr,
    "background": background
    }/>
  <#elseif displayEmptyImage>
  <div class="${cssClassBox}" <@preview.metadata "properties.pictures" />>
    <div class="${cssClassImage} cm-media--missing"></div>
  </div>
  </#if>
</#macro>
