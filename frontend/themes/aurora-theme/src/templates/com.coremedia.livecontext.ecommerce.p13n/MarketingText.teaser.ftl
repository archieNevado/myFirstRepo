<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.p13n.MarketingText" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<div class="cm-marketing-text"<@preview.metadata metadata![] />>
    <div class="cm-marketing-text__content">
      <span>${self.text?no_esc!""}</span>
    </div>
</div>
