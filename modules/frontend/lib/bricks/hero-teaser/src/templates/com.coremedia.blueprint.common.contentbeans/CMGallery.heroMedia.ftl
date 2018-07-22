<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMGallery" -->

<#import "../../freemarkerLibs/heroTeaser.ftl" as heroTeaser />

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

<@heroTeaser.renderMedia media=media
                         heroBlockClass=cm.localParameters().heroBlockClass!cm.UNDEFINED
                         link=cm.getLink(self.target!cm.UNDEFINED)
                         openInNewTab=self.openInNewTab
                         limitAspectRatios=bp.setting(self, "default_aspect_ratios_for_hero_teaser")
                         metadata=metadata
                         renderDimmer=cm.localParameters().renderDimmer!cm.UNDEFINED
                         renderEmptyMedia=cm.localParameters().renderEmptyImage!cm.UNDEFINED />
