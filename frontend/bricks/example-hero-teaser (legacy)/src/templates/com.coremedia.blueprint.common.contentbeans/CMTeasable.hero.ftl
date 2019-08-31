<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign blockClass=cm.localParameters().blockClass!"cm-hero" />
<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign enableTeaserOverlay=cm.localParameters().enableTeaserOverlay!true /> <#-- de-/activate teaser overlay generally-->

<div class="${blockClass} ${additionalClass}"<@preview.metadata self.content />>

  <@cm.include self=self view="heroMedia" params={
    "heroBlockClass": blockClass
  } + cm.localParameters() />
  <#if !enableTeaserOverlay || enableTeaserOverlay && !self.teaserOverlaySettings.enabled>
    <@cm.include self=self view="heroCaption" params={
      "heroBlockClass": blockClass
    } + cm.localParameters() />
  <#else>
    <@cm.include self=self view="heroOverlay" />
  </#if>
</div>
