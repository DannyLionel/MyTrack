import asyncio
from contextlib import AsyncExitStack, asynccontextmanager
from random import randrange
from asyncio_mqtt import Client, MqttError
import serial
import paho.mqtt.client as mqtt
import time
import string
import pynmea2
import datetime


async def advanced_example():
    # We 💛 context managers. Let's create a stack to help
    # us manage them.
    async with AsyncExitStack() as stack:
        # Keep track of the asyncio tasks that we create, so that
        # we can cancel them on exit
        tasks = set()
        stack.push_async_callback(cancel_tasks, tasks)

        # Connect to the MQTT broker
        client = Client("144.217.242.17")
        await stack.enter_async_context(client)
        #client.will_set(serial_no+"/connection","Offline",1,retain=False)


        # Messages that doesn't match a filter will get logged here
        messages = await stack.enter_async_context(client.unfiltered_messages())
        task = asyncio.create_task(log_messages(client, messages))
        tasks.add(task)

        # Subscribe to topic(s)
        # 🤔 Note that we subscribe *after* starting the message
        # loggers. Otherwise, we may miss retained messages.
        await client.subscribe(serial_no+"/check_connection")


        task = asyncio.create_task(publish(client, "connection","Online"))
        tasks.add(task)
        task = asyncio.create_task(post_gps(client, "gps"))
        tasks.add(task)

        # Wait for everything to complete (or fail due to, e.g., network
        # errors)
        await asyncio.gather(*tasks)

async def publish(client, topic, message):
    await client.publish(serial_no+"/"+topic, message, qos=1)
    await asyncio.sleep(1)

async def post_gps(client, topic):
    gps_connected_false_counter = 0
    pn = 0
    port="/dev/ttyACM" + str(pn)
    while True:
        try:
            ser=serial.Serial(port, baudrate=9600, timeout=0.5)
            dataout = pynmea2.NMEAStreamReader()
            newdata=ser.readline()
            try:
                gps_connected_false_counter = 0
                newmsg=pynmea2.parse(newdata.decode("utf-8"))
                lat=round(newmsg.latitude,6)
                lng=round(newmsg.longitude,6)
                speed=round(newmsg.spd_over_grnd * 1.60934,2)
                now = datetime.datetime.now()
                await client.publish(serial_no+"/"+topic, str(lat) + " " + str(lng) + " " + str(speed), qos=1)
                
            except Exception as e: 
                    print("Got wrong line")
        except Exception as ee:
            gps_connected_false_counter = gps_connected_false_counter + 1
            if (pn == 10):
                pn = -1
            pn = pn + 1
            port="/dev/ttyACM" + str(pn)
            if (gps_connected_false_counter == 1):
                print("GPS Disconnected from device")
                await client.publish(serial_no+"/"+topic, "GPS_Error", qos=1)              
       
        await asyncio.sleep(0.75)
       # await asyncio.sleep(0.5)

async def log_messages(client, messages):
    async for message in messages:
        # 🤔 Note that we assume that the message paylod is an
        # UTF8-encoded string (hence the `bytes.decode` call).
        #print(message.payload.decode())
        m = message.payload.decode()
        if m == "check connection now":
            asyncio.create_task(publish(client, "connection","Online"))


async def cancel_tasks(tasks):
    for task in tasks:
        if task.done():
            continue
        task.cancel()
        try:
            await task
        except asyncio.CancelledError:
            pass

async def main():
    # Run the advanced_example indefinitely. Reconnect automatically
    # if the connection is lost.
    reconnect_interval = 3  # [seconds]
    while True:
        try:
            await advanced_example()
        except MqttError as error:
            print(f'Error "{error}". Reconnecting in {reconnect_interval} seconds.')
        finally:
            await asyncio.sleep(reconnect_interval)

def on_connect(client, userdata, flags, rc):
    print ("Connected to Server")
    client.publish(serial_no+"/connection","Online")
    client.subscribe(serial_no+"/check_connection") 


#serial_no = "MKJI29380"
serial_no = "dis_sys_project/"+"MKJI96514"
try:
    asyncio.run(main())
except KeyboardInterrupt:
    client1 = mqtt.Client("P1") 
    client1.connect("144.217.242.17")
    client1.on_connect = on_connect
    client1.publish(serial_no+"/connection","Offline")