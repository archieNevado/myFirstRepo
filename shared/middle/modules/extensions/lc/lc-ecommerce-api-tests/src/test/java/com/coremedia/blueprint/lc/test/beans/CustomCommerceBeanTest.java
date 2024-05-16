package com.coremedia.blueprint.lc.test.beans;

import com.coremedia.blueprint.base.livecontext.client.beans.ClientCommerceBean;
import com.coremedia.blueprint.base.livecontext.client.beans.ClientCommerceBeanFactory;
import com.coremedia.blueprint.base.livecontext.client.common.GenericStoreContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.commerce.adapter.base.entities.Product;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
class CustomCommerceBeanTest {

  static final CommerceBeanType PRODUCTCOLOR = CommerceBeanType.of("productcolor");
  @Mock
  private CatalogAliasTranslationService catalogAliasTranslationService;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private GenericStoreContext storeContext;
  private ClientCommerceBeanFactory commerceBeanFactory;

  @BeforeEach
  void setUp() {
    commerceBeanFactory = new ClientCommerceBeanFactory(catalogAliasTranslationService, (bean) -> Optional.empty());
  }

  @Test
  void createCustomCommerceBean() {
    // register commerce bean type and its constructor
    commerceBeanFactory.register(PRODUCTCOLOR, ProductColor::new);

    // create custom commerce bean
    CommerceId commerceId = CommerceIdBuilder.builder(Vendor.of("test"), "catalog", PRODUCTCOLOR)
            .withExternalId("red hat")
            .build();
    CommerceBean commerceBean = commerceBeanFactory.createBeanFor(commerceId, storeContext);
    assertThat(commerceBean).isInstanceOf(ProductColor.class);
  }

  @Test
  void overrideBuiltInCommerceBean() {
    // register commerce bean type and its constructor
    commerceBeanFactory.register(BaseCommerceBeanType.PRODUCT, ProductColor::new);

    // create custom commerce bean
    CommerceId commerceId = CommerceIdBuilder.builder(Vendor.of("test"), "catalog", BaseCommerceBeanType.PRODUCT)
            .withExternalId("hat")
            .build();
    CommerceBean commerceBean = commerceBeanFactory.createBeanFor(commerceId, storeContext);
    assertThat(commerceBean).isInstanceOf(ProductColor.class);
  }

  static class ProductColor extends ClientCommerceBean<Product> {

    ProductColor(CommerceId commerceId, GenericStoreContext context, ClientCommerceBeanFactory factory) {
      super(commerceId, context, factory);
    }

    @Override
    protected Product loadData() {
      return null;
    }
  }

}
