package com.coremedia.lc.studio.lib.validators;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.ContentSiteAspect;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.workspace.Workspace;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceService;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.newStoreContext;
import static com.coremedia.rest.validation.Severity.ERROR;
import static com.coremedia.rest.validation.Severity.WARN;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Checks if catalog object can be loaded from catalog link property.
 * see also CatalogLink.as and CatalogLinkPropertyField.as
 */
public class CatalogLinkValidator extends ContentTypeValidatorBase {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogLinkValidator.class);

  private static final String CODE_ISSUE_ID_EMPTY = "EmptyExternalId";
  private static final String CODE_ISSUE_ID_INVALID = "InvalidId";
  private static final String CODE_ISSUE_ID_VALID_ONLY_IN_A_WORKSPACE = "ValidInAWorkspace";
  private static final String CODE_ISSUE_CATALOG_ERROR = "catalogError";
  private static final String CODE_ISSUE_CONTEXT_INVALID = "InvalidStoreContext";
  private static final String CODE_ISSUE_CONTEXT_NOT_FOUND = "StoreContextNotFound";

  private SitesService sitesService;
  private CommerceConnectionInitializer commerceConnectionInitializer;

  private String propertyName;

  /**
   * A convinient method to add an issue. The given code will be prefixed with
   * the name of the document type and '_'
   *
   * @param issues    the given issues
   * @param severity  the severity of this issue
   * @param code      a code identifying the type of issue. This will be prefixed.
   * @param arguments optional argument describing the issue, for example indicating a illegally linked object
   */
  protected void addIssue(Issues issues, Severity severity, String code, Object... arguments) {
    issues.addIssue(severity, getPropertyName(), getContentType() + '_' + code, arguments);
  }

  protected void emptyPropertyValue(@Nonnull Content content, @Nonnull Issues issues) {
    addIssue(issues, ERROR, CODE_ISSUE_ID_EMPTY);
  }

  protected void invalidStoreContext(Issues issues, Object... arguments) {
    addIssue(issues, WARN, CODE_ISSUE_CONTEXT_INVALID, arguments);
  }

  protected void storeContextNotFound(Issues issues, Object... arguments) {
    addIssue(issues, WARN, CODE_ISSUE_CONTEXT_NOT_FOUND, arguments);
  }

  protected void invalidExternalId(Issues issues, Object... arguments) {
    addIssue(issues, WARN, CODE_ISSUE_ID_INVALID, arguments);
  }

  protected void validOnlyInWorkspace(Issues issues, Object... arguments) {
    addIssue(issues, WARN, CODE_ISSUE_ID_VALID_ONLY_IN_A_WORKSPACE, arguments);
  }

  protected void catalogNotAvailable(Issues issues, String propertyValue) {
    issues.addIssue(WARN, propertyName, CODE_ISSUE_CATALOG_ERROR, propertyValue);
  }

  @Override
  public void validate(Content content, Issues issues) {
    if (content == null || !content.isInProduction()) {
      return;
    }

    String propertyValue = content.getString(propertyName);

    if (isBlank(propertyValue)) {
      emptyPropertyValue(content, issues);
      return;
    }

    Site site = getSite(content);

    if (site == null) {
      LOG.debug("The content {} belongs to no site; nothing to do.", content);
      return;
    }

    CommerceConnection commerceConnection;
    try {
      commerceConnection = commerceConnectionInitializer.getCommerceConnectionForSite(site);
    } catch (CommerceException ignored) {
      LOG.debug("StoreContext not found for content: {}", content.getPath());
      storeContextNotFound(issues, propertyValue);
      return;
    }

    StoreContext storeContext = commerceConnection.getStoreContext();

    try {
      CommerceBeanFactory commerceBeanFactory = commerceConnection.getCommerceBeanFactory();

      // clear the workspace id before validating
      StoreContext storeContextWithoutWorkspaceId = cloneStoreContextWithWorkspaceId(storeContext, null);
      boolean commerceBeanWithoutWorkspaceExists = hasCommerceBean(commerceBeanFactory, propertyValue,
              storeContextWithoutWorkspaceId);
      if (commerceBeanWithoutWorkspaceExists) {
        // catalog bean is found in the main catalog
        return;
      }

      String externalId = commerceConnection.getIdProvider().parseExternalIdFromId(propertyValue);

      // No commerce bean found that belongs to no workspace.
      // Search all workspaces for one with a commerce bean.
      Workspace workspace = findWorkspaceWithExistingCommerceBean(commerceConnection, storeContext,
              commerceBeanFactory, propertyValue);
      if (workspace != null) {
        validOnlyInWorkspace(issues, externalId, storeContext.getStoreName(), workspace.getName());
        return;
      }

      // commerce bean not found even in workspaces
      LOG.debug("id: {} not found in the store {}", propertyValue, storeContext.getStoreName());
      invalidExternalId(issues, externalId, storeContext.getStoreName());
    } catch (InvalidContextException e) {
      LOG.debug("StoreContext not found for content: {}", content.getPath(), e);
      invalidStoreContext(issues, propertyValue);
    } catch (InvalidIdException e) {
      LOG.debug("Invalid catalog id: {}", propertyValue, e);
      String storeName = storeContext.getStoreName();
      invalidExternalId(issues, propertyValue, storeName);
    } catch (CommerceException e) {
      LOG.debug("Catalog could not be accessed: {}", propertyValue, e);
      catalogNotAvailable(issues, propertyValue);
    }
  }

  private static StoreContext cloneStoreContextWithWorkspaceId(@Nonnull StoreContext source,
                                                               @Nullable String workspaceId) {
    StoreContext clone = cloneStoreContext(source);
    clone.setWorkspaceId(workspaceId);
    return clone;
  }

  private static StoreContext cloneStoreContext(@Nonnull StoreContext source) {
    StoreContext clone = newStoreContext();

    for (String name : source.getContextNames()) {
      Object value = source.get(name);
      clone.put(name, value);
    }

    return clone;
  }

  /**
   * Return the first workspace for which a commerce bean exists.
   */
  @Nullable
  private static Workspace findWorkspaceWithExistingCommerceBean(CommerceConnection commerceConnection,
                                                                 StoreContext storeContext,
                                                                 CommerceBeanFactory commerceBeanFactory,
                                                                 String propertyValue) {
    // This will be modified throughout the loop (to avoid potentially
    // costly context recreation). Drop afterwards/don't keep it around.
    StoreContext storeContextClone = cloneStoreContext(storeContext);

    List<Workspace> allWorkspaces = getWorkspaces(commerceConnection);

    for (Workspace workspace : allWorkspaces) {
      String workspaceId = workspace.getExternalTechId();
      storeContextClone.setWorkspaceId(workspaceId);

      boolean commerceBeanWithWorkspaceExists = hasCommerceBean(commerceBeanFactory, propertyValue, storeContextClone);
      if (commerceBeanWithWorkspaceExists) {
        return workspace;
      }
    }

    return null;
  }

  @Nonnull
  private static List<Workspace> getWorkspaces(@Nonnull CommerceConnection commerceConnection) {
    WorkspaceService workspaceService = commerceConnection.getWorkspaceService();

    if (workspaceService == null) {
      return emptyList();
    }

    return workspaceService.findAllWorkspaces();
  }

  @Nullable
  protected Site getSite(@Nonnull Content content) {
    ContentSiteAspect contentSiteAspect = sitesService.getContentSiteAspect(content);
    return contentSiteAspect.getSite();
  }

  private static boolean hasCommerceBean(@Nonnull CommerceBeanFactory commerceBeanFactory, @Nonnull String id,
                                         @Nonnull StoreContext storeContext) {
    try {
      CommerceBean commerceBean = commerceBeanFactory.loadBeanFor(id, storeContext);
      return commerceBean != null;
    } catch (NotFoundException e) {
      LOG.trace("Exception creating commerce bean for {} with store context {}: {}", id, storeContext, e.getMessage());
      return false;
    }
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Required
  public void setCommerceConnectionInitializer(CommerceConnectionInitializer commerceConnectionInitializer) {
    this.commerceConnectionInitializer = commerceConnectionInitializer;
  }

  @Required
  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  protected String getPropertyName() {
    return propertyName;
  }
}
