package com.coremedia.blueprint.caas.commerce.wiring;

import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;
import graphql.schema.idl.InterfaceWiringEnvironment;
import graphql.schema.idl.WiringFactory;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A wiring factory supporting access to a commerce adapter by providing a type resolver.
 */
@SuppressWarnings("unused")
public class CommerceWiringFactory implements WiringFactory {

  private static final String COMMERCE_BEAN_CLASS_NAME_PREFIX = "Client";
  private static Set<String> commerceBeanClassNames = Stream.of("CommerceBean", "Catalog", "Category", "Product", "ProductVariant")
          .collect(Collectors.toSet());

  /**
   * Construct a wiring factory for commerce connector access.
   */
  public CommerceWiringFactory() {
  }

  @Override
  public boolean providesTypeResolver(InterfaceWiringEnvironment environment) {
    return commerceBeanClassNames.contains(environment.getInterfaceTypeDefinition().getName());
  }

  /**
   * Returns a type resolver mapping the commerce bean names to GraphQL object types by appending the string "Impl" to the simple class name of the bean.
   */
  @Override
  public TypeResolver getTypeResolver(InterfaceWiringEnvironment environment) {
    return createTypeResolver();
  }

  private TypeResolver createTypeResolver() {
    return env -> {
      Object object = env.getObject();
      if (!(object instanceof CommerceBean)) {
        throw new IllegalArgumentException("must be CommerceBean: " + object);
      }
      String simpleClassName = object.getClass().getSimpleName();
      if (!simpleClassName.startsWith(COMMERCE_BEAN_CLASS_NAME_PREFIX)) {
        return null;
      }
      String typeName = simpleClassName.substring("Client".length()) + "Impl";
      GraphQLObjectType type = env.getSchema().getObjectType(typeName);
      return type;
    };
  }
}
