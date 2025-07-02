# 🎯 Planifest

**Planifest** es una solución integral para empresas organizadoras de eventos. Ofrece una plataforma robusta y eficiente para **gestionar eventos, controlar insumos, administrar personal** y optimizar la operación logística, todo desde un mismo lugar.

---

## 🧰 Tecnologías utilizadas

- **Java 17**  
- **Spring Boot**  
- **PostgreSQL**  
- **Maven**  
- **Thymeleaf**  
- **Spring Security**  
- **Lombok**, **JPA**, **RESTful APIs**

---

## ⚙️ Configuración del proyecto

### 1. Clonar el repositorio


git clone https://github.com/thjumi/planifest.git
cd planifest

2. Importar el proyecto en tu IDE
Puedes abrir el proyecto con:

IntelliJ IDEA (recomendado)
Eclipse
Spring Tool Suite

Asegúrate de tener instalado:
Java 17
Maven

3. Configurar la base de datos
Crea una base de datos en PostgreSQL, por ejemplo:
CREATE DATABASE planifest_db;
Luego crea el archivo src/main/resources/application.properties:

properties
spring.datasource.url=jdbc:postgresql://localhost:5432/planifest_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update

4. Ejecutar el proyecto
./mvnw spring-boot:run
O desde tu IDE, ejecuta la clase principal PlanifestApplication.java.

Una vez iniciado, accede a la aplicación en:
http://localhost:8080

5. Poblar la base de datos (opcional)
Si el proyecto incluye un archivo data.sql o un CommandLineRunner, los datos se insertarán automáticamente al iniciar.
Si no, puedes insertar datos manualmente en PostgreSQL.


Verifica que PostgreSQL esté corriendo correctamente.
Si usas Windows, asegúrate de tener las variables de entorno configuradas para Java y Maven.
Puedes modificar spring.jpa.hibernate.ddl-auto según el entorno (update, create, validate, none, create-drop).

👩‍💻 Autores
Sleider Rodriguez – Product Owner
Sebastián Barrgán y Bivian Cruz – Desarrolladores
Julieth Gómez – Scrum Master

