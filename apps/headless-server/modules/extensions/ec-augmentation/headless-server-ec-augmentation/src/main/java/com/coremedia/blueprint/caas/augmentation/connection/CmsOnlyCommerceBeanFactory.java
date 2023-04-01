package com.coremedia.blueprint.caas.augmentation.connection;

import com.coremedia.livecontext.ecommerce.catalog.Catalog;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.invoke.MethodHandles.lookup;

@DefaultAnnotation(NonNull.class)
class CmsOnlyCommerceBeanFactory implements CommerceBeanFactory {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  private static final Map<CommerceBeanType, Class<? extends CommerceBean>> BEAN_INTERFACES = Map.of(
          BaseCommerceBeanType.CATALOG, Catalog.class,
          BaseCommerceBeanType.CATEGORY, Category.class,
          BaseCommerceBeanType.PRODUCT, Product.class,
          BaseCommerceBeanType.SKU, ProductVariant.class
  );

  @Override
  public CommerceBean createBeanFor(CommerceId commerceId, StoreContext storeContext) {
    return createBean(commerceId, storeContext);
  }

  static CommerceBean createBean(CommerceId commerceId, StoreContext storeContext) {
    LOG.trace("Creating CmsOnly commerce bean for '{}'.", commerceId);
    var delegate = new CmsOnlyCommerceBean(commerceId, storeContext);
    var classLoader = CmsOnlyCommerceBeanFactory.class.getClassLoader();
    var interfaces = new Class[]{BEAN_INTERFACES.get(commerceId.getCommerceBeanType())};
    return (CommerceBean) Proxy.newProxyInstance(classLoader, interfaces,
            (proxy, method, args) -> {
              Class<?> declaringClass = method.getDeclaringClass();
              if (declaringClass.isAssignableFrom(CmsOnlyCommerceBean.class)) {
                return method.invoke(delegate, args);
              }
              if (declaringClass.equals(Category.class)) {
                if (method.getName().equals("isRoot")) {
                  // no hierarchy supported yet
                  return false;
                }
                if (method.getName().equals("getParent")) {
                  // no hierarchy supported yet
                  return null;
                }
              } else if (declaringClass.equals(Product.class)) {
                if (method.getName().equals("getCategory")) {
                  // no category assignment yet
                  return null;
                }
              }
              throw new UnsupportedOperationException(method.toString());
            }
    );
  }

  @Override
  public List<CommerceBean> createBeansFor(List<CommerceId> list, StoreContext storeContext) {
    return list.stream().map(id -> createBeanFor(id, storeContext)).collect(Collectors.toList());
  }

  @Nullable
  @Override
  public CommerceBean loadBeanFor(CommerceId commerceId, StoreContext storeContext) {
    // no loading
    return createBeanFor(commerceId, storeContext);
  }

  @Override
  public List<CommerceBean> loadBeansFor(List<CommerceId> list, StoreContext storeContext) {
    // no loading
    return createBeansFor(list, storeContext);
  }

}
