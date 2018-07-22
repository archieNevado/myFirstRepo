<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#import "../../freemarkerLibs/defaultTeaser.ftl" as defaultTeaser />

<#assign teaserBlockClass=cm.localParameters().teaserBlockClass!cm.UNDEFINED />

<@defaultTeaser.renderMedia media=self.firstMedia!cm.UNDEFINED
                            teaserBlockClass=teaserBlockClass
                            link=(cm.localParameters().renderLink!true)?then(cm.getLink(self.target!cm.UNDEFINED), "")
                            openInNewTab=self.openInNewTab
                            limitAspectRatios=cm.localParameters().limitAspectRatios!cm.UNDEFINED
                            metadata=["properties.pictures"]
                            renderDimmer=cm.localParameters().renderDimmer!cm.UNDEFINED
                            renderEmptyMedia=(self.teaserOverlaySettings.enabled || cm.localParameters().renderEmptyImage!true) />

<@cm.include self=self.target!cm.UNDEFINED view="_playButton"
                                           params=cm.isUndefined(teaserBlockClass)?then({}, {"blockClass": teaserBlockClass}) + {"openAsPopup": true} />
