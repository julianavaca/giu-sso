#!/bin/bash

APP=$1  #App name
ENV=$2  #Environment to deploy
HOST=$3  #Host for connect
SRC=$4   #Src for getting the code
PORT=$5  #Port to deploy

USAGE="Usage: <app> <environment> <host> <src> <port>";

DEST_DIR="/home/$ENV/$APP";
SRC_DIR="$SRC/$APP";

usage (){
    if [ $# -ne 5 ] ; then
        echo $USAGE
        exit 1;
    fi
}

copy(){
    echo "Creating destination folder...";
    ssh -t -t $1 "mkdir -p $3";
    echo "Destination folder created successfully...";
    echo "Cleaning working directory...";
    ssh -t -t $1 "rm -rf $3/*";
    echo "Working directory cleaned successfully...";
    echo "Coping files to deploy...";
    scp $2/* $1:$3;
    echo "Files copied successfully...";
}

create(){
    echo "Attempt to remove old docker container...";
    removeContainer $1 $3-$2
    echo "Attempt to remove old docker image...";
    removeImage $1 $3/$2
    ssh $1 "docker build -t $3/$2 $4";
}

run(){
    ssh $1 "docker run -e \"SPRING_PROFILES_ACTIVE=$2\" --net=\"host\" --name=$2-$3 -d -p $4:$4 -v /soft/newrelic:/newrelic -v /home/$2/$3/log:/log $2/$3";
}

removeContainer(){
    echo "Checking if docker container exists";
    ssh $1 "if docker ps -a | grep -q $2; then exit 0; else exit 1; fi"
    if [ $? -eq 0 ] ; then
        echo "Container exists removing it...";
        ssh $1 "docker rm -f $2"
        echo "Container removed successfully...";
    else
        echo "Container non exists...";
    fi
}

removeImage(){
    echo "Checking if docker image exists";
    ssh $1 "if docker images | grep -q $2; then exit 0; else exit 1; fi"
    if [ $? -eq 0 ] ; then
        echo "Image exists removing it...";
        ssh $1 "docker rmi -f $2"
        echo "Image removed successfully...";
    else
        echo "Image non exists...";
    fi
}

test(){
    echo "Checking if docker container exists";
    ssh $1 "if docker ps | grep -q $2; then exit 0; else exit 1; fi"
    if [ $? -eq 1 ] ; then
        echo "[ERROR] Container is not running exists!!!";
        exit 1;
    else
        echo "[SUCCESS] Container running";
    fi
}

#Begin deploy
usage $APP $HOST $ENV $SRC $PORT;
echo "Attempt to deploy $APP to $HOST, environment: $ENV and source: $SRC/$APP";
echo "Attempt to copy files";
copy $HOST $SRC_DIR $DEST_DIR;
echo "Attempt to create docker";
create $HOST $APP $ENV $DEST_DIR;
echo "Attempt to run docker";
run $HOST $ENV $APP $PORT;
echo "Testing deploy";
test $HOST $APP;
echo "Deploy finished successfully";
#Finish