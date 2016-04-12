<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMSpinner" -->

<article class="cm-details cm-details--spinner cm-spinner"<@cm.metadata self.content/>>

  <#-- title -->
  <h1 class="cm-details__headline"<@cm.metadata "properties.title"/>>${self.title!""}</h1>

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
    <div class="cm-details__text cm-richtext"<@cm.metadata "properties.detailText"/>>
      <@cm.include self=self.detailText!cm.UNDEFINED />
    </div>
  </#if>

  <#-- tags -->
  <@cm.include self=self view="asTagList"/>
</article>

<#-- related -->
<#if self.related?has_content>
  <div class="cm-related"<@cm.metadata "properties.related"/>>
    <h2>${bp.getMessage("related.label")}</h2>
    <@cm.include self=bp.getContainer(self.related) view="asMedialist" />
  </div>
</#if>

<#-- extensions -->
<@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_END />
