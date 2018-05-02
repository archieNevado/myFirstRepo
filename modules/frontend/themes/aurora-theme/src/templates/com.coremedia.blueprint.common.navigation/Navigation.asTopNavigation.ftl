<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.navigation.Navigation" -->
<#assign link=cm.getLink(self!cm.UNDEFINED)/>
<#assign uniqueId=bp.generateId("dm")/>
<#assign additionalCssClass=cm.localParameter("additionalCssClass","hidden")/>
<li<#if additionalCssClass?has_content> class="${additionalCssClass}"</#if><@preview.metadata self.content />>
  <a id="departmentButton_${uniqueId}" href="${link}" class="departmentButton" role="menuitem"<#if self.visibleChildren?has_content> aria-haspopup="true" data-toggle="departmentMenu_${uniqueId}"</#if>>
    <span<@preview.metadata "properties.title" />>${self.title}</span>
    <#if self.visibleChildren?has_content>
      <div class="arrow_button_icon"></div>
    </#if>
  </a>
  <div id="departmentMenu_${uniqueId}" class="max-width departmentMenu"<#if self.visibleChildren?has_content> role="menu"</#if> data-parent="departmentsMenu" data-id="${uniqueId}" aria-label="${self.title}">
    <div class="header">
      <a id="departmentLink_${uniqueId}" href="${link}" class="link menuLink" aria-label="${self.title}" role="menuitem" tabindex="-1">${self.title}</a>
      <a id="departmentToggle_${uniqueId}" href="#" class="toggle" role="button" data-toggle="departmentMenu_${uniqueId}" aria-labelledby="departmentLink_${uniqueId}"><span role="presentation"></span></a>
    </div>
  <#list self.visibleChildren>
    <ul class="categoryList" <@preview.metadata "properties.visibleChildren" />>
      <#items as child >
        <li>
          <@cm.include self=child view="asSubNavigation" params={"parentLevelId": uniqueId, "level": 1}/>
        </li>
      </#items>
    </ul>
  </#list>
  </div>
</li>