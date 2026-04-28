import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class AdminViewPanel extends JPanel {

    public AdminViewPanel() {
        initComponents();
        postInitStyle();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        headerHostPanel = new javax.swing.JPanel();
        bodyHostPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        headerHostPanel.setOpaque(false);
        headerHostPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(headerHostPanel, gridBagConstraints);

        bodyHostPanel.setOpaque(false);
        bodyHostPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(bodyHostPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void postInitStyle() {
        setBackground(new Color(241, 245, 252));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 22, 34));
    }

    public JPanel getHeaderHostPanel() {
        return headerHostPanel;
    }

    public JPanel getBodyHostPanel() {
        return bodyHostPanel;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bodyHostPanel;
    private javax.swing.JPanel headerHostPanel;
    // End of variables declaration//GEN-END:variables
}
