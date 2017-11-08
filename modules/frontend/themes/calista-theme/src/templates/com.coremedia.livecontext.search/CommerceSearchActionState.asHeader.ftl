<#-- @ftlvariable name="self" type="com.coremedia.livecontext.search.CommerceSearchActionState" -->

<div class="form-group"<@cm.metadata self.action.content />>
  <form id="cm-search" class="cm-search-form" method="POST" action="${cm.getLink(self)}" autocomplete="off">
    <#-- close button on mobile -->
    <button type="button" class="navbar-toggle pull-left cm-search-form__close">
      <span class="sr-only">${bp.getMessage('search_close')}</span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
    </button>
    <#-- search input field-->
    <input id="SimpleSearchForm_SearchTerm" type="search" class="search_input" placeholder="${self.action.title!""}" name="query" value=""<@cm.metadata "properties.title" /> />
    <#-- submit button -->
    <button id="cm-search-form__button" type="submit" class="btn cm-search__magnifier cm-search-form__button">
      <#--<span class="sr-only">${bp.getMessage('search_submit')}</span>-->
      <span></span>
    </button>
  </form>
</div>
