<#ftl strip_whitespace=true>
<#-- @ftlvariable name="blueprintFreemarkerFacade" type="com.coremedia.blueprint.cae.web.taglib.BlueprintFreemarkerFacade" -->

<#-- -------------------------------------------------------------------------------------------------------------------
 *
 * Please check the section "CoreMedia FreeMarker API" in chapter "Reference" in the "Frontend Developer Guide" for
 * details and examples for the following directives.
 * Any changes, additions or removals need to be documented in the manual.
 *
 ------------------------------------------------------------------------------------------------------------------- -->

<#-- GET PAGE METADATA -->
<#function getPageMetadata page>
  <#return blueprintFreemarkerFacade.getPageContext(page).content />
</#function>

<#-- GET PLACEMENT NAME -->
<#function getPlacementPropertyName placement>
  <#return blueprintFreemarkerFacade.getPlacementPropertyName(placement) />
</#function>

<#-- GET CONTAINER METADATA -->
<#function getContainerMetadata container>
  <#return blueprintFreemarkerFacade.getContainerMetadata(container) />
</#function>

<#-- GET CONTAINER WITH ITEMS -->
<#function getContainer items=[]>
  <#return blueprintFreemarkerFacade.getContainer(items) />
</#function>

<#-- GET CONTAINER WITH ITEMS OF GIVEN CONTAINER -->
<#function getContainerFromBase baseContainer items=[]>
  <#return blueprintFreemarkerFacade.getContainer(baseContainer, items) />
</#function>

<#-- GET LOCALE -->
<#function getPageLanguageTag object>
  <#return blueprintFreemarkerFacade.getLanguageTag(object) />
</#function>

<#-- GET DIRECTION -->
<#function getPageDirection object>
  <#return blueprintFreemarkerFacade.getDirection(object) />
</#function>

<#-- GET OFFSET -->
<#function getOffsetClass index numberOfItems itemsPerRow  prefix="" force=false>
  <#-- bootstrap default grid = 12 rows -->
  <#assign width=12/itemsPerRow />
  <#assign isFirstItemOfRow=(index % itemsPerRow == 0) />
  <#assign isLastRow=(numberOfItems - numberOfItems % itemsPerRow - index) <= 0 />
  <#-- define offset class to align items in rows containing less than 3 items centered -->
  <#assign offsetClass="" />
  <#-- offset only applies to first element of last row -->
  <#if (isLastRow && isFirstItemOfRow)>
    <#-- offset depends on the number of items in the last row -->
    <#assign offsetValue=(12-((numberOfItems % itemsPerRow)*width))/2 />
    <#assign offsetClass="${prefix}offset-${offsetValue}" />
  <#elseif (force)>
    <#assign offsetClass="${prefix}offset-0" />
  </#if>

  <#return offsetClass>
</#function>

<#-- RENDER NEW ROW -->
<#macro renderNewRow index itemsPerRow additionalClass="">
  <#-- bootstrap default grid = 12 rows -->
  <#assign width=12/itemsPerRow />
  <#assign isFirstItemOfRow=(index % itemsPerRow == 0) />
  <#-- offset only applies to first element of last row -->
  <#if (isFirstItemOfRow && index != 0)>
    </div>
    <div class="${additionalClass}row">
  </#if>
</#macro>

<#-- GET METADATA OF GIVEN PLACEMENT -->
<#function getPlacementHighlightingMetaData placement>
  <#return blueprintFreemarkerFacade.getPlacementHighlightingMetaData(placement)>
</#function>


<#-- --- DEPRECATED ------------------------------------------------------------------------------------------------ -->


<#-- DEPRECATED -->
<#function getPlacementByName name pagegrid>
  <#return blueprintFreemarkerFacade.getPlacementByName(name, pagegrid) />
</#function>

<#-- DEPRECATED, use bp.getLanguageTag instead -->
<#function getPageLanguage object>
  <#return blueprintFreemarkerFacade.getLanguage(object) />
</#function>
