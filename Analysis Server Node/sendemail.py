import requests
import sys

userdata = {"ha": sys.argv[1], "hb": sys.argv[2], "time": sys.argv[3], "name": sys.argv[4], "name_l": sys.argv[5]}
url = "https://allansantosh.com/DistributedProject/phpmail.php"
r = requests.post(url,params=userdata)