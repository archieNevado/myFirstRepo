<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#assign additionalClass=cm.localParameters().additionalClass!"cm-details" />
<#assign relatedView=cm.localParameters().relatedView!"related" />
<#assign carouselParams=cm.localParameters().carouselParams!{} />

<#assign renderDate=cm.localParameter("renderDate", true) />
<#assign renderTags=cm.localParameter("renderTags", true) />
<#assign renderRelated=cm.localParameter("renderRelated", true) />
<#assign carouselParams=carouselParams + {"additionalClass": "${additionalClass}__medias", "viewItems": "_header", "modifier": "details"}/>

<article class="${additionalClass}"<@cm.metadata self.content />>

  <#-- title -->
  <h1 class="${additionalClass}__headline"<@cm.metadata "properties.title"/>>${self.title!""}</h1>


  <#-- media -->
  <@cm.include self=bp.getContainer(self.media) view="asCarousel" params=carouselParams/>

  <#-- text -->
  <#if self.detailText?has_content>
    <div class="${additionalClass}__text cm-richtext"<@cm.metadata "properties.detailText"/>>
      <@cm.include self=self.detailText!cm.UNDEFINED />
    </div>
  </#if>

  <#-- date -->
  <#if renderDate && self.externallyDisplayedDate?has_content>
    <div class="${additionalClass}__date"<@cm.metadata "properties.externallyDisplayedDate"/>>
      <@bp.renderDate self.externallyDisplayedDate.time "${additionalClass}__time" />
    </div>
  </#if>

  <#-- tags -->
  <#if renderTags>
    <@cm.include self=self view="_tagList"/>
  </#if>
</article>

<#-- related -->
<#if renderRelated>
  <@cm.include self=self view="_related" params={"relatedView": relatedView}/>
</#if>

<#-- extensions -->
<@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_END />
