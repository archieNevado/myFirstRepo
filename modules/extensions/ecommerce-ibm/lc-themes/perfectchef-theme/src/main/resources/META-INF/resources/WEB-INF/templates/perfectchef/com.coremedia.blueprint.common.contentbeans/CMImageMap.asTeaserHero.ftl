<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMImageMap" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<div class="cm-imagemap cm-teaser cm-teaser--hero" data-cm-imagemap='{"coordsBaseWidth": "${bp.IMAGE_TRANSFORMATION_BASE_WIDTH}", "defaultLink": "${cm.getLink(self.target!cm.UNDEFINED)}"}'<@cm.metadata (metadata![]) + [self.content] />>
<#-- generate unique id for imagemap -->
<#assign imageMapId=bp.generateId("cm-map-")/>

<#-- generate quick info id for main teaser -->
<#assign quickInfoMainId=imageMapId + "-quickinfo--main" />

<#-- generate quick info ids list and forward the list to the templates areas map and areas quickinfo-->
<#assign imageMapAreas=bp.responsiveImageMapAreas(self) />
<#assign quickInfoIdList = [] />
<#if imageMapAreas?has_content>
  <#list imageMapAreas![] as imageMapArea>
    <#if imageMapArea?has_content>
      <#assign quickInfoIdList = quickInfoIdList + [bp.generateId("cm-quickinfo-")]/>
    </#if>
  </#list>
</#if>
<#assign quickInfoIdList = quickInfoIdList?join(",") />

<#-- imagemap does not make sense without image set -->
<#if self.picture?has_content>
  <div class="cm-imagemap__wrapper">
    <a class="cm-imagemap__link">
      <#-- include image -->
      <@cm.include self=self.picture params={
      "limitAspectRatios": lc.getAspectRatiosForTeaserHero(),
      "classBox": "cm-teaser__content cm-aspect-ratio-box",
      "classImage": "cm-imagemap__image cm-aspect-ratio-box__content cm-notselectable",
      "metadata": ["properties.pictures"],
      "additionalAttr": {"useMap": "#" + imageMapId!"", "unselectable": "on"}
      }/>
      <#-- headline -->
      <#if self.teaserTitle?has_content>
        <h2 class="cm-imagemap__title cm-heading2 cm-heading2--boxed cm-teaser__title"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h2>
      </#if>
    </a>

  <#-- include imagemap area map-->
    <@cm.include self=self view="areasMap" params={"imageMapId": imageMapId, "quickInfoMainId": quickInfoMainId,  "quickInfoIdList": quickInfoIdList}/>
  </div>
<#--include imagemap quick icons-->
  <@cm.include self=self view="areasQuickInfo" params={"imageMapId": imageMapId, "quickInfoMainId": quickInfoMainId, "quickInfoIdList": quickInfoIdList}/>
</#if>
</div>
