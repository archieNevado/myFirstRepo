<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.p13n.MarketingImage" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<div class="cm-teasable cm-teasable--image"<@preview.metadata metadata![] />>
  <div class="cm-teasable_picture-box">
    <!-- todo: should be moved to livecontext -->
    <img class="cm-uncropped-catalog-picture" src="${(self.thumbnailUrl)!""}" alt="${(self.shortText)!""}">
  </div>
</div>
