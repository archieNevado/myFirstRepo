<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#-- @ftlvariable name="self.content" type="com.coremedia.blueprint.common.contentbeans.CMChannel" -->
<#assign sliderMetadata=bp.setting(cmpage, "sliderMetaData", "")/>

<body id="top" <@cm.metadata data=sliderMetadata />>

<#-- skiplinks -->
<#if bp.setting(cmpage.navigation, "render_skip_links", true)>
  <div class="cm-skiplinks">
    <a class="sr-only sr-only-focusable" href="#cm-navigation"><@bp.message "skiplinks_navigation" /></a>
    <a class="sr-only sr-only-focusable" href="#cm-main"><@bp.message "skiplinks_content" /></a>
    <a class="sr-only sr-only-focusable" href="#cm-search"><@bp.message "skiplinks_search" /></a>
  </div>
</#if>

<#-- render page grid with content -->
<@cm.include self=self.pageGrid!cm.UNDEFINED />

<#-- info box for users with javascript disabled -->
<noscript class="cm-javascript">
  ${bp.getMessage("error_noJavascript")}
</noscript>

<#if bp.setting(cmpage.navigation, "render_back_to_top_button", true)>
  <a id="back-to-top" href="#" class="btn btn-primary cm-back-to-top" role="button" title="<@bp.message key="button_top" highlightErrors=false />" data-toggle="tooltip" data-placement="left">
    <span class="glyphicon glyphicon-chevron-up"></span>
  </a>
</#if>

<@cm.include self=self view="bodyEnd"/>

</body>
