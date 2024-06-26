package com.coremedia.blueprint.uapi.converter;

import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.wrapper.ContentWrapper;
import com.coremedia.cap.content.wrapper.TypedCapStructWrapperFactory;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * We do not see any usecase for this class and will remove it after the deprecation period.
 * If you really need it, please take it over into your project code.
 * But be aware, that it uses non-public API (com.coremedia.cap.content.wrapper.*)
 * which we do not guarantee to keep stable.
 * We recommend to implement own solutions for the particular project requirements.
 *
 * @deprecated since 2110
 */
@Deprecated
public class GenericIdToContentWrapperConverter implements GenericConverter {
  @Override
  public Set<ConvertiblePair> getConvertibleTypes() {
    return new HashSet<>(
            Arrays.asList(
                    new ConvertiblePair(Integer.class, ContentWrapper.class),
                    new ConvertiblePair(String.class, ContentWrapper.class),
                    new ConvertiblePair(ContentWrapper.class, Integer.class),
                    new ConvertiblePair(ContentWrapper.class, String.class)));
  }

  @Override
  public Object convert(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
    if (source == null) {
      return null;
    }

    if (targetType == null) {
      throw new IllegalArgumentException("Target type must be given, for converting a \"" + source.getClass().getName() + "\"");
    }

    if (!(source instanceof ContentWrapper)) {
      return convertToContentWrapper(source, targetType);
    }

    return convertFromContentWrapper((ContentWrapper) source, targetType);
  }

  private ContentWrapper convertToContentWrapper(Object source, TypeDescriptor targetType) {
    String contentId = parseId(source, targetType);
    Content content = contentRepository.getContent(contentId);

    return createTypedContent(content, targetType);
  }

  private ContentWrapper createTypedContent(Content untypedContent, TypeDescriptor targetType) {
    return (ContentWrapper)typedCapStructWrapperFactory.createTypedAccessWrapper(targetType.getType(), untypedContent);
  }

  private String parseId(Object source, TypeDescriptor targetType) {
    try {
      int id = source instanceof String ? Integer.parseInt((String) source) : (Integer) source;
      return IdHelper.formatContentId(id);
    }
    catch (NumberFormatException | ClassCastException e) {
      // invalid number
      // -> is handled as "bad request" in DefaultHandlerExceptionResolver
      throw new TypeMismatchException(source, targetType.getType(), e);
    }
  }

  @SuppressWarnings("UnusedParameters")
  private Object convertFromContentWrapper(ContentWrapper source, TypeDescriptor targetType) {
    throw new UnsupportedOperationException();
  }

  public void setTypedCapStructWrapperFactory(TypedCapStructWrapperFactory typedCapStructWrapperFactory) {
    this.typedCapStructWrapperFactory = typedCapStructWrapperFactory;
  }

  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  private TypedCapStructWrapperFactory typedCapStructWrapperFactory;
  private ContentRepository contentRepository;

  @PostConstruct
  protected void initialize() {
    if (contentRepository == null) {
      throw new IllegalStateException("Required property not set: contentRepository");
    }
    if (typedCapStructWrapperFactory == null) {
      throw new IllegalStateException("Required property not set: typedCapStructWrapperFactory");
    }
  }

}
