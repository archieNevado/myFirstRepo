<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="highlightingMap" type="java.util.Map" -->
<#-- @ftlvariable name="isLast" type="java.lang.Boolean" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />

<#assign cssClasses=cm.localParameters().islast!false?then(" is-last", "") />
<#assign highlightingItem=cm.localParameters().highlightingItem!{} />
<#assign teaserLength=bp.setting(self, "teaser.max.length", 200)/>
<#assign htmlDescription=bp.truncateHighlightedText((highlightingItem["htmlDescription"][0])!self.teaserTitle!"", teaserLength) />
<#assign teaserText=bp.truncateHighlightedText((highlightingItem["teaserText"][0])!"", teaserLength) />
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
        ${(highlightingItem["teaserTitle"][0]?no_esc)!self.teaserTitle}
      </h3>
      <#-- teaserText or htmlDescription as fallback -->
      <#if teaserText?has_content>
        <p<@preview.metadata "properties.teaserText" />>
          <@utils.renderWithLineBreaks text=teaserText />
        </p>
      <#elseif htmlDescription?has_content>
        <p<@preview.metadata "properties.htmlDescription" />>
          <@utils.renderWithLineBreaks text=htmlDescription />
        </p>
      </#if>
    </div>

    <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_SEARCH />
  </a>
</div>
