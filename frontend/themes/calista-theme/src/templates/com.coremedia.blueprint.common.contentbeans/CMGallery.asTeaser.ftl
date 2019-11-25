<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMGallery" -->

<#assign isLast=cm.localParameter("islast", false)/>
<@cm.include self=self view="teaser" params={
  "additionalClass": self.teaserOverlaySettings.enabled?then("teaser-overlay-enabled", "cm-teasable--gallery"),
  "isLast": isLast,
  "renderTeaserText": true,
  "renderEmptyImage": false,
  "renderDimmer": false
}/>
