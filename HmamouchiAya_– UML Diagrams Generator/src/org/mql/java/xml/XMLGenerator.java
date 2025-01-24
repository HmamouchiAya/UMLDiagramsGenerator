package org.mql.java.xml;

import org.mql.java.models.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class XMLGenerator {

    public static Document generateXML(PackageAbout rootPackage) throws Exception {
        Document doc = createDocument();
        Element rootElement = createRootElement(doc);
        
        populatePackageXML(rootPackage, doc, rootElement);
        
        saveXMLToFile(doc, generateFilePath(rootPackage));
        return doc;
    }

    private static Document createDocument() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        return docBuilder.newDocument();
    }

    private static Element createRootElement(Document doc) {
        Element rootElement = doc.createElement("project");
        doc.appendChild(rootElement);
        return rootElement;
    }

    private static String generateFilePath(PackageAbout rootPackage) {
        return "resources/generatedXML/" + rootPackage.getPackageName().replace('.', '/') + ".xml";
    }

    private static void populatePackageXML(PackageAbout packageAbout, Document doc, Element parentElement) {
        Element packageElement = createElement(doc, parentElement, "package");
        packageElement.setAttribute("name", packageAbout.getPackageName());

        processPackageContents(packageAbout, doc, packageElement);
    }

    private static void processPackageContents(PackageAbout packageAbout, Document doc, Element packageElement) {
        processClasses(packageAbout, doc, packageElement);
        processInterfaces(packageAbout, doc, packageElement);
        processEnums(packageAbout, doc, packageElement);
        processAnnotations(packageAbout, doc, packageElement);
        processPackages(packageAbout, doc, packageElement);
    }

    private static void processClasses(PackageAbout packageAbout, Document doc, Element packageElement) {
        for (ClassAbout classAbout : packageAbout.getClasses()) {
            createClassXML(classAbout, doc, packageElement);
        }
    }

    private static void processInterfaces(PackageAbout packageAbout, Document doc, Element packageElement) {
        for (InterfaceAbout interfaceAbout : packageAbout.getInterfaces()) {
            createInterfaceXML(interfaceAbout, doc, packageElement);
        }
    }

    private static void processEnums(PackageAbout packageAbout, Document doc, Element packageElement) {
        for (EnumAbout enumAbout : packageAbout.getEnums()) {
            createEnumXML(enumAbout, doc, packageElement);
        }
    }

    private static void processAnnotations(PackageAbout packageAbout, Document doc, Element packageElement) {
        for (AnnotationAbout annotationAbout : packageAbout.getAnnotations()) {
            createAnnotationXML(annotationAbout, doc, packageElement);
        }
    }

    private static void processPackages(PackageAbout packageAbout, Document doc, Element packageElement) {
        if (packageAbout.getPackages() != null) {
            for (PackageAbout Package : packageAbout.getPackages()) {
                populatePackageXML(Package, doc, packageElement);
            }
        }
    }

    private static void createClassXML(ClassAbout classAbout, Document doc, Element parentElement) {
        Element classElement = createElement(doc, parentElement, "class");
        
        createTextElement(doc, classElement, "name", classAbout.getSimpleName());
        createRelationshipsXML(classAbout, doc, classElement);
        createImplementedInterfacesXML(classAbout, doc, classElement);
        
        Element fieldsElement = createElement(doc, classElement, "fields");
        createFieldsXML(classAbout, doc, fieldsElement);
        
        Element methodsElement = createElement(doc, classElement, "methods");
        createMethodsXML(classAbout, doc, methodsElement);
    }

    private static void createImplementedInterfacesXML(ClassAbout classAbout, Document doc, Element classElement) {
        if (!classAbout.getImplementedInterfaces().isEmpty()) {
            Element implementedInterfaces = createElement(doc, classElement, "implementedInterfaces");
            for (InterfaceAbout iface : classAbout.getImplementedInterfaces()) {
                createImplementedInterfaceXML(iface, doc, implementedInterfaces);
            }
        }
    }

    private static Element createElement(Document doc, Element parentElement, String elementName) {
        Element element = doc.createElement(elementName);
        parentElement.appendChild(element);
        return element;
    }

    private static void createTextElement(Document doc, Element parentElement, String elementName, String textContent) {
        Element element = doc.createElement(elementName);
        element.appendChild(doc.createTextNode(textContent));
        parentElement.appendChild(element);
    }

    private static void createInterfaceXML(InterfaceAbout interfaceAbout, Document doc, Element parentElement) {
        Element interfaceElement = createElement(doc, parentElement, "interface");
        
        createTextElement(doc, interfaceElement, "simpleName", interfaceAbout.getSimpleName());
        createTextElement(doc, interfaceElement, "name", interfaceAbout.getName());
        createTextElement(doc, interfaceElement, "modifiers", interfaceAbout.getModifiers());
        
        if (interfaceAbout.getSuperClass() != null) {
            createTextElement(doc, interfaceElement, "extendedClass", interfaceAbout.getSuperClass());
        }
        
        Element fieldsElement = createElement(doc, interfaceElement, "fields");
        createFieldsXML(interfaceAbout, doc, fieldsElement);
        
        Element methodsElement = createElement(doc, interfaceElement, "methods");
        createMethodsXML(interfaceAbout, doc, methodsElement);
    }

    private static void createAnnotationXML(AnnotationAbout annotationAbout, Document doc, Element parentElement) {
        Element annotationElement = createElement(doc, parentElement, "annotation");
        
        createTextElement(doc, annotationElement, "name", annotationAbout.getAnnotationName());
        createTextElement(doc, annotationElement, "retentionPolicy", annotationAbout.getPolicy().toString());
        createTextElement(doc, annotationElement, "hasInherited", String.valueOf(annotationAbout.inheritanceStatus()));
        
        Element attributesElement = createElement(doc, annotationElement, "attributes");
        for (Map.Entry<String, String> attribute : annotationAbout.getAttributes().entrySet()) {
            Element attributeElement = createElement(doc, attributesElement, "attribute");
            createTextElement(doc, attributeElement, "name", attribute.getKey());
            createTextElement(doc, attributeElement, "type", attribute.getValue());
        }
    }

    private static void createEnumXML(EnumAbout enumAbout, Document doc, Element parentElement) {
        Element enumElement = createElement(doc, parentElement, "enum");
        
        createTextElement(doc, enumElement, "name", enumAbout.getName());
        
        Element fieldsElement = createElement(doc, enumElement, "fields");
        for (String field : enumAbout.getFields()) {
            createTextElement(doc, fieldsElement, "field", field);
        }
    }

    private static void createFieldsXML(Object sourceAbout, Document doc, Element fieldsElement) {
        // This method uses reflection or type-specific methods to get fields
        if (sourceAbout instanceof ClassAbout) {
            for (FieldAbout field : ((ClassAbout) sourceAbout).getFields()) {
                createFieldXML(field, doc, fieldsElement);
            }
        } else if (sourceAbout instanceof InterfaceAbout) {
            for (FieldAbout field : ((InterfaceAbout) sourceAbout).getFields()) {
                createFieldXML(field, doc, fieldsElement);
            }
        }
    }

    private static void createMethodsXML(Object sourceAbout, Document doc, Element methodsElement) {
        // This method uses reflection or type-specific methods to get methods
        if (sourceAbout instanceof ClassAbout) {
            for (MethodAbout method : ((ClassAbout) sourceAbout).getMethods()) {
                createMethodXML(method, doc, methodsElement);
            }
        } else if (sourceAbout instanceof InterfaceAbout) {
            for (MethodAbout method : ((InterfaceAbout) sourceAbout).getMethods()) {
                createMethodXML(method, doc, methodsElement);
            }
        }
    }

    private static void createFieldXML(FieldAbout field, Document doc, Element parentElement) {
        Element fieldElement = createElement(doc, parentElement, "field");
        
        createTextElement(doc, fieldElement, "name", field.getName());
        createTextElement(doc, fieldElement, "type", field.getType());
        createTextElement(doc, fieldElement, "modifier", String.valueOf(field.getModifier()));
    }

    private static void createMethodXML(MethodAbout method, Document doc, Element parentElement) {
        Element methodElement = createElement(doc, parentElement, "method");
        
        createTextElement(doc, methodElement, "name", method.getName());
        createTextElement(doc, methodElement, "returnType", method.getReturnType());
        createTextElement(doc, methodElement, "modifier", String.valueOf(method.getModifier()));
    }

    private static void createRelationshipsXML(ClassAbout classAbout, Document doc, Element parentElement) {
        Element relationsElement = createElement(doc, parentElement, "relationships");
        
        if (classAbout.getExtendedClass() != null) {
            createTextElement(doc, relationsElement, "parent", classAbout.getExtendedClass());
        }
        
        for (RelationshipAbout usedClass : classAbout.getUsedClasses()) {
            Element usedElement = createElement(doc, relationsElement, "uses");
            usedElement.setAttribute("from", usedClass.getSimpleFrom());
            usedElement.setAttribute("to", usedClass.getSimpleTo());
        }
        
        for (RelationshipAbout composedClass : classAbout.getComposedClasses()) {
            Element compositionElement = createElement(doc, relationsElement, "composition");
            compositionElement.setAttribute("from", composedClass.getSimpleFrom());
            compositionElement.setAttribute("to", composedClass.getSimpleTo());
            compositionElement.setAttribute("maxOccurs", composedClass.getMaxOccurs());
        }
        
        for (RelationshipAbout aggregatedClass : classAbout.getAggregatedClasses()) {
            Element aggregationElement = createElement(doc, relationsElement, "aggregation");
            aggregationElement.setAttribute("from", aggregatedClass.getSimpleFrom());
            aggregationElement.setAttribute("to", aggregatedClass.getSimpleTo());
            aggregationElement.setAttribute("maxOccurs", aggregatedClass.getMaxOccurs());
        }
    }

    private static void createImplementedInterfaceXML(InterfaceAbout iface, Document doc, Element parentElement) {
        Element interfaceElement = createElement(doc, parentElement, "interface");
        interfaceElement.setAttribute("name", iface.getSimpleName());
    }


    public static void printXML(Document doc) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(System.out));
    }
    
    public static void saveXMLToFile(Document doc, String filePath) throws TransformerException, IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(file));

        System.out.println("XML file saved to: " + file.getAbsolutePath());
    }

    // Other methods from the original class would remain mostly unchanged
    // Just apply similar refactoring principles
}