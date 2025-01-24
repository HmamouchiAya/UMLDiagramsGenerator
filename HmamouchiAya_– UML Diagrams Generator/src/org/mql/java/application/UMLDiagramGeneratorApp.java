package org.mql.java.application;



import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import org.mql.java.models.ClassAbout;
import org.mql.java.models.PackageAbout;
import org.mql.java.xml.PackageExplorer;
import org.mql.java.ui.UMLClassDiagramPanel;

public class UMLDiagramGeneratorApp {
    private static final String BASE_PACKAGE = "org.mql.java";
    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 700;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UMLDiagramGeneratorApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = initializeFrame();
        PackageExplorer explorer = new PackageExplorer(BASE_PACKAGE);
        PackageAbout packageInfo = explorer.analyzePackage();
        
        JPanel diagramContainer = createDiagramContainer(explorer);
        JScrollPane scrollPane = new JScrollPane(diagramContainer);
        
        frame.add(scrollPane);
        frame.setVisible(true);
    }

    private static JFrame initializeFrame() {
        JFrame frame = new JFrame("UML Diagram Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        return frame;
    }

    private static JPanel createDiagramContainer(PackageExplorer explorer) {
        Map<String, List<ClassAbout>> packageClassMap = explorer.groupClassesByPackage();
        
        JPanel container = new JPanel(new GridLayout(0, 2, 20, 20));
        packageClassMap.forEach((packageName, classes) -> {
        	UMLClassDiagramPanel diagramPanel = new UMLClassDiagramPanel(packageName, classes);
            diagramPanel.setBorder(createPanelBorder());
            container.add(diagramPanel);
        });
        
        return container;
    }

    private static CompoundBorder createPanelBorder() {
        return BorderFactory.createCompoundBorder(
            new LineBorder(Color.BLACK, 2),
            new EmptyBorder(10, 10, 10, 10)
        );
    }
}