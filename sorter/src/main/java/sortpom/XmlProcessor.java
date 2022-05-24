package sortpom;

import java.io.InputStream;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;
import sortpom.util.XmlOrderedResult;
import sortpom.verify.ElementComparator;
import sortpom.wrapper.operation.HierarchyRootWrapper;
import sortpom.wrapper.operation.WrapperFactory;

/**
 * Creates xml structure and sorts it.
 *
 * @author Bjorn Ekryd
 */
public class XmlProcessor {
  private final WrapperFactory factory;

  private Document originalDocument;
  private Document newDocument;

  public XmlProcessor(WrapperFactory factory) {
    this.factory = factory;
  }

  /**
   * Sets the original xml that should be sorted. Builds a dom document of the xml.
   *
   * @param originalXml the new original xml
   */
  public void setOriginalXml(final InputStream originalXml) throws DocumentException, SAXException {
    SAXReader parser = new SAXReader();
    parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    parser.setMergeAdjacentText(true);
    originalDocument = parser.read(originalXml);
  }

  /** Creates a new dom document that contains the sorted xml. */
  public void sortXml() {
    newDocument = (Document) originalDocument.clone();
    final Element rootElement = originalDocument.getRootElement().createCopy();

    HierarchyRootWrapper rootWrapper = factory.createFromRootElement(rootElement);

    rootWrapper.createWrappedStructure(factory);
    rootWrapper.detachStructure();
    rootWrapper.sortStructureAttributes();
    rootWrapper.sortStructureElements();
    rootWrapper.connectXmlStructure();

    newDocument.setRootElement(rootWrapper.getElementContent().getContent());
  }

  public Document getNewDocument() {
    return newDocument;
  }

  public XmlOrderedResult isXmlOrdered() {
    ElementComparator elementComparator =
        new ElementComparator(originalDocument.getRootElement(), newDocument.getRootElement());
    return elementComparator.isElementOrdered();
  }
}
