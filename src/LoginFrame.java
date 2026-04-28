import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import quickclinic.DBConnection;

public class LoginFrame extends JFrame {

    private static final String USERNAME_PLACEHOLDER = "Enter your username";
    private static final String PASSWORD_PLACEHOLDER = "Enter your password";

    public LoginFrame() {
        initComponents();
        postInitStyle();
        installPlaceholders();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        rootPanel = new javax.swing.JPanel();
        cardPanel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        lblError = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Quick Clinic - Login");
        setMinimumSize(new java.awt.Dimension(900, 780));
        setPreferredSize(new java.awt.Dimension(900, 780));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        rootPanel.setBackground(new java.awt.Color(234, 236, 242));
        rootPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 18, 18, 18));
        rootPanel.setLayout(new java.awt.GridBagLayout());

        cardPanel.setBackground(new java.awt.Color(255, 255, 255));
        cardPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(40, 38, 26, 38));
        cardPanel.setMinimumSize(new java.awt.Dimension(540, 700));
        cardPanel.setPreferredSize(new java.awt.Dimension(540, 700));

        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Quick Clinic");

        lblSubtitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSubtitle.setText("Appointment System");

        lblUsername.setText("Username");

        txtUsername.setText("jTextField1");
        txtUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsernameActionPerformed(evt);
            }
        });

        lblPassword.setText("Password");

        txtPassword.setText("jPasswordField1");

        btnLogin.setText("Login");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        lblError.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblError.setText(" ");

        javax.swing.GroupLayout cardPanelLayout = new javax.swing.GroupLayout(cardPanel);
        cardPanel.setLayout(cardPanelLayout);
        cardPanelLayout.setHorizontalGroup(
            cardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardPanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(cardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtUsername)
                    .addComponent(lblPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPassword)
                    .addComponent(btnLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(22, 22, 22))
            .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblSubtitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        cardPanelLayout.setVerticalGroup(
            cardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardPanelLayout.createSequentialGroup()
                .addComponent(lblTitle, 74, 74, 74)
                .addGap(0, 0, 0)
                .addComponent(lblSubtitle, 38, 38, 38)
                .addGap(62, 62, 62)
                .addComponent(lblUsername, 30, 30, 30)
                .addGap(8, 8, 8)
                .addComponent(txtUsername, 58, 58, 58)
                .addGap(30, 30, 30)
                .addComponent(lblPassword, 30, 30, 30)
                .addGap(8, 8, 8)
                .addComponent(txtPassword, 58, 58, 58)
                .addGap(42, 42, 42)
                .addComponent(btnLogin, 68, 68, 68)
                .addGap(12, 12, 12)
                .addComponent(lblError, 24, 24, 24)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        rootPanel.add(cardPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(rootPanel, gridBagConstraints);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        lblError.setForeground(new Color(188, 34, 50));
        lblError.setText(" ");
        resetLoginFieldStyles();

        String username = readUsername();
        String password = readPassword();

        if (username.isEmpty()) {
            markLoginFieldInvalid(txtUsername);
            lblError.setText("Username is required.");
            return;
        }
        if (password.isEmpty()) {
            markLoginFieldInvalid(txtPassword);
            lblError.setText("Password is required.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                lblError.setText("Database connection failed. Make sure MySQL is running and MySQL connector is configured.");
                return;
            }

            String sql = "SELECT * FROM account WHERE username=? AND password=? AND status='active'";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, username);
                pst.setString(2, password);

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        String role = resolveAccountRole(rs, username);
                        lblError.setForeground(new Color(16, 133, 74));
                        lblError.setText("Login Successful!");
                        boolean receptionist = "receptionist".equalsIgnoreCase(role);
                        new AdminDashboardFrame(username, receptionist).setVisible(true);
                        this.dispose();
                    } else {
                        lblError.setForeground(new Color(188, 34, 50));
                        lblError.setText("Invalid username or password.");
                        markLoginFieldInvalid(txtUsername);
                        markLoginFieldInvalid(txtPassword);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblError.setForeground(new Color(188, 34, 50));
            if (e.getSQLState() != null && e.getSQLState().startsWith("08")) {
                lblError.setText("Cannot connect to database. Please check server connection and try again.");
            } else {
                lblError.setText("Login request failed. Please verify your account table/setup and try again.");
            }
        }
    }//GEN-LAST:event_btnLoginActionPerformed

    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsernameActionPerformed

    private void postInitStyle() {
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 64));
        lblTitle.setForeground(new Color(16, 24, 44));

        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblSubtitle.setForeground(new Color(82, 95, 119));

        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblUsername.setForeground(new Color(19, 28, 48));

        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblPassword.setForeground(new Color(19, 28, 48));

        lblError.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblError.setForeground(new Color(188, 34, 50));

        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    private void installPlaceholders() {
        installTextPlaceholder(txtUsername, USERNAME_PLACEHOLDER);
        installPasswordPlaceholder(txtPassword, PASSWORD_PLACEHOLDER);
    }

    private void installTextPlaceholder(JTextField field, String placeholder) {
        field.setForeground(new Color(150, 157, 171));
        field.setText(placeholder);
        field.putClientProperty("placeholder.active", Boolean.TRUE);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                boolean active = Boolean.TRUE.equals(field.getClientProperty("placeholder.active"));
                if (active) {
                    field.setText("");
                    field.setForeground(new Color(28, 36, 54));
                    field.putClientProperty("placeholder.active", Boolean.FALSE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setForeground(new Color(150, 157, 171));
                    field.setText(placeholder);
                    field.putClientProperty("placeholder.active", Boolean.TRUE);
                }
            }
        });
    }

    private void installPasswordPlaceholder(javax.swing.JPasswordField field, String placeholder) {
        final char defaultEcho = field.getEchoChar();
        field.setEchoChar((char) 0);
        field.setForeground(new Color(150, 157, 171));
        field.setText(placeholder);
        field.putClientProperty("placeholder.active", Boolean.TRUE);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                boolean active = Boolean.TRUE.equals(field.getClientProperty("placeholder.active"));
                if (active) {
                    field.setText("");
                    field.setEchoChar(defaultEcho);
                    field.setForeground(new Color(28, 36, 54));
                    field.putClientProperty("placeholder.active", Boolean.FALSE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (new String(field.getPassword()).trim().isEmpty()) {
                    field.setEchoChar((char) 0);
                    field.setForeground(new Color(150, 157, 171));
                    field.setText(placeholder);
                    field.putClientProperty("placeholder.active", Boolean.TRUE);
                }
            }
        });
    }

    private String readUsername() {
        boolean active = Boolean.TRUE.equals(txtUsername.getClientProperty("placeholder.active"));
        return active ? "" : txtUsername.getText().trim();
    }

    private String readPassword() {
        boolean active = Boolean.TRUE.equals(txtPassword.getClientProperty("placeholder.active"));
        return active ? "" : new String(txtPassword.getPassword());
    }

    private void resetLoginFieldStyles() {
        txtUsername.setBackground(Color.WHITE);
        txtPassword.setBackground(Color.WHITE);
    }

    private void markLoginFieldInvalid(javax.swing.JComponent field) {
        field.setBackground(new Color(255, 244, 244));
    }

    private String resolveAccountRole(ResultSet rs, String username) {
        try {
            String role = rs.getString("role");
            if (role != null && !role.isBlank()) {
                return role.trim().toLowerCase();
            }
        } catch (SQLException ignored) {
            // Older tables may not have the role column yet.
        }
        return "admin".equalsIgnoreCase(username) ? "admin" : "receptionist";
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // Keep default look and feel when Nimbus is unavailable.
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    private static class RoundedCardPanel extends JPanel {

        private final int radius = 24;

        RoundedCardPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(28, 39, 74, 20));
            g2.fillRoundRect(2, 6, getWidth() - 4, getHeight() - 8, radius, radius);

            g2.setColor(new Color(255, 255, 255));
            g2.fillRoundRect(0, 0, getWidth(), getHeight() - 4, radius, radius);

            g2.setColor(new Color(229, 233, 242));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 5, radius, radius);
            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class RoundedButton extends JButton {

        private final int arc = 18;

        RoundedButton() {
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
            setContentAreaFilled(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, new Color(69, 118, 238), getWidth(), 0, new Color(52, 90, 219)));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(63, 109, 230));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
        }
    }

    private static class RoundedTextField extends javax.swing.JTextField {

        private final int arc = 20;

        RoundedTextField() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            setForeground(new Color(28, 36, 54));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(220, 225, 234));
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
        }

        @Override
        public Insets getInsets() {
            return new Insets(10, 18, 10, 18);
        }
    }

    private static class RoundedPasswordField extends javax.swing.JPasswordField {

        private final int arc = 20;

        RoundedPasswordField() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            setForeground(new Color(28, 36, 54));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(220, 225, 234));
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
        }

        @Override
        public Insets getInsets() {
            return new Insets(10, 18, 10, 18);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel rootPanel;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
