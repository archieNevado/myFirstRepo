<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign enableTeaserOverlay=cm.localParameters().enableTeaserOverlay!true />

<#assign cssClasses=self.teaserText?has_content?then(" is-text", "") + (cm.localParameters().islast!false)?then(" is-last", "") />

<div class="${blockClass} ${cssClasses} ${additionalClass}"<@preview.metadata self.content />>
  <div class="${blockClass}__wrapper">
    <@cm.include self=self view="teaserMedia" params={
      "teaserBlockClass": blockClass
    } + cm.localParameters() />
    <#if (enableTeaserOverlay && self.teaserOverlaySettings.enabled)>
      <@cm.include self=self view="teaserOverlay" />
    <#else>
      <@cm.include self=self view="teaserCaption" params={"teaserBlockClass": blockClass} + cm.localParameters() />
    </#if>
  </div>

  <#-- extensions -->
  <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_TEASER />
</div>
