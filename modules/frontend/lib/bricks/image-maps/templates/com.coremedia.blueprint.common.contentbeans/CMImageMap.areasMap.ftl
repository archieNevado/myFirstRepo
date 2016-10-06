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
<map <@bp.renderAttr { "name": imageMapId, "classes": ["cm-imagemap__areas"] } /> <@cm.dataAttribute name="data-cm-areas" data={"quickInfoMainId": quickInfoMainId} />>
<#if imageMapAreas?has_content>
  <#list imageMapAreas![] as imageMapArea>
    <#if imageMapArea?has_content>


    <#--<#assign quickInfoId=bp.generateId("cm-quickinfo-")/>-->
      <#assign index=imageMapArea?index/>
      <#assign quickInfoId=quickInfoIdList[index]/>
      <#assign dataCoords=bp.responsiveImageMapAreaData(imageMapArea.coords)/>

      <#if imageMapArea.linkedContent?has_content>
        <#assign linkedContent=imageMapArea.linkedContent />
        <#assign link=cm.getLink(linkedContent.target!cm.UNDEFINED) />

      <#-- hot zones as areas -->
        <area shape="${imageMapArea.shape}"
              coords="0,0,0,0"
          <@cm.dataAttribute name="data-coords" data=dataCoords />
              href="${link}"
              class="cm-imagemap__area"
              data-quickinfo="${quickInfoId}"
              alt="${imageMapArea.alt!""}"/>
      <#-- quickinfo marker-->
        <#if imageMapArea.displayAsInlineOverlay!false>
          <#assign classOverlay="" />
          <#assign theme=imageMapArea.inlineOverlayTheme!"" />
        <#-- only allow valid themes -->
          <#if (["dark", "light", "dark-on-light", "light-on-dark"]?seq_contains(theme))>
            <#assign classOverlay="cm-overlay--theme-" + theme />
          </#if>
          <div class="cm-imagemap__hotzone cm-imagemap__hotzone--text" data-quickinfo="${quickInfoId}">
            <@cm.include self=linkedContent!cm.UNDEFINED view="asOverlay" params={
            "classOverlay": classOverlay,
            "metadata": ["properties.localSettings"],
            "overlay": bp.setting(self, "overlay", {})
            } />
          </div>
        <#else>
          <@bp.button baseClass="" iconClass="basic-icon-imagemap" iconText=(linkedContent.teaserTitle!bp.getMessage("button_quickinfo")) attr={"class": "cm-imagemap__hotzone cm-imagemap__hotzone--icon", "data-cm-button--quickinfo": '{"target": "${quickInfoId!""}"}'} />
        </#if>
      </#if><#-- imageMapArea.linkedContent?has_content -->
    </#if>
  </#list>
<#else>
  <area shape="default" href="${cm.getLink(self.target!cm.UNDEFINED)}" class="cm-imagemap__area"/>
</#if>
</map>
