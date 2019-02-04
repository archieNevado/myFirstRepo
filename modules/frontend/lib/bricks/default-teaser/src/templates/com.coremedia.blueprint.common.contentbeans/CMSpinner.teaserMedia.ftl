<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMSpinner" -->
<#-- @ftlvariable name="renderType" type="java.lang.String" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />
<#import "../../freemarkerLibs/defaultTeaser.ftl" as defaultTeaser />

<#-- spinner (with at least 2 images) -->
<#if (self.sequence![])?size gte 2 && (renderType!"") != "plain">
  <@cm.include self=self view="_spinner"/>
<#else>
  <#assign link=defaultTeaser.getLink(self.target!cm.UNDEFINED, self.teaserSettings) />
  <@defaultTeaser.renderMedia media=self.firstMedia!cm.UNDEFINED
                              teaserBlockClass=cm.localParameter("teaserBlockClass")
                              link=link
                              openInNewTab=self.openInNewTab
                              limitAspectRatios=cm.localParameter("limitAspectRatios")
                              metadata=["properties.pictures"]
                              renderDimmer=cm.localParameter("renderDimmer")
                              renderEmptyMedia=(self.teaserOverlaySettings.enabled || cm.localParameter("renderEmptyImage", true)) />
  <@utils.optionalLink href=link
                       openInNewTab=self.openInNewTab>
    <div class="cm-spinner__icon"></div>
  </@utils.optionalLink>
</#if>
