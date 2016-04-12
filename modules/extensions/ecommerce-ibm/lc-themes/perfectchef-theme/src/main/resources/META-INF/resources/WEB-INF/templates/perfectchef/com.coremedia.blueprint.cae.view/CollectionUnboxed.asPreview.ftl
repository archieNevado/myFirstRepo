<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.view.CollectionUnboxed" -->

<#assign fragmentViews=[
  {
    "viewName": "inPlacementMain",
    "titleKey": "Preview_Label_Unboxed_In_Placement_Main"
  }, {
    "viewName": "inPlacementSidebar",
    "titleKey": "Preview_Label_Unboxed_In_Placement_Sidebar"
  }, {
    "viewName": "inCollection",
    "titleKey": "Preview_Label_Unboxed_In_Collection"
  }] />

<#if self.delegate?has_content>
  <div>
    <#list fragmentViews as fragmentView>
      <#assign viewName=fragmentView.viewName!"" />
      <#assign titleKey=fragmentView.titleKey!"" />
      <#-- id may not be generated using bp.generateId, as persisting toggle state in local storage will not work -->
      <#assign toggleId="toggle-" + fragmentView_index + "-" + viewName />

      <div class="toggle-item cm-preview-item" data-id="${toggleId}">
        <a href="#" class="toggle-button cm-preview-item__headline"><@bp.message titleKey /></a>
        <div class="toggle-container cm-preview-item__container">
          <div class="cm-preview-content cm-clearfix">
            <#switch viewName>
              <#case "inPlacementMain">
                <div class="content cm-placement-main"<@cm.metadata data=[self.delegate.content, "properties.items"] />>
                  <#list self.delegate.items as item>
                    <@cm.include self=item />
                  </#list>
                </div>
                <#break>
              <#case "inPlacementSidebar">
                <div class="content cm-placement-sidebar"<@cm.metadata data=[self.delegate.content, "properties.items"] />>
                  <#list self.delegate.items as item>
                    <@cm.include self=item view="asTeaser" />
                  </#list>
                </div>
                <#break>
              <#case "inCollection">
                <@cm.include self=self.delegate!cm.UNDEFINED view="asMasonry[]" params={"classCollection": "content"} />
                <#break>
            </#switch>
          </div>
        </div>
      </div>
    </#list>
  </div>
</#if>
