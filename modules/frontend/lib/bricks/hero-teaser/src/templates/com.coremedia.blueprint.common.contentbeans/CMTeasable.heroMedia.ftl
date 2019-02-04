<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#import "../../freemarkerLibs/heroTeaser.ftl" as heroTeaser />

<#assign heroBlockClass=cm.localParameters().heroBlockClass!cm.UNDEFINED />

<@heroTeaser.renderMedia media=self.firstMedia!cm.UNDEFINED
                         heroBlockClass=heroBlockClass
                         link=heroTeaser.getLink(self.target!cm.UNDEFINED, self.teaserSettings)
                         openInNewTab=self.openInNewTab
                         limitAspectRatios=bp.setting(self, "default_aspect_ratios_for_hero_teaser")
                         metadata=["properties.pictures"]
                         renderDimmer=cm.localParameters().renderDimmer!cm.UNDEFINED
                         renderEmptyMedia=(self.teaserOverlaySettings.enabled || cm.localParameters().renderEmptyImage!true) />

<#-- do not render a video popup if no link to detail page should be rendered (popup is a replacement for the detail page) -->
<#if self.teaserSettings.renderLinkToDetailPage>
  <@cm.include self=self.target!cm.UNDEFINED view="_playButton"
                                             params=cm.isUndefined(heroBlockClass)?then({}, {"blockClass": heroBlockClass}) + {"openAsPopup": true} />
</#if>
