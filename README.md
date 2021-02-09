# MyTrack

<img src="https://allansantosh.com/DistributedProject/mytrack.png" width="250" height="250"/>

## Introduction
Statistics show major cities like New York have had their auto theft rate increase by 60% in the year 2020. Most cars manufactured prior to 2010 lack the capability to track it’s GPS location if stolen. Our project was to create a compact GPS tracker that enables you to view the current location of the car from your smartphone device. 



## Project Structure

![picture alt](https://allansantosh.com/DistributedProject/dis_pro_structure.png "System Structure")


 ## Demo
 
 [![Watch the demo video](https://allansantosh.com/DistributedProject/dis_pro_Demo.png "Demo Video on Google Drive")](https://drive.google.com/file/d/1DqHj3wBf8m6oKxlSw5wjeBVqq63zad9v/view?usp=sharing)
 
 ## Installation Instructions
 
 ### Mobile App
 
 * Download the Project folder `Mobile App/MyTrack` and open it with Android Studio.
 * Compile and Run the Project on a Simulator or your own Raspberry Pi Device.
 * Use login username `freddy` and password `qwe` to login to the App.
 * Another login is username `allansantosh` and password `qwerty1` to login to the App.

 ### Data Analysis Node
 
 * Download the contents of the folder `Analysis Server Node`
 * Set Class Path. If using Linux use the following command
 `export CLASSPATH=$CLASSPATH:/pathto/file/org.eclipse.paho.client.mqttv3-1.2.5.jar:/pathto/file/mysql-connector-java-8.0.21.jar`
 * In terminal complile the java file by typing `javac main_server.java`
 * Launch program by typing `java main_server`
 
  ### Database Synchronization Node
 
 * Download the contents of the folder `Database Sync Node`
 * Set Class Path. If using Linux use the following command
 `export CLASSPATH=$CLASSPATH:/pathto/file/mysql-connector-java-8.0.21.jar`
 * In terminal complile the java file by typing `javac database_sync.java`
 * Launch program by typing `java database_sync`
 
  ### Database
 
 * The sample SQL file has been provided in the folder `Primary & Failover Database SQL`
 * The Databases are currently stored in a VPS and will be left Online till end of the month.
 * However if you want to simulate this locally, everywhere you see connection addrees to VPS IP address, change it to localhost.

  ### Raspberry PI GPS Tracker
 
 * Download the contents of the folder `Raspberry Pi (GPS Tracker)` into a Raspberry Pi
 * Run the python code like this `python3 tracker.py`
 * If you have a GPS sensor then connect it. Otherwise App will just say `Device Online` and will NOT show GPS coordinates. It may also show `GPS Malfunction` message indicating something is wrong with your GPS tracker (Raspberry Pi).
