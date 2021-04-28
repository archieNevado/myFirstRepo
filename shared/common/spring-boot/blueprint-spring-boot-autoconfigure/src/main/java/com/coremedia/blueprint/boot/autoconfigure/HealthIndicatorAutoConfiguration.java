package com.coremedia.blueprint.boot.autoconfigure;

import com.coremedia.cap.common.CapConnection;
import com.coremedia.elastic.core.api.search.SearchService;
import com.coremedia.elastic.core.mongodb.settings.MongoDbSettings;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import org.apache.solr.client.solrj.SolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.solr.SolrHealthIndicator;
import org.springframework.boot.actuate.system.DiskSpaceHealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.inject.Named;
import java.io.File;

@Configuration(proxyBeanMethods = false)
public class HealthIndicatorAutoConfiguration {

  // by using the @Named annotation we can prevent spring from prefixing the json key in the health servlet with the
  // surrounding class name, i.e. 'uapiConnection' instead of
  // 'com.coremedia.blueprint.boot.autoconfigure.HealthIndicatorAutoConfiguration$uapiConnection'
  @Configuration(proxyBeanMethods = false)
  @ConditionalOnBean(CapConnection.class)
  @ConditionalOnClass(CapConnection.class)
  @ConditionalOnEnabledHealthIndicator("uapiConnection")
  @Named("uapiConnectionHealthIndicator")
  @ConditionalOnMissingBean(name = "uapiConnectionHealthIndicator")
  public static class UapiConnectionHealthIndicator implements HealthIndicator {

    private final CapConnection connection;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public UapiConnectionHealthIndicator(CapConnection connection) {
      this.connection = connection;
    }

    @Override
    public Health health() {
      if (!connection.isContentRepositoryAvailable()) {
        return Health.down().withDetail("content repository", "offline").build();
      } else {
        Health.Builder builder = Health.up().withDetail("content repository", "OK");
        if (connection.getManager().isCapListRepositoryRequired()) {
          builder.withDetail("list repository", connection.getManager().isCapListRepositoryAvailable() ? "OK" : "offline");
        }
        if (connection.getManager().isWorkflowRepositoryRequired()) {
          builder.withDetail("workflow repository", connection.getManager().isWorkflowRepositoryAvailable() ? "OK" : "offline");
        }
        return builder.build();
      }
    }
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnBean(CapConnection.class)
  @ConditionalOnClass(CapConnection.class)
  @ConditionalOnEnabledHealthIndicator("uapiConnectionReadiness")
  @Named("uapiConnectionReadinessHealthIndicator")
  @ConditionalOnMissingBean(name = "uapiConnectionReadinessHealthIndicator")
  public static class UapiConnectionReadinessHealthIndicator implements HealthIndicator {

    private final CapConnection connection;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public UapiConnectionReadinessHealthIndicator(CapConnection connection) {
      this.connection = connection;
    }

    @Override
    public Health health() {
      Health connectionHealth;
      if (!connection.isContentRepositoryAvailable()) {
        return Health.down().withDetail("content repository", "offline").build();
      } else if (connection.isContentRepositoryToBeUnavailable()) {
        return Health.down().withDetail("content repository", "will be offline soon").build();
      } else {
        return Health.up().withDetail("content repository", "OK").build();
      }
    }
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnBean(MongoDbSettings.class)
  @ConditionalOnClass(MongoDbSettings.class)
  @ConditionalOnEnabledHealthIndicator("mongoDb")
  @Named("mongoDbHealthIndicator")
  @ConditionalOnMissingBean(name = "mongoDbHealthIndicator")
  public static class MongoDbHealthIndicator implements HealthIndicator {

    private final MongoDbSettings mongoDbSettings;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public MongoDbHealthIndicator(MongoDbSettings mongoDbSettings) {
      this.mongoDbSettings = mongoDbSettings;
    }

    @Override
    public Health health() {
      MongoClient client = null;
      Health health = Health.down().withDetail("mongo", "offline").build();
      try {
        client = new MongoClient(mongoDbSettings.getMongoClientURI());
        health = Health.up().withDetail("mongo", "OK").build();
        ;
      } catch (MongoException ignored) {
      } finally {
        if (client != null) {
          client.close();
        }
      }
      return health;
    }
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnBean(name = "elasticCoreSolrClient", value = SolrClient.class)
  @ConditionalOnClass({SolrClient.class, SearchService.class})
  @ConditionalOnEnabledHealthIndicator("elasticSolr")
  public static class ElasticSolrHealthIndicatorConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "elasticSolrHealthIndicator")
    public SolrHealthIndicator elasticSolrHealthIndicator(SolrClient elasticCoreSolrClient) {
      return new SolrHealthIndicator(elasticCoreSolrClient);
    }
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnBean(name = "solrClient", value = SolrClient.class)
  @ConditionalOnClass(SolrClient.class)
  @ConditionalOnEnabledHealthIndicator("contentSolr")
  public static class ContentSolrHealthIndicatorConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "contentSolrHealthIndicator")
    public SolrHealthIndicator contentSolrHealthIndicator(SolrClient solrClient) {
      return new SolrHealthIndicator(solrClient);
    }
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnBean(name = "connection", value = CapConnection.class)
  @ConditionalOnClass(CapConnection.class)
  @ConditionalOnEnabledHealthIndicator("blobCacheDiskSpace")
  public static class BlobCacheDiskSpaceHealthIndicatorConfiguration {
    @Value("${management.health.blobCacheDiskspace.threshold:52428800}")
    private long diskspaceThreshold;

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public DiskSpaceHealthIndicator blobCacheDiskSpaceHealthIndicator(CapConnection connection) {
      File blobCachePath = new File(connection.getManager().getBlobCachePath());
      return new DiskSpaceHealthIndicator(blobCachePath, DataSize.ofBytes(diskspaceThreshold));
    }
  }
}
