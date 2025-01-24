package org.mql.java.models;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class PackageAbout {
   private String packageName;
   private List<PackageAbout> Packages;
   private List<ClassAbout> classes;
   private List<InterfaceAbout> interfaces; 
   private List<AnnotationAbout> annotations;
   private List<EnumAbout> enums;


public PackageAbout() {
       this.packageName = "";
       initializeLists();
   }

   public PackageAbout(String packageLocation) {
       this.packageName = extractPackageName(packageLocation);
       initializeLists();
   }

   private String extractPackageName(String packagePath) {
       if (packagePath == null || packagePath.isEmpty()) {
           return "";
       }
       String[] pathSegments = packagePath.split("\\.");
       return pathSegments[pathSegments.length - 1];
   }

   private void initializeLists() {
       this.Packages = new Vector<>();
       this.classes = new Vector<>();
       this.interfaces = new Vector<>();
       this.annotations = new Vector<>();
       this.enums = new Vector<>();
   }

   public String getPackageName() {
       return packageName;
   }

   public List<PackageAbout> getPackages() {
       return Packages;
   }

   public List<ClassAbout> getClasses() {
       return classes;
   }

   public List<InterfaceAbout> getInterfaces() {
       return interfaces;
   }

   public List<AnnotationAbout> getAnnotations() {
       return annotations;
   }

   public List<EnumAbout> getEnums() {
       return enums;
   }

   public void addPackage(PackageAbout Package) {
       if (Package != null) {
           this.Packages.add(Package);
       }
   }

   public void addClass(ClassAbout classAbout) {
       if (classAbout != null) {
           this.classes.add(classAbout);
       }
   }

   public void addInterface(InterfaceAbout interfaceAbout) {
       if (interfaceAbout != null) {
           this.interfaces.add(interfaceAbout);
       }
   }

   public void addAnnotation(AnnotationAbout annotationAbout) {
       if (annotationAbout != null) {
           this.annotations.add(annotationAbout);
       }
   }

   public void addEnum(EnumAbout enumAbout) {
       if (enumAbout != null) {
           this.enums.add(enumAbout);
       }
   }
   public void setPackageName(String packageName) {
	this.packageName = packageName;
}

   public void setPackages(List<PackageAbout> Packages) {
       this.Packages = new Vector<>(Packages != null ? Packages : Collections.emptyList());
   }

   public void setClasses(List<ClassAbout> classes) {
       this.classes = new Vector<>(classes != null ? classes : Collections.emptyList());
   }

   public void setInterfaces(List<InterfaceAbout> interfaces) {
       this.interfaces = new Vector<>(interfaces != null ? interfaces : Collections.emptyList());
   }

   public void setAnnotations(List<AnnotationAbout> annotations) {
       this.annotations = new Vector<>(annotations != null ? annotations : Collections.emptyList());
   }

   public void setEnums(List<EnumAbout> enums) {
       this.enums = new Vector<>(enums != null ? enums : Collections.emptyList());
   }

   @Override
   public String toString() {
       return "PackageDetails{" +
              "packageName='" + packageName + '\'' +
              ", subPackages=" + Packages +
              ", classDefinitions=" + classes +
              ", interfaceDefinitions=" + interfaces +
              ", annotationDefinitions=" + annotations +
              ", enumDefinitions=" + enums +
              '}';
   }
}