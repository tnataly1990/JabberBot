/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jabberbot;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Admin
 */
public class JabberBot {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SettingsReader reader = new SettingsReader();
        reader.ReadSettings("Settings.txt");

        System.out.println("Jabber Bot. (c) RSREU 2013");

        BotThread bot = new BotThread(reader);
        bot.startBot();

        System.setErr(new PrintStream(new OutputStream() {
            public void write(int b) {
            }
        }));


        try {
            System.out.println("Press enter key to exit.");
            System.in.read();

        } catch (IOException ex) {
            Logger.getLogger(JabberBot.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
