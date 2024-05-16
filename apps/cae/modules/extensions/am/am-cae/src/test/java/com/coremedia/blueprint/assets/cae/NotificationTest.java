package com.coremedia.blueprint.assets.cae;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class NotificationTest {


  @Test
  public void test() {
    Notification notification1 = new Notification(Notification.NotificationType.SUCCESS, "key1", null);
    Notification notification2 = new Notification(Notification.NotificationType.INFO, "key2", null);
    Notification notificationSameAs2 = new Notification(Notification.NotificationType.INFO, "key2", null);
    List<?> differentParams = List.of("something");
    Notification notificationWithDifferentParams = new Notification(Notification.NotificationType.INFO, "key2", differentParams);

    assertNotEquals(notification1, notification2);
    assertEquals(notification1, notification1);
    assertEquals(notification2, notificationSameAs2);
    assertNotEquals(notification2, notificationWithDifferentParams);
    assertNotNull(notification1);

    assertEquals(Notification.NotificationType.SUCCESS, notification1.getType());
    assertEquals("key1", notification1.getKey());
    assertEquals(differentParams, notificationWithDifferentParams.getParams());

    assertNotEquals(notification1.hashCode(), notification2.hashCode());
  }
}
