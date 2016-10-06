package com.coremedia.livecontext.elastic.social.cae;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMAction;
import com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState;
import com.coremedia.blueprint.elastic.social.cae.action.RegistrationDisclaimers;
import com.coremedia.objectserver.view.substitution.Substitution;
import org.springframework.beans.factory.annotation.Required;

public class LiveContextSubstitutions {

  private SettingsService settingsService;

  public static final String LOGIN_ACTION_ID = "com.coremedia.blueprint.elastic.social.cae.flows.Login";
  public static final String PROFILE_ACTION_ID = "com.coremedia.blueprint.elastic.social.cae.flows.UserDetails";
  public static final String REGISTRATION_ACTION_ID = "com.coremedia.blueprint.elastic.social.cae.flows.Registration";

  @Substitution(LOGIN_ACTION_ID)
  public AuthenticationState createLoginActionStateBeanForLiveContext(CMAction action) {
    RegistrationDisclaimers disclaimers = settingsService.createProxy(RegistrationDisclaimers.class, action);
    return new AuthenticationState(action, null, AuthenticationState.class.getName(), null, disclaimers, settingsService);
  }


  /**
   * Creates a bean that represents the authentication state
   */
  @Substitution(PROFILE_ACTION_ID)
  public Object createProfileActionStateBeanForLiveContext(CMAction action) {
    // it's the same bean than for the login action. In fact the profile CMAction holds the logout button and  only
    // a link pointing to the profile. Thus, "login" and "profile" action might be merged.
    return createLoginActionStateBeanForLiveContext(action);
  }

  /**
   * Creates a bean that represents the registration state
   */
  @Substitution(REGISTRATION_ACTION_ID)
  public Object createRegistrationActionStateBeanForLiveContext(CMAction action) {
    return createLoginActionStateBeanForLiveContext(action);
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

}
