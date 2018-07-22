<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#import "../../freemarkerLibs/heroTeaser.ftl" as heroTeaser />

<#assign blockClass=cm.localParameters().blockClass!"cm-hero" />
<#assign renderTeaserText=cm.localParameters().renderTeaserText!true />
<#assign renderDimmer=cm.localParameters().renderDimmer!true />
<#assign renderEmptyImage=cm.localParameters().renderEmptyImage!true />

<div class="${blockClass}"<@preview.metadata self.content />>

  <@cm.include self=self view="heroMedia" params={
    "heroBlockClass": blockClass,
    "renderDimmer": renderDimmer,
    "renderEmptyImage": renderEmptyImage
  } />
  <#if !self.teaserOverlaySettings.enabled>
    <@cm.include self=self view="heroCaption" params={
      "heroBlockClass": blockClass,
      "renderTeaserText": renderTeaserText
    } />
  <#else>
    <@cm.include self=self view="heroOverlay" />
  </#if>
</div>
