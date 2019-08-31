<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.p13n.MarketingImage" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<div class="cm-marketing-image"<@preview.metadata metadata![] />>
  <div class="cm-marketing-image__box">
    <img class="cm-media cm-media--uncropped" src="${(self.thumbnailUrl)!""}" alt="${(self.shortText)!""}">
  </div>
</div>
