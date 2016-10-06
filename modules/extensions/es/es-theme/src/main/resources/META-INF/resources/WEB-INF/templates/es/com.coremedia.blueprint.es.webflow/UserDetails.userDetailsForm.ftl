<#-- @ftlvariable name="pViewOnly" type="java.lang.String" -->
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.webflow.WebflowActionState" -->
<#-- @ftlvariable name="flowExecutionKey" type="java.lang.String" -->
<#-- @ftlvariable name="userDetails" type="com.coremedia.blueprint.elastic.social.cae.flows.UserDetails" -->
<#-- @ftlvariable name="actionHandler" type="com.coremedia.blueprint.common.contentbeans.CMAction" -->
<#-- @ftlvariable name="elasticSocialConfiguration" type="com.coremedia.blueprint.elastic.social.configuration.ElasticSocialConfiguration" -->
<#-- @ftlvariable name="explicitInterests" type="com.coremedia.blueprint.personalization.forms.PersonalizationForm" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->
<#-- @ftlvariable name="value" type="java.util.TimeZone" -->
<#import "/spring.ftl" as spring>

<#assign elasticSocialConfiguration=es.getElasticSocialConfiguration(cmpage) />
<#assign actionHandler=self.action />

<div class="cm-box"<@cm.metadata data=[(self.action.content)!"", "properties.id"] />>

    <h3 class="cm-box__header cm-heading3 cm-heading3--boxed"><@bp.message es.messageKeys.USER_DETAILS_PERSONAL_DETAILS /></h3>

<#if userDetails?has_content>
    <div class="cm-box__content">

        <form method="post" enctype="multipart/form-data" class="cm-form form-horizontal">
            <input type="hidden" name="_CSRFToken" value="${_CSRFToken}"/>
            <input type="hidden" name="execution" value="${flowExecutionKey}"/>
            <input type="hidden" name="_eventId_saveUser"/>
          <@bp.notificationFromSpring path="userDetails" dismissable=false />

            <div class="form-group">
              <@spring.bind path="userDetails.username"/>
                <label for="${spring.status.expression?replace('[','')?replace(']','')}"
                       class="col-sm-2 control-label cm-form__label">${bp.getMessage("userDetails_username")}</label>

                <div class="col-sm-10 cm-form__value">
                  <@spring.formInput path="userDetails.username" attributes='class="form-control"'/>
                </div>
            </div>

            <fieldset class="cm-form__fieldset cm-fieldset">

              <#if userDetails.viewOwnProfile || userDetails.preview>

                  <div class="form-group">
                    <@spring.bind path="userDetails.givenname"/>
                      <label for="${spring.status.expression?replace('[','')?replace(']','')}"
                             class="col-sm-2 control-label cm-form__label">${bp.getMessage("userDetails_givenname")}</label>

                      <div class="col-sm-10 cm-form__value">
                        <@spring.formInput path="userDetails.givenname" attributes='class="form-control"'/>
                      </div>
                  </div>

                  <div class="form-group">
                    <@spring.bind path="userDetails.surname"/>
                      <label for="${spring.status.expression?replace('[','')?replace(']','')}"
                             class="col-sm-2 control-label cm-form__label">${bp.getMessage("userDetails_surname")}</label>

                      <div class="col-sm-10 cm-form__value">
                        <@spring.formInput path="userDetails.surname" attributes='class="form-control"'/>
                      </div>
                  </div>

                  <div class="form-group">
                    <@spring.bind path="userDetails.emailAddress"/>
                      <label for="${spring.status.expression?replace('[','')?replace(']','')}"
                             class="col-sm-2 control-label cm-form__label">${bp.getMessage("userDetails_emailAddress")}</label>

                      <div class="col-sm-10 cm-form__value">
                          <div class="input-group">
                              <span class="input-group-addon">@</span>
                            <@spring.formInput path="userDetails.emailAddress" attributes='class="form-control"' fieldType="email"/>
                          </div>
                      </div>
                  </div>

                  <div class="form-group">
                    <@spring.bind path="userDetails.receiveCommentReplyEmails"/>
                      <div class="col-sm-offset-2 col-sm-10 cm-form__value">
                          <div class="checkbox">
                              <label>
                                <@spring.formCheckbox path="userDetails.receiveCommentReplyEmails"/>${bp.getMessage("userDetails_receiveCommentReplyEmails")}
                              </label>
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

                  <div class="form-group">
                    <@spring.bind path="userDetails.deleteProfileImage"/>
                      <div class="col-sm-offset-2 col-sm-10 cm-form__value">
                          <div class="checkbox">
                              <label>
                                <@spring.formCheckbox path="userDetails.deleteProfileImage"/>${bp.getMessage("userDetails_deleteProfileImage")}
                              </label>
                          </div>
                      </div>
                  </div>
              </#if>

                <div class="form-group">
                    <div class="col-sm-offset-2 col-sm-10 cm-form__value">
                        <input type="file" accept="image/*" name="imageFile" id="imageFile"/>
                    </div>
                </div>

                <div class="form-group">
                  <@spring.bind path="userDetails.timeZoneId"/>
                    <label for="${spring.status.expression?replace('[','')?replace(']','')}"
                           class="col-sm-2 control-label cm-form__label">${bp.getMessage(es.messageKeys.USER_DETAILS_TIME_ZONE)}</label>

                    <div class="col-sm-10 cm-form__value">
                        <select id="${spring.status.expression?replace('[','')?replace(']','')}"
                                name="${spring.status.expression}" class="form-control">
                          <#list timeZones as value>
                              <option value="${value.ID}"<@spring.checkSelected value.ID/>>${value.ID}</option>
                          </#list>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                  <@spring.bind path="userDetails.localizedLocale"/>
                    <label for="${spring.status.expression?replace('[','')?replace(']','')}"
                           class="col-sm-2 control-label cm-form__label">${bp.getMessage(es.messageKeys.USER_DETAILS_LOCALE_LANGUAGE)}</label>

                    <div class="col-sm-10 cm-form__value">
                        <select id="${spring.status.expression?replace('[','')?replace(']','')}"
                                name="${spring.status.expression}" class="form-control">
                          <#list locales as value>
                              <option value="${value}"<@spring.checkSelected value/>>${value.displayLanguage}</option>
                          </#list>
                        </select>
                    </div>
                </div>

                <div class="cm-fieldset__item cm-field cm-field--detail">
                    <span class="cm-field__value"><@bp.message es.messageKeys.USER_DETAILS_CHANGE_PASSWORD /></span>
                </div>

                <div class="form-group">
                  <@spring.bind path="userDetails.password"/>
                    <label for="${spring.status.expression?replace('[','')?replace(']','')}"
                           class="col-sm-2 control-label cm-form__label">${bp.getMessage("userDetails_password")}</label>

                    <div class="col-sm-10 cm-form__value">
                      <@spring.formInput path="userDetails.password" attributes='class="form-control"' fieldType="password"/>
                    </div>
                </div>

                <div class="form-group">
                  <@spring.bind path="userDetails.newPassword"/>
                    <label for="${spring.status.expression?replace('[','')?replace(']','')}"
                           class="col-sm-2 control-label cm-form__label">${bp.getMessage("userDetails_newPassword")}</label>

                    <div class="col-sm-10 cm-form__value">
                      <@spring.formInput path="userDetails.newPassword" attributes='class="form-control"' fieldType="password"/>
                    </div>
                </div>

                <div class="form-group">
                  <@spring.bind path="userDetails.newPasswordRepeat"/>
                    <label for="${spring.status.expression?replace('[','')?replace(']','')}"
                           class="col-sm-2 control-label cm-form__label">${bp.getMessage("userDetails_newPasswordRepeat")}</label>

                    <div class="col-sm-10 cm-form__value">
                      <@spring.formInput path="userDetails.newPasswordRepeat" attributes='class="form-control"' fieldType="password"/>
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-sm-offset-2 col-sm-10 cm-form__value">
                      <@bp.button text=bp.getMessage("userDetails_saveProfile")   attr={"type": "submit", "id": "saveUser", "classes": ["btn","btn-primary"]} />
                      <@bp.button text=bp.getMessage("userDetails_cancel")        attr={"type": "submit", "id": "cancel", "name": "_eventId_cancel","classes": ["btn", "btn-default"]} />
                        <@bp.button text=bp.getMessage("userDetails_deleteProfile") attr={"type": "submit", "id": "deleteUser", "name": "_eventId_deleteUser", "classes": ["btn","btn-danger","pull-right"]} />
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
<#else>
  <@bp.notification type="error" dismissable=false text=bp.getMessage(es.messageKeys.USER_DETAILS_NO_USER_FOUND) additionalClasses=["cm-box__content"] />
</#if>
</div>