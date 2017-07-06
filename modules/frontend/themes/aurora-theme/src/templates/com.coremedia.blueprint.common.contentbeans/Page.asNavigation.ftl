<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#assign additionalCssClass=cm.localParameter("additionalCssClass","hidden")/>

<#assign rootNode=self.navigation.rootNavigation/>

<#assign uniqueId="drop_down_"+bp.generateId()/>

<div dojoType="wc.widget.RefreshArea" widgetId="${uniqueId}" controllerId="departmentSubMenu_Controller" aria-labelledby="departmentsButton" <@cm.metadata self.navigation.content />>
  <ul id="departmentsMenu" role="menu" data-parent="header" aria-labelledby="departmentsButton"<@cm.metadata "properties.children" />>
    <#list rootNode.visibleChildren as item>
      <#if item?is_last>
        <@cm.include self=item view="asTopNavigation" params={"additionalCssClass":"active"}/>
      <#else>
        <@cm.include self=item view="asTopNavigation" params={"additionalCssClass":additionalCssClass}/>
      </#if>
    </#list>
  </ul>
</div>
