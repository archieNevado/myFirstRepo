package com.coremedia.cms.middle.blueprint.validators;

import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridService;
import com.coremedia.blueprint.base.rest.validators.AbstractCodeValidator;
import com.coremedia.blueprint.base.rest.validators.ArchiveValidator;
import com.coremedia.blueprint.base.rest.validators.AtLeastOneNotEmptyValidator;
import com.coremedia.blueprint.base.rest.validators.ChannelIsPartOfNavigationValidator;
import com.coremedia.blueprint.base.rest.validators.ChannelNavigationValidator;
import com.coremedia.blueprint.base.rest.validators.ChannelReferrerValidator;
import com.coremedia.blueprint.base.rest.validators.ChannelSegmentValidator;
import com.coremedia.blueprint.base.rest.validators.ConfigurableDeadLinkValidator;
import com.coremedia.blueprint.base.rest.validators.IsPartOfNavigationValidator;
import com.coremedia.blueprint.base.rest.validators.NavigationValidatorsConfigurationProperties;
import com.coremedia.blueprint.base.rest.validators.NotEmptyMarkupValidator;
import com.coremedia.blueprint.base.rest.validators.PlacementsValidator;
import com.coremedia.blueprint.base.rest.validators.RootChannelSegmentValidator;
import com.coremedia.blueprint.base.rest.validators.SelfReferringLinkListValidator;
import com.coremedia.blueprint.base.rest.validators.TimelineValidator;
import com.coremedia.blueprint.base.rest.validators.ValidityValidator;
import com.coremedia.blueprint.base.rest.validators.VisibilityValidator;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.SiteModel;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.transform.TransformImageService;
import com.coremedia.cap.transform.TransformImageServiceConfiguration;
import com.coremedia.cap.undoc.common.spring.CapRepositoriesConfiguration;
import com.coremedia.image.ImageDimensionsExtractor;
import com.coremedia.rest.cap.validation.ContentTypeValidator;
import com.coremedia.rest.cap.validators.AvailableLocalesConfigurationProperties;
import com.coremedia.rest.cap.validators.AvailableLocalesValidator;
import com.coremedia.rest.cap.validators.ContentLocaleMatchesSiteLocaleValidator;
import com.coremedia.rest.cap.validators.CrossSiteLinkValidator;
import com.coremedia.rest.cap.validators.DuplicateDerivedInSiteValidator;
import com.coremedia.rest.cap.validators.ImageCropSizeValidator;
import com.coremedia.rest.cap.validators.ImageMapAreasValidator;
import com.coremedia.rest.cap.validators.ImageMapOverlayConfigurationValidator;
import com.coremedia.rest.cap.validators.LinkListMaxLengthValidator;
import com.coremedia.rest.cap.validators.MasterVersionUpdatedValidator;
import com.coremedia.rest.cap.validators.PossiblyMissingMasterReferenceValidator;
import com.coremedia.rest.cap.validators.SameMasterLinkValidator;
import com.coremedia.rest.cap.validators.SelfReferringStructLinkListValidator;
import com.coremedia.rest.cap.validators.SiteManagerGroupValidator;
import com.coremedia.rest.cap.validators.SiteNameValidator;
import com.coremedia.rest.cap.validators.StructLinkListIndexValidator;
import com.coremedia.rest.cap.validators.StructLinkListMaxLengthValidator;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;
import com.coremedia.rest.validators.EmailValidator;
import com.coremedia.rest.validators.ListMinLengthValidator;
import com.coremedia.rest.validators.NotEmptyValidator;
import com.coremedia.rest.validators.RegExpValidator;
import com.coremedia.rest.validators.UrlValidator;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Spring Configuration for Validators.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        AvailableLocalesConfigurationProperties.class,
        NavigationValidatorsConfigurationProperties.class
})
@Import({
        CapRepositoriesConfiguration.class,
        TransformImageServiceConfiguration.class
})
@ImportResource(
        value = {
                "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml",
                "classpath:/com/coremedia/blueprint/base/links/bpbase-links-services.xml",
                "classpath:/com/coremedia/blueprint/base/pagegrid/impl/bpbase-pagegrid-services.xml",
                // blueprint-segments.xml configures ContentSegmentStrategy instances for the ChannelSegmentValidator
                "classpath:/com/coremedia/blueprint/segments/blueprint-segments.xml",
                // mediatransform.xml provides configuration for the ImageCropSizeValidator
                "classpath:/framework/spring/mediatransform.xml"
        },
        reader = ResourceAwareXmlBeanDefinitionReader.class)
public class ValidatorsConfiguration {

  private static final String CM_LOCALIZED = "CMLocalized";

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.available-locales-validator.available-locales", matchIfMissing = true)
  AvailableLocalesValidator availableLocalesValidator(AvailableLocalesConfigurationProperties availableLocalesConfigurationProperties) {
    return new AvailableLocalesValidator(
            availableLocalesConfigurationProperties.getContentPath(),
            availableLocalesConfigurationProperties.getPropertyPath()
    );
  }

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Bean
  @ConditionalOnProperty(name = "validator.enabled.site-manager-group-validator.site-manager-group", matchIfMissing = true)
  SiteManagerGroupValidator siteManagerGroupValidator(CapConnection connection,
                                                      SiteModel siteModel) {
    return new SiteManagerGroupValidator(connection, siteModel);
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.content-type-validator.cm-localized-master-length", matchIfMissing = true)
  ContentTypeValidator cmLocalizedValidator(CapConnection connection) {
    return new ContentTypeValidator(type(connection, CM_LOCALIZED),
                                    true,
                                    List.of(new LinkListMaxLengthValidator("master", Set.of(Issues.LOCALIZATION_ISSUE_CATEGORY))));
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.content-type-validator.cm-teaser-targets-length", matchIfMissing = true)
  ContentTypeValidator cmTeaserValidator(CapConnection connection) {
    return new ContentTypeValidator(type(connection, "CMTeaser"),
                                    false,
                                    List.of(new StructLinkListMaxLengthValidator("targets", "links")));
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.content-type-validator.cm-picture-data-not-empty", matchIfMissing = true)
  ContentTypeValidator cmPictureValidator(CapConnection connection) {
    return new ContentTypeValidator(type(connection, "CMPicture"), true, List.of(new NotEmptyValidator("data")));
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.image-map-areas-validator.cm-image-map", matchIfMissing = true)
  ImageMapAreasValidator cmImageMapAreasValidator(CapConnection connection) {
    return new ImageMapAreasValidator(type(connection, "CMImageMap"), true, "localSettings", "pictures.data");
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.image-map-overlay-configuration-validator.cm-image-map", matchIfMissing = true)
  ImageMapOverlayConfigurationValidator cmImageMapOverlayConfigurationValidator(CapConnection connection) {
    return new ImageMapOverlayConfigurationValidator(type(connection, "CMImageMap"), true, "localSettings");
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.self-referring-link-list-validator.cm-linkable", matchIfMissing = true)
  SelfReferringLinkListValidator cmLinkListValidator(CapConnection connection) {
    return new SelfReferringLinkListValidator(type(connection, "CMLinkable"), true);
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.self-referring-struct-link-list-validator.cm-linkable", matchIfMissing = true)
  SelfReferringStructLinkListValidator cmStructLinkListValidator(CapConnection connection) {
    return new SelfReferringStructLinkListValidator(type(connection, "CMLinkable"), true);
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.struct-link-list-index-validator.cm-query-list", matchIfMissing = true)
  StructLinkListIndexValidator cmQueryListIndexValidator(CapConnection connection) {
    StructLinkListIndexValidator cmQueryListIndexValidator = new StructLinkListIndexValidator(type(connection, "CMQueryList"), false);
    cmQueryListIndexValidator.setPropertyName("extendedItems");
    cmQueryListIndexValidator.setListPropertyName("links");
    cmQueryListIndexValidator.setIndexPropertyName("index");
    cmQueryListIndexValidator.setMaxLengthPropertyName("limit");
    cmQueryListIndexValidator.setPaginationPropertyName("loadMore");
    return cmQueryListIndexValidator;
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.content-type-validator.cm-channel-title-not-empty", matchIfMissing = true)
  ContentTypeValidator cmChannelValidator(CapConnection connection) {
    return new ContentTypeValidator(type(connection, "CMChannel"),
                                    true,
                                    List.of(new NotEmptyValidator("title")));
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.content-type-validator.cm-viewtype-layout-not-empty", matchIfMissing = true)
  ContentTypeValidator cmViewtype(CapConnection connection) {
    return new ContentTypeValidator(type(connection, "CMViewtype"),
                                    true,
                                    List.of(new NotEmptyValidator("layout")));
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.channel-segment-validator.cm-channel", matchIfMissing = true)
  ChannelSegmentValidator cmChannelSegmentValidator(UrlPathFormattingHelper urlPathFormattingHelper,
                                                    CapConnection connection) {
    ChannelSegmentValidator validator = new ChannelSegmentValidator(type(connection, "CMChannel"), false);
    validator.setUrlPathFormattingHelper(urlPathFormattingHelper);
    return validator;
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.root-channel-segment-validator.cm-channel", matchIfMissing = true)
  RootChannelSegmentValidator cmChannelRootSegmentValidator(UrlPathFormattingHelper urlPathFormattingHelper,
                                                            SitesService sitesService,
                                                            CapConnection connection) {
    RootChannelSegmentValidator validator = new RootChannelSegmentValidator(type(connection, "CMChannel"), true);
    validator.setSitesService(sitesService);
    validator.setUrlPathFormattingHelper(urlPathFormattingHelper);
    return validator;
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.archive-validator.cm-template-set", matchIfMissing = true)
  ArchiveValidator cmArchiveValidator(CapConnection connection) {
    return new ArchiveValidator(type(connection, "CMTemplateSet"), false, "archive");
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.validity-validator.document", matchIfMissing = true)
  ValidityValidator cmValidityValidator(CapConnection connection) {
    ValidityValidator cmValidityValidator = new ValidityValidator(type(connection, null), true);
    cmValidityValidator.setPropertyValidFrom("validFrom");
    cmValidityValidator.setPropertyValidTo("validTo");
    return cmValidityValidator;
  }

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Bean
  @ConditionalOnProperty(name = "validator.enabled.visibility-validator.cm-channel", matchIfMissing = true)
  VisibilityValidator cmVisibilityValidator(CapConnection connection,
                                            ContentBackedPageGridService contentBackedPageGridService) {
    VisibilityValidator cmVisibilityValidator = new VisibilityValidator(type(connection, "CMChannel"), true);
    cmVisibilityValidator.setPageGridService(contentBackedPageGridService);
    cmVisibilityValidator.setPropertyValidFrom("validFrom");
    cmVisibilityValidator.setPropertyValidTo("validTo");
    cmVisibilityValidator.setPropertyVisibleFrom("visibleFrom");
    cmVisibilityValidator.setPropertyVisibleTo("visibleTo");
    return cmVisibilityValidator;
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.channel-navigation-validator.cm-channel", matchIfMissing = true)
  ChannelNavigationValidator cmChannelNavigationValidator(CapConnection connection) {
    return new ChannelNavigationValidator(type(connection, "CMChannel"), false);
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.channel-is-part-of-navigation-validator.cm-channel", matchIfMissing = true)
  ChannelIsPartOfNavigationValidator cmNotInNavigationValidator(CapConnection connection) {
    return new ChannelIsPartOfNavigationValidator(type(connection, "CMChannel"), false);
  }

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Bean
  @ConditionalOnProperty(name = "validator.enabled.is-part-of-navigation-validator.cm-linkable", matchIfMissing = true)
  IsPartOfNavigationValidator cmNotPartOfNavigationValidator(CapConnection connection,
                                                             ContextStrategy<Content, Content> contentContextStrategy,
                                                             NavigationValidatorsConfigurationProperties navigationValidatorsConfigurationProperties) {
    IsPartOfNavigationValidator cmNotPartOfNavigationValidator =
            new IsPartOfNavigationValidator(type(connection, "CMLinkable"), true);
    cmNotPartOfNavigationValidator.setContextStrategy(contentContextStrategy);
    cmNotPartOfNavigationValidator.setIgnorePaths(navigationValidatorsConfigurationProperties.getIgnorePath());
    return cmNotPartOfNavigationValidator;
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.channel-referrer-validator.cm-channel", matchIfMissing = true)
  ChannelReferrerValidator cmChannelReferrerValidator(CapConnection connection) {
    return new ChannelReferrerValidator(type(connection, "CMChannel"), false);
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.abstract-code-validator.cm-abstract-code", matchIfMissing = true)
  AbstractCodeValidator cmAbstractCodeValidator(CapConnection connection) {
    return new AbstractCodeValidator(type(connection, "CMAbstractCode"), true);
  }

  /**
   * All Document Types with title property not empty validation
   */
  @Bean
  @ConditionalOnProperty(name = "validator.enabled.content-type-validator.cm-article-not-empty", matchIfMissing = true)
  ContentTypeValidator cmArticleValidator(CapConnection connection) {
    return new ContentTypeValidator(type(connection, "CMArticle"),
                                    true,
                                    List.of(new NotEmptyValidator("title"),
                                            new NotEmptyMarkupValidator("detailText")));
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.content-type-validator.cm-person", matchIfMissing = true)
  ContentTypeValidator cmPersonValidator(CapConnection connection) {
    return new ContentTypeValidator(type(connection, "CMPerson"),
                                    true,
                                    List.of(new NotEmptyValidator("firstName"),
                                            new NotEmptyValidator("lastName"),
                                            new EmailValidator("eMail")));
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.master-version-updated-validator.cm-localized", matchIfMissing = true)
  MasterVersionUpdatedValidator masterVersionUpdatedValidator(CapConnection connection,
                                                              SitesService sitesService) {
    return new MasterVersionUpdatedValidator(type(connection, CM_LOCALIZED), true, sitesService);
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.content-locale-matches-site-locale-validator.cm-localized", matchIfMissing = true)
  ContentLocaleMatchesSiteLocaleValidator contentLocaleMatchesSiteLocaleValidator(
          CapConnection connection,
          SitesService sitesService,
          @Value("${contentLocaleMatchesSiteLocaleValidator.severity:WARN}") Severity severity) {
    ContentLocaleMatchesSiteLocaleValidator validator
            = new ContentLocaleMatchesSiteLocaleValidator(type(connection, CM_LOCALIZED), true, sitesService);
    validator.setSeverity(severity);
    return validator;
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.same-master-link-validator.cm-localized", matchIfMissing = true)
  SameMasterLinkValidator sameMasterLinkValidator(
          CapConnection connection,
          SitesService sitesService,
          @Value("${sameMasterLinkValidator.severity:WARN}") Severity severity) {
    SameMasterLinkValidator validator = new SameMasterLinkValidator(type(connection, CM_LOCALIZED), true, sitesService);
    validator.setSeverity(severity);
    return validator;
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.duplicate-derived-in-site-validator.cm-localized", matchIfMissing = true)
  DuplicateDerivedInSiteValidator duplicateDerivedInSiteValidator(
          CapConnection connection,
          SitesService sitesService,
          @Value("${duplicateDerivedInSiteValidator.severity:WARN}") Severity severity) {
    DuplicateDerivedInSiteValidator validator =
            new DuplicateDerivedInSiteValidator(type(connection, CM_LOCALIZED), true, sitesService);
    validator.setSeverity(severity);
    return validator;
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.possibly-missing-master-reference-validator.cm-localized", matchIfMissing = true)
  PossiblyMissingMasterReferenceValidator possiblyMissingMasterReferenceValidator(
          CapConnection connection,
          SitesService sitesService,
          @Value("${possiblyMissingMasterReferenceFromMasterValidator.severity:WARN}") Severity severity,
          @Value("${possiblyMissingMasterReferenceFromMasterValidator.maxIssues:20}") long maxIssues) {
    return new PossiblyMissingMasterReferenceValidator(type(connection, CM_LOCALIZED),
                                                       true,
                                                       sitesService,
                                                       severity,
                                                       maxIssues);
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.cross-site-link-validator.cm-localized", matchIfMissing = true)
  CrossSiteLinkValidator crossSiteLinkValidator(CapConnection connection,
                                                SitesService sitesService,
                                                @Value("WARN") Severity defaultSeverity,
                                                @Value("WARN") Severity severityCrossLocale,
                                                @Value("WARN") Severity severityCrossSite,
                                                @Value("WARN") Severity severityCrossSiteLocale) {
    CrossSiteLinkValidator crossSiteLinkValidator = new CrossSiteLinkValidator(type(connection, CM_LOCALIZED), true, sitesService);
    crossSiteLinkValidator.setExcludedProperties(Collections.singletonList("placement"));
    crossSiteLinkValidator.setDefaultSeverity(defaultSeverity);
    crossSiteLinkValidator.setSeverityCrossLocale(severityCrossLocale);
    crossSiteLinkValidator.setSeverityCrossSite(severityCrossSite);
    crossSiteLinkValidator.setSeverityCrossSiteLocale(severityCrossSiteLocale);
    return crossSiteLinkValidator;
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.configurable-dead-link-validator.document", matchIfMissing = true)
  ConfigurableDeadLinkValidator configurableDeadLinkValidator(CapConnection connection) {
    ConfigurableDeadLinkValidator configurableDeadLinkValidator =
            new ConfigurableDeadLinkValidator(connection.getContentRepository().getDocumentContentType(), true);
    configurableDeadLinkValidator.setExcludedProperties(Collections.singletonList("placement"));
    return configurableDeadLinkValidator;
  }

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Bean
  @ConditionalOnProperty(name = "validator.enabled.placements-validator.cm-channel", matchIfMissing = true)
  PlacementsValidator placementsValidator(CapConnection connection,
                                          SitesService sitesService,
                                          ContentBackedPageGridService contentBackedPageGridService,
                                          @Value("WARN") Severity severityCrossLocale,
                                          @Value("WARN") Severity severityCrossSite,
                                          @Value("WARN") Severity severityCrossSiteLocale,
                                          @Value("ERROR") Severity severityDeadLink) {
    PlacementsValidator placementsValidator = new PlacementsValidator(type(connection, "CMChannel"), true);
    placementsValidator.setSitesService(sitesService);
    placementsValidator.setPageGridService(contentBackedPageGridService);
    placementsValidator.setSeverityCrossLocale(severityCrossLocale);
    placementsValidator.setSeverityCrossSite(severityCrossSite);
    placementsValidator.setSeverityCrossSiteLocale(severityCrossSiteLocale);
    placementsValidator.setSeverityDeadLink(severityDeadLink);
    return placementsValidator;
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.content-type-validator.cm-audio-title-not-empty", matchIfMissing = true)
  ContentTypeValidator cmAudioValidator(CapConnection connection) {
    return new ContentTypeValidator(type(connection, "CMAudio"), true, List.of(new NotEmptyValidator("title")));
  }

  @SuppressWarnings("ProhibitedExceptionDeclared")
  @Bean
  @ConditionalOnProperty(name = "validator.enabled.content-type-validator.cm-download", matchIfMissing = true)
  ContentTypeValidator cmDownloadValidator(CapConnection connection) throws Exception {
    RegExpValidator regExpValidator = new RegExpValidator("filename", "^[^\\\\/:*?\"<>|]*$");
    regExpValidator.setCode("FilenameValidator");
    return new ContentTypeValidator(type(connection, "CMDownload"),
                                    true,
                                    List.of(new NotEmptyValidator("data"),
                                            new NotEmptyValidator("title"),
                                            regExpValidator));
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.content-type-validator.cm-external-link", matchIfMissing = true)
  ContentTypeValidator cmExternalLinkValidator(CapConnection connection) {
    return new ContentTypeValidator(type(connection, "CMExternalLink"),
                                    true,
                                    List.of(new NotEmptyValidator("url"),
                                            new UrlValidator("url", null)));
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.content-type-validator.cm-gallery-title-not-empty", matchIfMissing = true)
  ContentTypeValidator cmGalleryValidator(CapConnection connection) {
    return new ContentTypeValidator(type(connection, "CMGallery"), true, List.of(new NotEmptyValidator("title")));
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.content-type-validator.cm-video-title-not-empty", matchIfMissing = true)
  ContentTypeValidator cmVideoValidator(CapConnection connection) {
    return new ContentTypeValidator(type(connection, "CMVideo"), true, List.of(new NotEmptyValidator("title")));
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.at-least-one-not-empty-validator.cm-video", matchIfMissing = true)
  AtLeastOneNotEmptyValidator atLeastOneNotEmptyValidator(CapConnection connection) {
    AtLeastOneNotEmptyValidator atLeastOneNotEmptyValidator =
            new AtLeastOneNotEmptyValidator(type(connection, "CMVideo"), true);
    atLeastOneNotEmptyValidator.setShowIssueForProperty("data");
    atLeastOneNotEmptyValidator.setExactlyOneMustBeSet(true);
    atLeastOneNotEmptyValidator.setProperties(Arrays.asList("data", "dataUrl"));
    return atLeastOneNotEmptyValidator;
  }

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Bean
  @ConditionalOnProperty(name = "validator.enabled.site-name-validator.cm-site", matchIfMissing = true)
  SiteNameValidator cmSiteValidator(CapConnection connection, SiteModel siteModel) {
    return new SiteNameValidator(type(connection, "CMSite"), true, siteModel);
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.content-type-validator.cm-spinner-sequence-length", matchIfMissing = true)
  ContentTypeValidator cmSpinnerValidator(CapConnection connection) {
    ListMinLengthValidator listMinLengthValidator = new ListMinLengthValidator("sequence");
    listMinLengthValidator.setMinLength(2);
    return new ContentTypeValidator(type(connection, "CMSpinner"), true, List.of(listMinLengthValidator));
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.time-line-validator.cm-video", matchIfMissing = true)
  TimelineValidator cmTimelineValidator(CapConnection connection) {
    TimelineValidator cmTimelineValidator = new TimelineValidator(type(connection, "CMVideo"), true);
    cmTimelineValidator.setAllowSameStartTime(true);
    return cmTimelineValidator;
  }

  @Bean
  @ConditionalOnProperty(name = "validator.enabled.image-crop-size-validator.cm-picture", matchIfMissing = true)
  ImageCropSizeValidator imageCropSizeValidator(CapConnection connection,
                                                TransformImageService transformImageService,
                                                ImageDimensionsExtractor imageDimensionsExtractor) {
    ImageCropSizeValidator validator = new ImageCropSizeValidator(type(connection, "CMPicture"),
            false,
            "localSettings",
            "transforms",
            "data",
            "focusArea");
    validator.setTransformImageService(transformImageService);
    validator.setImageDimensionsExtractor(imageDimensionsExtractor);
    return validator;
  }

  @NonNull
  private static ContentType type(@NonNull CapConnection connection, @Nullable String typeStr) {
    return Objects.requireNonNull(typeStr!=null ?
            connection.getContentRepository().getContentType(typeStr) :
            connection.getContentRepository().getDocumentContentType());
  }
}
