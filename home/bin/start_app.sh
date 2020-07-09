export DATA_SERVICE_HOME="$(dirname "$PWD")"

export CLASSPATH=$CLASSPATH:$DATA_SERVICE_HOME/lib/*

java -cp $CLASSPATH com.bigdata.app.DataServiceApp --spring.config.location=$DATA_SERVICE_HOME/conf/application.properties

