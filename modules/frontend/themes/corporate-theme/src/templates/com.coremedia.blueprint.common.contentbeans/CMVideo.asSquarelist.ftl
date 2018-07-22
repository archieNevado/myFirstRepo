<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />

<#assign hasPicture=self.picture?has_content />
<#assign additionalClasses=hasPicture?then("cm-squarelist--dimmer", '') />

<div class="cm-squarelist ${additionalClasses}"<@preview.metadata self.content />>
  <@utils.optionalLink href="${cm.getLink(self.target)}">
    <#-- picture -->
    <#if hasPicture>
      <@cm.include self=self.picture view="media" params={
          "limitAspectRatios": [ "portrait_ratio1x1" ],
          "classBox": "cm-squarelist__picture-box",
          "classMedia": "cm-squarelist__picture",
          "metadata": ["properties.pictures"]
      }/>
    <#else>
      <div class="cm-squarelist__picture-box"<@preview.metadata "properties.pictures" />>
        <div class="cm-squarelist__picture cm-image--blank"></div>
      </div>
    </#if>

    <#-- play overlay icon-->
    <@cm.include self=self view="_playButton" params={"blockClass": "cm-squarelist", "openAsPopup": true}/>

    <#-- headline -->
    <#if self.teaserTitle?has_content>
      <h3 class="cm-squarelist__headline"<@preview.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h3>
    </#if>

    <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_TEASER />
  </@utils.optionalLink>
</div>
