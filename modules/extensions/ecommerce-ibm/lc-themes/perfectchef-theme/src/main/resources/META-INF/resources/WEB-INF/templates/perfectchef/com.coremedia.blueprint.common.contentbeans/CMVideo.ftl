<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->

<div class="cm-box cm-box--video"<@cm.metadata self.content />>
  <h2 class="cm-box__title cm-heading2 cm-heading2--boxed"<@cm.metadata "properties.title" />>${self.title!""}</h2>
  <div class="cm-box__container">
    <@cm.include self=self view="video" />
  </div>
</div>