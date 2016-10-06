<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMSpinner" -->
<#-- @ftlvariable name="limitAspectRatios" type="java.util.List" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->

<#assign ownPictureCssClass="" />
<#assign spinnerId = bp.generateId("spinner") />

<div class="cm-lightbox cm-lightbox--inline ${classBox}" data-cm-popup-class="cm-spinner--popup ${classBox}" <@cm.metadata self.content />>
  <#-- inline -->
  <#if (self.sequence![])?size gt 2>
    <div class="cm-teaser cm-teaser--spinner cm-spinner">
      <a href="#${spinnerId}" title="${self.title!""}">
        <div id="${spinnerId}" class="cm-spinner__canvas">
          <ol class="cm-spinner__images cm-aspect-ratio-box"<@cm.metadata "properties.sequence"/>>
            <#list self.sequence as image>
              <li class="cm-spinner__image">
                <@cm.include self=image params={
                "limitAspectRatios": limitAspectRatios,
                "classBox": "cm-aspect-ratio-box",
                "classImage": "cm-aspect-ratio-box__content"
                }/>
              </li>
            </#list>
          </ol>
          <div class="cm-spinner__icon"></div>
        </div>
      </a>
    </div>
  </#if>
</div>
