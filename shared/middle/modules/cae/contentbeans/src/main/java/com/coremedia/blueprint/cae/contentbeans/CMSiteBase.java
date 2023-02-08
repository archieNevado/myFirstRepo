package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CMSite;
import com.coremedia.cae.aspect.Aspect;

import java.util.List;
import java.util.Map;

/**
 * Generated base class for immutable beans of document type CMSite.
 * Should not be changed.
 */
public abstract class CMSiteBase extends CMLocalizedImpl implements CMSite {

  /**
   * @deprecated since 1907.1; Implement optional features as extensions.
   */
  @Deprecated
  @Override
  @SuppressWarnings("unchecked")
  public Map<String, ? extends Aspect<? extends CMSite>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMSite>>) super.getAspectByName();
  }

  /**
   * @deprecated since 1907.1; Implement optional features as extensions.
   */
  @Deprecated
  @Override
  @SuppressWarnings("unchecked")
  public List<? extends Aspect<? extends CMSite>> getAspects() {
    return (List<? extends Aspect<? extends CMSite>>) super.getAspects();
  }

  /**
   * Returns the value of the document property {@link #ROOT}.
   *
   * @return the value of the document property {@link #ROOT}
   */
  @Override
  public CMNavigation getRoot() {
    return createBeanFor(getContent().getLink(ROOT), CMNavigation.class);
  }

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link CMSite} objects
   */
  @Override
  public CMSite getMaster() {
    return (CMSite) super.getMaster();
  }

  /**
   * Returns the value of the document property {@link #ID}.
   *
   * @return the value of the document property {@link #ID}
   */
  @Override
  public String getId() {
    return getContent().getString(ID);
  }
}
