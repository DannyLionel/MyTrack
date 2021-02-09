import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import java.util.UUID;

public class main_server {

    public static void main(String[] args) throws MqttException, InterruptedException {
    
        final String publisherId = UUID.randomUUID().toString();
        final IMqttClient mqttClient = new MqttClient("tcp://" + "localhost" + ":1883", publisherId);
        final MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(0);
        options.setKeepAliveInterval(2);
        options.setWill("dis_sys_project/main_server","Server_Offline".getBytes(), 2, true); //#set will
        mqttClient.connect(options);

        final gps_clients gps_clients = new gps_clients(mqttClient);
           
        gps_clients.subscribe("dis_sys_project/#",0);
    }
}