package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMHasContexts;
import com.coremedia.cae.aspect.Aspect;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Generated base class for immutable beans of document type CMHasContexts.
 * Should not be changed.
 */
public abstract class CMHasContextsBase extends CMLinkableImpl implements CMHasContexts {

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link com.coremedia.blueprint.common.contentbeans.CMTeasable} objects
   */
  @Override
  public CMHasContexts getMaster() {
    return (CMHasContexts) super.getMaster();
  }

  @Override
  public Map<Locale, ? extends CMHasContexts> getVariantsByLocale() {
    return getVariantsByLocale(CMHasContexts.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<? extends CMHasContexts> getLocalizations() {
    return (Collection<? extends CMHasContexts>) super.getLocalizations();
  }

  /**
   * @deprecated since 1907.1; Implement optional features as extensions.
   */
  @Deprecated
  @Override
  @SuppressWarnings("unchecked")
  public Map<String, ? extends Aspect<? extends CMHasContexts>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMHasContexts>>) super.getAspectByName();
  }

  /**
   * @deprecated since 1907.1; Implement optional features as extensions.
   */
  @Deprecated
  @Override
  @SuppressWarnings("unchecked")
  public List<? extends Aspect<? extends CMHasContexts>> getAspects() {
    return (List<? extends Aspect<? extends CMHasContexts>>) super.getAspects();
  }

}
