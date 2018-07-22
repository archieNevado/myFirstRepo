<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.webflow.WebflowActionState" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->
<#-- @ftlvariable name="flowExecutionKey" type="java.lang.String" -->
<#-- @ftlvariable name="nextUrl" type="java.lang.String" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/components.ftl" as components />
<#import "../../freemarkerLibs/elastic-social.ftl" as elasticSocial />

<div class="container">
  <div class="row">
    <div class="cm-form cm-form--reset well col-xs-12 col-md-6 col-md-push-3"<@preview.metadata data=[self.action.content!"", "properties.id"]/>>
      <h1 class="cm-form__headline"><@bp.message "passwordReset_title" /></h1>
      <form method="post" data-cm-form--forgot="">
        <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}">
        <input type="hidden" name="execution" value="${flowExecutionKey!""}">
        <input type="hidden" name="nextUrl" value="${nextUrl!""}">
        <input type="hidden" name="_eventId_submit">

        <#-- notification -->
        <@elasticSocial.notificationFromSpring path="bpPasswordReset" additionalClasses=["alert alert-danger"] />

        <#-- email -->
        <div class="form-group">
          <@elasticSocial.labelFromSpring path="bpPasswordReset.emailAddress" text='${bp.getMessage("passwordReset_email_label")}'/>
          <@spring.formInput path="bpPasswordReset.emailAddress" attributes='class="form-control" placeholder="${bp.getMessage("passwordReset_email_label")}" required' fieldType="text"/>
        </div>

        <div class="form-group text-right">
          <@components.button text=bp.getMessage("passwordReset_button") attr={"type": "submit", "classes": ["btn","btn-primary"]} />
        </div>
      </form>
    </div>
  </div>
</div>
