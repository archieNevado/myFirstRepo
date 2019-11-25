<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.asset.CatalogPicture" -->
<#-- @ftlvariable name="limitAspectRatios" type="java.lang.String" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->
<#-- @ftlvariable name="classMedia" type="java.lang.String" -->

<#assign limitAspectRatios=cm.localParameters().limitAspectRatios![] />
<#assign classBox=cm.localParameters().classBox!"" />
<#assign classMedia=cm.localParameters().classMedia!"" />

<#if self.picture?has_content>
  <@cm.include self=bp.createBeanFor(self.picture) view="media" params={
    "limitAspectRatios": limitAspectRatios,
    "classBox": classBox,
    "classMedia": classMedia
  }/>
<#else>
  <div class="${classBox}">
    <img class="${classMedia} cm-uncropped-catalog-picture" src="${self.url!""}">
  </div>
</#if>
