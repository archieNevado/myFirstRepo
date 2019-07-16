<#-- @ftlvariable name="self" type="com.coremedia.livecontext.commercebeans.CategoryInSite" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />

<@utils.optionalLink href="${cm.getLink(self)}">${(self.category.name)!""}</@utils.optionalLink>
