<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMSpinner" -->
<#assign additionalClass=cm.localParameters().additionalClass!"cm-details" />
<#assign relatedView=cm.localParameters().relatedView!"related" />

<#assign renderDate=cm.localParameter("renderDate", true) />
<#assign renderTags=cm.localParameter("renderTags", true) />
<#assign renderRelated=cm.localParameter("renderRelated", true) />

<article class="${additionalClass} ${additionalClass}--spinner cm-spinner"<@cm.metadata self.content/>>

  <#-- title -->
  <h1 class="${additionalClass}__headline"<@cm.metadata "properties.title"/>>${self.title!""}</h1>

  <#-- spinner (with at least 2 images) -->
  <#if (self.sequence![])?size gt 2>
    <div class="cm-spinner__canvas">
      <ol class="cm-spinner__images"<@cm.metadata "properties.sequence"/>>
        <#list self.sequence as image>
          <li class="cm-spinner__image">
            <@cm.include self=image params={
            "limitAspectRatios": ["landscape_ratio4x3"],
            "classBox": "cm-spinner__picture-box",
            "classImage": "cm-spinner__picture"
            }/>
          </li>
        </#list>
      </ol>
      <div class="cm-spinner__icon"></div>
    </div>
  </#if>

  <#-- text -->
  <#if self.detailText?has_content>
    <div class="${additionalClass}__text cm-richtext"<@cm.metadata "properties.detailText"/>>
      <@cm.include self=self.detailText!cm.UNDEFINED />
    </div>
  </#if>

  <#-- date -->
  <#if renderDate && self.externallyDisplayedDate?has_content>
    <div class="${additionalClass}__date"<@cm.metadata "properties.externallyDisplayedDate"/>>
      <@bp.renderDate self.externallyDisplayedDate.time "${additionalClass}__time" />
    </div>
  </#if>

  <#-- tags -->
  <#if renderTags>
    <@cm.include self=self view="_tagList"/>
  </#if>
</article>

<#-- related -->
<#if renderRelated>
  <@cm.include self=self view="_related" params={"relatedView": relatedView}/>
</#if>

<#-- extensions -->
<@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_END />
