#!/bin/bash

SERVICE="producer"

if [ "$1" == "" ]; then
    docker run --rm -dit --name $SERVICE soprasteria/$SERVICE
else
    docker stop $SERVICE
fi
