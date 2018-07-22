<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#import "../../freemarkerLibs/heroTeaser.ftl" as heroTeaser />

<#assign heroBlockClass=cm.localParameters().heroBlockClass!cm.UNDEFINED />

<@heroTeaser.renderMedia media=self.firstMedia!cm.UNDEFINED
                         heroBlockClass=heroBlockClass
                         link=cm.getLink(self.target!cm.UNDEFINED)
                         openInNewTab=self.openInNewTab
                         limitAspectRatios=bp.setting(self, "default_aspect_ratios_for_hero_teaser")
                         metadata=["properties.pictures"]
                         renderDimmer=cm.localParameters().renderDimmer!cm.UNDEFINED
                         renderEmptyMedia=(self.teaserOverlaySettings.enabled || cm.localParameters().renderEmptyImage!true) />

<@cm.include self=self.target!cm.UNDEFINED view="_playButton"
                                           params=cm.isUndefined(heroBlockClass)?then({}, {"blockClass": heroBlockClass}) + {"openAsPopup": true} />
