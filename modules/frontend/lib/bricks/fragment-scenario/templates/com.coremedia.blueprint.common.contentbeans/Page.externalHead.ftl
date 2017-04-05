<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/>
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#-- @ftlvariable name="js" type="com.coremedia.blueprint.common.contentbeans.CMJavaScript" -->
<#-- @ftlvariable name="previewFacade" type="com.coremedia.objectserver.view.freemarker.PreviewFacade" -->
<#assign sliderMetadata=bp.setting(cmpage, "sliderMetaData", "")/>

<#assign context=self.context>
<#assign previewMetadata = previewFacade.metadata(lc.previewMetaData())>
<#assign sliderMetadata = previewFacade.metadata(sliderMetadata)>
<!--CM { "objectType":"page","renderType":"metadata","title":"${context.title}","description":"",
"keywords":"${context.keywords}","pageName":"${context.title}","pbe":"${previewMetadata?html}", "slider":"${sliderMetadata?html}" } CM-->

<#list self.css![] as css>
  <@cm.include self=css view="asCSSLink" />
</#list>
<#list self.internetExplorerCss![] as css>
  <@cm.include self=css view="asCSSLink"/>
</#list>

<#list self.headJavaScript![] as js>
  <@cm.include self=js view="asJSLink"/>
</#list>
<#list self.internetExplorerJavaScript![] as js>
  <@cm.include self=js view="asJSLink"/>
</#list>
<#-- include even the body js in external heads -->
<#list self.javaScript![] as js>
  <@cm.include self=js view="asJSLink"/>
</#list>

<@preview.previewScripts />

<#-- make the crawler index the coremedia content id-->
<#if self.content.contentId?has_content>
<meta name="coremedia_content_id" content="${self.content.contentId}"<@cm.metadata data=[bp.getPageMetadata(cmpage)!""]/>/>
</#if>
