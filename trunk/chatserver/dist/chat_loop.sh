#!/bin/bash

err=1
until [ $err == 0 ];
do
	java -Xmx512m -cp ./libs/*:aion-knight_chat.jar chatserver.ChatServer > log/stdout.log 2>&1
	err=$?
	sleep 10
done