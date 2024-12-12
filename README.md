
# jools-rpc - A Simple RPC Framework

![Static Badge](https://img.shields.io/badge/JoolsRpc-version_1.0-blue)
![Static Badge](https://img.shields.io/badge/JDK-17-blue?logo=java)
![Static Badge](https://img.shields.io/badge/etcd-v3.5.16-blueviole?logo=etcd)
![Static Badge](https://img.shields.io/badge/Maven-3.8.1-C71A36?logo=apachemaven)
![Static Badge](https://img.shields.io/badge/Vert.x-4.3.3-green?logo=eclipsevert.x)
![Static Badge](https://img.shields.io/badge/Hutool-v5.8.10-blueviolet?logo=hu)
![Static Badge](https://img.shields.io/badge/Guava-32.1.2-jellow?logo=google) 
![Static Badge](https://img.shields.io/badge/Redis-blue?logo=redis) 
![Static Badge](https://img.shields.io/badge/ZooKeeper-gream?logo=apachezookeeper) 

## Introduction
`JoolsRPC` is a lightweight RPC (Remote Procedure Call) framework implemented in Java, designed to simplify the process of remote service invocation. It provides an easy-to-use structure with a focus on flexibility, extensibility, and simplicity. This framework is ideal for learning purposes or for small-scale applications requiring lightweight RPC capabilities.

## ✨ Features
- 🚀 Asynchronous Communication: Built with Vert.x for high-performance, non-blocking TCP communication, supporting serialization, transmission, and service registration.
- 🔗 Transparent Service Invocation: Uses JDK dynamic proxies to enable seamless remote method calls without exposing communication details.
- 📦 Serialization Options: Supports JDK, JSON, Kryo, Hessian, and Protobuf, with flexible SPI-based extension.
- 🗂️ Registry: Supports Redis, Etcd, and ZooKeeper as service registry centers, with real-time node updates and caching.
- ⚡ Custom RPC Protocol: Designed an efficient RPC protocol inspired by Dubbo, optimizing transmission and parsing.
- ⚖️ Load Balancing: Includes algorithms like round-robin, random, consistent hashing, and weighted strategies.
- 🔄 Retry Mechanism: Implements fixed interval, incremental wait, and exponential backoff retry strategies using Guava Retrying.
- ⚙️ Global Configuration: Supports multi-environment configurations via Hutool and SnakeYAML.
- 🎭 Mock Service: Generates mock proxy objects to return simulated data for testing purposes.

## 🖼️ Project Design 
### 1. Latest Framework Structure
<img src="https://github.com/user-attachments/assets/5bcde6ca-3abd-424a-ab5e-a60771048bfa" alt="Jools-RPC-5.0 [Add Retry Strategies]" width="70%">

### 2. Simplify Key RPC Components
<img src="https://github.com/user-attachments/assets/8c488da8-7998-4c3d-8051-acfbdef037d7" alt="Simplify basic RPC framework" width="70%">

### 3. Key Functions of RPC Framework
<img src="https://github.com/user-attachments/assets/a7339c2b-b037-4ffe-835b-e82eacb647a0" alt="Key Functions" width="70%">

### 4. Customized Design Protocol Structure
<img src="https://github.com/user-attachments/assets/f0faa1e5-90a4-4977-bbbb-36254abe5e24" alt="Protocol Message Design" width="70%">

### 5. Decode and Encode for Customized Protocol
<img src="https://github.com/user-attachments/assets/358f761a-7941-4312-ae88-13390b166d4f" alt="Decode and Encode Diagram" width="70%">

## 🛠️ Technology Stack

- **Language**: Java
- **JDK Version**: 17
- **Build Tool**: Maven
- **Web Server**: Vert.x
- **Libraries**:
  - **Hutool**: For handling HTTP requests (`HTTPRequest`).
  - **Guava-Retrying**: For implementing retry mechanisms.
  - **Jedis**: For interacting with Redis databases.
  - **Mockito**: For mocking dependencies in unit tests.
  - **Curator**: For Zookeeper-based service discovery.
  - **Jetcd**: For interacting with Etcd as a distributed key-value store.
  - **Protobuf**: For protocol buffer-based serialization.
  - **SnakeYAML**: For parsing and working with YAML configuration files.
  - **Jackson-Databind**: For JSON serialization and deserialization.
  - **Hessian**: For RPC-based object serialization.
  - **Kryo**: For high-performance object serialization.
  - **JavaFaker**: For generating fake data in tests or mock scenarios.

## 📂 Project Structure

```
jools-rpc/
│
├── exp-consumer/               # Consumer module
│
├── exp-provider/               # Provider module
│
├── jools-rpc-core/             # Main module for Framework. Implement multi functions
│
├── jools-rpc-basic/            # Basic & simple framework just support local service register and discovert
│
└── pom.xml                         # Maven build file
```

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- **JDK 17** or higher
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
public class BasicProviderExample {

    public static void main(String[] args) {

        //Registry services
        LocalRegistry.register("UserService", UserServiceImpl.class);

        //Call services
        HttpServer vertxServer = new VertxHttpServer();
        vertxServer.doStart(8888);
    }
}
```

## 🔧 How It Works

1. **Service Registration**: Upon starting the `VertxHttpServer`, services are registered in the local registry (`LocalRegistry`) using a map where the key is the service name, and the value is the fully qualified class name.
2. **Service Consumption**: The consumer requests a proxy object of `UserService` using JDK dynamic proxies. Before invoking `getUser()`, an HTTP request is sent to the server using `HTTPRequest` (Hutool library).
3. **Request Handling**: The `HttpServerHandler` receives the request, looks up the service in the registry, and invokes the corresponding method using reflection.
4. **Serialization**: The request and response are serialized and deserialized using `JdkSerializer` for efficient network transmission.

## 📦 Dependencies

```xml
<dependency>
            <groupId>com.github.rholder</groupId>
            <artifactId>guava-retrying</artifactId>
            <version>2.0.0</version>
        </dependency>
        <!--        Redis -->
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>4.3.1</version>
        </dependency>
        <!--        接口 Mock-->
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>3.12.4</version> <!-- 或者更高版本 -->
            <scope>test</scope>
        </dependency>
        <!--        zookeeper-->
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-x-discovery</artifactId>
            <version>5.1.0</version>
        </dependency>
        <!-- Java - etcd 客户端-->
        <dependency>
            <groupId>io.etcd</groupId>
            <artifactId>jetcd-core</artifactId>
            <version>0.8.0</version>
        </dependency>
        <dependency>
            <groupId>com.google.protobuf</groupId>
            <artifactId>protobuf-java</artifactId>
            <version>3.20.3</version>
        </dependency>
        <dependency>
            <groupId>io.vertx</groupId>
            <artifactId>vertx-core</artifactId>
            <version>4.5.1</version>
        </dependency>
        <!--        Yaml配置类解析-->
        <dependency>
            <groupId>org.yaml</groupId>
            <artifactId>snakeyaml</artifactId>
            <version>2.2</version>
        </dependency>
        <!-- SLF4J API -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.7</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/ch.qos.logback/logback-classic -->
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.3.12</version>
        </dependency>
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.8.16</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.13.4</version>
        </dependency>
        <!-- 序列化 -->
        <!-- https://mvnrepository.com/artifact/com.caucho/hessian -->
        <dependency>
            <groupId>com.caucho</groupId>
            <artifactId>hessian</artifactId>
            <version>4.0.66</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/com.esotericsoftware/kryo -->
        <dependency>
            <groupId>com.esotericsoftware</groupId>
            <artifactId>kryo</artifactId>
            <version>5.6.0</version>
        </dependency>
        <dependency>
            <groupId>com.github.javafaker</groupId>
            <artifactId>javafaker</artifactId>
            <version>1.0.2</version>
        </dependency>
```
