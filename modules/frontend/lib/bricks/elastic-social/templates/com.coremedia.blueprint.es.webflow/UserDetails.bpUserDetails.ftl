<#-- @ftlvariable name="pViewOnly" type="java.lang.String" -->
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.webflow.WebflowActionState" -->
<#-- @ftlvariable name="flowExecutionKey" type="java.lang.String" -->
<#-- @ftlvariable name="userDetails" type="com.coremedia.blueprint.elastic.social.cae.flows.UserDetails" -->
<#-- @ftlvariable name="actionHandler" type="com.coremedia.blueprint.common.contentbeans.CMAction" -->
<#-- @ftlvariable name="elasticSocialConfiguration" type="com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialConfiguration" -->
<#-- @ftlvariable name="explicitInterests" type="com.coremedia.blueprint.personalization.forms.PersonalizationForm" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->
<#import "/spring.ftl" as spring>

<#assign elasticSocialConfiguration=es.getElasticSocialConfiguration(cmpage) />
<#-- content property of cmpage is CMAction -->
<#-- @ftlvariable name="actionContent" type="com.coremedia.blueprint.common.contentbeans.CMAction" -->
<#assign actionContent=cmpage.content />
<#assign itemId=bp.id((actionContent.content.id)!) />

<#assign actionHandler=self.action />

<div class="cm-box"<@cm.metadata data=[(self.action.content)!"", "properties.id"] />>

<#if userDetails?has_content>

  <#if userDetails.viewOwnProfile>
      <h3 class="cm-box__header cm-heading3 cm-heading3--boxed"><@bp.message "userDetails_personalDetails" /></h3>
  </#if>
  <#if !userDetails.viewOwnProfile>
      <h3 class="cm-box__header cm-heading3 cm-heading3--boxed"><@bp.message "userDetails_title" /></h3>
  </#if>

<div class="cm-box__content">
    <div class="cm-form form-horizontal">
      <#if userDetails.viewOwnProfile && userDetails.preModerationChanged>
          <div><@bp.message "userDetails_changesForPreModeration" /></div>
      </#if>

        <div class="form-group">
          <@spring.bind path="userDetails.username"/>
            <label for="${spring.status.expression?replace('[','')?replace(']','')}"
                   class="col-sm-2 control-label cm-form__label">${bp.getMessage("userDetails_username")}</label>

            <div class="col-sm-10 cm-form__value">
              <@spring.formInput path="userDetails.username" attributes='class="form-control" disabled="disabled"'/>
            </div>
        </div>


      <#if userDetails.viewOwnProfile || userDetails.preview>

          <div class="form-group">
            <@spring.bind path="userDetails.givenname"/>
              <label for="${spring.status.expression?replace('[','')?replace(']','')}"
                     class="col-sm-2 control-label cm-form__label">${bp.getMessage("userDetails_givenname")}</label>

              <div class="col-sm-10 cm-form__value">
                <@spring.formInput path="userDetails.givenname" attributes='class="form-control" disabled="disabled"'/>
              </div>
          </div>

          <div class="form-group">
            <@spring.bind path="userDetails.surname"/>
              <label for="${spring.status.expression?replace('[','')?replace(']','')}"
                     class="col-sm-2 control-label cm-form__label">${bp.getMessage("userDetails_surname")}</label>

              <div class="col-sm-10 cm-form__value">
                <@spring.formInput path="userDetails.surname" attributes='class="form-control" disabled="disabled"'/>
              </div>
          </div>

          <div class="form-group">
            <@spring.bind path="userDetails.emailAddress"/>
              <label for="${spring.status.expression?replace('[','')?replace(']','')}"
                     class="col-sm-2 control-label cm-form__label">${bp.getMessage("userDetails_emailAddress")}</label>

              <div class="col-sm-10 cm-form__value">
                  <div class="input-group">
                      <span class="input-group-addon">@</span>
                    <@spring.formInput path="userDetails.emailAddress" attributes='class="form-control" disabled="disabled"' fieldType="email"/>
                  </div>
              </div>
          </div>
      </#if>

      <#if userDetails.profileImage??>

          <div class="form-group">
            <@spring.bind path="userDetails.profileImage"/>
              <label for="${spring.status.expression?replace('[','')?replace(']','')}"
                     class="col-sm-2 control-label cm-form__label">${bp.getMessage("userDetails_profileImage")}</label>

              <div class="col-sm-10 cm-form__value">
                <#assign link=cm.getLink(userDetails.profileImage {"transform":true, "width":elasticSocialConfiguration.userImageWidth!60?int, "height": elasticSocialConfiguration.userImageHeight!60?int})/>
                  <img src="${link}" title="" alt="userimage"/>
              </div>
          </div>
      </#if>

      <#if userDetails.viewOwnProfile>
          <form method="post" enctype="multipart/form-data">
              <input type="hidden" name="execution" value="${flowExecutionKey!""}"/>
              <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}"/>
              <input type="hidden" name="_eventId_editUser"/>

              <div class="form-group">
                  <div class="col-sm-offset-2 col-sm-10 cm-form__value">
                    <@bp.button text=bp.getMessage("userDetails_editProfile") attr={"type": "submit", "id": "saveUser", "classes": ["btn","btn-primary"]} />
                  </div>
              </div>

          </form>
      </#if>

    <#--
    <#if elasticSocialConfiguration?has_content && elasticSocialConfiguration.complainingEnabled && !userDetails.viewOwnProfile
          && (es.isAnonymousUser() || (!es.isAnonymousUser() && !userDetails.viewOwnProfile))>
      <#assign navigationId=bp.id((cmpage.navigation.content.id)!) />
      <@es.complaining id=userDetails.id collection="users"
      value=es.hasComplaintForCurrentUser(userDetails.id, "users")
      itemId=itemId navigationId=navigationId />
    </#if>
    -->
    </div>

  <#if userDetails.viewOwnProfile
  && elasticSocialConfiguration?has_content
  && ((elasticSocialConfiguration.twitterAuthenticationEnabled && !userDetails.connectedWithTwitter)
  || (elasticSocialConfiguration.facebookAuthenticationEnabled && !userDetails.connectedWithFacebook))>
  </div>

      <h3 class="cm-box__header cm-heading3 cm-heading3--boxed"><@bp.message "userdetails_external_account" /></h3>

  <div class="cm-box__content">

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
    <#assign tenant=es.getCurrentTenant() />
      <div class="col-sm-offset-2 col-sm-10 cm-form__value">
        <#if elasticSocialConfiguration.facebookAuthenticationEnabled>
          <#assign facebookUrl=cm.getLink('/signin/facebook_' + tenant)/>
            <form action="${facebookUrl!""}" method="post" class="cm-form-facebook pull-left">
                <input type="hidden" name="nextUrl" value="${nextUrl!""}"/>
                <input type="hidden" name="registerUrl" value="${loginUrl!""}"/>
                <input type="hidden" name="loginUrl" value="${loginUrl!""}"/>
                <input type="hidden" name="scope" value="email"/>
                <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}"/>
                <input type="hidden" name="forceRegister" value="false"/>
              <@bp.button text=bp.getMessage("login_with_facebook") attr={"type": "submit", "id": "facebookConnect","classes": ["btn", "btn-default"]} />
            </form>
        </#if>

        <#if elasticSocialConfiguration.twitterAuthenticationEnabled>
          <#assign twitterUrl=cm.getLink('/signin/twitter_' + tenant)/>
            <form action="${twitterUrl!""}" method="post" class="cm-form-twitter pull-right">
                <input type="hidden" name="nextUrl" value="${nextUrl!""}"/>
                <input type="hidden" name="registerUrl" value="${loginUrl!""}"/>
                <input type="hidden" name="loginUrl" value="${loginUrl!""}"/>
                <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}"/>
                <input type="hidden" name="forceRegister" value="false"/>
              <@bp.button text=bp.getMessage("login_with_twitter") attr={"type": "submit", "id": "twitterConnect","classes": ["btn", "btn-default"]} />
            </form>
        </#if>
      </div>
  </#if>


    <h3 class="cm-heading3 cm-heading3--boxed"><@bp.message "userDetails_logging"/></h3>

    <div class="cm-form form-horizontal">

        <div class="form-group">
            <label id="lastLoginDate"
                   class="col-sm-2 control-label cm-form__label">${bp.getMessage("userDetails_lastLoginDate")}</label>

            <div class="col-sm-10 cm-form__value">
                <span class="help-block">
                  <#if userDetails.lastLoginDate?has_content>
                ${userDetails.lastLoginDate!?datetime?string.long_full}
                </#if>
                </span>
            </div>
        </div>
        <div class="form-group">
            <label id="registrationDate"
                   class="col-sm-2 control-label cm-form__label">${bp.getMessage("userDetails_registrationDate")}</label>

            <div class="col-sm-10 cm-form__value">
                <span class="help-block">${userDetails.registrationDate!?datetime?string.long_full}</span>
            </div>
        </div>
        <div class="form-group">
            <label id="numberOfLogins"
                   class="col-sm-2 control-label cm-form__label">${bp.getMessage("userDetails_numberOfLogins")}</label>

            <div class="col-sm-10 cm-form__value">
                <span class="help-block">${userDetails.numberOfLogins}</span>
            </div>
        </div>
        <div class="form-group">
            <label id="numberOfComments"
                   class="col-sm-2 control-label cm-form__label">${bp.getMessage("userDetails_numberOfComments")}</label>

            <div class="col-sm-10 cm-form__value">
                <span class="help-block">${userDetails.numberOfComments}</span>
            </div>
        </div>
        <div class="form-group">
            <label id="numberOfRatings"
                   class="col-sm-2 control-label cm-form__label">${bp.getMessage("userDetails_numberOfRatings")}</label>

            <div class="col-sm-10 cm-form__value">
                <span class="help-block">${userDetails.numberOfRatings}</span>
            </div>
        </div>
        <div class="form-group">
            <label id="numberOfLikes"
                   class="col-sm-2 control-label cm-form__label">${bp.getMessage("userDetails_numberOfLikes")}</label>

            <div class="col-sm-10 cm-form__value">
                <span class="help-block">${userDetails.numberOfLikes}</span>
            </div>
        </div>
    </div>
<#--#if userDetails.viewOwnProfile>
  <@cm.include self=self view="showPersonalizationForm"/>
</#if-->
</div>
<#else>
  <@bp.notification type="error" dismissable=false text=bp.getMessage("userDetails_noUserFound") additionalClasses=["cm-box__content"] />
</#if>
</div>