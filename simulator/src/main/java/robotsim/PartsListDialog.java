package robotsim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/** Every part a robot design uses, with pictures and counts. Can be saved as a CSV spreadsheet. */
final class PartsListDialog {

    private PartsListDialog() {
    }

    static void show(Window owner, final RobotDesign design) {
        final JDialog dialog = new JDialog(owner, "Parts list: " + design.name);
        List<RobotDesign.PartCount> parts = design.partsList();
        int total = 0;
        for (RobotDesign.PartCount p : parts) {
            total += p.count;
        }

        JPanel table = new JPanel(new GridBagLayout());
        table.setBackground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 8, 4, 8);
        c.anchor = GridBagConstraints.WEST;
        String category = null;
        int row = 0;
        for (RobotDesign.PartCount p : parts) {
            if (!p.part.category.equals(category)) {
                category = p.part.category;
                JLabel heading = new JLabel(category);
                heading.setFont(heading.getFont().deriveFont(Font.BOLD, 13f));
                heading.setForeground(new Color(0x6D28D9));
                c.gridx = 0;
                c.gridy = row++;
                c.gridwidth = 4;
                c.insets = new Insets(10, 8, 2, 8);
                table.add(heading, c);
                c.gridwidth = 1;
                c.insets = new Insets(3, 8, 3, 8);
            }
            c.gridy = row++;
            c.gridx = 0;
            table.add(new JLabel(PartIcons.icon(p.part, 40)), c);
            c.gridx = 1;
            JLabel count = new JLabel("× " + p.count);
            count.setFont(count.getFont().deriveFont(Font.BOLD, 14f));
            table.add(count, c);
            c.gridx = 2;
            JLabel name = new JLabel("<html><b>" + p.part.name + "</b><br><font color='#59636e'>" + details(p.part)
                    + "</font></html>");
            table.add(name, c);
            c.gridx = 3;
            JLabel ports = new JLabel(p.ports.isEmpty() ? "" : "Ports: " + String.join(", ", p.ports));
            ports.setForeground(new Color(0x1F6FEB));
            table.add(ports, c);
        }
        c.gridy = row;
        c.gridx = 0;
        c.weighty = 1;
        table.add(new JLabel(" "), c);

        JLabel header = new JLabel("<html><b>" + design.name + "</b>: " + total + " parts, " + parts.size()
                + " different kinds</html>");
        header.setBorder(BorderFactory.createEmptyBorder(10, 12, 6, 12));
        header.setFont(header.getFont().deriveFont(14f));

        JButton save = new JButton("Save as CSV...");
        save.addActionListener(e -> saveCsv(dialog, design));
        JButton close = new JButton("Close");
        close.addActionListener(e -> dialog.dispose());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(save);
        buttons.add(close);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(header, BorderLayout.NORTH);
        dialog.getContentPane().add(scroll, BorderLayout.CENTER);
        dialog.getContentPane().add(buttons, BorderLayout.SOUTH);
        dialog.setSize(560, 620);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    /** One line about a part: its useful numbers, or what it's for. */
    static String details(PartCatalog.PartType t) {
        switch (t.kind) {
            case MOTOR:
                return String.format("%.0f RPM, %s ticks per turn", t.number("rpm"), trim(t.number("ticksPerRev")));
            case WHEEL:
                return String.format("%s-inch diameter", trim(t.number("diameter")));
            default:
                return t.description;
        }
    }

    private static String trim(double d) {
        return d == Math.rint(d) ? String.valueOf((long) d) : String.valueOf(d);
    }

    /** The parts list as a CSV file: Part, Category, Count, Details, Ports. */
    static String csv(RobotDesign design) {
        StringBuilder sb = new StringBuilder("Part,Category,Count,Details,Ports\n");
        for (RobotDesign.PartCount p : design.partsList()) {
            sb.append(cell(p.part.name)).append(',').append(cell(p.part.category)).append(',').append(p.count)
                    .append(',').append(cell(details(p.part))).append(',').append(cell(String.join("; ", p.ports)))
                    .append('\n');
        }
        return sb.toString();
    }

    private static String cell(String s) {
        return s.contains(",") || s.contains("\"") ? "\"" + s.replace("\"", "\"\"") + "\"" : s;
    }

    private static void saveCsv(Component parent, RobotDesign design) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(design.name + " parts.csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("CSV spreadsheet", "csv"));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File f = chooser.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".csv")) {
            f = new File(f.getParentFile(), f.getName() + ".csv");
        }
        try {
            Files.write(f.toPath(), csv(design).getBytes(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent, "Couldn't save: " + ex.getMessage(), "Parts list",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
