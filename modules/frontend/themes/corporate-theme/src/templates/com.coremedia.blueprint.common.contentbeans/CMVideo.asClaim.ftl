<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />

<#assign cssClasses = cm.localParameter("cssClass", "") + cm.localParameter("islast", false)?then(" is-last", "") />

<div class="cm-claim cm-claim--video thumbnail ${cssClasses}"<@preview.metadata self.content />>
  <@utils.optionalLink href="${cm.getLink(self.target)}" attr={ "class": "cm-claim__link" }>
    <#-- picture -->
    <#if self.picture?has_content>
      <@cm.include self=self.picture view="media" params={
        "limitAspectRatios": [ "portrait_ratio1x1" ],
        "classBox": "cm-claim__picture-box",
        "classMedia": "cm-claim__picture",
        "metadata": ["properties.pictures"]
      }/>
    <#else>
      <div class="cm-claim__picture-box">
        <div class="cm-claim__picture cm-image--blank"></div>
      </div>
    </#if>

    <#-- play overlay icon-->
    <@cm.include self=self view="_playButton" params={"blockClass": "cm-claim", "openAsPopup": true}/>
  </@utils.optionalLink>

  <div class="caption">
    <#-- headline -->
    <h3 class="cm-claim__headline thumbnail-label"<@preview.metadata "properties.teaserTitle" />>
      <span>
        ${self.teaserTitle!""}
      </span>
    </h3>
    <#-- teaser text, 3 lines ~ 120 chars -->
    <p class="cm-claim__text"<@preview.metadata "properties.teaserText" />>
      <@utils.renderWithLineBreaks text=bp.truncateText(self.teaserText!"", bp.setting(self, "claim.max.length", 115)) />
    </p>
  </div>
</div>
