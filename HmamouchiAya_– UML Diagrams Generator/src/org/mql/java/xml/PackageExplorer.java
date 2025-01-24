package org.mql.java.xml;

import org.mql.java.models.*;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.lang.reflect.Modifier;

public class PackageExplorer {
    private final String packageName;
    private final String basePath;

    public PackageExplorer(String packageName) {
        this.packageName = packageName;
        this.basePath = "src/" + packageName.replace('.', '/');
    }

    private Stream<File> getJavaFiles(File directory) {
        return Optional.ofNullable(directory.listFiles())
            .map(files -> Arrays.stream(files)
                .flatMap(file -> file.isDirectory() 
                    ? getJavaFiles(file) 
                    : (file.getName().endsWith(".java") ? Stream.of(file) : Stream.empty())
                )
            )
            .orElse(Stream.empty());
    }

    private String getFullClassName(File file) {
        String relativePath = file.getPath().substring(basePath.length() + 1);
        return packageName + "." + relativePath.replace(File.separator, ".").replace(".java", "");
    }

    public List<ClassAbout> findClasses() {
        File directory = new File(basePath);
        return directory.exists() && directory.isDirectory()
            ? getJavaFiles(directory)
                .map(file -> new ClassAbout(getFullClassName(file)))
                .collect(Collectors.toList())
            : Collections.emptyList();
    }

    public PackageAbout analyzePackage() {
        File directory = new File(basePath);
        if (!directory.exists() || !directory.isDirectory()) {
            return null;
        }

        PackageAbout packageAbout = new PackageAbout(packageName);
        
        Map<String, List<Object>> typeCollections = new HashMap<>() {{
            put("class", new ArrayList<>());
            put("interface", new ArrayList<>());
            put("enum", new ArrayList<>());
            put("annotation", new ArrayList<>());
        }};

        getJavaFiles(directory).forEach(file -> {
            String fullClassName = getFullClassName(file);
            try {
                Class<?> cls = Class.forName(fullClassName);
                String type = determineClassType(cls);
                
                switch (type) {
                    case "class" -> typeCollections.get("class").add(new ClassAbout(fullClassName));
                    case "interface" -> typeCollections.get("interface").add(new InterfaceAbout(fullClassName));
                    case "enum" -> typeCollections.get("enum").add(new EnumAbout(fullClassName));
                    case "annotation" -> typeCollections.get("annotation").add(new AnnotationAbout(fullClassName));
                }
            } catch (ClassNotFoundException e) {
                // Handle exception
            }
        });

        packageAbout.setClasses(
            typeCollections.get("class").stream()
                .map(obj -> (ClassAbout) obj)
                .collect(Collectors.toList())
        );
        packageAbout.setInterfaces(
            typeCollections.get("interface").stream()
                .map(obj -> (InterfaceAbout) obj)
                .collect(Collectors.toList())
        );
        packageAbout.setEnums(
            typeCollections.get("enum").stream()
                .map(obj -> (EnumAbout) obj)
                .collect(Collectors.toList())
        );
        packageAbout.setAnnotations(
            typeCollections.get("annotation").stream()
                .map(obj -> (AnnotationAbout) obj)
                .collect(Collectors.toList())
        );

        return packageAbout;
    }

    private String determineClassType(Class<?> cls) {
        if (cls.isAnnotation()) return "annotation";
        if (cls.isEnum()) return "enum";
        if (cls.isInterface()) return "interface";
        return "class";
    }

    public Map<String, List<ClassAbout>> groupClassesByPackage() {
        File directory = new File(basePath);
        Map<String, List<ClassAbout>> packageMap = new HashMap<>();

        if (directory.exists() && directory.isDirectory()) {
            getJavaFiles(directory).forEach(file -> {
                String fullClassName = getFullClassName(file);
                String currentPackage = fullClassName.substring(0, fullClassName.lastIndexOf('.'));
                packageMap.computeIfAbsent(currentPackage, k -> new ArrayList<>())
                    .add(new ClassAbout(fullClassName));
            });
        }

        return packageMap;
    }
}