package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.common.layout.PageGrid;
import com.coremedia.blueprint.ecommerce.common.contentbeans.CMAbstractCategory;
import com.coremedia.cae.aspect.Aspect;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface CMExternalChannel extends CMAbstractCategory {

  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMExternalChannel'.
   */
  String NAME = "CMExternalChannel";

  /**
   * Name of the document property 'externalId'.
   *
   * <p>Useful for queries and content level code.
   */
  String EXTERNAL_ID = "externalId";

  /**
   * Name of the 'commerce' struct in localSettings struct.
   */
  String COMMERCE_STRUCT = "commerce";

  /**
   * Name of the 'selectChildren' property in the commerce struct.
   */
  String COMMERCE_SELECT_CHILDREN = "selectChildren";

  /**
   * Name of the 'children' property in the commerce struct.
   */
  String COMMERCE_CHILDREN = "children";

  @Override
  CMExternalChannel getMaster();

  @Override
  Map<Locale, ? extends CMExternalChannel> getVariantsByLocale();

  @Override
  Collection<? extends CMExternalChannel> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMExternalChannel>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMExternalChannel>> getAspects();

  String getExternalId();

  PageGrid getPdpPagegrid();

  boolean isCatalogRoot();

  List<String> getCommerceChildrenIds();

  boolean isCommerceChildrenSelected();

}
