<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="classHeadline" type="java.lang.String" -->

<#if self.pictures?has_content>
<div class="cm-headline ${classHeadline!""}">
  <@cm.include self=bp.getContainer(self.pictures) view="asCarousel" params={"viewItems": "asTeaserHero", "additionalClass":"cm-headline__image", "metadataItemsName":"pictures"}/>
  <#if self.teaserTitle?has_content>
      <h2 class="cm-headline__title cm-heading2 cm-heading2--boxed" <@cm.metadata "properties.teaserTitle"/>>${self.teaserTitle!""}</h2>
  </#if>
</div>
</#if>