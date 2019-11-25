<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.search.SearchActionState" -->

<#--
    Template Description:

    Displays a simple search form including a label, an input field and a submit button with an optional icon and label.
    The input field uses the new html5 type "search", minlength and required.
-->

<#assign searchQuery=self.form.query!""/>
<#assign searchLink=cm.getLink(self!cm.UNDEFINED, {"page": cmpage})/>
<#assign additionalCssClass=searchQuery?has_content?then(" focus", "")/>

<form id="cm-search-form" class="cm-search cm-search--form" action="${searchLink}" role="search">
  <label for="cm-search-query" class="cm-search__label">${cm.getMessage("search_label")}</label>
  <input id="cm-search-query" type="search" class="cm-search__input${additionalCssClass}" name="query" value="${searchQuery}" placeholder="${cm.getMessage("search_placeholder")}" minlength="${self.minimalSearchQueryLength!3}" required>
  <button class="cm-search__button">
    <i class="cm-search__button-icon"></i>
    <span class="cm-search__button-label">${self.action.title!""}</span>
  </button>
</form>
