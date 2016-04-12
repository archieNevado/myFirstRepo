<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="classVideo" type="java.lang.String" -->
<#-- @ftlvariable name="hideControls" type="java.lang.Boolean" -->
<#-- @ftlvariable name="autoplay" type="java.lang.Boolean" -->
<#-- @ftlvariable name="loop" type="java.lang.Boolean" -->
<#-- @ftlvariable name="muted" type="java.lang.Boolean" -->

<#if self.data?has_content || self.dataUrl?has_content>
  <#assign videoLink = bp.getVideoLink(self) />

  <video
    src="${videoLink}"
    class="cm-video cm-video--html5 ${classVideo!""}"
    poster="" <#-- leave poster empty, first frame will be used as poster -->
    <@cm.unescape hideControls?then('', 'controls="controls"') />
    <@cm.unescape autoplay?then('autoplay="autoplay"','') />
    <@cm.unescape loop?then('loop="loop"','') />
    <@cm.unescape muted?then('muted="muted"','') />
    data-cm-non-adaptive-content='{"overflow": "false"}'
    data-cm-video--html5='{"flash": "${cm.getLink((bp.setting(cmpage, "mediaelementplayer_flash").data)!cm.UNDEFINED)}"}'
    <@cm.metadata "properties.data"/>>
      <@bp.notification type="info" text=bp.getMessage("error.video.tag.not.available") dismissable=true />
  </video>
</#if>
