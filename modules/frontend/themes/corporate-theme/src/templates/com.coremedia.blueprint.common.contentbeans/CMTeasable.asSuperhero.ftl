<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->

<#assign index=cm.localParameters().index!0 />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />

<div class="cm-superhero ${additionalClass!""}"<@cm.metadata self.content /> data-cm-module="superhero">
  <#-- picture -->
  <@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix="cm-superhero" background=true/>

  <#if (self.teaserTitle?has_content || self.teaserText?has_content)>
    <#-- with banderole -->
    <div class="cm-superhero__banderole row">
      <div class="col-xs-10 col-xs-push-1 col-md-8 col-md-push-2">
        <#-- headline -->
        <@bp.optionalLink href="${link}">
          <h1 class="cm-superhero__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
        </@bp.optionalLink>
        <#-- teaser text -->
        <p class="cm-superhero__text"<@cm.metadata "properties.teaserText" />>
          <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "superhero.max.length", 140)) />
        </p>
        <#-- custom call-to-action button -->
        <@cm.include self=self view="_callToAction" params={"additionalClass": "cm-superhero__button cm-button--white "}/>
      </div>
    </div>
  <#-- button without banderole -->
  <#elseif link?has_content>
    <#-- custom call-to-action button -->
    <div class="cm-superhero__cta">
      <@cm.include self=self view="_callToAction" params={"additionalClass": "cm-button--white "}/>
    </div>
  </#if>
</div>
