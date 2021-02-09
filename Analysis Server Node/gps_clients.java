import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;
import java.util.*; 
import java.io.*;


public class gps_clients implements MqttCallback {

	
	private final IMqttClient client;
	String driver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://localhost/distributed_project";
	String username = "distributed_project_user";
	String password = "Rc@48g2u";
	static Connection conn;
    
    public gps_clients(final IMqttClient client) {
		this.client = client;
		client.setCallback(this);
		System.out.println("\n\nMyTrack Analysis Server Now Running \n\n");
		pub("dis_sys_project/main_server","Server_Online");
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url,username,password);
		}
		catch (Exception e) {
    		System.out.println("Cannot connect to database: " + e);
    	}
	}

    @Override
    public void messageArrived(String topic, MqttMessage message) {
		if (topic.equalsIgnoreCase("dis_sys_project/main_check_connection")){

			pub("dis_sys_project/main_server","Server_Online");

		}	
		else {
			
			try {
				getConnection(topic,message.toString());
			}
			catch (Exception e) {
				System.out.println(e);
			}
		}
    }
    
    public void subscribe(String topicFilter, int qos) throws MqttException {
        	
    	client.subscribe(topicFilter,qos);
    	
    }

	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}

	public void pub(String topic, String message1) {
            	
    	MqttMessage message = new MqttMessage();
    	message.setPayload(message1.getBytes());
		
    	try {
			client.publish(topic,message);

		} catch (MqttException e) {
			e.printStackTrace();
		}
    }
	
	public static Connection getConnection(String topic, String message) throws Exception {
		
		String[] topic_split = topic.split("\\/");
		String[] message_split = message.split("\\s+");
		String device_id = "";
		String device_name = "";
		String email="";

		PreparedStatement statement = conn.prepareStatement("SELECT * from device where serial_no = '" + topic_split[1] + "'");
    	ResultSet result = statement.executeQuery();
		while (result.next()) {
			device_id = result.getString("id");
			device_name = result.getString("name");
			}
		
		Date date = new Date();
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		date = new Timestamp(date.getTime());

		try {

		if (topic_split[2].equalsIgnoreCase("gps")) {
			PreparedStatement statement2 = conn.prepareStatement("INSERT into gps_log (id,device_id,latitude,longitude,speed,time_stamp) VALUES (NULL,'" +
			device_id + "','" + message_split[0] + "','" + message_split[1] + "','" + message_split[2] + "','" + date.toString() +"')");
			statement2.executeUpdate();
			System.out.println("Adding log to database : " + device_id + " " + message_split[0] + " " + message_split[1] + " " + message_split[2] + " " + date.toString());
		}

		else if(topic_split[2].equalsIgnoreCase("get_stats")){

			System.out.println("GPS requests for stats ....");
			System.out.println("Starting Analysis ...");

			ArrayList <stats> list = new ArrayList<stats>();
			PreparedStatement statement3 = conn.prepareStatement("SELECT * from gps_log where device_id = '" + device_id + "'");
    		ResultSet result3 = statement3.executeQuery();
			while (result3.next()) {
				list.add(new stats(result3.getString("id"),result3.getString("device_id"),result3.getString("latitude"),
					result3.getString("longitude"),result3.getString("speed"),result3.getString("time_stamp")));
			}

			int hard_acceleration_counter = 0;
			int hard_breaking_counter = 0;
			
			for (int i = 1 ; i < list.size(); i++) {
				Double a = ((Double.parseDouble(list.get(i).speed)/3.6) - (Double.parseDouble(list.get(i-1).speed)/3.6));
				if (a < -1.5) {
					hard_breaking_counter = hard_breaking_counter + 1;
				}
				if (a > 1.5) {
					hard_acceleration_counter = hard_acceleration_counter + 1;
				}
			}

			System.out.println("Analysis Complete");
			System.out.println("Hard Acceleration: " + hard_acceleration_counter);
			System.out.println("Hard Breaking: " + hard_breaking_counter);
			System.out.println("Sending Email to User .....");
			
			Process p = Runtime.getRuntime().exec("python3 sendemail.py "+ hard_acceleration_counter +  " " + hard_breaking_counter + " " + list.size() + " " + device_name);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream())); 
			
			System.out.println("Email Send");
		}

	}
		catch (Exception e) {
				System.out.print("");
		}

		return conn;
	}
}