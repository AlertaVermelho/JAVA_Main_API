# ESTÁGIO 1: Build da Aplicação com Maven e JDK completo
# Usamos uma imagem Maven que já inclui o OpenJDK 17.
# 'AS builder' nomeia este estágio para referência posterior.
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

# Define o diretório de trabalho dentro do container para este estágio
WORKDIR /app

# Copia primeiro o pom.xml para aproveitar o cache de camadas do Docker.
# Se o pom.xml não mudou, o Docker pode reutilizar a camada de download de dependências.
COPY pom.xml .

# Baixa todas as dependências (opcional, mas pode acelerar builds subsequentes se o pom não mudar)
# RUN mvn dependency:go-offline -B

# Copia o restante do código fonte da aplicação
COPY src ./src

# Compila a aplicação e empacota em um JAR executável.
# -DskipTests pula a execução de testes unitários durante o build do Docker (recomendado).
RUN mvn clean package -DskipTests spring-boot:repackage

# -----------------------------------------------------------------------------------

# ESTÁGIO 2: Criação da Imagem de Runtime Otimizada
# Usamos uma imagem JRE (Java Runtime Environment) slim baseada no Eclipse Temurin com Debian "Jammy".
# JRE é suficiente para rodar a aplicação e é menor que o JDK completo.
# Imagens "slim" são menores que as padrão, mas mais compatíveis que "alpine" para Java (devido ao glibc).
FROM eclipse-temurin:17-jre-slim-jammy

# Argumentos para definir o usuário e grupo da aplicação (podem ser build-args)
ARG APP_USER_NAME=appuser
ARG APP_GROUP_NAME=appgroup
ARG APP_UID=1001
ARG APP_GID=1001

# Cria um grupo e um usuário não-root para rodar a aplicação
# O 'RUN' executa comandos no shell do container durante a construção da imagem.
# 'groupadd -r' cria um grupo de sistema.
# 'useradd -r -g' cria um usuário de sistema e o adiciona ao grupo. '--no-log-init' evita problemas com /var/log/lastlog.
# '-ms /bin/sh' define o shell padrão (pode ser /bin/bash se precisar de um shell mais completo no container).
RUN groupadd -r -g ${APP_GID} ${APP_GROUP_NAME} && \
    useradd --no-log-init -r -u ${APP_UID} -g ${APP_GROUP_NAME} ${APP_USER_NAME}

# Define o diretório de trabalho para a aplicação no novo estágio
WORKDIR /app

# Copia o JAR executável do estágio 'builder' para o diretório de trabalho do estágio atual.
# Ajuste o nome do JAR se o seu artifactId ou version for diferente.
# O spring-boot-maven-plugin (com o goal repackage) geralmente coloca o JAR em target/
COPY --from=builder /app/target/redalert-0.0.1-SNAPSHOT.jar app.jar

# Define o proprietário dos arquivos da aplicação para o usuário não-root criado.
# Isso é importante para que o usuário 'appuser' tenha permissão para acessar os arquivos.
RUN chown -R ${APP_USER_NAME}:${APP_GROUP_NAME} /app

# Muda para o usuário não-root. A aplicação rodará com este usuário.
USER ${APP_USER_NAME}

# Expõe a porta em que a aplicação Spring Boot roda (definida no application.properties).
# Este é o server.port que você configurou (ex: 8074).
EXPOSE 8074

# Comando para executar a aplicação quando o container iniciar.
# Usamos "exec" para que o processo Java seja o PID 1 do container, recebendo sinais corretamente.
# -Djava.security.egd=file:/dev/./urandom é uma prática comum para melhorar a geração de números aleatórios seguros em alguns ambientes.
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]