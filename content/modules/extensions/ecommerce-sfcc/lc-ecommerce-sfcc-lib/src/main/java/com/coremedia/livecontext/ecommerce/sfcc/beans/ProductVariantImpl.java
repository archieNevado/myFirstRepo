package com.coremedia.livecontext.ecommerce.sfcc.beans;

import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityInfo;
import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccConfigurationProperties;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.VariationAttributeDocument;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Named("sfccCommerceBeanFactory:sku")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductVariantImpl extends ProductImpl implements ProductVariant {

  public ProductVariantImpl(@NonNull SfccConfigurationProperties sfccConfigurationProperties) {
    super(sfccConfigurationProperties);
  }

  @Override
  @Nullable
  public Product getParent() {
    String parentProductID = getDelegate().getMaster().getMasterId();
    if (parentProductID == null) {
      return null;
    }

    CommerceId commerceId = getCommerceIdProvider().formatProductTechId(getCatalogAlias(), parentProductID);

    return (Product) getCommerceBeanFactory().createBeanFor(commerceId, getContext());
  }

  @Nullable
  @Override
  public Object getAttributeValue(@NonNull String s) {
    Optional<ProductAttribute> match = getDefiningAttributes().stream()
            .filter(attribute -> attribute.getId().equals(s))
            .findFirst();

    if (!match.isPresent()) {
      return null;
    }
    return match.get().getValue();
  }


  @NonNull
  @Override
  public List<ProductAttribute> getDefiningAttributes() {
    if (definingAttributes == null) {
      Map<String, String> attributeValues = getDelegate().getVariationValues();
      List<VariationAttributeDocument> variationAttributes = getDelegate().getVariationAttributes();

      if (variationAttributes == null || variationAttributes.isEmpty()) {
        return Collections.emptyList();
      }

      List<ProductAttribute> result = variationAttributes.stream()
              .map(variationAttribute -> new ProductVariantAttributeImpl(variationAttribute, attributeValues.get(variationAttribute.getId()), true))
              .filter(Objects::nonNull)
              .collect(toList());

      definingAttributes = Collections.unmodifiableList(result);
    }

    return definingAttributes;
  }

  @Nullable
  @Override
  public AvailabilityInfo getAvailabilityInfo() {
    return null;
  }
}
