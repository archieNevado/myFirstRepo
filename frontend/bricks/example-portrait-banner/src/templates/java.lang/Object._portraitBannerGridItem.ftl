<#-- @ftlvariable name="self" type="java.lang.Object" -->

<#assign metadata=cm.localParameters().metadata![] />

<div class="cm-portrait-banner-grid__item"<@preview.metadata metadata />>
  <@cm.include self=self view="asPortraitBanner" />
</div>
