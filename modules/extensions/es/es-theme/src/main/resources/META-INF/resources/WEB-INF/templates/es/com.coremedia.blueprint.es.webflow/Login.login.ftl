<#import "/spring.ftl" as spring/>
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState" -->
<#-- @ftlvariable name="flowExecutionKey" type="java.lang.String" -->
<#-- @ftlvariable name="nextUrl" type="java.lang.String" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->


<#assign loginAction=self.loginAction!cm.UNDEFINED />

<div class="cm-box"<@cm.metadata data=[loginAction.content!"", "properties.id"] />>
  <h3 class="cm-box__header cm-heading3 cm-heading3--boxed"><@bp.message "login_sign_in" /></h3>


<#assign classContainer=cm.localParameters().classContainer!"" />
<#assign forgotPasswordAction=self.passwordResetAction!cm.UNDEFINED />
<#assign forgotPasswordUrl=cm.getLink(forgotPasswordAction, {"next": nextUrl})/>

  <form class="cm-form form-horizontal" method="post">
    <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}"/>
    <input type="hidden" name="execution" value="${flowExecutionKey!""}"/>
    <input type="hidden" name="nextUrl" value="${nextUrl!""}"/>
    <input type="hidden" name="_eventId_submit"/>

  <@bp.notificationFromSpring path="bpLoginForm" dismissable=false additionalClasses=["cm-form__notification"] />
    <div class="form-group">
    <@spring.bind path="bpLoginForm.name"/>
      <label for="${spring.status.expression?replace('[','')?replace(']','')}"
             class="col-sm-2 control-label cm-form__label">${bp.getMessage("login_name_label")}</label>

      <div class="col-sm-10 cm-form__value">
      <@spring.formInput path="bpLoginForm.name" attributes='class="form-control" placeholder="${bp.getMessage("login_name_label")}"'/>
      </div>
    </div>

    <div class="form-group">
    <@spring.bind path="bpLoginForm.password"/>
      <label for="${spring.status.expression?replace('[','')?replace(']','')}"
             class="col-sm-2 control-label cm-form__label">${bp.getMessage("login_password_label")}</label>

      <div class="col-sm-10 cm-form__value">
      <@spring.formInput path="bpLoginForm.password" fieldType="password" attributes='class="form-control" placeholder="${bp.getMessage("login_password_label")}"'/>
      </div>
    </div>

  <#if forgotPasswordUrl?has_content>
    <div class="cm-fieldset__item cm-field">
      <a href="${forgotPasswordUrl!""}"
         class="cm-field__value--link"><@bp.message es.messageKeys.LOGIN_FORGOT_PASSWORD /></a>
    </div>
  </#if>

    <div class="form-group">
      <div class="col-sm-offset-2 col-sm-10 cm-form__value">
      <@bp.button text=bp.getMessage("login_sign_in") attr={"type": "submit", "classes": ["btn","btn-primary"]} />
      </div>
    </div>
  </form>


<#assign loginAction=self.loginAction!cm.UNDEFINED />
<#assign loginFlow=bp.substitute(loginAction.id!"", loginAction)!cm.UNDEFINED />
<#assign loginUrl=cm.getLink(loginFlow, {
"absolute": true
}) />

<#assign registrationAction=self.registrationAction!cm.UNDEFINED />
<#assign registrationFlow=bp.substitute(registrationAction.id!"", registrationAction)!cm.UNDEFINED />
<#assign registerUrl=cm.getLink(registrationFlow, {
"absolute": true
}) />

<#assign elasticSocialConfiguration=es.getElasticSocialConfiguration(cmpage) />
<#if elasticSocialConfiguration?has_content>
  <#if elasticSocialConfiguration.twitterAuthenticationEnabled || elasticSocialConfiguration.facebookAuthenticationEnabled>



    <h3 class="cm-box__header cm-heading3 cm-heading3--boxed"><@bp.message "userdetails_external_account" /></h3>

    <div class="col-sm-offset-2 col-sm-10 cm-form__value">
      <#assign tenant=es.getCurrentTenant() />
      <#if elasticSocialConfiguration.facebookAuthenticationEnabled>
        <#assign facebookUrl=cm.getLink('/signin/facebook_' + tenant)/>
        <form action="${facebookUrl!""}" method="post" class="cm-form-facebook pull-left">
          <input type="hidden" name="nextUrl" value="${nextUrl!""}"/>
          <input type="hidden" name="registerUrl" value="${loginUrl!""}"/>
          <input type="hidden" name="loginUrl" value="${loginUrl!""}"/>
          <input type="hidden" name="scope" value="email"/>
          <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}"/>
          <input type="hidden" name="forceRegister" value="false"/>
          <@bp.button text=bp.getMessage(es.messageKeys.LOGIN_WITH_FACEBOOK) attr={"type": "submit", "id": "facebookConnect","classes": ["btn", "btn-default", "cm-button-group__button"]} />
        </form>
      </#if>

      <#if elasticSocialConfiguration.twitterAuthenticationEnabled>
        <#assign twitterUrl=cm.getLink('/signin/twitter_' + tenant)/>
        <form action="${twitterUrl!""}" method="post" class="cm-form-twitter  pull-right">
          <input type="hidden" name="nextUrl" value="${nextUrl!""}"/>
          <input type="hidden" name="registerUrl" value="${loginUrl!""}"/>
          <input type="hidden" name="loginUrl" value="${loginUrl!""}"/>
          <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}"/>
          <input type="hidden" name="forceRegister" value="false"/>
          <@bp.button text=bp.getMessage(es.messageKeys.LOGIN_WITH_TWITTER) attr={"type": "submit", "id": "twitterConnect","classes": ["btn", "btn-default", "cm-button-group__button"]} />
        </form>
      </#if>
    </div>
  </#if>
</#if>


</div>