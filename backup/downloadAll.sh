#Download All Data

URL="https://backup.4sqbrands.appspot.com/remote_api"
APPLICATION="4sqbrands"
CONFIG="config.yml"
EMAIL="apps@handstandtech.com"

DATE=$(date +"%Y-%m-%d - %H.%M.%OS");	

echo "DATE: $DATE"
mkdir "archives"
mkdir "archives/$DATE"
mkdir "archives/$DATE/logs"

downloadData()
{
        #Set KIND to be value of 1st Parameter
		KIND=$1
		
		#Run Download Data Command
		appcfg.py download_data \
		 	--config_file=$CONFIG \
		 	--url=$URL \
			--kind=$KIND \
		 	--email=$EMAIL \
		 	--filename="archives/$DATE/$KIND.csv" \
		 	--application=$APPLICATION  \
		 	--rps_limit=1000\
		 	--http_limit=1000 \
		 	--log_file="archives/$DATE/logs/$KIND-log.txt" \
		 	--result_db_filename="archives/$DATE/logs/$KIND-results.txt" \
		 	--db_filename=skip
}
downloadData "BrandDiscovered"
downloadData "FoursquareUser"
downloadData "User"
downloadData "DailyFollowEventCount"
downloadData "DailyFollowerCount"