# Estágio 1: Build da aplicação com Maven
# Use uma imagem do Maven para compilar o projeto e gerar o .jar
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# O 'clean package -DskipTests' compila, empacota e pula os testes para um build mais rápido.
# Verifique se o seu comando de build é este. Se usar Gradle, ajuste a linha.
RUN mvn clean package -DskipTests

# Estágio 2: Execução da aplicação
# Use uma imagem Java mais leve para rodar a aplicação
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copia o .jar do estágio de build para o estágio de execução
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta que a sua aplicação usa (padrão do Spring Boot é 8080)
EXPOSE 8080

# Comando para iniciar a aplicação quando o contêiner for executado
ENTRYPOINT ["java", "-jar", "app.jar"]