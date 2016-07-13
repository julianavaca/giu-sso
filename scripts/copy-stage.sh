#!/bin/bash

SRC_FOLDER=$1
DEST_FOLDER=$2

USAGE="Usage: <from> <to>"

usage() {
    if [ "$#" -ne 2 ]; then
        echo $USAGE;
        exit 1;
    fi
}

copy() {
    mkdir -p $2;
    cp -r $1/* $2;
}

test() {
    if find $1 -maxdepth 0 -empty | read v;
        then exit 1;
    fi
}

#Begin copy
usage $SRC_FOLDER $DEST_FOLDER;
echo "Attempt to copy files";
copy $SRC_FOLDER $DEST_FOLDER;
echo "Testing copy";
test $DEST_FOLDER;
echo "Copy finished successfully";