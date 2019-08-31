<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMImageMap" -->

<div class="cm-imagemap" <@cm.dataAttribute name="data-cm-imagemap" data={"coordsBaseWidth": bp.IMAGE_TRANSFORMATION_BASE_WIDTH} />>
  <@cm.include self=self view="_picture" params={
    "blockClass": cm.localParameter("heroBlockClass", "cm-hero"),
    "renderEmptyImage": cm.localParameter("renderEmptyImage", true)
  }
  />
</div>
