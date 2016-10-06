
<#--
 * Renders responsive images.
 *
 * @param self The item to render an image for
 * @param view (optional) the view to render the image with
 * @param limitAspectRatios (optional) A list of allowed aspect ratios.
 * @param classPrefix Set the classPrefix.
 * @param metadata (optional) if metadata shall be rendered it contains the value of param data for cm.metadata, default to ["properties.pictures"]
 * @param displayEmptyImage (optional) if set to true, a special div is rendered as a placeholder for a non existing image
 * @param displayDimmer (optional) render a dimmer on top of the image
 * @param additionalAttr (optional) additional attributes to be handed over to the responsive image renderer
 -->
<#macro responsiveImage self classPrefix view="" limitAspectRatios=[]  metadata=["properties.pictures"], displayEmptyImage=true, displayDimmer=true, additionalAttr={}>
  <#local cssClassBox>${classPrefix}__picture-box</#local>
  <#local cssClassImage>${classPrefix}__picture</#local>
  <#local cssClassDimmer>${classPrefix}__dimmer</#local>
  <#if self?has_content && (self != cm.UNDEFINED)>
    <@cm.include self=self view=view params={
    "limitAspectRatios": limitAspectRatios,
    "classBox": cssClassBox,
    "classImage": cssClassImage,
    "metadata": metadata,
    "additionalAttr": additionalAttr
    }/>
    <#if displayDimmer>
    <div class="${cssClassDimmer}"></div>
    </#if>
  <#elseif displayEmptyImage>
  <div class="${cssClassBox}" <@cm.metadata "properties.pictures" />>
    <div class="${cssClassImage} cm-image--missing"></div>
  </div>
  </#if>
</#macro>