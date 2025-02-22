for service in api-gateway authentication-service eureka-server file-upload-service group-service interaction-service message-service moderation-service notification-service post-service search-service story-service user-service; do
    echo "Processing $service..."

    cd $service || exit

    JAR_FILE="target/${service}-1.0-SNAPSHOT.jar"

    if [ -f "$JAR_FILE" ]; then
        echo "$JAR_FILE already exists. Skipping Maven build."
    else
        echo "$JAR_FILE does not exist. Running Maven build."
        mvn clean install
    fi

    cd ..
done

echo "All services are built!"

docker compose -f docker-compose.yml up