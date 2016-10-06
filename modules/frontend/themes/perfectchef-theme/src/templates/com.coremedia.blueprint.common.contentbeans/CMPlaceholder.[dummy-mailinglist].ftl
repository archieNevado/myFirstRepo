<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPlaceholder" -->

<div class="cm-mailinglist"<@cm.metadata self.content />>
  <h3 class="cm-mailinglist__title"><@bp.message "mailinglist_label" /></h3>
  <form class="cm-mailinglist__form">
    <input type="text" class="cm-input" placeholder="<@bp.message "mailinglist_placeholder" "" false />" />
  </form>
</div>
