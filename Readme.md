# API Java - RedAlert

## 1. Introdução

Esta API é o componente central do backend da plataforma AlertaÁgil, um sistema projetado para auxiliar comunidades no monitoramento e resposta a desastres naturais como enchentes e deslizamentos de terra.

O objetivo principal desta API é gerenciar usuários, receber e processar alertas de ocorrências reportados por cidadãos através de um aplicativo móvel, interagir com um serviço de Inteligência Artificial (Python) para classificar esses alertas e identificar hotspots (áreas de concentração de eventos), e fornecer dados consolidados para a visualização em um mapa no frontend.

A arquitetura completa do AlertaÁgil envolve esta API Java e uma API Python para acessar as funcionalidades de IA

## 2. Arquitetura Simplificada da API Java

Esta API é construída utilizando Spring Boot e segue uma arquitetura em camadas, compreendendo os seguintes módulos principais:

* **Autenticação e Usuários (`/auth`, `/users/me`):** Gerencia o registro, login (com JWT) e o perfil básico dos usuários cidadãos.
* **Alertas de Usuário (`/alerts`):** Recebe novos alertas, os envia para classificação na API de IA, e armazena os alertas classificados. Também fornece um endpoint para listagem interna.
* **Hotspots de Eventos (Lógica de Serviço Agendada):** Um job agendado processa periodicamente os alertas classificados, os envia para clustering na API de IA, e cria/atualiza registros de hotspots no banco de dados, associando os alertas a eles.
* **Dados para o Mapa (`/mapdata`):** Fornece os dados consolidados de hotspots ativos e alertas públicos relevantes para uma determinada área geográfica, consumidos pelo aplicativo móvel.

## 3. Pré-requisitos

Antes de rodar esta aplicação, certifique-se de que você tem instalado:

* Java 17 (ou superior)
* Apache Maven 3.6+ (para compilar e rodar)
* Docker (se for rodar a aplicação ou suas dependências em containers)
* Acesso a uma instância do Banco de Dados Oracle (pode ser uma instância local, um container Oracle XE, ou o banco da FIAP para desenvolvimento).
* A API Python de IA do projeto "AlertaÁgil" deve estar rodando e acessível pela rede.
* (Opcional) Postman ou similar para testar os endpoints da API (script automatizado para o Postman na raiz do projeto).

## 4. Configuração do Ambiente de Desenvolvimento Local

Siga estes passos para configurar seu ambiente local:

1.  **Clone o Repositório:**
    ```bash
    git clone [https://github.com/AlertaVermelho/JAVA_Main_API.git](https://github.com/AlertaVermelho/JAVA_Main_API.git)
    cd JAVA_Main_API
    ```

2.  **Configuração do Banco de Dados Oracle:**
    * As tabelas necessárias para esta API são: `USUARIOS`, `ALERTAS_USUARIO`, `HOTSPOTS_EVENTOS` (e `LOCAIS_MONITORADOS` se a API C# for usar o mesmo banco).
    * Os scripts SQL para criar este schema estão localizados na pasta `/database_scripts/schema_oracle.sql` dentro deste repositório. Execute este script no seu banco de dados Oracle alvo.

3.  **Arquivo `.env`:**
    * Esta aplicação utiliza um arquivo `.env` na raiz do projeto para carregar variáveis de ambiente sensíveis durante o desenvolvimento local.
    * Crie um arquivo chamado `.env` na raiz do projeto Java e seguindo o exemplo do arquivo `.env.example` adicione as seguintes variáveis, substituindo pelos seus valores:

        ```dotenv
        SPRING_PROFILES_ACTIVE=prod
        DB_PROD_URL=jdbc:oracle:thin:@//<seu_host_oracle_dev>:<sua_porta_oracle>/<SEU_SERVICE_NAME_OU_SID_DEV>
        DB_USERNAME=SEU_USUARIO_ORACLE_DEV
        DB_PASSWORD=SUA_SENHA_ORACLE_DEV

        # Segredo JWT (use uma string longa, aleatória e codificada em Base64)
        JWT_SECRET=SEU_SEGREDO_JWT_FORTE_EM_BASE64_AQUI

        # Expiração do JWT em milissegundos (opcional, o default no app.properties é 24h)
        JWT_EXPIRATION_MS=86400000

        # URL base da API Python de IA rodando localmente, exemplo:
        PYTHON_AI_API_BASE_URL=[http://127.0.0.1:8000](http://127.0.0.1:8000) 

        # Para controlar os defaults do job de hotspot se não definidos nos perfis
        # APP_HOTSPOT_FIXED_DELAY_MS_DEFAULT=300000
        # APP_HOTSPOT_INITIAL_DELAY_MS_DEFAULT=60000
        ```

4.  **Arquivos `application.properties`:**
    * O projeto utiliza perfis do Spring Boot para gerenciar configurações de diferentes ambientes.
    * **`application.properties`:** Contém configurações comuns a todos os ambientes e define `spring.profiles.active=dev` como padrão para desenvolvimento local. Ele usa placeholders (ex: `${JWT_SECRET}`) que são preenchidos pelas variáveis de ambiente (carregadas do `.env` em dev local).
    * **`application-prod.properties`:** Contém configurações para o ambiente de produção. **Todas as informações sensíveis ou que variam por ambiente devem ser placeholders (ex: `${DB_PROD_URL}`), preenchidas por variáveis de ambiente no servidor de deploy.**

## 5. Como Rodar a Aplicação

### 5.1. Rodando Localmente com Maven (Sem Docker para esta API)

1.  **Pré-requisitos:**
    * Garanta que seu Banco de Dados Oracle esteja acessível e com o schema criado (`schema_oracle.sql`).
    * Garanta que a API Python de IA esteja rodando localmente (ex: em `http://127.0.0.1:8000`).
    * Configure corretamente seu arquivo `.env` com as URLs e credenciais.
    * Certifique-se de que o `application.properties` está com `spring.profiles.active=prod` e que o `application-dev.properties` está configurado para seu Oracle local.

2.  **Execute o Comando Maven:**
    Na raiz do projeto Java, execute:
    ```bash
    mvn spring-boot:run
    ```
    Ou rode a classe `RedalertApplication.java` diretamente da sua IDE. A API estará disponível em `http://localhost:8074` (ou a porta configurada).

### 5.2. Rodando com Docker (API Java em um Container)

O `Dockerfile` na raiz deste projeto permite construir uma imagem Docker para esta API.

1.  **Construir a Imagem Docker da API Java:**
    Na raiz do projeto Java, execute:
    ```bash
    docker build -t redalert-java-api:latest .
    ```

2.  **Pré-requisitos para Rodar o Container Java:**
    * Um container do **Oracle DB** precisa estar rodando e acessível na mesma rede Docker.
    * Um container da **API Python de IA** precisa estar rodando e acessível na mesma rede Docker.

3.  **Criar uma Rede Docker (se ainda não existe):**
    ```bash
    docker network create alerta-agil-net
    ```

4.  **Exemplo de Comando `docker run` para a API Java:**
    Este comando assume que os containers do Oracle (`oracle-db-container`) e da API Python (`python-api-container`) já estão rodando na rede `alerta-agil-net`.
    ```bash
    docker run -d -p 8074:8074 \
      --name java-api-container \
      --network alerta-agil-net \
      -e SPRING_PROFILES_ACTIVE="prod" \
      -e DB_PROD_URL="jdbc:oracle:thin:@oracle-db-container:1521/XEPDB1" \
      -e DB_USERNAME="SYSTEM" \
      -e DB_PASSWORD="SuaSenhaDoOracleContainer" \
      -e JWT_SECRET="SEU_JWT_SECRET_FORTE_PARA_PRODUCAO_EM_BASE64" \
      -e JWT_EXPIRATION_MS="86400000" \
      -e PYTHON_AI_API_BASE_URL="http://python-api-container:8000" \
      redalert-java-api:latest
    ```
    * **Importante:** Substitua os valores das variáveis de ambiente (`SuaSenhaDoOracleContainer`, `SEU_JWT_SECRET_FORTE_PARA_PRODUCAO_EM_BASE64`, etc.) pelos valores corretos para seu ambiente de container.

## 6. API Python de IA (Interdependência)

Esta API Java depende de um serviço externo de Inteligência Artificial (API Python) para:
* **Classificação de Texto:** Determinar o tipo e a severidade dos alertas reportados.
* **Clustering de Alertas:** Agrupar alertas para identificar hotspots.

A comunicação é feita via HTTP REST. A URL base para a API Python é configurada na API Java através da propriedade `app.python.ai.baseurl` (que lê a variável de ambiente `PYTHON_AI_API_BASE_URL`).

Para detalhes sobre como configurar, rodar e entender o funcionamento da API Python, por favor, consulte o repositório dela:
* **Link para o Repositório da API Python:** [https://github.com/AlertaVermelho/PY_Pred_API](https://github.com/AlertaVermelho/PY_Pred_API).

## 7. Endpoints da API Java (Principais)

Uma visão geral dos principais grupos de endpoints. A documentação completa e interativa está disponível via Swagger UI.

* **`/auth`**:
    * `POST /register`: Registro de novos usuários.
    * `POST /login`: Autenticação e obtenção de token JWT.
* **`/users/me`**: (Requer Autenticação JWT)
    * `GET /users/me`: Obtém dados do usuário logado.
    * `PUT /users/me`: Atualiza dados do usuário logado.
    * `PUT /users/me/notification-token`: Atualiza o token de notificação push do usuário.
    * `DELETE users/me`: Deleta a conta do usuário se não houverem alertas associados
* **`/alerts`**: (Requer Autenticação JWT)
    * `POST /alerts`: Cria um novo alerta.
    * `GET /alerts`: Lista alertas com filtros (uso interno/específico e paginado).
* **`/mapdata`**:
    * `GET /mapdata`: Retorna dados agregados (hotspots e alertas públicos) para o mapa.

**Documentação Interativa (Swagger UI):**
Após iniciar a aplicação, acesse: `http://localhost:8074/swagger-ui.html` (ou a porta e IP corretos se rodando na VM).

## 8. Estrutura do Projeto Java

O projeto segue uma estrutura padrão de aplicações Spring Boot, organizada em pacotes como:
* `com.example.redalert.config`: Configurações da aplicação (Beans, Segurança, etc.).
* `com.example.redalert.controller`: Controladores REST que expõem os endpoints da API.
* `com.example.redalert.dto`: Objetos de Transferência de Dados para requisições e respostas.
* `com.example.redalert.exception`: Exceções customizadas e handlers globais.
* `com.example.redalert.model`: Entidades JPA que mapeiam as tabelas do banco.
* `com.example.redalert.repository`: Interfaces Spring Data JPA para acesso ao banco.
* `com.example.redalert.security`: Componentes relacionados à segurança (JWT, UserDetailsService).
* `com.example.redalert.service`: Interfaces e implementações da lógica de negócio.
* `com.example.redalert.client`: Clientes HTTP para comunicação com serviços externos (API Python, API C#).

---