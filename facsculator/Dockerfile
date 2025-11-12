# ----- Estágio 1: Build (Compilação com Maven) -----
# Usamos uma imagem oficial do Maven que já inclui o JDK 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 1. Copia o pom.xml e baixa as dependências (otimização de cache)
COPY pom.xml .
RUN mvn dependency:resolve

# 2. Copia o código-fonte e compila o projeto
COPY src ./src
# -DskipTests pula os testes durante o build do Docker (opcional, mas rápido)
RUN mvn package -DskipTests

# ----- Estágio 2: Final (Execução com JRE) -----
# Usamos uma imagem JRE (Runtime) slim, que é muito menor
FROM eclipse-temurin:17-jre AS final
WORKDIR /app

# 3. Copia o JAR compilado do estágio de build
# O nome do JAR é baseado no <artifactId>-<version>.jar
# O pom.xml padrão (quickstart) gera "complex-facsculator-1.0-SNAPSHOT.jar"
COPY --from=build /app/target/complex-facsculator-1.0-SNAPSHOT.jar app.jar

# 4. Define o "ponto de entrada"
# Em vez de 'java -jar', especificamos a classe principal (App)
# Isso é mais robusto e não exige configuração extra no pom.xml
ENTRYPOINT ["java", "-cp", "app.jar", "br.com.facsculator.App"]