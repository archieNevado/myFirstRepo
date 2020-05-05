<#-- @ftlvariable name="self" type="java.lang.Object" -->

<#assign metadata=cm.localParameters().metadata![] />

<#compress>
  <li class="cm-footer-navigation-column__item"<@preview.metadata metadata/>>
    <@cm.include self=self view="asFooterNavigationLink" />
  </li>
</#compress>
