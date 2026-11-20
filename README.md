# Proyecto Intermodular 2.º DAM · App de Calistenia, Movilidad y Bienestar

Prototipo funcional correspondiente a la primera entrega del Proyecto Intermodular (Curso 2026-2027).

## Funcionalidades implementadas en este hito
- **Autenticación con Firebase Auth:** Registro con correo y contraseña, inicio de sesión, recuperación de contraseña, cierre de sesión e inicio de sesión con Google (*Credential Manager*).
- **Base de datos remota (Cloud Firestore):** Operaciones CRUD en tiempo real para la gestión colaborativa de parques de calistenia y almacenamiento de perfiles de usuario.
- **Base de datos local y soporte Offline (Room / SQLite):** Persistencia local con caché automática y sincronización pendiente (`isSynced`) al recuperar conexión a internet.

## Arquitectura y tecnologías
- **Lenguaje e interfaz:** Kotlin + Jetpack Compose (Material Design 3).
- **Arquitectura:** MVVM + Repository Pattern (*Single Source of Truth* sobre Room).
- **Asincronía:** Kotlin Coroutines y StateFlow / Flow.
- **Backend y persistencia:** Firebase Authentication, Cloud Firestore y AndroidX Room.

## Estructura de paquetes
- `model/` — Modelos de dominio (`UserProfile`, `Park`).
- `data/local/` — Entidades Room, DAO y base de datos SQLite (`AppDatabase`, `ParkDao`, `ParkEntity`).
- `data/remote/` — Fuentes de datos de Firebase (`FirebaseAuthDataSource`, `FirestoreDataSource`).
- `data/repository/` — Lógica de negocio y sincronización *Offline-First* (`AuthRepository`, `ParkRepository`).
- `ui/auth/` — Pantalla y ViewModel de acceso (`LoginScreen`, `AuthViewModel`).
- `ui/home/` — Pantalla principal del CRUD y sincronización (`HomeScreen`, `HomeViewModel`).

## Configuración para ejecutar el proyecto
1. Clonar el repositorio y abrirlo en Android Studio.
2. Añadir el archivo `google-services.json` propio dentro del directorio `app/` (excluido del control de versiones por seguridad).
3. Sincronizar Gradle y ejecutar en un dispositivo o emulador con API 26 o superior.