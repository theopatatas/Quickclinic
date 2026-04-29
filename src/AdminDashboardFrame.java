import com.toedter.calendar.JDateChooser;
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
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
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
    private static final int DASHBOARD_TODAY_VISIBLE_ROWS = 6;
    private static final int DASHBOARD_TODAY_ROW_HEIGHT = 74;
    private static final int DASHBOARD_TODAY_ROW_GAP = 8;
    private static final int DASHBOARD_TODAY_SCROLL_HEIGHT =
        (DASHBOARD_TODAY_VISIBLE_ROWS * DASHBOARD_TODAY_ROW_HEIGHT)
        + ((DASHBOARD_TODAY_VISIBLE_ROWS - 1) * DASHBOARD_TODAY_ROW_GAP)
        + 4;
    private static final int APPOINTMENT_DURATION_MINUTES = 60;
    private static final String TIME_SLOT_BOOKED_MESSAGE =
        "This time slot is already booked. Please choose another time.";
    private static final int[] APPOINTMENT_TABLE_COLUMN_WIDTHS = {
        120, // Date
        90,  // Time
        180, // Patient Name
        220, // Reason
        180, // Allergies
        300, // Notes
        120, // Status
        130  // Actions
    };
    private static final int APPOINTMENT_TABLE_TOTAL_WIDTH = 1520;
    private static final int[] PATIENT_TABLE_COLUMN_WIDTHS = {
        120, // Patient ID
        140, // First Name
        130, // Middle Name
        150, // Last Name
        160, // Contact Number
        180, // Emergency Contact
        110  // Actions
    };
    private static final int PATIENT_TABLE_TOTAL_WIDTH = 1120;
    private static final int[] RECEPTIONIST_TABLE_COLUMN_WIDTHS = {
        150, // Account ID
        230, // Name
        220, // Username
        170, // Created
        130, // Status
        150  // Actions
    };
    private static final int RECEPTIONIST_TABLE_TOTAL_WIDTH = 1200;
    private static final int TABLE_SCROLL_HEIGHT =
        (TABLE_VISIBLE_ROWS * TABLE_ROW_HEIGHT) + ((TABLE_VISIBLE_ROWS - 1) * TABLE_ROW_GAP) + 20;
    private static final Color INPUT_BORDER_COLOR = new Color(210, 220, 236);
    private static final Color INPUT_ERROR_BORDER_COLOR = new Color(224, 93, 93);
    private static final Color INPUT_FOCUS_BORDER_COLOR = new Color(102, 134, 239);
    private static final Color INPUT_HOVER_BORDER_COLOR = new Color(176, 191, 222);

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
    private static final DateTimeFormatter APPOINTMENT_FORM_DATE_FORMAT =
        DateTimeFormatter.ofPattern("MM/dd/uuuu", Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter APPOINTMENT_FORM_TIME_FORMAT =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("h:mm a")
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter APPOINTMENT_TABLE_DATE_FORMAT =
        DateTimeFormatter.ofPattern("MM/dd/yy", Locale.ENGLISH);
    private static final DateTimeFormatter APPOINTMENT_CALENDAR_TITLE_FORMAT =
        DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH);

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
    private JDateChooser appointmentCalendarDatePicker;
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
        previewLogButton.setText("Logs");
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
        previewPatientsCardTitleLabel.setText("Receptionist Accounts");
        previewPatientsCardPanel.add(previewPatientsCardTitleLabel, java.awt.BorderLayout.NORTH);

        previewPatientsCardValueLabel.setFont(new java.awt.Font("Segoe UI", 1, 52)); // NOI18N
        previewPatientsCardValueLabel.setForeground(new java.awt.Color(120, 80, 218));
        previewPatientsCardValueLabel.setText("0");
        previewPatientsCardPanel.add(previewPatientsCardValueLabel, java.awt.BorderLayout.CENTER);

        previewPatientsCardSubLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        previewPatientsCardSubLabel.setForeground(new java.awt.Color(107, 124, 157));
        previewPatientsCardSubLabel.setText("Total receptionist accounts");
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
        statsGrid.add(previewStatCard("Receptionist Accounts", "0", "Total receptionist accounts", new Color(120, 80, 218)));

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
        logNav = new NavItem("⊟  Logs", VIEW_LOG);
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

        int statsRows = receptionistMode ? 1 : 2;
        int statsCols = receptionistMode ? 3 : 2;
        JPanel stats = new JPanel(new GridLayout(statsRows, statsCols, 18, 18));
        stats.setOpaque(false);
        stats.add(statCard("Today's Appointments", valLabel(out -> dashboardTodayValue = out), "No appointments today", new Color(63, 101, 228), new Color(236, 240, 250)));
        stats.add(statCard("Pending", valLabel(out -> dashboardPendingValue = out), "Appointments pending", new Color(235, 153, 45), new Color(248, 240, 229)));
        stats.add(statCard("Completed", valLabel(out -> dashboardCompletedValue = out), "Appointments completed", new Color(46, 174, 102), new Color(232, 246, 238)));
        if (!receptionistMode) {
            stats.add(statCard("Receptionist Accounts", valLabel(out -> dashboardReceptionistsValue = out), "Total receptionist accounts", new Color(120, 80, 218), new Color(241, 234, 251)));
        } else {
            dashboardReceptionistsValue = null;
        }

        RoundedPanel appointments = sectionCard();
        appointments.setLayout(new BorderLayout());
        appointments.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        int todaySectionHeight = DASHBOARD_TODAY_SCROLL_HEIGHT + 98;
        appointments.setMinimumSize(new Dimension(100, todaySectionHeight));
        appointments.setPreferredSize(new Dimension(1000, todaySectionHeight));
        appointments.setMaximumSize(new Dimension(Integer.MAX_VALUE, todaySectionHeight + 26));

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
        dashboardTodayRows.setBorder(BorderFactory.createEmptyBorder());
        JScrollPane todayScroll = dashboardTodayRowsScrollPane(dashboardTodayRows);
        todayScroll.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        appointments.add(todayScroll, BorderLayout.CENTER);

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

        RoundedPanel controlsCard = sectionCard();
        controlsCard.setLayout(new GridBagLayout());
        controlsCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        controlsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        tableToggle = new TogglePill("☷  Table View");
        calendarToggle = new TogglePill("◫  Calendar View");

        RoundedPanel searchBox = searchFieldBox("Search by patient name or reason...");
        searchBox.setPreferredSize(new Dimension(560, 64));
        searchBox.setMinimumSize(new Dimension(260, 64));
        searchBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        GridBagConstraints controlsGbc = new GridBagConstraints();
        controlsGbc.gridx = 0;
        controlsGbc.gridy = 0;
        controlsGbc.anchor = GridBagConstraints.WEST;
        controlsGbc.fill = GridBagConstraints.NONE;
        controlsGbc.insets = new Insets(0, 0, 0, 10);
        controlsCard.add(tableToggle, controlsGbc);

        controlsGbc.gridx = 1;
        controlsGbc.insets = new Insets(0, 0, 0, 14);
        controlsCard.add(calendarToggle, controlsGbc);

        controlsGbc.gridx = 2;
        controlsGbc.weightx = 1.0;
        controlsGbc.fill = GridBagConstraints.HORIZONTAL;
        controlsGbc.insets = new Insets(0, 0, 0, 0);
        controlsCard.add(searchBox, controlsGbc);

        appointmentSearchField = (JTextField) searchBox.getClientProperty("searchField");
        bindLiveSearch(appointmentSearchField, () -> {
            appointmentSearchQuery = appointmentSearchField.getText().trim().toLowerCase(Locale.ENGLISH);
            refreshAppointmentRows();
        });

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
        body.add(controlsCard);
        body.add(Box.createVerticalStrut(18));
        body.add(appointmentModePanel);
        return wrapMainView(header, body);
    }

    private RoundedPanel buildTableAppointmentsPanel() {
        RoundedPanel tableCard = sectionCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        applyFixedTableCardSize(tableCard, true);

        appointmentTableRows = new JPanel();
        appointmentTableRows.setOpaque(false);
        appointmentTableRows.setLayout(new BoxLayout(appointmentTableRows, BoxLayout.Y_AXIS));
        appointmentTableRows.putClientProperty("qc.tableWidth", APPOINTMENT_TABLE_TOTAL_WIDTH);
        appointmentTableRows.setMinimumSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, 10));
        appointmentTableRows.setPreferredSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, TABLE_SCROLL_HEIGHT));

        JPanel headerRow = appointmentTableHeaderRow();
        JScrollPane tableScroll = tableRowsScrollPane(appointmentTableRows, true);
        tableScroll.setColumnHeaderView(headerRow);
        tableScroll.getColumnHeader().setOpaque(false);
        tableScroll.getColumnHeader().setBackground(new Color(0, 0, 0, 0));
        JPanel headerCorner = new JPanel();
        headerCorner.setOpaque(false);
        tableScroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, headerCorner);

        tableCard.add(horizontalScrollHintPanel(), BorderLayout.NORTH);
        tableCard.add(tableScroll, BorderLayout.CENTER);
        return tableCard;
    }

    private JPanel buildCalendarAppointmentsPanel() {
        JPanel calendarPanel = new JPanel();
        calendarPanel.setOpaque(false);
        calendarPanel.setLayout(new BoxLayout(calendarPanel, BoxLayout.Y_AXIS));

        RoundedPanel dateCard = sectionCard();
        dateCard.setLayout(new BoxLayout(dateCard, BoxLayout.Y_AXIS));
        dateCard.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        dateCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));

        JPanel topRow = new JPanel();
        topRow.setOpaque(false);
        topRow.setLayout(new GridBagLayout());
        topRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        JButton left = calendarNavButton("‹");
        left.addActionListener(e -> setAppointmentCalendarDate(appointmentCalendarDate.minusDays(1)));
        appointmentCalendarDatePicker = new JDateChooser();
        appointmentCalendarDatePicker.setDateFormatString("MM/dd/yyyy");
        appointmentCalendarDatePicker.setDate(Date.valueOf(appointmentCalendarDate));
        styleDateChooserField(appointmentCalendarDatePicker);
        appointmentCalendarDatePicker.setPreferredSize(new Dimension(260, 42));
        appointmentCalendarDatePicker.setMinimumSize(new Dimension(240, 42));
        appointmentCalendarDatePicker.setMaximumSize(new Dimension(280, 42));
        appointmentCalendarDatePicker.addPropertyChangeListener("date", evt -> {
            Object newValue = evt.getNewValue();
            if (!(newValue instanceof java.util.Date)) {
                return;
            }
            LocalDate selectedDate = new Date(((java.util.Date) newValue).getTime()).toLocalDate();
            if (!selectedDate.equals(appointmentCalendarDate)) {
                setAppointmentCalendarDate(selectedDate);
            }
        });
        JButton today = new JButton("Today");
        today.setFont(new Font("Segoe UI", Font.BOLD, 14));
        today.setFocusPainted(false);
        today.setContentAreaFilled(true);
        today.setOpaque(true);
        today.setBackground(new Color(60, 101, 228));
        today.setForeground(Color.WHITE);
        today.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        today.setCursor(new Cursor(Cursor.HAND_CURSOR));
        today.setPreferredSize(new Dimension(90, 36));
        today.setMaximumSize(new Dimension(94, 36));
        today.addActionListener(e -> setAppointmentCalendarDate(LocalDate.now()));
        JButton right = calendarNavButton("›");
        right.addActionListener(e -> setAppointmentCalendarDate(appointmentCalendarDate.plusDays(1)));
        JPanel centeredControls = new JPanel();
        centeredControls.setOpaque(false);
        centeredControls.setLayout(new BoxLayout(centeredControls, BoxLayout.X_AXIS));
        centeredControls.add(left);
        centeredControls.add(Box.createHorizontalStrut(12));
        centeredControls.add(appointmentCalendarDatePicker);
        centeredControls.add(Box.createHorizontalStrut(10));
        centeredControls.add(today);
        centeredControls.add(Box.createHorizontalStrut(12));
        centeredControls.add(right);
        topRow.add(centeredControls, new GridBagConstraints());

        dateCard.add(Box.createVerticalStrut(2));
        dateCard.add(topRow);
        dateCard.add(Box.createVerticalStrut(16));
        appointmentCalendarTitleLabel = new JLabel();
        appointmentCalendarTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        appointmentCalendarTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        appointmentCalendarTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appointmentCalendarTitleLabel.setForeground(new Color(31, 41, 63));
        JPanel titleRow = new JPanel(new GridBagLayout());
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        titleRow.add(appointmentCalendarTitleLabel, new GridBagConstraints());
        dateCard.add(titleRow);
        dateCard.add(Box.createVerticalStrut(2));
        refreshAppointmentCalendarHeader();

        RoundedPanel listCard = sectionCard();
        listCard.setLayout(new BorderLayout());
        listCard.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        applyFixedTableCardSize(listCard, true);

        appointmentCalendarRows = new JPanel();
        appointmentCalendarRows.setOpaque(false);
        appointmentCalendarRows.setLayout(new BoxLayout(appointmentCalendarRows, BoxLayout.Y_AXIS));
        appointmentCalendarRows.putClientProperty("qc.tableWidth", APPOINTMENT_TABLE_TOTAL_WIDTH);
        appointmentCalendarRows.setMinimumSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, 10));
        appointmentCalendarRows.setPreferredSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, TABLE_SCROLL_HEIGHT));
        JPanel headerRow = appointmentTableHeaderRow();
        JScrollPane calendarScroll = tableRowsScrollPane(appointmentCalendarRows, true);
        calendarScroll.getVerticalScrollBar().setUnitIncrement(24);
        calendarScroll.getVerticalScrollBar().setBlockIncrement(TABLE_ROW_HEIGHT + TABLE_ROW_GAP);
        calendarScroll.setColumnHeaderView(headerRow);
        calendarScroll.getColumnHeader().setOpaque(false);
        calendarScroll.getColumnHeader().setBackground(new Color(0, 0, 0, 0));
        JPanel calendarHeaderCorner = new JPanel();
        calendarHeaderCorner.setOpaque(false);
        calendarScroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, calendarHeaderCorner);
        listCard.add(horizontalScrollHintPanel(), BorderLayout.NORTH);
        listCard.add(calendarScroll, BorderLayout.CENTER);

        calendarPanel.add(dateCard);
        calendarPanel.add(Box.createVerticalStrut(20));
        calendarPanel.add(listCard);
        return calendarPanel;
    }

    private JPanel buildPatientsView() {
        JPanel header = viewHeader("Patients", "Manage patient records", "+  Add Appointment", this::openAddAppointmentDialog);

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
        tableCard.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        applyFixedTableCardSize(tableCard, true);

        patientRows = new JPanel();
        patientRows.setOpaque(false);
        patientRows.setLayout(new BoxLayout(patientRows, BoxLayout.Y_AXIS));
        patientRows.putClientProperty("qc.tableWidth", PATIENT_TABLE_TOTAL_WIDTH);
        patientRows.setMinimumSize(new Dimension(PATIENT_TABLE_TOTAL_WIDTH, 10));
        patientRows.setPreferredSize(new Dimension(PATIENT_TABLE_TOTAL_WIDTH, TABLE_SCROLL_HEIGHT));

        JPanel headerRow = patientTableHeaderRow();
        JScrollPane patientScroll = tableRowsScrollPane(patientRows, true);
        patientScroll.setColumnHeaderView(headerRow);
        patientScroll.getColumnHeader().setOpaque(false);
        patientScroll.getColumnHeader().setBackground(new Color(0, 0, 0, 0));
        JPanel patientHeaderCorner = new JPanel();
        patientHeaderCorner.setOpaque(false);
        patientScroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, patientHeaderCorner);
        patientScroll.getVerticalScrollBar().setUnitIncrement(22);
        patientScroll.getVerticalScrollBar().setBlockIncrement(TABLE_ROW_HEIGHT + TABLE_ROW_GAP);

        tableCard.add(horizontalScrollHintPanel(), BorderLayout.NORTH);
        tableCard.add(patientScroll, BorderLayout.CENTER);

        body.add(summaryCard);
        body.add(Box.createVerticalStrut(18));
        body.add(searchCard);
        body.add(Box.createVerticalStrut(18));
        body.add(tableCard);
        return wrapMainView(header, body);
    }

    private JPanel buildLogView() {
        JPanel header = viewHeader(
            "Logs / Archive",
            "Completed and cancelled appointments are archived here",
            null,
            null
        );

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
        tableCard.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        applyFixedTableCardSize(tableCard, true);

        logRows = new JPanel();
        logRows.setOpaque(false);
        logRows.setLayout(new BoxLayout(logRows, BoxLayout.Y_AXIS));
        logRows.putClientProperty("qc.tableWidth", APPOINTMENT_TABLE_TOTAL_WIDTH);
        logRows.setMinimumSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, 10));
        logRows.setPreferredSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, TABLE_SCROLL_HEIGHT));

        JPanel headerRow = appointmentTableHeaderRow();
        JScrollPane logScroll = tableRowsScrollPane(logRows, true);
        logScroll.setColumnHeaderView(headerRow);
        logScroll.getColumnHeader().setOpaque(false);
        logScroll.getColumnHeader().setBackground(new Color(0, 0, 0, 0));
        JPanel logHeaderCorner = new JPanel();
        logHeaderCorner.setOpaque(false);
        logScroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, logHeaderCorner);
        logScroll.getVerticalScrollBar().setUnitIncrement(22);
        logScroll.getVerticalScrollBar().setBlockIncrement(TABLE_ROW_HEIGHT + TABLE_ROW_GAP);

        tableCard.add(horizontalScrollHintPanel(), BorderLayout.NORTH);
        tableCard.add(logScroll, BorderLayout.CENTER);

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
        receptionistRows.putClientProperty("qc.tableWidth", RECEPTIONIST_TABLE_TOTAL_WIDTH);
        receptionistRows.setMinimumSize(new Dimension(RECEPTIONIST_TABLE_TOTAL_WIDTH, 10));
        receptionistRows.setPreferredSize(new Dimension(RECEPTIONIST_TABLE_TOTAL_WIDTH, TABLE_SCROLL_HEIGHT));

        JPanel headerRow = receptionistTableHeaderRow();
        JScrollPane receptionistScroll = tableRowsScrollPane(receptionistRows, true);
        receptionistScroll.setColumnHeaderView(headerRow);
        receptionistScroll.getColumnHeader().setOpaque(false);
        receptionistScroll.getColumnHeader().setBackground(new Color(0, 0, 0, 0));
        JPanel receptionistHeaderCorner = new JPanel();
        receptionistHeaderCorner.setOpaque(false);
        receptionistScroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, receptionistHeaderCorner);
        receptionistScroll.getVerticalScrollBar().setUnitIncrement(22);
        receptionistScroll.getVerticalScrollBar().setBlockIncrement(TABLE_ROW_HEIGHT + TABLE_ROW_GAP);

        tableCard.add(horizontalScrollHintPanel(), BorderLayout.NORTH);
        tableCard.add(receptionistScroll, BorderLayout.CENTER);

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

    private JButton calendarNavButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBackground(new Color(243, 247, 255));
        button.setForeground(new Color(50, 74, 135));
        button.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 217, 241), 1, true),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)
            )
        );
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(38, 36));
        button.setMaximumSize(new Dimension(38, 36));
        return button;
    }

    private Border buildInputBorder(Color borderColor) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        );
    }

    private Border buildRoundedInputBorder(Color borderColor) {
        return BorderFactory.createCompoundBorder(
            new LineBorder(borderColor, 1, true),
            BorderFactory.createEmptyBorder(2, 10, 2, 6)
        );
    }

    private void applyDefaultFieldBorder(JComponent field) {
        Border defaultBorder = buildInputBorder(INPUT_BORDER_COLOR);
        field.setBorder(defaultBorder);
        field.putClientProperty("qc.defaultBorder", defaultBorder);
        field.putClientProperty("qc.invalid", Boolean.FALSE);
    }

    private void highlightInvalidField(JComponent field) {
        Object invalidBorder = field.getClientProperty("qc.invalidBorder");
        if (invalidBorder instanceof Border) {
            field.setBorder((Border) invalidBorder);
        } else {
            field.setBorder(buildInputBorder(INPUT_ERROR_BORDER_COLOR));
        }
        field.putClientProperty("qc.invalid", Boolean.TRUE);
        field.requestFocusInWindow();
    }

    private void resetFieldValidationState(JComponent... fields) {
        if (fields == null) {
            return;
        }
        for (JComponent field : fields) {
            if (field == null) {
                continue;
            }
            Object stored = field.getClientProperty("qc.defaultBorder");
            if (stored instanceof Border) {
                field.setBorder((Border) stored);
            } else {
                applyDefaultFieldBorder(field);
            }
            field.putClientProperty("qc.invalid", Boolean.FALSE);
            Object buttonObj = field.getClientProperty("qc.calendarButton");
            Object buttonColorObj = field.getClientProperty("qc.calendarButtonBaseColor");
            if (buttonObj instanceof JButton && buttonColorObj instanceof Color) {
                ((JButton) buttonObj).setBackground((Color) buttonColorObj);
            }
        }
    }

    private void styleDateChooserField(JDateChooser datePicker) {
        Border defaultBorder = buildRoundedInputBorder(INPUT_BORDER_COLOR);
        Border focusBorder = buildRoundedInputBorder(INPUT_FOCUS_BORDER_COLOR);
        Border hoverBorder = buildRoundedInputBorder(INPUT_HOVER_BORDER_COLOR);
        Border invalidBorder = buildRoundedInputBorder(INPUT_ERROR_BORDER_COLOR);

        datePicker.setBorder(defaultBorder);
        datePicker.putClientProperty("qc.defaultBorder", defaultBorder);
        datePicker.putClientProperty("qc.focusBorder", focusBorder);
        datePicker.putClientProperty("qc.hoverBorder", hoverBorder);
        datePicker.putClientProperty("qc.invalidBorder", invalidBorder);
        datePicker.putClientProperty("qc.invalid", Boolean.FALSE);
        datePicker.setOpaque(true);
        datePicker.setBackground(Color.WHITE);
        datePicker.setPreferredSize(new Dimension(320, 42));
        datePicker.setMinimumSize(new Dimension(320, 42));

        JTextField editorField = (JTextField) datePicker.getDateEditor().getUiComponent();
        editorField.setEditable(false);
        editorField.setFocusable(true);
        editorField.setHorizontalAlignment(SwingConstants.LEFT);
        editorField.setBackground(Color.WHITE);
        editorField.setForeground(new Color(35, 46, 68));
        editorField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        editorField.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 6));
        editorField.setCaretColor(new Color(61, 99, 210));

        JButton calendarButton = datePicker.getCalendarButton();
        Color buttonBaseColor = new Color(244, 247, 255);
        Color buttonHoverColor = new Color(234, 241, 255);
        Color buttonFocusColor = new Color(223, 234, 255);
        if (calendarButton != null) {
            calendarButton.setFocusable(true);
            calendarButton.setFocusPainted(false);
            calendarButton.setContentAreaFilled(true);
            calendarButton.setOpaque(true);
            calendarButton.setBackground(buttonBaseColor);
            calendarButton.setForeground(new Color(56, 88, 188));
            calendarButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            calendarButton.setBorder(
                BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(205, 217, 239), 1, true),
                    BorderFactory.createEmptyBorder(0, 0, 0, 0)
                )
            );
            calendarButton.setPreferredSize(new Dimension(40, 34));
            calendarButton.setMinimumSize(new Dimension(40, 34));
            calendarButton.setMaximumSize(new Dimension(40, 34));
            calendarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            calendarButton.setToolTipText("Choose date");
            datePicker.putClientProperty("qc.calendarButton", calendarButton);
            datePicker.putClientProperty("qc.calendarButtonBaseColor", buttonBaseColor);
        }

        Runnable applyDefaultBorder = () -> {
            if (!Boolean.TRUE.equals(datePicker.getClientProperty("qc.invalid"))) {
                datePicker.setBorder(defaultBorder);
            }
        };
        Runnable applyFocusBorder = () -> {
            if (!Boolean.TRUE.equals(datePicker.getClientProperty("qc.invalid"))) {
                datePicker.setBorder(focusBorder);
                if (calendarButton != null) {
                    calendarButton.setBackground(buttonFocusColor);
                }
            }
        };
        Runnable applyHoverBorder = () -> {
            if (!Boolean.TRUE.equals(datePicker.getClientProperty("qc.invalid"))) {
                datePicker.setBorder(hoverBorder);
                if (calendarButton != null) {
                    calendarButton.setBackground(buttonHoverColor);
                }
            }
        };
        Runnable applyButtonBaseColor = () -> {
            if (calendarButton != null) {
                calendarButton.setBackground(buttonBaseColor);
            }
        };

        FocusAdapter focusAdapter = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                applyFocusBorder.run();
            }

            @Override
            public void focusLost(FocusEvent e) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (editorField.isFocusOwner()
                        || (calendarButton != null && calendarButton.isFocusOwner())) {
                        applyFocusBorder.run();
                    } else {
                        applyDefaultBorder.run();
                        applyButtonBaseColor.run();
                    }
                });
            }
        };
        editorField.addFocusListener(focusAdapter);
        if (calendarButton != null) {
            calendarButton.addFocusListener(focusAdapter);
        }

        MouseAdapter hoverAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                applyHoverBorder.run();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (editorField.isFocusOwner()
                        || (calendarButton != null && calendarButton.isFocusOwner())) {
                        applyFocusBorder.run();
                    } else {
                        applyDefaultBorder.run();
                        applyButtonBaseColor.run();
                    }
                });
            }
        };
        datePicker.addMouseListener(hoverAdapter);
        editorField.addMouseListener(hoverAdapter);
        if (calendarButton != null) {
            calendarButton.addMouseListener(hoverAdapter);
        }
    }

    private void styleInputField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        applyDefaultFieldBorder(field);
    }

    private void applyLettersOnlyInputRestriction(JTextField field) {
        applyFilteredInputRestriction(field, "[A-Za-z]*", -1);
    }

    private void applyLettersAndSpacesInputRestriction(JTextField field) {
        applyFilteredInputRestriction(field, "[A-Za-z ]*", -1);
    }

    private void applyDigitsOnlyInputRestriction(JTextField field, int maxLength) {
        applyFilteredInputRestriction(field, "\\d*", maxLength);
    }

    private void applyFilteredInputRestriction(JTextField field, String allowedRegex, int maxLength) {
        if (!(field.getDocument() instanceof AbstractDocument)) {
            return;
        }
        AbstractDocument document = (AbstractDocument) field.getDocument();
        document.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                replace(fb, offset, 0, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String incoming = text == null ? "" : text;
                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                String next = current.substring(0, offset) + incoming + current.substring(offset + length);

                if ((maxLength < 0 || next.length() <= maxLength) && next.matches(allowedRegex)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    private void stylePasswordField(JPasswordField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        applyDefaultFieldBorder(field);
    }

    private JPanel landscapeFormPanel(String[] labels, JComponent[] fields, int columns, int preferredWidth) {
        if (labels == null || fields == null || labels.length != fields.length || labels.length == 0) {
            return new JPanel();
        }

        int safeColumns = Math.max(1, columns);
        int rows = (labels.length + safeColumns - 1) / safeColumns;

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 12, 0);

        for (int i = 0; i < labels.length; i++) {
            int row = i / safeColumns;
            int col = i % safeColumns;
            gbc.gridx = col;
            gbc.gridy = row;
            gbc.insets = new Insets(0, col == 0 ? 0 : 12, 12, 0);
            form.add(landscapeFieldBlock(labels[i], fields[i]), gbc);
        }

        int preferredHeight = Math.max(220, (rows * 86) + 30);
        form.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        form.setMinimumSize(new Dimension(Math.max(560, preferredWidth - 80), preferredHeight));
        return form;
    }

    private JPanel landscapeFieldBlock(String labelText, JComponent field) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(52, 64, 91));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        label.setLabelFor(field);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (field.getPreferredSize() != null) {
            int height = Math.max(40, field.getPreferredSize().height);
            field.setPreferredSize(new Dimension(320, height));
            field.setMinimumSize(new Dimension(220, height));
            field.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        }

        block.add(label);
        block.add(field);
        return block;
    }

    private void refreshAppointmentCalendarHeader() {
        if (appointmentCalendarDatePicker != null) {
            java.util.Date pickerDate = appointmentCalendarDatePicker.getDate();
            LocalDate pickerLocalDate = pickerDate == null
                ? null
                : new Date(pickerDate.getTime()).toLocalDate();
            if (pickerLocalDate == null || !pickerLocalDate.equals(appointmentCalendarDate)) {
                appointmentCalendarDatePicker.setDate(Date.valueOf(appointmentCalendarDate));
            }
        }
        if (appointmentCalendarTitleLabel != null) {
            String title = appointmentCalendarDate.format(APPOINTMENT_CALENDAR_TITLE_FORMAT);
            appointmentCalendarTitleLabel.setText(title);
        }
    }

    private void setAppointmentCalendarDate(LocalDate newDate) {
        if (newDate == null) {
            return;
        }
        if (!newDate.equals(appointmentCalendarDate)) {
            appointmentCalendarDate = newDate;
        }
        refreshAppointmentCalendarHeader();
        refreshAppointmentRows();
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
                "CREATE TABLE IF NOT EXISTS patient (" +
                "patient_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "first_name VARCHAR(80) NOT NULL, " +
                "middle_name VARCHAR(80) NULL, " +
                "last_name VARCHAR(80) NOT NULL, " +
                "contact_number VARCHAR(40) NULL, " +
                "emergency_contact_number VARCHAR(40) NULL, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)"
            );
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS dentist (" +
                "dentist_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "first_name VARCHAR(80) NOT NULL, " +
                "middle_name VARCHAR(80) NULL, " +
                "last_name VARCHAR(80) NOT NULL)"
            );
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS appointment (" +
                "appointment_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "patient_id INT NULL, " +
                "dentist_id INT NULL, " +
                "appointment_date DATE NOT NULL, " +
                "appointment_time TIME NOT NULL, " +
                "cancel_reason VARCHAR(255) DEFAULT '-', " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "allergies VARCHAR(255) NULL, " +
                "status ENUM('pending','completed','cancelled') NOT NULL DEFAULT 'pending', " +
                "reason VARCHAR(255) NOT NULL, " +
                "notes VARCHAR(500) NULL)"
            );
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS account (" +
                "account_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(80) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "role ENUM('admin','receptionist') NULL, " +
                "status ENUM('active','deactivated') NOT NULL DEFAULT 'active')"
            );
        }
        ensurePatientTableStructure(con);
        ensureDentistTableStructure(con);
        ensureAppointmentTableStructure(con);
        ensureAccountTableStructure(con);
        ensureAppointmentForeignKeys(con);
    }

    private void ensurePatientTableStructure(Connection con) throws SQLException {
        Set<String> patientColumns = getTableColumns(con, "patient");
        if (!patientColumns.contains("first_name")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE patient ADD COLUMN first_name VARCHAR(80) NULL");
            }
        }
        patientColumns = getTableColumns(con, "patient");
        if (!patientColumns.contains("middle_name")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE patient ADD COLUMN middle_name VARCHAR(80) NULL");
            }
        }
        patientColumns = getTableColumns(con, "patient");
        if (!patientColumns.contains("last_name")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE patient ADD COLUMN last_name VARCHAR(80) NULL");
            }
        }
        patientColumns = getTableColumns(con, "patient");
        if (!patientColumns.contains("contact_number")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE patient ADD COLUMN contact_number VARCHAR(40) NULL");
            }
        }
        patientColumns = getTableColumns(con, "patient");
        if (!patientColumns.contains("emergency_contact_number")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE patient ADD COLUMN emergency_contact_number VARCHAR(40) NULL");
            }
        }
        patientColumns = getTableColumns(con, "patient");
        if (patientColumns.contains("full_name")) {
            migrateLegacyPatientFullNameData(con);
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE patient DROP COLUMN full_name");
            } catch (SQLException ignored) {
                // Keep compatibility if column cannot be dropped.
            }
        }

        try (Statement st = con.createStatement()) {
            st.executeUpdate(
                "UPDATE patient SET emergency_contact_number=NULL " +
                "WHERE emergency_contact_number IS NOT NULL " +
                "AND (TRIM(emergency_contact_number)='' OR TRIM(emergency_contact_number)='-')"
            );
            st.executeUpdate(
                "UPDATE patient SET first_name='Unknown' " +
                "WHERE first_name IS NULL OR TRIM(first_name)=''"
            );
            st.executeUpdate(
                "UPDATE patient SET last_name='Unknown' " +
                "WHERE last_name IS NULL OR TRIM(last_name)=''"
            );
        }

        ensureUniqueIndexIfPossible(con, "patient", "uq_patient_contact_number", "contact_number");
        ensureUniqueIndexIfPossible(con, "patient", "uq_patient_emergency_contact_number", "emergency_contact_number");
    }

    private void ensureDentistTableStructure(Connection con) throws SQLException {
        Set<String> dentistColumns = getTableColumns(con, "dentist");
        if (!dentistColumns.contains("first_name")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE dentist ADD COLUMN first_name VARCHAR(80) NOT NULL DEFAULT 'Unknown'");
            }
        }
        dentistColumns = getTableColumns(con, "dentist");
        if (!dentistColumns.contains("middle_name")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE dentist ADD COLUMN middle_name VARCHAR(80) NULL");
            }
        }
        dentistColumns = getTableColumns(con, "dentist");
        if (!dentistColumns.contains("last_name")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE dentist ADD COLUMN last_name VARCHAR(80) NOT NULL DEFAULT 'Unknown'");
            }
        }
    }

    private void migrateLegacyPatientFullNameData(Connection con) throws SQLException {
        String selectSql = "SELECT patient_id, full_name, first_name, middle_name, last_name FROM patient";
        String updateSql = "UPDATE patient SET first_name=?, middle_name=?, last_name=? WHERE patient_id=?";
        try (PreparedStatement select = con.prepareStatement(selectSql);
             ResultSet rs = select.executeQuery();
             PreparedStatement update = con.prepareStatement(updateSql)) {
            while (rs.next()) {
                String currentFirst = safeText(rs.getString("first_name"), "").trim();
                String currentMiddle = safeText(rs.getString("middle_name"), "").trim();
                String currentLast = safeText(rs.getString("last_name"), "").trim();
                String legacyFullName = safeText(rs.getString("full_name"), "").trim();
                if (legacyFullName.isBlank()) {
                    continue;
                }
                if (!currentFirst.isBlank() && !currentLast.isBlank()) {
                    continue;
                }
                String[] parts = splitPatientNameParts(legacyFullName);
                String firstName = safeText(parts[0], "").trim();
                String middleName = safeText(parts[1], "").trim();
                String lastName = safeText(parts[2], "").trim();
                if (firstName.isBlank()) {
                    firstName = legacyFullName;
                }
                if (lastName.isBlank()) {
                    lastName = "Unknown";
                }
                update.setString(1, firstName);
                if (middleName.isBlank()) {
                    update.setNull(2, Types.VARCHAR);
                } else {
                    update.setString(2, middleName);
                }
                update.setString(3, lastName);
                update.setInt(4, rs.getInt("patient_id"));
                update.addBatch();
            }
            update.executeBatch();
        }
    }

    private void ensureAppointmentTableStructure(Connection con) throws SQLException {
        Set<String> appointmentColumns = getTableColumns(con, "appointment");
        if (!appointmentColumns.contains("patient_id")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE appointment ADD COLUMN patient_id INT NULL");
            }
            appointmentColumns = getTableColumns(con, "appointment");
        }
        if (!appointmentColumns.contains("dentist_id")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE appointment ADD COLUMN dentist_id INT NULL");
            }
            appointmentColumns = getTableColumns(con, "appointment");
        }
        if (!appointmentColumns.contains("allergies")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE appointment ADD COLUMN allergies VARCHAR(255) NULL");
            }
            appointmentColumns = getTableColumns(con, "appointment");
        }
        if (!appointmentColumns.contains("notes")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE appointment ADD COLUMN notes VARCHAR(500) NULL");
            }
            appointmentColumns = getTableColumns(con, "appointment");
        }
        if (!appointmentColumns.contains("status")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE appointment ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'pending'");
            }
        }
        appointmentColumns = getTableColumns(con, "appointment");
        if (!appointmentColumns.contains("cancel_reason")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE appointment ADD COLUMN cancel_reason VARCHAR(255) DEFAULT '-'");
            }
        }
        appointmentColumns = getTableColumns(con, "appointment");
        if (!appointmentColumns.contains("reason")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE appointment ADD COLUMN reason VARCHAR(255) NOT NULL DEFAULT ''");
            }
        }

        try (Statement st = con.createStatement()) {
            st.executeUpdate(
                "UPDATE appointment SET allergies='N/A' " +
                "WHERE allergies IS NULL OR TRIM(allergies)=''"
            );
            st.executeUpdate(
                "UPDATE appointment SET status='pending' " +
                "WHERE status IS NULL OR TRIM(status)=''"
            );
            st.executeUpdate(
                "UPDATE appointment SET notes='' " +
                "WHERE notes IS NULL"
            );
            st.executeUpdate(
                "UPDATE appointment a LEFT JOIN patient p ON p.patient_id = a.patient_id " +
                "SET a.patient_id=NULL WHERE a.patient_id IS NOT NULL AND p.patient_id IS NULL"
            );
            st.executeUpdate(
                "UPDATE appointment a LEFT JOIN dentist d ON d.dentist_id = a.dentist_id " +
                "SET a.dentist_id=NULL WHERE a.dentist_id IS NOT NULL AND d.dentist_id IS NULL"
            );
        }
    }

    private void ensureAccountTableStructure(Connection con) throws SQLException {
        Set<String> accountColumns = getTableColumns(con, "account");
        if (!accountColumns.contains("username")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE account ADD COLUMN username VARCHAR(80) NULL");
            }
        }
        accountColumns = getTableColumns(con, "account");
        if (!accountColumns.contains("password")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE account ADD COLUMN password VARCHAR(255) NULL");
            }
        }
        accountColumns = getTableColumns(con, "account");
        if (!accountColumns.contains("role")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE account ADD COLUMN role ENUM('admin','receptionist') NULL");
            }
        }
        accountColumns = getTableColumns(con, "account");
        if (!accountColumns.contains("status")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE account ADD COLUMN status ENUM('active','deactivated') NOT NULL DEFAULT 'active'");
            }
        }
    }

    private void ensureAppointmentForeignKeys(Connection con) throws SQLException {
        if (!hasForeignKey(con, "appointment", "fk_appointment_patient")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate(
                    "ALTER TABLE appointment " +
                    "ADD CONSTRAINT fk_appointment_patient " +
                    "FOREIGN KEY (patient_id) REFERENCES patient(patient_id) " +
                    "ON UPDATE CASCADE ON DELETE SET NULL"
                );
            } catch (SQLException ignored) {
                // Keep app operational even if FK cannot be applied in existing environments.
            }
        }
        if (!hasForeignKey(con, "appointment", "fk_appointment_dentist")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate(
                    "ALTER TABLE appointment " +
                    "ADD CONSTRAINT fk_appointment_dentist " +
                    "FOREIGN KEY (dentist_id) REFERENCES dentist(dentist_id) " +
                    "ON UPDATE CASCADE ON DELETE SET NULL"
                );
            } catch (SQLException ignored) {
                // Keep app operational even if FK cannot be applied in existing environments.
            }
        }
    }

    private void ensureUniqueIndexIfPossible(Connection con, String table, String indexName, String column) throws SQLException {
        if (hasIndex(con, table, indexName) || hasDuplicateNonBlankValues(con, table, column)) {
            return;
        }
        try (Statement st = con.createStatement()) {
            st.executeUpdate("CREATE UNIQUE INDEX " + indexName + " ON " + table + " (" + column + ")");
        } catch (SQLException ignored) {
            // Keep app-level validation as fallback when index creation is not possible.
        }
    }

    private boolean hasIndex(Connection con, String tableName, String indexName) throws SQLException {
        DatabaseMetaData meta = con.getMetaData();
        try (ResultSet rs = meta.getIndexInfo(con.getCatalog(), null, tableName, false, false)) {
            while (rs.next()) {
                String existing = rs.getString("INDEX_NAME");
                if (existing != null && existing.equalsIgnoreCase(indexName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasForeignKey(Connection con, String tableName, String fkName) throws SQLException {
        DatabaseMetaData meta = con.getMetaData();
        try (ResultSet rs = meta.getImportedKeys(con.getCatalog(), null, tableName)) {
            while (rs.next()) {
                String existing = rs.getString("FK_NAME");
                if (existing != null && existing.equalsIgnoreCase(fkName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasDuplicateNonBlankValues(Connection con, String tableName, String columnName) throws SQLException {
        String sql = "SELECT 1 FROM " + tableName +
            " WHERE " + columnName + " IS NOT NULL AND TRIM(" + columnName + ") <> '' " +
            "GROUP BY " + columnName + " HAVING COUNT(*) > 1 LIMIT 1";
        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            return rs.next();
        }
    }

    private void normalizeDuplicateTables(Connection con) throws SQLException {
        Set<String> tables = getExistingTables(con);
        mergePatientLikeTable(con, tables, "patients");
        mergePatientLikeTable(con, tables, "patient_information");
        mergeAppointmentLikeTable(con, tables, "appointments");
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
        if (!tables.contains(source) || "patient".equals(source)) {
            return;
        }

        Set<String> srcCols = getTableColumns(con, source);
        String firstNameCol = firstExisting(srcCols, "first_name", "firstname", "first");
        String middleNameCol = firstExisting(srcCols, "middle_name", "middlename", "middle", "m_name");
        String lastNameCol = firstExisting(srcCols, "last_name", "lastname", "last", "surname");
        String nameCol = firstExisting(srcCols, "full_name", "name", "patient_name");
        String contactCol = firstExisting(srcCols, "contact_number", "contact", "contact_no", "phone");
        String emergencyCol = firstExisting(srcCols, "emergency_contact_number", "emergency_contact", "emergency_contact_no", "emergency_phone");
        if (contactCol == null || (nameCol == null && firstNameCol == null)) {
            return;
        }

        String sql =
            "SELECT " +
            (firstNameCol != null ? "`" + firstNameCol + "` AS first_name, " : "") +
            (middleNameCol != null ? "`" + middleNameCol + "` AS middle_name, " : "") +
            (lastNameCol != null ? "`" + lastNameCol + "` AS last_name, " : "") +
            (nameCol != null ? "`" + nameCol + "` AS full_name, " : "") +
            "`" + contactCol + "` AS contact_number" +
            (emergencyCol != null ? ", `" + emergencyCol + "` AS emergency_contact_number" : "") +
            " FROM `" + source + "`";

        try (Statement read = con.createStatement();
             ResultSet rs = read.executeQuery(sql);
             PreparedStatement insert = con.prepareStatement(
                 "INSERT INTO patient (first_name, middle_name, last_name, contact_number, emergency_contact_number) VALUES (?, ?, ?, ?, ?)"
             )) {
            while (rs.next()) {
                String firstName = firstNameCol == null ? "" : safeText(rs.getString("first_name"), "").trim();
                String middleName = middleNameCol == null ? "" : safeText(rs.getString("middle_name"), "").trim();
                String lastName = lastNameCol == null ? "" : safeText(rs.getString("last_name"), "").trim();
                String fullName = nameCol == null ? "" : safeText(rs.getString("full_name"), "").trim();
                String contact = rs.getString("contact_number");
                if ((firstName.isBlank() || lastName.isBlank()) && !fullName.isBlank()) {
                    String[] parts = splitPatientNameParts(fullName);
                    firstName = safeText(parts[0], "").trim();
                    middleName = safeText(parts[1], "").trim();
                    lastName = safeText(parts[2], "").trim();
                    if (firstName.isBlank()) {
                        firstName = fullName;
                    }
                    if (lastName.isBlank()) {
                        lastName = "Unknown";
                    }
                }
                if (firstName.isBlank() || lastName.isBlank() || contact == null || contact.isBlank()) {
                    continue;
                }
                String emergencyContact = emergencyCol == null ? null : rs.getString("emergency_contact_number");
                if (emergencyContact != null && emergencyContact.isBlank()) {
                    emergencyContact = null;
                }
                insert.setString(1, firstName);
                if (middleName.isBlank()) {
                    insert.setNull(2, Types.VARCHAR);
                } else {
                    insert.setString(2, middleName);
                }
                insert.setString(3, lastName);
                insert.setString(4, contact.trim());
                if (emergencyContact == null) {
                    insert.setNull(5, Types.VARCHAR);
                } else {
                    insert.setString(5, emergencyContact.trim());
                }
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
        if (!tables.contains(source) || "appointment".equals(source)) {
            return;
        }

        Set<String> srcCols = getTableColumns(con, source);
        String patientCol = firstExisting(srcCols, "patient_name", "patient", "name");
        String dateCol = firstExisting(srcCols, "appointment_date", "date");
        String timeCol = firstExisting(srcCols, "appointment_time", "time");
        String reasonCol = firstExisting(srcCols, "reason", "purpose");
        String allergiesCol = firstExisting(srcCols, "allergies", "allergy");
        String notesCol = firstExisting(srcCols, "notes", "note");
        String contactCol = firstExisting(srcCols, "contact_number", "contact", "contact_no", "phone");
        String emergencyCol = firstExisting(srcCols, "emergency_contact_number", "emergency_contact", "emergency_contact_no", "emergency_phone");
        String statusCol = firstExisting(srcCols, "status");
        String cancelCol = firstExisting(srcCols, "cancel_reason", "cancellation_reason");
        if (patientCol == null || dateCol == null || timeCol == null || reasonCol == null) {
            return;
        }

        String sql = "SELECT `" + patientCol + "` AS patient_name, `" + dateCol + "` AS appointment_date, " +
            "`" + timeCol + "` AS appointment_time, `" + reasonCol + "` AS reason" +
            (allergiesCol != null ? ", `" + allergiesCol + "` AS allergies" : "") +
            (notesCol != null ? ", `" + notesCol + "` AS notes" : "") +
            (contactCol != null ? ", `" + contactCol + "` AS contact_number" : "") +
            (emergencyCol != null ? ", `" + emergencyCol + "` AS emergency_contact_number" : "") +
            (statusCol != null ? ", `" + statusCol + "` AS status" : "") +
            (cancelCol != null ? ", `" + cancelCol + "` AS cancel_reason" : "") +
            " FROM `" + source + "`";
        try (Statement read = con.createStatement();
             ResultSet rs = read.executeQuery(sql);
             PreparedStatement insert = con.prepareStatement(
                 "INSERT INTO appointment (patient_id, dentist_id, appointment_date, appointment_time, cancel_reason, allergies, status, reason, notes) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
             )) {
            while (rs.next()) {
                String patient = rs.getString("patient_name");
                String reason = rs.getString("reason");
                if (patient == null || patient.isBlank() || reason == null || reason.isBlank()) {
                    continue;
                }
                String allergies = allergiesCol == null ? "N/A" : rs.getString("allergies");
                if (allergies == null || allergies.isBlank()) {
                    allergies = "N/A";
                }
                LocalDate date = parseDateValue(rs.getObject("appointment_date"));
                LocalTime time = parseTimeValue(rs.getObject("appointment_time"));
                String status = statusCol == null ? "pending" : normalizeAppointmentStatus(rs.getString("status"));
                String cancelReason = cancelCol == null ? "-" : rs.getString("cancel_reason");
                if (cancelReason == null || cancelReason.isBlank()) {
                    cancelReason = "-";
                }
                String notes = notesCol == null ? "" : rs.getString("notes");
                if (notes == null) {
                    notes = "";
                }
                String contact = contactCol == null ? "" : rs.getString("contact_number");
                if (contact == null) {
                    contact = "";
                }
                String emergencyContact = emergencyCol == null ? "" : rs.getString("emergency_contact_number");
                if (emergencyContact == null) {
                    emergencyContact = "";
                }
                Integer patientId = ensurePatientExistsForAppointment(con, patient.trim(), contact.trim(), emergencyContact.trim());

                if (patientId == null) {
                    insert.setNull(1, Types.INTEGER);
                } else {
                    insert.setInt(1, patientId);
                }
                insert.setNull(2, Types.INTEGER);
                insert.setDate(3, Date.valueOf(date));
                insert.setTime(4, Time.valueOf(time.withSecond(0).withNano(0)));
                insert.setString(5, cancelReason);
                insert.setString(6, allergies.trim());
                insert.setString(7, status);
                insert.setString(8, reason.trim());
                insert.setString(9, notes.trim());
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
        String sql = "SELECT patient_id, first_name, middle_name, last_name, contact_number, emergency_contact_number " +
            "FROM patient ORDER BY patient_id DESC";
        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("patient_id");
                String code = String.format("PT-%04d", id);
                String emergencyContact = rs.getString("emergency_contact_number");
                if (emergencyContact == null) {
                    emergencyContact = "";
                }
                patientRecords.add(new PatientRecord(
                    id,
                    code,
                    rs.getString("first_name"),
                    rs.getString("middle_name"),
                    rs.getString("last_name"),
                    rs.getString("contact_number"),
                    emergencyContact
                ));
            }
        }
    }

    private void loadAppointmentsFromDatabase(Connection con) throws SQLException {
        String sql = "SELECT a.appointment_id, a.patient_id, " +
                     "COALESCE(NULLIF(TRIM(CONCAT_WS(' ', " +
                     "NULLIF(TRIM(p.first_name), ''), " +
                     "NULLIF(TRIM(p.middle_name), ''), " +
                     "NULLIF(TRIM(p.last_name), '')" +
                     ")), ''), '(No patient)') AS linked_patient_name, " +
                     "COALESCE(NULLIF(TRIM(p.contact_number), ''), '') AS linked_contact_number, " +
                     "COALESCE(NULLIF(TRIM(p.emergency_contact_number), ''), '') AS linked_emergency_contact_number, " +
                     "a.appointment_date, a.appointment_time, a.reason, " +
                     "COALESCE(NULLIF(TRIM(a.allergies), ''), 'N/A') AS allergies, " +
                     "COALESCE(a.notes, '') AS notes, a.status, " +
                     "COALESCE(a.cancel_reason, '-') AS cancel_reason " +
                     "FROM appointment a " +
                     "LEFT JOIN patient p ON p.patient_id = a.patient_id " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("appointment_id");
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
                    rs.getObject("patient_id") == null ? -1 : rs.getInt("patient_id"),
                    rs.getString("linked_patient_name"),
                    appointmentDate.format(DATE_LABEL_FORMAT),
                    appointmentTime.format(APPOINTMENT_INPUT_TIME_FORMAT),
                    rs.getString("reason"),
                    rs.getString("allergies"),
                    rs.getString("notes"),
                    status,
                    cancelReason,
                    rs.getString("linked_contact_number"),
                    rs.getString("linked_emergency_contact_number"),
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

    private AppointmentRecord fetchAppointmentRecordById(int appointmentId) {
        String sql = "SELECT a.appointment_id, a.patient_id, " +
                     "COALESCE(NULLIF(TRIM(CONCAT_WS(' ', " +
                     "NULLIF(TRIM(p.first_name), ''), " +
                     "NULLIF(TRIM(p.middle_name), ''), " +
                     "NULLIF(TRIM(p.last_name), '')" +
                     ")), ''), '(No patient)') AS linked_patient_name, " +
                     "COALESCE(NULLIF(TRIM(p.contact_number), ''), '') AS linked_contact_number, " +
                     "COALESCE(NULLIF(TRIM(p.emergency_contact_number), ''), '') AS linked_emergency_contact_number, " +
                     "a.appointment_date, a.appointment_time, a.reason, " +
                     "COALESCE(NULLIF(TRIM(a.allergies), ''), 'N/A') AS allergies, " +
                     "COALESCE(a.notes, '') AS notes, a.status, " +
                     "COALESCE(a.cancel_reason, '-') AS cancel_reason " +
                     "FROM appointment a " +
                     "LEFT JOIN patient p ON p.patient_id = a.patient_id " +
                     "WHERE a.appointment_id=? LIMIT 1";
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                return null;
            }
            ensureClinicTables(con);
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, appointmentId);
                try (ResultSet rs = pst.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
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
                    return new AppointmentRecord(
                        rs.getInt("appointment_id"),
                        rs.getObject("patient_id") == null ? -1 : rs.getInt("patient_id"),
                        rs.getString("linked_patient_name"),
                        appointmentDate.format(DATE_LABEL_FORMAT),
                        appointmentTime.format(APPOINTMENT_INPUT_TIME_FORMAT),
                        rs.getString("reason"),
                        rs.getString("allergies"),
                        rs.getString("notes"),
                        status,
                        cancelReason,
                        rs.getString("linked_contact_number"),
                        rs.getString("linked_emergency_contact_number"),
                        appointmentDate,
                        appointmentTime
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private PatientRecord fetchPatientRecordById(int patientId) {
        String sql = "SELECT patient_id, first_name, middle_name, last_name, contact_number, emergency_contact_number " +
                     "FROM patient WHERE patient_id=? LIMIT 1";
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                return null;
            }
            ensureClinicTables(con);
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, patientId);
                try (ResultSet rs = pst.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    int id = rs.getInt("patient_id");
                    String code = String.format("PT-%04d", id);
                    String emergencyContact = rs.getString("emergency_contact_number");
                    if (emergencyContact == null) {
                        emergencyContact = "";
                    }
                    return new PatientRecord(
                        id,
                        code,
                        rs.getString("first_name"),
                        rs.getString("middle_name"),
                        rs.getString("last_name"),
                        rs.getString("contact_number"),
                        emergencyContact
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ReceptionistRecord fetchReceptionistRecordForEdit(ReceptionistRecord baseRecord) {
        if (baseRecord == null) {
            return null;
        }
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                return null;
            }
            ensureClinicTables(con);

            AccountMeta meta = resolveAccountMeta(con);
            if (!meta.tableExists || !meta.columns.contains("username")) {
                return null;
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
            sql.append(" FROM account WHERE ");
            boolean usePk = baseRecord.accountPk > 0 && meta.idColumn != null;
            sql.append(usePk ? (meta.idColumn + "=?") : "username=?");
            if (meta.columns.contains("role")) {
                sql.append(" AND role='receptionist'");
            } else {
                sql.append(" AND username <> 'admin'");
            }
            sql.append(" LIMIT 1");

            try (PreparedStatement pst = con.prepareStatement(sql.toString())) {
                if (usePk) {
                    pst.setInt(1, baseRecord.accountPk);
                } else {
                    pst.setString(1, baseRecord.username);
                }
                try (ResultSet rs = pst.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
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
                    String createdText = baseRecord.createdText;
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
                    return new ReceptionistRecord(pk, accountCode, displayName, username, status, createdText);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean insertAppointmentToDatabase(
        String patientName,
        String contactNumber,
        String emergencyContactNumber,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        String reason,
        String allergies,
        String notes
    ) {
        String normalizedPatient = patientName == null ? "" : patientName.trim();
        String normalizedContact = normalizePhoneNumber(contactNumber);
        String normalizedEmergencyContact = normalizePhoneNumber(emergencyContactNumber);
        String normalizedReason = reason == null ? "" : reason.trim();
        String normalizedAllergies = allergies == null ? "" : allergies.trim();
        String normalizedNotes = notes == null ? "" : notes.trim();
        if (normalizedPatient.isBlank() || normalizedReason.isBlank() || appointmentDate == null || appointmentTime == null) {
            return false;
        }
        if (normalizedContact.isBlank() || normalizedEmergencyContact.isBlank()) {
            return false;
        }
        if (normalizedAllergies.isBlank()) {
            normalizedAllergies = "N/A";
        }

        String sql = "INSERT INTO appointment (patient_id, dentist_id, appointment_date, appointment_time, cancel_reason, allergies, status, reason, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                return false;
            }
            ensureClinicTables(con);
            boolean originalAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                if (hasOverlappingAppointment(con, appointmentDate, appointmentTime, APPOINTMENT_DURATION_MINUTES, null)) {
                    con.rollback();
                    return false;
                }

                Integer patientId = ensurePatientExistsForAppointment(
                    con,
                    normalizedPatient,
                    normalizedContact,
                    normalizedEmergencyContact
                );
                if (patientId == null) {
                    con.rollback();
                    return false;
                }

                boolean inserted;
                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setInt(1, patientId);
                    pst.setNull(2, Types.INTEGER);
                    pst.setDate(3, Date.valueOf(appointmentDate));
                    pst.setTime(4, Time.valueOf(appointmentTime.withSecond(0).withNano(0)));
                    pst.setString(5, "-");
                    pst.setString(6, normalizedAllergies);
                    pst.setString(7, "pending");
                    pst.setString(8, normalizedReason);
                    pst.setString(9, normalizedNotes);
                    inserted = pst.executeUpdate() > 0;
                }

                if (!inserted) {
                    con.rollback();
                    return false;
                }

                con.commit();
                return true;
            } catch (SQLException e) {
                try {
                    con.rollback();
                } catch (SQLException ignored) {
                    // Keep original exception.
                }
                throw e;
            } finally {
                try {
                    con.setAutoCommit(originalAutoCommit);
                } catch (SQLException ignored) {
                    // No-op.
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Integer ensurePatientExistsForAppointment(
        Connection con,
        String fullName,
        String contactNumber,
        String emergencyContactNumber
    ) throws SQLException {
        String normalizedFullName = safeText(fullName, "").trim();
        String[] nameParts = splitPatientNameParts(normalizedFullName);
        String firstName = safeText(nameParts[0], "").trim();
        String middleName = safeText(nameParts[1], "").trim();
        String lastName = safeText(nameParts[2], "").trim();
        if (firstName.isBlank() && !normalizedFullName.isBlank()) {
            firstName = normalizedFullName;
        }
        if (lastName.isBlank()) {
            lastName = "Unknown";
        }
        String normalizedContact = normalizePhoneNumber(contactNumber);
        String normalizedEmergency = normalizePhoneNumber(emergencyContactNumber);
        boolean allowNameOnlyFallback = normalizedContact.isBlank() && normalizedEmergency.isBlank();

        Integer existingPatientId = findExistingPatientIdForAppointment(
            con,
            firstName,
            middleName,
            lastName,
            normalizedContact,
            normalizedEmergency,
            allowNameOnlyFallback
        );
        if (existingPatientId != null) {
            return existingPatientId;
        }

        if (firstName.isBlank() || lastName.isBlank() || normalizedContact.isBlank() || normalizedEmergency.isBlank()) {
            return null;
        }

        String sql = "INSERT INTO patient (first_name, middle_name, last_name, contact_number, emergency_contact_number) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, firstName);
            if (middleName.isBlank()) {
                pst.setNull(2, Types.VARCHAR);
            } else {
                pst.setString(2, middleName);
            }
            pst.setString(3, lastName);
            pst.setString(4, normalizedContact);
            pst.setString(5, normalizedEmergency);
            int inserted = pst.executeUpdate();
            if (inserted <= 0) {
                return null;
            }
            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return findExistingPatientIdForAppointment(
                con,
                firstName,
                middleName,
                lastName,
                normalizedContact,
                normalizedEmergency,
                allowNameOnlyFallback
            );
        } catch (SQLException e) {
            if (isDuplicateKeyException(e)) {
                return findExistingPatientIdForAppointment(
                    con,
                    firstName,
                    middleName,
                    lastName,
                    normalizedContact,
                    normalizedEmergency,
                    allowNameOnlyFallback
                );
            }
            throw e;
        }
    }

    private Integer findExistingPatientIdForAppointment(
        Connection con,
        String firstName,
        String middleName,
        String lastName,
        String contactNumber,
        String emergencyContactNumber,
        boolean allowNameOnlyFallback
    ) throws SQLException {
        String normalizedFirst = safeText(firstName, "").trim();
        String normalizedMiddle = safeText(middleName, "").trim();
        String normalizedLast = safeText(lastName, "").trim();
        String normalizedContact = normalizePhoneNumber(contactNumber);
        String normalizedEmergency = normalizePhoneNumber(emergencyContactNumber);

        if (!normalizedFirst.isBlank() && !normalizedContact.isBlank() && !normalizedEmergency.isBlank()) {
            String exactSql = "SELECT patient_id FROM patient " +
                "WHERE LOWER(TRIM(first_name)) = LOWER(TRIM(?)) " +
                "AND LOWER(TRIM(COALESCE(middle_name, ''))) = LOWER(TRIM(?)) " +
                "AND LOWER(TRIM(last_name)) = LOWER(TRIM(?)) " +
                "AND contact_number = ? " +
                "AND COALESCE(TRIM(emergency_contact_number), '') = ? " +
                "LIMIT 1";
            try (PreparedStatement pst = con.prepareStatement(exactSql)) {
                pst.setString(1, normalizedFirst);
                pst.setString(2, normalizedMiddle);
                pst.setString(3, normalizedLast);
                pst.setString(4, normalizedContact);
                pst.setString(5, normalizedEmergency);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("patient_id");
                    }
                }
            }
        }

        Integer byContact = findPatientIdByPhone(con, normalizedContact);
        if (byContact != null) {
            return byContact;
        }

        Integer byEmergency = findPatientIdByPhone(con, normalizedEmergency);
        if (byEmergency != null) {
            return byEmergency;
        }

        if (allowNameOnlyFallback && !normalizedFirst.isBlank()) {
            String nameSql = "SELECT patient_id FROM patient " +
                "WHERE LOWER(TRIM(first_name)) = LOWER(TRIM(?)) " +
                "AND LOWER(TRIM(COALESCE(middle_name, ''))) = LOWER(TRIM(?)) " +
                "AND LOWER(TRIM(last_name)) = LOWER(TRIM(?)) " +
                "ORDER BY patient_id DESC LIMIT 1";
            try (PreparedStatement pst = con.prepareStatement(nameSql)) {
                pst.setString(1, normalizedFirst);
                pst.setString(2, normalizedMiddle);
                pst.setString(3, normalizedLast);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("patient_id");
                    }
                }
            }
        }

        return null;
    }

    private Integer findPatientIdByPhone(Connection con, String phone) throws SQLException {
        String normalized = normalizePhoneNumber(phone);
        if (normalized.isBlank()) {
            return null;
        }

        String sql = "SELECT patient_id FROM patient WHERE contact_number=? OR emergency_contact_number=? ORDER BY patient_id DESC LIMIT 1";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, normalized);
            pst.setString(2, normalized);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("patient_id");
                }
            }
        }
        return null;
    }

    private boolean isDuplicateKeyException(SQLException e) {
        if (e == null) {
            return false;
        }
        String sqlState = e.getSQLState();
        if (sqlState != null && sqlState.startsWith("23")) {
            return true;
        }
        return e.getErrorCode() == 1062;
    }

    private boolean updateAppointmentStatusInDatabase(int appointmentId, String newStatus, String cancelReason) {
        String sql = "UPDATE appointment SET status=?, cancel_reason=? WHERE appointment_id=?";
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

    private boolean updateAppointmentDetailsInDatabase(
        int appointmentId,
        int linkedPatientId,
        String firstName,
        String middleName,
        String lastName,
        String contactNumber,
        String emergencyContactNumber,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        String reason,
        String allergies,
        String notes
    ) {
        String normalizedFirstName = safeText(firstName, "").trim();
        String normalizedMiddleName = safeText(middleName, "").trim();
        String normalizedLastName = safeText(lastName, "").trim();
        String normalizedContact = normalizePhoneNumber(contactNumber);
        String normalizedEmergencyContact = normalizePhoneNumber(emergencyContactNumber);
        String normalizedReason = reason == null ? "" : reason.trim();
        String normalizedAllergies = allergies == null ? "" : allergies.trim();
        String normalizedNotes = notes == null ? "" : notes.trim();
        if (normalizedFirstName.isBlank() || normalizedLastName.isBlank() || normalizedReason.isBlank() || appointmentDate == null || appointmentTime == null) {
            return false;
        }
        if (normalizedContact.isBlank() || normalizedEmergencyContact.isBlank()) {
            return false;
        }
        if (normalizedAllergies.isBlank()) {
            normalizedAllergies = "N/A";
        }

        String sql = "UPDATE appointment " +
                     "SET patient_id=?, appointment_date=?, appointment_time=?, reason=?, allergies=?, notes=? " +
                     "WHERE appointment_id=?";
        String updatePatientSql = "UPDATE patient SET first_name=?, middle_name=?, last_name=?, contact_number=?, emergency_contact_number=? WHERE patient_id=?";
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                return false;
            }
            ensureClinicTables(con);
            boolean originalAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                if (hasOverlappingAppointment(con, appointmentDate, appointmentTime, APPOINTMENT_DURATION_MINUTES, appointmentId)) {
                    con.rollback();
                    return false;
                }

                int patientId = linkedPatientId;
                if (patientId <= 0) {
                    Integer resolvedPatientId = findLinkedPatientIdForAppointment(con, appointmentId);
                    patientId = resolvedPatientId == null ? -1 : resolvedPatientId;
                }
                if (patientId <= 0) {
                    con.rollback();
                    return false;
                }

                if (patientContactNumberExists(con, normalizedContact, patientId)
                    || patientContactNumberExists(con, normalizedEmergencyContact, patientId)) {
                    con.rollback();
                    return false;
                }

                boolean patientUpdated;
                try (PreparedStatement patientPst = con.prepareStatement(updatePatientSql)) {
                    patientPst.setString(1, normalizedFirstName);
                    if (normalizedMiddleName.isBlank()) {
                        patientPst.setNull(2, Types.VARCHAR);
                    } else {
                        patientPst.setString(2, normalizedMiddleName);
                    }
                    patientPst.setString(3, normalizedLastName);
                    patientPst.setString(4, normalizedContact);
                    patientPst.setString(5, normalizedEmergencyContact);
                    patientPst.setInt(6, patientId);
                    patientUpdated = patientPst.executeUpdate() > 0;
                }
                if (!patientUpdated) {
                    con.rollback();
                    return false;
                }

                boolean updated;
                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setInt(1, patientId);
                    pst.setDate(2, Date.valueOf(appointmentDate));
                    pst.setTime(3, Time.valueOf(appointmentTime.withSecond(0).withNano(0)));
                    pst.setString(4, normalizedReason);
                    pst.setString(5, normalizedAllergies);
                    pst.setString(6, normalizedNotes);
                    pst.setInt(7, appointmentId);
                    updated = pst.executeUpdate() > 0;
                }

                if (!updated) {
                    con.rollback();
                    return false;
                }

                con.commit();
                return true;
            } catch (SQLException e) {
                try {
                    con.rollback();
                } catch (SQLException ignored) {
                    // Keep original exception.
                }
                throw e;
            } finally {
                try {
                    con.setAutoCommit(originalAutoCommit);
                } catch (SQLException ignored) {
                    // No-op.
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Integer findLinkedPatientIdForAppointment(Connection con, int appointmentId) throws SQLException {
        String sql = "SELECT patient_id FROM appointment WHERE appointment_id=? LIMIT 1";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, appointmentId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int patientId = rs.getInt("patient_id");
                    if (!rs.wasNull() && patientId > 0) {
                        return patientId;
                    }
                }
            }
        }
        return null;
    }

    private boolean confirmAppointmentStatusUpdate() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to update this appointment status?",
            "Confirm Status Update",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }

    private boolean insertPatientToDatabase(
        String firstName,
        String middleName,
        String lastName,
        String contactNumber,
        String emergencyContactNumber
    ) {
        String sql = "INSERT INTO patient (first_name, middle_name, last_name, contact_number, emergency_contact_number) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                return false;
            }
            ensureClinicTables(con);
            if (patientContactNumberExists(con, contactNumber, null) || patientContactNumberExists(con, emergencyContactNumber, null)) {
                return false;
            }
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, safeText(firstName, "").trim());
                if (middleName == null || middleName.trim().isBlank()) {
                    pst.setNull(2, Types.VARCHAR);
                } else {
                    pst.setString(2, middleName.trim());
                }
                pst.setString(3, safeText(lastName, "").trim());
                pst.setString(4, contactNumber);
                pst.setString(5, emergencyContactNumber);
                return pst.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updatePatientInDatabase(
        PatientRecord record,
        String firstName,
        String middleName,
        String lastName,
        String contactNumber,
        String emergencyContactNumber
    ) {
        String sql = "UPDATE patient SET first_name=?, middle_name=?, last_name=?, contact_number=?, emergency_contact_number=? WHERE patient_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con == null ? null : con.prepareStatement(sql)) {
            if (con == null || pst == null) {
                return false;
            }
            ensureClinicTables(con);
            if (patientContactNumberExists(con, contactNumber, record.id)
                || patientContactNumberExists(con, emergencyContactNumber, record.id)) {
                return false;
            }
            pst.setString(1, safeText(firstName, "").trim());
            if (middleName == null || middleName.trim().isBlank()) {
                pst.setNull(2, Types.VARCHAR);
            } else {
                pst.setString(2, middleName.trim());
            }
            pst.setString(3, safeText(lastName, "").trim());
            pst.setString(4, contactNumber);
            pst.setString(5, emergencyContactNumber);
            pst.setInt(6, record.id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean patientContactNumberExists(Connection con, String contactNumber, Integer excludePatientId) throws SQLException {
        String normalized = normalizePhoneNumber(contactNumber);
        if (normalized.isBlank()) {
            return false;
        }

        StringBuilder sql = new StringBuilder(
            "SELECT 1 FROM patient WHERE (contact_number=? OR emergency_contact_number=?)"
        );
        if (excludePatientId != null) {
            sql.append(" AND patient_id<>?");
        }
        sql.append(" LIMIT 1");

        try (PreparedStatement pst = con.prepareStatement(sql.toString())) {
            pst.setString(1, normalized);
            pst.setString(2, normalized);
            if (excludePatientId != null) {
                pst.setInt(3, excludePatientId);
            }
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
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

    private boolean updateReceptionistInDatabase(
        ReceptionistRecord record,
        String fullName,
        String username,
        String newPassword
    ) {
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
            if (newPassword != null && !newPassword.isBlank()) {
                if (!meta.columns.contains("password")) {
                    return false;
                }
                setParts.add("password=?");
                values.add(newPassword);
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
        String idColumn = firstExisting(columns, "account_id", "id", "user_id");
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
        JTextField firstName = new JTextField();
        JTextField middleName = new JTextField();
        JTextField lastName = new JTextField();
        JTextField contact = new JTextField();
        JTextField emergencyContact = new JTextField();
        JDateChooser datePicker = new JDateChooser();
        datePicker.setDateFormatString("MM/dd/yyyy");
        LocalDate allowedStartDate = LocalDate.now();
        LocalDate allowedEndDate = allowedStartDate.withDayOfMonth(allowedStartDate.lengthOfMonth());
        datePicker.setMinSelectableDate(Date.valueOf(allowedStartDate));
        datePicker.setMaxSelectableDate(Date.valueOf(allowedEndDate));
        datePicker.setDate(Date.valueOf(allowedStartDate));

        JComboBox<String> timeSelector = new JComboBox<>(buildAppointmentTimeOptions());
        JTextField reason = new JTextField();
        JTextField allergies = new JTextField();
        JTextField notes = new JTextField();

        styleInputField(firstName);
        styleInputField(middleName);
        styleInputField(lastName);
        styleInputField(contact);
        styleInputField(emergencyContact);
        styleInputField(reason);
        styleInputField(allergies);
        styleInputField(notes);
        applyLettersOnlyInputRestriction(firstName);
        applyLettersOnlyInputRestriction(middleName);
        applyLettersOnlyInputRestriction(lastName);
        applyDigitsOnlyInputRestriction(contact, 11);
        applyDigitsOnlyInputRestriction(emergencyContact, 11);
        applyLettersAndSpacesInputRestriction(reason);
        applyLettersAndSpacesInputRestriction(allergies);
        styleDateChooserField(datePicker);
        applyDefaultFieldBorder(timeSelector);

        timeSelector.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        timeSelector.setBackground(Color.WHITE);
        timeSelector.setPreferredSize(new Dimension(320, 42));
        timeSelector.setSelectedItem("9:00 AM");

        firstName.setPreferredSize(new Dimension(320, 42));
        middleName.setPreferredSize(new Dimension(320, 42));
        lastName.setPreferredSize(new Dimension(320, 42));
        contact.setPreferredSize(new Dimension(320, 42));
        emergencyContact.setPreferredSize(new Dimension(320, 42));
        reason.setPreferredSize(new Dimension(320, 42));
        allergies.setPreferredSize(new Dimension(320, 42));
        notes.setPreferredSize(new Dimension(320, 42));

        JPanel formGrid = new JPanel(new GridLayout(5, 2, 18, 14));
        formGrid.setOpaque(false);
        formGrid.add(landscapeFieldBlock("First Name", firstName));
        formGrid.add(landscapeFieldBlock("Appointment Date", datePicker));
        formGrid.add(landscapeFieldBlock("Middle Name (optional)", middleName));
        formGrid.add(landscapeFieldBlock("Appointment Time", timeSelector));
        formGrid.add(landscapeFieldBlock("Last Name", lastName));
        formGrid.add(landscapeFieldBlock("Reason", reason));
        formGrid.add(landscapeFieldBlock("Contact Number", contact));
        formGrid.add(landscapeFieldBlock("Allergies (optional)", allergies));
        formGrid.add(landscapeFieldBlock("Emergency Contact Number", emergencyContact));
        formGrid.add(landscapeFieldBlock("Notes (optional)", notes));

        JPanel form = new JPanel(new BorderLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(14, 14, 10, 14));
        form.add(formGrid, BorderLayout.CENTER);
        form.setPreferredSize(new Dimension(860, 420));
        form.setMinimumSize(new Dimension(760, 400));

        while (true) {
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

            resetFieldValidationState(firstName, middleName, lastName, contact, emergencyContact, datePicker, timeSelector, reason, allergies, notes);

            String firstNameText = firstName.getText().trim();
            String middleNameText = middleName.getText().trim();
            String lastNameText = lastName.getText().trim();
            String contactText = contact.getText().trim();
            String emergencyContactText = emergencyContact.getText().trim();
            String reasonText = reason.getText().trim();
            String allergiesText = allergies.getText().trim();
            String notesText = notes.getText().trim();

            java.util.Date selectedDateValue = datePicker.getDate();
            if (selectedDateValue == null) {
                highlightInvalidField(datePicker);
                JOptionPane.showMessageDialog(
                    this,
                    "Please select an appointment date.",
                    "Invalid Appointment Date",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }
            LocalDate appointmentDate = new Date(selectedDateValue.getTime()).toLocalDate();
            String dateText = appointmentDate.format(APPOINTMENT_FORM_DATE_FORMAT);
            String timeText = timeSelector.getSelectedItem() == null
                ? ""
                : timeSelector.getSelectedItem().toString().trim();

            String firstNameError = validateRequiredPatientNamePart("First Name", firstNameText);
            if (firstNameError != null) {
                highlightInvalidField(firstName);
                JOptionPane.showMessageDialog(
                    this,
                    firstNameError,
                    "Invalid First Name",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            String middleNameError = validateOptionalPatientNamePart("Middle Name", middleNameText);
            if (middleNameError != null) {
                highlightInvalidField(middleName);
                JOptionPane.showMessageDialog(
                    this,
                    middleNameError,
                    "Invalid Middle Name",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            String lastNameError = validateRequiredPatientNamePart("Last Name", lastNameText);
            if (lastNameError != null) {
                highlightInvalidField(lastName);
                JOptionPane.showMessageDialog(
                    this,
                    lastNameError,
                    "Invalid Last Name",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            String contactError = validatePhoneNumber("Contact Number", contactText);
            if (contactError != null) {
                highlightInvalidField(contact);
                JOptionPane.showMessageDialog(
                    this,
                    contactError,
                    "Invalid Contact Number",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            String emergencyContactError = validatePhoneNumber("Emergency Contact Number", emergencyContactText);
            if (emergencyContactError != null) {
                highlightInvalidField(emergencyContact);
                JOptionPane.showMessageDialog(
                    this,
                    emergencyContactError,
                    "Invalid Emergency Contact Number",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            String normalizedContact = normalizePhoneNumber(contactText);
            String normalizedEmergencyContact = normalizePhoneNumber(emergencyContactText);
            if (!normalizedContact.isBlank() && normalizedContact.equals(normalizedEmergencyContact)) {
                highlightInvalidField(contact);
                highlightInvalidField(emergencyContact);
                JOptionPane.showMessageDialog(
                    this,
                    "Contact Number and Emergency Contact Number must be different.",
                    "Duplicate Contact Numbers",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            String dateValidationError = validateAppointmentDateInput(dateText);
            if (dateValidationError != null) {
                highlightInvalidField(datePicker);
                JOptionPane.showMessageDialog(
                    this,
                    dateValidationError,
                    "Invalid Appointment Date",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            String timeValidationError = validateAppointmentTimeInput(timeText);
            if (timeValidationError != null) {
                highlightInvalidField(timeSelector);
                JOptionPane.showMessageDialog(
                    this,
                    timeValidationError,
                    "Invalid Appointment Time",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            String reasonError = validateLettersAndSpacesField("Reason", reasonText, true);
            if (reasonError != null) {
                highlightInvalidField(reason);
                JOptionPane.showMessageDialog(
                    this,
                    reasonError,
                    "Invalid Reason",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            String allergiesError = validateLettersAndSpacesField("Allergies", allergiesText, false);
            if (allergiesError != null) {
                highlightInvalidField(allergies);
                JOptionPane.showMessageDialog(
                    this,
                    allergiesError,
                    "Invalid Allergies",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            if (allergiesText.isBlank()) {
                allergiesText = "N/A";
            }

            LocalTime appointmentTime;
            try {
                appointmentTime = parseAppointmentFormTime(timeText);
            } catch (Exception ex) {
                highlightInvalidField(timeSelector);
                JOptionPane.showMessageDialog(
                    this,
                    "Invalid appointment time. Use h:mm AM/PM format (example: 9:00 AM).",
                    "Invalid Appointment Time",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            String overlapError = validateAppointmentSlotAvailability(appointmentDate, appointmentTime, null);
            if (overlapError != null) {
                highlightInvalidField(datePicker);
                highlightInvalidField(timeSelector);
                JOptionPane.showMessageDialog(this, overlapError, "Schedule Conflict", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String patientName = buildPatientFullName(firstNameText, middleNameText, lastNameText);
            if (!insertAppointmentToDatabase(
                patientName,
                normalizedContact,
                normalizedEmergencyContact,
                appointmentDate,
                appointmentTime,
                reasonText,
                allergiesText,
                notesText
            )) {
                String postCheckOverlapError = validateAppointmentSlotAvailability(appointmentDate, appointmentTime, null);
                if (TIME_SLOT_BOOKED_MESSAGE.equals(postCheckOverlapError)) {
                    highlightInvalidField(datePicker);
                    highlightInvalidField(timeSelector);
                    JOptionPane.showMessageDialog(this, postCheckOverlapError, "Schedule Conflict", JOptionPane.WARNING_MESSAGE);
                    continue;
                }

                JOptionPane.showMessageDialog(
                    this,
                    "Could not save the appointment. Please check your database connection and try again.",
                    "Save Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                continue;
            }

            refreshAllViews();
            return;
        }
    }

    private void openAddReceptionistDialog() {
        if (!confirmAdminPasswordForReceptionistEdit()) {
            return;
        }

        JTextField firstName = new JTextField();
        JTextField middleName = new JTextField();
        JTextField lastName = new JTextField();
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JPasswordField confirmPassword = new JPasswordField();
        styleInputField(firstName);
        styleInputField(middleName);
        styleInputField(lastName);
        styleInputField(username);
        stylePasswordField(password);
        stylePasswordField(confirmPassword);
        applyLettersOnlyInputRestriction(firstName);
        applyLettersOnlyInputRestriction(middleName);
        applyLettersOnlyInputRestriction(lastName);

        JPanel form = landscapeFormPanel(
            new String[]{
                "First Name",
                "Username",
                "Middle Name (optional)",
                "Password",
                "Last Name",
                "Confirm Password"
            },
            new JComponent[]{
                firstName,
                username,
                middleName,
                password,
                lastName,
                confirmPassword
            },
            2,
            760
        );

        while (true) {
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

            resetFieldValidationState(firstName, middleName, lastName, username, password, confirmPassword);

            String firstNameText = firstName.getText().trim();
            String middleNameText = middleName.getText().trim();
            String lastNameText = lastName.getText().trim();
            String usernameText = username.getText().trim();
            String plainPassword = new String(password.getPassword()).trim();
            String plainConfirmPassword = new String(confirmPassword.getPassword()).trim();

            String firstNameError = validateRequiredPatientNamePart("First Name", firstNameText);
            if (firstNameError != null) {
                highlightInvalidField(firstName);
                JOptionPane.showMessageDialog(this, firstNameError, "Invalid First Name", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String middleNameError = validateOptionalPatientNamePart("Middle Name", middleNameText);
            if (middleNameError != null) {
                highlightInvalidField(middleName);
                JOptionPane.showMessageDialog(this, middleNameError, "Invalid Middle Name", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String lastNameError = validateRequiredPatientNamePart("Last Name", lastNameText);
            if (lastNameError != null) {
                highlightInvalidField(lastName);
                JOptionPane.showMessageDialog(this, lastNameError, "Invalid Last Name", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String usernameError = validateReceptionistUsername(usernameText);
            if (usernameError != null) {
                highlightInvalidField(username);
                JOptionPane.showMessageDialog(this, usernameError, "Invalid Username", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String passwordError = validateReceptionistPassword(plainPassword);
            if (passwordError != null) {
                highlightInvalidField(password);
                JOptionPane.showMessageDialog(this, passwordError, "Invalid Password", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            if (!plainPassword.equals(plainConfirmPassword)) {
                highlightInvalidField(password);
                highlightInvalidField(confirmPassword);
                JOptionPane.showMessageDialog(
                    this,
                    "Password and Confirm Password do not match.",
                    "Password Mismatch",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            String usernameDuplicateError = validateReceptionistUsernameUniqueness(usernameText, null, null);
            if (usernameDuplicateError != null) {
                highlightInvalidField(username);
                JOptionPane.showMessageDialog(this, usernameDuplicateError, "Duplicate Username", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String fullName = buildReceptionistFullName(firstNameText, middleNameText, lastNameText);

            if (!insertReceptionistToDatabase(fullName, usernameText, plainPassword)) {
                String duplicateAfterSaveError = validateReceptionistUsernameUniqueness(usernameText, null, null);
                if (duplicateAfterSaveError != null && duplicateAfterSaveError.startsWith("Username already exists")) {
                    highlightInvalidField(username);
                    JOptionPane.showMessageDialog(this, duplicateAfterSaveError, "Duplicate Username", JOptionPane.WARNING_MESSAGE);
                    continue;
                }
                JOptionPane.showMessageDialog(
                    this,
                    "Could not save receptionist account. Please check your database connection and try again.",
                    "Save Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                continue;
            }

            refreshAllViews();
            return;
        }
    }

    private void openEditAppointmentDialog(AppointmentRecord record) {
        if (record == null) {
            return;
        }
        AppointmentRecord latestRecord = fetchAppointmentRecordById(record.id);
        if (latestRecord == null) {
            JOptionPane.showMessageDialog(
                this,
                "Could not load the selected appointment. Please refresh and try again.",
                "Load Failed",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        record = latestRecord;
        if (record.patientPk <= 0) {
            JOptionPane.showMessageDialog(
                this,
                "This appointment is not linked to a patient record. Please relink the patient before editing.",
                "Missing Patient Link",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String[] nameParts = splitPatientNameParts(safeText(record.patientName, ""));
        JTextField firstName = new JTextField(safeText(nameParts[0], ""));
        JTextField middleName = new JTextField(safeText(nameParts[1], ""));
        JTextField lastName = new JTextField(safeText(nameParts[2], ""));
        JTextField contact = new JTextField(safeText(record.contactNumber, ""));
        JTextField emergencyContact = new JTextField(safeText(record.emergencyContactNumber, ""));
        JDateChooser datePicker = new JDateChooser();
        datePicker.setDateFormatString("MM/dd/yyyy");
        LocalDate allowedStartDate = LocalDate.now();
        LocalDate allowedEndDate = allowedStartDate.withDayOfMonth(allowedStartDate.lengthOfMonth());
        LocalDate seedDate = record.appointmentDate == null ? allowedStartDate : record.appointmentDate;
        if (seedDate.isBefore(allowedStartDate) || seedDate.isAfter(allowedEndDate)) {
            seedDate = allowedStartDate;
        }
        datePicker.setMinSelectableDate(Date.valueOf(allowedStartDate));
        datePicker.setMaxSelectableDate(Date.valueOf(allowedEndDate));
        datePicker.setDate(Date.valueOf(seedDate));

        JComboBox<String> timeSelector = new JComboBox<>(buildAppointmentTimeOptions());
        JTextField reason = new JTextField(safeText(record.reason, ""));
        String existingAllergies = safeText(record.allergies, "");
        if ("N/A".equalsIgnoreCase(existingAllergies.trim())) {
            existingAllergies = "";
        }
        JTextField allergies = new JTextField(existingAllergies);
        JTextField notes = new JTextField(safeText(record.notes, ""));

        styleInputField(firstName);
        styleInputField(middleName);
        styleInputField(lastName);
        styleInputField(contact);
        styleInputField(emergencyContact);
        styleInputField(reason);
        styleInputField(allergies);
        styleInputField(notes);
        applyLettersOnlyInputRestriction(firstName);
        applyLettersOnlyInputRestriction(middleName);
        applyLettersOnlyInputRestriction(lastName);
        applyDigitsOnlyInputRestriction(contact, 11);
        applyDigitsOnlyInputRestriction(emergencyContact, 11);
        applyLettersAndSpacesInputRestriction(reason);
        applyLettersAndSpacesInputRestriction(allergies);
        styleDateChooserField(datePicker);
        applyDefaultFieldBorder(timeSelector);

        timeSelector.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        timeSelector.setBackground(Color.WHITE);
        timeSelector.setPreferredSize(new Dimension(320, 42));
        String selectedTime = "9:00 AM";
        if (record.appointmentTime != null) {
            selectedTime = record.appointmentTime.withSecond(0).withNano(0).format(APPOINTMENT_FORM_TIME_FORMAT);
        } else if (record.timeText != null && !record.timeText.isBlank()) {
            try {
                selectedTime = parseTimeValue(record.timeText).format(APPOINTMENT_FORM_TIME_FORMAT);
            } catch (Exception ignored) {
                selectedTime = "9:00 AM";
            }
        }
        timeSelector.setSelectedItem(selectedTime);

        firstName.setPreferredSize(new Dimension(320, 42));
        middleName.setPreferredSize(new Dimension(320, 42));
        lastName.setPreferredSize(new Dimension(320, 42));
        contact.setPreferredSize(new Dimension(320, 42));
        emergencyContact.setPreferredSize(new Dimension(320, 42));
        reason.setPreferredSize(new Dimension(320, 42));
        allergies.setPreferredSize(new Dimension(320, 42));
        notes.setPreferredSize(new Dimension(320, 42));

        JPanel formGrid = new JPanel(new GridLayout(5, 2, 18, 14));
        formGrid.setOpaque(false);
        formGrid.add(landscapeFieldBlock("First Name", firstName));
        formGrid.add(landscapeFieldBlock("Appointment Date", datePicker));
        formGrid.add(landscapeFieldBlock("Middle Name (optional)", middleName));
        formGrid.add(landscapeFieldBlock("Appointment Time", timeSelector));
        formGrid.add(landscapeFieldBlock("Last Name", lastName));
        formGrid.add(landscapeFieldBlock("Reason", reason));
        formGrid.add(landscapeFieldBlock("Contact Number", contact));
        formGrid.add(landscapeFieldBlock("Allergies (optional)", allergies));
        formGrid.add(landscapeFieldBlock("Emergency Contact Number", emergencyContact));
        formGrid.add(landscapeFieldBlock("Notes (optional)", notes));

        JPanel form = new JPanel(new BorderLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(14, 14, 10, 14));
        form.add(formGrid, BorderLayout.CENTER);
        form.setPreferredSize(new Dimension(860, 420));
        form.setMinimumSize(new Dimension(760, 400));

        while (true) {
            int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Edit Appointment",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            resetFieldValidationState(firstName, middleName, lastName, contact, emergencyContact, datePicker, timeSelector, reason, allergies, notes);

            String firstNameText = firstName.getText().trim();
            String middleNameText = middleName.getText().trim();
            String lastNameText = lastName.getText().trim();
            String contactText = contact.getText().trim();
            String emergencyContactText = emergencyContact.getText().trim();
            String reasonText = reason.getText().trim();
            String allergiesText = allergies.getText().trim();
            String notesText = notes.getText().trim();

            java.util.Date selectedDateValue = datePicker.getDate();
            if (selectedDateValue == null) {
                highlightInvalidField(datePicker);
                JOptionPane.showMessageDialog(
                    this,
                    "Please select an appointment date.",
                    "Invalid Appointment Date",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }
            LocalDate appointmentDate = new Date(selectedDateValue.getTime()).toLocalDate();
            String dateText = appointmentDate.format(APPOINTMENT_FORM_DATE_FORMAT);
            String timeText = timeSelector.getSelectedItem() == null
                ? ""
                : timeSelector.getSelectedItem().toString().trim();

            String firstNameError = validateRequiredPatientNamePart("First Name", firstNameText);
            if (firstNameError != null) {
                highlightInvalidField(firstName);
                JOptionPane.showMessageDialog(this, firstNameError, "Invalid First Name", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String middleNameError = validateOptionalPatientNamePart("Middle Name", middleNameText);
            if (middleNameError != null) {
                highlightInvalidField(middleName);
                JOptionPane.showMessageDialog(this, middleNameError, "Invalid Middle Name", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String lastNameError = validateRequiredPatientNamePart("Last Name", lastNameText);
            if (lastNameError != null) {
                highlightInvalidField(lastName);
                JOptionPane.showMessageDialog(this, lastNameError, "Invalid Last Name", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String contactError = validatePhoneNumber("Contact Number", contactText);
            if (contactError != null) {
                highlightInvalidField(contact);
                JOptionPane.showMessageDialog(this, contactError, "Invalid Contact Number", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String emergencyContactError = validatePhoneNumber("Emergency Contact Number", emergencyContactText);
            if (emergencyContactError != null) {
                highlightInvalidField(emergencyContact);
                JOptionPane.showMessageDialog(this, emergencyContactError, "Invalid Emergency Contact Number", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String normalizedContact = normalizePhoneNumber(contactText);
            String normalizedEmergencyContact = normalizePhoneNumber(emergencyContactText);
            if (!normalizedContact.isBlank() && normalizedContact.equals(normalizedEmergencyContact)) {
                highlightInvalidField(contact);
                highlightInvalidField(emergencyContact);
                JOptionPane.showMessageDialog(
                    this,
                    "Contact Number and Emergency Contact Number must be different.",
                    "Duplicate Contact Numbers",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            String duplicateContactError = validatePatientContactUniqueness(
                normalizedContact,
                normalizedEmergencyContact,
                record.patientPk > 0 ? record.patientPk : null
            );
            if (duplicateContactError != null) {
                highlightInvalidField(contact);
                highlightInvalidField(emergencyContact);
                JOptionPane.showMessageDialog(this, duplicateContactError, "Duplicate Contact Number", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String dateValidationError = validateAppointmentDateInput(dateText);
            if (dateValidationError != null) {
                highlightInvalidField(datePicker);
                JOptionPane.showMessageDialog(
                    this,
                    dateValidationError,
                    "Invalid Appointment Date",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            String timeValidationError = validateAppointmentTimeInput(timeText);
            if (timeValidationError != null) {
                highlightInvalidField(timeSelector);
                JOptionPane.showMessageDialog(
                    this,
                    timeValidationError,
                    "Invalid Appointment Time",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            String reasonError = validateLettersAndSpacesField("Reason", reasonText, true);
            if (reasonError != null) {
                highlightInvalidField(reason);
                JOptionPane.showMessageDialog(this, reasonError, "Invalid Reason", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String allergiesError = validateLettersAndSpacesField("Allergies", allergiesText, false);
            if (allergiesError != null) {
                highlightInvalidField(allergies);
                JOptionPane.showMessageDialog(this, allergiesError, "Invalid Allergies", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            if (allergiesText.isBlank()) {
                allergiesText = "N/A";
            }

            LocalTime appointmentTime;
            try {
                appointmentTime = parseAppointmentFormTime(timeText);
            } catch (Exception ex) {
                highlightInvalidField(timeSelector);
                JOptionPane.showMessageDialog(
                    this,
                    "Invalid appointment time. Use h:mm AM/PM format (example: 9:00 AM).",
                    "Invalid Appointment Time",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            String overlapError = validateAppointmentSlotAvailability(appointmentDate, appointmentTime, record.id);
            if (overlapError != null) {
                highlightInvalidField(datePicker);
                highlightInvalidField(timeSelector);
                JOptionPane.showMessageDialog(this, overlapError, "Schedule Conflict", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            if (!updateAppointmentDetailsInDatabase(
                record.id,
                record.patientPk,
                firstNameText,
                middleNameText,
                lastNameText,
                normalizedContact,
                normalizedEmergencyContact,
                appointmentDate,
                appointmentTime,
                reasonText,
                allergiesText,
                notesText
            )) {
                String postCheckOverlapError = validateAppointmentSlotAvailability(appointmentDate, appointmentTime, record.id);
                if (TIME_SLOT_BOOKED_MESSAGE.equals(postCheckOverlapError)) {
                    highlightInvalidField(datePicker);
                    highlightInvalidField(timeSelector);
                    JOptionPane.showMessageDialog(this, postCheckOverlapError, "Schedule Conflict", JOptionPane.WARNING_MESSAGE);
                    continue;
                }

                JOptionPane.showMessageDialog(
                    this,
                    "Could not update the appointment. Please check your database connection and try again.",
                    "Update Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                continue;
            }

            refreshAllViews();
            JOptionPane.showMessageDialog(
                this,
                "Appointment and patient information updated successfully.",
                "Update Successful",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
    }

    private void openAddPatientDialog() {
        JTextField firstName = new JTextField();
        JTextField middleName = new JTextField();
        JTextField lastName = new JTextField();
        JTextField contact = new JTextField();
        JTextField emergencyContact = new JTextField();
        styleInputField(firstName);
        styleInputField(middleName);
        styleInputField(lastName);
        styleInputField(contact);
        styleInputField(emergencyContact);
        applyLettersOnlyInputRestriction(firstName);
        applyLettersOnlyInputRestriction(middleName);
        applyLettersOnlyInputRestriction(lastName);
        applyDigitsOnlyInputRestriction(contact, 11);
        applyDigitsOnlyInputRestriction(emergencyContact, 11);

        JPanel form = landscapeFormPanel(
            new String[]{
                "First Name",
                "Middle Name (optional)",
                "Last Name",
                "Contact Number",
                "Emergency Contact Number"
            },
            new JComponent[]{
                firstName,
                middleName,
                lastName,
                contact,
                emergencyContact
            },
            2,
            760
        );

        while (true) {
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

            resetFieldValidationState(firstName, middleName, lastName, contact, emergencyContact);

            String firstError = validateRequiredPatientNamePart("First Name", firstName.getText());
            if (firstError != null) {
                highlightInvalidField(firstName);
                JOptionPane.showMessageDialog(this, firstError, "Invalid First Name", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String middleError = validateOptionalPatientNamePart("Middle Name", middleName.getText());
            if (middleError != null) {
                highlightInvalidField(middleName);
                JOptionPane.showMessageDialog(this, middleError, "Invalid Middle Name", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String lastError = validateRequiredPatientNamePart("Last Name", lastName.getText());
            if (lastError != null) {
                highlightInvalidField(lastName);
                JOptionPane.showMessageDialog(this, lastError, "Invalid Last Name", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String contactError = validatePhoneNumber("Contact Number", contact.getText());
            if (contactError != null) {
                highlightInvalidField(contact);
                JOptionPane.showMessageDialog(this, contactError, "Invalid Contact Number", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String emergencyContactError = validatePhoneNumber("Emergency Contact Number", emergencyContact.getText());
            if (emergencyContactError != null) {
                highlightInvalidField(emergencyContact);
                JOptionPane.showMessageDialog(this, emergencyContactError, "Invalid Emergency Contact Number", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String normalizedContact = normalizePhoneNumber(contact.getText());
            String normalizedEmergency = normalizePhoneNumber(emergencyContact.getText());
            if (normalizedContact.equals(normalizedEmergency)) {
                highlightInvalidField(contact);
                highlightInvalidField(emergencyContact);
                JOptionPane.showMessageDialog(
                    this,
                    "Contact Number and Emergency Contact Number must be different.",
                    "Duplicate Contact Number",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            String duplicateError = validatePatientContactUniqueness(normalizedContact, normalizedEmergency, null);
            if (duplicateError != null) {
                highlightInvalidField(contact);
                highlightInvalidField(emergencyContact);
                JOptionPane.showMessageDialog(this, duplicateError, "Duplicate Contact Number", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            if (!insertPatientToDatabase(
                firstName.getText(),
                middleName.getText(),
                lastName.getText(),
                normalizedContact,
                normalizedEmergency
            )) {
                String postInsertDuplicateError = validatePatientContactUniqueness(normalizedContact, normalizedEmergency, null);
                if (postInsertDuplicateError != null
                    && (postInsertDuplicateError.startsWith("Contact Number already exists")
                        || postInsertDuplicateError.startsWith("Emergency Contact Number already exists"))) {
                    highlightInvalidField(contact);
                    highlightInvalidField(emergencyContact);
                    JOptionPane.showMessageDialog(this, postInsertDuplicateError, "Duplicate Contact Number", JOptionPane.WARNING_MESSAGE);
                    continue;
                }
                JOptionPane.showMessageDialog(
                    this,
                    "Could not save patient record. Please check your database connection and try again.",
                    "Save Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                continue;
            }

            refreshAllViews();
            return;
        }
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
        int receptionistAccounts = receptionistRecords.size();

        if (dashboardTodayValue != null) {
            dashboardTodayValue.setText(String.valueOf(todayTotal));
        }
        if (dashboardPendingValue != null) {
            dashboardPendingValue.setText(String.valueOf(pending));
        }
        if (dashboardCompletedValue != null) {
            dashboardCompletedValue.setText(String.valueOf(completed));
        }
        if (dashboardReceptionistsValue != null) {
            dashboardReceptionistsValue.setText(String.valueOf(receptionistAccounts));
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
            updateDashboardTodayRowsHeight(1, true);
        } else {
            for (AppointmentRecord record : todayRecords) {
                if (dashboardTodayRows.getComponentCount() > 0) {
                    dashboardTodayRows.add(Box.createVerticalStrut(DASHBOARD_TODAY_ROW_GAP));
                }
                dashboardTodayRows.add(dashboardTodayRow(record));
            }
            updateDashboardTodayRowsHeight(todayRecords.size(), false);
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
            updateRowsPanelHeightFromChildren(patientRows);
        } else {
            int i = 0;
            for (PatientRecord record : filtered) {
                if (i > 0) {
                    patientRows.add(Box.createVerticalStrut(TABLE_ROW_GAP));
                }
                patientRows.add(patientRow(record));
                i++;
            }
            updateRowsPanelHeightFromChildren(patientRows);
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
            }
            if (isSameCalendarDate(record, appointmentCalendarDate)) {
                calendarRecords.add(record);
            }
        }
        calendarRecords.sort((a, b) -> {
            if (a.appointmentTime == null && b.appointmentTime == null) {
                return safeText(a.timeText, "").compareToIgnoreCase(safeText(b.timeText, ""));
            }
            if (a.appointmentTime == null) {
                return 1;
            }
            if (b.appointmentTime == null) {
                return -1;
            }
            return a.appointmentTime.compareTo(b.appointmentTime);
        });

        if (pendingRecords.isEmpty()) {
            appointmentTableRows.add(emptyStatePanel("No appointments yet.", "No pending records to show."));
            updateRowsPanelHeightFromChildren(appointmentTableRows);
        } else {
            int i = 0;
            for (AppointmentRecord record : pendingRecords) {
                if (i > 0) {
                    appointmentTableRows.add(Box.createVerticalStrut(TABLE_ROW_GAP));
                }
                appointmentTableRows.add(appointmentTableRow(record));
                i++;
            }
            updateRowsPanelHeightFromChildren(appointmentTableRows);
        }

        if (calendarRecords.isEmpty()) {
            appointmentCalendarRows.add(calendarDateEmptyStatePanel());
            updateRowsPanelHeightFromChildren(appointmentCalendarRows);
        } else {
            for (AppointmentRecord record : calendarRecords) {
                if (appointmentCalendarRows.getComponentCount() > 0) {
                    appointmentCalendarRows.add(Box.createVerticalStrut(TABLE_ROW_GAP));
                }
                appointmentCalendarRows.add(appointmentTableRow(record));
            }
            updateRowsPanelHeightFromChildren(appointmentCalendarRows);
        }

        appointmentTableRows.revalidate();
        appointmentTableRows.repaint();
        appointmentCalendarRows.revalidate();
        appointmentCalendarRows.repaint();
    }

    private JPanel appointmentTableRow(AppointmentRecord record) {
        RoundedPanel row = new RoundedPanel(14, new Color(247, 250, 255));
        row.setLayout(new GridBagLayout());
        row.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 229, 242), 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
            )
        );
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMinimumSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, TABLE_ROW_HEIGHT));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        String formattedDate = record.appointmentDate == null
            ? safeText(record.dateText, "-")
            : record.appointmentDate.format(APPOINTMENT_TABLE_DATE_FORMAT);
        JLabel dateCell = rowLabelLimited(formattedDate, 12);
        dateCell.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        addAppointmentTableGridCell(row, dateCell, 0, GridBagConstraints.NORTHWEST);

        JLabel timeCell = rowLabel(safeText(record.timeText, "--:--"));
        timeCell.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timeCell.setForeground(new Color(63, 101, 228));
        addAppointmentTableGridCell(row, timeCell, 1, GridBagConstraints.NORTHWEST);

        JLabel patientCell = rowLabelLimited(safeText(record.patientName, "(No patient)"), 26);
        patientCell.setFont(new Font("Segoe UI", Font.BOLD, 15));
        addAppointmentTableGridCell(row, patientCell, 2, GridBagConstraints.NORTHWEST);

        JPanel reasonCell = appointmentWrappedTextCell(safeText(record.reason, "-"), appointmentColumnWidth(3) - 10);
        addAppointmentTableGridCell(row, reasonCell, 3, GridBagConstraints.NORTHWEST);

        JPanel allergiesCell = appointmentWrappedTextCell(safeText(record.allergies, "N/A"), appointmentColumnWidth(4) - 10);
        addAppointmentTableGridCell(row, allergiesCell, 4, GridBagConstraints.NORTHWEST);

        JPanel notesCell = appointmentWrappedTextCell(safeText(record.notes, "N/A"), appointmentColumnWidth(5) - 10);
        addAppointmentTableGridCell(row, notesCell, 5, GridBagConstraints.NORTHWEST);

        JPanel statusCell = statusBadge(record.status);
        addAppointmentTableGridCell(row, statusCell, 6, GridBagConstraints.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));

        JButton editButton = softActionButton("Edit", new Color(236, 241, 251), new Color(40, 53, 79));
        editButton.setPreferredSize(new Dimension(96, 30));
        editButton.setMinimumSize(new Dimension(96, 30));
        editButton.setMaximumSize(new Dimension(96, 30));
        editButton.addActionListener(e -> openEditAppointmentDialog(record));

        JButton confirmButton = softActionButton("Confirm", new Color(231, 246, 236), new Color(46, 174, 102));
        confirmButton.setPreferredSize(new Dimension(96, 30));
        confirmButton.setMinimumSize(new Dimension(96, 30));
        confirmButton.setMaximumSize(new Dimension(96, 30));
        confirmButton.addActionListener(e -> {
            if (!confirmAppointmentStatusUpdate()) {
                return;
            }
            if (!updateAppointmentStatusInDatabase(record.id, "completed", "-")) {
                JOptionPane.showMessageDialog(
                    this,
                    "Could not mark this appointment as Completed. Please try again.",
                    "Update Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            refreshAllViews();
        });

        JButton cancelButton = softActionButton("Cancel", new Color(252, 236, 236), new Color(224, 93, 93));
        cancelButton.setPreferredSize(new Dimension(96, 30));
        cancelButton.setMinimumSize(new Dimension(96, 30));
        cancelButton.setMaximumSize(new Dimension(96, 30));
        cancelButton.addActionListener(e -> {
            if (!confirmAppointmentStatusUpdate()) {
                return;
            }
            String reason = JOptionPane.showInputDialog(this, "Cancel reason (optional):", "Cancelled by admin");
            if (reason == null) {
                return;
            }
            if (!updateAppointmentStatusInDatabase(
                record.id,
                "cancelled",
                reason.isBlank() ? "Cancelled by admin" : reason.trim()
            )) {
                JOptionPane.showMessageDialog(
                    this,
                    "Could not mark this appointment as Cancelled. Please try again.",
                    "Update Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            refreshAllViews();
        });

        actions.setPreferredSize(new Dimension(100, 112));
        actions.setMinimumSize(new Dimension(100, 112));
        actions.add(editButton);
        actions.add(Box.createVerticalStrut(8));
        actions.add(confirmButton);
        actions.add(Box.createVerticalStrut(8));
        actions.add(cancelButton);
        addAppointmentTableGridCell(row, actions, 7, GridBagConstraints.NORTHEAST);

        int wrappedHeight = Math.max(reasonCell.getPreferredSize().height, allergiesCell.getPreferredSize().height);
        wrappedHeight = Math.max(wrappedHeight, notesCell.getPreferredSize().height);
        wrappedHeight = Math.max(wrappedHeight, actions.getPreferredSize().height);
        wrappedHeight = Math.max(wrappedHeight, statusCell.getPreferredSize().height);
        int dynamicHeight = Math.max(TABLE_ROW_HEIGHT, wrappedHeight + 28);
        row.setMinimumSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, dynamicHeight));
        row.setPreferredSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, dynamicHeight));
        row.setMaximumSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, dynamicHeight));
        return row;
    }

    private JPanel dashboardTodayRow(AppointmentRecord record) {
        RoundedPanel row = new RoundedPanel(14, new Color(247, 250, 255));
        row.setLayout(new BorderLayout(12, 0));
        row.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        row.setMinimumSize(new Dimension(10, DASHBOARD_TODAY_ROW_HEIGHT));
        row.setPreferredSize(new Dimension(1000, DASHBOARD_TODAY_ROW_HEIGHT));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, DASHBOARD_TODAY_ROW_HEIGHT));
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
            if (!confirmAppointmentStatusUpdate()) {
                return;
            }
            if (!updateAppointmentStatusInDatabase(record.id, "completed", "-")) {
                JOptionPane.showMessageDialog(
                    this,
                    "Could not mark this appointment as Completed. Please try again.",
                    "Update Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            refreshAllViews();
        });

        JButton cancelled = actionButton("✕", new Color(231, 86, 86));
        cancelled.addActionListener(e -> {
            if (!confirmAppointmentStatusUpdate()) {
                return;
            }
            String reasonText = JOptionPane.showInputDialog(this, "Cancel reason (optional):", "Cancelled by admin");
            if (reasonText == null) {
                return;
            }
            if (!updateAppointmentStatusInDatabase(
                record.id,
                "cancelled",
                reasonText.isBlank() ? "Cancelled by admin" : reasonText.trim()
            )) {
                JOptionPane.showMessageDialog(
                    this,
                    "Could not mark this appointment as Cancelled. Please try again.",
                    "Update Failed",
                    JOptionPane.ERROR_MESSAGE
                );
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
        RoundedPanel row = new RoundedPanel(14, new Color(247, 250, 255));
        row.setLayout(new GridBagLayout());
        row.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 229, 242), 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
            )
        );
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMinimumSize(new Dimension(PATIENT_TABLE_TOTAL_WIDTH, TABLE_ROW_HEIGHT));
        row.setMaximumSize(new Dimension(PATIENT_TABLE_TOTAL_WIDTH, Integer.MAX_VALUE));

        JPanel patientIdCell = chipLabel(truncateText(safeText(record.patientId, "—"), 16));
        addPatientTableGridCell(row, patientIdCell, 0, GridBagConstraints.WEST);

        JPanel firstNameCell = appointmentWrappedTextCell(safeText(record.firstName, "-"), patientColumnWidth(1) - 10);
        addPatientTableGridCell(row, firstNameCell, 1, GridBagConstraints.NORTHWEST);

        JPanel middleNameCell = appointmentWrappedTextCell(safeText(record.middleName, "-"), patientColumnWidth(2) - 10);
        addPatientTableGridCell(row, middleNameCell, 2, GridBagConstraints.NORTHWEST);

        JPanel lastNameCell = appointmentWrappedTextCell(safeText(record.lastName, "-"), patientColumnWidth(3) - 10);
        addPatientTableGridCell(row, lastNameCell, 3, GridBagConstraints.NORTHWEST);

        JPanel contactCell = appointmentWrappedTextCell(safeText(record.contactNumber, "-"), patientColumnWidth(4) - 10);
        addPatientTableGridCell(row, contactCell, 4, GridBagConstraints.NORTHWEST);

        JPanel emergencyContactCell = appointmentWrappedTextCell(
            safeText(record.emergencyContactNumber, "-"),
            patientColumnWidth(5) - 10
        );
        addPatientTableGridCell(row, emergencyContactCell, 5, GridBagConstraints.NORTHWEST);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));

        JButton edit = softActionButton("Edit", new Color(236, 241, 251), new Color(40, 53, 79));
        edit.setPreferredSize(new Dimension(88, 32));
        edit.setMinimumSize(new Dimension(88, 32));
        edit.setMaximumSize(new Dimension(88, 32));
        edit.setAlignmentX(Component.CENTER_ALIGNMENT);
        edit.addActionListener(e -> {
            PatientRecord latestRecord = fetchPatientRecordById(record.id);
            if (latestRecord == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Could not load the selected patient. Please refresh and try again.",
                    "Load Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            JTextField firstNameField = new JTextField(safeText(latestRecord.firstName, ""));
            JTextField middleNameField = new JTextField(safeText(latestRecord.middleName, ""));
            JTextField lastNameField = new JTextField(safeText(latestRecord.lastName, ""));
            JTextField contactField = new JTextField(latestRecord.contactNumber);
            JTextField emergencyContactField = new JTextField(latestRecord.emergencyContactNumber);
            styleInputField(firstNameField);
            styleInputField(middleNameField);
            styleInputField(lastNameField);
            styleInputField(contactField);
            styleInputField(emergencyContactField);
            applyLettersOnlyInputRestriction(firstNameField);
            applyLettersOnlyInputRestriction(middleNameField);
            applyLettersOnlyInputRestriction(lastNameField);
            applyDigitsOnlyInputRestriction(contactField, 11);
            applyDigitsOnlyInputRestriction(emergencyContactField, 11);
            JPanel form = landscapeFormPanel(
                new String[]{
                    "First Name",
                    "Middle Name (optional)",
                    "Last Name",
                    "Contact Number",
                    "Emergency Contact Number"
                },
                new JComponent[]{
                    firstNameField,
                    middleNameField,
                    lastNameField,
                    contactField,
                    emergencyContactField
                },
                2,
                760
            );
            while (true) {
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

                resetFieldValidationState(firstNameField, middleNameField, lastNameField, contactField, emergencyContactField);

                String firstError = validateRequiredPatientNamePart("First Name", firstNameField.getText());
                if (firstError != null) {
                    highlightInvalidField(firstNameField);
                    JOptionPane.showMessageDialog(this, firstError, "Invalid First Name", JOptionPane.WARNING_MESSAGE);
                    continue;
                }

                String middleError = validateOptionalPatientNamePart("Middle Name", middleNameField.getText());
                if (middleError != null) {
                    highlightInvalidField(middleNameField);
                    JOptionPane.showMessageDialog(this, middleError, "Invalid Middle Name", JOptionPane.WARNING_MESSAGE);
                    continue;
                }

                String lastError = validateRequiredPatientNamePart("Last Name", lastNameField.getText());
                if (lastError != null) {
                    highlightInvalidField(lastNameField);
                    JOptionPane.showMessageDialog(this, lastError, "Invalid Last Name", JOptionPane.WARNING_MESSAGE);
                    continue;
                }

                String contactError = validatePhoneNumber("Contact Number", contactField.getText());
                if (contactError != null) {
                    highlightInvalidField(contactField);
                    JOptionPane.showMessageDialog(this, contactError, "Invalid Contact Number", JOptionPane.WARNING_MESSAGE);
                    continue;
                }

                String emergencyContactError = validatePhoneNumber("Emergency Contact Number", emergencyContactField.getText());
                if (emergencyContactError != null) {
                    highlightInvalidField(emergencyContactField);
                    JOptionPane.showMessageDialog(this, emergencyContactError, "Invalid Emergency Contact Number", JOptionPane.WARNING_MESSAGE);
                    continue;
                }

                String normalizedContact = normalizePhoneNumber(contactField.getText());
                String normalizedEmergency = normalizePhoneNumber(emergencyContactField.getText());
                if (normalizedContact.equals(normalizedEmergency)) {
                    highlightInvalidField(contactField);
                    highlightInvalidField(emergencyContactField);
                    JOptionPane.showMessageDialog(
                        this,
                        "Contact Number and Emergency Contact Number must be different.",
                        "Duplicate Contact Number",
                        JOptionPane.WARNING_MESSAGE
                    );
                    continue;
                }

                String duplicateError = validatePatientContactUniqueness(
                    normalizedContact,
                    normalizedEmergency,
                    latestRecord.id
                );
                if (duplicateError != null) {
                    highlightInvalidField(contactField);
                    highlightInvalidField(emergencyContactField);
                    JOptionPane.showMessageDialog(this, duplicateError, "Duplicate Contact Number", JOptionPane.WARNING_MESSAGE);
                    continue;
                }

                if (!updatePatientInDatabase(
                    latestRecord,
                    firstNameField.getText(),
                    middleNameField.getText(),
                    lastNameField.getText(),
                    normalizedContact,
                    normalizedEmergency
                )) {
                    String postUpdateDuplicateError = validatePatientContactUniqueness(
                        normalizedContact,
                        normalizedEmergency,
                        latestRecord.id
                    );
                    if (postUpdateDuplicateError != null
                        && (postUpdateDuplicateError.startsWith("Contact Number already exists")
                            || postUpdateDuplicateError.startsWith("Emergency Contact Number already exists"))) {
                        highlightInvalidField(contactField);
                        highlightInvalidField(emergencyContactField);
                        JOptionPane.showMessageDialog(this, postUpdateDuplicateError, "Duplicate Contact Number", JOptionPane.WARNING_MESSAGE);
                        continue;
                    }
                    JOptionPane.showMessageDialog(
                        this,
                        "Could not update patient record. Please check your database connection and try again.",
                        "Update Failed",
                        JOptionPane.ERROR_MESSAGE
                    );
                    continue;
                }
                refreshAllViews();
                JOptionPane.showMessageDialog(
                    this,
                    "Record updated successfully.",
                    "Update Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
        });

        actions.setPreferredSize(new Dimension(96, 36));
        actions.setMinimumSize(new Dimension(96, 36));
        actions.setMaximumSize(new Dimension(96, 36));
        actions.add(edit);
        addPatientTableGridCell(row, actions, 6, GridBagConstraints.CENTER);

        int wrappedHeight = Math.max(firstNameCell.getPreferredSize().height, middleNameCell.getPreferredSize().height);
        wrappedHeight = Math.max(wrappedHeight, lastNameCell.getPreferredSize().height);
        wrappedHeight = Math.max(wrappedHeight, contactCell.getPreferredSize().height);
        wrappedHeight = Math.max(wrappedHeight, emergencyContactCell.getPreferredSize().height);
        wrappedHeight = Math.max(wrappedHeight, actions.getPreferredSize().height);
        int dynamicHeight = Math.max(TABLE_ROW_HEIGHT, wrappedHeight + 28);
        row.setMinimumSize(new Dimension(PATIENT_TABLE_TOTAL_WIDTH, dynamicHeight));
        row.setPreferredSize(new Dimension(PATIENT_TABLE_TOTAL_WIDTH, dynamicHeight));
        row.setMaximumSize(new Dimension(PATIENT_TABLE_TOTAL_WIDTH, dynamicHeight));
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
            logRows.add(logEmptyStatePanel());
            updateRowsPanelHeightFromChildren(logRows);
        } else {
            int i = 0;
            for (AppointmentRecord record : logged) {
                if (i > 0) {
                    logRows.add(Box.createVerticalStrut(TABLE_ROW_GAP));
                }
                logRows.add(logRow(record));
                i++;
            }
            updateRowsPanelHeightFromChildren(logRows);
        }

        logRows.revalidate();
        logRows.repaint();
    }

    private JPanel logRow(AppointmentRecord record) {
        RoundedPanel row = new RoundedPanel(14, new Color(247, 250, 255));
        row.setLayout(new GridBagLayout());
        row.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 229, 242), 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
            )
        );
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMinimumSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, TABLE_ROW_HEIGHT));
        row.setMaximumSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, Integer.MAX_VALUE));

        String formattedDate = record.appointmentDate == null
            ? safeText(record.dateText, "-")
            : record.appointmentDate.format(APPOINTMENT_TABLE_DATE_FORMAT);
        JLabel dateCell = rowLabelLimited(formattedDate, 12);
        dateCell.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        addAppointmentTableGridCell(row, dateCell, 0, GridBagConstraints.NORTHWEST);

        JLabel timeCell = rowLabel(safeText(record.timeText, "--:--"));
        timeCell.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timeCell.setForeground(new Color(63, 101, 228));
        addAppointmentTableGridCell(row, timeCell, 1, GridBagConstraints.NORTHWEST);

        JLabel patientCell = rowLabelLimited(safeText(record.patientName, "(No patient)"), 26);
        patientCell.setFont(new Font("Segoe UI", Font.BOLD, 15));
        addAppointmentTableGridCell(row, patientCell, 2, GridBagConstraints.NORTHWEST);

        String reasonText = safeText(record.reason, "-");
        if ("cancelled".equals(record.status)) {
            String cancel = safeText(record.cancelReason, "");
            if (!cancel.isBlank() && !"-".equals(cancel)) {
                reasonText = reasonText + "  |  Cancel: " + cancel;
            }
        }
        JPanel reasonCell = appointmentWrappedTextCell(reasonText, appointmentColumnWidth(3) - 10);
        addAppointmentTableGridCell(row, reasonCell, 3, GridBagConstraints.NORTHWEST);

        JPanel allergiesCell = appointmentWrappedTextCell(safeText(record.allergies, "N/A"), appointmentColumnWidth(4) - 10);
        addAppointmentTableGridCell(row, allergiesCell, 4, GridBagConstraints.NORTHWEST);

        JPanel notesCell = appointmentWrappedTextCell(safeText(record.notes, "N/A"), appointmentColumnWidth(5) - 10);
        addAppointmentTableGridCell(row, notesCell, 5, GridBagConstraints.NORTHWEST);

        JPanel statusCell = statusBadge(record.status);
        addAppointmentTableGridCell(row, statusCell, 6, GridBagConstraints.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));

        if ("cancelled".equals(record.status)) {
            JButton restore = softActionButton("Restore", new Color(234, 240, 252), new Color(44, 58, 86));
            restore.setPreferredSize(new Dimension(96, 32));
            restore.setMinimumSize(new Dimension(96, 32));
            restore.setMaximumSize(new Dimension(96, 32));
            restore.setAlignmentX(Component.CENTER_ALIGNMENT);
            restore.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to restore this appointment?",
                    "Confirm Restore",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
                if (!updateAppointmentStatusInDatabase(record.id, "pending", "-")) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Could not restore this cancelled appointment right now. Please try again.",
                        "Restore Failed",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                refreshAllViews();
            });
            actions.add(restore);
        } else {
            JLabel noAction = new JLabel("-");
            noAction.setFont(new Font("Segoe UI", Font.BOLD, 16));
            noAction.setForeground(new Color(132, 145, 171));
            noAction.setAlignmentX(Component.CENTER_ALIGNMENT);
            actions.add(noAction);
        }

        actions.setPreferredSize(new Dimension(100, 36));
        actions.setMinimumSize(new Dimension(100, 36));
        actions.setMaximumSize(new Dimension(100, 36));
        addAppointmentTableGridCell(row, actions, 7, GridBagConstraints.CENTER);

        int wrappedHeight = Math.max(reasonCell.getPreferredSize().height, allergiesCell.getPreferredSize().height);
        wrappedHeight = Math.max(wrappedHeight, notesCell.getPreferredSize().height);
        wrappedHeight = Math.max(wrappedHeight, actions.getPreferredSize().height);
        wrappedHeight = Math.max(wrappedHeight, statusCell.getPreferredSize().height);
        int dynamicHeight = Math.max(TABLE_ROW_HEIGHT, wrappedHeight + 28);
        row.setMinimumSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, dynamicHeight));
        row.setPreferredSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, dynamicHeight));
        row.setMaximumSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, dynamicHeight));
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
                receptionistRows.add(receptionistEmptyStatePanel("No active receptionists yet."));
            } else {
                receptionistRows.add(receptionistEmptyStatePanel("No deactivated receptionists yet."));
            }
            updateRowsPanelHeightFromChildren(receptionistRows);
        } else {
            int i = 0;
            for (ReceptionistRecord record : filtered) {
                if (i > 0) {
                    receptionistRows.add(Box.createVerticalStrut(TABLE_ROW_GAP));
                }
                receptionistRows.add(receptionistRow(record));
                i++;
            }
            updateRowsPanelHeightFromChildren(receptionistRows);
        }

        receptionistRows.revalidate();
        receptionistRows.repaint();
    }

    private JPanel receptionistEmptyStatePanel(String messageText) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(26, 0, 26, 0));

        JLabel message = new JLabel(messageText);
        message.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        message.setForeground(new Color(88, 103, 131));
        message.setHorizontalAlignment(SwingConstants.CENTER);
        wrapper.add(message, new GridBagConstraints());
        return wrapper;
    }

    private JPanel receptionistRow(ReceptionistRecord record) {
        RoundedPanel row = new RoundedPanel(14, new Color(247, 250, 255));
        row.setLayout(new GridBagLayout());
        row.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 229, 242), 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
            )
        );
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMinimumSize(new Dimension(RECEPTIONIST_TABLE_TOTAL_WIDTH, TABLE_ROW_HEIGHT));
        row.setMaximumSize(new Dimension(RECEPTIONIST_TABLE_TOTAL_WIDTH, Integer.MAX_VALUE));

        JPanel accountIdCell = chipLabel(truncateText(safeText(record.accountId, "—"), 16));
        addReceptionistTableGridCell(row, accountIdCell, 0, GridBagConstraints.CENTER);

        JPanel nameCell = appointmentWrappedTextCell(
            safeText(record.name, "(No name)"),
            receptionistColumnWidth(1) - 10
        );
        addReceptionistTableGridCell(row, nameCell, 1, GridBagConstraints.NORTHWEST);

        JPanel usernameCell = appointmentWrappedTextCell(
            "@" + safeText(record.username, "-"),
            receptionistColumnWidth(2) - 10
        );
        addReceptionistTableGridCell(row, usernameCell, 2, GridBagConstraints.NORTHWEST);

        JPanel createdCell = appointmentWrappedTextCell(
            safeText(record.createdText, "-"),
            receptionistColumnWidth(3) - 10
        );
        addReceptionistTableGridCell(row, createdCell, 3, GridBagConstraints.NORTHWEST);

        JPanel statusCell = statusBadge(record.status);
        addReceptionistTableGridCell(row, statusCell, 4, GridBagConstraints.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));

        JButton edit = softActionButton("Edit", new Color(236, 241, 251), new Color(40, 53, 79));
        edit.setPreferredSize(new Dimension(108, 32));
        edit.setMinimumSize(new Dimension(108, 32));
        edit.setMaximumSize(new Dimension(108, 32));
        edit.setAlignmentX(Component.CENTER_ALIGNMENT);
        edit.addActionListener(e -> {
            if (!confirmAdminPasswordForReceptionistEdit()) {
                return;
            }

            ReceptionistRecord latestRecord = fetchReceptionistRecordForEdit(record);
            if (latestRecord == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Could not load the selected receptionist. Please refresh and try again.",
                    "Load Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            JTextField nameField = new JTextField(latestRecord.name);
            JTextField usernameField = new JTextField(latestRecord.username);
            JPasswordField passwordField = new JPasswordField();
            JPasswordField confirmPasswordField = new JPasswordField();
            styleInputField(nameField);
            styleInputField(usernameField);
            stylePasswordField(passwordField);
            stylePasswordField(confirmPasswordField);
            JPanel form = landscapeFormPanel(
                new String[]{
                    "Full Name",
                    "Username",
                    "New Password (optional)",
                    "Confirm New Password"
                },
                new JComponent[]{
                    nameField,
                    usernameField,
                    passwordField,
                    confirmPasswordField
                },
                2,
                720
            );
            while (true) {
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

                resetFieldValidationState(nameField, usernameField, passwordField, confirmPasswordField);

                String nameError = validateReceptionistName(nameField.getText().trim());
                if (nameError != null) {
                    highlightInvalidField(nameField);
                    JOptionPane.showMessageDialog(this, nameError, "Invalid Name", JOptionPane.WARNING_MESSAGE);
                    continue;
                }

                String usernameError = validateReceptionistUsername(usernameField.getText().trim());
                if (usernameError != null) {
                    highlightInvalidField(usernameField);
                    JOptionPane.showMessageDialog(this, usernameError, "Invalid Username", JOptionPane.WARNING_MESSAGE);
                    continue;
                }

                String duplicateError = validateReceptionistUsernameUniqueness(
                    usernameField.getText().trim(),
                    latestRecord.accountPk > 0 ? latestRecord.accountPk : null,
                    latestRecord.username
                );
                if (duplicateError != null) {
                    highlightInvalidField(usernameField);
                    JOptionPane.showMessageDialog(this, duplicateError, "Duplicate Username", JOptionPane.WARNING_MESSAGE);
                    continue;
                }

                String newPassword = new String(passwordField.getPassword()).trim();
                String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
                boolean updatePassword = !newPassword.isBlank() || !confirmPassword.isBlank();

                if (updatePassword) {
                    String passwordError = validateReceptionistPassword(newPassword);
                    if (passwordError != null) {
                        highlightInvalidField(passwordField);
                        JOptionPane.showMessageDialog(this, passwordError, "Invalid Password", JOptionPane.WARNING_MESSAGE);
                        continue;
                    }
                    if (!newPassword.equals(confirmPassword)) {
                        highlightInvalidField(passwordField);
                        highlightInvalidField(confirmPasswordField);
                        JOptionPane.showMessageDialog(
                            this,
                            "Password and Confirm Password do not match.",
                            "Password Mismatch",
                            JOptionPane.WARNING_MESSAGE
                        );
                        continue;
                    }
                }

                if (!updateReceptionistInDatabase(
                    latestRecord,
                    nameField.getText().trim(),
                    usernameField.getText().trim(),
                    updatePassword ? newPassword : null
                )) {
                    String duplicateAfterUpdateError = validateReceptionistUsernameUniqueness(
                        usernameField.getText().trim(),
                        latestRecord.accountPk > 0 ? latestRecord.accountPk : null,
                        latestRecord.username
                    );
                    if (duplicateAfterUpdateError != null && duplicateAfterUpdateError.startsWith("Username already exists")) {
                        highlightInvalidField(usernameField);
                        JOptionPane.showMessageDialog(this, duplicateAfterUpdateError, "Duplicate Username", JOptionPane.WARNING_MESSAGE);
                        continue;
                    }
                    JOptionPane.showMessageDialog(
                        this,
                        "Could not update receptionist account. Please check your database connection and try again.",
                        "Update Failed",
                        JOptionPane.ERROR_MESSAGE
                    );
                    continue;
                }
                refreshAllViews();
                JOptionPane.showMessageDialog(
                    this,
                    "Record updated successfully.",
                    "Update Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
        });

        boolean activeReceptionist = "active".equals(record.status);
        JButton statusAction;
        if (activeReceptionist) {
            statusAction = softActionButton("Deactivate", new Color(252, 236, 236), new Color(224, 93, 93));
            statusAction.setPreferredSize(new Dimension(108, 32));
            statusAction.setMinimumSize(new Dimension(108, 32));
            statusAction.setMaximumSize(new Dimension(108, 32));
            statusAction.setAlignmentX(Component.CENTER_ALIGNMENT);
            statusAction.addActionListener(e -> {
                if (!confirmAdminPasswordForReceptionistEdit()) {
                    return;
                }
                if (!updateReceptionistStatusInDatabase(record, "deactivated")) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Could not deactivate this receptionist right now. Please try again.",
                        "Update Failed",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                refreshAllViews();
            });
        } else {
            statusAction = softActionButton("Activate", new Color(231, 246, 236), new Color(73, 190, 107));
            statusAction.setPreferredSize(new Dimension(108, 32));
            statusAction.setMinimumSize(new Dimension(108, 32));
            statusAction.setMaximumSize(new Dimension(108, 32));
            statusAction.setAlignmentX(Component.CENTER_ALIGNMENT);
            statusAction.addActionListener(e -> {
                if (!confirmAdminPasswordForReceptionistEdit()) {
                    return;
                }
                if (!updateReceptionistStatusInDatabase(record, "active")) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Could not activate this receptionist right now. Please try again.",
                        "Update Failed",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                refreshAllViews();
            });
        }

        actions.setPreferredSize(new Dimension(112, 72));
        actions.setMinimumSize(new Dimension(112, 72));
        actions.setMaximumSize(new Dimension(112, 72));
        if (activeReceptionist) {
            actions.add(edit);
            actions.add(Box.createVerticalStrut(8));
        }
        actions.add(statusAction);
        addReceptionistTableGridCell(row, actions, 5, GridBagConstraints.CENTER);

        int wrappedHeight = Math.max(nameCell.getPreferredSize().height, usernameCell.getPreferredSize().height);
        wrappedHeight = Math.max(wrappedHeight, createdCell.getPreferredSize().height);
        wrappedHeight = Math.max(wrappedHeight, statusCell.getPreferredSize().height);
        wrappedHeight = Math.max(wrappedHeight, actions.getPreferredSize().height);
        int dynamicHeight = Math.max(TABLE_ROW_HEIGHT, wrappedHeight + 28);
        row.setMinimumSize(new Dimension(RECEPTIONIST_TABLE_TOTAL_WIDTH, dynamicHeight));
        row.setPreferredSize(new Dimension(RECEPTIONIST_TABLE_TOTAL_WIDTH, dynamicHeight));
        row.setMaximumSize(new Dimension(RECEPTIONIST_TABLE_TOTAL_WIDTH, dynamicHeight));
        return row;
    }

    private boolean confirmAdminPasswordForReceptionistEdit() {
        JPasswordField adminPasswordField = new JPasswordField();
        stylePasswordField(adminPasswordField);

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.add(new JLabel("Enter admin password to continue."));
        form.add(adminPasswordField);

        int result = JOptionPane.showConfirmDialog(
            this,
            form,
            "Confirm Admin Password",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) {
            return false;
        }

        String adminPassword = new String(adminPasswordField.getPassword()).trim();
        if (adminPassword.isBlank()) {
            highlightInvalidField(adminPasswordField);
            JOptionPane.showMessageDialog(
                this,
                "Admin password is required to edit receptionist accounts.",
                "Admin Password Required",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        if (!isAdminPasswordCorrect(adminPassword)) {
            highlightInvalidField(adminPasswordField);
            JOptionPane.showMessageDialog(
                this,
                "Incorrect admin password. Editing is not allowed.",
                "Authentication Failed",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        return true;
    }

    private boolean isAdminPasswordCorrect(String adminPassword) {
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                return false;
            }

            AccountMeta meta = resolveAccountMeta(con);
            if (!meta.tableExists || !meta.columns.contains("username") || !meta.columns.contains("password")) {
                return false;
            }

            StringBuilder sql = new StringBuilder(
                "SELECT 1 FROM account WHERE LOWER(username)=LOWER(?) AND password=?"
            );
            if (meta.columns.contains("status")) {
                sql.append(" AND LOWER(status)='active'");
            }
            if (meta.columns.contains("role")) {
                sql.append(" AND LOWER(role)='admin'");
            }
            sql.append(" LIMIT 1");

            try (PreparedStatement pst = con.prepareStatement(sql.toString())) {
                pst.setString(1, adminName);
                pst.setString(2, adminPassword);
                try (ResultSet rs = pst.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
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

    private String[] splitPatientNameParts(String fullName) {
        String clean = safeText(fullName, "").trim();
        if (clean.isEmpty()) {
            return new String[] {"", "", ""};
        }

        String[] parts = clean.split("\\s+");
        String firstName = parts[0];
        String middleName = "";
        String lastName = "";

        if (parts.length == 2) {
            lastName = parts[1];
        } else if (parts.length >= 3) {
            middleName = parts[1];
            StringBuilder lastNameBuilder = new StringBuilder(parts[2]);
            for (int i = 3; i < parts.length; i++) {
                lastNameBuilder.append(" ").append(parts[i]);
            }
            lastName = lastNameBuilder.toString();
        }

        return new String[] {firstName, middleName, lastName};
    }

    private String buildPatientFullName(String firstName, String middleName, String lastName) {
        String first = firstName == null ? "" : firstName.trim();
        String middle = middleName == null ? "" : middleName.trim();
        String last = lastName == null ? "" : lastName.trim();
        if (middle.isBlank()) {
            return (first + " " + last).trim();
        }
        return (first + " " + middle + " " + last).trim();
    }

    private String buildReceptionistFullName(String firstName, String middleName, String lastName) {
        String first = firstName == null ? "" : firstName.trim();
        String middle = middleName == null ? "" : middleName.trim();
        String last = lastName == null ? "" : lastName.trim();
        if (middle.isBlank()) {
            return (first + " " + last).trim();
        }
        return (first + " " + middle + " " + last).trim();
    }

    private String validateRequiredPatientNamePart(String fieldLabel, String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isBlank()) {
            return fieldLabel + " is required.";
        }
        if (trimmed.length() < 2) {
            return fieldLabel + " must be at least 2 letters.";
        }
        if (!trimmed.matches("[A-Za-z]+")) {
            return fieldLabel + " must contain letters only. Numbers and special characters are not allowed.";
        }
        return null;
    }

    private String validateLettersAndSpacesField(String fieldLabel, String value, boolean required) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isBlank()) {
            return required ? fieldLabel + " is required." : null;
        }
        if (!trimmed.matches("[A-Za-z ]+")) {
            return fieldLabel + " must contain letters and spaces only. Numbers and special characters are not allowed.";
        }
        return null;
    }

    private String validateOptionalPatientNamePart(String fieldLabel, String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isBlank()) {
            return null;
        }
        if (!trimmed.matches("[A-Za-z]+")) {
            return fieldLabel + " must contain letters only when provided. Numbers and special characters are not allowed.";
        }
        return null;
    }

    private String validateReceptionistName(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isBlank()) {
            return "Full Name is required.";
        }
        if (trimmed.length() < 2) {
            return "Full Name must be at least 2 characters.";
        }
        if (!trimmed.matches("[A-Za-z][A-Za-z .'-]*")) {
            return "Full Name may contain letters, spaces, apostrophes, periods, and hyphens only.";
        }
        return null;
    }

    private String validateReceptionistUsername(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isBlank()) {
            return "Username is required.";
        }
        if (trimmed.length() < 4) {
            return "Username must be at least 4 characters.";
        }
        if (!trimmed.matches("[A-Za-z0-9_.]+")) {
            return "Username can only contain letters, numbers, underscore (_), and period (.)";
        }
        return null;
    }

    private String validateReceptionistPassword(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isBlank()) {
            return "Password is required.";
        }
        if (trimmed.length() < 6) {
            return "Password must be at least 6 characters.";
        }
        return null;
    }

    private String validateReceptionistUsernameUniqueness(
        String username,
        Integer excludeAccountPk,
        String excludeUsername
    ) {
        String normalized = username == null ? "" : username.trim();
        if (normalized.isBlank()) {
            return "Username is required.";
        }

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                return "Unable to validate username because the database is not connected.";
            }

            AccountMeta meta = resolveAccountMeta(con);
            if (!meta.tableExists || !meta.columns.contains("username")) {
                return "Account table is not available. Please verify your database setup.";
            }

            StringBuilder sql = new StringBuilder("SELECT 1 FROM account WHERE LOWER(username)=LOWER(?)");
            boolean useIdExclusion = excludeAccountPk != null && meta.idColumn != null;
            if (useIdExclusion) {
                sql.append(" AND ").append(meta.idColumn).append("<>?");
            } else if (excludeUsername != null && !excludeUsername.isBlank()) {
                sql.append(" AND LOWER(username)<>LOWER(?)");
            }
            sql.append(" LIMIT 1");

            try (PreparedStatement pst = con.prepareStatement(sql.toString())) {
                pst.setString(1, normalized);
                if (useIdExclusion) {
                    pst.setInt(2, excludeAccountPk);
                } else if (excludeUsername != null && !excludeUsername.isBlank()) {
                    pst.setString(2, excludeUsername.trim());
                }

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return "Username already exists. Please choose a different username.";
                    }
                }
            }

            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "Unable to validate username right now. Please try again.";
        }
    }

    private String normalizePhoneNumber(String value) {
        return value == null ? "" : value.trim();
    }

    private String validateAppointmentDateInput(String value) {
        String raw = value == null ? "" : value.trim();
        if (raw.isBlank()) {
            return "Appointment date is required.";
        }

        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(raw, APPOINTMENT_FORM_DATE_FORMAT);
        } catch (Exception ex) {
            return "Invalid date format. Please use MM/DD/YYYY (example: 04/28/2026).";
        }

        LocalDate today = LocalDate.now();
        LocalDate endOfCurrentMonth = today.withDayOfMonth(today.lengthOfMonth());
        if (parsedDate.isBefore(today) || parsedDate.isAfter(endOfCurrentMonth)) {
            return "Appointments can only be booked from today until the end of the current month.";
        }

        DayOfWeek day = parsedDate.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return "Weekend dates are not allowed. Please select a Monday to Friday schedule.";
        }

        return null;
    }

    private String[] buildAppointmentTimeOptions() {
        List<String> options = new ArrayList<>();
        LocalTime current = LocalTime.of(9, 0);
        LocalTime closing = LocalTime.of(17, 0);
        while (!current.isAfter(closing)) {
            options.add(current.format(APPOINTMENT_FORM_TIME_FORMAT));
            current = current.plusMinutes(30);
        }
        return options.toArray(new String[0]);
    }

    private LocalTime parseAppointmentFormTime(String value) {
        String raw = value == null ? "" : value.trim();
        return LocalTime.parse(raw, APPOINTMENT_FORM_TIME_FORMAT);
    }

    private String validateAppointmentTimeInput(String value) {
        String raw = value == null ? "" : value.trim();
        if (raw.isBlank()) {
            return "Appointment time is required.";
        }

        LocalTime parsedTime;
        try {
            parsedTime = parseAppointmentFormTime(raw);
        } catch (Exception ex) {
            return "Invalid time format. Use 12-hour format with AM/PM only (example: 9:00 AM). Military time is not allowed.";
        }

        LocalTime openingTime = LocalTime.of(9, 0);
        LocalTime closingTime = LocalTime.of(17, 0);
        if (parsedTime.isBefore(openingTime)) {
            return "Appointments before 9:00 AM are not allowed.";
        }
        if (parsedTime.isAfter(closingTime)) {
            return "Appointments after 5:00 PM are not allowed.";
        }

        return null;
    }

    private String validateAppointmentSlotAvailability(
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        Integer excludeAppointmentId
    ) {
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                return "Unable to validate schedule because the database is not connected.";
            }
            ensureClinicTables(con);
            boolean hasOverlap = hasOverlappingAppointment(
                con,
                appointmentDate,
                appointmentTime,
                APPOINTMENT_DURATION_MINUTES,
                excludeAppointmentId
            );
            if (hasOverlap) {
                return TIME_SLOT_BOOKED_MESSAGE;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "Unable to validate appointment schedule right now. Please try again.";
        }
    }

    private boolean hasOverlappingAppointment(
        Connection con,
        LocalDate appointmentDate,
        LocalTime appointmentStart,
        int durationMinutes,
        Integer excludeAppointmentId
    ) throws SQLException {
        String sql = "SELECT appointment_id, appointment_time, status FROM appointment WHERE appointment_date=?";
        LocalTime appointmentEnd = appointmentStart.plusMinutes(durationMinutes);

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setDate(1, Date.valueOf(appointmentDate));
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int existingId = rs.getInt("appointment_id");
                    if (excludeAppointmentId != null && existingId == excludeAppointmentId) {
                        continue;
                    }
                    String status = normalizeAppointmentStatus(rs.getString("status"));
                    if (!"pending".equals(status)) {
                        continue;
                    }

                    Time existingRawTime = rs.getTime("appointment_time");
                    if (existingRawTime == null) {
                        continue;
                    }

                    LocalTime existingStart = existingRawTime.toLocalTime().withSecond(0).withNano(0);
                    LocalTime existingEnd = existingStart.plusMinutes(durationMinutes);
                    boolean overlaps = appointmentStart.isBefore(existingEnd) && existingStart.isBefore(appointmentEnd);
                    if (overlaps) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private String validatePhoneNumber(String fieldLabel, String value) {
        String normalized = normalizePhoneNumber(value);
        if (normalized.isBlank()) {
            return fieldLabel + " is required.";
        }
        if (!normalized.matches("\\d+")) {
            return fieldLabel + " must contain digits only. Letters and symbols are not allowed.";
        }
        if (normalized.length() != 11) {
            return fieldLabel + " must be exactly 11 digits.";
        }
        if (!normalized.startsWith("09")) {
            return fieldLabel + " must start with 09.";
        }
        return null;
    }

    private String validatePatientContactUniqueness(
        String contactNumber,
        String emergencyContactNumber,
        Integer excludePatientId
    ) {
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                return "Unable to validate contact numbers because the database is not connected.";
            }
            ensureClinicTables(con);

            if (patientContactNumberExists(con, contactNumber, excludePatientId)) {
                return "Contact Number already exists in the database. Please use a different number.";
            }
            if (patientContactNumberExists(con, emergencyContactNumber, excludePatientId)) {
                return "Emergency Contact Number already exists in the database. Please use a different number.";
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "Unable to validate contact number uniqueness right now. Please try again.";
        }
    }

    private boolean matchesAppointmentSearch(AppointmentRecord record) {
        if (appointmentSearchQuery.isBlank()) {
            return true;
        }
        String haystack = (
            record.patientName + " " +
            record.contactNumber + " " +
            record.emergencyContactNumber + " " +
            record.reason + " " +
            record.allergies + " " +
            record.notes + " " +
            record.dateText + " " +
            record.timeText
        )
            .toLowerCase(Locale.ENGLISH);
        return haystack.contains(appointmentSearchQuery);
    }

    private boolean isSameCalendarDate(AppointmentRecord record, LocalDate selectedDate) {
        if (record == null || selectedDate == null) {
            return false;
        }
        LocalDate recordDate = resolveAppointmentRecordDate(record);
        return selectedDate.equals(recordDate);
    }

    private LocalDate resolveAppointmentRecordDate(AppointmentRecord record) {
        if (record == null) {
            return null;
        }
        if (record.appointmentDate != null) {
            return record.appointmentDate;
        }
        String raw = safeText(record.dateText, "").trim();
        if (raw.isBlank()) {
            return null;
        }
        DateTimeFormatter[] fallbackFormats = new DateTimeFormatter[]{
            APPOINTMENT_FORM_DATE_FORMAT,
            CALENDAR_INPUT_FORMAT,
            DATE_LABEL_FORMAT
        };
        for (DateTimeFormatter formatter : fallbackFormats) {
            try {
                return LocalDate.parse(raw, formatter);
            } catch (Exception ignored) {
                // Try the next formatter.
            }
        }
        return null;
    }

    private boolean matchesPatientSearch(PatientRecord record) {
        if (patientSearchQuery.isBlank()) {
            return true;
        }
        String haystack = (
            safeText(record.firstName, "") + " " +
            safeText(record.middleName, "") + " " +
            safeText(record.lastName, "") + " " +
            safeText(record.contactNumber, "") + " " +
            safeText(record.emergencyContactNumber, "") + " " +
            safeText(record.patientId, "")
        )
            .toLowerCase(Locale.ENGLISH);
        return haystack.contains(patientSearchQuery);
    }

    private boolean matchesLogSearch(AppointmentRecord record) {
        if (logSearchQuery.isBlank()) {
            return true;
        }
        String haystack = (record.patientName + " " + record.reason + " " + record.allergies + " " + record.notes + " " + record.cancelReason + " " + record.dateText + " " + record.timeText)
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

    private JPanel appointmentTableHeaderRow() {
        RoundedPanel header = new RoundedPanel(12, new Color(243, 247, 255));
        header.setLayout(new GridBagLayout());
        header.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(223, 231, 244), 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
            )
        );
        header.setMinimumSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, 52));
        header.setPreferredSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, 52));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        addAppointmentTableGridCell(header, appointmentHeaderLabel("Date"), 0, GridBagConstraints.WEST);
        addAppointmentTableGridCell(header, appointmentHeaderLabel("Time"), 1, GridBagConstraints.WEST);
        addAppointmentTableGridCell(header, appointmentHeaderLabel("Patient Name"), 2, GridBagConstraints.WEST);
        addAppointmentTableGridCell(header, appointmentHeaderLabel("Reason"), 3, GridBagConstraints.WEST);
        addAppointmentTableGridCell(header, appointmentHeaderLabel("Allergies"), 4, GridBagConstraints.WEST);
        addAppointmentTableGridCell(header, appointmentHeaderLabel("Notes"), 5, GridBagConstraints.WEST);
        JLabel statusHeader = appointmentHeaderLabel("Status");
        statusHeader.setHorizontalAlignment(SwingConstants.CENTER);
        addAppointmentTableGridCell(header, statusHeader, 6, GridBagConstraints.CENTER);
        JLabel actionsHeader = appointmentHeaderLabel("Actions");
        actionsHeader.setHorizontalAlignment(SwingConstants.CENTER);
        addAppointmentTableGridCell(header, actionsHeader, 7, GridBagConstraints.CENTER);
        return header;
    }

    private JLabel appointmentHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(new Color(73, 87, 112));
        return label;
    }

    private void addAppointmentTableGridCell(JPanel row, Component content, int columnIndex, int anchor) {
        int colWidth = appointmentColumnWidth(columnIndex);
        Dimension contentPref = content.getPreferredSize();
        int prefHeight = Math.max(24, contentPref == null ? 24 : contentPref.height);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setMinimumSize(new Dimension(colWidth, prefHeight));
        wrapper.setPreferredSize(new Dimension(colWidth, prefHeight));
        wrapper.setMaximumSize(new Dimension(colWidth, Integer.MAX_VALUE));
        if (anchor == GridBagConstraints.CENTER) {
            wrapper.add(content, BorderLayout.CENTER);
        } else if (anchor == GridBagConstraints.EAST || anchor == GridBagConstraints.NORTHEAST || anchor == GridBagConstraints.SOUTHEAST) {
            wrapper.add(content, BorderLayout.EAST);
        } else {
            wrapper.add(content, BorderLayout.WEST);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = columnIndex;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = anchor;
        gbc.insets = new java.awt.Insets(0, 8, 0, 8);
        row.add(wrapper, gbc);
    }

    private int appointmentColumnWidth(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= APPOINTMENT_TABLE_COLUMN_WIDTHS.length) {
            return 120;
        }
        return APPOINTMENT_TABLE_COLUMN_WIDTHS[columnIndex];
    }

    private JPanel patientTableHeaderRow() {
        RoundedPanel header = new RoundedPanel(12, new Color(243, 247, 255));
        header.setLayout(new GridBagLayout());
        header.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(223, 231, 244), 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
            )
        );
        header.setMinimumSize(new Dimension(PATIENT_TABLE_TOTAL_WIDTH, 52));
        header.setPreferredSize(new Dimension(PATIENT_TABLE_TOTAL_WIDTH, 52));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        addPatientTableGridCell(header, appointmentHeaderLabel("Patient ID"), 0, GridBagConstraints.WEST);
        addPatientTableGridCell(header, appointmentHeaderLabel("First Name"), 1, GridBagConstraints.WEST);
        addPatientTableGridCell(header, appointmentHeaderLabel("Middle Name"), 2, GridBagConstraints.WEST);
        addPatientTableGridCell(header, appointmentHeaderLabel("Last Name"), 3, GridBagConstraints.WEST);
        addPatientTableGridCell(header, appointmentHeaderLabel("Contact Number"), 4, GridBagConstraints.WEST);
        addPatientTableGridCell(header, appointmentHeaderLabel("Emergency Contact"), 5, GridBagConstraints.WEST);
        JLabel actionsHeader = appointmentHeaderLabel("Actions");
        actionsHeader.setHorizontalAlignment(SwingConstants.CENTER);
        addPatientTableGridCell(header, actionsHeader, 6, GridBagConstraints.CENTER);
        return header;
    }

    private int patientColumnWidth(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= PATIENT_TABLE_COLUMN_WIDTHS.length) {
            return 120;
        }
        return PATIENT_TABLE_COLUMN_WIDTHS[columnIndex];
    }

    private void addPatientTableGridCell(JPanel row, Component content, int columnIndex, int anchor) {
        int colWidth = patientColumnWidth(columnIndex);
        Dimension contentPref = content.getPreferredSize();
        int prefHeight = Math.max(24, contentPref == null ? 24 : contentPref.height);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setMinimumSize(new Dimension(colWidth, prefHeight));
        wrapper.setPreferredSize(new Dimension(colWidth, prefHeight));
        wrapper.setMaximumSize(new Dimension(colWidth, Integer.MAX_VALUE));
        if (anchor == GridBagConstraints.CENTER) {
            wrapper.add(content, BorderLayout.CENTER);
        } else if (anchor == GridBagConstraints.EAST || anchor == GridBagConstraints.NORTHEAST || anchor == GridBagConstraints.SOUTHEAST) {
            wrapper.add(content, BorderLayout.EAST);
        } else {
            wrapper.add(content, BorderLayout.WEST);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = columnIndex;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = anchor;
        gbc.insets = new java.awt.Insets(0, 8, 0, 8);
        row.add(wrapper, gbc);
    }

    private JPanel receptionistTableHeaderRow() {
        RoundedPanel header = new RoundedPanel(12, new Color(243, 247, 255));
        header.setLayout(new GridBagLayout());
        header.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(223, 231, 244), 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
            )
        );
        header.setMinimumSize(new Dimension(RECEPTIONIST_TABLE_TOTAL_WIDTH, 52));
        header.setPreferredSize(new Dimension(RECEPTIONIST_TABLE_TOTAL_WIDTH, 52));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        addReceptionistTableGridCell(header, appointmentHeaderLabel("Account ID"), 0, GridBagConstraints.WEST);
        addReceptionistTableGridCell(header, appointmentHeaderLabel("Name"), 1, GridBagConstraints.WEST);
        addReceptionistTableGridCell(header, appointmentHeaderLabel("Username"), 2, GridBagConstraints.WEST);
        addReceptionistTableGridCell(header, appointmentHeaderLabel("Created"), 3, GridBagConstraints.WEST);
        JLabel statusHeader = appointmentHeaderLabel("Status");
        statusHeader.setHorizontalAlignment(SwingConstants.CENTER);
        addReceptionistTableGridCell(header, statusHeader, 4, GridBagConstraints.CENTER);
        JLabel actionsHeader = appointmentHeaderLabel("Actions");
        actionsHeader.setHorizontalAlignment(SwingConstants.CENTER);
        addReceptionistTableGridCell(header, actionsHeader, 5, GridBagConstraints.CENTER);
        return header;
    }

    private int receptionistColumnWidth(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= RECEPTIONIST_TABLE_COLUMN_WIDTHS.length) {
            return 120;
        }
        return RECEPTIONIST_TABLE_COLUMN_WIDTHS[columnIndex];
    }

    private void addReceptionistTableGridCell(JPanel row, Component content, int columnIndex, int anchor) {
        int colWidth = receptionistColumnWidth(columnIndex);
        Dimension contentPref = content.getPreferredSize();
        int prefHeight = Math.max(24, contentPref == null ? 24 : contentPref.height);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setMinimumSize(new Dimension(colWidth, prefHeight));
        wrapper.setPreferredSize(new Dimension(colWidth, prefHeight));
        wrapper.setMaximumSize(new Dimension(colWidth, Integer.MAX_VALUE));
        if (anchor == GridBagConstraints.CENTER) {
            wrapper.add(content, BorderLayout.CENTER);
        } else if (anchor == GridBagConstraints.EAST || anchor == GridBagConstraints.NORTHEAST || anchor == GridBagConstraints.SOUTHEAST) {
            wrapper.add(content, BorderLayout.EAST);
        } else {
            wrapper.add(content, BorderLayout.WEST);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = columnIndex;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = anchor;
        gbc.insets = new java.awt.Insets(0, 8, 0, 8);
        row.add(wrapper, gbc);
    }

    private JPanel appointmentWrappedTextCell(String text, int preferredWidth) {
        int safeWidth = Math.max(80, preferredWidth);
        String safeValue = safeText(text, "N/A");
        JTextArea textArea = new JTextArea(safeValue);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setOpaque(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setForeground(new Color(64, 78, 104));
        textArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        textArea.setRows(1);
        textArea.setColumns(Math.max(8, safeWidth / 10));
        textArea.setSize(new Dimension(safeWidth, Integer.MAX_VALUE));

        Dimension textPreferred = textArea.getPreferredSize();
        int finalHeight = Math.max(22, textPreferred.height);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(textArea, BorderLayout.NORTH);
        wrap.setPreferredSize(new Dimension(safeWidth, finalHeight));
        wrap.setMinimumSize(new Dimension(safeWidth, finalHeight));
        wrap.setMaximumSize(new Dimension(safeWidth, finalHeight));
        return wrap;
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

    private void updateRowsPanelHeightFromChildren(JPanel rowsPanel) {
        if (rowsPanel == null) {
            return;
        }
        int totalHeight = 6;
        Component[] children = rowsPanel.getComponents();
        if (children.length == 0) {
            updateRowsPanelPreferredHeight(rowsPanel, 1);
            return;
        }
        for (Component child : children) {
            if (child == null) {
                continue;
            }
            Dimension pref = child.getPreferredSize();
            totalHeight += Math.max(0, pref == null ? 0 : pref.height);
        }
        totalHeight = Math.max(1, totalHeight);
        int targetWidth = APPOINTMENT_TABLE_TOTAL_WIDTH;
        Object tableWidthObj = rowsPanel.getClientProperty("qc.tableWidth");
        if (tableWidthObj instanceof Number) {
            targetWidth = Math.max(10, ((Number) tableWidthObj).intValue());
        }
        rowsPanel.setMinimumSize(new Dimension(targetWidth, totalHeight));
        rowsPanel.setPreferredSize(new Dimension(targetWidth, totalHeight));
        rowsPanel.setMaximumSize(new Dimension(targetWidth, totalHeight));
    }

    private JScrollPane tableRowsScrollPane(JPanel rowsPanel) {
        return tableRowsScrollPane(rowsPanel, false);
    }

    private JScrollPane tableRowsScrollPane(JPanel rowsPanel, boolean allowHorizontalScroll) {
        rowsPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        JScrollPane scrollPane = new JScrollPane(rowsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(
            allowHorizontalScroll ? JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED : JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, Integer.MAX_VALUE));
        if (allowHorizontalScroll) {
            scrollPane.getHorizontalScrollBar().setUnitIncrement(22);
            scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(Integer.MAX_VALUE, 10));
        }
        scrollPane.setMinimumSize(new Dimension(100, TABLE_SCROLL_HEIGHT));
        scrollPane.setPreferredSize(new Dimension(1000, TABLE_SCROLL_HEIGHT));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, TABLE_SCROLL_HEIGHT));
        return scrollPane;
    }

    private JPanel horizontalScrollHintPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 4, 10, 4));

        JLabel hint = new JLabel("Scroll horizontally to view more details.");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hint.setForeground(new Color(108, 121, 146));
        wrapper.add(hint, BorderLayout.WEST);
        return wrapper;
    }

    private JScrollPane dashboardTodayRowsScrollPane(JPanel rowsPanel) {
        JScrollPane scrollPane = new JScrollPane(rowsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBlockIncrement(64);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, Integer.MAX_VALUE));
        scrollPane.setMinimumSize(new Dimension(100, DASHBOARD_TODAY_SCROLL_HEIGHT));
        scrollPane.setPreferredSize(new Dimension(1000, DASHBOARD_TODAY_SCROLL_HEIGHT));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, DASHBOARD_TODAY_SCROLL_HEIGHT));
        return scrollPane;
    }

    private void updateDashboardTodayRowsHeight(int rowCount, boolean emptyState) {
        if (dashboardTodayRows == null) {
            return;
        }
        int contentHeight;
        if (emptyState) {
            contentHeight = DASHBOARD_TODAY_SCROLL_HEIGHT;
        } else {
            int safeRows = Math.max(1, rowCount);
            contentHeight = (safeRows * DASHBOARD_TODAY_ROW_HEIGHT)
                + (Math.max(0, safeRows - 1) * DASHBOARD_TODAY_ROW_GAP)
                + 4;
        }
        dashboardTodayRows.setMinimumSize(new Dimension(10, contentHeight));
        dashboardTodayRows.setPreferredSize(new Dimension(10, contentHeight));
        dashboardTodayRows.setMaximumSize(new Dimension(Integer.MAX_VALUE, contentHeight));
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

    private JPanel logEmptyStatePanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(28, 0, 28, 0));

        JLabel message = new JLabel("No logs available.");
        message.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        message.setForeground(new Color(88, 103, 131));
        message.setHorizontalAlignment(SwingConstants.CENTER);
        wrapper.add(message, new GridBagConstraints());
        return wrapper;
    }

    private JPanel calendarDateEmptyStatePanel() {
        RoundedPanel card = new RoundedPanel(14, new Color(247, 250, 255));
        card.setLayout(new GridBagLayout());
        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 229, 242), 1),
                BorderFactory.createEmptyBorder(18, 14, 18, 14)
            )
        );
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        int emptyHeight = Math.max(TABLE_ROW_HEIGHT, TABLE_SCROLL_HEIGHT - 10);
        card.setMinimumSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, emptyHeight));
        card.setPreferredSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, emptyHeight));
        card.setMaximumSize(new Dimension(APPOINTMENT_TABLE_TOTAL_WIDTH, emptyHeight));

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("No appointments for this date.");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(66, 83, 118));

        String selectedDate = appointmentCalendarDate == null
            ? "-"
            : appointmentCalendarDate.format(APPOINTMENT_CALENDAR_TITLE_FORMAT);
        JLabel subtitle = new JLabel(selectedDate);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(126, 140, 168));

        stack.add(title);
        stack.add(Box.createVerticalStrut(8));
        stack.add(subtitle);
        card.add(stack, new GridBagConstraints());
        return card;
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
        final int patientPk;
        final String patientName;
        final String contactNumber;
        final String emergencyContactNumber;
        final String dateText;
        final String timeText;
        final String reason;
        final String allergies;
        final String notes;
        final LocalDate appointmentDate;
        final LocalTime appointmentTime;
        String status;
        String cancelReason;

        AppointmentRecord(
            int id,
            int patientPk,
            String patientName,
            String dateText,
            String timeText,
            String reason,
            String allergies,
            String notes,
            String status,
            String cancelReason,
            String contactNumber,
            String emergencyContactNumber,
            LocalDate appointmentDate,
            LocalTime appointmentTime
        ) {
            this.id = id;
            this.patientPk = patientPk;
            this.patientName = patientName;
            this.contactNumber = contactNumber;
            this.emergencyContactNumber = emergencyContactNumber;
            this.dateText = dateText;
            this.timeText = timeText;
            this.reason = reason;
            this.allergies = allergies;
            this.notes = notes;
            this.status = status;
            this.cancelReason = cancelReason;
            this.appointmentDate = appointmentDate;
            this.appointmentTime = appointmentTime;
        }
    }

    private static class PatientRecord {
        final int id;
        final String patientId;
        String firstName;
        String middleName;
        String lastName;
        String contactNumber;
        String emergencyContactNumber;

        PatientRecord(
            int id,
            String patientId,
            String firstName,
            String middleName,
            String lastName,
            String contactNumber,
            String emergencyContactNumber
        ) {
            this.id = id;
            this.patientId = patientId;
            this.firstName = firstName;
            this.middleName = middleName;
            this.lastName = lastName;
            this.contactNumber = contactNumber;
            this.emergencyContactNumber = emergencyContactNumber;
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
