package com.coremedia.blueprint.elastic.social.cae.controller;

import com.coremedia.cap.multisite.SitesService;
import com.coremedia.elastic.social.api.comments.Comment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class HandlerInfoTest {

  private String messageType = "error";
  private String path = "comment";
  private String text = "text";

  @Test
  void test() {
    SitesService sitesService = mock(SitesService.class);

    Comment comment = mock(Comment.class);
    String link = "link";

    HandlerInfo handlerInfo = new HandlerInfo();
    handlerInfo.addMessage(messageType, path, text);
    handlerInfo.setSuccess(false);
    handlerInfo.setModel(comment);
    handlerInfo.setLink(link);

    assertThat(handlerInfo.isSuccess()).isFalse();
    assertThat(handlerInfo.getModel()).isEqualTo(comment);
    assertThat(handlerInfo.getLink()).isEqualTo(link);
    List<HandlerInfo.Message> messages = handlerInfo.getMessages();
    assertThat(messages).hasSize(1);
    HandlerInfo.Message message = messages.get(0);
    assertThat(message.getType()).isEqualTo(messageType);
    assertThat(message.getPath()).isEqualTo(path);
    assertThat(message.getText()).isEqualTo(text);
  }

  @Test
  void testMessage() {
    HandlerInfo.Message message = new HandlerInfo.Message(path, text, messageType);
    assertThat(message.getType()).isEqualTo(messageType);
    assertThat(message.getPath()).isEqualTo(path);
    assertThat(message.getText()).isEqualTo(text);

    String anotherMessageType = "success";
    String noPath = null;
    String anotherText = " another text";

    message.setPath(noPath);
    message.setText(anotherText);
    message.setType(anotherMessageType);

    assertThat(message.getType()).isEqualTo(anotherMessageType);
    assertThat(message.getPath()).isNull();
    assertThat(message.getText()).isEqualTo(anotherText);
  }
}
