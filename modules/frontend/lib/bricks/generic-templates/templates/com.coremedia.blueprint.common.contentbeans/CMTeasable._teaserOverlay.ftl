<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign teaserOverlaySettings=self.teaserOverlaySettings />
<#assign teaserOverlayStyle=self.teaserOverlayStyle />

<#assign overlayClass=[] />
<#if teaserOverlayStyle.cls?has_content>
  <#assign overlayClass=overlayClass + ["${teaserOverlayStyle.cls}"] />
</#if>

<#assign overlayStyle=[] />
<#assign positionX=teaserOverlaySettings.positionX + 50 />
<#assign positionY=teaserOverlaySettings.positionY + 50 />
<#assign width=(teaserOverlaySettings.width > 0)?then(teaserOverlaySettings.width, 50) />
<#assign overlayStyle=overlayStyle + ["left: ${positionX}%;"] />
<#assign overlayStyle=overlayStyle + ["margin-right: -${positionX}%;"] />
<#assign overlayStyle=overlayStyle + ["top: ${positionY}%;"] />
<#assign overlayStyle=overlayStyle + ["margin-bottom: -${positionY}%;"] />
<#assign overlayStyle=overlayStyle + ["transform: translate(-${positionX}%, -${positionY}%);"] />
<#assign overlayStyle=overlayStyle + ["width: ${width}%;"] />

<#assign textStyle=[] />
<#if teaserOverlayStyle.color?has_content>
  <#assign textStyle=textStyle + ["color: ${teaserOverlayStyle.color};"] />
</#if>
<#if teaserOverlayStyle.backgroundColor?has_content>
  <#assign textStyle=textStyle + ["background-color: ${teaserOverlayStyle.backgroundColor};"] />
</#if>

<#if teaserOverlaySettings.enabled>
  <#assign modifiers=[] />
  <div class="cm-teaser-overlay ${modifiers?join(" ")} ${additionalClass} "<@cm.optionalAttributes {"style": overlayStyle?join(" ")} />>
    <div class="cm-teaser-overlay__text cm-richtext ${overlayClass?join(" ")}"<@cm.optionalAttributes {"style": textStyle?join(" ")} /><@cm.metadata ["properties.teaserText", "properties.localSettings"] />>
      <#if self.teaserText?has_content>
        <@cm.include self=self.teaserText!cm.UNDEFINED />
      </#if>
    </div>
  </div>
</#if>
