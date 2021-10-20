#!/bin/bash

# Change this to your netid
netid=cme150030

# Root directory of your project
PROJDIR=/home/012/c/cm/cme150030/cs6378

# Directory where the config file is located on your local system
CONFIGLOCAL=$HOME/IdeaProjects/CS3678.001-Project1/src/config.txt

# Directory your java classes are in
BINDIR=$PROJDIR/bin

# Your main project class
PROG=Main

n=0

#./dist.sh
./cleanup.sh

echo ""
echo "Activate servers"
cat $CONFIGLOCAL | sed -e "s/#.*//" | sed -e "/^\s*$/d" | sed -e "s/\r$//" |
(
    read i
    while [[ $n -lt $i ]] ;
    do
    	read line
    	p=$( echo $line | awk '{ print $1 }' )
        host=$( echo $line | awk '{ print $2 }' )
	
	echo "Start $host"
	ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $netid@$host java -cp $BINDIR $PROG $p $CONFIGLOCAL > $host&

        n=$(( n + 1 ))
    done
)
echo "Activation complete"

tail -f dc*
