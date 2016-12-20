<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMSitemap" -->

<#assign maxDepth=bp.setting(self, "sitemap_depth", 4)?number />
<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign additionalClass=cm.localParameters().additionalClass!"cm-teasable" />

<#if self.root?has_content>
<div class="${additionalClass} ${cssClasses}"<@cm.metadata self.content />>
  <div class="${additionalClass}__wrapper">

    <@cm.include self=self.root view="asLinkList" params={
    "maxDepth": maxDepth,
    "cssClass": "cm-collection cm-collection--sitemap"
    } />
  </div>
</div>
</#if>
