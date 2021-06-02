package com.coremedia.blueprint.lc.test;

import io.specto.hoverfly.junit5.HoverflyExtension;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Simple Wrapper to disable hoverly extension for tests against the real system.
 * see {@link HoverflyTestHelper#useTapes()}
 *
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@Deprecated(since = "2104.2", forRemoval = true)
@SuppressWarnings("removal")
public class SwitchableHoverflyExtension extends HoverflyExtension {

  @Override
  public void beforeEach(ExtensionContext context) {
    if (!HoverflyTestHelper.useTapes()){
      return;
    }
    super.beforeEach(context);
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    if (!HoverflyTestHelper.useTapes()){
      return;
    }
    super.beforeAll(context);
  }

  @Override
  public void afterAll(ExtensionContext context) {
    if (!HoverflyTestHelper.useTapes()){
      return;
    }
    super.afterAll(context);
  }
}
