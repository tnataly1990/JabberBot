/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jabberbot;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Presence;

import java.sql.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;


/**
 * @author Admin
 */
public class JBot implements RosterListener {

    private XMPPConnection connection;
    private Roster roster;
    private HashSet onlineSet = new HashSet();
    private Connection dbconnection = null;
    private java.text.SimpleDateFormat sdf;

    public void start(String userName, String password, String jabberServerAddress, String jabberServerPort,
                      String dbConnectionString, String dbLogin, String dbPassword) throws Exception {
        ConnectionConfiguration config = new ConnectionConfiguration(jabberServerAddress, Integer.parseInt(jabberServerPort), "IamHelloKittyBot");
        connection = new XMPPConnection(config);
        connection.connect();
        connection.login(userName, password);
        System.out.println("Jabber Server Connection: OK!");
        roster = connection.getRoster();
        sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        connectToDB(dbConnectionString, dbLogin, dbPassword);
        updateOnlineEntriesSet();
        roster.addRosterListener(this);
    }

    public XMPPConnection getConnection() {
        return connection;
    }

    public String getTimeForMonth(int user) {
        String SELECT_MOD_SQL = "SELECT l2 as l3, round((SUM(time_worked))/60) as summed FROM\n"
                + "(\n"
                + "SELECT l1 as l2, \n"
                + "TIME_TO_SEC(TIMEDIFF(time_off, time_on)) AS time_worked\n"
                + "FROM h2 where time_on is not null and time_on > DATE_FORMAT(now(),'%X-%m-01 00:00')) as t\n"
                + "group by l2 having l3=?";
        PreparedStatement selectIdByLoginStmt = null;
        try {
            selectIdByLoginStmt = dbconnection.prepareStatement(SELECT_MOD_SQL);
            selectIdByLoginStmt.setInt(1, user);
            selectIdByLoginStmt.execute();
            ResultSet result = selectIdByLoginStmt.getResultSet();
            if (result.next()) {
                int time = result.getInt(2);
                return "Today worked " + time/60/8+ " days " + (time - (60*8)*(time/60/8))/60 + " hours.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Cant get data from database for this user";
    }

    public String getTimeForDay(int user) {
        String SELECT_MOD_SQL = "SELECT l2 as l3, round((SUM(time_worked))/60) as summed FROM\n"
                + "(\n"
                + "SELECT l1 as l2, \n"
                + "TIME_TO_SEC(TIMEDIFF(time_off, time_on)) AS time_worked\n"
                + "FROM h2 where time_on is not null and time_on > DATE_FORMAT(now(),'%X-%m-%d 00:00')) as t\n"
                + "group by l2 having l3=?";
        PreparedStatement selectIdByLoginStmt = null;
        try {
            selectIdByLoginStmt = dbconnection.prepareStatement(SELECT_MOD_SQL);
            selectIdByLoginStmt.setInt(1, user);
            selectIdByLoginStmt.execute();
            ResultSet result = selectIdByLoginStmt.getResultSet();
            if (result.next()) {
                int time = result.getInt(2);
                return "Today worked " + time/60 + " hours " + time%60 + " minutes.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Cant get data from database for this user";
    }

    public int getUserId(String name) {
        PreparedStatement selectIdByLoginStmt = null;
        try {
            selectIdByLoginStmt = dbconnection.prepareStatement("select id from users where login = ?");
            selectIdByLoginStmt.setString(1, name);
            selectIdByLoginStmt.execute();
            ResultSet result = selectIdByLoginStmt.getResultSet();
            int loginId = -1;
            if (result.next()) {
                loginId = result.getInt(1);
            }
            return loginId;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void connectToDB(String dbConnectionString, String dbLogin, String dbPassword) {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            dbconnection = java.sql.DriverManager.getConnection(dbConnectionString,
                    dbLogin, dbPassword);
            System.out.println("DB Connection: OK!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateOnlineEntriesSet() {
        Collection<RosterEntry> entries = roster.getEntries();
        for (RosterEntry r : entries) {
            if (roster.getPresence(r.getUser()).isAvailable()) {
                onlineSet.add(r.getUser());
            }
        }
    }

    public void entriesAdded(Collection<String> collection) {
    }

    public void entriesUpdated(Collection<String> collection) {
    }

    public void entriesDeleted(Collection<String> collection) {
    }

    public void presenceChanged(Presence prsnc) {
        String name = prsnc.getFrom().substring(0, prsnc.getFrom().indexOf("/"));
        int res = roster.getPresence(prsnc.getFrom()).isAvailable() ? 1 : 0;
        if (res == 1) {
            if (onlineSet.contains(name)) return;
            else
                onlineSet.add(name);
        } else
            onlineSet.remove(name);
        PreparedStatement selectIdByLoginStmt = null;
        PreparedStatement insertIntoHistoryStmt = null;
        PreparedStatement insertIntoUserStmt = null;
        try {
            int loginId =  getUserId(name);
            if (loginId >= 0) {
                insertIntoHistoryStmt = dbconnection.prepareStatement("insert into history (user, status, time) values (?, ?, ?)");
                insertIntoHistoryStmt.setInt(1, loginId);
                insertIntoHistoryStmt.setString(2, Integer.toString(res));
                insertIntoHistoryStmt.setString(3, sdf.format(Calendar.getInstance().getTime()));
                insertIntoHistoryStmt.execute();
            } else {
                insertIntoUserStmt = dbconnection.prepareStatement("insert into users (login) values (?)");
                insertIntoUserStmt.setString(1, name);
                insertIntoUserStmt.execute();
            }
            if (res == 0) {
                System.out.println("User " + name + " has gone offline at "
                        + sdf.format(Calendar.getInstance().getTime()));
            } else {
                System.out.println("User " + name + " has gone online at "
                        + sdf.format(Calendar.getInstance().getTime()));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                selectIdByLoginStmt.close();
                insertIntoHistoryStmt.close();
                insertIntoUserStmt.close();
                dbconnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
}
