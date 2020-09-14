package com.coremedia.blueprint.taxonomies.cycleprevention;

import com.coremedia.blueprint.taxonomies.TaxonomyUtil;
import com.coremedia.cap.common.CapObjectDestroyedException;
import com.coremedia.cap.common.CapSession;
import com.coremedia.cap.common.InvalidPropertyValueException;
import com.coremedia.cap.common.NoSuchPropertyDescriptorException;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.content.NotAuthorizedException;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

class TaxonomyCycleValidatorImpl implements TaxonomyCycleValidator {
  private static final Logger LOG = getLogger(lookup().lookupClass());

  static final String CHILDREN_ATTRIBUTE_IDENTIFIER = "children";

  @Override
  public boolean isCyclic(@NonNull Content tax, ContentType contentType) {
    // Use ConnectionSession to ensure we detect cycles even with restricted
    // permissions.
    CapSession originalSession = tax.getRepository().getConnection().getConnectionSession().activate();
    try {
      return checkCycle(tax, new ArrayList<>(), contentType);
    } finally {
      originalSession.activate();
    }
  }

  private boolean checkCycle(@NonNull Content tax, @NonNull List<? super String> path, ContentType contentType) {
    try {
      if (tax.isDestroyed() || !tax.isInProduction() || !isTaxonomy(tax, contentType)) {
        return false;
      }

      String taxonomyId = tax.getId();

      if (path.contains(taxonomyId)) {
        return true;
      }

      path.add(taxonomyId);
      List<Content> children = tax.getLinks(CHILDREN_ATTRIBUTE_IDENTIFIER);
      if (children.isEmpty()) {
        return false;
      }

      for (Content child : children) {
        if (checkCycle(child, path, contentType)) {
          return true;
        }
      }
    } catch (NoSuchPropertyDescriptorException | InvalidPropertyValueException | NotAuthorizedException | CapObjectDestroyedException e) {
      LOG.warn("Exception while checking cycle in taxonomies. Will assume no cycle. Current object: {}", tax, e);
    }

    return false;
  }

  @VisibleForTesting
  boolean isTaxonomy(@NonNull Content tax, ContentType contentType) {
    return TaxonomyUtil.isTaxonomy(tax, contentType);
  }
}
