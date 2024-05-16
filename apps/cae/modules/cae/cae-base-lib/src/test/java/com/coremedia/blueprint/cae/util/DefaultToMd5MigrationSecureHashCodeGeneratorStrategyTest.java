package com.coremedia.blueprint.cae.util;

import com.coremedia.objectserver.view.dynamic.CaeSecretConfigurationProperties;
import com.coremedia.objectserver.view.dynamic.MD5SecureHashCodeGeneratorStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {
                DefaultToMd5MigrationSecureHashCodeGeneratorStrategyTest.LocalConfig.class,
        },
        properties = "cae.hashing.migration-mode=true")
class DefaultToMd5MigrationSecureHashCodeGeneratorStrategyTest {

  @Autowired
  private MD5SecureHashCodeGeneratorStrategy md5SecureHashCodeGeneratorStrategy;

  @Autowired
  private DefaultSecureHashCodeGeneratorStrategy defaultSecureHashCodeGeneratorStrategy;

  @Autowired
  private DefaultToMd5MigrationSecureHashCodeGeneratorStrategy migrationStrategy;

  @Test
  void hashCodesOfBothStrategiesShouldMatch() {
    //GIVEN
    Map<String, Object> input = Map.of("test", "value");
    String legacyHashCode = defaultSecureHashCodeGeneratorStrategy.generateSecureHashCode(input);
    String md5HashCode = md5SecureHashCodeGeneratorStrategy.generateSecureHashCode(input);

    //THEN
    assertThat(migrationStrategy.matches(input, legacyHashCode)).isTrue();
    assertThat(migrationStrategy.matches(input, md5HashCode)).isTrue();
  }

  @Test
  void hashShouldBeGeneratedWithMD() {
    //GIVEN
    Map<String, Object> input = Map.of("test", "value");
    String migrationStrategyHashCode = migrationStrategy.generateSecureHashCode(input);

    //THEN
    assertThat(defaultSecureHashCodeGeneratorStrategy.matches(input, migrationStrategyHashCode)).isFalse();
    assertThat(migrationStrategy.matches(input, migrationStrategyHashCode)).isTrue();
    assertThat(md5SecureHashCodeGeneratorStrategy.matches(input, migrationStrategyHashCode)).isTrue();
  }

  @Configuration
  @EnableConfigurationProperties(CaeSecretConfigurationProperties.class)
  public static class LocalConfig {

    @Bean
    @ConditionalOnMissingBean(name = "mD5SecureHashCodeGeneratorStrategy")
    public MD5SecureHashCodeGeneratorStrategy mD5SecureHashCodeGeneratorStrategy(CaeSecretConfigurationProperties secretConfigurationProperties) {
      return new MD5SecureHashCodeGeneratorStrategy(secretConfigurationProperties);
    }

    @Bean
    @ConditionalOnExpression("${cae.hashing.backward-compatibility:false} || ${cae.hashing.migration-mode:false}")
    public DefaultSecureHashCodeGeneratorStrategy secureHashCodeGeneratorStrategy() {
      return new DefaultSecureHashCodeGeneratorStrategy();
    }

    @Bean
    @Primary
    @ConditionalOnProperty("cae.hashing.migration-mode")
    public DefaultToMd5MigrationSecureHashCodeGeneratorStrategy defaultToMd5MigrationSecureHashCodeGeneratorStrategy(
            DefaultSecureHashCodeGeneratorStrategy defaultSecureHashCodeGeneratorStrategy,
            MD5SecureHashCodeGeneratorStrategy md5SecureHashCodeGeneratorStrategy) {
      return new DefaultToMd5MigrationSecureHashCodeGeneratorStrategy(defaultSecureHashCodeGeneratorStrategy, md5SecureHashCodeGeneratorStrategy);
    }
  }

}
