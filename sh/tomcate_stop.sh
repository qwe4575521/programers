serverpid=`ps -ef|grep  $HOME/apache-tomcat|grep -v grep|awk '{print $2}'`
echo $serverpid
kill -9 $serverpid