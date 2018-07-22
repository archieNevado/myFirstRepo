<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextExternalChannel" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />

<@utils.optionalLink href=(cm.localParameters().renderLink!true)?then(cm.getLink(self.target!cm.UNDEFINED), "")
                     openInNewTab=self.openInNewTab>
  <@cm.include self=self view="_picture" params={
    "blockClass": cm.localParameters().heroBlockClass!"cm-hero",
    "renderDimmer": cm.localParameters().renderDimmer!true,
    "renderEmptyImage": cm.localParameters().renderEmptyImage!true
  }/>
</@utils.optionalLink>
