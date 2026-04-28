import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import quickclinic.DBConnection;

public class AdminDashboardFrame extends JFrame {

    private static final String VIEW_DASHBOARD = "dashboard";
    private static final String VIEW_APPOINTMENTS = "appointments";
    private static final String VIEW_PATIENTS = "patients";
    private static final String VIEW_LOG = "log";
    private static final String VIEW_RECEPTIONISTS = "receptionists";
    private static final int TABLE_VISIBLE_ROWS = 4;
    private static final int TABLE_ROW_HEIGHT = 96;
    private static final int TABLE_ROW_GAP = 8;
    private static final int TABLE_SCROLL_HEIGHT =
        (TABLE_VISIBLE_ROWS * TABLE_ROW_HEIGHT) + ((TABLE_VISIBLE_ROWS - 1) * TABLE_ROW_GAP) + 20;

    private final String adminName;
    private final boolean receptionistMode;
    private final List<AppointmentRecord> appointmentRecords = new ArrayList<>();
    private final List<PatientRecord> patientRecords = new ArrayList<>();
    private final List<ReceptionistRecord> receptionistRecords = new ArrayList<>();
    private static final DateTimeFormatter DATE_LABEL_FORMAT =
        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter CALENDAR_INPUT_FORMAT =
        DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter APPOINTMENT_INPUT_DATE_FORMAT =
        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter APPOINTMENT_INPUT_TIME_FORMAT =
        DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    private CardLayout mainCardLayout;
    private JPanel mainCardPanel;

    private NavItem dashboardNav;
    private NavItem appointmentsNav;
    private NavItem patientsNav;
    private NavItem logNav;
    private NavItem receptionistsNav;

    private JLabel apptTotalValue;
    private JLabel apptPendingValue;
    private JLabel apptCompletedValue;
    private CardLayout appointmentModeLayout;
    private JPanel appointmentModePanel;
    private JPanel appointmentTableRows;
    private JPanel appointmentCalendarRows;
    private TogglePill tableToggle;
    private TogglePill calendarToggle;
    private JTextField appointmentSearchField;
    private JTextField appointmentCalendarDateField;
    private JLabel appointmentCalendarTitleLabel;
    private LocalDate appointmentCalendarDate = LocalDate.now();
    private String appointmentSearchQuery = "";

    private JLabel logTotalValue;
    private JLabel logCompletedValue;
    private JLabel logCancelledValue;
    private JPanel logRows;
    private TogglePill logAllToggle;
    private TogglePill logCompletedToggle;
    private TogglePill logCancelledToggle;
    private JTextField logSearchField;
    private String logFilter = "all";
    private String logSearchQuery = "";

    private JLabel patientTotalValue;
    private JPanel patientRows;
    private JTextField patientSearchField;
    private String patientSearchQuery = "";

    private JLabel receptionistTotalValue;
    private JLabel receptionistActiveValue;
    private JLabel receptionistDeactivatedValue;
    private JPanel receptionistRows;
    private TogglePill receptionistActiveToggle;
    private TogglePill receptionistDeactivatedToggle;
    private JPanel receptionistAddButtonWrap;
    private JTextField receptionistSearchField;
    private String receptionistFilter = "active";
    private String receptionistSearchQuery = "";
    private boolean databaseCleanupDone = false;

    private JLabel dashboardTodayValue;
    private JLabel dashboardPendingValue;
    private JLabel dashboardCompletedValue;
    private JLabel dashboardPatientsValue;
    private JLabel dashboardReceptionistsValue;
    private JPanel dashboardTodayRows;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentHostPanel;
    private javax.swing.JPanel previewAppointmentDetailsPanel;
    private javax.swing.JLabel previewAppointmentPatientLabel;
    private javax.swing.JLabel previewAppointmentReasonLabel;
    private javax.swing.JPanel previewAppointmentRowPanel;
    private javax.swing.JLabel previewAppointmentStatusLabel;
    private javax.swing.JLabel previewAppointmentTimeLabel;
    private javax.swing.JButton previewAppointmentsButton;
    private javax.swing.JPanel previewCompletedCardPanel;
    private javax.swing.JLabel previewCompletedCardSubLabel;
    private javax.swing.JLabel previewCompletedCardTitleLabel;
    private javax.swing.JLabel previewCompletedCardValueLabel;
    private javax.swing.JPanel previewDashboardBodyPanel;
    private javax.swing.JButton previewDashboardButton;
    private javax.swing.JPanel previewDashboardContent;
    private javax.swing.JLabel previewHeaderLabel;
    private javax.swing.JPanel previewHeaderPanel;
    private javax.swing.JButton previewLogButton;
    private javax.swing.JPanel previewMainPanel;
    private javax.swing.JButton previewNewAppointmentButton;
    private javax.swing.JButton previewPatientsButton;
    private javax.swing.JPanel previewPatientsCardPanel;
    private javax.swing.JLabel previewPatientsCardSubLabel;
    private javax.swing.JLabel previewPatientsCardTitleLabel;
    private javax.swing.JLabel previewPatientsCardValueLabel;
    private javax.swing.JPanel previewPendingCardPanel;
    private javax.swing.JLabel previewPendingCardSubLabel;
    private javax.swing.JLabel previewPendingCardTitleLabel;
    private javax.swing.JLabel previewPendingCardValueLabel;
    private javax.swing.JButton previewReceptionistsButton;
    private javax.swing.JPanel previewSidebarCenterPanel;
    private javax.swing.JLabel previewSidebarFooterLabel;
    private javax.swing.JPanel previewSidebarFooterPanel;
    private javax.swing.JLabel previewSidebarLabel;
    private javax.swing.JButton previewSidebarLogoutButton;
    private javax.swing.JPanel previewSidebarMenuPanel;
    private javax.swing.JPanel previewSidebarPanel;
    private javax.swing.JPanel previewStatsGridPanel;
    private javax.swing.JPanel previewTodayAppointmentsCardPanel;
    private javax.swing.JPanel previewTodayAppointmentsHeaderPanel;
    private javax.swing.JLabel previewTodayAppointmentsTitleLabel;
    private javax.swing.JPanel previewTodayCardPanel;
    private javax.swing.JLabel previewTodayCardSubLabel;
    private javax.swing.JLabel previewTodayCardTitleLabel;
    private javax.swing.JLabel previewTodayCardValueLabel;
    private javax.swing.JLabel previewViewAllLabel;
    private javax.swing.JLabel previewWelcomeLabel;
    private javax.swing.JPanel rootPanel;
    // End of variables declaration//GEN-END:variables

    public AdminDashboardFrame() {
        this("admin", false);
    }

    public AdminDashboardFrame(String adminName) {
        this(adminName, false);
    }

    public AdminDashboardFrame(String adminName, boolean receptionistMode) {
        this.adminName = (adminName == null || adminName.isBlank()) ? "admin" : adminName.toLowerCase();
        this.receptionistMode = receptionistMode;
        initComponents();
        if (java.beans.Beans.isDesignTime()) {
            installDesignTimePreview();
            return;
        }
        if (receptionistMode) {
            setTitle("Quick Clinic - Receptionist Dashboard");
        }
        initDashboardLayout();
        ensureDatabaseSchema();
        refreshAllViews();
        contentHostPanel.revalidate();
        contentHostPanel.repaint();
        pack();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        rootPanel = new javax.swing.JPanel();
        contentHostPanel = new javax.swing.JPanel();
        previewSidebarPanel = new javax.swing.JPanel();
        previewSidebarLabel = new javax.swing.JLabel();
        previewSidebarCenterPanel = new javax.swing.JPanel();
        previewSidebarMenuPanel = new javax.swing.JPanel();
        previewDashboardButton = new javax.swing.JButton();
        previewAppointmentsButton = new javax.swing.JButton();
        previewPatientsButton = new javax.swing.JButton();
        previewLogButton = new javax.swing.JButton();
        previewReceptionistsButton = new javax.swing.JButton();
        previewSidebarFooterPanel = new javax.swing.JPanel();
        previewSidebarFooterLabel = new javax.swing.JLabel();
        previewSidebarLogoutButton = new javax.swing.JButton();
        previewMainPanel = new javax.swing.JPanel();
        previewHeaderPanel = new javax.swing.JPanel();
        previewHeaderLabel = new javax.swing.JLabel();
        previewNewAppointmentButton = new javax.swing.JButton();
        previewDashboardContent = new javax.swing.JPanel();
        previewWelcomeLabel = new javax.swing.JLabel();
        previewDashboardBodyPanel = new javax.swing.JPanel();
        previewStatsGridPanel = new javax.swing.JPanel();
        previewTodayCardPanel = new javax.swing.JPanel();
        previewTodayCardTitleLabel = new javax.swing.JLabel();
        previewTodayCardValueLabel = new javax.swing.JLabel();
        previewTodayCardSubLabel = new javax.swing.JLabel();
        previewPendingCardPanel = new javax.swing.JPanel();
        previewPendingCardTitleLabel = new javax.swing.JLabel();
        previewPendingCardValueLabel = new javax.swing.JLabel();
        previewPendingCardSubLabel = new javax.swing.JLabel();
        previewCompletedCardPanel = new javax.swing.JPanel();
        previewCompletedCardTitleLabel = new javax.swing.JLabel();
        previewCompletedCardValueLabel = new javax.swing.JLabel();
        previewCompletedCardSubLabel = new javax.swing.JLabel();
        previewPatientsCardPanel = new javax.swing.JPanel();
        previewPatientsCardTitleLabel = new javax.swing.JLabel();
        previewPatientsCardValueLabel = new javax.swing.JLabel();
        previewPatientsCardSubLabel = new javax.swing.JLabel();
        previewTodayAppointmentsCardPanel = new javax.swing.JPanel();
        previewTodayAppointmentsHeaderPanel = new javax.swing.JPanel();
        previewTodayAppointmentsTitleLabel = new javax.swing.JLabel();
        previewViewAllLabel = new javax.swing.JLabel();
        previewAppointmentRowPanel = new javax.swing.JPanel();
        previewAppointmentTimeLabel = new javax.swing.JLabel();
        previewAppointmentDetailsPanel = new javax.swing.JPanel();
        previewAppointmentPatientLabel = new javax.swing.JLabel();
        previewAppointmentReasonLabel = new javax.swing.JLabel();
        previewAppointmentStatusLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Quick Clinic - Admin Dashboard");
        setMinimumSize(new java.awt.Dimension(1280, 800));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        rootPanel.setBackground(new java.awt.Color(240, 244, 250));
        rootPanel.setLayout(new java.awt.BorderLayout());

        contentHostPanel.setOpaque(false);
        contentHostPanel.setLayout(new java.awt.BorderLayout());

        previewSidebarPanel.setBackground(new java.awt.Color(17, 34, 76));
        previewSidebarPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(24, 22, 24, 22));
        previewSidebarPanel.setMinimumSize(new java.awt.Dimension(280, 200));
        previewSidebarPanel.setPreferredSize(new java.awt.Dimension(320, 800));
        previewSidebarPanel.setLayout(new java.awt.BorderLayout());

        previewSidebarLabel.setForeground(new java.awt.Color(255, 255, 255));
        previewSidebarLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        previewSidebarLabel.setText("<html><div style='font-family:Segoe UI;color:#FFFFFF;'><div style='font-size:18px;font-weight:700;'>Quick Clinic</div><div style='font-size:12px;color:#B8C4DE;margin-top:2px;margin-bottom:16px;'>Admin Panel</div><hr style='border:0;border-top:1px solid #3A4E76;'/></div></html>");
        previewSidebarLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        previewSidebarPanel.add(previewSidebarLabel, java.awt.BorderLayout.NORTH);

        previewSidebarCenterPanel.setOpaque(false);
        previewSidebarCenterPanel.setLayout(new java.awt.BorderLayout());

        previewSidebarMenuPanel.setOpaque(false);
        previewSidebarMenuPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 0, 0));
        previewSidebarMenuPanel.setLayout(new java.awt.GridLayout(5, 1));

        previewDashboardButton.setBackground(new java.awt.Color(63, 101, 228));
        previewDashboardButton.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        previewDashboardButton.setForeground(new java.awt.Color(255, 255, 255));
        previewDashboardButton.setText("Dashboard");
        previewDashboardButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 20, 12, 20));
        previewDashboardButton.setBorderPainted(false);
        previewDashboardButton.setFocusPainted(false);
        previewDashboardButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        previewDashboardButton.setIconTextGap(12);
        previewDashboardButton.setMaximumSize(new java.awt.Dimension(260, 54));
        previewDashboardButton.setMinimumSize(new java.awt.Dimension(210, 54));
        previewDashboardButton.setPreferredSize(new java.awt.Dimension(250, 54));
        previewDashboardButton.setRequestFocusEnabled(false);
        previewSidebarMenuPanel.add(previewDashboardButton);

        previewAppointmentsButton.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        previewAppointmentsButton.setBackground(new java.awt.Color(30, 51, 98));
        previewAppointmentsButton.setForeground(new java.awt.Color(217, 224, 242));
        previewAppointmentsButton.setText("Appointments");
        previewAppointmentsButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 20, 12, 20));
        previewAppointmentsButton.setBorderPainted(false);
        previewAppointmentsButton.setFocusPainted(false);
        previewAppointmentsButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        previewAppointmentsButton.setIconTextGap(12);
        previewAppointmentsButton.setRequestFocusEnabled(false);
        previewAppointmentsButton.setOpaque(true);
        previewSidebarMenuPanel.add(previewAppointmentsButton);

        previewPatientsButton.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        previewPatientsButton.setBackground(new java.awt.Color(30, 51, 98));
        previewPatientsButton.setForeground(new java.awt.Color(217, 224, 242));
        previewPatientsButton.setText("Patients");
        previewPatientsButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 20, 12, 20));
        previewPatientsButton.setBorderPainted(false);
        previewPatientsButton.setFocusPainted(false);
        previewPatientsButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        previewPatientsButton.setIconTextGap(12);
        previewPatientsButton.setRequestFocusEnabled(false);
        previewPatientsButton.setOpaque(true);
        previewSidebarMenuPanel.add(previewPatientsButton);

        previewLogButton.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        previewLogButton.setBackground(new java.awt.Color(30, 51, 98));
        previewLogButton.setForeground(new java.awt.Color(217, 224, 242));
        previewLogButton.setText("Log");
        previewLogButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 20, 12, 20));
        previewLogButton.setBorderPainted(false);
        previewLogButton.setFocusPainted(false);
        previewLogButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        previewLogButton.setIconTextGap(12);
        previewLogButton.setRequestFocusEnabled(false);
        previewLogButton.setOpaque(true);
        previewSidebarMenuPanel.add(previewLogButton);

        previewReceptionistsButton.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        previewReceptionistsButton.setBackground(new java.awt.Color(30, 51, 98));
        previewReceptionistsButton.setForeground(new java.awt.Color(217, 224, 242));
        previewReceptionistsButton.setText("Receptionists");
        previewReceptionistsButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 20, 12, 20));
        previewReceptionistsButton.setBorderPainted(false);
        previewReceptionistsButton.setFocusPainted(false);
        previewReceptionistsButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        previewReceptionistsButton.setIconTextGap(12);
        previewReceptionistsButton.setRequestFocusEnabled(false);
        previewReceptionistsButton.setOpaque(true);
        previewSidebarMenuPanel.add(previewReceptionistsButton);

        previewSidebarCenterPanel.add(previewSidebarMenuPanel, java.awt.BorderLayout.NORTH);

        previewSidebarPanel.add(previewSidebarCenterPanel, java.awt.BorderLayout.CENTER);

        previewSidebarFooterPanel.setOpaque(false);
        previewSidebarFooterPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 0, 0, 0));
        previewSidebarFooterPanel.setLayout(new java.awt.BorderLayout());

        previewSidebarFooterLabel.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        previewSidebarFooterLabel.setForeground(new java.awt.Color(184, 196, 222));
        previewSidebarFooterLabel.setText("<html><div style='margin-top:4px;'>Logged in as</div><div style='color:#FFFFFF;font-size:16px;font-weight:700;'>admin</div></html>");
        previewSidebarFooterLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 2, 10, 2));
        previewSidebarFooterPanel.add(previewSidebarFooterLabel, java.awt.BorderLayout.NORTH);

        previewSidebarLogoutButton.setBackground(new java.awt.Color(49, 73, 125));
        previewSidebarLogoutButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        previewSidebarLogoutButton.setForeground(new java.awt.Color(255, 255, 255));
        previewSidebarLogoutButton.setText("Logout");
        previewSidebarLogoutButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        previewSidebarLogoutButton.setFocusPainted(false);
        previewSidebarLogoutButton.setPreferredSize(new java.awt.Dimension(220, 48));
        previewSidebarLogoutButton.setRequestFocusEnabled(false);
        previewSidebarFooterPanel.add(previewSidebarLogoutButton, java.awt.BorderLayout.SOUTH);

        previewSidebarPanel.add(previewSidebarFooterPanel, java.awt.BorderLayout.SOUTH);

        contentHostPanel.add(previewSidebarPanel, java.awt.BorderLayout.WEST);

        previewMainPanel.setBackground(new java.awt.Color(241, 245, 252));
        previewMainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 36, 28, 36));
        previewMainPanel.setLayout(new java.awt.BorderLayout());

        previewHeaderPanel.setOpaque(false);
        previewHeaderPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 8, 0));
        previewHeaderPanel.setLayout(new java.awt.BorderLayout());

        previewHeaderLabel.setFont(new java.awt.Font("Segoe UI", 1, 33)); // NOI18N
        previewHeaderLabel.setForeground(new java.awt.Color(20, 30, 52));
        previewHeaderLabel.setText("Dashboard");
        previewHeaderPanel.add(previewHeaderLabel, java.awt.BorderLayout.WEST);

        previewNewAppointmentButton.setBackground(new java.awt.Color(63, 101, 228));
        previewNewAppointmentButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        previewNewAppointmentButton.setForeground(new java.awt.Color(255, 255, 255));
        previewNewAppointmentButton.setText("New Appointment");
        previewNewAppointmentButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 22, 12, 22));
        previewNewAppointmentButton.setFocusPainted(false);
        previewNewAppointmentButton.setPreferredSize(new java.awt.Dimension(266, 54));
        previewNewAppointmentButton.setRequestFocusEnabled(false);
        previewHeaderPanel.add(previewNewAppointmentButton, java.awt.BorderLayout.EAST);

        previewMainPanel.add(previewHeaderPanel, java.awt.BorderLayout.NORTH);

        previewDashboardContent.setOpaque(false);
        previewDashboardContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 0, 0, 0));
        previewDashboardContent.setLayout(new java.awt.BorderLayout());

        previewWelcomeLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        previewWelcomeLabel.setForeground(new java.awt.Color(90, 104, 134));
        previewWelcomeLabel.setText("Welcome back! Here's your overview for today.");
        previewWelcomeLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 8, 0));
        previewDashboardContent.add(previewWelcomeLabel, java.awt.BorderLayout.NORTH);

        previewDashboardBodyPanel.setOpaque(false);
        previewDashboardBodyPanel.setLayout(new java.awt.BorderLayout());

        previewStatsGridPanel.setOpaque(false);
        previewStatsGridPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 16, 0));
        previewStatsGridPanel.setLayout(new java.awt.GridLayout(2, 2));

        previewTodayCardPanel.setBackground(new java.awt.Color(255, 255, 255));
        previewTodayCardPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 20, 18, 20));
        previewTodayCardPanel.setLayout(new java.awt.BorderLayout());

        previewTodayCardTitleLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        previewTodayCardTitleLabel.setForeground(new java.awt.Color(33, 47, 83));
        previewTodayCardTitleLabel.setText("Today's Appointments");
        previewTodayCardPanel.add(previewTodayCardTitleLabel, java.awt.BorderLayout.NORTH);

        previewTodayCardValueLabel.setFont(new java.awt.Font("Segoe UI", 1, 52)); // NOI18N
        previewTodayCardValueLabel.setForeground(new java.awt.Color(57, 98, 226));
        previewTodayCardValueLabel.setText("0");
        previewTodayCardPanel.add(previewTodayCardValueLabel, java.awt.BorderLayout.CENTER);

        previewTodayCardSubLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        previewTodayCardSubLabel.setForeground(new java.awt.Color(107, 124, 157));
        previewTodayCardSubLabel.setText("No appointments today");
        previewTodayCardPanel.add(previewTodayCardSubLabel, java.awt.BorderLayout.SOUTH);

        previewStatsGridPanel.add(previewTodayCardPanel);

        previewPendingCardPanel.setBackground(new java.awt.Color(255, 255, 255));
        previewPendingCardPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 20, 18, 20));
        previewPendingCardPanel.setLayout(new java.awt.BorderLayout());

        previewPendingCardTitleLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        previewPendingCardTitleLabel.setForeground(new java.awt.Color(33, 47, 83));
        previewPendingCardTitleLabel.setText("Pending");
        previewPendingCardPanel.add(previewPendingCardTitleLabel, java.awt.BorderLayout.NORTH);

        previewPendingCardValueLabel.setFont(new java.awt.Font("Segoe UI", 1, 52)); // NOI18N
        previewPendingCardValueLabel.setForeground(new java.awt.Color(230, 148, 33));
        previewPendingCardValueLabel.setText("0");
        previewPendingCardPanel.add(previewPendingCardValueLabel, java.awt.BorderLayout.CENTER);

        previewPendingCardSubLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        previewPendingCardSubLabel.setForeground(new java.awt.Color(107, 124, 157));
        previewPendingCardSubLabel.setText("Appointments pending");
        previewPendingCardPanel.add(previewPendingCardSubLabel, java.awt.BorderLayout.SOUTH);

        previewStatsGridPanel.add(previewPendingCardPanel);

        previewCompletedCardPanel.setBackground(new java.awt.Color(255, 255, 255));
        previewCompletedCardPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 20, 18, 20));
        previewCompletedCardPanel.setLayout(new java.awt.BorderLayout());

        previewCompletedCardTitleLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        previewCompletedCardTitleLabel.setForeground(new java.awt.Color(33, 47, 83));
        previewCompletedCardTitleLabel.setText("Completed");
        previewCompletedCardPanel.add(previewCompletedCardTitleLabel, java.awt.BorderLayout.NORTH);

        previewCompletedCardValueLabel.setFont(new java.awt.Font("Segoe UI", 1, 52)); // NOI18N
        previewCompletedCardValueLabel.setForeground(new java.awt.Color(45, 173, 94));
        previewCompletedCardValueLabel.setText("0");
        previewCompletedCardPanel.add(previewCompletedCardValueLabel, java.awt.BorderLayout.CENTER);

        previewCompletedCardSubLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        previewCompletedCardSubLabel.setForeground(new java.awt.Color(107, 124, 157));
        previewCompletedCardSubLabel.setText("Appointments completed");
        previewCompletedCardPanel.add(previewCompletedCardSubLabel, java.awt.BorderLayout.SOUTH);

        previewStatsGridPanel.add(previewCompletedCardPanel);

        previewPatientsCardPanel.setBackground(new java.awt.Color(255, 255, 255));
        previewPatientsCardPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 20, 18, 20));
        previewPatientsCardPanel.setLayout(new java.awt.BorderLayout());

        previewPatientsCardTitleLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        previewPatientsCardTitleLabel.setForeground(new java.awt.Color(33, 47, 83));
        previewPatientsCardTitleLabel.setText("Total Patients");
        previewPatientsCardPanel.add(previewPatientsCardTitleLabel, java.awt.BorderLayout.NORTH);

        previewPatientsCardValueLabel.setFont(new java.awt.Font("Segoe UI", 1, 52)); // NOI18N
        previewPatientsCardValueLabel.setForeground(new java.awt.Color(52, 163, 188));
        previewPatientsCardValueLabel.setText("0");
        previewPatientsCardPanel.add(previewPatientsCardValueLabel, java.awt.BorderLayout.CENTER);

        previewPatientsCardSubLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        previewPatientsCardSubLabel.setForeground(new java.awt.Color(107, 124, 157));
        previewPatientsCardSubLabel.setText("Registered patients");
        previewPatientsCardPanel.add(previewPatientsCardSubLabel, java.awt.BorderLayout.SOUTH);

        previewStatsGridPanel.add(previewPatientsCardPanel);

        previewDashboardBodyPanel.add(previewStatsGridPanel, java.awt.BorderLayout.NORTH);

        previewTodayAppointmentsCardPanel.setBackground(new java.awt.Color(255, 255, 255));
        previewTodayAppointmentsCardPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 20, 20, 20));
        previewTodayAppointmentsCardPanel.setLayout(new java.awt.BorderLayout());

        previewTodayAppointmentsHeaderPanel.setOpaque(false);
        previewTodayAppointmentsHeaderPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0));
        previewTodayAppointmentsHeaderPanel.setLayout(new java.awt.BorderLayout());

        previewTodayAppointmentsTitleLabel.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        previewTodayAppointmentsTitleLabel.setForeground(new java.awt.Color(30, 42, 70));
        previewTodayAppointmentsTitleLabel.setText("Today's Appointments");
        previewTodayAppointmentsHeaderPanel.add(previewTodayAppointmentsTitleLabel, java.awt.BorderLayout.WEST);

        previewViewAllLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        previewViewAllLabel.setForeground(new java.awt.Color(63, 101, 228));
        previewViewAllLabel.setText("View All >");
        previewTodayAppointmentsHeaderPanel.add(previewViewAllLabel, java.awt.BorderLayout.EAST);

        previewTodayAppointmentsCardPanel.add(previewTodayAppointmentsHeaderPanel, java.awt.BorderLayout.NORTH);

        previewAppointmentRowPanel.setBackground(new java.awt.Color(248, 251, 255));
        previewAppointmentRowPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 16, 14, 16));
        previewAppointmentRowPanel.setLayout(new java.awt.BorderLayout());

        previewAppointmentTimeLabel.setFont(new java.awt.Font("Segoe UI", 1, 46)); // NOI18N
        previewAppointmentTimeLabel.setForeground(new java.awt.Color(63, 101, 228));
        previewAppointmentTimeLabel.setText("08:00");
        previewAppointmentRowPanel.add(previewAppointmentTimeLabel, java.awt.BorderLayout.WEST);

        previewAppointmentDetailsPanel.setOpaque(false);
        previewAppointmentDetailsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 16, 0, 0));
        previewAppointmentDetailsPanel.setLayout(new javax.swing.BoxLayout(previewAppointmentDetailsPanel, javax.swing.BoxLayout.Y_AXIS));

        previewAppointmentPatientLabel.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        previewAppointmentPatientLabel.setForeground(new java.awt.Color(36, 53, 83));
        previewAppointmentPatientLabel.setText("John Doe");
        previewAppointmentDetailsPanel.add(previewAppointmentPatientLabel);

        previewAppointmentReasonLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        previewAppointmentReasonLabel.setForeground(new java.awt.Color(107, 124, 156));
        previewAppointmentReasonLabel.setText("Regular checkup");
        previewAppointmentDetailsPanel.add(previewAppointmentReasonLabel);

        previewAppointmentRowPanel.add(previewAppointmentDetailsPanel, java.awt.BorderLayout.CENTER);

        previewAppointmentStatusLabel.setBackground(new java.awt.Color(255, 245, 231));
        previewAppointmentStatusLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        previewAppointmentStatusLabel.setForeground(new java.awt.Color(228, 154, 47));
        previewAppointmentStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        previewAppointmentStatusLabel.setText("pending");
        previewAppointmentStatusLabel.setOpaque(true);
        previewAppointmentStatusLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 14, 6, 14));
        previewAppointmentRowPanel.add(previewAppointmentStatusLabel, java.awt.BorderLayout.EAST);

        previewTodayAppointmentsCardPanel.add(previewAppointmentRowPanel, java.awt.BorderLayout.CENTER);

        previewDashboardBodyPanel.add(previewTodayAppointmentsCardPanel, java.awt.BorderLayout.CENTER);

        previewDashboardContent.add(previewDashboardBodyPanel, java.awt.BorderLayout.CENTER);

        previewMainPanel.add(previewDashboardContent, java.awt.BorderLayout.CENTER);

        contentHostPanel.add(previewMainPanel, java.awt.BorderLayout.CENTER);

        rootPanel.add(contentHostPanel, java.awt.BorderLayout.CENTER);

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

    private void initDashboardLayout() {
        contentHostPanel.removeAll();
        contentHostPanel.add(buildSidebar(), BorderLayout.WEST);

        mainCardLayout = new CardLayout();
        mainCardPanel = new JPanel(mainCardLayout);
        mainCardPanel.add(buildDashboardView(), VIEW_DASHBOARD);
        mainCardPanel.add(buildAppointmentsView(), VIEW_APPOINTMENTS);
        mainCardPanel.add(buildPatientsView(), VIEW_PATIENTS);
        mainCardPanel.add(buildLogView(), VIEW_LOG);
        if (!receptionistMode) {
            mainCardPanel.add(buildReceptionistsView(), VIEW_RECEPTIONISTS);
        }
        contentHostPanel.add(mainCardPanel, BorderLayout.CENTER);

        setActiveView(VIEW_DASHBOARD);
        contentHostPanel.revalidate();
        contentHostPanel.repaint();
    }

    private void installDesignTimePreview() {
        try {
            contentHostPanel.removeAll();
            contentHostPanel.add(buildSidebar(), BorderLayout.WEST);
            contentHostPanel.add(buildDashboardView(), BorderLayout.CENTER);
            contentHostPanel.revalidate();
            contentHostPanel.repaint();
        } catch (Exception ex) {
            contentHostPanel.removeAll();
            contentHostPanel.add(previewMainPanel, BorderLayout.CENTER);
        }
    }

    private void buildPreviewDashboardContent(JPanel host) {
        host.removeAll();
        host.setLayout(new BorderLayout(0, 14));

        JLabel welcome = new JLabel("Welcome back! Here's your overview for today.");
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcome.setForeground(new Color(90, 104, 134));
        host.add(welcome, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);

        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 16, 16));
        statsGrid.setOpaque(false);
        statsGrid.add(previewStatCard("Today's Appointments", "0", "No appointments today", new Color(57, 98, 226)));
        statsGrid.add(previewStatCard("Pending", "0", "Appointments pending", new Color(230, 148, 33)));
        statsGrid.add(previewStatCard("Completed", "0", "Appointments completed", new Color(45, 173, 94)));
        statsGrid.add(previewStatCard("Total Patients", "0", "Registered patients", new Color(52, 163, 188)));

        body.add(statsGrid, BorderLayout.NORTH);
        body.add(previewAppointmentCard(), BorderLayout.CENTER);
        host.add(body, BorderLayout.CENTER);
    }

    private JPanel previewStatCard(String titleText, String valueText, String subtitleText, Color valueColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 235, 244), 1),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(33, 47, 83));

        JLabel value = new JLabel(valueText);
        value.setFont(new Font("Segoe UI", Font.BOLD, 52));
        value.setForeground(valueColor);
        value.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        JLabel subtitle = new JLabel(subtitleText);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(107, 124, 157));

        card.add(title, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        card.add(subtitle, BorderLayout.SOUTH);
        return card;
    }

    private JPanel previewAppointmentCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 235, 244), 1),
                BorderFactory.createEmptyBorder(18, 20, 20, 20)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Today's Appointments");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 42, 70));

        JLabel viewAll = new JLabel("View All >");
        viewAll.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewAll.setForeground(new Color(63, 101, 228));

        header.add(title, BorderLayout.WEST);
        header.add(viewAll, BorderLayout.EAST);

        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setBackground(new Color(248, 251, 255));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 233, 245), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JLabel time = new JLabel("08:00");
        time.setFont(new Font("Segoe UI", Font.BOLD, 48));
        time.setForeground(new Color(63, 101, 228));

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        JLabel patient = new JLabel("John Doe");
        patient.setFont(new Font("Segoe UI", Font.BOLD, 20));
        patient.setForeground(new Color(36, 53, 83));

        JLabel reason = new JLabel("Regular checkup");
        reason.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reason.setForeground(new Color(107, 124, 156));

        details.add(patient);
        details.add(Box.createVerticalStrut(2));
        details.add(reason);

        JPanel statusWrap = new JPanel(new BorderLayout());
        statusWrap.setOpaque(false);

        JLabel status = new JLabel("pending");
        status.setFont(new Font("Segoe UI", Font.BOLD, 14));
        status.setForeground(new Color(228, 154, 47));
        status.setOpaque(true);
        status.setBackground(new Color(255, 245, 231));
        status.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(243, 217, 168), 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));

        statusWrap.add(status, BorderLayout.NORTH);
        row.add(time, BorderLayout.WEST);
        row.add(details, BorderLayout.CENTER);
        row.add(statusWrap, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);
        card.add(row, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildSidebar() {
        GradientPanel sidebar = new GradientPanel(new Color(17, 34, 76), new Color(10, 23, 51));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(310, 900));
        sidebar.setBorder(BorderFactory.createEmptyBorder(28, 22, 22, 22));

        JLabel title = new JLabel("Quick Clinic");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Admin Panel");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(178, 190, 215));
        if (receptionistMode) {
            subtitle.setText("Reception Panel");
        }

        sidebar.add(title);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(separator());
        sidebar.add(Box.createVerticalStrut(16));

        dashboardNav = new NavItem("▦  Dashboard", VIEW_DASHBOARD);
        appointmentsNav = new NavItem("◫  Appointments", VIEW_APPOINTMENTS);
        patientsNav = new NavItem("◌  Patients", VIEW_PATIENTS);
        logNav = new NavItem("⊟  Log", VIEW_LOG);
        if (!receptionistMode) {
            receptionistsNav = new NavItem("◍  Receptionists", VIEW_RECEPTIONISTS);
        } else {
            receptionistsNav = null;
        }

        attachNavBehavior(dashboardNav, VIEW_DASHBOARD);
        attachNavBehavior(appointmentsNav, VIEW_APPOINTMENTS);
        attachNavBehavior(patientsNav, VIEW_PATIENTS);
        attachNavBehavior(logNav, VIEW_LOG);
        if (!receptionistMode) {
            attachNavBehavior(receptionistsNav, VIEW_RECEPTIONISTS);
        }

        sidebar.add(dashboardNav);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(appointmentsNav);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(patientsNav);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(logNav);
        sidebar.add(Box.createVerticalStrut(10));
        if (!receptionistMode) {
            sidebar.add(receptionistsNav);
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(separator());
        sidebar.add(Box.createVerticalStrut(16));

        JPanel identity = new JPanel();
        identity.setOpaque(false);
        identity.setLayout(new BoxLayout(identity, BoxLayout.X_AXIS));
        identity.setAlignmentX(Component.LEFT_ALIGNMENT);

        AvatarCircle avatar = new AvatarCircle("A");
        avatar.setPreferredSize(new Dimension(56, 56));
        avatar.setMaximumSize(new Dimension(56, 56));
        JPanel textWrap = new JPanel();
        textWrap.setOpaque(false);
        textWrap.setLayout(new BoxLayout(textWrap, BoxLayout.Y_AXIS));
        JLabel logged = new JLabel("Logged in as");
        logged.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        logged.setForeground(new Color(178, 190, 215));
        JLabel name = new JLabel(adminName);
        name.setFont(new Font("Segoe UI", Font.BOLD, 12));
        name.setForeground(Color.WHITE);
        textWrap.add(logged);
        textWrap.add(Box.createVerticalStrut(2));
        textWrap.add(name);

        identity.add(avatar);
        identity.add(Box.createHorizontalStrut(12));
        identity.add(textWrap);

        sidebar.add(identity);
        sidebar.add(Box.createVerticalStrut(12));

        JButton logout = new JButton("Logout");
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.setFocusPainted(false);
        logout.setForeground(Color.WHITE);
        logout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logout.setBackground(new Color(47, 64, 104));
        logout.setBorder(BorderFactory.createEmptyBorder(11, 22, 11, 22));
        logout.setMaximumSize(new Dimension(170, 44));
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        sidebar.add(logout);

        return sidebar;
    }

    private void attachNavBehavior(NavItem navItem, String targetView) {
        navItem.setAlignmentX(Component.LEFT_ALIGNMENT);
        navItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        navItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setActiveView(targetView);
            }
        });
    }

    private JPanel buildDashboardView() {
        JPanel header = viewHeader(
            "Dashboard",
            "Welcome back! Here's your overview for today.",
            "+  New Appointment",
            this::openAddAppointmentDialog
        );

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));

        JPanel stats = new JPanel(new GridLayout(receptionistMode ? 2 : 3, 2, 18, 18));
        stats.setOpaque(false);
        stats.add(statCard("Today's Appointments", valLabel(out -> dashboardTodayValue = out), "No appointments today", new Color(63, 101, 228), new Color(236, 240, 250)));
        stats.add(statCard("Pending", valLabel(out -> dashboardPendingValue = out), "Appointments pending", new Color(235, 153, 45), new Color(248, 240, 229)));
        stats.add(statCard("Completed", valLabel(out -> dashboardCompletedValue = out), "Appointments completed", new Color(46, 174, 102), new Color(232, 246, 238)));
        stats.add(statCard("Total Patients", valLabel(out -> dashboardPatientsValue = out), "Registered patients", new Color(54, 163, 189), new Color(228, 244, 247)));
        if (!receptionistMode) {
            stats.add(statCard("Receptionists", valLabel(out -> dashboardReceptionistsValue = out), "Active receptionists", new Color(120, 80, 218), new Color(241, 234, 251)));
            JPanel blank = new JPanel();
            blank.setOpaque(false);
            stats.add(blank);
        }

        RoundedPanel appointments = sectionCard();
        appointments.setLayout(new BorderLayout());
        appointments.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        appointments.setPreferredSize(new Dimension(1000, 340));
        appointments.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));

        JPanel appHeader = new JPanel(new BorderLayout());
        appHeader.setOpaque(false);
        JLabel appTitle = new JLabel("Today's Appointments");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        appTitle.setForeground(new Color(26, 36, 58));
        JLabel viewAll = new JLabel("View All  >");
        viewAll.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewAll.setForeground(new Color(60, 101, 228));
        viewAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setActiveView(VIEW_APPOINTMENTS);
            }
        });
        appHeader.add(appTitle, BorderLayout.WEST);
        appHeader.add(viewAll, BorderLayout.EAST);

        appointments.add(appHeader, BorderLayout.NORTH);

        dashboardTodayRows = new JPanel();
        dashboardTodayRows.setOpaque(false);
        dashboardTodayRows.setLayout(new BoxLayout(dashboardTodayRows, BoxLayout.Y_AXIS));
        dashboardTodayRows.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        appointments.add(dashboardTodayRows, BorderLayout.CENTER);

        body.add(stats);
        body.add(Box.createVerticalStrut(20));
        body.add(appointments);
        refreshDashboardTodayAppointments();
        return wrapMainView(header, body);
    }

    private JPanel buildAppointmentsView() {
        JPanel header = viewHeader(
            "Appointments",
            "View daily appointment schedule",
            "+  New Appointment",
            this::openAddAppointmentDialog
        );

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));

        JPanel summary = new JPanel(new GridLayout(1, 3, 18, 0));
        summary.setOpaque(false);
        summary.setMaximumSize(new Dimension(Integer.MAX_VALUE, 154));
        summary.setPreferredSize(new Dimension(1000, 154));
        summary.add(summaryCard("◫", "Total", valLabel(out -> apptTotalValue = out), new Color(63, 101, 228)));
        summary.add(summaryCard("◷", "Pending", valLabel(out -> apptPendingValue = out), new Color(235, 153, 45)));
        summary.add(summaryCard("✓", "Completed", valLabel(out -> apptCompletedValue = out), new Color(73, 190, 107)));

        RoundedPanel toggleCard = sectionCard();
        toggleCard.setLayout(new BoxLayout(toggleCard, BoxLayout.X_AXIS));
        toggleCard.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        toggleCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        tableToggle = new TogglePill("☷  Table View");
        calendarToggle = new TogglePill("◫  Calendar View");
        toggleCard.add(tableToggle);
        toggleCard.add(Box.createHorizontalStrut(10));
        toggleCard.add(calendarToggle);
        toggleCard.add(Box.createHorizontalGlue());

        RoundedPanel searchCard = sectionCard();
        searchCard.setLayout(new BorderLayout());
        searchCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        searchCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 132));
        RoundedPanel searchBox = searchFieldBox("Search by patient name or reason...");
        appointmentSearchField = (JTextField) searchBox.getClientProperty("searchField");
        bindLiveSearch(appointmentSearchField, () -> {
            appointmentSearchQuery = appointmentSearchField.getText().trim().toLowerCase(Locale.ENGLISH);
            refreshAppointmentRows();
        });
        searchCard.add(searchBox, BorderLayout.CENTER);

        appointmentModeLayout = new CardLayout();
        appointmentModePanel = new JPanel(appointmentModeLayout);
        appointmentModePanel.setOpaque(false);
        appointmentModePanel.add(buildTableAppointmentsPanel(), "table");
        appointmentModePanel.add(buildCalendarAppointmentsPanel(), "calendar");

        tableToggle.setActive(true);
        calendarToggle.setActive(false);
        appointmentModeLayout.show(appointmentModePanel, "table");

        tableToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tableToggle.setActive(true);
                calendarToggle.setActive(false);
                appointmentModeLayout.show(appointmentModePanel, "table");
            }
        });
        calendarToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tableToggle.setActive(false);
                calendarToggle.setActive(true);
                appointmentModeLayout.show(appointmentModePanel, "calendar");
            }
        });

        body.add(summary);
        body.add(Box.createVerticalStrut(18));
        body.add(toggleCard);
        body.add(Box.createVerticalStrut(18));
        body.add(searchCard);
        body.add(Box.createVerticalStrut(18));
        body.add(appointmentModePanel);
        return wrapMainView(header, body);
    }

    private RoundedPanel buildTableAppointmentsPanel() {
        RoundedPanel tableCard = sectionCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        applyFixedTableCardSize(tableCard, true);

        appointmentTableRows = new JPanel();
        appointmentTableRows.setOpaque(false);
        appointmentTableRows.setLayout(new BoxLayout(appointmentTableRows, BoxLayout.Y_AXIS));

        JPanel headerRow = dashboardStyleTableHeader(
            "Time",
            "Patient / Reason / Date",
            "Status / Actions",
            112,
            240
        );

        tableCard.add(headerRow, BorderLayout.NORTH);
        tableCard.add(tableRowsScrollPane(appointmentTableRows), BorderLayout.CENTER);
        return tableCard;
    }

    private JPanel buildCalendarAppointmentsPanel() {
        JPanel calendarPanel = new JPanel();
        calendarPanel.setOpaque(false);
        calendarPanel.setLayout(new BoxLayout(calendarPanel, BoxLayout.Y_AXIS));

        RoundedPanel dateCard = sectionCard();
        dateCard.setLayout(new BoxLayout(dateCard, BoxLayout.Y_AXIS));
        dateCard.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        dateCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));

        JPanel topRow = new JPanel();
        topRow.setOpaque(false);
        topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));
        JButton left = flatNavButton("‹");
        left.addActionListener(e -> {
            appointmentCalendarDate = appointmentCalendarDate.minusDays(1);
            refreshAppointmentCalendarHeader();
            refreshAppointmentRows();
        });
        RoundedPanel dateBox = new RoundedPanel(12, new Color(249, 251, 255));
        dateBox.setLayout(new BorderLayout());
        dateBox.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        dateBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        appointmentCalendarDateField = new JTextField();
        appointmentCalendarDateField.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appointmentCalendarDateField.setForeground(new Color(35, 46, 68));
        appointmentCalendarDateField.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        appointmentCalendarDateField.addActionListener(e -> {
            if (applyCalendarDateFromInput()) {
                refreshAppointmentRows();
            }
        });
        appointmentCalendarDateField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                applyCalendarDateFromInput();
            }
        });
        dateBox.add(appointmentCalendarDateField, BorderLayout.CENTER);
        RoundedPanel today = new RoundedPanel(14, new Color(86, 176, 234));
        today.setLayout(new BorderLayout());
        today.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JLabel todayLabel = new JLabel("Today");
        todayLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        todayLabel.setForeground(new Color(18, 33, 58));
        today.setCursor(new Cursor(Cursor.HAND_CURSOR));
        today.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                appointmentCalendarDate = LocalDate.now();
                refreshAppointmentCalendarHeader();
                refreshAppointmentRows();
            }
        });
        today.add(todayLabel, BorderLayout.CENTER);
        JButton right = flatNavButton("›");
        right.addActionListener(e -> {
            appointmentCalendarDate = appointmentCalendarDate.plusDays(1);
            refreshAppointmentCalendarHeader();
            refreshAppointmentRows();
        });
        topRow.add(left);
        topRow.add(Box.createHorizontalStrut(14));
        topRow.add(dateBox);
        topRow.add(Box.createHorizontalStrut(12));
        topRow.add(today);
        topRow.add(Box.createHorizontalStrut(16));
        topRow.add(right);
        dateCard.add(topRow);
        dateCard.add(Box.createVerticalStrut(16));
        appointmentCalendarTitleLabel = new JLabel();
        appointmentCalendarTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        appointmentCalendarTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        appointmentCalendarTitleLabel.setForeground(new Color(31, 41, 63));
        dateCard.add(appointmentCalendarTitleLabel);
        refreshAppointmentCalendarHeader();

        RoundedPanel listCard = sectionCard();
        listCard.setLayout(new BorderLayout());
        listCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        listCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        appointmentCalendarRows = new JPanel();
        appointmentCalendarRows.setOpaque(false);
        appointmentCalendarRows.setLayout(new BoxLayout(appointmentCalendarRows, BoxLayout.Y_AXIS));
        listCard.add(appointmentCalendarRows, BorderLayout.CENTER);

        calendarPanel.add(dateCard);
        calendarPanel.add(Box.createVerticalStrut(18));
        calendarPanel.add(listCard);
        return calendarPanel;
    }

    private JPanel buildPatientsView() {
        JPanel header = viewHeader("Patients", "Manage patient records", "+  Add Patient", this::openAddPatientDialog);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));

        RoundedPanel summaryCard = sectionCard();
        summaryCard.setLayout(new BorderLayout());
        summaryCard.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        summaryCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        JPanel leftBlock = new JPanel();
        leftBlock.setOpaque(false);
        leftBlock.setLayout(new BoxLayout(leftBlock, BoxLayout.Y_AXIS));
        JLabel totalTitle = new JLabel("Total Patients");
        totalTitle.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        totalTitle.setForeground(new Color(78, 90, 114));
        patientTotalValue = new JLabel("0");
        patientTotalValue.setFont(new Font("Segoe UI", Font.BOLD, 46));
        patientTotalValue.setForeground(new Color(18, 30, 52));
        leftBlock.add(totalTitle);
        leftBlock.add(Box.createVerticalStrut(8));
        leftBlock.add(patientTotalValue);

        RoundedPanel iconBox = new RoundedPanel(14, new Color(230, 246, 244));
        iconBox.setLayout(new GridBagLayout());
        iconBox.setPreferredSize(new Dimension(72, 72));
        JLabel icon = new JLabel("◌");
        icon.setForeground(new Color(72, 201, 188));
        icon.setFont(new Font("Segoe UI", Font.BOLD, 26));
        iconBox.add(icon, new GridBagConstraints());
        summaryCard.add(leftBlock, BorderLayout.WEST);
        summaryCard.add(iconBox, BorderLayout.EAST);

        RoundedPanel searchCard = sectionCard();
        searchCard.setLayout(new BorderLayout());
        searchCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        searchCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 132));
        RoundedPanel searchBox = searchFieldBox("Search by name or contact number...");
        patientSearchField = (JTextField) searchBox.getClientProperty("searchField");
        bindLiveSearch(patientSearchField, () -> {
            patientSearchQuery = patientSearchField.getText().trim().toLowerCase(Locale.ENGLISH);
            refreshPatientRows();
        });
        searchCard.add(searchBox, BorderLayout.CENTER);

        RoundedPanel tableCard = sectionCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        applyFixedTableCardSize(tableCard, true);

        patientRows = new JPanel();
        patientRows.setOpaque(false);
        patientRows.setLayout(new BoxLayout(patientRows, BoxLayout.Y_AXIS));

        JPanel headerRow = dashboardStyleTableHeader(
            "Patient ID",
            "Patient / Contact",
            "Actions",
            122,
            170
        );

        tableCard.add(headerRow, BorderLayout.NORTH);
        tableCard.add(tableRowsScrollPane(patientRows), BorderLayout.CENTER);

        body.add(summaryCard);
        body.add(Box.createVerticalStrut(18));
        body.add(searchCard);
        body.add(Box.createVerticalStrut(18));
        body.add(tableCard);
        return wrapMainView(header, body);
    }

    private JPanel buildLogView() {
        JPanel header = viewHeader("Log", "View completed and cancelled appointments", null, null);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));

        JPanel summary = new JPanel(new GridLayout(1, 3, 18, 0));
        summary.setOpaque(false);
        summary.setMaximumSize(new Dimension(Integer.MAX_VALUE, 154));
        summary.add(summaryCard("◫", "Total Logged", valLabel(out -> logTotalValue = out), new Color(20, 34, 58)));
        summary.add(summaryCard("✓", "Completed", valLabel(out -> logCompletedValue = out), new Color(73, 190, 107)));
        summary.add(summaryCard("✕", "Cancelled", valLabel(out -> logCancelledValue = out), new Color(224, 93, 93)));

        RoundedPanel controlsCard = sectionCard();
        controlsCard.setLayout(new BoxLayout(controlsCard, BoxLayout.X_AXIS));
        controlsCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        controlsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 132));

        RoundedPanel searchBox = searchFieldBox("Search by patient name or reason...");
        logSearchField = (JTextField) searchBox.getClientProperty("searchField");
        bindLiveSearch(logSearchField, () -> {
            logSearchQuery = logSearchField.getText().trim().toLowerCase(Locale.ENGLISH);
            refreshLogRows();
        });
        searchBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        logAllToggle = new TogglePill("All");
        logCompletedToggle = new TogglePill("Completed");
        logCancelledToggle = new TogglePill("Cancelled");
        logAllToggle.setMaximumSize(new Dimension(120, 64));
        logCompletedToggle.setMaximumSize(new Dimension(180, 64));
        logCancelledToggle.setMaximumSize(new Dimension(160, 64));

        logAllToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setLogFilter("all");
            }
        });
        logCompletedToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setLogFilter("completed");
            }
        });
        logCancelledToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setLogFilter("cancelled");
            }
        });

        controlsCard.add(searchBox);
        controlsCard.add(Box.createHorizontalStrut(12));
        controlsCard.add(logAllToggle);
        controlsCard.add(Box.createHorizontalStrut(10));
        controlsCard.add(logCompletedToggle);
        controlsCard.add(Box.createHorizontalStrut(10));
        controlsCard.add(logCancelledToggle);

        RoundedPanel tableCard = sectionCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        applyFixedTableCardSize(tableCard, true);

        logRows = new JPanel();
        logRows.setOpaque(false);
        logRows.setLayout(new BoxLayout(logRows, BoxLayout.Y_AXIS));

        JPanel headerRow = dashboardStyleTableHeader(
            "Time / Date",
            "Patient / Reason",
            "Status / Action",
            118,
            250
        );

        tableCard.add(headerRow, BorderLayout.NORTH);
        tableCard.add(tableRowsScrollPane(logRows), BorderLayout.CENTER);

        body.add(summary);
        body.add(Box.createVerticalStrut(18));
        body.add(controlsCard);
        body.add(Box.createVerticalStrut(18));
        body.add(tableCard);
        return wrapMainView(header, body);
    }

    private JPanel buildReceptionistsView() {
        JPanel header = receptionistsHeader();

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));

        JPanel summary = new JPanel(new GridLayout(1, 3, 18, 0));
        summary.setOpaque(false);
        summary.setMaximumSize(new Dimension(Integer.MAX_VALUE, 154));
        summary.add(summaryCard("◫", "Total Accounts", valLabel(out -> receptionistTotalValue = out), new Color(20, 34, 58)));
        summary.add(summaryCard("✓", "Active", valLabel(out -> receptionistActiveValue = out), new Color(73, 190, 107)));
        summary.add(summaryCard("✕", "Deactivated", valLabel(out -> receptionistDeactivatedValue = out), new Color(89, 103, 129)));

        RoundedPanel controlsCard = sectionCard();
        controlsCard.setLayout(new BoxLayout(controlsCard, BoxLayout.X_AXIS));
        controlsCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        controlsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 132));

        RoundedPanel searchBox = searchFieldBox("Search by username or name...");
        receptionistSearchField = (JTextField) searchBox.getClientProperty("searchField");
        bindLiveSearch(receptionistSearchField, () -> {
            receptionistSearchQuery = receptionistSearchField.getText().trim().toLowerCase(Locale.ENGLISH);
            refreshReceptionistRows();
        });
        searchBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        receptionistActiveToggle = new TogglePill("◌  Active");
        receptionistDeactivatedToggle = new TogglePill("◌  Deactivated");
        receptionistActiveToggle.setMaximumSize(new Dimension(160, 64));
        receptionistDeactivatedToggle.setMaximumSize(new Dimension(220, 64));

        receptionistActiveToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setReceptionistFilter("active");
            }
        });
        receptionistDeactivatedToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setReceptionistFilter("deactivated");
            }
        });

        controlsCard.add(searchBox);
        controlsCard.add(Box.createHorizontalStrut(12));
        controlsCard.add(receptionistActiveToggle);
        controlsCard.add(Box.createHorizontalStrut(10));
        controlsCard.add(receptionistDeactivatedToggle);

        RoundedPanel tableCard = sectionCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        applyFixedTableCardSize(tableCard, true);

        receptionistRows = new JPanel();
        receptionistRows.setOpaque(false);
        receptionistRows.setLayout(new BoxLayout(receptionistRows, BoxLayout.Y_AXIS));

        JPanel headerRow = dashboardStyleTableHeader(
            "Account ID",
            "Receptionist / Username",
            "Status / Actions",
            138,
            320
        );

        tableCard.add(headerRow, BorderLayout.NORTH);
        tableCard.add(tableRowsScrollPane(receptionistRows), BorderLayout.CENTER);

        body.add(summary);
        body.add(Box.createVerticalStrut(18));
        body.add(controlsCard);
        body.add(Box.createVerticalStrut(18));
        body.add(tableCard);
        return wrapMainView(header, body);
    }

    private JPanel receptionistsHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Receptionist Accounts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 33));
        title.setForeground(new Color(20, 30, 52));
        JLabel subtitle = new JLabel("Manage active receptionist user accounts");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(84, 96, 119));
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitle);
        header.add(titleBlock, BorderLayout.WEST);

        GradientButton addReceptionistButton = new GradientButton("+  Add Receptionist");
        addReceptionistButton.setPreferredSize(new Dimension(250, 62));
        addReceptionistButton.setMinimumSize(new Dimension(250, 62));
        addReceptionistButton.setMaximumSize(new Dimension(250, 62));
        addReceptionistButton.addActionListener(e -> openAddReceptionistDialog());

        receptionistAddButtonWrap = new JPanel(new BorderLayout());
        receptionistAddButtonWrap.setOpaque(false);
        receptionistAddButtonWrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        receptionistAddButtonWrap.add(addReceptionistButton, BorderLayout.EAST);
        header.add(receptionistAddButtonWrap, BorderLayout.EAST);

        return header;
    }

    private JPanel wrapMainView(JPanel header, JPanel body) {
        AdminViewPanel main = new AdminViewPanel();
        main.getHeaderHostPanel().add(header, BorderLayout.CENTER);
        main.getBodyHostPanel().add(body, BorderLayout.CENTER);
        return main;
    }

    private JPanel viewHeader(String titleText, String subtitleText, String buttonText, Runnable onClick) {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 33));
        title.setForeground(new Color(20, 30, 52));
        JLabel subtitle = new JLabel(subtitleText);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(84, 96, 119));
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitle);

        header.add(titleBlock, BorderLayout.WEST);

        if (buttonText != null) {
            GradientButton headerButton = new GradientButton(buttonText);
            headerButton.setPreferredSize(new Dimension(250, 62));
            headerButton.setMinimumSize(new Dimension(250, 62));
            headerButton.setMaximumSize(new Dimension(250, 62));
            if (onClick != null) {
                headerButton.addActionListener(e -> onClick.run());
            }

            JPanel buttonWrap = new JPanel(new BorderLayout());
            buttonWrap.setOpaque(false);
            buttonWrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
            buttonWrap.add(headerButton, BorderLayout.EAST);
            header.add(buttonWrap, BorderLayout.EAST);
        }

        return header;
    }

    private RoundedPanel searchFieldBox(String placeholder) {
        RoundedPanel searchBox = new RoundedPanel(14, new Color(249, 251, 255));
        searchBox.setLayout(new BorderLayout(10, 0));
        searchBox.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JLabel icon = new JLabel("◌");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        icon.setForeground(new Color(95, 109, 136));

        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        field.setForeground(new Color(35, 46, 68));
        field.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        field.setOpaque(false);
        field.setToolTipText(placeholder);

        searchBox.add(icon, BorderLayout.WEST);
        searchBox.add(field, BorderLayout.CENTER);
        searchBox.putClientProperty("searchField", field);
        return searchBox;
    }

    private void bindLiveSearch(JTextField field, Runnable onChange) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onChange.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChange.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onChange.run();
            }
        });
    }

    private JButton flatNavButton(String text) {
        JButton button = new JButton(text);
        button.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.BOLD, 22));
        button.setForeground(new Color(40, 48, 66));
        return button;
    }

    private void styleInputField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 220, 236)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    private void refreshAppointmentCalendarHeader() {
        if (appointmentCalendarDateField != null) {
            appointmentCalendarDateField.setText(appointmentCalendarDate.format(CALENDAR_INPUT_FORMAT));
        }
        if (appointmentCalendarTitleLabel != null) {
            String title = appointmentCalendarDate.getDayOfWeek().name().substring(0, 1)
                + appointmentCalendarDate.getDayOfWeek().name().substring(1).toLowerCase(Locale.ENGLISH)
                + ", " + appointmentCalendarDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH));
            appointmentCalendarTitleLabel.setText(title);
        }
    }

    private boolean applyCalendarDateFromInput() {
        if (appointmentCalendarDateField == null) {
            return false;
        }
        try {
            LocalDate parsed = LocalDate.parse(appointmentCalendarDateField.getText().trim(), CALENDAR_INPUT_FORMAT);
            appointmentCalendarDate = parsed;
            refreshAppointmentCalendarHeader();
            return true;
        } catch (Exception ex) {
            refreshAppointmentCalendarHeader();
            return false;
        }
    }

    private void ensureDatabaseSchema() {
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                return;
            }
            ensureClinicTables(con);
            if (!databaseCleanupDone) {
                normalizeDuplicateTables(con);
                databaseCleanupDone = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ensureClinicTables(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS patients (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "patient_code VARCHAR(20) UNIQUE, " +
                "full_name VARCHAR(120) NOT NULL, " +
                "contact_number VARCHAR(40) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
            );
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS appointments (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "patient_name VARCHAR(120) NOT NULL, " +
                "appointment_date DATE NOT NULL, " +
                "appointment_time TIME NOT NULL, " +
                "reason VARCHAR(255) NOT NULL, " +
                "status VARCHAR(20) NOT NULL DEFAULT 'pending', " +
                "cancel_reason VARCHAR(255) DEFAULT '-', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
            );
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS account (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(80) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "full_name VARCHAR(120) NULL, " +
                "role VARCHAR(40) NULL, " +
                "status VARCHAR(20) NOT NULL DEFAULT 'active', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
            );
        }
    }

    private void normalizeDuplicateTables(Connection con) throws SQLException {
        Set<String> tables = getExistingTables(con);
        mergePatientLikeTable(con, tables, "patient");
        mergeAppointmentLikeTable(con, tables, "appointment");
        mergeAccountLikeTable(con, tables, "accounts", null);
        mergeAccountLikeTable(con, tables, "receptionist", "receptionist");
        mergeAccountLikeTable(con, tables, "receptionists", "receptionist");
    }

    private Set<String> getExistingTables(Connection con) throws SQLException {
        Set<String> tables = new HashSet<>();
        DatabaseMetaData meta = con.getMetaData();
        try (ResultSet rs = meta.getTables(con.getCatalog(), null, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME").toLowerCase(Locale.ENGLISH));
            }
        }
        return tables;
    }

    private void mergePatientLikeTable(Connection con, Set<String> tables, String sourceTable) throws SQLException {
        String source = sourceTable.toLowerCase(Locale.ENGLISH);
        if (!tables.contains(source) || "patients".equals(source)) {
            return;
        }

        Set<String> srcCols = getTableColumns(con, source);
        String nameCol = firstExisting(srcCols, "full_name", "name", "patient_name");
        String contactCol = firstExisting(srcCols, "contact_number", "contact", "contact_no", "phone");
        String codeCol = firstExisting(srcCols, "patient_code", "patient_id", "code");
        if (nameCol == null || contactCol == null) {
            return;
        }

        String sql = "SELECT `" + nameCol + "` AS full_name, `" + contactCol + "` AS contact_number" +
            (codeCol != null ? ", `" + codeCol + "` AS patient_code" : "") +
            " FROM `" + source + "`";
        int fallbackSeq = 1;
        try (Statement read = con.createStatement();
             ResultSet rs = read.executeQuery(sql);
             PreparedStatement insert = con.prepareStatement(
                 "INSERT INTO patients (patient_code, full_name, contact_number) VALUES (?, ?, ?)"
             )) {
            while (rs.next()) {
                String fullName = rs.getString("full_name");
                String contact = rs.getString("contact_number");
                if (fullName == null || fullName.isBlank() || contact == null || contact.isBlank()) {
                    continue;
                }
                String code = codeCol == null ? null : rs.getString("patient_code");
                if (code == null || code.isBlank()) {
                    code = String.format("PT-MIG-%04d", fallbackSeq++);
                }
                insert.setString(1, code);
                insert.setString(2, fullName.trim());
                insert.setString(3, contact.trim());
                try {
                    insert.executeUpdate();
                } catch (SQLException ignored) {
                    // Skip row duplicates while consolidating.
                }
            }
        }

        dropTable(con, source);
    }

    private void mergeAppointmentLikeTable(Connection con, Set<String> tables, String sourceTable) throws SQLException {
        String source = sourceTable.toLowerCase(Locale.ENGLISH);
        if (!tables.contains(source) || "appointments".equals(source)) {
            return;
        }

        Set<String> srcCols = getTableColumns(con, source);
        String patientCol = firstExisting(srcCols, "patient_name", "patient", "name");
        String dateCol = firstExisting(srcCols, "appointment_date", "date");
        String timeCol = firstExisting(srcCols, "appointment_time", "time");
        String reasonCol = firstExisting(srcCols, "reason", "purpose");
        String statusCol = firstExisting(srcCols, "status");
        String cancelCol = firstExisting(srcCols, "cancel_reason", "cancellation_reason");
        if (patientCol == null || dateCol == null || timeCol == null || reasonCol == null) {
            return;
        }

        String sql = "SELECT `" + patientCol + "` AS patient_name, `" + dateCol + "` AS appointment_date, " +
            "`" + timeCol + "` AS appointment_time, `" + reasonCol + "` AS reason" +
            (statusCol != null ? ", `" + statusCol + "` AS status" : "") +
            (cancelCol != null ? ", `" + cancelCol + "` AS cancel_reason" : "") +
            " FROM `" + source + "`";
        try (Statement read = con.createStatement();
             ResultSet rs = read.executeQuery(sql);
             PreparedStatement insert = con.prepareStatement(
                 "INSERT INTO appointments (patient_name, appointment_date, appointment_time, reason, status, cancel_reason) " +
                 "VALUES (?, ?, ?, ?, ?, ?)"
             )) {
            while (rs.next()) {
                String patient = rs.getString("patient_name");
                String reason = rs.getString("reason");
                if (patient == null || patient.isBlank() || reason == null || reason.isBlank()) {
                    continue;
                }
                LocalDate date = parseDateValue(rs.getObject("appointment_date"));
                LocalTime time = parseTimeValue(rs.getObject("appointment_time"));
                String status = statusCol == null ? "pending" : normalizeAppointmentStatus(rs.getString("status"));
                String cancelReason = cancelCol == null ? "-" : rs.getString("cancel_reason");
                if (cancelReason == null || cancelReason.isBlank()) {
                    cancelReason = "-";
                }

                insert.setString(1, patient.trim());
                insert.setDate(2, Date.valueOf(date));
                insert.setTime(3, Time.valueOf(time.withSecond(0).withNano(0)));
                insert.setString(4, reason.trim());
                insert.setString(5, status);
                insert.setString(6, cancelReason);
                try {
                    insert.executeUpdate();
                } catch (SQLException ignored) {
                    // Skip duplicate rows while consolidating.
                }
            }
        }

        dropTable(con, source);
    }

    private void mergeAccountLikeTable(Connection con, Set<String> tables, String sourceTable, String forcedRole) throws SQLException {
        String source = sourceTable.toLowerCase(Locale.ENGLISH);
        if (!tables.contains(source) || "account".equals(source)) {
            return;
        }

        Set<String> srcCols = getTableColumns(con, source);
        String userCol = firstExisting(srcCols, "username", "user_name");
        String passCol = firstExisting(srcCols, "password", "pass");
        if (userCol == null) {
            return;
        }

        String nameCol = firstExisting(srcCols, "full_name", "name");
        String statusCol = firstExisting(srcCols, "status");
        String roleCol = firstExisting(srcCols, "role");

        String sql = "SELECT `" + userCol + "` AS username" +
            (passCol != null ? ", `" + passCol + "` AS password" : "") +
            (nameCol != null ? ", `" + nameCol + "` AS display_name" : "") +
            (statusCol != null ? ", `" + statusCol + "` AS status" : "") +
            (roleCol != null ? ", `" + roleCol + "` AS role" : "") +
            " FROM `" + source + "`";

        try (Statement read = con.createStatement();
             ResultSet rs = read.executeQuery(sql)) {
            while (rs.next()) {
                String username = rs.getString("username");
                if (username == null || username.isBlank()) {
                    continue;
                }
                String password = passCol == null ? "123456" : rs.getString("password");
                if (password == null || password.isBlank()) {
                    password = "123456";
                }
                String fullName = nameCol == null ? username : rs.getString("display_name");
                String status = statusCol == null ? "active" : normalizeAccountStatus(rs.getString("status"));
                String role = forcedRole != null ? forcedRole : (roleCol == null ? null : rs.getString("role"));
                upsertAccountRow(con, username.trim(), password, fullName, status, role);
            }
        }

        dropTable(con, source);
    }

    private void upsertAccountRow(Connection con, String username, String password, String fullName, String status, String role) throws SQLException {
        AccountMeta meta = resolveAccountMeta(con);
        if (!meta.tableExists || !meta.columns.contains("username") || !meta.columns.contains("password")) {
            return;
        }
        if (accountUsernameExists(con, username)) {
            List<String> setParts = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            if (meta.columns.contains("password")) {
                setParts.add("password=?");
                values.add(password);
            }
            if (meta.nameColumn != null) {
                setParts.add(meta.nameColumn + "=?");
                values.add(fullName);
            }
            if (meta.columns.contains("status")) {
                setParts.add("status=?");
                values.add(status);
            }
            if (meta.columns.contains("role") && role != null && !role.isBlank()) {
                setParts.add("role=?");
                values.add(role);
            }
            if (setParts.isEmpty()) {
                return;
            }
            String sql = "UPDATE account SET " + String.join(", ", setParts) + " WHERE username=?";
            values.add(username);
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                for (int i = 0; i < values.size(); i++) {
                    pst.setObject(i + 1, values.get(i));
                }
                pst.executeUpdate();
            }
            return;
        }

        List<String> cols = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        cols.add("username");
        values.add(username);
        cols.add("password");
        values.add(password);
        if (meta.nameColumn != null) {
            cols.add(meta.nameColumn);
            values.add(fullName);
        }
        if (meta.columns.contains("status")) {
            cols.add("status");
            values.add(status);
        }
        if (meta.columns.contains("role") && role != null && !role.isBlank()) {
            cols.add("role");
            values.add(role);
        }
        String placeholders = String.join(", ", java.util.Collections.nCopies(cols.size(), "?"));
        String sql = "INSERT INTO account (" + String.join(", ", cols) + ") VALUES (" + placeholders + ")";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            for (int i = 0; i < values.size(); i++) {
                pst.setObject(i + 1, values.get(i));
            }
            pst.executeUpdate();
        }
    }

    private boolean accountUsernameExists(Connection con, String username) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement("SELECT 1 FROM account WHERE username=? LIMIT 1")) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void dropTable(Connection con, String tableName) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.executeUpdate("DROP TABLE IF EXISTS `" + tableName + "`");
        }
    }

    private LocalDate parseDateValue(Object raw) {
        if (raw instanceof Date) {
            return ((Date) raw).toLocalDate();
        }
        if (raw instanceof Timestamp) {
            return ((Timestamp) raw).toLocalDateTime().toLocalDate();
        }
        if (raw == null) {
            return LocalDate.now();
        }
        String text = String.valueOf(raw).trim();
        List<DateTimeFormatter> formats = Arrays.asList(
            DateTimeFormatter.ISO_LOCAL_DATE,
            APPOINTMENT_INPUT_DATE_FORMAT,
            CALENDAR_INPUT_FORMAT
        );
        for (DateTimeFormatter fmt : formats) {
            try {
                return LocalDate.parse(text, fmt);
            } catch (Exception ignored) {
                // Try next format.
            }
        }
        return LocalDate.now();
    }

    private LocalTime parseTimeValue(Object raw) {
        if (raw instanceof Time) {
            return ((Time) raw).toLocalTime().withSecond(0).withNano(0);
        }
        if (raw instanceof Timestamp) {
            return ((Timestamp) raw).toLocalDateTime().toLocalTime().withSecond(0).withNano(0);
        }
        if (raw == null) {
            return LocalTime.of(9, 0);
        }
        String text = String.valueOf(raw).trim();
        List<DateTimeFormatter> formats = Arrays.asList(
            DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
        );
        for (DateTimeFormatter fmt : formats) {
            try {
                return LocalTime.parse(text, fmt).withSecond(0).withNano(0);
            } catch (Exception ignored) {
                // Try next format.
            }
        }
        return LocalTime.of(9, 0);
    }

    private void loadAllDataFromDatabase() {
        appointmentRecords.clear();
        patientRecords.clear();
        receptionistRecords.clear();

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                return;
            }
            ensureClinicTables(con);
            if (!databaseCleanupDone) {
                normalizeDuplicateTables(con);
                databaseCleanupDone = true;
            }
            loadPatientsFromDatabase(con);
            loadAppointmentsFromDatabase(con);
            loadReceptionistsFromDatabase(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPatientsFromDatabase(Connection con) throws SQLException {
        String sql = "SELECT id, patient_code, full_name, contact_number FROM patients ORDER BY id DESC";
        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String code = rs.getString("patient_code");
                if (code == null || code.isBlank()) {
                    code = String.format("PT-%04d", id);
                }
                patientRecords.add(new PatientRecord(
                    id,
                    code,
                    rs.getString("full_name"),
                    rs.getString("contact_number")
                ));
            }
        }
    }

    private void loadAppointmentsFromDatabase(Connection con) throws SQLException {
        String sql = "SELECT id, patient_name, appointment_date, appointment_time, reason, status, " +
                     "COALESCE(cancel_reason, '-') AS cancel_reason " +
                     "FROM appointments ORDER BY appointment_date DESC, appointment_time DESC";
        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Date dateValue = rs.getDate("appointment_date");
                Time timeValue = rs.getTime("appointment_time");
                LocalDate appointmentDate = dateValue == null ? LocalDate.now() : dateValue.toLocalDate();
                LocalTime appointmentTime = timeValue == null
                    ? LocalTime.of(9, 0)
                    : timeValue.toLocalTime().withSecond(0).withNano(0);
                String status = normalizeAppointmentStatus(rs.getString("status"));
                String cancelReason = rs.getString("cancel_reason");
                if (cancelReason == null || cancelReason.isBlank()) {
                    cancelReason = "-";
                }
                appointmentRecords.add(new AppointmentRecord(
                    id,
                    rs.getString("patient_name"),
                    appointmentDate.format(DATE_LABEL_FORMAT),
                    appointmentTime.format(APPOINTMENT_INPUT_TIME_FORMAT),
                    rs.getString("reason"),
                    status,
                    cancelReason,
                    appointmentDate,
                    appointmentTime
                ));
            }
        }
    }

    private void loadReceptionistsFromDatabase(Connection con) throws SQLException {
        AccountMeta meta = resolveAccountMeta(con);
        if (!meta.tableExists || !meta.columns.contains("username")) {
            return;
        }

        List<String> selectParts = new ArrayList<>();
        if (meta.idColumn != null) {
            selectParts.add(meta.idColumn + " AS account_pk");
        }
        selectParts.add("username");
        if (meta.nameColumn != null) {
            selectParts.add(meta.nameColumn + " AS display_name");
        } else {
            selectParts.add("username AS display_name");
        }
        if (meta.columns.contains("status")) {
            selectParts.add("status");
        } else {
            selectParts.add("'active' AS status");
        }
        if (meta.createdColumn != null) {
            selectParts.add(meta.createdColumn + " AS created_value");
        }

        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(String.join(", ", selectParts));
        sql.append(" FROM account");

        List<String> where = new ArrayList<>();
        if (meta.columns.contains("role")) {
            where.add("role='receptionist'");
        } else {
            where.add("username <> 'admin'");
        }
        if (!where.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", where));
        }
        if (meta.idColumn != null) {
            sql.append(" ORDER BY ").append(meta.idColumn).append(" DESC");
        } else {
            sql.append(" ORDER BY username ASC");
        }

        try (PreparedStatement pst = con.prepareStatement(sql.toString());
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                int pk = meta.idColumn == null ? -1 : rs.getInt("account_pk");
                String username = rs.getString("username");
                String displayName = rs.getString("display_name");
                if (displayName == null || displayName.isBlank()) {
                    displayName = username;
                }
                String status = normalizeAccountStatus(rs.getString("status"));
                String accountCode = pk > 0
                    ? String.format("RCP-%04d", pk)
                    : "RCP-" + username.toUpperCase(Locale.ENGLISH);
                String createdText = "-";
                if (meta.createdColumn != null) {
                    Object raw = rs.getObject("created_value");
                    if (raw instanceof Timestamp) {
                        Timestamp ts = (Timestamp) raw;
                        createdText = ts.toLocalDateTime().toLocalDate().format(DATE_LABEL_FORMAT);
                    } else if (raw instanceof Date) {
                        Date dt = (Date) raw;
                        createdText = dt.toLocalDate().format(DATE_LABEL_FORMAT);
                    } else if (raw != null) {
                        createdText = String.valueOf(raw);
                    }
                }
                receptionistRecords.add(new ReceptionistRecord(pk, accountCode, displayName, username, status, createdText));
            }
        }
    }

    private boolean insertAppointmentToDatabase(String patientName, LocalDate appointmentDate, LocalTime appointmentTime, String reason) {
        String sql = "INSERT INTO appointments (patient_name, appointment_date, appointment_time, reason, status, cancel_reason) " +
                     "VALUES (?, ?, ?, ?, 'pending', '-')";
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                return false;
            }
            ensureClinicTables(con);
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, patientName);
                pst.setDate(2, Date.valueOf(appointmentDate));
                pst.setTime(3, Time.valueOf(appointmentTime.withSecond(0).withNano(0)));
                pst.setString(4, reason);
                return pst.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateAppointmentStatusInDatabase(int appointmentId, String newStatus, String cancelReason) {
        String sql = "UPDATE appointments SET status=?, cancel_reason=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con == null ? null : con.prepareStatement(sql)) {
            if (con == null || pst == null) {
                return false;
            }
            pst.setString(1, newStatus);
            pst.setString(2, cancelReason == null || cancelReason.isBlank() ? "-" : cancelReason);
            pst.setInt(3, appointmentId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean insertPatientToDatabase(String fullName, String contactNumber) {
        String sql = "INSERT INTO patients (patient_code, full_name, contact_number) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                return false;
            }
            ensureClinicTables(con);
            String code = nextPatientCodeFromDatabase(con);
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, code);
                pst.setString(2, fullName);
                pst.setString(3, contactNumber);
                return pst.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updatePatientInDatabase(PatientRecord record, String fullName, String contactNumber) {
        String sql = "UPDATE patients SET full_name=?, contact_number=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con == null ? null : con.prepareStatement(sql)) {
            if (con == null || pst == null) {
                return false;
            }
            pst.setString(1, fullName);
            pst.setString(2, contactNumber);
            pst.setInt(3, record.id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean insertReceptionistToDatabase(String fullName, String username, String password) {
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                return false;
            }
            AccountMeta meta = resolveAccountMeta(con);
            if (!meta.tableExists || !meta.columns.contains("username") || !meta.columns.contains("password")) {
                return false;
            }

            List<String> columns = new ArrayList<>(Arrays.asList("username", "password"));
            List<Object> values = new ArrayList<>(Arrays.asList(username, password));
            if (meta.columns.contains("status")) {
                columns.add("status");
                values.add("active");
            }
            if (meta.nameColumn != null) {
                columns.add(meta.nameColumn);
                values.add(fullName);
            }
            if (meta.columns.contains("role")) {
                columns.add("role");
                values.add("receptionist");
            }

            String placeholders = String.join(", ", java.util.Collections.nCopies(columns.size(), "?"));
            String sql = "INSERT INTO account (" + String.join(", ", columns) + ") VALUES (" + placeholders + ")";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                for (int i = 0; i < values.size(); i++) {
                    pst.setObject(i + 1, values.get(i));
                }
                return pst.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateReceptionistInDatabase(ReceptionistRecord record, String fullName, String username) {
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                return false;
            }
            AccountMeta meta = resolveAccountMeta(con);
            if (!meta.tableExists || !meta.columns.contains("username")) {
                return false;
            }

            List<String> setParts = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            setParts.add("username=?");
            values.add(username);
            if (meta.nameColumn != null) {
                setParts.add(meta.nameColumn + "=?");
                values.add(fullName);
            }

            String where = record.accountPk > 0 && meta.idColumn != null
                ? meta.idColumn + "=?"
                : "username=?";
            values.add(record.username);
            if (record.accountPk > 0 && meta.idColumn != null) {
                values.set(values.size() - 1, record.accountPk);
            }

            String sql = "UPDATE account SET " + String.join(", ", setParts) + " WHERE " + where;
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                for (int i = 0; i < values.size(); i++) {
                    pst.setObject(i + 1, values.get(i));
                }
                return pst.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateReceptionistStatusInDatabase(ReceptionistRecord record, String status) {
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                return false;
            }
            AccountMeta meta = resolveAccountMeta(con);
            if (!meta.tableExists || !meta.columns.contains("status")) {
                return false;
            }

            String where = record.accountPk > 0 && meta.idColumn != null
                ? meta.idColumn + "=?"
                : "username=?";
            String sql = "UPDATE account SET status=? WHERE " + where;
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, status);
                if (record.accountPk > 0 && meta.idColumn != null) {
                    pst.setInt(2, record.accountPk);
                } else {
                    pst.setString(2, record.username);
                }
                return pst.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String nextPatientCodeFromDatabase(Connection con) throws SQLException {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 AS next_id FROM patients";
        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return String.format("PT-%04d", rs.getInt("next_id"));
            }
        }
        return "PT-0001";
    }

    private String normalizeAppointmentStatus(String raw) {
        if (raw == null) {
            return "pending";
        }
        String lower = raw.trim().toLowerCase(Locale.ENGLISH);
        if ("completed".equals(lower) || "cancelled".equals(lower) || "pending".equals(lower)) {
            return lower;
        }
        return "pending";
    }

    private String normalizeAccountStatus(String raw) {
        if (raw == null) {
            return "active";
        }
        String lower = raw.trim().toLowerCase(Locale.ENGLISH);
        if ("active".equals(lower)) {
            return "active";
        }
        if ("deactivated".equals(lower) || "inactive".equals(lower) || "disabled".equals(lower)) {
            return "deactivated";
        }
        return "active";
    }

    private AccountMeta resolveAccountMeta(Connection con) throws SQLException {
        Set<String> columns = getTableColumns(con, "account");
        if (columns.isEmpty()) {
            return new AccountMeta(false, columns, null, null, null);
        }
        String idColumn = firstExisting(columns, "id", "account_id", "user_id");
        String nameColumn = firstExisting(columns, "full_name", "name");
        String createdColumn = firstExisting(columns, "created_at", "date_created", "created_on");
        return new AccountMeta(true, columns, idColumn, nameColumn, createdColumn);
    }

    private Set<String> getTableColumns(Connection con, String tableName) throws SQLException {
        Set<String> columns = new HashSet<>();
        DatabaseMetaData meta = con.getMetaData();
        try (ResultSet rs = meta.getColumns(con.getCatalog(), null, tableName, null)) {
            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME").toLowerCase(Locale.ENGLISH));
            }
        }
        return columns;
    }

    private String firstExisting(Set<String> columns, String... candidates) {
        for (String candidate : candidates) {
            if (columns.contains(candidate.toLowerCase(Locale.ENGLISH))) {
                return candidate;
            }
        }
        return null;
    }

    private void openAddAppointmentDialog() {
        JTextField patient = new JTextField();
        JTextField date = new JTextField();
        JTextField time = new JTextField();
        JTextField reason = new JTextField();
        styleInputField(patient);
        styleInputField(date);
        styleInputField(time);
        styleInputField(reason);

        date.setText(LocalDate.now().format(APPOINTMENT_INPUT_DATE_FORMAT));
        time.setText("09:00");

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.add(new JLabel("Patient Name"));
        form.add(patient);
        form.add(new JLabel("Date (e.g. Apr 27, 2026)"));
        form.add(date);
        form.add(new JLabel("Time (e.g. 09:00)"));
        form.add(time);
        form.add(new JLabel("Reason"));
        form.add(reason);

        int result = JOptionPane.showConfirmDialog(
            this,
            form,
            "Add Appointment",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        if (patient.getText().isBlank() || date.getText().isBlank() || time.getText().isBlank() || reason.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        LocalDate appointmentDate;
        LocalTime appointmentTime;
        try {
            appointmentDate = LocalDate.parse(date.getText().trim(), APPOINTMENT_INPUT_DATE_FORMAT);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use e.g. Apr 27, 2026");
            return;
        }
        try {
            appointmentTime = LocalTime.parse(time.getText().trim(), APPOINTMENT_INPUT_TIME_FORMAT);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid time format. Use 24h format HH:mm (e.g. 09:00).");
            return;
        }

        if (!insertAppointmentToDatabase(
            patient.getText().trim(),
            appointmentDate,
            appointmentTime,
            reason.getText().trim()
        )) {
            JOptionPane.showMessageDialog(this, "Failed to save appointment to database.");
            return;
        }

        refreshAllViews();
    }

    private void openAddReceptionistDialog() {
        JTextField name = new JTextField();
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        styleInputField(name);
        styleInputField(username);
        password.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        password.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 220, 236)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.add(new JLabel("Full Name"));
        form.add(name);
        form.add(new JLabel("Username"));
        form.add(username);
        form.add(new JLabel("Password"));
        form.add(password);

        int result = JOptionPane.showConfirmDialog(
            this,
            form,
            "Add Receptionist",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String plainPassword = new String(password.getPassword());
        if (name.getText().isBlank() || username.getText().isBlank() || plainPassword.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        if (!insertReceptionistToDatabase(
            name.getText().trim(),
            username.getText().trim(),
            plainPassword
        )) {
            JOptionPane.showMessageDialog(this, "Failed to save receptionist to database.");
            return;
        }

        refreshAllViews();
    }

    private void openAddPatientDialog() {
        JTextField name = new JTextField();
        JTextField contact = new JTextField();
        styleInputField(name);
        styleInputField(contact);

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.add(new JLabel("Patient Name"));
        form.add(name);
        form.add(new JLabel("Contact Number"));
        form.add(contact);

        int result = JOptionPane.showConfirmDialog(
            this,
            form,
            "Add Patient",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        if (name.getText().isBlank() || contact.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        if (!insertPatientToDatabase(name.getText().trim(), contact.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Failed to save patient to database.");
            return;
        }

        refreshAllViews();
    }

    private void refreshAllViews() {
        loadAllDataFromDatabase();
        refreshDashboardCounts();
        refreshDashboardTodayAppointments();
        refreshAppointmentSummary();
        refreshAppointmentRows();
        refreshPatientSummary();
        refreshPatientRows();
        refreshLogSummary();
        refreshLogRows();
        refreshReceptionistSummary();
        refreshReceptionistRows();
    }

    private void refreshDashboardCounts() {
        int todayTotal = 0;
        for (AppointmentRecord record : appointmentRecords) {
            if (LocalDate.now().equals(record.appointmentDate)) {
                todayTotal++;
            }
        }
        int pending = countByStatus("pending");
        int completed = countByStatus("completed");
        int totalPatients = patientRecords.size();
        int activeReceptionists = countReceptionistsByStatus("active");

        if (dashboardTodayValue != null) {
            dashboardTodayValue.setText(String.valueOf(todayTotal));
        }
        if (dashboardPendingValue != null) {
            dashboardPendingValue.setText(String.valueOf(pending));
        }
        if (dashboardCompletedValue != null) {
            dashboardCompletedValue.setText(String.valueOf(completed));
        }
        if (dashboardPatientsValue != null) {
            dashboardPatientsValue.setText(String.valueOf(totalPatients));
        }
        if (dashboardReceptionistsValue != null) {
            dashboardReceptionistsValue.setText(String.valueOf(activeReceptionists));
        }
    }

    private void refreshDashboardTodayAppointments() {
        if (dashboardTodayRows == null) {
            return;
        }

        dashboardTodayRows.removeAll();

        List<AppointmentRecord> todayRecords = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (AppointmentRecord record : appointmentRecords) {
            if (today.equals(record.appointmentDate)) {
                todayRecords.add(record);
            }
        }
        todayRecords.sort((a, b) -> a.appointmentTime.compareTo(b.appointmentTime));

        if (todayRecords.isEmpty()) {
            dashboardTodayRows.add(emptyStatePanel("No appointments yet.", "You have no appointments scheduled for today."));
        } else {
            int maxVisible = 5;
            int shown = 0;
            for (AppointmentRecord record : todayRecords) {
                if (shown >= maxVisible) {
                    break;
                }
                if (shown > 0) {
                    dashboardTodayRows.add(Box.createVerticalStrut(8));
                }
                dashboardTodayRows.add(dashboardTodayRow(record));
                shown++;
            }
            if (todayRecords.size() > maxVisible) {
                dashboardTodayRows.add(Box.createVerticalStrut(10));
                JLabel more = new JLabel("+" + (todayRecords.size() - maxVisible) + " more appointments today");
                more.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                more.setForeground(new Color(96, 111, 138));
                more.setAlignmentX(Component.LEFT_ALIGNMENT);
                dashboardTodayRows.add(more);
            }
        }

        dashboardTodayRows.revalidate();
        dashboardTodayRows.repaint();
    }

    private void refreshAppointmentSummary() {
        if (apptTotalValue != null) {
            apptTotalValue.setText(String.valueOf(appointmentRecords.size()));
        }
        if (apptPendingValue != null) {
            apptPendingValue.setText(String.valueOf(countByStatus("pending")));
        }
        if (apptCompletedValue != null) {
            apptCompletedValue.setText(String.valueOf(countByStatus("completed")));
        }
    }

    private void refreshPatientSummary() {
        if (patientTotalValue != null) {
            patientTotalValue.setText(String.valueOf(patientRecords.size()));
        }
    }

    private void refreshPatientRows() {
        if (patientRows == null) {
            return;
        }

        patientRows.removeAll();

        List<PatientRecord> filtered = new ArrayList<>();
        for (PatientRecord record : patientRecords) {
            if (matchesPatientSearch(record)) {
                filtered.add(record);
            }
        }

        if (filtered.isEmpty()) {
            patientRows.add(emptyStatePanel("No patients yet.", "No records to show."));
            updateRowsPanelPreferredHeight(patientRows, 1);
        } else {
            int i = 0;
            for (PatientRecord record : filtered) {
                if (i > 0) {
                    patientRows.add(Box.createVerticalStrut(TABLE_ROW_GAP));
                }
                patientRows.add(patientRow(record));
                i++;
            }
            updateRowsPanelPreferredHeight(patientRows, filtered.size());
        }

        patientRows.revalidate();
        patientRows.repaint();
    }

    private void refreshAppointmentRows() {
        if (appointmentTableRows == null || appointmentCalendarRows == null) {
            return;
        }

        appointmentTableRows.removeAll();
        appointmentCalendarRows.removeAll();

        List<AppointmentRecord> pendingRecords = new ArrayList<>();
        List<AppointmentRecord> calendarRecords = new ArrayList<>();
        for (AppointmentRecord record : appointmentRecords) {
            if ("pending".equals(record.status) && matchesAppointmentSearch(record)) {
                pendingRecords.add(record);
                if (appointmentCalendarDate.equals(record.appointmentDate)) {
                    calendarRecords.add(record);
                }
            }
        }

        if (pendingRecords.isEmpty()) {
            appointmentTableRows.add(emptyStatePanel("No appointments yet.", "No pending records to show."));
            updateRowsPanelPreferredHeight(appointmentTableRows, 1);
        } else {
            int i = 0;
            for (AppointmentRecord record : pendingRecords) {
                if (i > 0) {
                    appointmentTableRows.add(Box.createVerticalStrut(TABLE_ROW_GAP));
                }
                appointmentTableRows.add(appointmentTableRow(record));
                i++;
            }
            updateRowsPanelPreferredHeight(appointmentTableRows, pendingRecords.size());
        }

        if (calendarRecords.isEmpty()) {
            appointmentCalendarRows.add(emptyStatePanel("No appointments yet.", "No pending records for this date."));
        } else {
            for (AppointmentRecord record : calendarRecords) {
                appointmentCalendarRows.add(calendarRow(record));
                appointmentCalendarRows.add(Box.createVerticalStrut(10));
            }
        }

        appointmentTableRows.revalidate();
        appointmentTableRows.repaint();
        appointmentCalendarRows.revalidate();
        appointmentCalendarRows.repaint();
    }

    private JPanel appointmentTableRow(AppointmentRecord record) {
        RoundedPanel row = tableRowCard(TABLE_ROW_HEIGHT);
        row.setLayout(new BorderLayout(12, 0));

        JLabel timeLabel = new JLabel(safeText(record.timeText, "--:--"));
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        timeLabel.setForeground(new Color(63, 101, 228));
        timeLabel.setPreferredSize(new Dimension(112, 40));
        row.add(rowCell(timeLabel), BorderLayout.WEST);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        JLabel patientLabel = new JLabel(truncateText(safeText(record.patientName, "(No patient)"), 28));
        patientLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        patientLabel.setForeground(new Color(33, 46, 71));
        patientLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        String subtitleText = truncateText(
            safeText(record.reason, "-") + "  •  " + safeText(record.dateText, "-"),
            44
        );
        JLabel subtitleLabel = new JLabel(subtitleText);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(109, 124, 151));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        details.add(patientLabel);
        details.add(Box.createVerticalStrut(4));
        details.add(subtitleLabel);
        row.add(details, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        actions.add(statusBadge(record.status));
        actions.add(Box.createHorizontalStrut(14));

        JButton completed = actionButton("✓", new Color(84, 195, 102));
        completed.setToolTipText("Mark as completed");
        completed.addActionListener(e -> {
            if (!updateAppointmentStatusInDatabase(record.id, "completed", "-")) {
                JOptionPane.showMessageDialog(this, "Failed to update appointment status.");
                return;
            }
            refreshAllViews();
        });

        JButton cancelled = actionButton("✕", new Color(231, 86, 86));
        cancelled.setToolTipText("Mark as cancelled");
        cancelled.addActionListener(e -> {
            String reason = JOptionPane.showInputDialog(this, "Cancel reason (optional):", "Cancelled by admin");
            if (reason == null) {
                return;
            }
            if (!updateAppointmentStatusInDatabase(
                record.id,
                "cancelled",
                reason.isBlank() ? "Cancelled by admin" : reason.trim()
            )) {
                JOptionPane.showMessageDialog(this, "Failed to update appointment status.");
                return;
            }
            refreshAllViews();
        });

        actions.add(completed);
        actions.add(Box.createHorizontalStrut(8));
        actions.add(cancelled);
        row.add(rowCell(actions), BorderLayout.EAST);
        return row;
    }

    private JPanel dashboardTodayRow(AppointmentRecord record) {
        RoundedPanel row = new RoundedPanel(14, new Color(247, 250, 255));
        row.setLayout(new BorderLayout(12, 0));
        row.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel time = new JLabel(record.timeText);
        time.setFont(new Font("Segoe UI", Font.BOLD, 22));
        time.setForeground(new Color(63, 101, 228));
        time.setPreferredSize(new Dimension(74, 44));
        row.add(time, BorderLayout.WEST);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        JLabel patient = new JLabel(safeText(record.patientName, "(No patient)"));
        patient.setFont(new Font("Segoe UI", Font.BOLD, 14));
        patient.setForeground(new Color(33, 46, 71));
        JLabel reason = new JLabel(safeText(record.reason, "-"));
        reason.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reason.setForeground(new Color(109, 124, 151));
        details.add(patient);
        details.add(Box.createVerticalStrut(3));
        details.add(reason);
        row.add(details, BorderLayout.CENTER);

        row.add(statusBadge(record.status), BorderLayout.EAST);
        return row;
    }

    private JPanel calendarRow(AppointmentRecord record) {
        RoundedPanel row = new RoundedPanel(14, new Color(250, 252, 255));
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel time = new JLabel(record.timeText);
        time.setFont(new Font("Segoe UI", Font.BOLD, 20));
        time.setForeground(new Color(67, 104, 228));

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        JLabel patient = new JLabel(record.patientName);
        patient.setFont(new Font("Segoe UI", Font.BOLD, 15));
        patient.setForeground(new Color(34, 45, 67));
        JLabel reason = new JLabel(record.reason);
        reason.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reason.setForeground(new Color(106, 120, 148));
        details.add(patient);
        details.add(Box.createVerticalStrut(4));
        details.add(reason);

        JButton completed = actionButton("✓", new Color(84, 195, 102));
        completed.addActionListener(e -> {
            if (!updateAppointmentStatusInDatabase(record.id, "completed", "-")) {
                JOptionPane.showMessageDialog(this, "Failed to update appointment status.");
                return;
            }
            refreshAllViews();
        });

        JButton cancelled = actionButton("✕", new Color(231, 86, 86));
        cancelled.addActionListener(e -> {
            String reasonText = JOptionPane.showInputDialog(this, "Cancel reason (optional):", "Cancelled by admin");
            if (reasonText == null) {
                return;
            }
            if (!updateAppointmentStatusInDatabase(
                record.id,
                "cancelled",
                reasonText.isBlank() ? "Cancelled by admin" : reasonText.trim()
            )) {
                JOptionPane.showMessageDialog(this, "Failed to update appointment status.");
                return;
            }
            refreshAllViews();
        });

        row.add(time);
        row.add(Box.createHorizontalStrut(18));
        row.add(details);
        row.add(Box.createHorizontalGlue());
        row.add(completed);
        row.add(Box.createHorizontalStrut(8));
        row.add(cancelled);

        return row;
    }

    private JPanel patientRow(PatientRecord record) {
        RoundedPanel row = tableRowCard(TABLE_ROW_HEIGHT);
        row.setLayout(new BorderLayout(12, 0));

        JLabel patientIdLabel = new JLabel(truncateText(safeText(record.patientId, "—"), 12));
        patientIdLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        patientIdLabel.setForeground(new Color(63, 101, 228));
        patientIdLabel.setPreferredSize(new Dimension(122, 40));
        row.add(rowCell(patientIdLabel), BorderLayout.WEST);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(truncateText(safeText(record.name, "(No patient)"), 34));
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(new Color(33, 46, 71));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel contactLabel = new JLabel(truncateText("Contact: " + safeText(record.contactNumber, "-"), 44));
        contactLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contactLabel.setForeground(new Color(109, 124, 151));
        contactLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        details.add(nameLabel);
        details.add(Box.createVerticalStrut(4));
        details.add(contactLabel);
        row.add(details, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));

        JButton edit = softActionButton("Edit", new Color(236, 241, 251), new Color(40, 53, 79));
        edit.addActionListener(e -> {
            JTextField nameField = new JTextField(record.name);
            JTextField contactField = new JTextField(record.contactNumber);
            styleInputField(nameField);
            styleInputField(contactField);
            JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
            form.add(new JLabel("Patient Name"));
            form.add(nameField);
            form.add(new JLabel("Contact Number"));
            form.add(contactField);
            int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Edit Patient",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            if (result != JOptionPane.OK_OPTION) {
                return;
            }
            if (nameField.getText().isBlank() || contactField.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }
            if (!updatePatientInDatabase(record, nameField.getText().trim(), contactField.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Failed to update patient.");
                return;
            }
            refreshAllViews();
        });

        actions.add(edit);
        row.add(rowCell(actions), BorderLayout.EAST);
        return row;
    }

    private void refreshLogSummary() {
        int completed = countByStatus("completed");
        int cancelled = countByStatus("cancelled");
        int totalLogged = completed + cancelled;

        if (logTotalValue != null) {
            logTotalValue.setText(String.valueOf(totalLogged));
        }
        if (logCompletedValue != null) {
            logCompletedValue.setText(String.valueOf(completed));
        }
        if (logCancelledValue != null) {
            logCancelledValue.setText(String.valueOf(cancelled));
        }
    }

    private void refreshLogRows() {
        if (logRows == null) {
            return;
        }

        logRows.removeAll();

        List<AppointmentRecord> logged = new ArrayList<>();
        for (AppointmentRecord record : appointmentRecords) {
            boolean inLog = "completed".equals(record.status) || "cancelled".equals(record.status);
            boolean matchesFilter = "all".equals(logFilter) || logFilter.equals(record.status);
            boolean matchesSearch = matchesLogSearch(record);
            if (inLog && matchesFilter && matchesSearch) {
                logged.add(record);
            }
        }

        if (logged.isEmpty()) {
            logRows.add(emptyStatePanel("No log records yet.", "Completed and cancelled appointments will appear here."));
            updateRowsPanelPreferredHeight(logRows, 1);
        } else {
            int i = 0;
            for (AppointmentRecord record : logged) {
                if (i > 0) {
                    logRows.add(Box.createVerticalStrut(TABLE_ROW_GAP));
                }
                logRows.add(logRow(record));
                i++;
            }
            updateRowsPanelPreferredHeight(logRows, logged.size());
        }

        logRows.revalidate();
        logRows.repaint();
    }

    private JPanel logRow(AppointmentRecord record) {
        RoundedPanel row = tableRowCard(TABLE_ROW_HEIGHT);
        row.setLayout(new BorderLayout(12, 0));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setPreferredSize(new Dimension(118, 44));

        JLabel timeLabel = new JLabel(safeText(record.timeText, "--:--"));
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        timeLabel.setForeground(new Color(63, 101, 228));
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel(truncateText(safeText(record.dateText, "-"), 16));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(new Color(109, 124, 151));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(timeLabel);
        left.add(Box.createVerticalStrut(2));
        left.add(dateLabel);
        row.add(rowCell(left), BorderLayout.WEST);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        JLabel patientLabel = new JLabel(truncateText(safeText(record.patientName, "(No patient)"), 30));
        patientLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        patientLabel.setForeground(new Color(33, 46, 71));
        patientLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String reasonText = safeText(record.reason, "-");
        if ("cancelled".equals(record.status)) {
            String cancel = safeText(record.cancelReason, "");
            if (!cancel.isBlank() && !"-".equals(cancel)) {
                reasonText = reasonText + "  •  Cancel: " + cancel;
            }
        }
        JLabel subtitleLabel = new JLabel(truncateText(reasonText, 62));
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(109, 124, 151));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        details.add(patientLabel);
        details.add(Box.createVerticalStrut(4));
        details.add(subtitleLabel);
        row.add(details, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        actions.add(statusBadge(record.status));
        if ("cancelled".equals(record.status)) {
            actions.add(Box.createHorizontalStrut(10));
            JButton restore = softActionButton("Restore", new Color(234, 240, 252), new Color(44, 58, 86));
            restore.addActionListener(e -> {
                if (!updateAppointmentStatusInDatabase(record.id, "pending", "-")) {
                    JOptionPane.showMessageDialog(this, "Failed to restore appointment.");
                    return;
                }
                refreshAllViews();
            });
            actions.add(restore);
        }
        row.add(rowCell(actions), BorderLayout.EAST);
        return row;
    }

    private void setLogFilter(String filter) {
        logFilter = filter;
        if (logAllToggle != null) {
            logAllToggle.setActive("all".equals(filter));
        }
        if (logCompletedToggle != null) {
            logCompletedToggle.setActive("completed".equals(filter));
        }
        if (logCancelledToggle != null) {
            logCancelledToggle.setActive("cancelled".equals(filter));
        }
        refreshLogRows();
    }

    private void refreshReceptionistSummary() {
        if (receptionistTotalValue != null) {
            receptionistTotalValue.setText(String.valueOf(receptionistRecords.size()));
        }
        if (receptionistActiveValue != null) {
            receptionistActiveValue.setText(String.valueOf(countReceptionistsByStatus("active")));
        }
        if (receptionistDeactivatedValue != null) {
            receptionistDeactivatedValue.setText(String.valueOf(countReceptionistsByStatus("deactivated")));
        }
    }

    private void refreshReceptionistRows() {
        if (receptionistRows == null) {
            return;
        }

        receptionistRows.removeAll();

        List<ReceptionistRecord> filtered = new ArrayList<>();
        for (ReceptionistRecord record : receptionistRecords) {
            if (receptionistFilter.equals(record.status) && matchesReceptionistSearch(record)) {
                filtered.add(record);
            }
        }

        if (filtered.isEmpty()) {
            if ("active".equals(receptionistFilter)) {
                receptionistRows.add(emptyStatePanel("No active receptionists yet.", "Use Add Receptionist to create an account."));
            } else {
                receptionistRows.add(emptyStatePanel("No deactivated receptionists yet.", "Deactivated accounts will appear here."));
            }
            updateRowsPanelPreferredHeight(receptionistRows, 1);
        } else {
            int i = 0;
            for (ReceptionistRecord record : filtered) {
                if (i > 0) {
                    receptionistRows.add(Box.createVerticalStrut(TABLE_ROW_GAP));
                }
                receptionistRows.add(receptionistRow(record));
                i++;
            }
            updateRowsPanelPreferredHeight(receptionistRows, filtered.size());
        }

        receptionistRows.revalidate();
        receptionistRows.repaint();
    }

    private JPanel receptionistRow(ReceptionistRecord record) {
        RoundedPanel row = tableRowCard(TABLE_ROW_HEIGHT);
        row.setLayout(new BorderLayout(12, 0));

        JLabel accountIdLabel = new JLabel(truncateText(safeText(record.accountId, "—"), 12));
        accountIdLabel.setFont(new Font("Monospaced", Font.BOLD, 19));
        accountIdLabel.setForeground(new Color(63, 101, 228));
        accountIdLabel.setPreferredSize(new Dimension(138, 40));
        row.add(rowCell(accountIdLabel), BorderLayout.WEST);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(truncateText(safeText(record.name, "(No name)"), 30));
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(new Color(33, 46, 71));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String usernameAndCreated = "@" + safeText(record.username, "-")
            + "  •  Created " + safeText(record.createdText, "-");
        JLabel subtitleLabel = new JLabel(truncateText(usernameAndCreated, 56));
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(109, 124, 151));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        details.add(nameLabel);
        details.add(Box.createVerticalStrut(4));
        details.add(subtitleLabel);
        row.add(details, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        actions.add(statusBadge(record.status));
        actions.add(Box.createHorizontalStrut(10));

        JButton edit = softActionButton("Edit", new Color(236, 241, 251), new Color(40, 53, 79));
        edit.addActionListener(e -> {
            JTextField nameField = new JTextField(record.name);
            JTextField usernameField = new JTextField(record.username);
            styleInputField(nameField);
            styleInputField(usernameField);
            JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
            form.add(new JLabel("Full Name"));
            form.add(nameField);
            form.add(new JLabel("Username"));
            form.add(usernameField);
            int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Edit Receptionist",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            if (result != JOptionPane.OK_OPTION) {
                return;
            }
            if (nameField.getText().isBlank() || usernameField.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }
            if (!updateReceptionistInDatabase(record, nameField.getText().trim(), usernameField.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Failed to update receptionist.");
                return;
            }
            refreshAllViews();
        });

        JButton statusAction;
        if ("active".equals(record.status)) {
            statusAction = softActionButton("Deactivate", new Color(252, 236, 236), new Color(224, 93, 93));
            statusAction.addActionListener(e -> {
                if (!updateReceptionistStatusInDatabase(record, "deactivated")) {
                    JOptionPane.showMessageDialog(this, "Failed to update receptionist status.");
                    return;
                }
                refreshAllViews();
            });
        } else {
            statusAction = softActionButton("Activate", new Color(231, 246, 236), new Color(73, 190, 107));
            statusAction.addActionListener(e -> {
                if (!updateReceptionistStatusInDatabase(record, "active")) {
                    JOptionPane.showMessageDialog(this, "Failed to update receptionist status.");
                    return;
                }
                refreshAllViews();
            });
        }

        actions.add(edit);
        actions.add(Box.createHorizontalStrut(8));
        actions.add(statusAction);
        row.add(rowCell(actions), BorderLayout.EAST);
        return row;
    }

    private void setReceptionistFilter(String filter) {
        receptionistFilter = filter;
        if (receptionistActiveToggle != null) {
            receptionistActiveToggle.setActive("active".equals(filter));
        }
        if (receptionistDeactivatedToggle != null) {
            receptionistDeactivatedToggle.setActive("deactivated".equals(filter));
        }
        if (receptionistAddButtonWrap != null) {
            receptionistAddButtonWrap.setVisible("active".equals(filter));
        }
        refreshReceptionistRows();
    }

    private int countReceptionistsByStatus(String status) {
        int count = 0;
        for (ReceptionistRecord record : receptionistRecords) {
            if (status.equals(record.status)) {
                count++;
            }
        }
        return count;
    }

    private boolean matchesAppointmentSearch(AppointmentRecord record) {
        if (appointmentSearchQuery.isBlank()) {
            return true;
        }
        String haystack = (record.patientName + " " + record.reason + " " + record.dateText + " " + record.timeText)
            .toLowerCase(Locale.ENGLISH);
        return haystack.contains(appointmentSearchQuery);
    }

    private boolean matchesPatientSearch(PatientRecord record) {
        if (patientSearchQuery.isBlank()) {
            return true;
        }
        String haystack = (record.name + " " + record.contactNumber + " " + record.patientId)
            .toLowerCase(Locale.ENGLISH);
        return haystack.contains(patientSearchQuery);
    }

    private boolean matchesLogSearch(AppointmentRecord record) {
        if (logSearchQuery.isBlank()) {
            return true;
        }
        String haystack = (record.patientName + " " + record.reason + " " + record.cancelReason + " " + record.dateText + " " + record.timeText)
            .toLowerCase(Locale.ENGLISH);
        return haystack.contains(logSearchQuery);
    }

    private boolean matchesReceptionistSearch(ReceptionistRecord record) {
        if (receptionistSearchQuery.isBlank()) {
            return true;
        }
        String haystack = (record.accountId + " " + record.name + " " + record.username)
            .toLowerCase(Locale.ENGLISH);
        return haystack.contains(receptionistSearchQuery);
    }

    private int countByStatus(String status) {
        int count = 0;
        for (AppointmentRecord record : appointmentRecords) {
            if (status.equals(record.status)) {
                count++;
            }
        }
        return count;
    }

    private String safeText(String text, String fallback) {
        if (text == null || text.isBlank()) {
            return fallback;
        }
        return text;
    }

    private JLabel tableHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(73, 87, 112));
        return label;
    }

    private JPanel dashboardStyleTableHeader(String leftText, String centerText, String rightText, int leftWidth, int rightWidth) {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(6, 6, 12, 6));

        JLabel left = tableHeader(leftText);
        left.setPreferredSize(new Dimension(leftWidth, 28));
        header.add(rowCell(left), BorderLayout.WEST);

        JLabel center = tableHeader(centerText);
        header.add(center, BorderLayout.CENTER);

        JLabel right = tableHeader(rightText);
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        right.setPreferredSize(new Dimension(rightWidth, 28));
        header.add(right, BorderLayout.EAST);

        return header;
    }

    private JLabel rowLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setForeground(new Color(40, 53, 79));
        return label;
    }

    private JLabel rowLabelLimited(String text, int maxChars) {
        String safe = safeText(text, "-");
        String shown = truncateText(safe, maxChars);
        JLabel label = rowLabel(shown);
        if (!shown.equals(safe)) {
            label.setToolTipText(safe);
        }
        return label;
    }

    private String truncateText(String text, int maxChars) {
        if (text == null) {
            return "";
        }
        String trimmed = text.trim();
        if (trimmed.length() <= maxChars) {
            return trimmed;
        }
        return trimmed.substring(0, Math.max(1, maxChars - 1)) + "…";
    }

    private JPanel chipLabel(String text) {
        RoundedPanel chip = new RoundedPanel(8, new Color(232, 239, 248));
        chip.setLayout(new BorderLayout());
        chip.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        JLabel label = new JLabel(safeText(text, "-"));
        label.setFont(new Font("Monospaced", Font.BOLD, 14));
        label.setForeground(new Color(34, 47, 74));
        chip.add(label, BorderLayout.WEST);
        return chip;
    }

    private JPanel statusBadge(String status) {
        Color bg;
        Color fg;
        String text;
        if ("completed".equals(status)) {
            bg = new Color(231, 246, 236);
            fg = new Color(73, 190, 107);
            text = "completed";
        } else if ("cancelled".equals(status)) {
            bg = new Color(252, 236, 236);
            fg = new Color(224, 93, 93);
            text = "cancelled";
        } else if ("active".equals(status)) {
            bg = new Color(231, 246, 236);
            fg = new Color(73, 190, 107);
            text = "active";
        } else if ("deactivated".equals(status)) {
            bg = new Color(241, 245, 251);
            fg = new Color(89, 103, 129);
            text = "deactivated";
        } else {
            bg = new Color(252, 245, 231);
            fg = new Color(235, 153, 45);
            text = "pending";
        }

        RoundedPanel badge = new RoundedPanel(12, bg);
        badge.setLayout(new BorderLayout());
        badge.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(fg);
        badge.add(label, BorderLayout.CENTER);
        return badge;
    }

    private JButton actionButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton softActionButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private RoundedPanel tableRowCard(int height) {
        RoundedPanel row = new RoundedPanel(14, new Color(247, 250, 255));
        row.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMinimumSize(new Dimension(10, height));
        row.setPreferredSize(new Dimension(1000, height));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        return row;
    }

    private void applyFixedTableCardSize(RoundedPanel tableCard, boolean hasHeaderRow) {
        int headerBlock = hasHeaderRow ? 56 : 0;
        int totalHeight = TABLE_SCROLL_HEIGHT + headerBlock + 36;
        tableCard.setMinimumSize(new Dimension(100, totalHeight));
        tableCard.setPreferredSize(new Dimension(1000, totalHeight));
        tableCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, totalHeight));
    }

    private JPanel rowCell(Component content) {
        JPanel cell = new JPanel(new GridBagLayout());
        cell.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        cell.add(content, gbc);
        return cell;
    }

    private void updateRowsPanelPreferredHeight(JPanel rowsPanel, int rowCount) {
        int safeRows = Math.max(1, rowCount);
        int height = (safeRows * TABLE_ROW_HEIGHT) + (Math.max(0, safeRows - 1) * TABLE_ROW_GAP) + 6;
        rowsPanel.setMinimumSize(new Dimension(10, height));
        rowsPanel.setPreferredSize(new Dimension(10, height));
        rowsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    private JScrollPane tableRowsScrollPane(JPanel rowsPanel) {
        rowsPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        JScrollPane scrollPane = new JScrollPane(rowsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, Integer.MAX_VALUE));
        scrollPane.setMinimumSize(new Dimension(100, TABLE_SCROLL_HEIGHT));
        scrollPane.setPreferredSize(new Dimension(1000, TABLE_SCROLL_HEIGHT));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, TABLE_SCROLL_HEIGHT));
        return scrollPane;
    }

    private JPanel emptyStatePanel(String title, String description) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("[]");
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 30));
        icon.setForeground(new Color(166, 176, 195));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(77, 93, 127));

        JLabel descLabel = new JLabel(description);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(122, 136, 164));

        stack.add(icon);
        stack.add(Box.createVerticalStrut(8));
        stack.add(titleLabel);
        stack.add(Box.createVerticalStrut(6));
        stack.add(descLabel);

        wrapper.add(stack, new GridBagConstraints());
        return wrapper;
    }

    private JPanel separatorLine() {
        JPanel line = new JPanel();
        line.setBackground(new Color(225, 231, 241));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setPreferredSize(new Dimension(1000, 1));
        return line;
    }

    private RoundedPanel statCard(String title, JLabel valueLabel, String description, Color valueColor, Color iconBack) {
        RoundedPanel card = sectionCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        RoundedPanel iconBox = new RoundedPanel(14, iconBack);
        iconBox.setPreferredSize(new Dimension(86, 86));
        iconBox.setLayout(new GridBagLayout());
        JLabel dot = new JLabel("O");
        dot.setForeground(valueColor);
        dot.setFont(new Font("Segoe UI", Font.BOLD, 26));
        iconBox.add(dot, new GridBagConstraints());

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setForeground(new Color(28, 38, 60));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 29));
        valueLabel.setForeground(valueColor);
        JLabel d = new JLabel(description);
        d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        d.setForeground(new Color(107, 121, 148));

        right.add(t);
        right.add(Box.createVerticalStrut(6));
        right.add(valueLabel);
        right.add(Box.createVerticalStrut(2));
        right.add(d);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
        content.add(iconBox);
        content.add(Box.createHorizontalStrut(16));
        content.add(right);

        card.add(content, BorderLayout.WEST);
        return card;
    }

    private RoundedPanel statCard(String title, String value, String description, Color valueColor, Color iconBack) {
        return statCard(title, new JLabel(value), description, valueColor, iconBack);
    }

    private RoundedPanel summaryCard(String icon, String title, JLabel valueLabel, Color valueColor) {
        RoundedPanel card = sectionCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(icon + "  " + title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(new Color(78, 91, 116));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 46));
        valueLabel.setForeground(valueColor);

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(14));
        content.add(valueLabel);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private RoundedPanel summaryCard(String icon, String title, String value, Color valueColor) {
        return summaryCard(icon, title, new JLabel(value), valueColor);
    }

    private JLabel valLabel(LabelOut setter) {
        JLabel label = new JLabel("0");
        setter.set(label);
        return label;
    }

    private RoundedPanel sectionCard() {
        return new RoundedPanel(18, Color.WHITE);
    }

    private JPanel navStatic(String text) {
        RoundedPanel wrap = new RoundedPanel(12, new Color(0, 0, 0, 0));
        wrap.setLayout(new BorderLayout());
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        wrap.setPreferredSize(new Dimension(258, 56));
        wrap.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
        JLabel label = new JLabel(text);
        label.setForeground(new Color(231, 236, 246));
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        wrap.add(label, BorderLayout.WEST);
        return wrap;
    }

    private JPanel separator() {
        JPanel sep = new JPanel();
        sep.setBackground(new Color(58, 77, 114));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setPreferredSize(new Dimension(258, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sep;
    }

    private void setActiveView(String view) {
        if (receptionistMode && VIEW_RECEPTIONISTS.equals(view)) {
            view = VIEW_DASHBOARD;
        }
        if (!java.beans.Beans.isDesignTime()) {
            loadAllDataFromDatabase();
        }
        mainCardLayout.show(mainCardPanel, view);
        dashboardNav.setActive(VIEW_DASHBOARD.equals(view));
        appointmentsNav.setActive(VIEW_APPOINTMENTS.equals(view));
        patientsNav.setActive(VIEW_PATIENTS.equals(view));
        logNav.setActive(VIEW_LOG.equals(view));
        if (receptionistsNav != null) {
            receptionistsNav.setActive(VIEW_RECEPTIONISTS.equals(view));
        }

        if (VIEW_LOG.equals(view)) {
            setLogFilter(logFilter);
        }
        if (VIEW_APPOINTMENTS.equals(view)) {
            refreshAppointmentCalendarHeader();
            refreshAppointmentRows();
        }
        if (!receptionistMode && VIEW_RECEPTIONISTS.equals(view)) {
            setReceptionistFilter(receptionistFilter);
        }
    }

    private static class AppointmentRecord {
        final int id;
        final String patientName;
        final String dateText;
        final String timeText;
        final String reason;
        final LocalDate appointmentDate;
        final LocalTime appointmentTime;
        String status;
        String cancelReason;

        AppointmentRecord(
            int id,
            String patientName,
            String dateText,
            String timeText,
            String reason,
            String status,
            String cancelReason,
            LocalDate appointmentDate,
            LocalTime appointmentTime
        ) {
            this.id = id;
            this.patientName = patientName;
            this.dateText = dateText;
            this.timeText = timeText;
            this.reason = reason;
            this.status = status;
            this.cancelReason = cancelReason;
            this.appointmentDate = appointmentDate;
            this.appointmentTime = appointmentTime;
        }
    }

    private static class PatientRecord {
        final int id;
        final String patientId;
        String name;
        String contactNumber;

        PatientRecord(int id, String patientId, String name, String contactNumber) {
            this.id = id;
            this.patientId = patientId;
            this.name = name;
            this.contactNumber = contactNumber;
        }
    }

    private static class ReceptionistRecord {
        final int accountPk;
        final String accountId;
        String name;
        String username;
        String status;
        final String createdText;

        ReceptionistRecord(int accountPk, String accountId, String name, String username, String status, String createdText) {
            this.accountPk = accountPk;
            this.accountId = accountId;
            this.name = name;
            this.username = username;
            this.status = status;
            this.createdText = createdText;
        }
    }

    private static class AccountMeta {
        final boolean tableExists;
        final Set<String> columns;
        final String idColumn;
        final String nameColumn;
        final String createdColumn;

        AccountMeta(boolean tableExists, Set<String> columns, String idColumn, String nameColumn, String createdColumn) {
            this.tableExists = tableExists;
            this.columns = columns;
            this.idColumn = idColumn;
            this.nameColumn = nameColumn;
            this.createdColumn = createdColumn;
        }
    }

    @FunctionalInterface
    private interface LabelOut {
        void set(JLabel label);
    }

    private static class NavItem extends RoundedPanel {
        private final JLabel label;

        NavItem(String text, String viewKey) {
            super(12, new Color(0, 0, 0, 0));
            setName(viewKey);
            setLayout(new BorderLayout());
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
            setPreferredSize(new Dimension(258, 56));
            setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
            label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            add(label, BorderLayout.WEST);
            setActive(false);
        }

        void setActive(boolean active) {
            setFillColor(active ? new Color(59, 98, 220) : new Color(0, 0, 0, 0));
            label.setForeground(active ? Color.WHITE : new Color(231, 236, 246));
            repaint();
        }
    }

    private static class TogglePill extends RoundedPanel {
        private final JLabel label;

        TogglePill(String text) {
            super(18, new Color(245, 247, 252));
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
            setMaximumSize(new Dimension(250, 64));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.BOLD, 16));
            add(label, BorderLayout.CENTER);
            setActive(false);
        }

        void setActive(boolean active) {
            setFillColor(active ? new Color(59, 98, 220) : new Color(245, 247, 252));
            label.setForeground(active ? Color.WHITE : new Color(34, 43, 64));
            repaint();
        }
    }

    private static class GradientPanel extends JPanel {
        private final Color start;
        private final Color end;

        GradientPanel(Color start, Color end) {
            this.start = start;
            this.end = end;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, start, getWidth(), getHeight(), end));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class GradientButton extends JButton {
        private final int arc = 14;

        GradientButton(String text) {
            super(text);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, new Color(67, 114, 235), getWidth(), 0, new Color(56, 93, 219)));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private Color fill;

        RoundedPanel(int radius, Color fill) {
            this.radius = radius;
            this.fill = fill;
            setOpaque(false);
        }

        void setFillColor(Color fill) {
            this.fill = fill;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (fill.getAlpha() > 0) {
                g2.setColor(new Color(29, 47, 86, 16));
                g2.fill(new RoundRectangle2D.Double(2, 3, Math.max(0, getWidth() - 4), Math.max(0, getHeight() - 4), radius, radius));
                g2.setColor(fill);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
                g2.setColor(new Color(223, 229, 240));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, radius, radius));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class AvatarCircle extends JPanel {
        private final String text;

        AvatarCircle(String text) {
            this.text = text;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, new Color(72, 111, 235), getWidth(), getHeight(), new Color(93, 66, 221)));
            g2.fill(new Ellipse2D.Double(0, 0, getWidth(), getHeight()));
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
            int tw = g2.getFontMetrics().stringWidth(text);
            int th = g2.getFontMetrics().getAscent();
            g2.drawString(text, (getWidth() - tw) / 2, (getHeight() + th) / 2 - 3);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
