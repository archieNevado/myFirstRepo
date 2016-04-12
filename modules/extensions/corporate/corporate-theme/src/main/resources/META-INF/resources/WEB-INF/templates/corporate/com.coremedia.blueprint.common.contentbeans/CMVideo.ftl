<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->

<#assign hasVideo=self.data?has_content || self.dataUrl?has_content />

<article class="cm-details cm-details--video"<@cm.metadata self.content />>

  <#-- title -->
  <h1 class="cm-details__headline"<@cm.metadata "properties.title"/>>${self.title!""}</h1>

  <#-- video or picture -->
  <div class="cm-details__medias">
    <#if hasVideo>
      <div class="cm-details__video-box">
        <@cm.include self=self view="video" params={
          "classVideo": "cm-details__video",
          "hideControls": false,
          "autoplay": false,
          "loop": false,
          "muted": false
        } />
      </div>
    <#elseif self.picture?has_content>
      <div class="alert alert-warning alert-dismissible" role="alert">
        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        ${bp.getMessage("error.video.not.available")}
      </div>
      <@cm.include self=self.picture params={
        "limitAspectRatios": ["landscape_ratio16x9"],
        "classBox": "cm-details__video-box",
        "classImage": "cm-details__video"
      }/>
    </#if>
  </div>

  <#-- text -->
  <#if self.detailText?has_content>
    <div class="cm-details__text cm-richtext"<@cm.metadata "properties.detailText"/>>
      <@cm.include self=self.detailText!cm.UNDEFINED />
    </div>
  </#if>

  <#-- date -->
  <#if self.externallyDisplayedDate?has_content>
    <div class="cm-details__date"<@cm.metadata "properties.externallyDisplayedDate"/>>
      <@bp.renderDate self.externallyDisplayedDate.time "cm-details__time" />
    </div>
  </#if>

  <#-- tags -->
  <@cm.include self=self view="asTagList"/>
</article>

<#-- related -->
<#if self.related?has_content>
<div class="cm-related"<@cm.metadata "properties.related"/>>
  <h2>${bp.getMessage("related.label")}</h2>
  <@cm.include self=bp.getContainer(self.related) view="asMedialist" />
</div>
</#if>

<#-- extensions -->
<@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_END />
