import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.text.SimpleDateFormat;

public class database_sync {

    static String driver = "com.mysql.cj.jdbc.Driver";
    static String url = "jdbc:mysql://localhost/distributed_project";
    static String username = "distributed_project_user";
    static String password = "Rc@48g2u";
    static String url2 = "jdbc:mysql://localhost/distributed_project_backup";
    static String username2 = "distributed_project_backup_user";
    static String password2 = "oq5~hV50";
    static Connection conn, conn2;
    static boolean infinite_loop = true;
    static int back_online = 2;
    static Date date_pm, date_bk;

    public static void main(String[] args)  {

        System.out.println("\n\nDataBase Sync Node Is Now Running\n\n");
        try{
        run_database_sync();}
        catch (Exception e) {
            System.out.println(e);
        }
    }


    public static Connection run_database_sync() throws Exception {

        while (infinite_loop) {

            try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url,username,password);
            System.out.println("Primary Database: Online");
            if (back_online == 0) {
                back_online = 1;
                }
            }

            catch (Exception e) {
                System.out.println("Primary Database: Offline");
                back_online = 0;
            }

       
            try {
                Class.forName(driver);
                conn2 = DriverManager.getConnection(url2,username2,password2);
                System.out.println("Backup Database: Online");
                }
    
                catch (Exception e) {
                    System.out.println("Backup: Offline");
            }

            if (back_online == 1) {
                back_online = 2;

                System.out.println("\nDatabase Sync in process ....");
            
                //  Fixing users table

                PreparedStatement statement1 = conn.prepareStatement("SELECT * from user");
                ResultSet result1 = statement1.executeQuery();

                PreparedStatement statement2 = conn2.prepareStatement("SELECT * from user");
                ResultSet result2 = statement2.executeQuery();

                PreparedStatement statement3 = conn.prepareStatement("SELECT * from device");
                ResultSet result3 = statement3.executeQuery();

                PreparedStatement statement4 = conn2.prepareStatement("SELECT * from device");
                ResultSet result4 = statement4.executeQuery();

                PreparedStatement statement5 = conn.prepareStatement("SELECT * from gps_log");
                ResultSet result5 = statement5.executeQuery();

                PreparedStatement statement6 = conn2.prepareStatement("SELECT * from gps_log");
                ResultSet result6 = statement6.executeQuery();

                PreparedStatement statement7 = conn.prepareStatement("SELECT * from ownership");
                ResultSet result7 = statement7.executeQuery();

                PreparedStatement statement8 = conn2.prepareStatement("SELECT * from ownership");
                ResultSet result8 = statement8.executeQuery();

                ArrayList <user> user_list_pm_database = new ArrayList<user>();
                ArrayList <user> user_list_backup_database = new ArrayList<user>();
                ArrayList <device> device_list_pm_database = new ArrayList<device>();
                ArrayList <device> device_list_backup_database = new ArrayList<device>();
                ArrayList <stats> stats_list_pm_database = new ArrayList<stats>();
                ArrayList <stats> stats_list_backup_database = new ArrayList<stats>();
                ArrayList <ownership> ownership_list_pm_database = new ArrayList<ownership>();
                ArrayList <ownership> ownership_list_backup_database = new ArrayList<ownership>();

                SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");


                while (result1.next()) {
                    user_list_pm_database.add(new user(result1.getString("id"),result1.getString("username"),result1.getString("name"),
                    result1.getString("password"),result1.getString("email"),result1.getString("timestamp")));
                }

                while (result2.next()) {
                    user_list_backup_database.add(new user(result2.getString("id"),result2.getString("username"),result2.getString("name"),
                    result2.getString("password"),result2.getString("email"),result2.getString("timestamp")));
                }

                while (result3.next()) {
                    device_list_pm_database.add(new device(result3.getString("id"),result3.getString("serial_no"),result3.getString("name"),
                   result3.getString("timestamp")));
                }

                while (result4.next()) {
                    device_list_backup_database.add(new device(result4.getString("id"),result4.getString("serial_no"),result4.getString("name"),
                    result4.getString("timestamp")));
                }

                while (result5.next()) {
                    stats_list_pm_database.add(new stats(result5.getString("id"),result5.getString("device_id"),result5.getString("latitude"),
                    result5.getString("longitude"),result5.getString("speed"),result5.getString("time_stamp")));
                }
               
                while (result6.next()) {
                    stats_list_backup_database.add(new stats(result6.getString("id"),result6.getString("device_id"),result6.getString("latitude"),
                    result6.getString("longitude"),result6.getString("speed"),result6.getString("time_stamp")));
                }

                while (result7.next()) {
                    ownership_list_pm_database.add(new ownership(result7.getString("id"),result7.getString("device_id"),result7.getString("user_id"),
                    result7.getString("owner_type"), result7.getString("timestamp")));
                }

                while (result8.next()) {
                    ownership_list_backup_database.add(new ownership(result8.getString("id"),result8.getString("device_id"),result8.getString("user_id"),
                    result8.getString("owner_type"), result8.getString("timestamp")));
                }


                System.out.println("\nSyncing User Table ....");

                for (int i = 0; i < user_list_backup_database.size(); i++) {

                    date_pm = sdf.parse(user_list_pm_database.get(i).time);
                    date_bk = sdf.parse(user_list_backup_database.get(i).time);

                    if( date_bk.after(date_pm)) {
                        System.out.println("Inconsistency found in id " + user_list_pm_database.get(i).id + " for time " + user_list_pm_database.get(i).time + " . Updating with newer data recorded at time " + user_list_backup_database.get(i).time);
                        PreparedStatement statement9 = conn.prepareStatement("UPDATE user SET id = '" + user_list_backup_database.get(i).id + "', username ='" + user_list_backup_database.get(i).username + "', name ='" + user_list_backup_database.get(i).name + "', password ='" + user_list_backup_database.get(i).password + "', email ='" + user_list_backup_database.get(i).email + "', timestamp ='" + user_list_backup_database.get(i).time + "' WHERE id ='" + user_list_backup_database.get(i).id + "'");
                        statement9.executeUpdate();
                        System.out.println("Updated Inconsistency");
                    }
                }

                System.out.println("Syncing User Table Complete....");
                System.out.println("\nSyncing Device Table ....");


                for (int i = 0; i < device_list_backup_database.size(); i++) {

                    date_pm = sdf.parse(device_list_pm_database.get(i).time);
                    date_bk = sdf.parse(device_list_backup_database.get(i).time);

                    if( date_bk.after(date_pm)) {
                        System.out.println("Inconsistency found in id " + device_list_pm_database.get(i).id + " for time " + device_list_pm_database.get(i).time + " . Updating with newer data recorded at time " + device_list_backup_database.get(i).time);
                        PreparedStatement statement10 = conn.prepareStatement("UPDATE device SET id = '" + device_list_backup_database.get(i).id + "', serial_no ='" + device_list_backup_database.get(i).serial_no + "', name ='" + device_list_backup_database.get(i).name +  "', timestamp ='" + device_list_backup_database.get(i).time + "' WHERE id ='" + device_list_backup_database.get(i).id + "'");
                        statement10.executeUpdate();
                        System.out.println("Updated Inconsistency");
                    }
                }

                System.out.println("Syncing Device Table Complete....");
                System.out.println("\nSyncing GPS Log Table ....");


                for (int i = 0; i < stats_list_backup_database.size(); i++) {

                    date_pm = sdf.parse(stats_list_pm_database.get(i).time);
                    date_bk = sdf.parse(stats_list_backup_database.get(i).time);

                    if( date_bk.after(date_pm)) {
                        System.out.println("Inconsistency found in id " + stats_list_pm_database.get(i).id + " for time " + stats_list_pm_database.get(i).time + " . Updating with newer data recorded at time " + stats_list_backup_database.get(i).time);
                        PreparedStatement statement11 = conn.prepareStatement("UPDATE gps_log SET id = '" + stats_list_backup_database.get(i).id + "', device_id ='" + stats_list_backup_database.get(i).device_id + "', latitude ='" + stats_list_backup_database.get(i).lat + "', longitide ='" + stats_list_backup_database.get(i).lng + "', speed ='" + stats_list_backup_database.get(i).speed + "', timestamp ='" + stats_list_backup_database.get(i).time + "' WHERE id ='" + stats_list_backup_database.get(i).id + "'");
                        statement11.executeUpdate();
                        System.out.println("Updated Inconsistency");
                    }
                }

                
                System.out.println("Syncing GPS Log Complete....");
                System.out.println("\nSyncing Ownership Table ....");


                for (int i = 0; i < ownership_list_backup_database.size(); i++) {

                    date_pm = sdf.parse(ownership_list_pm_database.get(i).time);
                    date_bk = sdf.parse(ownership_list_backup_database.get(i).time);

                    if( date_bk.after(date_pm)) {
                        System.out.println("Inconsistency found for id = " + ownership_list_pm_database.get(i).id + " at time " + ownership_list_pm_database.get(i).time + ".\nUpdating with newer data recorded at time " + ownership_list_backup_database.get(i).time);
                        PreparedStatement statement12 = conn.prepareStatement("UPDATE ownership SET id = '" + ownership_list_backup_database.get(i).id + "', device_id ='" + ownership_list_backup_database.get(i).device_id + "', user_id ='" + ownership_list_backup_database.get(i).user_id + "', owner_type ='" + ownership_list_backup_database.get(i).owner_type + "', timestamp ='" + ownership_list_backup_database.get(i).time + "' WHERE id ='" + ownership_list_backup_database.get(i).id + "'");
                        statement12.executeUpdate();
                        System.out.println("Updated Inconsistency");
                    }
                }

                System.out.println("\nPrimary Database has been completely synced!");

            }































            System.out.println("");
            TimeUnit.SECONDS.sleep(2);

        }

        return conn;

    }

}