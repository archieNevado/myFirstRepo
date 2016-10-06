<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMImageMap" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->
<#-- @ftlvariable name="quickInfoModal" type="java.lang.Boolean" -->
<#-- @ftlvariable name="quickInfoGroup" type="java.lang.String" -->

<#assign index=cm.localParameters().index!0 />
<#assign hasImage=self.picture?has_content />
<#assign hasEvenIndex=(index % 2 == 0) />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign imageMapParams=bp.initializeImageMap(quickInfoModal, quickInfoGroup)/>


<div class="cm-imagemap cm-teaser cm-teaser--hero"
     data-cm-imagemap='{"coordsBaseWidth": "${bp.IMAGE_TRANSFORMATION_BASE_WIDTH}", "defaultLink": "${cm.getLink(self.target!cm.UNDEFINED)}"}'<@cm.metadata (metadata![]) + [self.content] />>

    <div class="cm-teasable ${hasEvenIndex ? then('', 'cm-teasable--alternative')} ${hasImage ? then('', 'cm-teasable--no-image')} row ${additionalClass!""}"<@cm.metadata self.content />>
    <#if hasImage>
        <div class="col-xs-12 col-sm-6 ${hasEvenIndex ? then('col-sm-push-6', '')}">
          <@cm.include self=self view="_picture" params={
          "renderDimmer": false,
          "renderEmptyImage": false,
          "limitAspectRatios": bp.setting(cmpage.navigation, "default_aspect_ratios_for_hero_teaser", []) } +
          imageMapParams/>
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
          <@cm.include self=self view="_areasQuickInfo" params=imageMapParams/>
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>
