package com.coremedia.lc.studio.lib.validators;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
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

import java.util.Collections;
import java.util.List;

import static com.coremedia.rest.validation.Severity.ERROR;
import static com.coremedia.rest.validation.Severity.WARN;
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
  private static final String CODE_ISSUE_CONTEXT_INVALID = "InvalidStoreContext";
  private static final String CODE_ISSUE_CONTEXT_NOT_FOUND = "StoreContextNotFound";

  private static final String CODE_ISSUE_CATALOG_ERROR = "catalogError";

  private String propertyName;
  private CommerceConnectionInitializer commerceConnectionInitializer;

  /**
   * A convinient method to add an issue. The given code will be prefixed with
   * the name of the document type and '_'
   * @param issues the given issues
   * @param severity the severity of this issue
   * @param code a code identifying the type of issue. This will be prefixed.
   * @param arguments optional argument describing the issue, for example indicating a illegally linked object
   */
  protected void addIssue(Issues issues, Severity severity, String code, Object... arguments) {
    issues.addIssue(severity, getPropertyName(), getContentType() + '_' + code, arguments);
  }

  protected void emptyPropertyValue(Content content, Issues issues) {
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
    if (content != null && content.isInProduction()) {
      String propertyValue = content.getString(propertyName);
      if (isBlank(propertyValue)) {
        emptyPropertyValue(content, issues);
      } else {
        initConnection(content);
        CommerceConnection commerceConnection = Commerce.getCurrentConnection();
        if (null == commerceConnection) {
          LOG.debug("StoreContext not found for content: " + content.getPath());
          storeContextNotFound(issues, propertyValue);
          return;
        }

        CommerceBean commerceBean;
        StoreContext currentContext = null;
        try {
          String externalId = commerceConnection.getIdProvider().parseExternalIdFromId(propertyValue);
          //clear the workspace id before validating
          currentContext = commerceConnection.getStoreContext();
          if (null == currentContext) {
            LOG.debug("StoreContext not found for content: " + content.getPath());
            storeContextNotFound(issues, propertyValue);
            return;
          }
          currentContext.setWorkspaceId(null);
          commerceBean = loadOrReturnNull(propertyValue, currentContext);

          if (commerceBean != null) {
            // catalog bean is found in the main catalog
            return;
          }

          //commerce bean still not found. search it in each workspace
          //is workspace available
          List<Workspace> allWorkspaces = Collections.emptyList();
          WorkspaceService workspaceService = commerceConnection.getWorkspaceService();
          if (workspaceService != null){
            allWorkspaces = workspaceService.findAllWorkspaces();
          }

          for (Workspace workspace : allWorkspaces) {
            currentContext.setWorkspaceId(workspace.getExternalTechId());
            commerceBean = loadOrReturnNull(propertyValue, currentContext);
            if (commerceBean != null) {
              validOnlyInWorkspace(issues, externalId, currentContext.getStoreName(), workspace.getName());
              return;
            }
          }

          //commerce bean not found even in workspaces
          LOG.debug("id: " + propertyValue + " not found in the store " + currentContext.getStoreName());
          invalidExternalId(issues, externalId, currentContext.getStoreName());

        } catch (InvalidContextException e) {
          LOG.debug("StoreContext not found for content: " + content.getPath(), e);
          invalidStoreContext(issues, propertyValue);
        } catch (InvalidIdException e) {
          LOG.debug("Invalid catalog id: " + propertyValue, e);
          String s = currentContext != null ? currentContext.getStoreName() : "null";
          invalidExternalId(issues, propertyValue, s);
        } catch (CommerceException e) {
          LOG.debug("Catalog could not be accessed: " + propertyValue, e);
          catalogNotAvailable(issues, propertyValue);
        }
      }
    }
  }

  private CommerceBean loadOrReturnNull(String id, StoreContext storeContext) {
    CommerceBean result = null;
    try {
      result = Commerce.getCurrentConnection().getCommerceBeanFactory().loadBeanFor(id, storeContext);
    } catch (NotFoundException e) {
      LOG.trace("Exception creating commerce bean for {} with store context {}: {}", id, storeContext, e.getMessage());
    }
    return result;
  }

  protected void initConnection(Content content) {
    commerceConnectionInitializer.init(content);
  }

  @Required
  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  protected String getPropertyName() {
    return propertyName;
  }

  @Required
  public void setCommerceConnectionInitializer(CommerceConnectionInitializer commerceConnectionInitializer) {
    this.commerceConnectionInitializer = commerceConnectionInitializer;
  }

}
