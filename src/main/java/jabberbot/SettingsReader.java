/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jabberbot;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * @author Admin
 */
public class SettingsReader {

    public String DBConnectionString;
    public String dbLogin;
    public String dbPassword;
    public String jabberServerAddress;
    public String jabberServerPort;
    public String jabberServerLogin;
    public String jabberServerPassword;

    private String extractValue(String baseString) {
        return
                baseString.substring(baseString.indexOf("=") + 1,
                        baseString.length()).trim();
    }
    
    
    /*
     * 
     * Setting file example
     * DBConnectionString = jdbc:mysql://5.166.154.203:3306/Jabber;
     * dbLogin = root
     * dbPassword = 18021955
     * jabberServerAddress = 5.166.154.203
     * jabberServerPort = 5222
     * jabberServerLogin = petrov
     * jabberServerPassword = gay
     */

    public void ReadSettings(String filename) {
        try {
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            //Read File Line By Line

            DBConnectionString = extractValue(br.readLine());
            dbLogin = extractValue(br.readLine());
            dbPassword = extractValue(br.readLine());
            jabberServerAddress = extractValue(br.readLine());
            jabberServerPort = extractValue(br.readLine());
            jabberServerLogin = extractValue(br.readLine());
            jabberServerPassword = extractValue(br.readLine());

            in.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

    }

}
