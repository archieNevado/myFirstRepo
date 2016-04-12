<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->

<#assign index=cm.localParameters().index!0 />

<div class="cm-superhero ${additionalClass!""}"<@cm.metadata self.content /> data-cm-module="superhero">
<#-- picture -->
<#if self.picture?has_content>
  <@cm.include self=self.picture view="asBackgroundImage"
  params={
  "classBox": "cm-superhero__image",
  "classImage": "cm-image--superhero",
  "metadata": ["properties.pictures"]
  }/>
  <div class="cm-superhero__dimmer"></div>
<#else>
  <div class="cm-superhero__image"></div>
</#if>

<#if (self.teaserTitle?has_content || self.teaserText?has_content)>
<#-- with banderole -->
  <div class="cm-superhero__banderole row">
    <div class="col-xs-10 col-xs-push-1 col-md-8 col-md-push-2">
    <#-- headline -->
        <h1 class="cm-superhero__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
    </div>
  </div>
</#if>
</div>
