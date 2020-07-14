set DATA_SERVICE_HOME=%cd%\..

echo "starting app...."

set CLASSPATH=%CLASSPATH%;%DATA_SERVICE_HOME%\lib\*

java -cp %CLASSPATH% com.bigdata.app.DataServiceApp -DDATA_SERVICE_HOME=$DATA_SERVICE_HOME --spring.config.location=%DATA_SERVICE_HOME%\conf\application.properties

