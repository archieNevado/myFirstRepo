<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->

<#assign index=cm.localParameters().index!0 />
<#assign additionalClass=cm.localParameters().additionalClass!"" />

<div class="cm-hero ${additionalClass!""}"<@cm.metadata self.content />>
<#-- picture -->
<#if self.picture?has_content>
  <@cm.include self=self.picture params={
  "limitAspectRatios": ["landscape_ratio4x3","landscape_ratio16x9"],
  "classBox": "cm-hero__picture-box",
  "classImage": "cm-hero__picture",
  "metadata": ["properties.pictures"]
  }/>
  <div class="cm-hero__dimmer"></div>
<#else>
  <div class="cm-hero__picture-box" <@cm.metadata "properties.pictures" />>
    <div class="cm-hero__picture"></div>
  </div>
</#if>

<#if self.teaserTitle?has_content>
<#-- with banderole -->
  <div class="cm-hero__banderole row">
    <div class="col-xs-10 col-xs-push-1">
    <#-- headline -->
        <h1 class="cm-hero__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
    </div>
  </div>
</#if>
</div>
