package com.coremedia.livecontext.ecommerce.sfcc.cae.preview;

import com.coremedia.objectserver.web.links.LinkTransformer;
import com.coremedia.objectserver.web.links.ParameterAppendingLinkTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

import static com.coremedia.livecontext.ecommerce.sfcc.push.PushServiceImpl.PREVIEW_PARAMETER;
import static com.coremedia.livecontext.ecommerce.sfcc.push.PushServiceImpl.PUSH_MODE_PARAMETER;

@Configuration
public class SfccPreviewCaeConfiguration {

  @Bean
  SiteDateFilter siteDateFilter() {
    return new SiteDateFilter();
  }

  @Bean
  SfccDelegatingLinkTransformer sfccDelegatingLinkTransformer(){
    //list of sfcc preview link transformers
    List<LinkTransformer> linkTransformerList = new ArrayList<>();
    linkTransformerList.add(siteDateAppendingLinkTransformer());
    linkTransformerList.add(userSegmentLinkTransformer());
    linkTransformerList.add(previewParamLinkTransformer());
    linkTransformerList.add(pushModeParamLinkTransformer());

    return new SfccDelegatingLinkTransformer(linkTransformerList);
  }

  /**
   * Creates a CAE LinkTransformer that rewrites all generated links
   * and adds the request parameter for the site  date.
   * No need to expose as separate bean. Further initialization hooks are skipped.
   */
  private SiteDateAppendingLinkTransformer siteDateAppendingLinkTransformer() {
    return new SiteDateAppendingLinkTransformer();
  }

  /**
   * Creates a CAE LinkTransformer that rewrites all generated links and
   * adds the request parameter for the Customer Segment.
   * No need to expose as separate bean. Further initialization hooks are skipped.
   */
  private UserSegmentAppendingLinkTransformer userSegmentLinkTransformer() {
    return new UserSegmentAppendingLinkTransformer();
  }

  /**
   * Parameter is processed by the coremedia sfcc cartridge.
   * If set to true, all fragments are requested from the preview cae.
   * If set to false, all fragments are requested from the live cae.
   * No need to expose as separate bean. Further initialization hooks are skipped.
   */
  private ParameterAppendingLinkTransformer previewParamLinkTransformer() {
    ParameterAppendingLinkTransformer linkTransformer = new ParameterAppendingLinkTransformer();
    linkTransformer.setParameterName(PREVIEW_PARAMETER);
    return linkTransformer;
  }

  /**
   * Parameter is processed by the coremedia sfcc cartridge.
   * If set to true (default):
   *  - the vtl include placeholders are processed and substituted in fragment payload.
   *  - the fragment payload may be directly delivered from content assets stored on the sfcc commerce system
   * If set to false:
   *  - the vtl include placeholders are not processed during fragment processing.
   *  - the payload is always requested from the configured cae fragment host
   *
   * No need to expose as separate bean. Further initialization hooks are skipped.
   */
  private ParameterAppendingLinkTransformer pushModeParamLinkTransformer(){
    ParameterAppendingLinkTransformer linkTransformer = new ParameterAppendingLinkTransformer();
    linkTransformer.setParameterName(PUSH_MODE_PARAMETER);
    return linkTransformer;
  }

}
