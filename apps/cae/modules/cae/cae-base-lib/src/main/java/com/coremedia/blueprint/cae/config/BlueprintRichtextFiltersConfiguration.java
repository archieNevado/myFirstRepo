package com.coremedia.blueprint.cae.config;

import com.coremedia.blueprint.cae.richtext.filter.AppendClassToElementFilter;
import com.coremedia.blueprint.cae.richtext.filter.CMDownloadLinkValidationFilter;
import com.coremedia.blueprint.cae.richtext.filter.ConfigurableRichtextToHtmlFilterFactory;
import com.coremedia.blueprint.cae.richtext.filter.FilterFactory;
import com.coremedia.blueprint.cae.richtext.filter.ImageFilter;
import com.coremedia.blueprint.cae.richtext.filter.ImgCompletionFilter;
import com.coremedia.blueprint.cae.richtext.filter.LinkEmbedFilter;
import com.coremedia.blueprint.cae.richtext.filter.LinkValidationFilter;
import com.coremedia.blueprint.cae.richtext.filter.P2TagFilter;
import com.coremedia.blueprint.cae.richtext.filter.UnsurroundFilter;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.id.IdProvider;
import com.coremedia.id.IdServicesConfiguration;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.objectserver.view.config.CaeViewServicesConfiguration;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.coremedia.springframework.customizer.Customize;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@Import({
        IdServicesConfiguration.class,
        CaeViewServicesConfiguration.class,
})
public class BlueprintRichtextFiltersConfiguration {

  /**
   * Predefined programmed view for markup of grammar "coremedia-richtext-1.0"
   */
  @Bean(autowireCandidate = false)
  @Customize(value = "richtextMarkupView.xmlFilterFactory", mode = Customize.Mode.REPLACE)
  @Order(10_000)
  ConfigurableRichtextToHtmlFilterFactory configurableRichtextToHtmlFilterFactory(IdProvider idProvider,
                                                                                  LinkFormatter linkFormatter,
                                                                                  LinkValidationFilter linkValidationFilter,
                                                                                  CMDownloadLinkValidationFilter cmDownloadLinkValidationFilter,
                                                                                  ImgCompletionFilter imgCompletionFilter,
                                                                                  LinkEmbedFilter linkEmbedFilter,
                                                                                  FilterFactory imageFilter, FilterFactory p2TagFilter, FilterFactory appendClassToElementFilter, FilterFactory unsurroundFilter) {
    ConfigurableRichtextToHtmlFilterFactory factory = new ConfigurableRichtextToHtmlFilterFactory();
    factory.setIdProvider(idProvider);
    factory.setLinkFormatter(linkFormatter);
    factory.setXmlFiltersBeforeUriFormatter(List.of(
            linkValidationFilter,
            cmDownloadLinkValidationFilter,
            imgCompletionFilter,
            linkEmbedFilter,
            imageFilter
    ));
    factory.setXmlFilters(List.of(
            p2TagFilter,
            appendClassToElementFilter,
            unsurroundFilter
    ));
    return factory;
  }

  /**
   * Suppresses links to CMDownloads not containing blob data.
   */
  @Bean
  LinkValidationFilter linkValidationFilter(IdProvider idProvider,
                                            DataViewFactory dataViewFactory,
                                            ValidationService<Object> validationService) {
    LinkValidationFilter filter = new LinkValidationFilter();
    filter.setIdProvider(idProvider);
    filter.setDataViewFactory(dataViewFactory);
    filter.setValidationService(validationService);
    filter.setRenderLinkText(false);
    return filter;
  }

  /**
   * Suppresses links to invalid content.
   */
  @Bean
  CMDownloadLinkValidationFilter cmDownloadLinkValidationFilter(IdProvider idProvider,
                                                                DataViewFactory dataViewFactory,
                                                                ValidationService validationService) {
    CMDownloadLinkValidationFilter filter = new CMDownloadLinkValidationFilter();
    filter.setIdProvider(idProvider);
    filter.setDataViewFactory(dataViewFactory);
    filter.setValidationService(validationService);
    return filter;
  }

  /**
   * Amends missing img attributes.
   */
  @Bean
  ImgCompletionFilter imgCompletionFilter(IdProvider idProvider,
                                          ContentBeanFactory contentBeanFactory) {
    ImgCompletionFilter filter = new ImgCompletionFilter();
    filter.setIdProvider(idProvider);
    filter.setContentBeanFactory(contentBeanFactory);
    return filter;
  }

  /**
   * Enables embedding of images by rendering theses documents with the view "asRichtextEmbed".
   */
  @Bean
  ImageFilter imageFilter(ContentRepository contentRepository,
                          ContentBeanFactory contentBeanFactory,
                          DataViewFactory dataViewFactory,
                          ValidationService validationService) {
    ImageFilter filter = new ImageFilter();
    filter.setContentRepository(contentRepository);
    filter.setContentBeanFactory(contentBeanFactory);
    filter.setDataViewFactory(dataViewFactory);
    filter.setValidationService(validationService);
    return filter;
  }

  /**
   * Enables embedding of linked documents by rendering theses documents with the view "asRichttextEmbed".
   */
  @Bean
  LinkEmbedFilter linkEmbedFilter(IdProvider idProvider,
                                  DataViewFactory dataViewFactory) {
    LinkEmbedFilter filter = new LinkEmbedFilter();
    filter.setIdProvider(idProvider);
    filter.setDataViewFactory(dataViewFactory);
    return filter;
  }

  @Bean
  AppendClassToElementFilter appendClassToElementFilter() {
    AppendClassToElementFilter filter = new AppendClassToElementFilter();
    filter.setElementList(Map.of(
            "ul", "rte--list",
            "ol", "rte--list"
    ));
    return filter;
  }

  /**
   * Converts p-paragraphs with formatting that has native HTML elements to their
   * respective HTML element.
   */
  @Bean
  P2TagFilter p2TagFilter() {
    P2TagFilter filter = new P2TagFilter();
    filter.setMapping(Map.of(
       "p--heading-1", "h1",
       "p--heading-2", "h2",
       "p--heading-3", "h3",
       "p--heading-4", "h4",
       "p--heading-5", "h5",
       "p--heading-6", "h6",
       "p--heading-7", "h7",
       "p--heading-8", "h8",
       "p--standard", "p",
       "p--pre", "pre"
    ));
    return filter;
  }

  /**
   * Removes surrounding divs from coremedia-richtext-1.0.
   */
  @Bean
  UnsurroundFilter unsurroundFilter() {
    return new UnsurroundFilter();
  }

}
