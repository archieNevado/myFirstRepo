<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMSpinner" -->

<div class="cm-spinner"<@cm.metadata self.content/>>
  <#-- Title -->
  <h2 class="cm-spinner__headline"<@cm.metadata "properties.title"/>>${self.title!""}</h2>
  <#-- Spinner (with at least 2 images) -->
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
    </div>
  </#if>
  <#-- Extensions -->
  <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_END />
</div>

<#-- related -->
<#if self.related?has_content>
<div class="cm-related"<@cm.metadata "properties.related"/>>
  <h2>${bp.getMessage("related.label")}</h2>
  <@cm.include self=bp.getContainer(self.related) view="asTeaser" />
</div>
</#if>
