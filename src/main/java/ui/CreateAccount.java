package ui;

import database.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateAccount extends BaseUI {

    private JTextField nameField;
    private JTextField diabetesField;
    private JTextField insulinTypeField;
    private JTextField insulinAdminField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField doctorEmailField;
    private JTextField doctorAddressField;
    private JTextField doctorEmergencyField;
    private JPasswordField passwordField;

    public CreateAccount() {
        super("Create Account");

        // Main gradient
        JPanel mainPanel = createGradientPanel(Color.WHITE, new Color(240, 240, 240));
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx=0;

        // Title
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK);

        gbc.gridy=0;
        gbc.gridwidth=2;
        gbc.insets = new Insets(0,0,120,0);
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth=1;
        gbc.insets = new Insets(3,5,3,5);

        int row = 1;
        row = addLabelAndField(mainPanel, gbc, row, "Full Name:",          nameField          = new JTextField(15));
        row = addLabelAndField(mainPanel, gbc, row, "Type of Diabetes:",   diabetesField      = new JTextField(15));
        row = addLabelAndField(mainPanel, gbc, row, "Type of Insulin:",    insulinTypeField   = new JTextField(15));
        row = addLabelAndField(mainPanel, gbc, row, "Insulin Admin:",      insulinAdminField  = new JTextField(15));
        row = addLabelAndField(mainPanel, gbc, row, "Email:",              emailField         = new JTextField(15));
        row = addLabelAndField(mainPanel, gbc, row, "Phone:",              phoneField         = new JTextField(15));
        row = addLabelAndField(mainPanel, gbc, row, "Doctor Email:",       doctorEmailField   = new JTextField(15));
        row = addLabelAndField(mainPanel, gbc, row, "Doctor Address:",     doctorAddressField = new JTextField(15));
        row = addLabelAndField(mainPanel, gbc, row, "Doctor Emergency:",   doctorEmergencyField=new JTextField(15));
        row = addLabelAndField(mainPanel, gbc, row, "Password:",           passwordField      = new JPasswordField(15));

        // Create Button
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth=2;
        gbc.insets = new Insets(15,0,15,0);
        JButton createBtn = new JButton("Create Account");
        createBtn.setBackground(new Color(237,165,170));
        createBtn.setForeground(Color.BLACK);
        mainPanel.add(createBtn, gbc);

        createBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCreate();
            }
        });

        setVisible(true);
    }

    private int addLabelAndField(JPanel panel, GridBagConstraints gbc, int row,
                                 String labelText, JComponent field) {
        gbc.gridy = row;
        gbc.gridx = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
        return row+1;
    }

    private void handleCreate() {
        String name         = nameField.getText().trim();
        String diabetes     = diabetesField.getText().trim();
        String insulinType  = insulinTypeField.getText().trim();
        String insulinAdmin = insulinAdminField.getText().trim();
        String email        = emailField.getText().trim();
        String phone        = phoneField.getText().trim();
        String docEmail     = doctorEmailField.getText().trim();
        String docAddress   = doctorAddressField.getText().trim();
        String docEmerg     = doctorEmergencyField.getText().trim();
        String pass         = new String(passwordField.getPassword());

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name, Email, and Password are required!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create user
        User u = new User();
        u.setName(name);
        u.setDiabetesType(diabetes);
        u.setInsulinType(insulinType);
        u.setInsulinAdmin(insulinAdmin);
        u.setEmail(email);
        u.setPhone(phone);
        u.setDoctorEmail(docEmail);
        u.setDoctorAddress(docAddress);
        u.setDoctorEmergencyPhone(docEmerg);
        u.setPassword(pass);

        // Insert
        UserDAO dao = new UserDAO();
        User created = dao.createUser(u);
        if (created != null && created.getId() > 0) {
            JOptionPane.showMessageDialog(this,
                    "Account created successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new Home(created);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to create account. Try a different email or check DB logs.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
