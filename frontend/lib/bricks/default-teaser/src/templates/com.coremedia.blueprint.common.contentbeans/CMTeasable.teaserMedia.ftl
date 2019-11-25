<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#import "../../freemarkerLibs/defaultTeaser.ftl" as defaultTeaser />

<#assign teaserBlockClass=cm.localParameters().teaserBlockClass!cm.UNDEFINED />

<@defaultTeaser.renderMedia media=self.firstMedia!cm.UNDEFINED
                            teaserBlockClass=teaserBlockClass
                            link=defaultTeaser.getLink(self.target!cm.UNDEFINED, self.teaserSettings)
                            openInNewTab=self.openInNewTab
                            limitAspectRatios=cm.localParameters().limitAspectRatios!cm.UNDEFINED
                            metadata=["properties.pictures"]
                            renderDimmer=cm.localParameters().renderDimmer!cm.UNDEFINED
                            renderEmptyMedia=(self.teaserOverlaySettings.enabled || cm.localParameters().renderEmptyImage!true) />

<#-- do not render a video popup if no link to detail page should be rendered (popup is a replacement for the detail page) -->
<#if self.teaserSettings.renderLinkToDetailPage>
  <@cm.include self=self.target!cm.UNDEFINED view="_playButton"
                                             params=cm.isUndefined(teaserBlockClass)?then({}, {"blockClass": teaserBlockClass}) + {"openAsPopup": true} />
</#if>
