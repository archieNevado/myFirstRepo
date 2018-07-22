<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/cta.ftl" as cta />
<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />
<#import "*/node_modules/@coremedia/brick-media/src/freemarkerLibs/media.ftl" as mediaLib />

<#assign defaultBlockClass="cm-hero" />

<#macro renderMedia media
                    heroBlockClass=defaultBlockClass
                    additionalClass=""
                    link=""
                    openInNewTab=false
                    limitAspectRatios=[]
                    metadata=[]
                    renderDimmer=true
                    renderEmptyMedia=true>
  <#-- @ftlvariable name="media" type="com.coremedia.blueprint.common.contentbeans.CMMedia" -->

  <#--
   Provide fallback values as long as "cm.localParameter" is used to fill the parameters of the macro.
   Can be removed as soon as this is not supported anymore.

   Important: These values need to be kept in sync with the signature of the macro
   -->
  <#local heroBlockClass=cm.notUndefined(heroBlockClass, defaultBlockClass) />
  <#local additionalClass=cm.notUndefined(additionalClass, "") />
  <#local link=cm.notUndefined(link, "") />
  <#local openInNewTab=cm.notUndefined(openInNewTab, false) />
  <#local limitAspectRatios=cm.notUndefined(limitAspectRatios, []) />
  <#local metadata=cm.notUndefined(metadata, []) />
  <#local renderDimmer=cm.notUndefined(renderDimmer, true) />
  <#local renderEmptyMedia=cm.notUndefined(renderEmptyMedia, true) />

  <@utils.optionalLink href="${link}" openInNewTab=openInNewTab>
    <#if media?has_content && media != cm.UNDEFINED>
      <#-- media -->
      <@cm.include self=media view="media" params={
        "limitAspectRatios": limitAspectRatios,
        "classBox": "${heroBlockClass}__picture-box",
        "classMedia": "${heroBlockClass}__picture",
        "metadata": metadata,
        <#--player settings for video and audio -->
        "hideControls": true,
        "autoplay": true,
        "loop": true,
        "muted": true,
        "preload": true
      }/>
      <#if renderDimmer>
        <div class="${heroBlockClass}__dimmer"></div>
      </#if>
    <#else>
      <#if renderEmptyMedia>
        <div class="${heroBlockClass}__picture-box"<@preview.metadata metadata />>
          <@mediaLib.renderEmptyMedia additionalClass="${heroBlockClass}__picture" />
        </div>
      </#if>
    </#if>
  </@utils.optionalLink>
</#macro>

<#macro renderCaption title=""
                      text=""
                      link=""
                      openInNewTab=false
                      ctaButtons=[]
                      heroBlockClass=defaultBlockClass
                      metadataTitle=[]
                      metadataText=[]>
  <#--
  Provide fallback values as long as "cm.localParameter" is used to fill the parameters of the macro.
  Can be removed as soon as this is not supported anymore.

  Important: These values need to be kept in sync with the signature of the macro
  -->
  <#local link=cm.notUndefined(link, "") />
  <#local openInNewTab=cm.notUndefined(openInNewTab, false) />
  <#local heroBlockClass=cm.notUndefined(heroBlockClass, defaultBlockClass) />
  <#local metadataTitle=cm.notUndefined(metadataTitle, []) />
  <#local metadataText=cm.notUndefined(metadataText, []) />

  <div class="${heroBlockClass}__caption">
    <#-- title -->
    <#if title?has_content>
      <@utils.optionalLink href=link openInNewTab=openInNewTab>
        <h1 class="${heroBlockClass}__headline"<@preview.metadata metadataTitle />>${title}</h1>
      </@utils.optionalLink>
    </#if>

    <#-- teaser text -->
    <#if text?has_content>
      <p class="${heroBlockClass}__text"<@preview.metadata metadataText />>
        ${text}
      </p>
    </#if>

    <#-- cta -->
    <@cta.render buttons=ctaButtons
                 additionalClass="${heroBlockClass}__cta" />
  </div>
</#macro>