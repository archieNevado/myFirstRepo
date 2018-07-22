<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMGallery" -->

<#-- this template is required to override Container.asTeaser.ftl,
     otherwise CMGallery will be automatically flattened -->

<#-- render teaser with default settings -->
<@cm.include self=self view="teaser" params={"additionalClass": "cm-teasable--gallery"} />
