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
        
        if (interfaceAbout.superClass() != null) {
            createTextElement(doc, interfaceElement, "extendedClass", interfaceAbout.superClass());
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
        
        Element methodPropertiesElement = createElement(doc, annotationElement, "methodProperties");
        for (Map.Entry<String, String> methodPropertie : annotationAbout.getMethodProperties().entrySet()) {
            Element methodpropertieElement = createElement(doc, methodPropertiesElement, "methodPropertie");
            createTextElement(doc, methodpropertieElement, "name", methodPropertie.getKey());
            createTextElement(doc, methodpropertieElement, "type", methodPropertie.getValue());
        }
    }

    private static void createEnumXML(EnumAbout enumAbout, Document doc, Element parentElement) {
        Element enumElement = createElement(doc, parentElement, "enum");
        
        createTextElement(doc, enumElement, "name", enumAbout.getQualifiedName());
        
        Element fieldsElement = createElement(doc, enumElement, "fields");
        for (String field : enumAbout.getFields()) {
            createTextElement(doc, fieldsElement, "field", field);
        }
    }

    private static void createFieldsXML(Object sourceAbout, Document doc, Element fieldsElement) {
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
        
        createTextElement(doc, fieldElement, "name", field.getFieldName());
        createTextElement(doc, fieldElement, "type", field.getFieldType());
        createTextElement(doc, fieldElement, "modifier", String.valueOf(field.getModifier()));
    }

    private static void createMethodXML(MethodAbout method, Document doc, Element parentElement) {
        Element methodElement = createElement(doc, parentElement, "method");
        
        createTextElement(doc, methodElement, "name", method.getMethodName());
        createTextElement(doc, methodElement, "returnType", method.getOutputType());
        createTextElement(doc, methodElement, "modifier", String.valueOf(method.getModifer()));
    }

    private static void createRelationshipsXML(ClassAbout classAbout, Document doc, Element parentElement) {
        Element associationsElement = createElement(doc, parentElement, "relationships");
        
        if (classAbout.getSuperClass() != null) {
            createTextElement(doc, associationsElement, "parent", classAbout.getSuperClass());
        }
        
        for (AssociationAbout dependecyAssociattion : classAbout.getDependencyAssociations()) {
            Element usedElement = createElement(doc, associationsElement, "uses");
            usedElement.setAttribute("sourceClass", dependecyAssociattion.getSourceClassName());
            usedElement.setAttribute("targetClass", dependecyAssociattion.getTargetClassName());
        }
        
        for (AssociationAbout compositionAssociation : classAbout.getCompositionAssociations()) {
            Element compositionElement = createElement(doc, associationsElement, "composition");
            compositionElement.setAttribute("sourcetClass", compositionAssociation.getSourceClassName());
            compositionElement.setAttribute("targetClass", compositionAssociation.getTargetClassName());
            compositionElement.setAttribute("upperBound", compositionAssociation.getUpperBound());
        }
        
        for (AssociationAbout aggregationAssociation : classAbout.getAggregationAssociations()) {
            Element aggregationElement = createElement(doc, associationsElement, "aggregation");
            aggregationElement.setAttribute("sourceClass", aggregationAssociation.getSourceClassName());
            aggregationElement.setAttribute("targetclass", aggregationAssociation.getTargetClassName());
            aggregationElement.setAttribute("upperBound", aggregationAssociation.getUpperBound());
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

}