<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.p13n.MarketingText" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<div class="cm-teasable cm-teasable--text"<@preview.metadata metadata![] />>
    <div class="cm-teasable__content">
      <span>${self.text?no_esc!""}</span>
    </div>
</div>
