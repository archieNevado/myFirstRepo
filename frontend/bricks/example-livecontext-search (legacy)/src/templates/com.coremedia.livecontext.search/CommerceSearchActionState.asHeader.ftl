<#-- @ftlvariable name="self" type="com.coremedia.livecontext.search.CommerceSearchActionState" -->

<#import "*/node_modules/@coremedia/brick-utils/src/freemarkerLibs/components.ftl" as components />

<#assign formMethod=cm.localParameter("formMethod", "GET") />
<#assign renderAsPopup=cm.localParameter("renderAsPopup", true) />
<#assign minSearchLength=3 />

<div class="cm-search cm-header-icon cm-header-icon--search cm-header-icon--hide-text" <@cm.dataAttribute name="data-cm-popup-control"
  data={"button": ".cm-popup-button--search", "popup": ".cm-popup--search"} /><@preview.metadata self.action.content />>
  <a href="${cm.getLink(self)}" title="${self.action.title!""}" class="cm-popup-button cm-popup-button--search" <@preview.metadata "properties.title" />>
    <i class="cm-header-icon__symbol icon-search"></i>
    <span class="cm-header-icon__info">${self.action.title!""}</span>
  </a>
  <div class="cm-popup cm-popup--search">

    <form class="cm-search--form" method="${formMethod}" action="${cm.getLink(self)}" autocomplete="off" <@cm.dataAttribute name="data-cm-search"
      data={"urlSuggestions": cm.getLink(self, "json"), "minLength": minSearchLength} />>
      <fieldset class="cm-search__form-fieldset">
        <label for="SimpleSearchForm_SearchTerm" class="cm-search__form-label" <@preview.metadata "properties.title" />>${self.action.title!""}</label>
        <#-- id and class is used by wcs -->
        <input id="SimpleSearchForm_SearchTerm" type="search" class="cm-search__form-input" placeholder="${cm.getMessage("search_placeholder")}" name="query" value=""<@preview.metadata "properties.title" /> required>
      </fieldset>
      <@components.button text=cm.getMessage("search_button_label") baseClass="" iconClass="icon-arrow-right" attr={"type": "submit", "class": "cm-search__form-button", "title":  self.action.title!"", "metadata": "properties.title"} />
      <div class="cm-popup cm-popup--search-suggestions">
        <ul class="cm-search-suggestions">
          <li class="cm-search-suggestions__item"></li>
        </ul>
        <span class="cm-search-no-suggestions"><@cm.message "search_no_suggestions" /></span>
      </div>
    </form>

  </div>
</div>
