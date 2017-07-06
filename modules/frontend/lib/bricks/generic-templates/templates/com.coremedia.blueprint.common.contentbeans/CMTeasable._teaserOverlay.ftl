<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign renderCTA=cm.localParameter("renderCTA", true) />
<#assign renderCTALink=cm.localParameter("renderCTALink", true) />

<#assign teaserOverlaySettings=self.teaserOverlaySettings />
<#assign teaserOverlayStyle=self.teaserOverlayStyle />

<#if teaserOverlaySettings.enabled>
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

  <#if teaserOverlayStyle.color?has_content>
    <#assign overlayStyle=overlayStyle + ["color: ${teaserOverlayStyle.color};"] />
  </#if>
  <#if teaserOverlayStyle.backgroundColor?has_content>
    <#assign overlayStyle=overlayStyle + ["background-color: ${teaserOverlayStyle.backgroundColor};"] />
  </#if>
  <#if teaserOverlayStyle.additionalStyles?has_content>
    <#assign overlayStyle=overlayStyle + [teaserOverlayStyle.additionalStyles] />
  </#if>

  <div class="cm-teaser-overlay ${additionalClass}"<@cm.optionalAttributes {"style": overlayStyle?join(" ")} />>
    <div class="cm-teaser-overlay__text cm-richtext ${overlayClass?join(" ")}"<@cm.metadata ["properties.teaserText", "properties.localSettings"] />>
      <#-- get the teaser text without falling back to the detail text (see CMS-9680) -->
      <#assign rawTeaserText=self.content.getMarkup("teaserText")!"" />
      <#if rawTeaserText?has_content>
        <@cm.include self=self.teaserText!cm.UNDEFINED />
      </#if>
    </div>
    <#if renderCTA>
      <div class="cm-teaser-overlay__cta"<@cm.metadata ["properties.teaserText", "properties.localSettings"] />>
        <@cm.include self=self view="_callToAction" params={
          "renderLink": renderCTALink,
          "additionalClass": teaserOverlayStyle.ctaCls!""
        } />
      </div>
    </#if>
  </div>
</#if>
