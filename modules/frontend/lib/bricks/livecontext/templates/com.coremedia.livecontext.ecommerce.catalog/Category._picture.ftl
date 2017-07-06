<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.catalog.Category" -->
<#assign additionalClass=cm.localParameters().additionalClass!"cm-teasable" />

<div class="${additionalClass}__picture-box">
    <img class="${additionalClass}__picture" <@cm.dataAttribute name="data-cm-non-adaptive-content" data={"overflow": true} /> src="${(self.thumbnailUrl)!""}" alt="${(self.name)!""}" />
</div>