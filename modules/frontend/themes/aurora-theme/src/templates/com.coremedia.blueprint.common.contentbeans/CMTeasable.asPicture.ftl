<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#assign blockClass=cm.localParameters().blockClass!"cm-teaser--megamenu" />
<@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix=blockClass limitAspectRatios=bp.setting(cmpage.navigation, "default_aspect_ratios_for_megamenu_teaser", [])/>