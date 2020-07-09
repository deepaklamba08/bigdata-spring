export DATA_SERVICE_HOME="$(dirname "$PWD")"

java -jar $DATA_SERVICE_HOME/lib/data-service-1.0-SNAPSHOT.jar --spring.config.location=$DATA_SERVICE_HOME/conf/application.properties

