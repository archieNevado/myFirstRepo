<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.CMExternalChannel" -->
<#-- @ftlvariable name="isRoot" type="java.lang.Boolean" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->
<#-- @ftlvariable name="isTopLevel" type="java.lang.Boolean" -->
<#-- @ftlvariable name="showNavigationLabel" type="java.lang.Boolean" -->

<#assign cssClass=cm.localParameters().cssClass!""/>
<#assign isRoot=(isRoot!true)/>
<#assign isTopLevel=cm.localParameters().isTopLevel!false/>
<#assign showNavigationLabel=cm.localParameters().showNavigationLabel!false/>
<#assign contentData=self.content!{}/>
<#assign depth=cm.localParameters().depth+1!1/>
<#assign showPicturesInNavigation=cm.localParameters().showPicturesInNavigation!true/>

<#if isRoot || (!((self.hidden)!false))>

<#-- add css class active, if this item is part of the active navigation -->
  <#if (bp.isActiveNavigation(self, (cmpage.navigation.navigationPathList)![]))>
    <#assign cssClass= cssClass + ' active'/>
  </#if>

  <#if self.visibleChildren?has_content>
  <li class="${cssClass} cm-navigation-item dropdown cm-navigation-item-depth-${depth} dropdown" <@preview.metadata data=["properties.children", contentData]/>>
  <#--link to this item in navigation and render children in dropdown list -->
    <@cm.include self=self view="asLink"/>
    <#if isTopLevel>
      <a href="#" class="cm-navigation-item-depth-${depth}__dropdown cm-navigation-item__dropdown dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><span class="caret"></span></a>
    </#if>
    <@cm.include self=self view="asLinkList" params={
      "isRoot": false,
      "depth": depth,
      "cssClass": "dropdown-menu ${cssClass}",
      "showPicturesInNavigation": showPicturesInNavigation,
      "showNavigationLabel": showNavigationLabel
    }/>
  </li>
  <#else>
  <li class="${cssClass} cm-navigation-item cm-navigation-item-depth-${depth}" <@preview.metadata data=["properties.children", contentData]/>>
  <#-- link to this item in navigation -->
    <@cm.include self=self view="asLink"/>
    <#if showPicturesInNavigation && depth == 2>
      <#if self.category.picture?has_content>
        <#assign picture=bp.createBeanFor(self.category.picture)/>
      <#elseif self.category.catalogPicture?has_content>
        <#assign picture=self.category.catalogPicture/>
      <#elseif self.picture?has_content>
        <#assign picture=self.picture/>
      </#if>

      <#if picture?has_content>
        <a class="cm-navigation-item__picture-link" href="${cm.getLink(self.target!cm.UNDEFINED)}">
          <@bp.responsiveImage self=picture!cm.UNDEFINED classPrefix="cm-navigation"/>
        </a>
      </#if>
    </#if>
  </li>
  </#if>
</#if>