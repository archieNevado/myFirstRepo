<#--
  Initializes an object containing unique ids for the image-map rendering that is meant to be passed to both the picture
  and the quick-info rendering partials of the image-map.

  @param quickInfoModal Set to true if the quick-info should be shown as modal popups.
  @param quickInfoGroup Defines the group the quick-infos are assigned to.
  @param imageMapIdPrefix Defines a prefix to for the generated id of image map.
  @param quickInfoMainIdSuffix Defines a suffix to be added to the generated quick-info representing the main teaser target
  @param quickInfoIdPrefix Defines a prefix for the generated ids of every quick-info

  Example:
  <#assign imageMapParams=generateIds(self)/>
  <@cm.include self=self view="_picture" params=imageMapParams />
  <@cm.include self=self view="_areasQuickInfo" params=imageMapParams/>
-->
<#function generateIds imageMap quickInfoModal=false quickInfoGroup="" imageMapIdPrefix="cm-map-" quickInfoMainIdSuffix="-quickinfo--main" quickInfoIdPrefix="cm-quickinfo-">
  <#-- generate unique id for imagemap -->
  <#assign imageMapId=bp.generateId(imageMapIdPrefix)/>

  <#-- generate quick info id for main teaser -->
  <#assign quickInfoMainId=imageMapId + quickInfoMainIdSuffix />

  <#-- generate quick info ids list and forward the list to the templates areas map and areas quickinfo-->
  <#assign quickInfoData={} />
  <#if quickInfoModal?has_content && quickInfoModal?is_boolean>
    <#assign quickInfoData=quickInfoData + {"modal": quickInfoModal!false} />
  </#if>
  <#if quickInfoGroup?has_content>
    <#assign quickInfoData=quickInfoData + {"group": quickInfoGroup!""} />
  </#if>

  <#-- generate quick info ids list and forward the list to the templates areas map and areas quickinfo-->
  <#assign imageMapAreas=bp.responsiveImageMapAreas(imageMap) />
  <#assign quickInfoIdList = [] />
  <#if imageMapAreas?has_content>
    <#list imageMapAreas![] as imageMapArea>
      <#if imageMapArea?has_content>
        <#assign quickInfoIdList = quickInfoIdList + [bp.generateId(quickInfoIdPrefix)]/>
      </#if>
    </#list>
  </#if>
  <#assign quickInfoIdList = quickInfoIdList?join(",") />

  <#assign imageMapParams={
  "imageMapId": imageMapId,
  "quickInfoMainId": quickInfoMainId,
  "quickInfoIdList": quickInfoIdList
  }/>

  <#return imageMapParams/>
</#function>
