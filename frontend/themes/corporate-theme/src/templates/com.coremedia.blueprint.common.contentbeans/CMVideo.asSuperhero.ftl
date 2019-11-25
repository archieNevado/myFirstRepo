<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />

<#assign index=cm.localParameters().index!0 />
<#assign additionalClasses=cm.localParameters().additionalClass!"" />

<div class="cm-superhero cm-superhero--video ${additionalClasses!""}" data-cm-module="superhero" <@preview.metadata self.content />>

  <#-- video -->
  <@cm.include self=self view="media" params={
    "classMedia": "cm-superhero__video",
    "autoplay": true,
    "loop": true,
    "hideControls": true,
    "muted": true,
    "preload": true
  } />

  <#-- picture -->
  <@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix="cm-superhero" background=true/>

  <#-- caption -->
  <#if (self.teaserTitle?has_content || self.teaserText?has_content)>
    <div class="cm-superhero__caption row">
      <div class="col-xs-10 col-xs-push-1 col-md-8 col-md-push-2">
        <#-- headline -->
        <h1 class="cm-superhero__headline"<@preview.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
        <#-- teaser text -->
        <p class="cm-superhero__text"<@preview.metadata "properties.teaserText" />>
          <@utils.renderWithLineBreaks text=bp.truncateText(self.teaserText!"", bp.setting(self, "superhero.max.length", 140)) />
        </p>
      </div>
    </div>
  </#if>
</div>
