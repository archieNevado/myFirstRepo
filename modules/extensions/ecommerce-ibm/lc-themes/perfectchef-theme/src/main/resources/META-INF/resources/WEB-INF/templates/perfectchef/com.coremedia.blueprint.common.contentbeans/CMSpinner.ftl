<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMSpinner" -->

<div class="cm-box cm-box--spinner cm-spinner"<@cm.metadata self.content/>>
  <#-- headline -->
  <h2 class="cm-box__headline cm-heading2 cm-heading2--boxed" <@cm.metadata "properties.teaserTitle"/>>${self.teaserTitle!""}</h2>
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
      <div class="cm-spinner__icon"></div>
    </div>
  </#if>
  <#-- detailtext -->
  <#if self.detailText?has_content>
    <div class="cm-box__content"<@cm.metadata "properties.detailText"/>>
      <@cm.include self=self.detailText!cm.UNDEFINED />
    </div>
  </#if>
</div>
