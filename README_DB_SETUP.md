Instrucciones para configurar la conexión a Aiven MySQL

Resumen:
- La instancia Aiven requiere SSL. Usamos sslMode=REQUIRED por defecto (no valida CA). Para validar la CA del servidor y evitar MITM, usa VERIFY_CA y genera un truststore.

Pasos para generar un truststore PKCS12 desde el certificado CA de Aiven:
1. Descarga el certificado CA desde la consola de Aiven (link "CA certificate" -> Descargar). Guarda el archivo como aiven-ca.pem

2. Convierte el PEM a PKCS12 truststore (Java 11+ soporta PKCS12):

```bash
# mueve a la carpeta del proyecto o a donde quieras guardar el truststore
openssl x509 -in aiven-ca.pem -out aiven-ca.der -outform DER
keytool -importcert -alias aiven-ca -file aiven-ca.der -keystore aiven-truststore.p12 -storetype PKCS12 -storepass changeit -noprompt
```

3. Ejecuta la aplicación apuntando la JVM al truststore (ejemplo con Gradle):

```bash
# Ejecuta con verify CA (asumiendo que generaste el truststore en el directorio actual)
./gradlew bootRun --no-daemon -Djavax.net.ssl.trustStore=$(pwd)/aiven-truststore.p12 -Djavax.net.ssl.trustStorePassword=changeit -Dspring.datasource.url="jdbc:mysql://tratohecho-nicogueva20-1a12.e.aivencloud.com:25201/defaultdb?sslMode=VERIFY_CA&serverTimezone=UTC"
```

Notas de seguridad:
- No comites el truststore ni la contraseña en git.
- Reemplaza la contraseña del truststore ("changeit") por algo seguro.
- En entornos productivos, almacena credenciales en variables de entorno o gestores de secretos.

Alternativa: usar sslMode=REQUIRED (sin verificar CA) — más sencilla para desarrollo, menos segura.

Si quieres, puedo:
- Convertir la configuración en variables de entorno en `application.properties`.
- Añadir un script `scripts/generate-truststore.sh` que automatice los pasos.
- Quitar la contraseña en texto plano y usar variables de entorno en `application.properties`.

