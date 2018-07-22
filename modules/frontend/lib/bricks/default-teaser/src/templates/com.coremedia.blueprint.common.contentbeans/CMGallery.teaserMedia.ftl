<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMGallery" -->

<#import "../../freemarkerLibs/defaultTeaser.ftl" as defaultTeaser />

<!-- special picture calculation for CMGallery -->
<#assign media=cm.UNDEFINED />
<#assign metadata=[] />
<#if self.firstMedia?has_content>
  <#assign media=self.firstMedia />
  <#assign metadata=["properties.pictures"]/>
<#elseif self.items?has_content>
  <#assign media=self.items[0] />
  <#assign metadata=["properties.items"]/>
</#if>

<@defaultTeaser.renderMedia media=media
                            teaserBlockClass=cm.localParameters().teaserBlockClass!cm.UNDEFINED
                            link=(cm.localParameters().renderLink!true)?then(cm.getLink(self.target!cm.UNDEFINED), "")
                            openInNewTab=self.openInNewTab
                            limitAspectRatios=cm.localParameters().limitAspectRatios!cm.UNDEFINED
                            metadata=metadata
                            renderDimmer=cm.localParameters().renderDimmer!cm.UNDEFINED
                            renderEmptyMedia=(self.teaserOverlaySettings.enabled || cm.localParameters().renderEmptyImage!true) />
