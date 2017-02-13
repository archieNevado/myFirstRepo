<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState" -->
<#-- @ftlvariable name="profileAction" type="com.coremedia.blueprint.common.contentbeans.CMAction" -->
<#-- @ftlvariable name="loginLink" type="java.lang.String" -->
<#-- @ftlvariable name="logoutLink" type="java.lang.String" -->

<#assign elasticSocialSettings=bp.setting(cmpage.navigation "elasticSocial" {})/>
<#if elasticSocialSettings?has_content && elasticSocialSettings.enabled!false>
  <#assign loginAction=self.loginAction />
  <#assign logoutAction=self.logoutAction />
  <#assign profileAction=self.profileAction />

  <#if loginAction?has_content && logoutAction?has_content && profileAction?has_content>
    <#assign logoutLink=cm.getLink(logoutAction)/>
    <#if self.authenticated>
    <div class="cm-placeholder cm-user-chooser" <@cm.metadata data=[logoutAction.content, "properties.id"] />>
      <a class="cm-user-chooser__button" title="${self.user.givenName}" data-toggle="collapse"
         data-target=".cm-user-chooser__items">
        <#if self.user.image?has_content>
          <#assign elasticSocialSettings=bp.setting(cmpage, "elasticSocial")/>
          <#assign link=cm.getLink(self.user.image {"transform":true, "width":elasticSocialSettings.userImageThumbnailWidth!60?int, "height": elasticSocialSettings.userImageCommentThumbnailHeight!60?int})/>
          <img src="${link}" class="img-rounded cm-user-chooser__profileimage"/>
        </#if>
        <span class="cm-user-chooser__givenname">${self.user.givenName!""}</span>
        <span class="cm-user-chooser__surname">${self.user.surName!""}</span>
        <span class="cm-user-chooser__icon glyphicon glyphicon-triangle-bottom" aria-hidden="true"></span>
      </a>
      <ul class="cm-user-chooser__items collapse">
        <li class="cm-user-chooser__item">
          <a href="${logoutLink}"
             title="<@bp.message key=es.messageKeys.LOGOUT_TITLE highlightErrors=false />" <@cm.metadata data=[logoutAction.content, "properties.id"] />>
            <span class="cm-icon__info"><@bp.message es.messageKeys.LOGOUT_TITLE /></span>
          </a>
        </li>
        <li class="cm-user-chooser__item">
          <a href="${cm.getLink(profileAction)}"
             title="<@bp.message key=es.messageKeys.USER_DETAILS_TITLE highlightErrors=false />" <@cm.metadata data=[profileAction.content, "properties.id"] />>
            <span class="cm-icon__info"><@bp.message es.messageKeys.USER_DETAILS_TITLE /></span>
          </a>
        </li>
      </ul>
    </div>
    <#else>
    <div class="cm-placeholder cm-user-chooser" <@cm.metadata data=[loginAction.content, "properties.id"] />>
      <a class="cm-user-chooser__button" title="<@bp.message key=es.messageKeys.LOGIN_TITLE highlightErrors=false/>" data-toggle="collapse"
         data-target=".cm-user-chooser__items">
        <@bp.message es.messageKeys.LOGIN_TITLE />
        <span class="cm-user-chooser__icon glyphicon glyphicon-triangle-bottom" aria-hidden="true"></span>
      </a>
      <ul class="cm-user-chooser__items collapse">
        <li class="cm-user-chooser__item">
          <#assign loginLink=cm.getLink(self, {"next": "$nextUrl$", "absolute": true, "scheme": "https"})/>
          <a data-href="${loginLink}"
             title="Sign in" <@cm.metadata data=[loginAction.content, "properties.id"] />>
          ${self.action.teaserTitle}
          </a>
        </li>
        <#assign registrationAction=self.registrationAction!cm.UNDEFINED />
        <#assign registrationFlow=bp.substitute(registrationAction.id!"", registrationAction)!cm.UNDEFINED />

        <#if registrationFlow?has_content>

          <li class="cm-user-chooser__item">
            <#assign registerLink=cm.getLink(registrationFlow, {"next": "$nextUrl$", "absolute": true, "scheme": "https"})/>
            <a data-href="${registerLink}"
               title="<@bp.message key=es.messageKeys.LOGIN_SIGN_UP highlightErrors=false />" <@cm.metadata data=[loginAction.content, "properties.id"] />>
              <@bp.message es.messageKeys.LOGIN_SIGN_UP />
            </a>
          </li>
        </#if>
      </ul>
    </div>
    </#if>
  </#if>
</#if>