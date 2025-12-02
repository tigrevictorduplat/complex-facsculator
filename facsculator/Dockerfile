# ----- Estágio 1: Build (Compilação com Maven) -----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 1. Copia o pom.xml e baixa as dependências
COPY pom.xml .
RUN mvn dependency:resolve

# 2. Copia o código-fonte e compila o projeto
COPY src ./src
RUN mvn package -DskipTests

# ----- Estágio 2: Final (Execução com JRE) -----
FROM eclipse-temurin:17-jre AS final
WORKDIR /app

# 3. Copia o JAR compilado (usando *.jar para pegar qualquer versão gerada)
COPY --from=build /app/target/*.jar app.jar

# 4. Define o "ponto de entrada"
ENTRYPOINT ["java", "-cp", "app.jar", "com.facsculator.App"]