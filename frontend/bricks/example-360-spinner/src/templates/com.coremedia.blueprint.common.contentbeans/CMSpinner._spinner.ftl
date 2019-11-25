<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMSpinner" -->
<#assign spinnerId=cm.localParameter("id", "") />
<#assign imagesCssClass=cm.localParameter("imagesCssClass", "cm-spinner") />
<#assign imagesCssClassSuffix=cm.localParameter("imagesCssClassSuffix", "picture") />
<#assign spinnerCssClass=cm.localParameter("spinnerCssClass", "cm-spinner__images") />
<#assign limitAspectRatios=cm.localParameters().limitAspectRatios![] />

<#-- spinner (with at least 2 images) -->
<#if (self.sequence![])?size gte 2>
  <div id="${spinnerId!""}" class="cm-spinner__canvas">
    <ol class="${spinnerCssClass}"<@preview.metadata "properties.sequence"/>>
    <#list self.sequence as image>
      <li class="cm-spinner__image">
        <@bp.responsiveImage self=image!cm.UNDEFINED classPrefix=imagesCssClass displayDimmer=false limitAspectRatios=limitAspectRatios classSuffix=imagesCssClassSuffix/>
      </li>
    </#list>
    </ol>

    <div class="cm-spinner__icon"></div>
  </div>
</#if>
