#!/bin/bash
#Downloads an App Engine Datastore to CSV
 
 
APPLICATION=$1
CONFIG=$2
URL=$3

DATE=$(date +"%Y-%m-%d - %r");

echo "DATE: $DATE"
mkdir "archives"
mkdir "archives/$DATE"
mkdir "archives/$DATE/logs"

count=4
until [ "$*" = "" ]
do
  KIND=$4
  if test "$KIND" != ""
	then
		 echo "Kind: $KIND"	
		 appcfg.py download_data \
		 	--config_file=$CONFIG \
		 	--url=$URL --kind=$KIND \
		 	--email=ssaammee@gmail.com \
		 	--filename="archives/$DATE/$KIND.csv" \
		 	--application=$APPLICATION  \
		 	--rps_limit=1000\
		 	--http_limit=1000 \
		 	--log_file="archives/$DATE/logs/log.txt" \
		 	--result_db_filename="archives/$DATE/logs/results.txt" \
		 	--db_filename=skip
	fi
  shift
  count=`expr $count + 1`
done
