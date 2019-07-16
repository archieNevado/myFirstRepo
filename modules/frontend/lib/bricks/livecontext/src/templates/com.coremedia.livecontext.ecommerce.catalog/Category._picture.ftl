<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.catalog.Category" -->
<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />

<div class="${blockClass}__picture-box">
  <img class="${blockClass}__picture cm-uncropped-catalog-picture" src="${(self.thumbnailUrl)!""}" alt="${(self.name)!""}">
</div>
