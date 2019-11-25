<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.webflow.WebflowActionState" -->

<#assign action=self.action />

<li class="cm-action" <@preview.metadata data=[action.content, "properties.id"] />>
  <#assign actionLink=cm.getLink(action, {"next": "$nextUrl$", "absolute": true, "scheme": "https"})/>
  <a data-href="${actionLink}" <@preview.metadata data="properties.teaserTitle" />>
    ${action.teaserTitle!""}
  </a>
</li>
