<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMImageMap" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />
<#import "../../freemarkerLibs/image-map.ftl" as imageMap />

<#assign blockClass=cm.localParameters().blockClass!"cm-details" />
<#assign relatedView=cm.localParameters().relatedView!"related" />
<#assign renderDate=cm.localParameter("renderDate", true) />
<#assign renderTags=cm.localParameter("renderTags", false) />
<#assign renderRelated=cm.localParameter("renderRelated", false) />
<#assign useQuickinfo=cm.localParameter("imagemap_use_quickinfo", true)/>

<#assign imageMapParams=imageMap.generateIds(self)/>
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />

<article class="${blockClass} ${blockClass}--imagemap"<@preview.metadata self.content />>

  <#-- title -->
  <h1 class="${blockClass}__headline"<@preview.metadata "properties.teaserTitle"/>>${self.teaserTitle!""}</h1>

  <div class="cm-imagemap" data-cm-imagemap='{"coordsBaseWidth": "${bp.IMAGE_TRANSFORMATION_BASE_WIDTH}", "defaultLink": "${link}"}'>

    <#-- picture + hot zones -->
    <@cm.include self=self view="_picture" params={
      "blockClass": blockClass,
      "renderDimmer": false,
      "useQuickinfo": useQuickinfo
    } + imageMapParams
    />

    <#--include imagemap quickinfos -->
    <#if useQuickinfo>
      <@cm.include self=self view="_areasQuickInfo" params=imageMapParams/>
    </#if>

  </div>

  <#-- text -->
  <#if self.teaserText?has_content>
    <div class="${blockClass}__text cm-richtext"<@preview.metadata "properties.teaserText"/>>
      <@cm.include self=self.teaserText!cm.UNDEFINED />
    </div>
  </#if>

  <#-- date -->
  <#if renderDate && self.externallyDisplayedDate?has_content>
    <div class="${blockClass}__date"<@preview.metadata "properties.externallyDisplayedDate"/>>
      <@utils.renderDate date=self.externallyDisplayedDate.time
                         cssClass="${blockClass}__time"
                         metadata=["properties.externallyDisplayedDate"] />
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

