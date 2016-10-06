<#-- @ftlvariable name="self" type="com.coremedia.blueprint.ecommerce.contentbeans.CMProduct" -->
<#assign additionalClass=cm.localParameters().additionalClass!"cm-details" />
<#assign relatedView=cm.localParameters().relatedView!"related" />
<#assign carouselParams=cm.localParameters().carouselParams!{} />

<#assign renderDate=cm.localParameter("renderDate", true) />
<#assign renderTags=cm.localParameter("renderTags", true) />
<#assign renderRelated=cm.localParameter("renderRelated", true) />
<#assign carouselParams=carouselParams + {"additionalClass": "${additionalClass}__medias", "viewItems": "_header", "modifier": "details"}/>

<article class="${additionalClass} ${additionalClass}--product"<@cm.metadata self.content />>

  <#-- title -->
  <h1 class="${additionalClass}__headline"<@cm.metadata "properties.productName"/>>${self.productName!""}</h1>

  <#-- media -->
  <@cm.include self=bp.getContainer(self.media) view="asCarousel" params={"modifier": "details", "additionalClass": "${additionalClass}__medias", "controlIcon": "triangle", "viewItems": "_header", "displayPagination": true}/>

<#-- product code -->
    <div class="${additionalClass}__code"<@cm.metadata "properties.productCode"/>>
        <span>${bp.getMessage("product_code")}</span> ${self.productCode!""}
    </div>

  <#-- text -->
  <#if self.detailText?has_content>
    <div class="${additionalClass}__text cm-richtext"<@cm.metadata "properties.detailText"/>>
      <@cm.include self=self.detailText!cm.UNDEFINED />
    </div>
  </#if>

  <#-- downloads -->
  <#if self.downloads?has_content>
    <div class="${additionalClass}__downloads cm-downloads"<@cm.metadata "properties.downloads"/>>
      <h2>${bp.getMessage("download_label")}</h2>
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
