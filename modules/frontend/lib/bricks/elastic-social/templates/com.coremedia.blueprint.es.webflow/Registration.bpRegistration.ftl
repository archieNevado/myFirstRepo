<#import "/spring.ftl" as spring/>
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState" -->
<#-- @ftlvariable name="flowExecutionKey" type="java.lang.String" -->
<#-- @ftlvariable name="nextUrl" type="java.lang.String" -->
<#-- @ftlvariable name="elasticSocialConfiguration" type="com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialConfiguration" -->
<#-- @ftlvariable name="registrationFlow" type="com.coremedia.blueprint.elastic.social.cae.flows.Registration" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->

<#assign termsOfUseLink=cm.getLink(self.disclaimers.linkTermsOfUse!cm.UNDEFINED)/>
<#assign privacyPolicyLink=cm.getLink(self.disclaimers.linkPrivacyPolicy!cm.UNDEFINED)/>
<#assign elasticSocialConfiguration=es.getElasticSocialConfiguration(cmpage)/>
<#assign registrationAction=self.registrationAction!cm.UNDEFINED />
<#assign registrationFlow=bp.substitute(registrationAction.id!"", registrationAction)!cm.UNDEFINED />

<div class="cm-box"<@cm.metadata data=[registrationAction.content!"", "properties.id"]/>>


  <h3 class="cm-box__header cm-heading3 cm-heading3--boxed"><@bp.message "registration_register" /></h3>


  <div class="cm-box__content">

    <form method="post" enctype="multipart/form-data" class="cm-form form-horizontal" data-cm-form--registration="">

      <input type="hidden" name="execution" value="${flowExecutionKey!""}"/>

    <@spring.bind path="bpRegistration.timeZoneId"/>
      <input type="hidden" name="timeZoneId" id="timezone" value="${spring.stringStatusValue}"/>
      <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}"/>
      <input type="hidden" name="_eventId_submit"/>
    <@bp.notificationFromSpring path="bpRegistration" dismissable=false additionalClasses=["cm-form__notification"] />
      <div class="form-group">
      <@spring.bind path="bpRegistration.username"/>
        <label for="${spring.status.expression?replace('[','')?replace(']','')}"
               class="col-sm-2 control-label cm-form__label">${bp.getMessage("registration_username_label")}</label>

        <div class="col-sm-10 cm-form__value">
        <@spring.formInput path="bpRegistration.username" attributes='class="form-control" placeholder="${bp.getMessage("registration_username_label")}"'/>
        </div>
      </div>

      <div class="form-group">
      <@spring.bind path="bpRegistration.givenname"/>
        <label for="${spring.status.expression?replace('[','')?replace(']','')}"
               class="col-sm-2 control-label cm-form__label">${bp.getMessage("registration_givenname_label")}</label>

        <div class="col-sm-10 cm-form__value">
        <@spring.formInput path="bpRegistration.givenname" attributes='class="form-control" placeholder="${bp.getMessage("registration_givenname_label")}"'/>
        </div>
      </div>

      <div class="form-group">
      <@spring.bind path="bpRegistration.surname"/>
        <label for="${spring.status.expression?replace('[','')?replace(']','')}"
               class="col-sm-2 control-label cm-form__label">${bp.getMessage("registration_surname_label")}</label>

        <div class="col-sm-10 cm-form__value">
        <@spring.formInput path="bpRegistration.surname" attributes='class="form-control" placeholder="${bp.getMessage("registration_surname_label")}"'/>
        </div>
      </div>

      <div class="form-group">
      <@spring.bind path="bpRegistration.emailAddress"/>
        <label for="${spring.status.expression?replace('[','')?replace(']','')}"
               class="col-sm-2 control-label cm-form__label">${bp.getMessage("registration_emailAddress_label")}</label>

        <div class="col-sm-10 cm-form__value">
          <div class="input-group">
            <span class="input-group-addon">@</span>
          <@spring.formInput path="bpRegistration.emailAddress" attributes='class="form-control" placeholder="${bp.getMessage("registration_emailAddress_label")}"' fieldType="email"/>
          </div>
        </div>
      </div>




    <#if !(registrationFlow.registeringWithProvider!false)>

      <div class="form-group">
        <@spring.bind path="bpRegistration.password"/>
        <label for="${spring.status.expression?replace('[','')?replace(']','')}"
               class="col-sm-2 control-label cm-form__label">${bp.getMessage("registration_password_label")}</label>

        <div class="col-sm-10 cm-form__value">
          <@spring.formInput path="bpRegistration.password" attributes='class="form-control" placeholder="${bp.getMessage("registration_password_label")}"' fieldType="password"/>
        </div>
      </div>

      <div class="form-group">
        <@spring.bind path="bpRegistration.confirmPassword"/>
        <label for="${spring.status.expression?replace('[','')?replace(']','')}"
               class="col-sm-2 control-label cm-form__label">${bp.getMessage("registration_confirmPassword_label")}</label>

        <div class="col-sm-10 cm-form__value">
          <@spring.formInput path="bpRegistration.confirmPassword" attributes='class="form-control" placeholder="${bp.getMessage("registration_confirmPassword_label")}"' fieldType="password"/>
        </div>
      </div>

    </#if>

      <div class="form-group">
      <@spring.bind "bpRegistration.profileImage"/>
        <label for="imageFile"
               class="col-sm-2 control-label cm-form__label"><@bp.message "registration_imageFile_label" /></label>

        <div class="col-sm-10 cm-form__value">
          <input name="imageFile" type="file" value="" accept="image/*"/>
        </div>
      </div>
    <#if registration?has_content && bpRegistration.profileImage?has_content>
      <#assign imageUrl=cm.getLink(bpRegistration.profileImage)/>
      <div>
        <img src="${imageUrl!""}" alt="userimage"/><br/>
        <@spring.formCheckbox bp.getMessage(es.messageKeys.REGISTRATION_DELETE_PROFILE_IMAGE) />
        <label for="deleteProfileImage"><@bp.message es.messageKeys.REGISTRATION_DELETE_PROFILE_IMAGE /></label>
      </div>
    </#if>
    <#assign privacyPolicy><a href="${privacyPolicyLink!""}"
                              target="_blank"><@bp.message es.messageKeys.REGISTRATION_LINK_PRIVACY_POLICY_LABEL /></a></#assign>
    <#assign termsOfUse><a href="${termsOfUseLink}"
                           target="_blank"><@bp.message es.messageKeys.REGISTRATION_LINK_TERMS_OF_USE_LABEL /></a></#assign>

    <#assign text=bp.getMessage("registration_acceptTermsOfUse_label", ["#privacyPolicy#", "#termsOfUse#"])/>


      <div class="form-group">
      <@spring.bind path="bpRegistration.acceptTermsOfUse"/>
        <div class="col-sm-offset-2 col-sm-10 cm-form__value">
          <div class="checkbox">
            <label>
            <@spring.formCheckbox path="bpRegistration.acceptTermsOfUse"/><@cm.unescape text?replace("#privacyPolicy#", privacyPolicy)?replace("#termsOfUse#", termsOfUse) />
            </label>
          </div>
        </div>
      </div>

    <#if elasticSocialConfiguration.captchaForRegistrationRequired!false>
      <script src="http://www.google.com/recaptcha/api/challenge?k=${elasticSocialConfiguration.captchaPublicKey!""}"></script>
    </#if>
      <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10 cm-form__value">
        <@bp.button text=bp.getMessage("registration_title") attr={"type": "submit", "classes": ["btn","btn-primary"]} />
        </div>
      </div>
    </form>

  </div>
</div>
