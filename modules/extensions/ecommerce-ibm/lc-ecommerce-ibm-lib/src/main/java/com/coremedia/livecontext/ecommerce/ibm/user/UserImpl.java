package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.user.User;

import java.util.Map;

import static com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper.getValueForKey;

public class UserImpl extends AbstractIbmCommerceBean implements User {

  private Map<String, Object> delegate;

  public Map<String, Object> getDelegate() {
    return delegate;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void setDelegate(Object delegate) {
    this.delegate = (Map<String, Object>) delegate;
  }

  @Override
  public String getFirstName() {
    return getValueForKey(getDelegate(), "firstName", String.class);
  }

  @Override
  public String getLastName() {
    return getValueForKey(getDelegate(), "lastName", String.class);
  }

  @Override
  public String getLogonId() {
    return getValueForKey(getDelegate(), "logonId", String.class);
  }

  @Override
  public String getUserId() {
    return getValueForKey(getDelegate(), "userId", String.class);
  }

  @Override
  public String getExternalId() {
    return getValueForKey(getDelegate(), "logonId", String.class);
  }

  @Override
  public String getExternalTechId() {
    return getExternalId();
  }

  @Override
  public String getEmail1() {
    return getValueForKey(getDelegate(), "email1", String.class);
  }

  @Override
  public String getEmail2() {
    return getValueForKey(getDelegate(), "email2", String.class);
  }

  @Override
  public String getEmail3() {
    return getValueForKey(getDelegate(), "email3", String.class);
  }

  @Override
  public String getCity() {
    return getValueForKey(getDelegate(), "city", String.class);
  }

  @Override
  public String getCountry() {
    return getValueForKey(getDelegate(), "country", String.class);
  }

  @Override
  public String getLogonPassword() {
    return getValueForKey(getDelegate(), "logonPassword", String.class);
  }

  @Override
  public String getLogonPasswordVerify() {
    return getValueForKey(getDelegate(), "logonPasswordVerify", String.class);
  }

  @Override
  public String getChallengeAnswer() {
    return getValueForKey(getDelegate(), "challengeAnswer", String.class);
  }

  @Override
  public String getChallengeQuestion() {
    return getValueForKey(getDelegate(), "challengeQuestion", String.class);
  }
}
