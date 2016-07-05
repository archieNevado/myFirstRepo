<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/>
<#-- @ftlvariable name="self" type="java.lang.Object" -->

<#assign language=bp.getPageLanguageTag(self) />
<#assign direction=bp.getPageDirection(self) />
<!DOCTYPE html>
<!--[if lte IE 8]> <html class="no-js lt-ie9" lang="${language}" dir="${direction}"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang="${language}" dir="${direction}"> <!--<![endif]-->

<#if cmpage?has_content>
  <@cm.include self=cmpage view="head"/>
<#else>
  <head>
    <@preview.previewScripts />
  </head>
</#if>

<body id="top" class="cm-page-preview">

  <@cm.include self=self view="asPreview"/>

  <#-- Show icon in CoreMedia developerMode -->
  <#if cmpage?has_content>
    <#if cmpage.developerMode>
      <div class="cm-preview-developer-mode" data-cm-developer-mode="true">
        <i class="icon-wrench" title="You're in Developer Mode"></i>
        <#-- this js is used for a automatic reload of webrources changes, triggert by the grunt watch task -->
        <script src="http://localhost:35729/livereload.js"></script>
      </div>
    </#if>

    <@cm.include self=cmpage view="bodyEnd"/>
  </#if>

</body>
</html>
