<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.p13n.MarketingImage" -->

<div class="cm-box">
  <div class="cm-box__header cm-headline">
    <div class="cm-headline__image cm-aspect-ratio-box">
      <img class="cm-aspect-ratio-box__content" <@cm.dataAttribute name="data-cm-non-adaptive-content" data={"overflow": false} /> src="${(self.thumbnailUrl)!""}" alt="${(self.shortText)!""}" />
    </div>
  </div>
</div>
