# Mock Requests Plugin

This plugin provides the ability to mock requests, which won't be sent to the server but
which are directly provided with mock responses. As such, it is typically meant for
error scenarios.

## History

The plugin got introduced for the `Story801WriteInterceptorsTest` which in its history
required a special configuration in the Studio server, which is not available in
Blueprint Studio.

As dynamic packages cannot influence the server configuration, the workaround was to
make 3 tests of one. The test dissolved into a test of the REST server and the REST
client and the last missing link was a test for Studio, which should show a message
box on failure.

The last step is what this plugin is for. It provokes a 403 response on client side,
which would otherwise come from server. To find the corresponding tests in REST
server and REST client just search for `NOT_VALID`, which is part of the request
answer.

## Usage

Here are some important steps to take in your test. For a complete usage example have a look at `Story801WriteInterceptorsTest`.

* Enabled the plugin in your test, similar to this:

    ```java
    public class StoryExampleTest {
      private DynamicModulesTestService dynamicModulesTestService;
      
      @Before
      public void setUp() {
        dynamicModulesTestService = startDynamicModulesTestService("com.coremedia.blueprint.internal__mock-requests-plugin");
      }
    }
    ```

* Register a mock response via `MOCK_REQUEST_REGISTRY` using the `MockRequestRegistry` Java wrapper:

    ```java
    public static class Steps {
      @Inject
      private MockRequestRegistry mockRequestRegistry;
    
      void given_a_document_with_an_intercepted_title_property_is_opened(Reference<? super Content> refDoc,
                                                                         Reference<? super String> refProperty) {
        String property = "title";
        String validPattern = "^[\\D]*$";
      
        mockRequestRegistry.registerMockRegExpValidator(property, validPattern);
        // ...
      }
    
    }
    ```

It is expected, that you need to provide custom mocked requests for new tests. In order to do so,
extend the `MockRequestRegistry` and its wrapper accordingly.
