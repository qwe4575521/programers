#!/bin/sh
cd /$HOME/app
java -jar -Xms512m -Xmx512m file-sync.jar 2 &
