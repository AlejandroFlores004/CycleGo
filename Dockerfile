# 1) Etapa de build: compilar el WAR con Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q dependency:go-offline

COPY src ./src
RUN mvn -q clean package -DskipTests

# 2) Etapa de runtime: Tomcat que ejecuta tu WAR
FROM tomcat:10.1-jdk17-temurin

# INSTALAR pg_dump y pg_restore (cliente de PostgreSQL)
RUN apt-get update && \
    apt-get install -y postgresql-client && \
    rm -rf /var/lib/apt/lists/*

# Opcional: borrar las apps por defecto de Tomcat (host-manager, docs, etc.)
RUN rm -rf /usr/local/tomcat/webapps/*

# Copiar tu WAR generado y desplegarlo como ROOT.war
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
