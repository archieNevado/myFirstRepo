<#--
  Returns the offset for given elements of the bootstrap grid.

  @param index The 0-based number as index of the current item in the loop.
  @param numberOfItems The total number of items.
  @param itemsPerRow The number of items per row.
  @param prefix The given prefix for the offset CSS class.
  @param force Forces the offset value to offset-0 with the given prefix as CSS class attribute.

  Example:
  <#assign numberOfItems=items?size/>
  <#assign itemsPerRow=cm.localParameter("itemsPerRow", 3)/>

  <#list items as item>
    <#assign offsetClassTablet=""/>
    <#if center>
      <#assign offsetClassTablet=getOffsetClass(item_index, numberOfItems, itemsPerRow, " col-sm-")/>
    </#if>
  </#list>
-->
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

<#--
  Renders a closing and an opening div for Bootstrap Framework grid.

  @param index The 0-based number as index of the current item in the loop.
  @param itemsPerRow The number of items per row.
  @param additionalClass Renders an additional CSS class to a div tag as row element.

  Example:
  <#assign itemsPerRow=cm.localParameter("itemsPerRow", 3)/>
  <#assign addRows=cm.localParameters().addRows!true/>

  <#list items as item>
    <#if addRows>
      <@renderNewRow index=item_index itemsPerRow=itemsPerRow additionalClass="my-class"/>
    </#if>
  </#list>
-->
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
