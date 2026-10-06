package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Icon;

public final class ClinicIcon implements Icon {
    private final String type;
    private final Color color;
    private final int size;

    public ClinicIcon(String type, Color color, int size) {
        this.type = type;
        this.color = color;
        this.size = size;
    }

    @Override public int getIconWidth() { return size; }
    @Override public int getIconHeight() { return size; }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.translate(x, y);
        g.scale(size / 24d, size / 24d);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        switch (type) {
            case "dashboard": chart(g); break;
            case "branch": building(g); break;
            case "employee":
            case "customer": people(g); break;
            case "pet": paw(g); break;
            case "appointment": calendar(g); break;
            case "medical": cross(g); break;
            case "detail": document(g); break;
            case "service": capsule(g); break;
            case "vaccination": syringe(g); break;
            case "inventory": boxes(g); break;
            case "invoice": receipt(g); break;
            case "report": chart(g); break;
            case "reminder": bell(g); break;
            case "history": clock(g); break;
            case "logout": logout(g); break;
            default: document(g);
        }
        g.dispose();
    }

    private void chart(Graphics2D g) {
        g.draw(new Line2D.Double(3, 20, 21, 20));
        g.draw(new Line2D.Double(4, 20, 4, 4));
        g.fillRoundRect(7, 12, 3, 6, 2, 2);
        g.fillRoundRect(12, 8, 3, 10, 2, 2);
        g.fillRoundRect(17, 4, 3, 14, 2, 2);
    }

    private void building(Graphics2D g) {
        g.drawRoundRect(3, 5, 18, 16, 2, 2);
        g.draw(new Line2D.Double(8, 5, 8, 21));
        g.draw(new Line2D.Double(14, 5, 14, 21));
        g.draw(new Line2D.Double(3, 11, 21, 11));
        g.draw(new Line2D.Double(3, 16, 21, 16));
    }

    private void people(Graphics2D g) {
        g.draw(new Ellipse2D.Double(8, 3, 8, 8));
        Path2D shoulders = new Path2D.Double();
        shoulders.moveTo(3, 21);
        shoulders.curveTo(3, 14, 7, 12, 12, 12);
        shoulders.curveTo(17, 12, 21, 15, 21, 21);
        g.draw(shoulders);
    }

    private void paw(Graphics2D g) {
        g.fill(new Ellipse2D.Double(3, 5, 5, 6));
        g.fill(new Ellipse2D.Double(9.5, 2.5, 5, 6));
        g.fill(new Ellipse2D.Double(16, 5, 5, 6));
        g.fill(new Ellipse2D.Double(6, 12, 12, 9));
    }

    private void calendar(Graphics2D g) {
        g.drawRoundRect(3, 5, 18, 16, 2, 2);
        g.draw(new Line2D.Double(3, 10, 21, 10));
        g.draw(new Line2D.Double(8, 3, 8, 7));
        g.draw(new Line2D.Double(16, 3, 16, 7));
        g.fill(new Ellipse2D.Double(7, 13, 2, 2));
        g.fill(new Ellipse2D.Double(11, 13, 2, 2));
        g.fill(new Ellipse2D.Double(15, 13, 2, 2));
    }

    private void cross(Graphics2D g) {
        g.drawRoundRect(4, 3, 16, 18, 2, 2);
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(12, 8, 12, 17));
        g.draw(new Line2D.Double(8, 12.5, 16, 12.5));
    }

    private void document(Graphics2D g) {
        g.drawRoundRect(5, 3, 14, 18, 2, 2);
        g.draw(new Line2D.Double(8, 8, 16, 8));
        g.draw(new Line2D.Double(8, 12, 16, 12));
        g.draw(new Line2D.Double(8, 16, 14, 16));
    }

    private void capsule(Graphics2D g) {
        g.rotate(-Math.PI / 4, 12, 12);
        g.draw(new RoundRectangle2D.Double(4, 8, 16, 8, 4, 4));
        g.draw(new Line2D.Double(12, 8, 12, 16));
    }

    private void syringe(Graphics2D g) {
        g.rotate(-Math.PI / 4, 12, 12);
        g.drawRoundRect(7, 7, 10, 11, 2, 2);
        g.draw(new Line2D.Double(12, 7, 12, 3));
        g.draw(new Line2D.Double(9, 5, 15, 5));
        g.draw(new Line2D.Double(12, 18, 12, 22));
        g.draw(new Line2D.Double(9, 11, 15, 11));
        g.draw(new Line2D.Double(9, 14, 15, 14));
    }

    private void boxes(Graphics2D g) {
        Path2D shape = new Path2D.Double();
        shape.moveTo(3, 7); shape.lineTo(12, 3); shape.lineTo(21, 7); shape.lineTo(12, 12); shape.closePath();
        shape.moveTo(3, 7); shape.lineTo(3, 17); shape.lineTo(12, 22); shape.lineTo(12, 12);
        shape.moveTo(21, 7); shape.lineTo(21, 17); shape.lineTo(12, 22);
        shape.moveTo(12, 3); shape.lineTo(12, 12);
        g.draw(shape);
    }

    private void receipt(Graphics2D g) {
        Path2D shape = new Path2D.Double();
        shape.moveTo(5, 3); shape.lineTo(19, 3); shape.lineTo(19, 21);
        shape.lineTo(16, 19); shape.lineTo(13, 21); shape.lineTo(10, 19);
        shape.lineTo(7, 21); shape.lineTo(5, 20); shape.closePath();
        g.draw(shape);
        g.draw(new Line2D.Double(8, 8, 16, 8));
        g.draw(new Line2D.Double(8, 12, 16, 12));
        g.draw(new Line2D.Double(8, 16, 13, 16));
    }

    private void bell(Graphics2D g) {
        Path2D shape = new Path2D.Double();
        shape.moveTo(5, 17); shape.curveTo(7, 14, 6, 7, 9, 5);
        shape.curveTo(12, 2, 16, 5, 17, 8); shape.curveTo(18, 12, 17, 15, 20, 17);
        shape.closePath();
        g.draw(shape);
        g.draw(new Line2D.Double(4, 18, 20, 18));
        g.draw(new java.awt.geom.Arc2D.Double(9, 19, 6, 3, 180, 180, java.awt.geom.Arc2D.OPEN));
    }

    private void clock(Graphics2D g) {
        g.draw(new Ellipse2D.Double(3, 3, 18, 18));
        g.draw(new Line2D.Double(12, 6, 12, 12));
        g.draw(new Line2D.Double(12, 12, 17, 14));
    }

    private void logout(Graphics2D g) {
        g.drawRoundRect(3, 4, 11, 16, 2, 2);
        g.draw(new Line2D.Double(10, 12, 21, 12));
        g.draw(new Line2D.Double(17, 8, 21, 12));
        g.draw(new Line2D.Double(17, 16, 21, 12));
    }
}
