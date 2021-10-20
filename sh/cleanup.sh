#!/bin/bash


# Change this to your netid
netid=cme150030

#
# Root directory of your project
PROJDIR=/home/012/c/cm/cme150030/cs6378

#
# Directory where the config file is located on your local system
CONFIGLOCAL=$HOME/launch/config.txt

n=0

echo "Cleanup start"
cat $CONFIGLOCAL | sed -e "s/#.*//" | sed -e "/^\s*$/d" | sed -e "s/\r$//" |
(
    read i
    while [[ $n -lt $i ]]
    do
    	read line
        host=$( echo $line | awk '{ print $2 }' )

        echo "Cleanup $host"
        ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $netid@$host pkill -u $netid java &
        sleep 1

        n=$(( n + 1 ))
    done
   
)
echo "Cleanup complete"
