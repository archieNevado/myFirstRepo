<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />

<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign target=(self.target?has_content && self.target.openInNewTab)?then("_blank", "") />
<#assign rel=(self.target?has_content && self.target.openInNewTab)?then("noopener", "") />

<div class="cm-gap" data-cm-module="gap"<@preview.metadata self.content />>
  <#-- picture -->
  <@utils.optionalLink href="${link}" attr={"target":target,"rel":rel}>
    <#if self.picture?has_content>
      <div class="cm-gap__embed">
        <div class="cm-gap__embed-item">
          <@cm.include self=self.picture view="media" params={
            "limitAspectRatios": [],
            "classBox": "cm-gap__picture-box",
            "classMedia": "cm-gap__picture",
            "metadata": ["properties.pictures"]
          }/>
        </div>  
      </div>
    <#else>
      <div class="cm-gap__embed-item">
        <div class="cm-gap__picture-box" <@preview.metadata "properties.pictures" />>
          <#-- just reserve space, gaps should not have an empty picture -->
        </div>
      </div>
    </#if>
    <div class="cm-gap__dimmer"></div>
    <#if (self.teaserTitle?has_content || self.teaserText?has_content)>
      <#-- with caption -->
      <div class="cm-gap__caption row">
        <div class="col-xs-10 col-xs-push-1 col-md-8 col-md-push-2">
          <#-- headline -->
          <h2 class="cm-gap__headline"<@preview.metadata "properties.teaserTitle" />>
            <span>${self.teaserTitle!""} 
            <#if link?has_content><i class="cm-gap__arrow"></i></#if>
            </span>
          </h2>
          <#-- teaser text -->
          <p class="cm-gap__text"<@preview.metadata "properties.teaserText" />>
            <@utils.renderWithLineBreaks text=bp.truncateText(self.teaserText!"", bp.setting(self, "gap.max.length", 140)) />
          </p>
        </div>
      </div>
    </#if>
  </@utils.optionalLink>
</div>
