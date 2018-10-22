<#-- @ftlvariable name="self" type="com.coremedia.livecontext.commercebeans.CategoryInSite" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />

<#if self.category?has_content && (cmpage.navigation.rootNavigation)?has_content>
  <#assign cssClasses = cm.localParameter("islast", false)?then(" is-last", "") />
  <#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
  <#assign additionalClass=cm.localParameters().additionalClass!"" />
  <#assign link=cm.getLink(self) />

  <#assign renderTeaserTitle=cm.localParameter("renderTeaserTitle", true) />
  <#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
  <#assign renderDimmer=cm.localParameter("renderDimmer", true) />
  <#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

  <div class="${blockClass} ${blockClass}--category ${cssClasses} ${additionalClass}"<@preview.metadata metadata![] />>
    <div class="${blockClass}__wrapper">
      <@utils.optionalLink href="${link}">
        <#-- picture -->
        <@bp.responsiveImage self=(self.category.catalogPicture)!cm.UNDEFINED classPrefix=blockClass displayEmptyImage=renderEmptyImage displayDimmer=renderDimmer limitAspectRatios=bp.setting(cmpage.navigation, "default_aspect_ratios_for_teaser", [])/>
        <div class="${blockClass}__caption">
          <#if renderTeaserTitle>
            <h3 class="${blockClass}__headline">${(self.category.name)!""}</h3>
          </#if>
        </div>
      </@utils.optionalLink>
    </div>
  </div>
</#if>
