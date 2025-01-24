package org.mql.java.application;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;
import org.mql.java.models.ClassAbout;
import org.mql.java.models.PackageAbout;
import org.mql.java.ui.UMLClassDiagramPanel;
import org.mql.java.xml.PackageExplorer;
import org.w3c.dom.Document;

public class UMLDiagramGeneratorApp {
    public static void main(String[] args) { 
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("UML Diagram Generator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            
            String packageName = "org.mql.java";
            PackageExplorer scanner = new PackageExplorer(packageName);
            PackageAbout basePackage = scanner.analyzePackage();
            System.out.println(basePackage);
            
            Map<String, List<ClassAbout>> map = scanner.groupClassesByPackage();
            
            JPanel parentPanel = new JPanel();
            parentPanel.setLayout(new GridLayout(0, 2, 200, 200));
            map.forEach((name, classes) -> {
                UMLClassDiagramPanel diagramPanel = new UMLClassDiagramPanel(name, classes);
                diagramPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Color.BLACK, 2),
                    new EmptyBorder(10, 10, 10, 10) 
                ));
                parentPanel.add(diagramPanel);
            });
        
            JScrollPane scrollPane = new JScrollPane(parentPanel);
            frame.add(scrollPane);
            frame.setVisible(true);
        });  

        try {
            PackageAbout rootPackage = new PackageExplorer("org.mql.java").analyzePackage();
            Document xmlDocument = org.mql.java.xml.XMLGenerator.generateXML(rootPackage);
            org.mql.java.xml.XMLGenerator.printXML(xmlDocument);
            
            PackageAbout mypkg = new org.mql.java.xml.XMLParser("resources/generatedXML/java.xml").parse();
            System.out.println(mypkg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
}