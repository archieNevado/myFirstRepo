package com.coremedia.livecontext.p13n.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.personalization.contentbeans.CMUserProfile;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.preview.TestContextExtractor;
import com.google.common.collect.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Extracts commerce contractIds from cmUserProfile and enriches the the StoreContext.
 * The p13n ContextCollection is not enriched since there is no CoreMedia p13n feature based on contracts implemented yet.
 * The contractIds of test personas are extracted for contract based perview of b2b shop pages in studio.
 */
public class CommerceContractIdTestContextExtractor implements TestContextExtractor {
  private static final Logger LOG = LoggerFactory.getLogger(CommerceContractIdTestContextExtractor.class);

  private ContentBeanFactory contentBeanFactory;

  static final String PROPERTIES_PREFIX = "properties";
  static String COMMERCE_CONTEXT = "commerce";
  static String USER_CONTRACT_PROPERTY = "usercontracts";

  private static String CONTRACT_PROPERTY_PATH = CMUserProfile.PROFILE_EXTENSIONS + "[" + PROPERTIES_PREFIX + "][" + COMMERCE_CONTEXT + "][" + USER_CONTRACT_PROPERTY + "]";

  @Override
  public void extractTestContextsFromContent(Content content, ContextCollection contextCollection) {
    if (content == null) {
      LOG.debug("supplied content is null; cannot extract any contexts");
      return;
    }

    ContentBean cmUserProfileBean = contentBeanFactory.createBeanFor(content);
    if (!(cmUserProfileBean instanceof CMUserProfile)) {
      LOG.debug("cannot extract context from contentbean of type {}", cmUserProfileBean.getClass().toString());
      return;
    }

    Object contractIds = getProperty((CMUserProfile) cmUserProfileBean, CONTRACT_PROPERTY_PATH);

    if (contractIds instanceof List) {
      List contractList = (List) contractIds;
      if (!contractList.isEmpty()) {
        addContractIdsForPreviewToStoreContext(contractList);
      }
    }
  }

  private void addContractIdsForPreviewToStoreContext(List<String> contractList) {
    CommerceConnection currentConnection = CurrentCommerceConnection.find().orElse(null);

    StoreContext storeContext = currentConnection != null ? currentConnection.getStoreContext() : null;

    if (storeContext == null) {
      LOG.debug("Store context is null.");
      return;
    }

    List<String> contractIds = toExternalIds(contractList);

    StoreContext newStoreContext = currentConnection.getStoreContextProvider()
            .buildContext(storeContext)
            .withContractIdsForPreview(contractIds)
            .build();

    currentConnection.setStoreContext(newStoreContext);
  }

  private static List<String> toExternalIds(List<String> contractList) {
    return contractList.stream()
            .map(CommerceIdParserHelper::parseCommerceId)
            .map(commerceId -> commerceId.flatMap(CommerceId::getExternalId))
            .flatMap(Streams::stream)
            .collect(toList());
  }

  private Object getProperty(CMUserProfile userProfile, String propertyPath) {
    try {
      return PropertyAccessorFactory.forBeanPropertyAccess(userProfile).getPropertyValue(propertyPath);
    } catch (InvalidPropertyException | PropertyAccessException ex) {
      return null;
    }
  }

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

}
