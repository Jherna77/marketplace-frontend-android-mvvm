# BaZaaR Marketplace Android – Frontend

![Arquitectura](https://img.shields.io/badge/Architecture-MVVM-blueviolet)
![Android Studio](https://img.shields.io/badge/Android%20Studio-Meerkat-3DDC84?logo=androidstudio\&logoColor=white)
![Java 17](https://img.shields.io/badge/Java-17-orange?logo=openjdk\&logoColor=white)
![JWT Security](https://img.shields.io/badge/Security-JWT-orange?logo=jsonwebtokens\&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-Build-CA4245?logo=gradle\&logoColor=white)
![Stripe](https://img.shields.io/badge/Stripe-Integration-626CD9?logo=stripe\&logoColor=white)
![MIT License](https://img.shields.io/badge/License-MIT-yellow?logo=opensourceinitiative\&logoColor=white)

## Índice

- [Visión General](#visión-general)
- [Arquitectura (MVVM)](#arquitectura-MVVM)
- [Funcionalidades](#funcionalidades-principales)
- [Seguridad y API](#comunicación-con-la-api-y-seguridad)
- [Tecnologías](#tecnologías-utilizadas)
- [Cómo Ejecutar](#cómo-ejecutar)
- [Futuras Mejoras](#futuras-mejoras)

---

## Visión General

Este repositorio contiene el cliente frontend de un marketplace de comercio electrónico, desarrollado como una aplicación Android nativa, diseñada para comunicarse con el backend a través de una API REST.

La aplicación ofrece una experiencia completa y realista, soportando múltiples roles:

* **Usuarios anónimos:** navegación por el catálogo.
* **Clientes:** compras, pedidos, valoraciones y gestión de perfil.
* **Tiendas:** gestión de productos y pedidos.
* **Administradores:** gestión de usuarios, categorías, productos y copias de seguridad del sistema.

El proyecto pone el foco en buenas prácticas arquitectónicas, usabilidad y diseño escalable, integrando características reales como autenticación JWT, gestión de imágenes y pagos con Stripe.

<p align="center">
  <img src="/screenshots/HomeLogin.png" alt="Vista general de inicio y login" width="340"/>
</p>

---

## Arquitectura (MVVM)

El diseño del frontend se basa en el patrón arquitectónico Model-View-ViewModel (MVVM), que es el recomendado por Google para el desarrollo de interfaces modernas en Android y cuya idea principal es separar la lógica de la interfaz de usuario del modelo de datos subyacente.

<p align="center">
  <img src="/screenshots/MVVM.png" alt="Visión general de la arquitectura MVVM" width="700"/>
</p>

### Objetivos de la Arquitectura

* **Separación de responsabilidades:** límites claros entre UI, lógica de negocio y acceso a datos.
* **Alta testabilidad** y modularidad en todas las capas.
* **Interfaz reactiva**, basada en ViewModels y LiveData.
* **Desacoplamiento** mediante Puertos de Dominio (interfaces).
* Uso de **Mappers** para convertir DTOs de la API en modelos de dominio puros.

### Estructura por Capas

La estructura del proyecto refuerza explícitamente la separación de responsabilidades:

| Capa        | Ruta (simplificada) | Responsabilidad                                                                                      |
| ----------- | ------------------- | ---------------------------------------------------------------------------------------------------- |
| **Dominio** | `domain/`           | Modelos de negocio, puertos (interfaces), reglas de validación, callbacks.                           |
| **Data**    | `data/`             | Comunicación con API, DTOs, configuración de red, repositorios (implementación de puertos), mappers. |
| **UI**      | `ui/`               | Activities, Fragments, ViewModels, adaptadores de UI y estados de vista.                             |
| **Core**    | `core/`             | Constantes, inyección de dependencias (DI), utilidades y gestión de ficheros.                        |

Los ViewModels no dependen de clases de infraestructura ni de Retrofit, únicamente de puertos definidos en la capa de dominio.

### Flujo de Comunicación

El flujo de interacción sigue siempre una dirección clara hacia el dominio:

`UI (Fragment/Activity) → ViewModel → Puerto de Dominio → Repositorio (Data) → ApiService → Backend`

Este enfoque evita que la lógica de red o infraestructura contamine la capa de presentación.

---

## Funcionalidades Principales

### Soporte de Roles de Usuario

* **Anónimo:** navegación por el catálogo, búsqueda y detalle de productos.
* **Cliente:** carrito de compra, pagos con Stripe, favoritos, historial de pedidos, valoraciones, mensajería y perfil.
* **Tienda:** gestión de productos, publicación de artículos, actualización de pedidos, visualización de ventas y mensajería.
* **Administrador:** gestión de usuarios, productos, categorías y copias de seguridad.

<p align="center">
  <img src="/screenshots/AdminDashboard.png" alt="Panel de administración" width="170"/>
</p>

### Experiencia de Marketplace

* Catálogo: navegación por categorías, búsqueda por nombre de producto y posibilidad de filtrar resultados.

<p align="center">
  <img src="/screenshots/Search.png" alt="Proceso de búsqueda de productos" width="510"/>
</p>

* Detalle de producto: vista detallada con galería de imágenes e información relevante (descripción, stock disponible, valoración global, reseñas, etc).

<p align="center">
  <img src="/screenshots/ProductDetails.png" alt="Detalle de producto" width="170"/>
</p>

* Gestión de productos (tiendas): creación y actualización de productos, incluyendo precio, stock disponible, descripción, categorías e imágenes.

<p align="center">
  <img src="/screenshots/ProductManagement.png" alt="Gestión de productos" width="1000"/>
</p>

### Carrito y Pedidos

* Añadir/eliminar productos, selección de cantidad de unidades a comprar y cálculo del subtotal.

<p align="center">
  <img src="/screenshots/Cart.png" alt="Carrito de compra" width="340"/>
</p>

* Pago seguro: flujo de pago integrado mediante Stripe SDK.

<p align="center">
  <img src="/screenshots/Payment.png" alt="Proceso de pago" width="510"/>
</p>

* Historial y acceso a los detalles de cada pedido.

<p align="center">
  <img src="/screenshots/OrderDetail.png" alt="Detalle de pedido" width="340"/>
</p>

### Valoraciones y Mensajería

* Creación de valoraciones y reseñas de un producto sobre el que se ha realizado una compra.
* Sistema de mensajería cliente–tienda con notificaciones de actualización.

<p align="center">
  <img src="/screenshots/Messages.png" alt="Mensajería" width="340"/>
</p>

### Copias de Seguridad (Administrador)

Creación y restauración de copias de respaldo de la base de datos e imágenes existentes.

<p align="center">
  <img src="/screenshots/Backups.png" alt="Gestión de copias de seguridad" width="340"/>
</p>

---

## Comunicación con la API y Seguridad

El cliente garantiza una comunicación eficiente y segura con el backend:

* **Cliente HTTP:** Retrofit 2 junto con Gson para peticiones estructuradas y serialización JSON.
* **Seguridad:** Interceptor de OkHttp para inyectar automáticamente el token JWT en peticiones protegidas.
* **Gestión de sesión:** almacenamiento persistente del token mediante SharedPreferences y un `SessionManager` dedicado.
* **Gestión de errores:** uso de un Callback Delegator para unificar diferentes eventos asíncronos (estados de carga, error y éxito en toda la aplicación).

---

## Tecnologías Utilizadas

* **Lenguaje:** Java 17
* **IDE:** Android Studio
* **Red:** Retrofit 2, Gson, OkHttp
* **UI / Diseño:** Material Design 3
* **Imágenes:** Glide (carga y cacheo eficiente)
* **Pagos:** Stripe SDK
* **Persistencia local:** SharedPreferences

---

## Cómo Ejecutar

### 1. Requisitos previos

* **Android Studio Meerkat** (o superior)
* **JDK 17**
* Instancia en ejecución del [Backend BaZaaR](https://github.com/Jherna77/marketplace-backend-spring-hexagonal)
* Dispositivo físico o emulador (Android 8.0+ recomendado)

### 2. Clonar el Repositorio

```bash
git clone https://github.com/Jherna77/marketplace-frontend-android-mvvm.git
cd marketplace-frontend-android-mvvm
```

Abrir el proyecto con **Android Studio**.

### 3. Configurar la URL del Servidor

El cliente Android necesita conocer la dirección del backend. Debe configurarse en **dos puntos**:

#### 3.1. `BASE_URL` en `Values.java`

`src/main/java/com/jhernandez/frontend/bazaar/core/constants/Values.java`

```java
public static final String BASE_URL = "http://TU.IP.DEL.SERVIDOR:8080";
```

Ejemplos:

* Máquina local (misma red): `http://192.168.1.XX:8080`
* Emulador Android: `http://10.0.2.2:8080`
* Producción: `https://api.mibazaar.com`

#### 3.2. Configurar tráfico HTTP en `network_security_config.xml`

Si el backend usa **HTTP**, Android requiere permitir explícitamente tráfico sin cifrar:

```xml
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">TU.IP.DEL.SERVIDOR</domain>
    </domain-config>
</network-security-config>
```

### 4. Verificar Conexión con el Backend

```text
http://TU.IP.DEL.SERVIDOR:8080/ping
```

### 5. Ejecutar la Aplicación

1. Selecciona dispositivo o emulador.
2. Pulsa **Run**.
3. Regístrate o inicia sesión.

Si todo está correctamente configurado, la aplicación se comunicará con el backend sin problemas.

---

## Futuras Mejoras

* Migración a **Jetpack Compose**.
* Integración de **notificaciones push**.
* Soporte de **internacionalización** y múltiples idiomas.
* Nuevos proveedores de pago (PayPal) y paneles analíticos.
* **Integración con RRSS** (Google, Facebook) y autenticación biométrica.
