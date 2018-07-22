<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />
<#import "../../freemarkerLibs/defaultTeaser.ftl" as defaultTeaser />

<#assign renderTeaserText=cm.localParameters().renderTeaserText!true />
<#assign textHtml>
  <#if renderTeaserText && self.teaserText?has_content>
    <@utils.renderWithLineBreaks text=bp.truncateText(self.teaserText!"", bp.setting(self, "teaser.max.length", 200)) />
  </#if>
</#assign>

<@defaultTeaser.renderCaption title=(cm.localParameters().renderTeaserTitle!true)?then(self.teaserTitle!"", "")
                              text=textHtml?no_esc
                              link=(cm.localParameters().renderLink!true)?then(cm.getLink(self.target!cm.UNDEFINED), "")
                              openInNewTab=self.openInNewTab
                              ctaButtons=self.callToActionSettings
                              teaserBlockClass=cm.localParameters().teaserBlockClass!cm.UNDEFINED
                              metadataTitle=["properties.teaserTitle"]
                              metadataText=["properties.teaserText"] />
