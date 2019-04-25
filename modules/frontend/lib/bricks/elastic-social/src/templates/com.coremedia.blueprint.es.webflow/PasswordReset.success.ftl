<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.webflow.WebflowActionState" -->
<#-- @ftlvariable name="emailAddress" type="java.lang.String" -->

<#import "../../freemarkerLibs/elastic-social.ftl" as elasticSocial />

<div class="container">
  <div class="row">
    <div class="cm-form cm-form--success col-xs-12 col-md-8 col-md-push-2"<@preview.metadata data=[self.action.content!"", "properties.id"]/>>
      <@elasticSocial.notification type="success" title=cm.getMessage("passwordReset_title") text=cm.getMessage("passwordReset_success", [emailAddress!""]) additionalClasses=["alert alert-success"] />
    </div>
  </div>
</div>
