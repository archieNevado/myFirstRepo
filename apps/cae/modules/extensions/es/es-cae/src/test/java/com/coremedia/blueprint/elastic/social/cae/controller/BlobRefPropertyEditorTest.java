package com.coremedia.blueprint.elastic.social.cae.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

class BlobRefPropertyEditorTest {

  @Test
  void test() {
    BlobRefPropertyEditor editor = new BlobRefPropertyEditor();
    editor.setAsText("123");

    Object value = editor.getValue();

    assertThat(value)
            .asInstanceOf(type(BlobRef.class))
            .returns("123", from(BlobRef::getId));
  }
}
