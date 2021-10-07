#!/bin/bash

NETID=hxc200011
FILES="./TestProj ./launch"
TARGET=/
#FILES=./TestProj/bin/Node.class
#TARGET=/TestProj/bin/

scp -r $FILES $NETID@dc01:~$TARGET
scp -r $FILES $NETID@dc03:~$TARGET
scp -r $FILES $NETID@dc04:~$TARGET
scp -r $FILES $NETID@dc05:~$TARGET
scp -r $FILES $NETID@dc06:~$TARGET
scp -r $FILES $NETID@dc07:~$TARGET
scp -r $FILES $NETID@dc08:~$TARGET
scp -r $FILES $NETID@dc09:~$TARGET
