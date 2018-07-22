<#-- @ftlvariable name="self" type="com.coremedia.blueprint.ecommerce.contentbeans.CMProduct" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />

<#assign blockClass=cm.localParameters().blockClass!"cm-details" />
<#assign relatedView=cm.localParameters().relatedView!"related" />
<#assign carouselParams=cm.localParameters().carouselParams!{} />

<#assign renderDate=cm.localParameter("renderDate", true) />
<#assign renderTags=cm.localParameter("renderTags", true) />
<#assign renderRelated=cm.localParameter("renderRelated", true) />
<#assign carouselParams=carouselParams + {"additionalClass": "${blockClass}__medias", "viewItems": "_header", "modifier": "details"}/>

<article class="${blockClass} ${blockClass}--product"<@preview.metadata self.content />>

  <#-- title -->
  <h1 class="${blockClass}__headline"<@preview.metadata "properties.productName"/>>${self.productName!""}</h1>

  <#-- media -->
  <@cm.include self=bp.getContainer(self.media) view="asCarousel" params={"modifier": "details", "additionalClass": "${blockClass}__medias", "controlIcon": "triangle", "viewItems": "_header", "displayPagination": true, "metadataItemsName":"pictures"}/>

  <#-- product code -->
  <div class="${blockClass}__code"<@preview.metadata "properties.productCode"/>>
      <span>${bp.getMessage("product_code")}</span> ${self.productCode!""}
  </div>

  <#-- text -->
  <#if self.detailText?has_content>
    <div class="${blockClass}__text cm-richtext"<@preview.metadata "properties.detailText"/>>
      <@cm.include self=self.detailText!cm.UNDEFINED />
    </div>
  </#if>

  <#-- downloads -->
  <#if self.downloads?has_content>
    <div class="${blockClass}__downloads cm-downloads"<@preview.metadata "properties.downloads"/>>
      <h3>${bp.getMessage("download_label")}</h3>
      <ul class="cm-downloads__items">
        <#list self.downloads![] as download>
          <li class="cm-downloads__item">
            <@cm.include self=download view="asLink" params={
              "cssClass": "cm-glyphicon-before"
            }/>
          </li>
        </#list>
      </ul>
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

<#-- extensions -->
<@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_END />

<#-- related -->
<#if renderRelated>
  <@cm.include self=self view="_related" params={"relatedView": relatedView}/>
</#if>
