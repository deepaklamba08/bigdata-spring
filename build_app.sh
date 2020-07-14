echo "starting build.."
mvn clean compile install -DskipTests
cp data-service/target/data-service-1.0-SNAPSHOT.jar app_home/lib/
echo "completed!!!"