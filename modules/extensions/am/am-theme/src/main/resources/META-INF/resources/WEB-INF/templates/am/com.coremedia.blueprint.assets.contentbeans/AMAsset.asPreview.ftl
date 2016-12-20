<#-- @ftlvariable name="self" type="com.coremedia.blueprint.assets.contentbeans.AMAsset" -->

<#assign fragmentViews=[] />
<#if am.hasDownloadPortal()>
  <#assign fragmentViews=fragmentViews + [{"viewName": "asDownloadPortal", "titleKey": "am_preview_label_download_portal"}] />
</#if>
<#list self.renditions as rendition>
  <#if rendition.blob?has_content>
    <#assign name=rendition.name!"" />
    <#if name?has_content && name != "original">
      <#assign viewName="[" + name + "]" />
      <#assign titleKey="am_preview_label_rendition_" + name />
      <#assign title=name?cap_first />
      <#assign fragmentViews=fragmentViews + [{"bean": rendition, "viewName": viewName, "titleKey": titleKey, "title": title}] />
    </#if>
  </#if>
</#list>

<#if (fragmentViews?size > 0)>
  <@cm.include self=self view="multiViewPreview" params={
    "fragmentViews": fragmentViews
  }/>
<#else>
  <#-- TODO: CMS-6811 this is a message for the preview which should be localized in the language of the studio user. -->
  <span>No renditions to preview.</span>
</#if>
