<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMImageMap" -->

<#assign limitAspectRatios=bp.setting(cmpage.navigation, "default_aspect_ratios_for_teaser", [])/>
<#assign additionalClass=cm.localParameters().additionalClass!"cm-teasable" />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />
<#assign limitAspectRatios=cm.localParameter("limitAspectRatios", []) />
<#assign imageMapId=cm.localParameter("imageMapId", "") />
<#assign quickInfoMainId=cm.localParameter("quickInfoMainId", "") />
<#assign quickInfoIdList=cm.localParameter("quickInfoIdList", "") />
<#assign useQuickinfo=cm.localParameter("useQuickinfo", true) />

<#if self.picture?has_content>
<div class="cm-imagemap__wrapper">
    <a class="cm-imagemap__link">
    <#-- include image -->
          <@cm.include self=self.picture params={
    "limitAspectRatios": limitAspectRatios,
    "classBox": "${additionalClass}__picture-box",
    "classImage":  "${additionalClass}__picture cm-imagemap__image cm-notselectable",
    "metadata": ["properties.pictures"],
    "additionalAttr": {"useMap": "#" + imageMapId!"", "unselectable": "on"}
    }/>
    </a>
  <#-- include imagemap -->
  <@cm.include self=self view="_areasMap" params={"imageMapId": imageMapId, "quickInfoMainId": quickInfoMainId, "quickInfoIdList": quickInfoIdList, "useQuickinfo": useQuickinfo}/>
</div>
<#if renderDimmer>
  <div class="${additionalClass}__dimmer"></div>
</#if>
<#elseif renderEmptyImage>
<div class="${additionalClass}__picture-box" <@cm.metadata "properties.pictures" />>
    <div class="${additionalClass}__picture"></div>
</div>
</#if>