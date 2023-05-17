package com.coremedia.blueprint.elastic.social.cae.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BlobRefImplTest {

  @Test
  void test() {
    String id = "id";
    BlobRefImpl ref = new BlobRefImpl(id);

    assertThat(ref.getId()).isEqualTo(id);
  }
}
