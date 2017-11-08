<#-- @ftlvariable name="self" type="com.coremedia.livecontext.commercebeans.CategoryInSite" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<#assign cssClasses = cm.localParameter("islast", false)?then(" is-last", "") />
<#assign additionalClass=cm.localParameters().additionalClass!"cm-teasable" />
<#assign link=cm.getLink(self) />

<div class="${additionalClass} ${additionalClass}--category"<@cm.metadata metadata![] />>
  <div class="${additionalClass}__wrapper">
    <@bp.optionalLink href="${link}">
      <#-- picture -->
      <div class="${additionalClass}__caption caption">
        <@cm.include self=self.category view="_picture" params={"additionalClass":additionalClass}/>
          <h3 class="${additionalClass}__headline">
            <span>${(self.category.name)!""}</span>
          </h3>
      </div>
    </@bp.optionalLink>
  </div>
</div>
