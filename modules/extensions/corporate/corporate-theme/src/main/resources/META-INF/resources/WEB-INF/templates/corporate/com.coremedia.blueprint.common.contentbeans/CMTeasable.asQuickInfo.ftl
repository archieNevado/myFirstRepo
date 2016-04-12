<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="classQuickInfo" type="java.lang.String" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->
<#-- @ftlvariable name="quickInfoId" type="java.lang.String" -->
<#-- @ftlvariable name="quickInfoModal" type="java.lang.Boolean" -->
<#-- @ftlvariable name="quickInfoGroup" type="java.lang.String" -->
<#-- @ftlvariable name="overlay" type="java.util.Map" -->

<#-- if overlay configuration is not set explicitly assert false for each key not set -->
<#assign overlay={
"displayTitle": false,
"displayShortText": false
} + overlay!{} />
<#assign quickInfoData={} />
<#if quickInfoModal?has_content && quickInfoModal?is_boolean>
  <#assign quickInfoData=quickInfoData + {"modal": quickInfoModal!false} />
</#if>
<#if quickInfoGroup?has_content>
  <#assign quickInfoData=quickInfoData + {"group": quickInfoGroup!""} />
</#if>

<div id="${quickInfoId!bp.generateId("quickinfo")}" class="cm-quickinfo ${classQuickInfo!""}" <@cm.dataAttribute name="data-cm-quickinfo" data=quickInfoData /><@cm.metadata (metadata![]) + [self.content] />>

<#-- title -->
<#assign showTitle=self.teaserTitle?has_content && overlay.displayTitle />
<#assign showTeaserText=self.teaserText?has_content && overlay.displayShortText />

<#if showTitle || showTeaserText>
  <div class="cm-quickinfo__property cm-quickinfo__property--general">
  <#-- teaserTitle -->
    <#if showTitle>
      <h5 class="cm-quickinfo__title cm-heading5"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle}</h5>
    </#if>
  <#-- teaserText -->
    <#if showTeaserText>
      <div class="cm-quickinfo__text"<@cm.metadata "properties.teaserText" />><@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", 185) /></div>
    </#if>
  </div>
</#if>

<#--<a class="cm-teasable__button cm-button cm-button--white btn btn-default" href=cm.getLink(self)>-->
  <a class="cm-button cm-button--white btn btn-default" href="${cm.getLink(self)}">
  ${bp.getMessage("button_read_more")}
  </a>
</div>
