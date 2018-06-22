<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->
<#-- @ftlvariable name="collectionProperty" type="java.util.List" -->

<#assign cssClass=cm.localParameters().cssClass!""/>
<#assign depth=(cm.localParameters().depth!0)+1/>
<#assign showPicturesInNavigation=cm.localParameters().showPicturesInNavigation!true/>

<#-- add css class active, if this item is the actual page -->
<#if (self == cmpage.content)>
  <#assign cssClass= cssClass + ' active'/>
</#if>

<li class="${cssClass} cm-navigation-item cm-navigation-item-depth-${depth}" <@preview.metadata collectionProperty!["properties.children"]/>>
  <@cm.include self=self view="asLink" params={"cssClass" : "cm-navigation-item__title"}/>

  <#if showPicturesInNavigation && depth == 2 && self.picture?has_content>
    <a class="cm-navigation-item__picture-link" href="${cm.getLink(self.target!cm.UNDEFINED)}">
      <@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix="cm-navigation"/>
    </a>
  </#if>
</li>