package com.coremedia.blueprint.cae.richtext.filter;

import com.coremedia.xml.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * An abstract Filter that handles embedding data
 * like link targets or images
 */
public abstract class EmbeddingFilter extends Filter implements FilterFactory {
  private static final Logger LOG = LoggerFactory.getLogger(EmbeddingFilter.class);

  /**
   * Counter to memorize markup nesting in which embedding is currently taking place
   */
  private int skipLevelsDuringEmbedding;
  private final SaxElementStack elementStack = new SaxElementStack();


  // --- abstract ---------------------------------------------------

  /**
   * Decide whether the data denoted by this element is to be embedded
   */
  protected abstract boolean mustEmbed(String tag, Attributes atts);

  /**
   * Render the data to be embedded
   */
  protected abstract void renderEmbeddedData(String tag, Attributes atts);


  // --- Filter -----------------------------------------------------

  /**
   * Initialize this instance with default values when a document starts
   */
  @Override
  public void startDocument() throws SAXException {
    skipLevelsDuringEmbedding = 0;
    elementStack.clear();
    super.startDocument();
  }

  /**
   * Possibly replace the element with embedded data
   * <p>
   * If required by {@link #mustEmbed(String, Attributes)}, replace this
   * element (incl. any children) by {@link #renderEmbeddedData(String, Attributes)}
   */
  @Override
  public void startElement(String namespaceUri, String localName, String qName, Attributes atts) throws SAXException {
    String tag = asTag(namespaceUri, localName, qName);
    if (skipLevelsDuringEmbedding==0) {
      if (mustEmbed(tag, atts)) {
        renderEmbeddedData(tag, atts);
        ++skipLevelsDuringEmbedding;
      } else {
        super.startElement(namespaceUri, localName, qName, atts);
        elementStack.push(new SaxElementData(namespaceUri, localName, qName, atts));
      }
    } else {
      String parentTag = elementStack.isEmpty() ? "(root)" : elementStack.top().asTag();
      LOG.info("Cannot handle nested element {} in a {} with mode embedded. Ignore.", tag, parentTag);
      ++skipLevelsDuringEmbedding;
    }
  }

  /**
   * In case this method is called, and a &lt;p&gt; tag has been withheld, render the &lt;p&gt; tag with it's
   * original attributes. In either case, call the super implementation.
   */
  @Override
  public void endElement(String namespaceUri, String localName, String qName) throws SAXException {
    if (skipLevelsDuringEmbedding==0) {
      super.endElement(namespaceUri, localName, qName);
      elementStack.pop();  // NOSONAR  Don't need the result here, but must sync the stack.
    } else {
      assert skipLevelsDuringEmbedding>0 : "mismatching open/close counter while embedding data";
      --skipLevelsDuringEmbedding;
    }
  }

  /**
   * In case this method is called, and a &lt;p&gt; tag has been withheld, render the &lt;p&gt; tag with it's
   * original attributes. In either case, call the super implementation.
   */
  @Override
  public void characters(char[] text, int start, int length) throws SAXException {
    if (skipLevelsDuringEmbedding==0) {
      super.characters(text, start, length);
    }
  }

  /**
   * In case this method is called, and a &lt;p&gt; tag has been withheld, render the &lt;p&gt; tag with it's
   * original attributes. In either case, call the super implementation.
   */
  @Override
  public void ignorableWhitespace(char[] text, int start, int length) throws SAXException {
    if (skipLevelsDuringEmbedding==0) {
      super.ignorableWhitespace(text, start, length);
    }
  }

  /**
   * In case this method is called, and a &lt;p&gt; tag has been withheld, render the &lt;p&gt; tag with it's
   * original attributes. In either case, call the super implementation.
   */
  @Override
  public void processingInstruction(String target, String data) throws SAXException {
    if (skipLevelsDuringEmbedding==0) {
      super.processingInstruction(target, data);
    }
  }

  /**
   * In case this method is called, and a &lt;p&gt; tag has been withheld, render the &lt;p&gt; tag with it's
   * original attributes. In either case, call the super implementation.
   */
  @Override
  public void skippedEntity(String name) throws SAXException {
    if (skipLevelsDuringEmbedding==0) {
      super.skippedEntity(name);
    }
  }

  @Override
  public void endDocument() throws SAXException {
    assert elementStack.isEmpty() : "Stack not empty at endDocument().  This indicates a bug in the extending class.";
    super.endDocument();
  }


  // --- internal ---------------------------------------------------

  private static String asTag(String uri, String localName, String qName) {
    return "".equals(uri) ? qName : localName;
  }
}
