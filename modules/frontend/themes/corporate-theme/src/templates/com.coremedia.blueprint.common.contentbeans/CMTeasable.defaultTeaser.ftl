<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/cta.ftl" as cta />
<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />

<#assign index=cm.localParameters().index!0 />
<#assign hasImage=self.picture?has_content />
<#assign hasEvenIndex=(index % 2 == 0) />
<#assign additionalVariantCssClass="" />
<#assign additionalButtonCssClass="cm-button " />
<#assign additionalNoImageCssClass="" />
<#assign additionalImgCssClass=""/>
<#assign additionalTextCssClass=""/>
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign target=(self.target?has_content && self.target.openInNewTab)?then("_blank", "") />
<#assign rel=(self.target?has_content && self.target.openInNewTab)?then("noopener", "") />

<#if !hasEvenIndex>
  <#assign additionalVariantCssClass="cm-teasable--alternative" />
<#else>
  <#assign additionalButtonCssClass=additionalButtonCssClass + "cm-button--white " />
</#if>
<#if !hasImage>
  <#assign additionalNoImageCssClass="cm-teasable--no-image" />
</#if>
<#if !hasEvenIndex && hasImage>
  <#assign additionalImgCssClass="col-sm-6"/>
  <#assign additionalTextCssClass="col-sm-6"/>
</#if>
<#if hasEvenIndex && hasImage>
  <#assign additionalImgCssClass="col-sm-6 col-sm-push-6"/>
  <#assign additionalTextCssClass="col-sm-6 col-sm-pull-6"/>
</#if>

<div class="cm-teasable ${additionalVariantCssClass} ${additionalNoImageCssClass} row ${additionalClass!""}"<@preview.metadata self.content />>
  <#if hasImage>
    <div class="col-xs-12 ${additionalImgCssClass}">
      <@utils.optionalLink href=link attr={"target":target,"rel":rel}>
      <#-- picture -->
        <@cm.include self=self.picture view="media" params={
          "limitAspectRatios": [ "portrait_ratio1x1", "landscape_ratio16x9" ],
          "classBox": "cm-teasable__picture-box",
          "classMedia": "cm-teasable__picture",
          "metadata": ["properties.pictures"]
        }/>
      </@utils.optionalLink>
    </div>
  </#if>
  <div class="col-xs-12 ${additionalTextCssClass}">
    <div class="cm-teasable__text-content-box">
      <div class="cm-teasable__text-content">
      <#-- headline -->
        <@utils.optionalLink href="${link}" attr={"target":target,"rel":rel}>
          <h3 class="cm-teasable__headline"<@preview.metadata "properties.teaserTitle" />>
            <span>${self.teaserTitle!""}</span>
          </h3>
        </@utils.optionalLink>
      <#-- teaser text -->
        <#assign truncatedTeaserText=bp.truncateText(self.teaserText!"", bp.setting(self, "teaser.max.length", 140)) />
        <#if truncatedTeaserText?has_content>
          <p class="cm-teasable__text"<@preview.metadata "properties.teaserText" />>
            <@utils.renderWithLineBreaks text=truncatedTeaserText />
          </p>
        </#if>
        <#-- cta -->
        <@cta.render buttons=self.callToActionSettings
                     additionalClass="cm-teasable__cta"
                     additionalButtonClass=additionalButtonCssClass />
      </div>
    </div>
  </div>
</div>
