# 📬 Colección de Postman - Sistema de Inventario y Ventas

Esta carpeta contiene los archivos de Postman para probar la API REST.

## 📋 Archivos incluidos

- `inventory-system-collection.json` - Colección con todos los endpoints
- `inventory-system-environment.json` - Entorno con variables configuradas

## 🚀 Cómo importar en Postman

### Opción 1: Importación rápida
1. Abre Postman
2. Botón **"Import"** (esquina superior izquierda)
3. Arrastra los archivos o selecciona **"Upload Files"**
4. Selecciona ambos archivos JSON
5. Click en **"Import"**

### Opción 2: Importación manual
1. **File → Import**
2. **Choose Files**
3. Selecciona `inventory-system-collection.json`
4. Repite para `inventory-system-environment.json`

## ⚙️ Configurar el entorno

1. En Postman, esquina superior derecha
2. Selecciona el entorno **"Inventario - Local"**
3. Verifica que `baseUrl` sea `http://localhost:8080/api`

## 🎯 Variables de entorno

| Variable | Descripción | Valor por defecto |
|---------|-------------|-------------------|
| `baseUrl` | URL base de la API | `http://localhost:8080/api` |
| `token` | Token JWT (se llena automáticamente) | - |
| `adminToken` | Token de admin (se llena automáticamente) | - |
| `productoId` | ID de producto para pruebas | `1` |
| `categoriaId` | ID de categoría para pruebas | `1` |
| `ventaId` | ID de venta para pruebas | `1` |

## 🤖 Script de autenticación automática

La colección incluye scripts que guardan automáticamente el token después del login:

```javascript
// Script de prueba en el endpoint Login
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set("token", response.data.token);
    
    if (response.data.rol === "ADMIN") {
        pm.environment.set("adminToken", response.data.token);
    }
}