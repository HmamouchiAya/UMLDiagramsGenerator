package org.mql.java.xml;


import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.mql.java.models.*;

public class XMLParser {
    private XMLNode xmlNode;

    public XMLParser(String path) {
        this.xmlNode = new XMLNode(path);
    }
    
    public PackageAbout parse() {
        return parseRecursively(xmlNode.firstChild());
    }
    
    private PackageAbout parseRecursively(XMLNode node) {
        PackageAbout myPackage = createPackageAbout(node);
        myPackage.setPackages(extractPackages(node));
        myPackage.setClasses(extractClasses(node));
        
        return myPackage;
    }
    
    private PackageAbout createPackageAbout(XMLNode node) {
        PackageAbout myPackage = new PackageAbout();
        myPackage.setPackageName(node.getAttribute("packageName"));
        return myPackage;
    }
    
    private List<PackageAbout> extractPackages(XMLNode node) {
        List<PackageAbout> Packages = new Vector<>();
        List<XMLNode> children = node.children() != null ? node.children() : Collections.emptyList();
        
        for (XMLNode child : children) {
            if (child.getName().equals("package")) {
                Packages.add(parseRecursively(child));
            }
        }
        
        return Packages;
    }
    
    private List<ClassAbout> extractClasses(XMLNode node) {
        List<ClassAbout> packageClasses = new Vector<>();
        List<XMLNode> children = node.children() != null ? node.children() : Collections.emptyList();
        
        for (XMLNode child : children) {
            if (child.getName().equals("class")) {
                packageClasses.add(generateClass(child));
            }
        }
        
        return packageClasses;
    }
    
    private ClassAbout generateClass(XMLNode node) {
        ClassAbout cls = new ClassAbout();
        cls.setName(node.child("name").getValue());
        
        cls.setFields(extractFields(node));
        cls.setMethods(extractMethods(node));
        cls.setSuperClass(extractSuperClass(node));
        
        cls.setAssociations(extractAssociations(node));
        cls.setAggregationAssociations(extractAggregationAssociations(node));
        cls.setCompositionAssociations(extractCompositionAssociations(node));
        cls.setImplementedInterfaces(extractImplementedInterfaces(node));
 
        return cls;
    }
    
    private List<FieldAbout> extractFields(XMLNode node) {
        List<FieldAbout> classFields = new Vector<>();
        XMLNode fieldsNode = node.child("fields");
        List<XMLNode> fields = fieldsNode != null ? fieldsNode.children() : Collections.emptyList();
        
        for (XMLNode field : fields) {
            FieldAbout fieldAbout = new FieldAbout();
            fieldAbout.setFieldName(field.child("fieldName").getValue());
            fieldAbout.setFieldType(field.child("fieldType").getValue());
            fieldAbout.setModifier(field.child("modifier").getValue().charAt(0));
            classFields.add(fieldAbout);
        }
        
        return classFields;
    }
    
    private List<MethodAbout> extractMethods(XMLNode node) {
        List<MethodAbout> classMethods = new Vector<>();
        XMLNode methodsNode = node.child("methods");
        List<XMLNode> methods = methodsNode != null ? methodsNode.children() : Collections.emptyList();
        
        for (XMLNode method : methods) {
            MethodAbout methodInfo = new MethodAbout();
            methodInfo.setMethodName(method.child("methodName").getValue());
            methodInfo.setOutputType(method.child("outputType").getValue());
            methodInfo.setModifier(method.child("modifier").getValue().charAt(0));
            classMethods.add(methodInfo);
        }
        
        return classMethods;
    }
    
    private String extractSuperClass(XMLNode node) {
        XMLNode associationsNode = node.child("associations");
        List<XMLNode> associations = associationsNode != null ? associationsNode.children() : Collections.emptyList();
        
        for (XMLNode associationType : associations) {
            if (associationType.getName().equals("parent")) {
                return associationType.getValue();
            }
        }
        
        return null;
    }
    
    private List<AssociationAbout> extractAssociations(XMLNode node) {
        List<AssociationAbout> associationsList = new Vector<>();
        XMLNode associationsNode = node.child("associations");
        List<XMLNode> associations = associationsNode != null ? associationsNode.children() : Collections.emptyList();
        
        for (XMLNode associationType : associations) {
            if (!associationType.getName().equals("parent")) {
            	AssociationAbout associationAbout = new AssociationAbout();
            	associationAbout.setSourceClass(associationType.getAttribute("sourceClass"));
            	associationAbout.setTargetClass(associationType.getAttribute("targetClass"));
            	associationAbout.setAssociationType(associationType.getName());
                associationsList.add(associationAbout);
            }
        }
        
        return associationsList;
    }
    
    private List<AssociationAbout> extractAggregationAssociations(XMLNode node) {
        return extractSpecificAssociations(node, "aggregation");
    }
    
    private List<AssociationAbout> extractCompositionAssociations(XMLNode node) {
        return extractSpecificAssociations(node, "composition");
    }
    
    private List<AssociationAbout> extractSpecificAssociations(XMLNode node, String associationType) {
        List<AssociationAbout> specificAssociations = new Vector<>();
        XMLNode associationsNode = node.child("associations");
        List<XMLNode> associations = associationsNode != null ? associationsNode.children() : Collections.emptyList();
        
        for (XMLNode association : associations) {
            if (association.getName().equals(association)) {
            	AssociationAbout associationAbout = new AssociationAbout();
            	associationAbout.setSourceClass(association.getAttribute("sourceClass"));
            	associationAbout.setTargetClass(association.getAttribute("targetClass"));
            	associationAbout.setAssociationType(associationType);
                specificAssociations.add(associationAbout);
            }
        }
        
        return specificAssociations;
    }
    
    private List<InterfaceAbout> extractImplementedInterfaces(XMLNode node) {
        List<InterfaceAbout> implementedInterfaces = new Vector<>();
        XMLNode implementedInterfacesNode = node.child("implementedInterfaces");
        
        if (implementedInterfacesNode != null) {
            List<XMLNode> ifaces = implementedInterfacesNode.children();
            for (XMLNode iface : ifaces) {
                InterfaceAbout ifaceAbout = new InterfaceAbout();
                ifaceAbout.setName(iface.getAttribute("name"));
                implementedInterfaces.add(ifaceAbout);
            }
        }
        
        return implementedInterfaces;
    }
}