<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState" -->
<#-- @ftlvariable name="flowExecutionKey" type="java.lang.String" -->
<#-- @ftlvariable name="nextUrl" type="java.lang.String" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/components.ftl" as components />
<#import "../../freemarkerLibs/elastic-social.ftl" as elasticSocial />

<#assign elasticSocialConfiguration=es.getElasticSocialConfiguration(cmpage) />
<#assign loginAction=self.loginAction!cm.UNDEFINED />
<#assign loginFlow=cm.substitute(loginAction.id!"", loginAction) />
<#assign classContainer=cm.localParameters().classContainer!"" />
<#assign forgotPasswordAction=self.passwordResetAction!cm.UNDEFINED />
<#assign forgotPasswordUrl=cm.getLink(forgotPasswordAction, {"next": nextUrl})/>
<#assign registrationAction=self.registrationAction!cm.UNDEFINED />
<#assign registrationFlow=cm.substitute(registrationAction.id!"", registrationAction) />
<#assign registerLink=cm.getLink(registrationFlow, {"next": nextUrl, "absolute": true, "scheme": "https"})/>

<#if elasticSocialConfiguration?has_content>
<div class="container">
  <div class="row">
    <div class="cm-form cm-form--login well col-xs-12 col-md-6 col-md-push-3"<@preview.metadata data=[loginAction.content!"", "properties.id"] />>
      <#-- login -->
      <h1 class="cm-form__headline"><@bp.message "login_sign_in" /></h1>
      <form method="post">
        <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}">
        <input type="hidden" name="execution" value="${flowExecutionKey!""}">
        <input type="hidden" name="nextUrl" value="${nextUrl!""}">
        <input type="hidden" name="_eventId_submit">

        <#-- notification -->
        <@elasticSocial.notificationFromSpring path="bpLoginForm" additionalClasses=["alert alert-danger"] />

        <#-- Login Name -->
        <div class="form-group">
          <@elasticSocial.labelFromSpring path="bpLoginForm.name" text='${bp.getMessage("login_name_label")}'/>
          <@spring.formInput path="bpLoginForm.name" attributes='class="form-control" placeholder="${bp.getMessage("login_name_label")}" required'/>
        </div>

        <#-- Password -->
        <div class="form-group">
          <@elasticSocial.labelFromSpring path="bpLoginForm.password" text='${bp.getMessage("login_password_label")}'/>
          <@spring.formInput path="bpLoginForm.password" fieldType="password" attributes='class="form-control" placeholder="${bp.getMessage("login_password_label")}" required'/>
        </div>

        <#-- Forgot Password Link -->
        <#if forgotPasswordUrl?has_content>
          <div class="form-group">
            <a href="${forgotPasswordUrl!""}"
               class="cm-form__link"><@bp.message "login_forgot_password" /></a>
          </div>
        </#if>

        <#-- Send Button -->
        <div class="form-group">
          <@components.button text=bp.getMessage("login_sign_in") attr={"type": "submit", "classes": ["btn","btn-primary"]} />
        </div>
      </form>

      <#-- twitter /facebook login-->
      <#if elasticSocialConfiguration.twitterAuthenticationEnabled || elasticSocialConfiguration.facebookAuthenticationEnabled>
        <#assign loginUrl=cm.getLink(loginFlow, {
          "absolute": true
        }) />
        <#assign registerUrl=cm.getLink(registrationFlow, {
          "absolute": true
        }) />
        <div class="cm-form__container">
          <h3><@bp.message "userdetails_external_account" /></h3>
          <#assign tenant=es.getCurrentTenant() />
          <#if elasticSocialConfiguration.facebookAuthenticationEnabled>
            <#assign facebookUrl=cm.getLink('/signin/facebook_' + tenant)/>
            <form action="${facebookUrl!""}" method="post" class="cm-form-facebook pull-left">
              <input type="hidden" name="nextUrl" value="${nextUrl!""}">
              <input type="hidden" name="registerUrl" value="${loginUrl!""}">
              <input type="hidden" name="loginUrl" value="${loginUrl!""}">
              <input type="hidden" name="scope" value="email">
              <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}">
              <input type="hidden" name="forceRegister" value="false">
              <@components.button text=bp.getMessage("login_with_facebook") attr={"type": "submit", "id": "facebookConnect","classes": ["btn", "btn-default", "cm-button-group__button"]} />
            </form>
          </#if>

          <#if elasticSocialConfiguration.twitterAuthenticationEnabled>
            <#assign twitterUrl=cm.getLink('/signin/twitter_' + tenant)/>
            <form action="${twitterUrl!""}" method="post" class="cm-form-twitter  pull-right">
              <input type="hidden" name="nextUrl" value="${nextUrl!""}">
              <input type="hidden" name="registerUrl" value="${loginUrl!""}">
              <input type="hidden" name="loginUrl" value="${loginUrl!""}">
              <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}">
              <input type="hidden" name="forceRegister" value="false">
              <@components.button text=bp.getMessage("login_with_twitter") attr={"type": "submit", "id": "twitterConnect","classes": ["btn", "btn-default", "cm-button-group__button"]} />
            </form>
          </#if>
        </div>
      </#if>

      <div class="cm-form__container">
        <h3><@bp.message "login_create_account" /></h3>
        <a href="${registerLink}" class="btn cm-button cm-button--secondary">
          <@bp.message "login_sign_up_button" />
        </a>
      </div>
    </div>
  </div>
</div>
</#if>
