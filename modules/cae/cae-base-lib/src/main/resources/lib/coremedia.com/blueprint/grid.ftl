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

<#--
 * Utility function to allow an dynamizable container.
 * A strategy determines if the container will be rendered as dynamic include.
 *
 * @param object The object that can be persisted in a link
 * @param propertyPath The property path to retrieve the container's items
 * @return a new container
 -->
<#function getDynamizableContainer object propertyPath>
  <#return blueprintFreemarkerFacade.getDynamizableContainer(object, propertyPath) />
</#function>

<#-- GET CONTAINER WITH ITEMS OF GIVEN CONTAINER -->
<#--
 * Utility function to allow rendering of containers with custom items, e.g. partial containers with a subset of
 * the items the original container had.
 *
 * @param baseContainer The base container the new container shall be created from
 * @param items The items to be put inside the new container
 -->
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

<#-- GET METADATA OF GIVEN PLACEMENT -->
<#function getPlacementHighlightingMetaData placement>
  <#return blueprintFreemarkerFacade.getPlacementHighlightingMetaData(placement)>
</#function>


<#-- --- DEPRECATED ------------------------------------------------------------------------------------------------ -->


<#-- DEPRECATED, see Frontend Developer Guide -->
<#function getOffsetClass index numberOfItems itemsPerRow  prefix="" force=false>
  <#-- bootstrap default grid = 12 rows -->
  <#local width=12/itemsPerRow />
  <#local isFirstItemOfRow=(index % itemsPerRow == 0) />
  <#local isLastRow=(numberOfItems - numberOfItems % itemsPerRow - index) <= 0 />
  <#-- define offset class to align items in rows containing less than 3 items centered -->
  <#local offsetClass="" />
  <#-- offset only applies to first element of last row -->
  <#if (isLastRow && isFirstItemOfRow)>
    <#-- offset depends on the number of items in the last row -->
    <#local offsetValue=(12-((numberOfItems % itemsPerRow)*width))/2 />
    <#local offsetClass="${prefix}offset-${offsetValue}" />
  <#elseif (force)>
    <#local offsetClass="${prefix}offset-0" />
  </#if>

  <#return offsetClass>
</#function>

<#-- DEPRECATED, see Frontend Developer Guide -->
<#macro renderNewRow index itemsPerRow additionalClass="">
  <#-- bootstrap default grid = 12 rows -->
  <#local width=12/itemsPerRow />
  <#local isFirstItemOfRow=(index % itemsPerRow == 0) />
  <#-- offset only applies to first element of last row -->
  <#if (isFirstItemOfRow && index != 0)>
    </div>
    <div class="${additionalClass}row">
  </#if>
</#macro>

<#-- DEPRECATED -->
<#function getPlacementByName name pagegrid>
  <#return blueprintFreemarkerFacade.getPlacementByName(name, pagegrid) />
</#function>

<#-- DEPRECATED, use bp.getLanguageTag instead -->
<#function getPageLanguage object>
  <#return blueprintFreemarkerFacade.getLanguage(object) />
</#function>
