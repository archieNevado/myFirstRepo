package com.coremedia.ecommerce.studio.rest;

import com.coremedia.cap.common.CapConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.rest.cap.WithDependencies;
import com.coremedia.rest.controller.EntityController;
import com.coremedia.rest.linking.Linker;
import com.coremedia.service.previewurl.PreviewUrlService;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.ecommerce.studio.rest.AbstractCatalogResource.PATH_CATALOG_ALIAS;
import static com.coremedia.ecommerce.studio.rest.AbstractCatalogResource.PATH_ID;
import static com.coremedia.ecommerce.studio.rest.AbstractCatalogResource.PATH_SITE_ID;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = CommerceBeanPreviewsResource.PREVIEWS_URI_PATH, produces = APPLICATION_JSON_VALUE)
public class CommerceBeanPreviewsResource implements EntityController<CommerceBeanPreviews> {
  private static final String PATH_RESOURCE_TYPE = "resourceType";
  static final String PREVIEWS_URI_PATH
          = "livecontext/previews/{" + PATH_RESOURCE_TYPE + "}/{" + PATH_SITE_ID + "}/{" + PATH_CATALOG_ALIAS + "}/{" + PATH_ID + ":.+}";

  private final PreviewUrlService previewUrlService;
  private final Map<String, CommerceBeanResource> resourceMap;
  private final CapConnection connection;
  private final Linker linker;

  public CommerceBeanPreviewsResource(PreviewUrlService previewUrlService,
                                      CategoryResource categoryResource, ProductResource productResource,
                                      ProductVariantResource productVariantResource, CapConnection connection, Linker linker) {
    this.previewUrlService = previewUrlService;
    this.connection = connection;
    this.linker = linker;

    this.resourceMap = Map.of(
            CategoryResource.PATH_TYPE, categoryResource,
            ProductResource.PATH_TYPE, productResource,
            ProductVariantResource.PATH_TYPE, productVariantResource
    );
  }

  @GetMapping
  public ResponseEntity<WithDependencies> get(@PathVariable Map<String, String> params) {
    CommerceBeanPreviews entity = getEntity(params);

    WithDependencies withDependencies = WithDependencies.compute(connection, linker, () -> previewUrlService.getPreviews(entity.getCommerceBean()));
    return ResponseEntity.ok(withDependencies);
  }

  @Override
  public CommerceBeanPreviews getEntity(@NonNull Map<String, String> params) {
    CommerceBean commerceBean = lookupResource(params.get(PATH_RESOURCE_TYPE))
            .map(resource -> resource.getEntity(params))
            .filter(CommerceBean.class::isInstance)
            .map(CommerceBean.class::cast)
            .orElseThrow(IllegalArgumentException::new);

    return new CommerceBeanPreviews(commerceBean);
  }

  @NonNull
  @Override
  public Map<String, String> getPathVariables(@NonNull CommerceBeanPreviews previews) {
    CommerceBean entity = previews.getCommerceBean();
    StoreContext context = entity.getContext();
    Map<String, String> params = new HashMap<>();
    params.put(PATH_RESOURCE_TYPE, entity.getId().getCommerceBeanType().value());
    params.put(PATH_ID, entity.getExternalId());
    params.put(PATH_CATALOG_ALIAS, entity.getId().getCatalogAlias().value());
    params.put(PATH_SITE_ID, context.getSiteId());
    return params;
  }

  private Optional<CommerceBeanResource> lookupResource(String lookupKey) {
    return Optional.ofNullable(resourceMap.get(lookupKey));
  }

}
