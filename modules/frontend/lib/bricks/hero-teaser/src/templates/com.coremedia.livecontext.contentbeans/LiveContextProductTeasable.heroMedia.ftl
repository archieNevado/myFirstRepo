<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextProductTeasable" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />
<#import "../../freemarkerLibs/heroTeaser.ftl" as heroTeaser />

<@utils.optionalLink href=heroTeaser.getLink(self.productInSite!cm.UNDEFINED, self.teaserSettings)
                     openInNewTab=self.openInNewTab>
  <@cm.include self=self view="_picture" params={
    "blockClass": cm.localParameters().heroBlockClass!"cm-hero",
    "renderDimmer": cm.localParameters().renderDimmer!true,
    "renderEmptyImage": cm.localParameters().renderEmptyImage!true
  }/>
</@utils.optionalLink>
