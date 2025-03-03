
# J-RPC: Lightweight and easy-to-use remote procedure call (RPC)

![Static Badge](https://img.shields.io/badge/JoolsRpc-version_1.0-blue)
![Static Badge](https://img.shields.io/badge/JDK-17-blue?logo=java)
![Static Badge](https://img.shields.io/badge/SpringBoot-3.4.0-green?logo=spring)
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
- 🔗 Transparent Service Invocation: Uses JDK dynamic proxies to enable seamless remote method calls, hiding communication details and simplifying development with annotation-driven mechanisms.
- 📦 Customizable Serialization: Supports JDK, JSON, Kryo, Hessian, and Protobuf, with dynamic loading and flexible SPI-based extension for user-defined serialization methods.
- 🗂️ Multi-Backend Registry: Supports Redis, Etcd, and ZooKeeper as service registries, with real-time updates, caching, and easy adapter switching via configuration.
- ⚡ Custom RPC Protocol: Inspired by Dubbo, implements efficient metadata transmission with custom headers and optional GZIP/Snappy compression to optimize bandwidth.
- ⚖️ Advanced Load Balancing: Offers strategies like round-robin, random, consistent hashing, and least connections, and allows custom extensions via SPI.
- 🔄 Retry Strategies: Implements fixed interval, incremental wait, exponential backoff, and advanced fault-tolerance policies like FailSafe, FailFast, FailOver, and FailBack.
- ⚙️ TCP Stability: Solves half-packet and sticky-packet issues using a decorator pattern for reliable and maintainable communication.
- 🎭 Mock Service: Generates mock service proxies for testing, allowing simulated responses with predefined or random data.
- 🔍 Real-Time Service Discovery: Leverages Etcd's Watch mechanism for real-time node updates, minimizing polling overhead and improving response speed.
- 🧩 Plugin Architecture: Enables easy extension of serialization, load balancing, and protocol handlers with a modular plugin system.
- ✨ Annotation-Driven Development: Simplifies service registration and remote proxy injection via annotations, supporting fallback and mock services.
- 🔧 Dynamic Registry Switching: Allows seamless switching between Etcd, Redis, and ZooKeeper registries without code changes.
- 🔒 Enhanced Fault Tolerance: Provides robust retry and failover mechanisms to ensure high availability, with local pseudo-service fallback for graceful degradation.
-🛡️ Two-Way Interceptors: Implements  interceptor chains, configurable via SPI with priority control.
-📌 Version-Driven Routing: Services can declare version numbers via ServiceMetaInfo 'serviceVersion' field, enabling consumers to precisely target specific implementations using semantic version matching.
- 🔐 Secure Request Context: RPC requests now carry extensible parameter lists, supporting to verify whether this RpcRequest sender had login. Support to set 'AutoLogin' via RpcConifg or SDK settings.
- ⏱️ Intelligent Timeout Escalation: Support to use default constant in 'RpcConstant'. User can reset Timeout duration by RpcConfig or SDK settings.


## 📂 Project Structure

```
jools-rpc/
│
├── jools-spring-boot-starter [jrpc-spring-boot-starter]/               # Spring Boot Starter Based on JRpc
│
├── example-springboot-consumer/              # Consumer Module Based on `jrpc-spring-boot-starter` 
│
├──example-springboot-provider/               # Provider Module Based on `jrpc-spring-boot-starter` 
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


### 1. Create a Shared Package
Before setting up the Provider and Consumer, create a shared package that contains the common entity (User) and service interface (UserService). This package will be used by both the Service Provider and Consumer.

Example: Define the Shared Entity and Service Interface
```java
// User.java
public class User {
    private String name;

    public User(String name) {
        this.name = name;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

// UserService.java
public interface UserService {
    User getUser(User user);
}
```
Package and Publish to Local Repository
Once the shared package is created, package it into a JAR file and publish it to your local Maven repository so that both the Provider and Consumer can use it.

```bash

Copy
# Package and publish the shared module to the local Maven repository
mvn clean install
```

### 2. Import Dependencies
Creat Service Provider module and Consumer module.
Add the required dependencies to your pom.xml or build.gradle file to include the framework in your project.

```xml
<!-- Example for Maven -->
<dependency>
    <groupId>com.example.rpc</groupId>
    <artifactId>jrpc-framework</artifactId>
    <version>1.0.0</version>
</dependency>
<!-- Supposed your intalled common package -->
<dependency>
  <groupId>{common-package-name}</groupId>
  <artifactId>exp-common</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```
### 3. Configure the Service Provider
Create a Service Provider application based on Spring Boot and annotate the main class with @EnableRpc to enable the RPC service functionality.
Because Provider need to start a web server. Using default value of field `needServer` in `@EnableRpc` annotation.
```java

```java
@SpringBootApplication
@EnableRpc
public class ExampleSpringbootProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringbootProviderApplication.class, args);
    }
}
```
Next, implement the service interface you want to expose as an RPC service, for example:

```java
@Service
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        user.setName("Hello, " + user.getName());
        return user;
    }
}
```


### 4. Configure the Service Consumer
Create a Service Consumer application and annotate the main class with `@EnableJRpc`.
Set the needServer parameter to false to indicate that this is a consumer-only client.

```java
@SpringBootApplication
@EnableJRpc(needServer = false)
public class ExampleSpringbootConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringbootConsumerApplication.class, args);
    }
}
```

### 5. Inject Remote Service Proxy
Use @JRpcReference to inject the remote service proxy into a Bean. The framework will transparently create a proxy object for the UserService interface and handle the RPC communication underneath.

```java
/**
 * @description: Bean class injected into the Spring container.
 * The @JRpcReference annotation allows the framework to inject
 * a proxy object for transparent RPC service calls.
 */
@Service
@Slf4j
public class ExampleServiceImpl {

    @JRpcReference
    private UserService userService;

    public void testUserService() {
        log.info("UserService field is injected by: {}", userService.getClass().getSimpleName());
        User user = new User("Jools Wakoo");
        User ans = userService.getUser(user);
        System.out.println(ans.getName());
    }
}
```



### 6. Test the Consumer Bean
Create a test class to verify the functionality of the injected UserService proxy. The framework will automatically handle the RPC communication, and the testUserService() method will call the remote service transparently.

```java
public class ExampleServiceImplTest {

    @Resource
    private ExampleServiceImpl exampleService;

    @Test
    public void testUserService() {
        exampleService.testUserService();
    }
}
```
📝 ### Summary
Service Provider: Use @EnableRpc and implement the service interface.
Service Consumer: Use @EnableJRpc and inject the service proxy using @JRpcReference.
Transparent RPC: The framework handles all the underlying communication and serialization, allowing you to focus on business logic without worrying about the RPC layer.



## 🖼️ Project Design 
### 1. Latest Framework Structure
<img src="https://github.com/user-attachments/assets/dc17c2ba-7ca5-430d-a776-a2acd5209461" width="70%">

### 2. Simplify Baisc RPC Framework Structure
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
- **Service Registry**: Etcd, ZooKeeper(Optional), Redis(Optional)
- **Spring Boot**: 3.4.0
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
