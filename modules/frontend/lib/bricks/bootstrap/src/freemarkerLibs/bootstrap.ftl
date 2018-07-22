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

  Note:
  For now uses the deprecated function to avoid code duplication as we cannot depend from "cae-base-lib" to
  the frontend workspace.
-->
<#function getOffsetClass index numberOfItems itemsPerRow  prefix="" force=false>
  <#return bp.getOffsetClass(index, numberOfItems, itemsPerRow, prefix, force) />
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
      <@bp.renderNewRow index=item_index itemsPerRow=itemsPerRow additionalClass="row-grid "/>
    </#if>
  </#list>

  Note:
  For now uses the deprecated function to avoid code duplication as we cannot depend from "cae-base-lib" to
  the frontend workspace.
-->
<#macro renderNewRow index itemsPerRow additionalClass="">
  <@bp.renderNewRow index=index itemsPerRow=itemsPerRow additionalClass=additionalClass />
</#macro>
