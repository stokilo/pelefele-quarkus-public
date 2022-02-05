# Quarkus backend for pelefele.com

```shell script
mvn compile quarkus:dev
```

## Profiles

Available profiles:

dev

stage

prod

## Development

Run postgres database on localhost 

```shell script
docker-compose -f quarkus-remote/src/main/docker/docker-compose.yaml up
```

Run quarkus dev 

```shell script
mvn compile quarkus:dev
```

## Package application and push into AWS ECR repo

The application can be packaged using:
```shell script
mvn package -DskipTests -Dquarkus.profile=stage 
```

## Package a native application and push into AWS ECR repo

```shell script
mvn clean package -DskipTests -Pnative -Dquarkus.profile=stage
```

