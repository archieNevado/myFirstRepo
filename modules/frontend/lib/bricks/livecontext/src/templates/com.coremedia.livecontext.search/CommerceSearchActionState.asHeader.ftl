<#-- @ftlvariable name="self" type="com.coremedia.livecontext.search.CommerceSearchActionState" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/components.ftl" as components />

<#assign formMethod=cm.localParameter("formMethod", "GET") />
<#assign renderAsPopup=cm.localParameter("renderAsPopup", true) />
<#assign additionalFormClass="" />
<#assign minSearchLength=3 />

<#if renderAsPopup>
  <div class="cm-search cm-icon cm-icon--search" <@cm.dataAttribute name="data-cm-popup-control"
    data={"button": ".cm-popup-button--search", "popup": ".cm-popup--search"} /><@preview.metadata self.action.content />>
    <a href="${cm.getLink(self)}" title="${self.action.title!""}" class="cm-popup-button cm-popup-button--search" <@preview.metadata "properties.title" />>
      <i class="cm-icon__symbol icon-search"></i>
      <span class="cm-icon__info cm-visuallyhidden">${self.action.title!""}</span>
    </a>
    <div class="cm-popup cm-popup--search">
</#if>

<form class="cm-search-form ${additionalFormClass}" method="${formMethod}" action="${cm.getLink(self)}" autocomplete="off" <@cm.dataAttribute name="data-cm-search"
  data={"urlSuggestions": cm.getLink(self, "json"), "minLength": minSearchLength} />>
  <fieldset class="cm-search-form__fieldset">
    <div class="cm-search-form__field">
      <label for="SimpleSearchForm_SearchTerm" class="search_label" <@preview.metadata "properties.title" />>${self.action.title!""}</label>
      <#-- id and class is used by wcs -->
      <input id="SimpleSearchForm_SearchTerm" type="search" class="search_input" placeholder="${self.action.title!""}" name="query" value=""<@preview.metadata "properties.title" />>
    </div>
    <@components.button baseClass="" iconClass="icon-arrow-right" attr={"type": "submit", "id": "cm-search-form__button", "class": "cm-search-form__button", "title":  self.action.title!"", "metadata": "properties.title"} />
  </fieldset>
  <div class="cm-popup cm-popup--search-suggestions">
    <ul class="cm-search-suggestions">
      <li class="cm-search-suggestions__item"></li>
    </ul>
    <span class="cm-search-no-suggestions"><@bp.message "search_no_suggestions" /></span>
  </div>
</form>

<#if renderAsPopup>
    </div>
  </div>
</#if>
