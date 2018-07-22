<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/cta.ftl" as cta />
<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/teaserOverlay.ftl" as teaserOverlay />

<#assign additionalClass=cm.localParameters().additionalClass!"" />

<#assign ctaCls=(self.teaserOverlayStyle.ctaCls)!"" />
<#assign afterText>
  <@cta.render buttons=self.callToActionSettings
               additionalClass="cm-teaser-overlay__cta"
               additionalButtonClass="cm-teaser-overlay__cta-button ${ctaCls}" />
</#assign>

<@teaserOverlay.render teaserOverlaySettings=self.teaserOverlaySettings
                       teaserOverlayStyle=self.teaserOverlayStyle
                       text=self.content.getMarkup("teaserText")!""
                       additionalClass=additionalClass
                       afterText=afterText />
