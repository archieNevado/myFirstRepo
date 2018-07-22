<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMAudio" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->
<#-- @ftlvariable name="classMedia" type="java.lang.String" -->
<#-- @ftlvariable name="hideControls" type="java.lang.Boolean" -->
<#-- @ftlvariable name="autoplay" type="java.lang.Boolean" -->
<#-- @ftlvariable name="loop" type="java.lang.Boolean" -->
<#-- @ftlvariable name="muted" type="java.lang.Boolean" -->
<#-- @ftlvariable name="preload" type="java.lang.Boolean" -->
<#-- @ftlvariable name="attr" type="java.util.Map" -->
<#-- @ftlvariable name="_renderBox" type="java.lang.Boolean" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />
<#import "../../freemarkerLibs/media.ftl" as media />

<#if self.data?has_content || self.dataUrl?has_content>
  <#-- for compatibility, private parameter _renderBox will be removed with the next AEP -->
  <@utils.optionalTag condition=cm.localParameters()._renderBox!true attr={
    "class": cm.localParameters().classBox!"",
    "metadata": cm.localParameters().metadata![] + [self.content]
  }>
    <@media.renderAudio link=media.getLink(self)
                        additionalClass=cm.localParameters().classMedia!cm.UNDEFINED
                        playerSettings={
                          "hideControls": cm.localParameters().hideControls!self.playerSettings.hideControls,
                          "autoplay": cm.localParameters().autoplay!self.playerSettings.autoplay,
                          "muted": cm.localParameters().muted!self.playerSettings.muted,
                          "loop": cm.localParameters().loop!self.playerSettings.loop
                        }
                        preload=cm.localParameters().preload!cm.UNDEFINED
                        metadata=(cm.localParameters().metadataMedia![]) + [self.content, "properties.data"]
                        attr=cm.localParameters().attr!cm.UNDEFINED/>
  </@utils.optionalTag>
<#else>
  <div class="cm-audio--missing"<@preview.metadata self.content/>>
    ${bp.getMessage("error_audio_not_available")}
  </div>
</#if>
