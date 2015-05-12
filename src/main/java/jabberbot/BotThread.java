/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jabberbot;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jabberbot.Commands.JOKE;
import static jabberbot.Commands.MONTH;
import static jabberbot.Commands.TIME;

/**
 * @author Admin
 */
public class BotThread implements Runnable {

    private JBot bot;
    private SettingsReader reader;
    Thread t = null;

    public BotThread(SettingsReader reader) {
        this.reader = reader;
    }

    public void startBot() {
        t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    public void run() {
        bot = new JBot();
        try {
            bot.start(reader.jabberServerLogin, reader.jabberServerPassword,
                    reader.jabberServerAddress, reader.jabberServerPort,
                    reader.DBConnectionString, reader.dbLogin,
                    reader.dbPassword);
        } catch (Exception ex) {
            Logger.getLogger(JabberBot.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Can't start bot. Sorry");
        }

        XMPPConnection connection = bot.getConnection();
        PacketFilter filter = new AndFilter(new PacketTypeFilter(Message.class));

        PacketListener myListener = new PacketListener() {
            public void processPacket(Packet packet) {
                if (packet instanceof Message) {
                    Message message = (Message) packet;
                    processMessage(message);
                }
            }
        };

        connection.addPacketListener(myListener, filter);


        while (connection.isConnected()) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }
        }

    }

    private void processMessage(Message message) {
        String messageBody = message.getBody();
        String JID = message.getFrom();
        sendMessage(JID, messageBody);
    }

    private void sendMessage(String to, String message) {
        String name = to.substring(0, to.indexOf("/"));
        XMPPConnection connection = bot.getConnection();
        if (!message.equals("")) {
            ChatManager chatmanager = connection.getChatManager();
            Chat newChat = chatmanager.createChat(to, null);
            try {
                int userId = bot.getUserId(name);
                if (userId >= 0) {
                    newChat.sendMessage(getResponse(message, userId));
                } else {
                    System.out.println("No data for this user.");
                }
            } catch (XMPPException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private String getResponse(String command, int userId) {
        if (command.equals(TIME.getCommand())) {
            return bot.getTimeForDay(userId);
        } else if (command.equals(MONTH.getCommand())) {
            return bot.getTimeForMonth(userId);
        } else if (command.equals(JOKE.getCommand())) {
            return "petrov gay!!!";
        } else {
            return "time: show time, worked today \n "
                    + "month: show time worked at this month \n"
                    + "other commands will call help message.";
        }
    }
}
