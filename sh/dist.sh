#!/bin/bash

NETID=cme150030
CONFIGLOCAL=$HOME/IdeaProjects/CS3678.001-Project1/src/config.txt
FILES="./TestProj ./launch"
TARGET=/
#FILES=./TestProj/bin/Node.class
#TARGET=/TestProj/bin/

n=0

echo "Sync program files"
cat $CONFIGLOCAL | sed -e "s/#.*//" | sed -e "/^\s*$/d" | sed -e "s/\r$//" |
(
    read i
    while [[ $n -lt $i ]] ;
    do
    	read line
      host=$( echo $line | awk '{ print $2 }' )

	    echo "Sync $host"
      #scp -r $FILES $NETID@$host:~$TARGET
      rsync -avzhP $FILES $NETID@$host:~$TARGET

      n=$(( n + 1 ))
    done
)
echo "Sync complete"
