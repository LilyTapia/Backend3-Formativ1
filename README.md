# Bank Batch Modernization

Este proyecto moderniza los procesos batch bancarios utilizando Spring Batch para el procesamiento de transacciones diarias, cálculo de intereses mensuales y generación de estados de cuenta anuales.

## 🏗️ Arquitectura del Proyecto

El proyecto implementa una solución completa de Spring Batch con:

- **Lectura de CSV**: Procesamiento de archivos CSV con transacciones bancarias
- **Validación y Transformación**: Reglas de negocio comprehensivas para validar datos
- **Manejo de Errores**: Sistema robusto de detección y manejo de anomalías
- **Persistencia**: Soporte para H2, MySQL y PostgreSQL
- **Monitoreo**: Logging detallado y métricas de ejecución

## 🚀 Características Principales

### Jobs Implementados

1. **Daily Transactions Report Job** (`dailyTransactionsReportJob`)
   - Lee archivos CSV de transacciones diarias
   - Valida y procesa cada transacción
   - Detecta anomalías y errores de datos
   - Persiste resultados en base de datos

2. **Monthly Interest Job** (`monthlyInterestJob`)
   - Calcula intereses mensuales para todas las cuentas
   - Actualiza balances de cuentas
   - Genera registro de intereses

3. **Annual Statement Job** (`annualStatementJob`)
   - Genera estados de cuenta anuales
   - Resume depósitos y retiros del año
   - Calcula balances finales

### Validaciones Implementadas

- ✅ Validación de campos obligatorios
- ✅ Verificación de existencia de cuentas
- ✅ Validación de formato de montos y fechas
- ✅ Verificación de rangos de transacciones
- ✅ Validación de categorías de transacciones
- ✅ Reglas de negocio específicas por tipo de cuenta
- ✅ Detección de retiros excesivos
- ✅ Validación de fechas futuras y muy antiguas

## 📋 Requisitos Previos

- Java 17 o superior
- Maven 3.6 o superior
- Base de datos (H2, MySQL, o PostgreSQL)

## 🛠️ Configuración e Instalación

### 1. Clonar el Repositorio

```bash
git clone <repository-url>
cd bank-batch-modernization
```

### 2. Configuración de Base de Datos

#### Opción A: H2 (Por defecto - Para desarrollo)
No requiere configuración adicional. La base de datos se crea automáticamente en memoria.

#### Opción B: MySQL
```bash
# Crear base de datos
mysql -u root -p
CREATE DATABASE banco_batch;

# Ejecutar con perfil MySQL
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

#### Opción C: PostgreSQL
```bash
# Crear base de datos
psql -U postgres
CREATE DATABASE banco_batch;

# Ejecutar con perfil PostgreSQL
mvn spring-boot:run -Dspring-boot.run.profiles=postgresql
```

### 3. Compilar y Ejecutar

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar la aplicación
mvn spring-boot:run
```

## 🎯 Uso de la Aplicación

### Ejecutar Jobs via REST API

La aplicación expone endpoints REST para ejecutar los jobs:

#### 1. Procesar Transacciones Diarias
```bash
# Procesar archivo por defecto
curl -X POST http://localhost:8080/jobs/daily

# Procesar archivo específico por fecha
curl -X POST "http://localhost:8080/jobs/daily?date=2025-08-02"
```

#### 2. Calcular Intereses Mensuales
```bash
curl -X POST "http://localhost:8080/jobs/monthly?period=2025-08"
```

#### 3. Generar Estados de Cuenta Anuales
```bash
curl -X POST "http://localhost:8080/jobs/annual?year=2025"
```

### Estructura de Archivos CSV

Los archivos CSV deben seguir este formato:

```csv
accountNumber,txnDate,amount,category
ACC-1001,2025-08-01,50000,DEPOSIT
ACC-1001,2025-08-01,-3000,PAYMENT
ACC-2001,2025-08-01,-15000,LOAN_PAYMENT
```

#### Categorías Válidas:
- `DEPOSIT` - Depósito
- `WITHDRAWAL` - Retiro
- `PAYMENT` - Pago
- `TRANSFER` - Transferencia
- `LOAN_PAYMENT` - Pago de préstamo
- `INTEREST` - Interés
- `FEE` - Comisión
- `REFUND` - Reembolso
- `ADJUSTMENT` - Ajuste

## 🧪 Testing

### Ejecutar Tests Unitarios
```bash
mvn test
```

### Ejecutar Tests de Integración
```bash
mvn test -Dtest=*IntegrationTest
```

### Datos de Prueba

El proyecto incluye archivos CSV de prueba:
- `transactions_2025-08-01.csv` - Datos válidos
- `transactions_2025-08-02.csv` - Datos adicionales válidos
- `transactions_test_errors.csv` - Datos con errores para testing


## 🗄️ Esquema de Base de Datos

### Tablas Principales:

#### `accounts`
- Información de cuentas bancarias
- Tipos: SAVINGS, LOAN
- Balances y tasas de interés

#### `processed_transactions`
- Transacciones procesadas
- Incluye flag de anomalías y mensajes de error

#### `interest_ledger`
- Registro de intereses mensuales calculados

#### `annual_statement`
- Estados de cuenta anuales generados


## 🚨 Manejo de Errores

### Estrategias Implementadas:

1. **Skip Policy**: Omite registros con errores (límite: 50)
2. **Fault Tolerance**: Continúa procesamiento ante errores
3. **Anomaly Detection**: Marca registros problemáticos sin detener el job
4. **Detailed Logging**: Registra todos los errores para auditoría

### Tipos de Anomalías Detectadas:

- Cuentas inexistentes
- Formatos de datos inválidos
- Montos fuera de rango
- Fechas inválidas o futuras
- Categorías no reconocidas
- Violaciones de reglas de negocio

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver `LICENSE` para más detalles.

## 📞 Soporte

Para soporte técnico o preguntas a:
- Seba & Lily

---

**Desarrollado con ❤️ usando Spring Batch**
