<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign renderLink=cm.localParameters().renderLink!true />
<#assign renderTeaserTitle=cm.localParameters().renderTeaserTitle!true />
<#assign renderTeaserText=cm.localParameters().renderTeaserText!true />
<#assign renderDimmer=cm.localParameters().renderDimmer!true />
<#assign renderEmptyImage=cm.localParameters().renderEmptyImage!true />
<#assign limitAspectRatiosKey="default_aspect_ratios_for_" + cm.localParameters().limitAspectRatiosKey!"teaser" />
<#assign limitAspectRatios=cm.localParameters().limitAspectRatios!bp.setting(self, limitAspectRatiosKey, []) />

<#assign cssClasses=self.teaserText?has_content?then(" is-text", "") + (cm.localParameters().islast!false)?then(" is-last", "") />
<#assign link=renderLink?then(cm.getLink(self.target!cm.UNDEFINED), "") />
<#assign target=(self.target?has_content && self.target.openInNewTab)?then("_blank", "_self") />
<#assign rel=(self.target?has_content && self.target.openInNewTab)?then("noopener", "") />

<div class="${blockClass} ${cssClasses} ${additionalClass}"<@preview.metadata self.content />>
  <div class="${blockClass}__wrapper">
    <@cm.include self=self view="teaserMedia" params={
      "teaserBlockClass": blockClass,
      "limitAspectRatios": limitAspectRatios,
      "renderLink": renderLink,
      "renderDimmer": renderDimmer,
      "renderEmptyImage": renderEmptyImage
    } />
    <#if !self.teaserOverlaySettings.enabled>
      <@cm.include self=self view="teaserCaption" params={
        "teaserBlockClass": blockClass,
        "renderLink": renderLink,
        "renderTeaserTitle": renderTeaserTitle,
        "renderTeaserText": renderTeaserText
      } />
    <#else>
      <@cm.include self=self view="teaserOverlay"/>
    </#if>
  </div>

  <#-- extensions -->
  <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_TEASER />
</div>
