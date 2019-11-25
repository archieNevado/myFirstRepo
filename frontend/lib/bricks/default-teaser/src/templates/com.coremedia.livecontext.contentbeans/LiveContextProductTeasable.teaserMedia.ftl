<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextProductTeasable" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />
<#import "../../freemarkerLibs/defaultTeaser.ftl" as defaultTeaser />

<@utils.optionalLink href=defaultTeaser.getLink(self.productInSite!cm.UNDEFINED, self.teaserSettings)
                     openInNewTab=self.openInNewTab>
  <@cm.include self=self view="_picture" params={
    "blockClass": cm.localParameters().teaserBlockClass!"cm-teasable",
    "renderDimmer": cm.localParameters().renderDimmer!true,
    "renderEmptyImage": cm.localParameters().renderEmptyImage!true
  }/>
</@utils.optionalLink>
