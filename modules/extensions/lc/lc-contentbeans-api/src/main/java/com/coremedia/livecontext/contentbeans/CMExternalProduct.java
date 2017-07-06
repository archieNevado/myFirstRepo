package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cap.struct.Struct;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @cm.template.api
 */
public interface CMExternalProduct extends CMTeasable {

  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMExternalProduct'.
   */
  String NAME = "CMExternalProduct";

  /**
   * Name of the document property 'externalId'.
   * <p>
   * <p>Useful for queries and content level code.
   */
  String EXTERNAL_ID = "externalId";

  /**
   * The name of the pagegrid property
   */
  String PAGEGRID = "pdpPagegrid";

  @Override
  CMExternalProduct getMaster();

  @Override
  Map<Locale, ? extends CMExternalProduct> getVariantsByLocale();

  @Override
  Collection<? extends CMExternalProduct> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMExternalProduct>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMExternalProduct>> getAspects();

  /**
   * Returns the external ID.
   *
   * @return the external ID.
   */
  String getExternalId();

  /**
   * Returns the value of the document property {@link #PAGEGRID}.
   *
   * @return the value of the document property {@link #PAGEGRID}
   */
  Struct getPagegridStruct();
}
