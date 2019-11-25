<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/cta.ftl" as cta />
<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />

<#assign index=cm.localParameters().index!0 />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign target=(self.target?has_content && self.target.openInNewTab)?then("_blank", "") />
<#assign rel=(self.target?has_content && self.target.openInNewTab)?then("noopener", "") />

<div class="cm-superhero ${additionalClass!""}"<@preview.metadata self.content /> data-cm-module="superhero">
  <#-- picture -->
  <@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix="cm-superhero" background=true/>

  <#if (self.teaserTitle?has_content || self.teaserText?has_content)>
    <#-- with caption -->
    <div class="cm-superhero__caption row">
      <div class="col-xs-10 col-xs-push-1 col-md-8 col-md-push-2">
        <#-- headline -->
        <@utils.optionalLink href="${link}" attr={"target":target,"rel":rel}>
          <h1 class="cm-superhero__headline"<@preview.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
        </@utils.optionalLink>
        <#-- teaser text -->
        <p class="cm-superhero__text"<@preview.metadata "properties.teaserText" />>
          <@utils.renderWithLineBreaks text=bp.truncateText(self.teaserText!"", bp.setting(self, "superhero.max.length", 140)) />
        </p>
        <#-- cta -->
        <@cta.render buttons=self.callToActionSettings
                     additionalButtonClass="cm-superhero__cta" />
      </div>
    </div>
  <#-- button without caption -->
  <#elseif link?has_content>
    <#-- cta -->
    <@cta.render buttons=self.callToActionSettings
                 additionalButtonClass="cm-superhero__cta" />
  </#if>
</div>
