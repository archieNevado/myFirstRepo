<#ftl encoding="UTF-8">
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMImageMap" -->
<#-- @ftlvariable name="imageMapId" type="java.lang.String" -->
<#-- @ftlvariable name="quickInfoMainId" type="java.lang.String" -->
<#-- @ftlvariable name="quickInfoIdList" type="java.lang.String" -->

<#-- if imageMapId is not given, generate new id -->
<#assign imageMapId=imageMapId!(bp.generateId("cm-map-")) />
<#assign quickInfoMainId=quickInfoMainId!(imageMapId + "-quickinfo--main") />

<#assign quickInfoIdList=quickInfoIdList?split(",") />

<#assign imageMapAreas=bp.responsiveImageMapAreas(self) />

<#--imagemap with areas -->
<#if imageMapAreas?has_content>
  <#list imageMapAreas![] as imageMapArea>
    <#if imageMapArea?has_content>

      <#assign index=imageMapArea?index/>
      <#assign quickInfoId=quickInfoIdList[index]/>

      <#if imageMapArea.linkedContent?has_content>
        <#assign linkedContent=imageMapArea.linkedContent />
      <#-- include quickinfo popup -->
        <@cm.include self=linkedContent view="asQuickInfo" params={
        "classQuickInfo": "cm-imagemap__quickinfo cm-quickinfo--compact",
        "metadata": ["properties.localSettings"],
        "quickInfoId": quickInfoId,
        "quickInfoGroup": imageMapId,
        "overlay": bp.setting(self, "overlay", {})
        } />
      </#if><#-- imageMapArea.linkedContent?has_content -->
    </#if>
  </#list>
</#if>
<#-- add main target as quickinfo and button -->
<#if self.target?has_content>
  <div class="cm-imagemap__button-group cm-button-group cm-button-group--overlay">
    <@bp.button href=cm.getLink(self.target!cm.UNDEFINED) text=bp.getMessage("button_more_info") attr={"classes": ["cm-button-group__button"], "metadata": ["properties.target", self.target.content]} />
  </div>
    <@cm.include self=self.target!cm.UNDEFINED view="asQuickInfo" params={
      "classQuickInfo": "cm-imagemap__quickinfo cm-quickinfo--main cm-quickinfo--compact",
      "metadata": ["properties.target"],
      "quickInfoId": quickInfoMainId,
      "quickInfoGroup": imageMapId,
      "overlay": {
        "displayTitle": true,
        "displayShortText": true
      }
    } />
</#if>