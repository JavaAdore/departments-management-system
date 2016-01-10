/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package departments.management.system;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author orcl
 */
public class DataSource {

    private static Connection conn;

    static void closeConnection() {
        if(conn !=null)
        {
            try {
                conn.close();
            } catch (Exception e) {
            }
        }
    }

    private DataSource() {
    }

    public static Connection initiateConnection() {
        try {
            if (conn == null || conn.isClosed()) {

                Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
           String database = new File( new File(DataSource.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent() + File.separator + "UnitsManamementSystem.accdb").getPath();

               
                String url = "jdbc:ucanaccess://" + database;

                // specify url, username, pasword - make sure these are valid 
                conn = DriverManager.getConnection(url, "", "");

                System.out.println("Connection Succesfull");

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());

        }

        return conn;
    }

}
