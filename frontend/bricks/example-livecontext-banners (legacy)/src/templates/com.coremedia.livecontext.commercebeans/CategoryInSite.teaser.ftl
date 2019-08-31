<#-- @ftlvariable name="self" type="com.coremedia.livecontext.commercebeans.CategoryInSite" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<#import "*/node_modules/@coremedia/brick-utils/src/freemarkerLibs/utils.ftl" as utils />

<#if self.category?has_content && (cmpage.navigation.rootNavigation)?has_content>
  <#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
  <#assign additionalClass=cm.localParameters().additionalClass!"" />
  <#assign link=cm.getLink(self) />

  <#assign renderWrapper=cm.localParameters().renderWrapper!true />
  <#assign renderTeaserTitle=cm.localParameters().renderTeaserTitle!true />
  <#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

  <div class="${blockClass} ${blockClass}--category ${additionalClass}"<@preview.metadata metadata![] />>
  <@utils.optionalTag condition=renderWrapper attr={"class": "${blockClass}__wrapper"}>
      <@utils.optionalLink href="${link}">
        <#-- picture -->
        <@bp.responsiveImage self=(self.category.catalogPicture)!cm.UNDEFINED classPrefix=blockClass displayEmptyImage=renderEmptyImage/>
        <div class="${blockClass}__caption">
          <#if renderTeaserTitle>
            <h3 class="${blockClass}__headline">${(self.category.name)!""}</h3>
          </#if>
        </div>
      </@utils.optionalLink>
    </div>
  </@utils.optionalTag>
</#if>
