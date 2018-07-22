<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->

<#import "../../freemarkerLibs/media.ftl" as media />

<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
<#assign openAsPopup=cm.localParameters().openAsPopup!false />

<#assign videoLink=media.getLink(self) />

<#if videoLink?has_content>
  <#if openAsPopup>
    <a href="${cm.getLink(self)}" class="${blockClass}__play cm-play-button" <@cm.dataAttribute name="data-cm-video-popup" data={ "url": videoLink, "parentSelector": ".${blockClass}" } />></a>
  <#else>
    <div class="${blockClass}__play cm-play-button"></div>
  </#if>
</#if>
