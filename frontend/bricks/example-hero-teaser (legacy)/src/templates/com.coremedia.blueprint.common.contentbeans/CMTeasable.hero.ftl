<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#import "../../freemarkerLibs/heroTeaser.ftl" as heroTeaser />

<#assign blockClass=cm.localParameters().blockClass!"cm-hero" />

<div class="${blockClass}"<@preview.metadata self.content />>

  <@cm.include self=self view="heroMedia" params={
    "heroBlockClass": blockClass
  } + cm.localParameters() />
  <#if !self.teaserOverlaySettings.enabled>
    <@cm.include self=self view="heroCaption" params={
      "heroBlockClass": blockClass
    } + cm.localParameters() />
  <#else>
    <@cm.include self=self view="heroOverlay" />
  </#if>
</div>
