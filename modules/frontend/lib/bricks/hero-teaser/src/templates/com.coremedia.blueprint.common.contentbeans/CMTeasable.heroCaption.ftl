<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />
<#import "../../freemarkerLibs/heroTeaser.ftl" as heroTeaser />

<#assign renderTeaserText=cm.localParameters().renderTeaserText!true />
<#assign textHtml>
  <#if renderTeaserText && self.teaserText?has_content>
    <@utils.renderWithLineBreaks text=bp.truncateText(self.teaserText!"", bp.setting(self, "hero-max-length", 140)) />
  </#if>
</#assign>

<@heroTeaser.renderCaption title=self.teaserTitle!""
                           text=textHtml?no_esc
                           link=cm.getLink(self.target!cm.UNDEFINED)
                           openInNewTab=self.openInNewTab
                           ctaButtons=self.callToActionSettings
                           heroBlockClass=cm.localParameters().heroBlockClass!cm.UNDEFINED
                           metadataTitle=["properties.teaserTitle"]
                           metadataText=["properties.teaserText"] />
