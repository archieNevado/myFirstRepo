<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#assign additionalClass=cm.localParameters().additionalClass!"cm-teaser--megamenu" />
<@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix=additionalClass limitAspectRatios=bp.setting(cmpage.navigation, "default_aspect_ratios_for_megamenu_teaser", [])/>