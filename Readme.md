docker run -d \
  -p 8080:8074 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_PROD_URL="jdbc:oracle:thin:@sua_url_prod" \
  -e DB_PROD_USERNAME="seu_usuario_prod" \
  -e DB_PROD_PASSWORD="sua_senha_prod" \
  -e JWT_SECRET="seu_jwt_secret_de_producao_super_forte" \
  -e LOCATIONS_API_URL="url_da_sua_api_csharp_em_prod" \
  --name redalert-java-api \
  sua_imagem_docker_redalert:latest