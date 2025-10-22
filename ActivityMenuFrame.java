
package za.ac.tut.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Gadifele Parsons Radingoanis
   Student NO : 241265129
 */
public class ActivityMenuFrame extends JFrame {
    
    private JPanel userNamePnl;
    private JPanel passwordPnl;
    private JPanel headingPnl;
    private JPanel buttonPnl;
    private JPanel mainPnl;
    
    private JLabel userNameLbl;
    private JLabel passwordLbl;
    private JLabel headingLbl;
    
    private JTextField userNameTxt;
    private JTextField passwordTxt;
    
    private JButton logIn;
    private JButton cancel;
    
    private JMenuBar menuBar;
    private JMenu fileMenu;
    
    private JMenuItem openFile;
    private JMenuItem exit;    
    
    public ActivityMenuFrame() {
        setTitle("REGISTER");
        setSize(50, 100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        //create menu bar
        menuBar = new JMenuBar();
        
        //create menu
        fileMenu = new JMenu("File");
        
        //create menu items
        openFile = new JMenuItem("Open File...");
        openFile.addActionListener(new chooseFileToLogin());
        
        exit = new JMenuItem("Exit.");
        exit.addActionListener(new exitFile());
        
        // Add menu items to menu
        fileMenu.add(openFile);        
        fileMenu.add(exit);
        
        //add menu to the menu bar
        menuBar.add(fileMenu);
        
        // Panels
        userNamePnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passwordPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headingPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));       
        buttonPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
        mainPnl = new JPanel(new BorderLayout());
        
        // Labels
        userNameLbl = new JLabel("Username : ");
        passwordLbl = new JLabel("Password : ");
        
        headingLbl = new JLabel("LOGIN DETAILS");
        headingLbl.setForeground(Color.BLUE);
        headingLbl.setFont(new Font("SERIF", Font.BOLD + Font.ITALIC, 30));
        headingLbl.setBorder(new BevelBorder(BevelBorder.RAISED));
        
        // Buttons
        logIn = new JButton("LogIn");
        logIn.addActionListener(new saveToDatabase());
        
        //logIn.addActionListener(new clearAfterLoginFields());
                
        cancel = new JButton("Cancel");
        cancel.addActionListener(new clearFields());
        // TextFields
        userNameTxt = new JTextField(15);
        passwordTxt = new JTextField(15);
        
        // Add to panels
        userNamePnl.add(userNameLbl);
        userNamePnl.add(userNameTxt);
        
        passwordPnl.add(passwordLbl);
        passwordPnl.add(passwordTxt);
        
        buttonPnl.add(logIn);
        buttonPnl.add(cancel);
        
        // Main panels
        headingPnl.add(headingLbl);
        
        mainPnl.add(headingPnl, BorderLayout.NORTH);
        mainPnl.add(userNamePnl, BorderLayout.WEST);
        mainPnl.add(passwordPnl, BorderLayout.EAST);
        mainPnl.add(buttonPnl, BorderLayout.SOUTH);
        
        //add the menu bar to the frame
        setJMenuBar(menuBar);
        
        // Add to JFrame panel
        add(mainPnl);
        
        pack();
        
        //set resizable
        setResizable(false);
        
        //visible
        setVisible(true);
    }
    
    private class clearAfterLoginFields implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent ae) {
            String username = userNameTxt.getText();
            String password = passwordTxt.getText();
            
            if(username.isEmpty() || password.isEmpty()){
                JOptionPane.showMessageDialog(null, "Please enter both username and password");
            } else{
                JOptionPane.showMessageDialog(null, "Login succesful.");
            }
            
            // Clear both fields
            userNameTxt.setText("");
            passwordTxt.setText("");
        }
        
    }
    
    private class chooseFileToLogin implements ActionListener{
               
        @Override
        public void actionPerformed(ActionEvent ae) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(null);
            
            if(result == JFileChooser.APPROVE_OPTION){
                
                File file = fileChooser.getSelectedFile();
                
                try (BufferedReader br = new BufferedReader(new FileReader(file))){
                    StringBuilder content = new StringBuilder();
                    String line;
                    
                    if((line = br.readLine()) != null){
                        String[] parts = line.split(",");
                        
                         if(parts.length >= 2) {
                                userNameTxt.setText(parts[0].trim());
                                passwordTxt.setText(parts[1].trim());
                                
                           } else {
                                JOptionPane.showMessageDialog(null, "Invalid file format. Use: username,password");
                            }
                         
                        } else {
                            JOptionPane.showMessageDialog(null, "File is empty!");
                        }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error reading file: " + ex.getMessage());
                }
                    
            }
        }
        
    }
        
    private class saveToDatabase implements ActionListener {

       @Override
       public void actionPerformed(ActionEvent ae) {
           String username = userNameTxt.getText().trim();
           String password = passwordTxt.getText().trim();

           if (username.isEmpty() || password.isEmpty()) {
               JOptionPane.showMessageDialog(null, "Please enter both username and password");
               return;
           }
           
           Connection conn = null;
           PreparedStatement pst = null;
           
            try {
                
                   conn = DriverManager.getConnection("jdbc:derby://localhost:1527/LogInDetailsDB", "app", "app");

                   pst = conn.prepareStatement("INSERT INTO LOGINDETAILS (USERNAME, PASSWORD, LOGIN_TIME) VALUES (?, ?, ?)");
                   pst.setString(1, username);
                   pst.setString(2, password);
                   pst.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

                   pst.executeUpdate();

                   JOptionPane.showMessageDialog(null, "Login successful! Welcome " + username);

                    // Clear fields AFTER successful login
                    userNameTxt.setText("");
                    passwordTxt.setText("");
                
                 } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                }
       }
   }


       private class clearFields implements ActionListener{

           @Override
           public void actionPerformed(ActionEvent ae) {
               // Clear both fields
               userNameTxt.setText("");
               passwordTxt.setText("");
           }

       }

       private class exitFile implements ActionListener{

           @Override
           public void actionPerformed(ActionEvent ae) {
               System.exit(0);
           }

       }

   }

