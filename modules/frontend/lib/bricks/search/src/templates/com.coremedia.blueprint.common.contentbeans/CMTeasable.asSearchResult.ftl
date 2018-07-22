<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="highlightingMap" type="java.util.Map" -->
<#-- @ftlvariable name="isLast" type="java.lang.Boolean" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />

<#assign cssClasses=cm.localParameters().islast!false?then(" is-last", "") />
<#assign highlightedItem=(highlightingMap.get(self))!{} />
<#assign teaserLength=bp.setting(self, "teaser.max.length", 200)/>
<#assign htmlDescription=bp.truncateHighlightedText((highlightedItem["htmlDescription"][0])!self.htmlDescription!"", teaserLength) />
<#assign teaserText=bp.truncateHighlightedText((highlightedItem["teaserText"][0])!self.teaserText!"", teaserLength) />
<#assign target=(self.target?has_content && self.target.openInNewTab)?then('target="_blank"', "") />
<#assign rel=(self.target?has_content && self.target.openInNewTab)?then('rel="noopener"', "") />

<div class="cm-search__item ${cssClasses}"<@preview.metadata self.content />>
  <a href="${cm.getLink(self.target!cm.UNDEFINED)}" ${target?no_esc} ${rel?no_esc}>
    <#-- image -->
    <#if self.picture?has_content>
      <@cm.include self=self.picture view="media" params={
        "limitAspectRatios": ["landscape_ratio4x3"],
        "classBox": "cm-search__picture-box",
        "classMedia": "cm-search__picture",
        "metadata": ["properties.pictures"]
      }/>
    </#if>
    <div class="cm-search__caption">
      <#-- teaserTitle -->
      <h3<@preview.metadata "properties.teaserTitle" />>
        ${(highlightedItem["teaserTitle"][0])!self.teaserTitle}
      </h3>
      <#-- htmlDescription or teaserText -->
      <#if htmlDescription?has_content>
        <p<@preview.metadata "properties.htmlDescription" />>
          <@utils.renderWithLineBreaks text=htmlDescription />
        </p>
      <#elseif teaserText?has_content>
        <p<@preview.metadata "properties.teaserText" />>
          <@utils.renderWithLineBreaks text=teaserText />
        </p>
      </#if>
    </div>

    <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_SEARCH />
  </a>
</div>
