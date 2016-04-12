package com.coremedia.livecontext.sitemap;

import org.springframework.web.util.UriComponentsBuilder;

class WcsPcsCrawlSitemapRenderer extends WcsCrawlSitemapRenderer {
  @Override
  protected String toCrawlurl(String url) {
    // crawl url must be http because commerce crawler can not handle https
    url = UriComponentsBuilder.fromUriString(url).scheme("http").queryParam("view", "forCrawler").build().toUriString();
    return super.toCrawlurl(url);
  }
}
