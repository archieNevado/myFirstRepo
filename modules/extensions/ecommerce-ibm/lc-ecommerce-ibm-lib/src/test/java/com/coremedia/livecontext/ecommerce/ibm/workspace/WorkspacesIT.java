package com.coremedia.livecontext.ecommerce.ibm.workspace;

import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.common.IbmCommerceIdProvider;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.workspace.Workspace;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceService;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.List;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(classes = IbmServiceTestBase.LocalConfig.class)
@ActiveProfiles(IbmServiceTestBase.LocalConfig.PROFILE)
public class WorkspacesIT extends IbmServiceTestBase {

  @Inject
  WorkspaceService workspaceService;

  @Inject
  CatalogService catalogService;

  @Inject
  private IbmCommerceIdProvider ibmCommerceIdProvider;

  @Test
  public void testFindAllWorkspaces() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    Workspace workspace = findWorkspace("Anniversary");
    assertEquals("segment id has wrong format", BaseCommerceBeanType.WORKSPACE, workspace.getId().getCommerceBeanType());
  }

  @Test
  public void testFindTestContentInWorkspace() {
    if (useBetamaxTapes()) {
      return;
    }

    Workspace workspace = findWorkspace("Anniversary");

    StoreContext storeContext = testConfig.getStoreContext();
    StoreContextHelper.setWorkspaceId(storeContext, workspace.getExternalTechId());
    StoreContextHelper.setCurrentContext(storeContext);

    CommerceId categoryId = ibmCommerceIdProvider.formatCategoryId(storeContext.getCatalogAlias(), "PC_ForTheCook");
    Category category0 = catalogService.findCategoryById(categoryId, storeContext);
    assertNotNull("category \"PC_ForTheCook\" not found", category0);

    List<Category> subCategories = catalogService.findSubCategories(category0);
    Category category1 = null;
    for (Category c : subCategories) {
      if ("PC_Anniversary".equals(c.getExternalId())) {
        category1 = c;
        break;
      }
    }
    assertNotNull("category \"PC_Anniversary\" not found", category1);

    List<Product> products = catalogService.findProductsByCategory(category1);
    assertNotNull(products);
    assertFalse(products.isEmpty());

    Product product = null;
    for (Product p : products) {
      if ("PC_COOKING_HAT".equals(p.getExternalId())) {
        product = p;
      }
    }
    assertNotNull("product \"PC_COOKING_HAT\" not found", product);
  }

  private Workspace findWorkspace(String name) {
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
    UserContext userContext = UserContext.builder().build();
    UserContextHelper.setCurrentContext(userContext);

    List<Workspace> workspaces = workspaceService.findAllWorkspaces(StoreContextHelper.getCurrentContext());
    assertNotNull(workspaces);
    assertFalse(workspaces.isEmpty());

    Workspace workspace = null;
    for (Workspace w : workspaces) {
      if (w.getName().startsWith(name)) {
        workspace = w;
      }
    }
    assertNotNull("workspace \"" + name + "...\" not found", workspace);

    return workspace;
  }
}
