<#-- @ftlvariable name="self" type="java.lang.Object" -->
<#-- @ftlvariable name="fragmentViews" type="java.util.List" -->
<#-- @ftlvariable name="additionalAttr" type="java.util.Map" -->
<#-- @ftlvariable name="fragmentView.bean" type="java.lang.Object" -->
<#-- @ftlvariable name="fragmentView.viewName" type="java.lang.String" -->
<#-- @ftlvariable name="fragmentView.viewParams" type="java.util.Map" -->
<#-- @ftlvariable name="fragmentView.titleKey" type="java.lang.String" -->
<#-- @ftlvariable name="fragmentView.title" type="java.lang.String" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />

<div<@utils.renderAttr attr=additionalAttr!{} /> <@preview.metadata self.content!cm.UNDEFINED />>
  <#-- iterate over all views as requested by the including template -->
  <#list fragmentViews![] as fragmentView>
    <#assign bean=fragmentView.bean!self />
    <#assign viewName=fragmentView.viewName!cm.UNDEFINED />
    <#assign viewParams=fragmentView.viewParams!{} />
    <#assign titleKey=fragmentView.titleKey!"" />
    <#assign title=fragmentView.title!"" />
    <#-- id may not be generated using bp.generateId, as persisting toggle state in local storage will not work -->
    <#assign toggleId="toggle-" + fragmentView_index + "-" + viewName />

    <div class="toggle-item cm-preview-item" data-id="${toggleId}">
      <a href="#" class="toggle-button cm-preview-item__headline">
        <#if titleKey?has_content && (bp.hasMessage(titleKey) || !title?has_content)>
            <@bp.message titleKey />
          <#else>
        ${title}
        </#if>
      </a>
      <div class="toggle-container cm-preview-item__container">
        <div class="cm-preview-content">
          <#if viewName =="asListItem">
            <ol class="list">
              <@cm.include self=bean view=viewName params=viewParams />
            </ol>
          <#else>
            <div class="content">
              <#if viewName == "DEFAULT">
                  <@cm.include self=bean params=viewParams />
                <#else>
                <@cm.include self=bean view=viewName params=viewParams />
              </#if>
            </div>
          </#if>
        </div>
      </div>
    </div>
  </#list>
</div>
