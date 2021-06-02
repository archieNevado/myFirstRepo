package com.coremedia.blueprint.headlessserver;

import com.coremedia.blueprint.base.caas.model.adapter.LocalizedVariantsAdapterFactory;
import com.coremedia.blueprint.base.caas.model.adapter.NavigationAdapterFactory;
import com.coremedia.blueprint.base.caas.model.adapter.ByPathAdapterFactory;
import com.coremedia.blueprint.base.caas.model.adapter.PageGridAdapterFactory;
import com.coremedia.blueprint.base.caas.model.adapter.SettingsAdapterFactory;
import com.coremedia.blueprint.base.caas.model.adapter.StructAdapterFactory;
import com.coremedia.blueprint.base.caas.segments.CMLinkableSegmentStrategy;
import com.coremedia.blueprint.base.links.ContentSegmentStrategy;
import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridService;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.blueprint.image.transformation.ImageTransformationConfiguration;
import com.coremedia.caas.config.CaasGraphqlConfigurationProperties;
import com.coremedia.caas.config.RemoteServiceConfiguration;
import com.coremedia.caas.config.StaxContextConfigurationProperties;
import com.coremedia.caas.filter.InProductionFilterPredicate;
import com.coremedia.caas.filter.ValidityDateFilterPredicate;
import com.coremedia.caas.link.ContentLink;
import com.coremedia.caas.link.ContentLinkComposer;
import com.coremedia.caas.link.GraphQLLink;
import com.coremedia.caas.media.ResponsiveMediaAdapter;
import com.coremedia.caas.media.ResponsiveMediaAdapterFactory;
import com.coremedia.caas.model.ContentRoot;
import com.coremedia.caas.model.adapter.CMGrammarRichTextAdapterFactory;
import com.coremedia.caas.model.adapter.ContentBlobAdapter;
import com.coremedia.caas.model.adapter.ContentBlobAdapterFactory;
import com.coremedia.caas.model.adapter.ExtendedLinkListAdapterFactory;
import com.coremedia.caas.model.adapter.HtmlAdapterFactory;
import com.coremedia.caas.model.adapter.LinkListAdapterFactory;
import com.coremedia.caas.model.adapter.RemoteServiceAdapterFactory;
import com.coremedia.caas.model.adapter.RichTextAdapter;
import com.coremedia.caas.model.adapter.RichTextAdapterFactory;
import com.coremedia.caas.model.adapter.RichTextAdapterRegistry;
import com.coremedia.caas.model.converter.CMGrammarRichTextToMapConverter;
import com.coremedia.caas.model.converter.MapToNestedMapsConverter;
import com.coremedia.caas.model.converter.RichTextToStringConverter;
import com.coremedia.caas.model.converter.StructToNestedMapsConverter;
import com.coremedia.caas.model.mapper.CompositeModelMapper;
import com.coremedia.caas.model.mapper.FilteringModelMapper;
import com.coremedia.caas.model.mapper.ModelMapper;
import com.coremedia.caas.model.mapper.ModelMappingPropertyAccessor;
import com.coremedia.caas.model.mapper.ModelMappingWiringFactory;
import com.coremedia.caas.richtext.RichtextTransformerReader;
import com.coremedia.caas.richtext.RichtextTransformerRegistry;
import com.coremedia.caas.richtext.config.loader.ClasspathConfigResourceLoader;
import com.coremedia.caas.richtext.config.loader.ConfigResourceLoader;
import com.coremedia.caas.schema.CoercingMap;
import com.coremedia.caas.schema.CoercingRichTextTree;
import com.coremedia.caas.schema.SchemaParser;
import com.coremedia.caas.service.cache.CacheInstances;
import com.coremedia.caas.service.cache.CacheMapWrapper;
import com.coremedia.caas.service.cache.Weighted;
import com.coremedia.caas.spel.SpelDirectiveWiring;
import com.coremedia.caas.spel.SpelEvaluationStrategy;
import com.coremedia.caas.spel.SpelFunctions;
import com.coremedia.caas.web.CaasPersistedQueryConfigurationProperties;
import com.coremedia.caas.web.CaasServiceConfigurationProperties;
import com.coremedia.caas.web.CaasWebConfig;
import com.coremedia.caas.web.GraphiqlConfigurationProperties;
import com.coremedia.caas.web.filter.HSTSResponseHeaderFilter;
import com.coremedia.caas.web.link.ContentBlobLinkComposer;
import com.coremedia.caas.web.link.ResponsiveMediaLinkComposer;
import com.coremedia.caas.web.metadata.MetadataConfigurationProperties;
import com.coremedia.caas.web.metadata.MetadataProvider;
import com.coremedia.caas.web.metadata.MetadataRoot;
import com.coremedia.caas.web.persistedqueries.DefaultPersistedQueriesLoader;
import com.coremedia.caas.web.persistedqueries.DefaultQueryNormalizer;
import com.coremedia.caas.web.persistedqueries.PersistedQueriesLoader;
import com.coremedia.caas.web.persistedqueries.QueryNormalizer;
import com.coremedia.caas.web.view.ViewBySiteFilterDataFetcher;
import com.coremedia.caas.web.wiring.GraphQLInvocationImpl;
import com.coremedia.caas.wiring.CapStructPropertyAccessor;
import com.coremedia.caas.wiring.CompositeTypeNameResolver;
import com.coremedia.caas.wiring.CompositeTypeNameResolverProvider;
import com.coremedia.caas.wiring.ContentRepositoryWiringFactory;
import com.coremedia.caas.wiring.ContextInstrumentation;
import com.coremedia.caas.wiring.ConvertingDataFetcher;
import com.coremedia.caas.wiring.DataFetcherMappingInstrumentation;
import com.coremedia.caas.wiring.ExecutionTimeoutInstrumentation;
import com.coremedia.caas.wiring.FilteringDataFetcher;
import com.coremedia.caas.wiring.ProvidesTypeNameResolver;
import com.coremedia.caas.wiring.RemoteLinkWiringFactory;
import com.coremedia.caas.wiring.TypeNameResolver;
import com.coremedia.caas.wiring.TypeNameResolverWiringFactory;
import com.coremedia.caas.wrapper.UrlPathFormater;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructService;
import com.coremedia.link.CompositeLinkComposer;
import com.coremedia.link.LinkComposer;
import com.coremedia.link.uri.UriLinkBuilder;
import com.coremedia.link.uri.UriLinkBuilderImpl;
import com.coremedia.link.uri.UriLinkComposer;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.objectserver.urlservice.UrlServiceRequestParams;
import com.coremedia.springframework.customizer.Customize;
import com.coremedia.springframework.customizer.CustomizerConfiguration;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import com.coremedia.xml.Markup;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import edu.umd.cs.findbugs.annotations.Nullable;
import graphql.ExecutionInput;
import graphql.GraphQL;
import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.preparsed.PreparsedDocumentEntry;
import graphql.execution.preparsed.PreparsedDocumentProvider;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.WiringFactory;
import graphql.spring.web.servlet.ExecutionResultHandler;
import graphql.spring.web.servlet.GraphQLInvocation;
import org.apache.commons.io.IOUtils;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.BatchLoaderWithContext;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderOptions;
import org.dataloader.DataLoaderRegistry;
import org.dataloader.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.expression.MapAccessor;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.coremedia.caas.web.CaasWebConfig.ATTRIBUTE_NAMES_TO_GQL_CONTEXT;
import static com.coremedia.caas.web.CaasWebConfig.FORWARD_HEADER_MAP;
import static java.lang.invoke.MethodHandles.lookup;
import static java.util.Collections.emptyList;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        CaasServiceConfigurationProperties.class,
        CaasGraphqlConfigurationProperties.class,
        CaasPersistedQueryConfigurationProperties.class,
        RemoteServiceConfiguration.class,
        GraphiqlConfigurationProperties.class,
        StaxContextConfigurationProperties.class,
        MetadataConfigurationProperties.class
})
@EnableWebMvc
@ComponentScan({
        "com.coremedia.caas",
        "com.coremedia.blueprint.base.caas",
        "com.coremedia.cap.undoc.common.spring"
})
@ImportResource(value = {
        "classpath:/com/coremedia/blueprint/base/settings/impl/bpbase-settings-services.xml",
        "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml",
        "classpath:/com/coremedia/blueprint/base/pagegrid/impl/bpbase-pagegrid-services.xml",
        "classpath:/com/coremedia/blueprint/base/navigation/context/bpbase-default-contextstrategy.xml",
        "classpath:/com/coremedia/blueprint/base/links/bpbase-urlpathformatting.xml"
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
@Import({
        ImageTransformationConfiguration.class,
        CustomizerConfiguration.class
})
public class CaasConfig implements WebMvcConfigurer {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());
  private static final String OPTIONAL_QUERY_ROOT_BEAN_NAME_PREFIX = "query-root:";
  private static final int TWENTY_FOUR_HOURS = 24 * 60 * 60;
  private static final int CORS_RESPONSE_MAX_AGE = TWENTY_FOUR_HOURS;

  private final CaasServiceConfigurationProperties caasServiceConfigurationProperties;
  private final CaasGraphqlConfigurationProperties caasGraphqlConfigurationProperties;
  private final CaasPersistedQueryConfigurationProperties caasPersistedQueryConfigurationProperties;
  private final GraphiqlConfigurationProperties graphiqlConfigurationProperties;
  private final MetadataConfigurationProperties metadataConfigurationProperties;

  public CaasConfig(CaasServiceConfigurationProperties caasServiceConfigurationProperties,
                    CaasGraphqlConfigurationProperties caasGraphqlConfigurationProperties,
                    CaasPersistedQueryConfigurationProperties caasPersistedQueryConfigurationProperties,
                    GraphiqlConfigurationProperties graphiqlConfigurationProperties,
                    MetadataConfigurationProperties metadataConfigurationProperties) {
    this.caasServiceConfigurationProperties = caasServiceConfigurationProperties;
    this.caasGraphqlConfigurationProperties = caasGraphqlConfigurationProperties;
    this.caasPersistedQueryConfigurationProperties = caasPersistedQueryConfigurationProperties;
    this.graphiqlConfigurationProperties = graphiqlConfigurationProperties;
    this.metadataConfigurationProperties = metadataConfigurationProperties;
  }

  @Override
  public void configurePathMatch(PathMatchConfigurer matcher) {
    matcher.setUseSuffixPatternMatch(false);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "OPTIONS")
            .allowCredentials(true)
            .maxAge(CORS_RESPONSE_MAX_AGE);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    List<String> resources = new ArrayList<>(Arrays.asList("/static/**", "/docs/**"));
    List<String> resourceLocations = new ArrayList<>();
    if (graphiqlConfigurationProperties.isEnabled()) {
      resources.add("/graphiql/static/**");
    }
    if (caasServiceConfigurationProperties.getSwagger().isEnabled()) {
      resources.add("swagger-ui.html");
      resources.add("/webjars/**");
      resourceLocations.add("classpath:/META-INF/resources/webjars/");
    }
    if (caasServiceConfigurationProperties.isPreview()) {
      resourceLocations.add("classpath:/static/");
      resourceLocations.add("classpath:/META-INF/resources/");
    }
    registry.addResourceHandler(resources.toArray(new String[0]))
            .addResourceLocations(resourceLocations.toArray(new String[0]));
  }

  @Bean
  @ConditionalOnProperty("caas.logRequests")
  public Filter logFilter() {
    CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter() {
      @Override
      protected boolean shouldLog(HttpServletRequest request) {
        return true;
      }

      @Override
      protected void beforeRequest(HttpServletRequest request, @Nullable String message) {
        if (!RequestMethod.OPTIONS.name().equals(request.getMethod())) {
          LOG.trace(message);
        }
      }
    };
    filter.setIncludeQueryString(true);
    filter.setIncludePayload(false);
    return filter;
  }

  @Bean
  public Filter hstsResponseHeaderFilter(CaasServiceConfigurationProperties caasServiceConfigurationProperties) {
    return new HSTSResponseHeaderFilter(caasServiceConfigurationProperties);
  }

  @Bean("cacheManager")
  @SuppressWarnings("unchecked")
  public CacheManager cacheManager() {
    ImmutableList.Builder<org.springframework.cache.Cache> builder = ImmutableList.builder();
    caasServiceConfigurationProperties.getCacheSpecs().forEach((cacheName, cacheSpec) -> {
      com.github.benmanes.caffeine.cache.Cache cache = Caffeine.from(cacheSpec)
              .weigher((key, value) -> {
                if (value instanceof Weighted) {
                  return ((Weighted) value).getWeight();
                }
                return String.valueOf(value).length();
              })
              .build();
      builder.add(new CaffeineCache(cacheName, cache));
    });
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    cacheManager.setCaches(builder.build());
    return cacheManager;
  }

  @Bean
  public LinkComposer<Object, GraphQLLink> graphQlLinkComposer(List<LinkComposer<?, ? extends GraphQLLink>> linkComposers) {
    return new CompositeLinkComposer<>(linkComposers, emptyList());
  }

  @Bean
  public LinkComposer<Object, String> uriLinkComposer(List<LinkComposer<?, ? extends UriLinkBuilder>> linkComposers) {
    return new UriLinkComposer<>(
            new CompositeLinkComposer<>(linkComposers, emptyList()));
  }

  @Bean
  public LinkComposer<ContentBlobAdapter, UriLinkBuilder> contentBlobLinkComposer() {
    return contentBlobAdapter -> new ContentBlobLinkComposer().apply(contentBlobAdapter);
  }

  @Bean
  public LinkComposer<ResponsiveMediaAdapter, UriLinkBuilder> responsiveMediaLinkComposer(MimeTypeService mimeTypeService, UrlPathFormater urlPathFormater) {
    return responsiveMediaAdapter -> new ResponsiveMediaLinkComposer(mimeTypeService, urlPathFormater).apply(responsiveMediaAdapter);
  }

  @Bean
  public LinkComposer<Content, ContentLink> contentLinkComposer() {
    return content -> new ContentLinkComposer().apply(content);
  }

  @Bean
  public LinkComposer<Content, UriLinkBuilder> contentUriLinkComposer() {
    return content -> Optional.of(new UriLinkBuilderImpl(
            UriComponentsBuilder.fromUriString(content.getId()).build()));
  }

  @Bean
  public Function<Content, String> contentIdProvider() {
    return content -> String.valueOf(IdHelper.parseContentId(content.getId()));
  }

  /**
   * Returns a type resolver mapping content type names to GraphQL object types by appending the string "Impl".
   * If such a GraphQL type exists within the schema, it is returned (wrapped with an Optional).
   * Otherwise, the name of the parent content type is tried (again with the string "Impl" appended),
   * and so on until either a corresponding GraphQL type is found or the top of the content type hierarchy is reached.
   * In the latter case, an empty Optional is returned.
   */
  @Bean
  public TypeNameResolver<Content> contentTypeNameResolver(@Lazy GraphQLSchema schema) {
    return content -> {
      ContentType currentContentType = content.getType();
      while (currentContentType != null) {
        String typeName = currentContentType.getName() + "Impl";
        GraphQLObjectType type = schema.getObjectType(typeName);
        if (type != null) {
          return Optional.of(type.getName());
        }
        currentContentType = currentContentType.getParent();
      }
      return Optional.empty();
    };
  }

  @Bean
  @SuppressWarnings("squid:S1067")
  public ProvidesTypeNameResolver providesContentTypeNameResolver(ContentRepository repository) {
    return typeName ->
            "Banner".equals(typeName) ||
                    "Detail".equals(typeName) ||
                    "CollectionItem".equals(typeName) ||
                    "HasPageGrid".equals(typeName) ||
                    repository.getContentType(typeName) != null ? Optional.of(true) : Optional.empty();
  }

  @Bean
  public TypeNameResolver<Object> compositeTypeNameResolver(List<TypeNameResolver<?>> typeNameResolvers) {
    return new CompositeTypeNameResolver<>(typeNameResolvers);
  }

  @Bean
  public ProvidesTypeNameResolver compositeProvidesTypeNameResolver(List<ProvidesTypeNameResolver> providesTypeNameResolvers) {
    return new CompositeTypeNameResolverProvider(providesTypeNameResolvers);
  }

  @Bean
  public TypeNameResolverWiringFactory typeNameResolverWiringFactory(
          @Qualifier("compositeProvidesTypeNameResolver") ProvidesTypeNameResolver providesTypeNameResolver,
          @Qualifier("compositeTypeNameResolver") TypeNameResolver<Object> typeNameResolver) {
    return new TypeNameResolverWiringFactory(providesTypeNameResolver, typeNameResolver);
  }

  @Bean
  public NavigationAdapterFactory navigationAdapter(@Qualifier("contentContextStrategy") ContextStrategy<Content, Content> contextStrategy, Map<String, TreeRelation<Content>> treeRelations) {
    return new NavigationAdapterFactory(contextStrategy, treeRelations);
  }

  @Bean
  public LinkListAdapterFactory mediaLinkListAdapter() {
    return new LinkListAdapterFactory("pictures");
  }

  @Bean
  public LinkListAdapterFactory teaserMediaLinkListAdapter(@Qualifier("teaserTargetsAdapter") ExtendedLinkListAdapterFactory teaserTargetsAdapter) {
    return new LinkListAdapterFactory("pictures",
            content -> teaserTargetsAdapter.to(content).getTargets().stream()
                    .flatMap(target -> target.getLinks("pictures").stream()
                    ));
  }

  @Bean
  public LinkListAdapterFactory channelMediaLinkListAdapter(@Qualifier("pageGridAdapter") PageGridAdapterFactory pageGridAdapter,
                                                            @Qualifier("mediaLinkListAdapter") LinkListAdapterFactory mediaLinkListAdapter,
                                                            @Qualifier("teaserMediaLinkListAdapter") LinkListAdapterFactory teaserMediaLinkListAdapter) {
    return new LinkListAdapterFactory("pictures",
            content -> pageGridAdapter.to(content, "placement").getPlacements(null, Arrays.asList("header", "footer")).stream()
                    .flatMap(placement -> placement.getItems().stream())
                    .map(teasable -> {
                      if (teasable.getType().isSubtypeOf("CMTeaser")) {
                        return teaserMediaLinkListAdapter.to(teasable, "CMMedia").first();
                      } else if (teasable.getType().isSubtypeOf("CMPicture")) {
                        return teasable;
                      } else {
                        return mediaLinkListAdapter.to(teasable, "CMMedia").first();
                      }
                    })
                    .filter(Objects::nonNull));
  }

  @Bean
  public ExtendedLinkListAdapterFactory teaserTargetsAdapter() {
    return new ExtendedLinkListAdapterFactory("targets", "links", "target", "CMLinkable", "target");
  }

  @Bean
  public PageGridAdapterFactory pageGridAdapter(@Qualifier("contentBackedPageGridService") ContentBackedPageGridService contentBackedPageGridService) {
    return new PageGridAdapterFactory(contentBackedPageGridService);
  }

  @Bean
  public SettingsAdapterFactory settingsAdapter(SettingsService settingsService, StructAdapterFactory structAdapterFactory) {
    return new SettingsAdapterFactory(settingsService, structAdapterFactory);
  }

  @Bean
  public StructAdapterFactory structAdapter(StructService structService, SettingsService settingsService) {
    return new StructAdapterFactory(structService, settingsService);
  }

  @Bean
  public ContentBlobAdapterFactory contentBlobAdapter() {
    return new ContentBlobAdapterFactory();
  }

  @Bean
  public RemoteServiceAdapterFactory remoteServiceAdapter(RemoteServiceConfiguration remoteServiceConfiguration) {
    return new RemoteServiceAdapterFactory(remoteServiceConfiguration);
  }

  @Bean
  public ByPathAdapterFactory byPathAdapter(ContentRepository repository, SitesService sitesService, UrlPathFormattingHelper urlPathFormattingHelper, @Qualifier("navigationAdapter") NavigationAdapterFactory navigationAdapterFactory) {
    return new ByPathAdapterFactory(repository, sitesService, urlPathFormattingHelper, navigationAdapterFactory);
  }

  @Bean
  public LocalizedVariantsAdapterFactory localizedVariantsAdapterFactory(SitesService sitesService) {
    return new LocalizedVariantsAdapterFactory(sitesService);
  }

  @Bean
  public HtmlAdapterFactory htmlAdapterFactory() {
    return new HtmlAdapterFactory();
  }

  @Bean
  public CMGrammarRichTextAdapterFactory cmGrammarRichTextFactory(
          ContentRepository contentRepository,
          ResponsiveMediaAdapterFactory mediaResource,
          RichtextTransformerRegistry richtextTransformerRegistry,
          LinkComposer<Object, String> uriLinkComposer,
          LinkComposer<Object, GraphQLLink> graphqlLinkComposer,
          StaxContextConfigurationProperties staxContextConfigurationProperties) {
    return new CMGrammarRichTextAdapterFactory(contentRepository, mediaResource, richtextTransformerRegistry, uriLinkComposer, graphqlLinkComposer, staxContextConfigurationProperties);
  }

  @Bean
  public RichTextAdapterRegistry richTextAdapterRegistry(List<RichTextAdapterFactory<?>> richTextAdapterFactories) {
    return new RichTextAdapterRegistry(richTextAdapterFactories);
  }

  @Bean
  public ModelMapper<Markup, RichTextAdapter> richTextModelMapper(RichTextAdapterRegistry richTextAdapterRegistry) {
    return richTextAdapterRegistry::get;
  }

  @Bean
  public ModelMapper<GregorianCalendar, ZonedDateTime> dateModelMapper() {
    return gregorianCalendar -> Optional.of(gregorianCalendar.toZonedDateTime());
  }


  @Bean
  public ConfigResourceLoader richTextConfigResourceLoader() {
    return new ClasspathConfigResourceLoader("/");
  }

  @Bean
  public RichtextTransformerRegistry richtextTransformerRegistry(@Qualifier("richTextConfigResourceLoader") ConfigResourceLoader resourceLoader, @Qualifier("cacheManager") CacheManager cacheManager, StaxContextConfigurationProperties staxContextConfigurationProperties) throws IOException {
    return new RichtextTransformerReader(resourceLoader, cacheManager, staxContextConfigurationProperties).read();
  }

  @Bean
  public PropertyAccessor mapPropertyAccessor() {
    return new MapAccessor();
  }

  @Bean
  public PropertyAccessor reflectivePropertyAccessor() {
    return new ReflectivePropertyAccessor();
  }

  @Bean
  public PropertyAccessor capStructPropertyAccessor() {
    return new CapStructPropertyAccessor();
  }

  @Bean
  @Qualifier("propertyAccessors")
  public List<PropertyAccessor> propertyAccessors(List<PropertyAccessor> propertyAccessors, ModelMapper<Object, Object> modelMapper) {
    return propertyAccessors.stream()
            .map(propertyAccessor -> new ModelMappingPropertyAccessor(propertyAccessor, modelMapper))
            .collect(Collectors.toList());
  }

  @Bean
  public SpelEvaluationStrategy spelEvaluationStrategy(BeanFactory beanFactory, @Qualifier("propertyAccessors") List<PropertyAccessor> propertyAccessors) {
    return new SpelEvaluationStrategy(beanFactory, propertyAccessors);
  }

  @Bean
  @Qualifier("globalSpelVariables")
  public Method first() throws NoSuchMethodException {
    return SpelFunctions.class.getDeclaredMethod("first", List.class);
  }

  @Bean
  public SchemaDirectiveWiring fetch(SpelEvaluationStrategy spelEvaluationStrategy,
                                     @Qualifier("globalSpelVariables") Map<String, Object> globalSpelVariables) {
    return new SpelDirectiveWiring(spelEvaluationStrategy, globalSpelVariables);
  }

  @Bean
  public RichTextToStringConverter richTextToStringConverter() {
    return new RichTextToStringConverter();
  }

  @Bean
  public CMGrammarRichTextToMapConverter cmGrammarRichTextToMapConverter() {
    return new CMGrammarRichTextToMapConverter();
  }

  @Bean
  public Converter<Map<String, Object>, Map<String, Object>> mapToNestedMapsConverter(ModelMapper<Markup, RichTextAdapter> richTextModelMapper,
                                                                                      ContentBlobAdapterFactory contentBlobAdapterFactory,
                                                                                      LinkComposer<Object, String> uriLinkComposer) {
    return new MapToNestedMapsConverter(richTextModelMapper, contentBlobAdapterFactory, uriLinkComposer, RichTextAdapter.DEFAULT_VIEW);
  }

  @Bean
  public Converter<Struct, Map<String, Object>> structToNestedMapsConverter(MapToNestedMapsConverter mapToNestedMapsConverter) {
    return new StructToNestedMapsConverter(mapToNestedMapsConverter);
  }

  @Bean
  public ConversionServiceFactoryBean graphQlConversionService(Set<Converter> converters) {
    ConversionServiceFactoryBean conversionServiceFactoryBean = new ConversionServiceFactoryBean();
    conversionServiceFactoryBean.setConverters(converters);
    return conversionServiceFactoryBean;
  }

  @Bean
  public ContentRepositoryWiringFactory contentRepositoryWiringFactory(Map<String, GraphQLScalarType> builtinScalars,
                                                                       ContentRepository repository) {
    return new ContentRepositoryWiringFactory(repository, builtinScalars);
  }

  @Bean
  public RemoteLinkWiringFactory remoteLinkWiringFactory() {
    return new RemoteLinkWiringFactory();
  }

  @Bean
  @Qualifier("filterPredicate")
  public Predicate<Object> validityDateFilterPredicate() {
    return new ValidityDateFilterPredicate();
  }

  @Bean
  @Qualifier("filterPredicate")
  public Predicate<Object> inProductionFilterPredicate() {
    return new InProductionFilterPredicate();
  }

  @Bean
  public ModelMapper<Object, Object> rootModelMapper(List<ModelMapper<?, ?>> modelMappers, @Qualifier("filterPredicate") List<Predicate<Object>> predicates) {
    return new FilteringModelMapper(new CompositeModelMapper<>(modelMappers), predicates);
  }

  @Bean
  @Qualifier("queryRoot")
  public ContentRoot content(ContentRepository repository, SitesService sitesService) {
    return new ContentRoot(repository, sitesService);
  }

  @Bean
  @Qualifier("queryRoot")
  @ConditionalOnProperty(prefix = "caas.metadata", name = "enabled", havingValue = "true", matchIfMissing = true)
  public MetadataRoot metadata(TypeDefinitionRegistry typeDefinitionRegistry, List<MetadataProvider> metadataProvider) {
    return new MetadataRoot(typeDefinitionRegistry, metadataProvider);
  }

  @Bean
  @Customize("contentSegmentStrategyMap")
  public Map<String, ContentSegmentStrategy> caasContentSegmentStrategyMap() {
    return Map.of("CMLinkable", new CMLinkableSegmentStrategy());
  }

  @Bean
  public GraphQLInvocation graphQLInvocation(GraphQL graphQL,
                                             @Qualifier("queryRoot") Map<String, Object> queryRoots,
                                             DataLoaderRegistry dataLoaderRegistry, CaasServiceConfigurationProperties caasServiceConfigurationProperties,
                                             @Qualifier(ATTRIBUTE_NAMES_TO_GQL_CONTEXT) Set<String> requestAttributeNamesToGraphqlContext) {
    return new GraphQLInvocationImpl(graphQL, renameQueryRootsWithOptionalPrefix(queryRoots), dataLoaderRegistry, caasServiceConfigurationProperties, requestAttributeNamesToGraphqlContext);
  }

  @Bean
  public ExecutionResultHandler executionResultHandler() {
    return new CaasExecutionResultHandler();
  }

  @Bean
  public QueryNormalizer queryNormalizer() {
    return new DefaultQueryNormalizer();
  }

  @Bean
  public PersistedQueriesLoader persistedQueriesLoader() {
    return new DefaultPersistedQueriesLoader(caasPersistedQueryConfigurationProperties.getQueryResourcesPattern(),
            caasPersistedQueryConfigurationProperties.getApolloQueryMapResourcesPattern(),
            caasPersistedQueryConfigurationProperties.getRelayQueryMapResourcesPattern(),
            caasPersistedQueryConfigurationProperties.getExcludeFileNamePattern());
  }

  @Bean
  public Map<String, String> persistedQueries(PersistedQueriesLoader persistedQueriesLoader) {
    return persistedQueriesLoader.loadQueries();
  }

  @Bean
  public ContextInstrumentation contextInstrumentation() {
    return new ContextInstrumentation();
  }

  @Bean
  public DataFetcherMappingInstrumentation dataFetchingInstrumentation(SpelEvaluationStrategy spelEvaluationStrategy,
                                                                       @Qualifier("filterPredicate") List<Predicate<Object>> filterPredicates,
                                                                       @Qualifier("graphQlConversionService") ConversionService conversionService,
                                                                       @Qualifier("conversionTypeMap") Map<String, Class<?>> conversionTypeMap,
                                                                       SitesService sitesService
  ) {
    return new DataFetcherMappingInstrumentation((dataFetcher, parameters) ->
            new ConvertingDataFetcher(
                    new FilteringDataFetcher(
                            new ViewBySiteFilterDataFetcher(dataFetcher, sitesService, caasServiceConfigurationProperties),
                            filterPredicates
                    ),
                    conversionService,
                    conversionTypeMap
            )
    );
  }

  @Bean
  @ConditionalOnProperty(prefix = "caas.graphql", name = "max-execution-timeout")
  public ExecutionTimeoutInstrumentation executionTimeoutInstrumentation() {
    long maxExecutionTimeout = caasGraphqlConfigurationProperties.getMaxExecutionTimeout().toMillis();
    LOG.info("caas.graphql.max-execution-timeout: {} ms", maxExecutionTimeout);
    return new ExecutionTimeoutInstrumentation(maxExecutionTimeout);
  }

  @Bean
  @ConditionalOnExpression("#{T(Integer).valueOf(${caas.graphql.max-query-depth:30}) > 0}")
  public MaxQueryDepthInstrumentation maxQueryDepthInstrumentation() {
    int maxQueryDepth = caasGraphqlConfigurationProperties.getMaxQueryDepth();
    LOG.info("caas.graphql.max-query-depth: {}", maxQueryDepth);
    return new MaxQueryDepthInstrumentation(maxQueryDepth);
  }

  @Bean
  @ConditionalOnExpression("#{T(Integer).valueOf(${caas.graphql.max-query-complexity:0}) > 0}")
  public MaxQueryComplexityInstrumentation maxQueryComplexityInstrumentation() {
    int maxQueryComplexity = caasGraphqlConfigurationProperties.getMaxQueryComplexity();
    LOG.info("caas.graphql.max-query-complexity: {}", maxQueryComplexity);
    return new MaxQueryComplexityInstrumentation(maxQueryComplexity);
  }

  @Bean
  public TypeDefinitionRegistry typeDefinitionRegistry()
          throws IOException {
    SchemaParser schemaParser = new SchemaParser();
    PathMatchingResourcePatternResolver loader = new PathMatchingResourcePatternResolver();
    StringBuilder stringBuilder = new StringBuilder();
    Resource[] resources = loader.getResources("classpath*:*-schema.graphql");

    List<Resource> allResources = new ArrayList<>(Arrays.asList(resources));

    if (metadataConfigurationProperties.isEnabled()) {
      Resource[] metadataResources = loader.getResources("classpath*:graphql/metadata/metadata-schema.graphql");
      allResources.addAll(Arrays.asList(metadataResources));
    }

    List<String> additionalSchemaLocations = caasServiceConfigurationProperties.getAdditionSchemaLocations();
    if (additionalSchemaLocations != null) {
      for (String additionalLocation : additionalSchemaLocations) {
        Resource[] additionalResources = loader.getResources(additionalLocation);
        allResources.addAll(Arrays.asList(additionalResources));
      }
    }

    for (Resource resource : allResources) {
      LOG.info("merging GraphQL schema {}", resource.getURI());
      try (InputStreamReader in = new InputStreamReader(resource.getInputStream())) {
        stringBuilder.append(IOUtils.toString(in));
      } catch (IOException e) {
        throw new IOException("There was an error while reading the schema file " + resource.getFilename(), e);
      }
    }
    return schemaParser.parse(stringBuilder.toString());
  }

  @Bean
  public GraphQLSchema graphQLSchema(Map<String, SchemaDirectiveWiring> directiveWirings,
                                     TypeDefinitionRegistry typeRegistry,
                                     @Qualifier("rootModelMapper") ModelMapper<Object, Object> modelMapper,
                                     List<WiringFactory> wiringFactories)
          throws IOException {
    WiringFactory wiringFactory = new ModelMappingWiringFactory(modelMapper, wiringFactories);
    RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring()
            .wiringFactory(wiringFactory);
    directiveWirings.forEach(builder::directive);
    RuntimeWiring wiring = builder.build();
    SchemaGenerator schemaGenerator = new SchemaGenerator();
    return schemaGenerator.makeExecutableSchema(typeRegistry, wiring);
  }

  @Bean
  public PreparsedDocumentProvider preparsedDocumentProvider(CacheManager cacheManager) {
    return new PreparsedDocumentProvider() {
      Cache cache = cacheManager.getCache(CacheInstances.PREPARSED_DOCUMENTS);
      Function<ExecutionInput, PreparsedDocumentEntry> computeFunction = executionInput -> new PreparsedDocumentEntry(
              graphql.parser.Parser.parse(executionInput.getQuery())
      );

      @Override
      public PreparsedDocumentEntry getDocument(ExecutionInput executionInput, Function<ExecutionInput, PreparsedDocumentEntry> computeFunction) {
        return cache.get(executionInput.getQuery(), () -> computeFunction.apply(executionInput));
      }
    };
  }

  @Bean
  public GraphQL graphQL(GraphQLSchema graphQLSchema,
                         PreparsedDocumentProvider preparsedDocumentProvider,
                         List<Instrumentation> instrumentations) {
    return GraphQL.newGraphQL(graphQLSchema)
            .instrumentation(new ChainedInstrumentation(instrumentations))
            .preparsedDocumentProvider(preparsedDocumentProvider)
            .build();
  }

  @Bean
  @Qualifier("conversionTypeMap")
  public Map<String, Class<?>> conversionTypeMap() {

    /* add corresponding custom scalar types from content-schema.graphql here */
    return new ImmutableMap.Builder<String, Class<?>>()
            .put("MapOfString", Map.class)
            .put("MapOfInt", Map.class)
            .put("MapOfLong", Map.class)
            .put("MapOfFloat", Map.class)
            .put("MapOfBoolean", Map.class)
            .put("RichTextTree", Map.class)
            .put("JSON", Map.class)
            .put("BigDecimal", BigDecimal.class)
            .build();
  }

  @Bean
  public GraphQLScalarType MapOfString(ConversionService conversionService) {
    return GraphQLScalarType.newScalar().name("MapOfString").description("Built-in map of scalar type").coercing(new CoercingMap<>(String.class, conversionService)).build();
  }

  @Bean
  public GraphQLScalarType MapOfInt(ConversionService conversionService) {
    return GraphQLScalarType.newScalar().name("MapOfInt").description("Map of Integer").coercing(new CoercingMap<>(Integer.class, conversionService)).build();
  }

  @Bean
  public GraphQLScalarType MapOfLong(ConversionService conversionService) {
    return GraphQLScalarType.newScalar().name("MapOfLong").description("Map of Long").coercing(new CoercingMap<>(Long.class, conversionService)).build();
  }

  @Bean
  public GraphQLScalarType MapOfFloat(ConversionService conversionService) {
    return GraphQLScalarType.newScalar().name("MapOfFloat").description("Map of Float").coercing(new CoercingMap<>(Double.class, conversionService)).build();
  }

  @Bean
  public GraphQLScalarType MapOfBoolean(ConversionService conversionService) {
    return GraphQLScalarType.newScalar().name("MapOfBoolean").description("Map of Boolean").coercing(new CoercingMap<>(Boolean.class, conversionService)).build();
  }

  @Bean
  public GraphQLScalarType RichTextTree() {
    return GraphQLScalarType.newScalar().name("RichTextTree").description("Built-in rich text object tree").coercing(new CoercingRichTextTree()).build();
  }

  @Bean
  public GraphQLScalarType JSON() {
    return ExtendedScalars.Json;
  }

  @Bean
  public GraphQLScalarType BigDecimal() {
    return ExtendedScalars.GraphQLBigDecimal;
  }

  private Map<String, Object> renameQueryRootsWithOptionalPrefix(Map<String, Object> queryRoots) {
    Map<String, Object> renamedQueryRoots = new LinkedHashMap<>(queryRoots.size());
    for (var rootEntry : queryRoots.entrySet()) {
      String name = rootEntry.getKey();
      if (name.startsWith(OPTIONAL_QUERY_ROOT_BEAN_NAME_PREFIX)) {
        name = name.substring(OPTIONAL_QUERY_ROOT_BEAN_NAME_PREFIX.length());
        LOG.info("adding GraphQL query root {} (renamed from {})", name, rootEntry.getKey());
      } else {
        LOG.info("adding GraphQL query root {}", name);
      }
      renamedQueryRoots.put(name, rootEntry.getValue());
    }
    return renamedQueryRoots;
  }

  @Bean
  public DataLoaderRegistry dataLoaderRegistry(Map<String, DataLoader<String, Try<String>>> dataLoaders) {
    DataLoaderRegistry registry = new DataLoaderRegistry();
    dataLoaders.forEach(registry::register);
    return registry;
  }

  @Bean
  public DataLoader<String, Try<String>> remoteLinkDataLoader(RemoteServiceAdapterFactory rsa, CacheManager cacheManager) {

    BatchLoaderWithContext<String, Try<String>> batchLoader = new BatchLoaderWithContext<String, Try<String>>() {
      @Override
      public CompletionStage<List<Try<String>>> load(List<String> keys, BatchLoaderEnvironment environment) {

        environment.getKeyContexts().put(FORWARD_HEADER_MAP, CaasWebConfig.getForwardHeaderMap());
        return CompletableFuture.supplyAsync(() -> {
          return rsa.to().formatLinks(keys.stream().map(key -> {
            return UrlServiceRequestParams.create(
                    key,
                    (String) ((Map) environment.getKeyContexts().get(key)).get("siteId"),
                    (String) ((Map) environment.getKeyContexts().get(key)).get("context")
            );
          }).collect(Collectors.toList()), (Map<String, String>) environment.getKeyContexts().get(FORWARD_HEADER_MAP));
        });

      }
    };

    org.springframework.cache.Cache remoteLinkCache = cacheManager.getCache(CacheInstances.REMOTE_LINKS);
    DataLoaderOptions options;
    if (remoteLinkCache != null) {
      options = DataLoaderOptions.newOptions().setCacheMap(new CacheMapWrapper(remoteLinkCache));
    } else {
      options = DataLoaderOptions.newOptions().setCachingEnabled(false);
      LOG.warn("No configuration for caffeine cache '{}' found. Caching disabled!", CacheInstances.REMOTE_LINKS);
    }
    return DataLoader.newDataLoader(batchLoader, options);
  }

}
