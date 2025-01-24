package org.mql.java.ui;

import org.mql.java.models.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class UMLClassDiagramPanel extends JPanel {
    private static final int CLASS_WIDTH = 100; // Reduced width
    private static final int CLASS_HEIGHT = 40; // Reduced height
    private static final int PADDING = 20;
    private static final int SPACING = 50;
    private static final Color[] COLOR_PALETTE = {
        new Color(41, 128, 185), new Color(52, 152, 219),
        new Color(26, 188, 156), new Color(22, 160, 133),
        new Color(142, 68, 173)
    };

    private List<ClassAbout> classes;
    private Map<String, Point> classLocations;

    public UMLClassDiagramPanel(String name, List<ClassAbout> classes) {
        this.classes = classes;
        this.classLocations = new HashMap<>();
        setupPanel(name);
    }

    private void setupPanel(String name) {
        setPreferredSize(new Dimension(800, 800));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
            BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING)
        ));
        setLayout(new BorderLayout());
        JLabel label = new JLabel(name, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 14)); // Reduced font size
        add(label, BorderLayout.NORTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 2 - PADDING - SPACING;

        int numClasses = classes.size();
        double angleStep = 2 * Math.PI / numClasses;

        for (int i = 0; i < numClasses; i++) {
            double angle = i * angleStep;
            int x = (int) (centerX + radius * Math.cos(angle)) - CLASS_WIDTH / 2;
            int y = (int) (centerY + radius * Math.sin(angle)) - CLASS_HEIGHT / 2;

            Point location = new Point(x, y);
            classLocations.put(classes.get(i).getName(), location);
            drawClass(g2, classes.get(i), location, i);
        }

        drawAssociations(g2);
    }

    private void drawClass(Graphics2D g2, ClassAbout cls, Point location, int index) {
        int x = location.x, y = location.y;

        Color baseColor = COLOR_PALETTE[index % COLOR_PALETTE.length];
        Color lightColor = new Color(
            Math.min(baseColor.getRed() + 60, 255),
            Math.min(baseColor.getGreen() + 60, 255),
            Math.min(baseColor.getBlue() + 60, 255),
            180
        );

        GradientPaint gradient = new GradientPaint(x, y, lightColor, x, y + CLASS_HEIGHT, baseColor);
        g2.setPaint(gradient);
        g2.fillRoundRect(x, y, CLASS_WIDTH, CLASS_HEIGHT, 15, 15);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 10)); // Reduced font size for class name
        g2.drawString(cls.getSimpleName(), x + 5, y + 15);

        g2.setColor(Color.DARK_GRAY);
        g2.drawRoundRect(x, y, CLASS_WIDTH, CLASS_HEIGHT, 15, 15);

        int fieldStartY = y + CLASS_HEIGHT;
        int fieldHeight = drawFields(g2, cls.getFields(), x, fieldStartY, CLASS_WIDTH);

        int methodStartY = fieldStartY + fieldHeight;
        int methodHeight = drawMethods(g2, cls.getMethods(), x, methodStartY, CLASS_WIDTH);

        g2.setColor(Color.DARK_GRAY);
        g2.drawRoundRect(x, y, CLASS_WIDTH, CLASS_HEIGHT + fieldHeight + methodHeight, 15, 15);
    }

    private int drawFields(Graphics2D g2, List<FieldAbout> fields, int x, int y, int width) {
        int fieldHeight = fields.size() * 15; // Reduced line height
        g2.setColor(new Color(240, 240, 240, 150));
        g2.fillRoundRect(x, y, width, fieldHeight, 10, 10);
        g2.setColor(Color.DARK_GRAY);
        g2.drawRoundRect(x, y, width, fieldHeight, 10, 10);
        g2.setFont(new Font("Consolas", Font.PLAIN, 9)); // Reduced font size
        int currentY = y + 12;
        for (FieldAbout field : fields) {
            g2.drawString(field.getFormattedString(), x + 5, currentY);
            currentY += 15;
        }
        return fieldHeight;
    }

    private int drawMethods(Graphics2D g2, List<MethodAbout> methods, int x, int y, int width) {
        int methodHeight = methods.size() * 15; // Reduced line height
        g2.setFont(new Font("Consolas", Font.PLAIN, 9)); // Reduced font size
        int currentY = y + 12;
        for (MethodAbout method : methods) {
            g2.drawString(method.getFormattedMethod(), x + 5, currentY);
            currentY += 15;
        }
        return methodHeight;
    }

    private void drawAssociations(Graphics2D g2) {
        for (ClassAbout cls : classes) {
            for (AssociationAbout rel : cls.getAssociations()) {
                Point from = classLocations.get(rel.getSourceClass());
                Point to = classLocations.get(rel.getTargetClass());
                if (from != null && to != null) {
                    drawAssociationLine(g2, from, to, rel.getAssociationType());
                }
            }
        }
    }

    private void drawAssociationLine(Graphics2D g2, Point from, Point to, String associationType) {
        Point start = calculateConnectionPoint(from, to, CLASS_WIDTH, CLASS_HEIGHT);
        Point end = calculateConnectionPoint(to, from, CLASS_WIDTH, CLASS_HEIGHT);

        g2.drawLine(start.x, start.y, end.x, end.y);

        switch (associationType) {
            case "Dependecy":
                drawArrow(g2, start, end);
                break;
            case "Aggregation":
                drawDiamond(g2, start, end, false);
                break;
            case "Composition":
                drawDiamond(g2, start, end, true);
                break;
            case "Implementation":
                drawDashedLine(g2, start, end);
                drawHollowArrow(g2, start, end);
                break;
        }
    }

    private Point calculateConnectionPoint(Point source, Point target, int width, int height) {
        int x = source.x + width / 2;
        int y = source.y + height / 2;

        if (source.y + height < target.y) {
            y = source.y + height;
        } else if (source.y > target.y + height) {
            y = source.y;
        }

        if (source.x + width < target.x) {
            x = source.x + width;
        } else if (source.x > target.x + width) {
            x = source.x;
        }

        return new Point(x, y);
    }




    private void drawDiamond(Graphics2D g2, Point from, Point to, boolean filled) {
        int diamondSize = 10;

        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double length = Math.sqrt(dx * dx + dy * dy);

        double unitDx = dx / length;
        double unitDy = dy / length;

        int offsetX = (int) (unitDx * diamondSize);
        int offsetY = (int) (unitDy * diamondSize);

        int centerX = from.x + offsetX;
        int centerY = from.y + offsetY;

        int[] xPoints = {
            centerX, 
            centerX - diamondSize, 
            centerX, 
            centerX + diamondSize
        };

        int[] yPoints = {
            centerY - diamondSize, 
            centerY, 
            centerY + diamondSize, 
            centerY
        };

        Polygon diamond = new Polygon(xPoints, yPoints, 4);

        double angle = Math.atan2(dy, dx);
        AffineTransform transform = new AffineTransform();
        transform.setToRotation(angle, centerX, centerY);

        Shape rotatedDiamond = transform.createTransformedShape(diamond);

        if (filled) {
            g2.setColor(Color.BLACK);
            g2.fill(rotatedDiamond);
        } else {
            g2.setColor(Color.WHITE);
            g2.fill(rotatedDiamond);
        }

        g2.setColor(Color.BLACK);
        g2.draw(rotatedDiamond);
    }

    private void drawDashedLine(Graphics2D g2, Point start, Point end) {
        float[] dashPattern = {10.0f, 10.0f};
        Stroke originalStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(
            2.0f, 
            BasicStroke.CAP_BUTT, 
            BasicStroke.JOIN_MITER, 
            10.0f, 
            dashPattern, 
            0.0f
        ));
        g2.drawLine(start.x, start.y, end.x, end.y);
        g2.setStroke(originalStroke);
    }

    private void drawArrow(Graphics2D g2, Point start, Point end) {
        int arrowSize = 20;
        double angle = Math.atan2(end.y - start.y, end.x - start.x);

        int[] xPoints = {
            end.x,
            end.x - (int) (arrowSize * Math.cos(angle - Math.PI / 6)),
            end.x - (int) (arrowSize * Math.cos(angle + Math.PI / 6))
        };

        int[] yPoints = {
            end.y,
            end.y - (int) (arrowSize * Math.sin(angle - Math.PI / 6)),
            end.y - (int) (arrowSize * Math.sin(angle + Math.PI / 6))
        };

        Polygon arrowHead = new Polygon(xPoints, yPoints, 3);
        g2.fillPolygon(arrowHead);
    }

    private void drawHollowArrow(Graphics2D g2, Point start, Point end) {
        int arrowSize = 10;
        double angle = Math.atan2(end.y - start.y, end.x - start.x);

        int x1 = end.x - (int) (arrowSize * Math.cos(angle - Math.PI / 6));
        int y1 = end.y - (int) (arrowSize * Math.sin(angle - Math.PI / 6));

        int x2 = end.x - (int) (arrowSize * Math.cos(angle + Math.PI / 6));
        int y2 = end.y - (int) (arrowSize * Math.sin(angle + Math.PI / 6));

        g2.setColor(Color.BLACK);
        g2.drawLine(end.x, end.y, x1, y1);
        g2.drawLine(end.x, end.y, x2, y2);
    }
}