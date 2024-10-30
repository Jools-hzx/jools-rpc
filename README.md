
# jools-rpc - A Simple RPC Framework

![Static Badge](https://img.shields.io/badge/JoolsRpc-version_1.0-blue)
![Static Badge](https://img.shields.io/badge/JDK-17-blue?logo=java)
![Static Badge](https://img.shields.io/badge/Maven-3.8.1-C71A36?logo=apachemaven)
![Static Badge](https://img.shields.io/badge/Vert.x-4.3.3-green?logo=eclipsevert.x)
![Static Badge](https://img.shields.io/badge/Hutool-v5.8.10-blueviolet?logo=hu)

`JoolsRPC` is a lightweight RPC (Remote Procedure Call) framework implemented in Java, designed to simplify the process of remote service invocation. It provides an easy-to-use structure with a focus on flexibility, extensibility, and simplicity. This framework is ideal for learning purposes or for small-scale applications requiring lightweight RPC capabilities.

## ✨ Features

- **Consumer Module**: `exp-consumer` module acts as the service consumer.
- **Proxy Service**: Uses dynamic proxy (`UserService`) to allow consumers to call remote services.
- **Request Client**: `BasicConsumerExample` sends requests before invoking `getUser()` using `HTTPRequest` from the Hutool library.
- **Serialization/Deserialization**: Implemented via `JdkSerializer` to serialize and deserialize objects.
- **Local Web Server**: Powered by `VertxHttpServer`, the web server is based on the Vert.x framework and synchronously registers services to a local service registry on startup.
- **Request Handler**: `HttpServerHandler` processes incoming HTTP requests, queries the local registry, and uses reflection to invoke the appropriate methods.
- **Local Service Registry**: `LocalRegistry` manages service registrations using a `Map`, where the key is the service name, and the value is the fully qualified class name.
- **Provider**: Implements the `UserService` interface to provide services.

## 🛠️ Technology Stack

- **Language**: Java
- **JDK Version**: 8
- **Build Tool**: Maven
- **Web Server**: Vert.x
- **Libraries**:
  - **Hutool**: For handling HTTP requests (`HTTPRequest`).

## 📂 Project Structure

```
jools-rpc/
│
├── exp-consumer/               # Consumer module
│   └── BasicConsumerExample.java   # Example of service consumption
│
├── provider/
│   └── UserService.java            # Service interface
│   └── UserServiceImpl.java        # Service implementation
│
├── registry/
│   └── LocalRegistry.java          # Local service registry
│
├── server/
│   └── VertxHttpServer.java        # Vert.x-based web server
│   └── HttpServerHandler.java      # Custom request handler
│
├── serializer/
│   └── JdkSerializer.java          # JDK-based serialization/deserialization
│
└── pom.xml                         # Maven build file
```

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- **JDK 8** or higher
- **Maven 3.6+**
- Internet connection to download Maven dependencies

### Installation

1. **Clone the repository**:

    ```bash
    git clone https://github.com/Jools-hzx/jools-rpc.git
    ```

2. **Navigate to the project directory**:

    ```bash
    cd jools-rpc
    ```

3. **Build the project using Maven**:

    ```bash
    mvn clean install
    ```

### Running the Project

1. **Start the Vert.x Web Server**:

    Navigate to the provider module and start the `BasicProviderExample`, it will start Web Server(based on Vert.x) as well:

    ```bash
    mvn exec:java -Dexec.mainClass="com.jools.exp.provider.BasicProviderExample"
    ```

2. **Consume the service**:

    After the server is running, you can run the consumer by executing `BasicConsumerExample`:

    ```bash
    mvn exec:java -Dexec.mainClass="com.jools.rpc.expconsumer.BasicConsumerExample"
    ```

### Example Usage

```java
UserService userService = BasicConsumerExample.getUserService();
User user = userService.getUser("123");
System.out.println("User received from RPC: " + user);
```

## 🔧 How It Works

1. **Service Registration**: Upon starting the `VertxHttpServer`, services are registered in the local registry (`LocalRegistry`) using a map where the key is the service name, and the value is the fully qualified class name.
2. **Service Consumption**: The consumer requests a proxy object of `UserService` using JDK dynamic proxies. Before invoking `getUser()`, an HTTP request is sent to the server using `HTTPRequest` (Hutool library).
3. **Request Handling**: The `HttpServerHandler` receives the request, looks up the service in the registry, and invokes the corresponding method using reflection.
4. **Serialization**: The request and response are serialized and deserialized using `JdkSerializer` for efficient network transmission.

## 📦 Dependencies

```xml
<dependencies>
    <!-- Vert.x Core -->
    <dependency>
        <groupId>io.vertx</groupId>
        <artifactId>vertx-core</artifactId>
        <version>4.3.3</version>
    </dependency>

    <!-- Hutool HTTP -->
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-http</artifactId>
        <version>5.8.10</version>
    </dependency>

    <!-- JUnit for testing -->
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.13.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```
