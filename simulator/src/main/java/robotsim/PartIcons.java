package robotsim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

/** Small pictures of each part, for the parts list and (later) the robot builder's menu. */
final class PartIcons {

    private static final Map<String, ImageIcon> CACHE = new HashMap<String, ImageIcon>();

    private PartIcons() {
    }

    static synchronized ImageIcon icon(PartCatalog.PartType t, int size) {
        String key = t.id + "@" + size;
        ImageIcon icon = CACHE.get(key);
        if (icon == null) {
            icon = new ImageIcon(draw(t, size));
            CACHE.put(key, icon);
        }
        return icon;
    }

    static BufferedImage draw(PartCatalog.PartType t, int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.scale(size / 48.0, size / 48.0);  // draw on a 48 x 48 grid
        Color c = new Color(t.color);
        Color dark = new Color(0x1F2328);
        Color metal = new Color(0x9AA3AD);
        g.setStroke(new BasicStroke(1.4f));
        switch (t.kind) {
            case CHASSIS:
                g.setPaint(shade(c));
                g.fill(new Rectangle2D.Double(6, 6, 36, 36));
                g.setColor(new Color(0xEAEEF2));
                g.fill(new Rectangle2D.Double(12, 12, 24, 24));
                g.setColor(dark);
                g.draw(new Rectangle2D.Double(6, 6, 36, 36));
                g.draw(new Rectangle2D.Double(12, 12, 24, 24));
                break;
            case CHANNEL:
                boolean upright = t.height > t.length;
                Rectangle2D bar = upright ? new Rectangle2D.Double(19, 4, 10, 40) : new Rectangle2D.Double(4, 19, 40, 10);
                g.setPaint(shade(c));
                g.fill(bar);
                g.setColor(dark);
                g.draw(bar);
                for (int i = 0; i < 5; i++) {
                    double hx = upright ? 24 : 8 + i * 8, hy = upright ? 8 + i * 8 : 24;
                    g.fill(new Ellipse2D.Double(hx - 1.5, hy - 1.5, 3, 3));
                }
                break;
            case PLATE:
                g.setPaint(shade(c));
                g.fill(new Rectangle2D.Double(5, 10, 38, 28));
                g.setColor(dark);
                g.draw(new Rectangle2D.Double(5, 10, 38, 28));
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 3; j++) {
                        g.fill(new Ellipse2D.Double(11 + i * 9, 16 + j * 8, 3, 3));
                    }
                }
                break;
            case BRACKET: {
                Path2D l = new Path2D.Double();
                l.moveTo(10, 8);
                l.lineTo(20, 8);
                l.lineTo(20, 30);
                l.lineTo(40, 30);
                l.lineTo(40, 40);
                l.lineTo(10, 40);
                l.closePath();
                g.setPaint(shade(c));
                g.fill(l);
                g.setColor(dark);
                g.draw(l);
                break;
            }
            case HUB:
                g.setPaint(shade(c));
                g.fill(new RoundRectangle2D.Double(5, 9, 38, 30, 6, 6));
                g.setColor(new Color(0x2EA043));
                g.fill(new Ellipse2D.Double(35, 13, 4, 4));
                g.setColor(metal);
                for (int i = 0; i < 4; i++) {
                    g.fill(new Rectangle2D.Double(10 + i * 7, 31, 4, 4));
                }
                break;
            case BATTERY:
                g.setPaint(shade(c));
                g.fill(new RoundRectangle2D.Double(6, 12, 36, 26, 4, 4));
                g.setColor(dark);
                g.fill(new Rectangle2D.Double(12, 8, 6, 4));
                g.fill(new Rectangle2D.Double(30, 8, 6, 4));
                g.setColor(Color.WHITE);
                g.drawString("12V", 14, 30);
                break;
            case SWITCH:
                g.setPaint(shade(c));
                g.fill(new RoundRectangle2D.Double(10, 16, 28, 18, 4, 4));
                g.setColor(Color.WHITE);
                g.fill(new RoundRectangle2D.Double(16, 20, 8, 10, 3, 3));
                break;
            case MOTOR:
                g.setPaint(shade(c));
                g.fill(new RoundRectangle2D.Double(4, 15, 26, 18, 8, 8));
                g.setPaint(shade(new Color(0xC9A227)));
                g.fill(new Rectangle2D.Double(28, 14, 10, 20));
                g.setColor(metal);
                g.fill(new Rectangle2D.Double(38, 22, 7, 4));
                break;
            case SERVO:
                g.setPaint(shade(c));
                g.fill(new RoundRectangle2D.Double(8, 14, 32, 22, 4, 4));
                g.setColor(Color.WHITE);
                g.fill(new Ellipse2D.Double(26, 8, 10, 10));
                g.fill(new Rectangle2D.Double(30, 6, 2, 14));
                break;
            case WHEEL:
                g.setPaint(shade(c));
                g.fill(new Ellipse2D.Double(4, 4, 40, 40));
                g.setColor(metal);
                g.fill(new Ellipse2D.Double(16, 16, 16, 16));
                g.setColor(dark);
                g.fill(new Ellipse2D.Double(21, 21, 6, 6));
                if (!Double.isNaN(t.number("omni"))) {
                    g.setColor(new Color(0x8C959F));
                    for (int i = 0; i < 8; i++) {
                        double a = i * Math.PI / 4;
                        g.fill(new Ellipse2D.Double(24 + 17 * Math.cos(a) - 2.5, 24 + 17 * Math.sin(a) - 2.5, 5, 5));
                    }
                }
                break;
            case ARM:
                g.setPaint(shade(c));
                g.fill(new RoundRectangle2D.Double(6, 20, 38, 8, 4, 4));
                g.setColor(dark);
                g.fill(new Ellipse2D.Double(7, 21, 6, 6));
                break;
            case CLAW:
                g.setPaint(shade(c));
                g.fill(new Rectangle2D.Double(8, 20, 12, 8));
                g.fill(new Rectangle2D.Double(18, 10, 22, 5));
                g.fill(new Rectangle2D.Double(18, 33, 22, 5));
                g.setColor(dark);
                g.fill(new Rectangle2D.Double(4, 18, 6, 12));
                break;
            case HOOK: {
                g.setColor(new Color(0x57606A));
                g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Path2D j = new Path2D.Double();
                j.moveTo(24, 6);
                j.lineTo(24, 30);
                j.quadTo(24, 40, 16, 40);
                j.quadTo(10, 40, 10, 33);
                g.draw(j);
                break;
            }
            case TOUCH:
                g.setPaint(shade(c));
                g.fill(new RoundRectangle2D.Double(4, 18, 40, 12, 4, 4));
                g.setColor(new Color(0xCF222E));
                g.fill(new Ellipse2D.Double(19, 19, 10, 10));
                break;
            case COLOR:
                g.setPaint(shade(c));
                g.fill(new RoundRectangle2D.Double(10, 10, 28, 28, 6, 6));
                g.setColor(dark);
                g.fill(new Ellipse2D.Double(17, 17, 14, 14));
                g.setColor(Color.WHITE);
                g.fill(new Ellipse2D.Double(21, 20, 4, 4));
                break;
            default:
                g.setColor(c);
                g.fill(new Rectangle2D.Double(8, 8, 32, 32));
                break;
        }
        g.dispose();
        return img;
    }

    /** A soft top-to-bottom shading, so flat shapes look a little solid. */
    private static GradientPaint shade(Color c) {
        return new GradientPaint(0, 4, c.brighter(), 0, 44, c.darker());
    }
}
