<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMImageMap" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->
<#-- @ftlvariable name="classQuickInfo" type="java.lang.String" -->
<#-- @ftlvariable name="quickInfoId" type="java.lang.String" -->
<#-- @ftlvariable name="quickInfoModal" type="java.lang.Boolean" -->
<#-- @ftlvariable name="quickInfoGroup" type="java.lang.String" -->

<#assign index=cm.localParameters().index!0 />
<#assign hasImage=self.picture?has_content />
<#assign hasEvenIndex=(index % 2 == 0) />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />

<#-- generate unique id for imagemap -->
<#assign imageMapId=bp.generateId("cm-map-")/>

<#-- generate quick info id for main teaser -->
<#assign quickInfoMainId=imageMapId + "-quickinfo--main" />

<#-- generate quick info ids list and forward the list to the templates areas map and areas quickinfo-->
<#assign quickInfoData={} />
<#if quickInfoModal?has_content && quickInfoModal?is_boolean>
  <#assign quickInfoData=quickInfoData + {"modal": quickInfoModal!false} />
</#if>
<#if quickInfoGroup?has_content>
  <#assign quickInfoData=quickInfoData + {"group": quickInfoGroup!""} />
</#if>

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

<div class="cm-imagemap cm-teaser cm-teaser--hero" data-cm-imagemap='{"coordsBaseWidth": "${bp.IMAGE_TRANSFORMATION_BASE_WIDTH}", "defaultLink": "${cm.getLink(self.target!cm.UNDEFINED)}"}'<@cm.metadata (metadata![]) + [self.content] />>

  <div class="cm-teasable ${hasEvenIndex ? then('', 'cm-teasable--alternative')} ${hasImage ? then('', 'cm-teasable--no-image')} row ${additionalClass!""}"<@cm.metadata self.content />>
  <#if hasImage>
    <div class="col-xs-12 col-sm-6 ${hasEvenIndex ? then('col-sm-push-6', '')}">
      <div class="cm-imagemap__wrapper">
        <a class="cm-imagemap__link">
          <#-- include image -->
          <@cm.include self=self.picture params={
          "limitAspectRatios": ["portrait_ratio1x1", "landscape_ratio16x9"],
          "classBox": "cm-teasable__picture-box",
          "classImage": "cm-teasable__picture cm-imagemap__image cm-notselectable",
          "metadata": ["properties.pictures"],
          "additionalAttr": {"useMap": "#" + imageMapId!"", "unselectable": "on"}
          }/>
        </a>
          <#-- include imagemap -->
          <@cm.include self=self view="areasMap" params={"imageMapId": imageMapId, "quickInfoMainId": quickInfoMainId, "quickInfoIdList": quickInfoIdList}/>
      </div>
    </div>
  </#if>
    <div class="col-xs-12 col-sm-6 ${hasEvenIndex ? then('col-sm-pull-6', '')}">
      <div class="cm-teasable__text-content-box">
        <div class="cm-teasable__text-content">
        <#-- headline -->
        <@bp.optionalLink href="${link}">
          <h3 class="cm-teasable__text"<@cm.metadata "properties.teaserTitle" />>
            <span>${self.teaserTitle!""}</span>
          </h3>
        </@bp.optionalLink>

        <#-- TEASER TEXT -->
          <p class="cm-teasable__text">
          <#--include imagemap-->
          <@cm.include self=self view="areasQuickInfo" params={"imageMapId": imageMapId, "quickInfoMainId": quickInfoMainId, "quickInfoIdList": quickInfoIdList}/>
          </p>
        </div>
      </div>
    </div>
  </div>
</div>
