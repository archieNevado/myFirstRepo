<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPlaceholder" -->
<#-- @ftlvariable name="cmpage.content" type="com.coremedia.blueprint.ecommerce.contentbeans.CMCategory" -->
<#-- @ftlvariable name="product" type="com.coremedia.blueprint.ecommerce.contentbeans.CMProduct" -->

<#import "*/node_modules/@coremedia/brick-bootstrap/src/freemarkerLibs/bootstrap.ftl" as bootstrap />

<#assign products=cmpage.content.products![]/>
<#assign numberOfItems=products?size />
<#assign itemsPerRow=4 />

<#if (numberOfItems > 0)>
<div class="cm-placeholder cm-placeholder--category"<@preview.metadata self.content />>
  <#-- headline -->
  <h2 class="cm-placeholder__headline"<@preview.metadata "properties.title" />>${self.title}</h2>
  <#-- list of products -->
  <div class="row">
    <#list products as item>
      <#-- add new row -->
      <@bootstrap.renderNewRow item_index itemsPerRow />
      <#-- render the items as claim teaser -->
      <div class="col-xs-6 col-sm-3">
        <@cm.include self=item view="asSquarelist" />
      </div>
    </#list>
  </div>
</div>
</#if>
