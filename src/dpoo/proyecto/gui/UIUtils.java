package dpoo.proyecto.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class UIUtils {

    public static class LabeledField {
        public final String label;
        public final JComponent field;

        public LabeledField(String label, JComponent field) {
            this.label = label;
            this.field = field;
        }
    }

    public static JPanel formPanel(String title, LabeledField... fields) {
        return formPanel(title, null, fields);
    }

    public static JPanel formPanel(String title, JComponent footer, LabeledField... fields) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        JPanel grid = new JPanel(new GridLayout(fields.length, 2, 8, 8));
        for (LabeledField lf : fields) {
            grid.add(new JLabel(lf.label));
            grid.add(lf.field);
        }
        panel.add(grid, BorderLayout.CENTER);
        if (footer != null) {
            panel.add(footer, BorderLayout.SOUTH);
        }
        return panel;
    }

    public static JScrollPane scroll(JComponent component) {
        JScrollPane sp = new JScrollPane(component);
        sp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return sp;
    }
}
