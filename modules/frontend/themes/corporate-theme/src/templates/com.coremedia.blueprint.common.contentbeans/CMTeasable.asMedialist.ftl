<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign target=(self.target?has_content && self.target.openInNewTab)?then("_blank", "") />
<#assign rel=(self.target?has_content && self.target.openInNewTab)?then("noopener", "") />

<div class="cm-medialist"<@preview.metadata self.content />>
  <@bp.optionalLink href="${link}" attr={"target":target,"rel":rel}>
    <#-- picture -->
    <#if self.picture?has_content>
      <@cm.include self=self.picture params={
      "limitAspectRatios": [ "portrait_ratio1x1" ],
      "classBox": "cm-medialist__picture-box",
      "classImage": "cm-medialist__picture",
      "metadata": ["properties.pictures"]
      }/>
    <#else>
      <div class="cm-medialist__picture-box">
        <div class="cm-medialist__picture cm-image--missing"></div>
      </div>
    </#if>
    <#-- caption -->
    <div class="cm-medialist__caption">
      <#-- date -->
      <#if self.externallyDisplayedDate?has_content>
       <@bp.renderDate self.externallyDisplayedDate.time "cm-medialist__time" />
      </#if>
      <#-- headline -->
      <#if self.teaserTitle?has_content>
        <h3 class="cm-medialist__headline"<@preview.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h3>
      </#if>
      <#-- teaser text, 3 lines ~ 160 chars -->
      <p class="cm-medialist__text"<@preview.metadata "properties.teaserText" />>
        <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "medialist.max.length", 160)) />
      </p>
    </div>
  </@bp.optionalLink>
</div>
