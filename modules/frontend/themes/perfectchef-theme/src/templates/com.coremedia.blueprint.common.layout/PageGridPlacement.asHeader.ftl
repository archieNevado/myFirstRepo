<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->
<#assign navigation=cmpage.navigation!cm.UNDEFINED/>
<#assign rootNavigationNavigation=cmpage.navigation.rootNavigation!cm.UNDEFINED/>
<#if rootNavigationNavigation?has_content>
  <#assign link=cm.getLink(rootNavigationNavigation)/>
    <div class="cm-placement-header__logo cm-logo">
      <@bp.optionalLink href="${link}" attr={
      "title": rootNavigationNavigation.title!"",
      "class": "cm-ir"
      }>
          <span class="cm-visuallyhidden">${rootNavigationNavigation.title!""}</span>
      </@bp.optionalLink>
    </div>


  <#list self.items![] as item>
  <div class="cm-placement-header__item">
    <@cm.include self=item view="asHeader"/>
  </div>
  </#list>

    <nav id="cm-navigation" class="cm-placement-header__item cm-icon cm-icon--navigation">
        <ul class="cm-icon__symbol cm-navigation cm-dropdown" data-dropdown-menus="ul" data-dropdown-items="li"
            data-dropdown-class-button-open="icon-menu-next" data-dropdown-class-button-close="icon-menu-back">
          <@cm.include self=rootNavigationNavigation view="asLinkListItem" params={"maxDepth": 5} />
        </ul>
    </nav>

  <#if lcNavigation?has_content>
    <#assign navigation=lcNavigation />
  <#else>
    <#assign navigation=cmpage.navigation />
  </#if>
  <@cm.include self=navigation!cm.UNDEFINED view="asBreadcrumb"
  params={
  "classBreadcrumb": "cm-placement-header__breadcrumb"
  } />
</#if>