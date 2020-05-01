# ![TypedRest](logo.svg) for Java

[![Build status](https://img.shields.io/appveyor/ci/TypedRest/TypedRest-Java.svg)](https://ci.appveyor.com/project/TypedRest/TypedRest-Java)  
TypedRest for Java helps you build type-safe, fluent-style REST API clients.

Common REST patterns such as collections are represented as classes, allowing you to write more idiomatic code. For example, TypedRest lets you turn this:

```java
HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://example.com/contacts/123")).build();
HttpResponse<> response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
Contact contact = objectMapper.readValue(response.body(), Contact.class);
```

into this:

```java
MyServiceClient myService = new MyServiceClient(URI.create("http://example.com/"));
Contact contact = myService.getContacts().get("123").read();
```

## Maven artifacts

Artifact group: `com.oneandone`

[![typedrest-core](https://img.shields.io/maven-central/v/com.oneandone/typedrest-core.svg?label=typedrest-core)](https://mvnrepository.com/artifact/com.oneandone/typedrest-core)  
The main TypedRest library.

[![typedrest-annotations](https://img.shields.io/maven-central/v/com.oneandone/typedrest-annotations.svg?label=typedrest-annotations)](https://mvnrepository.com/artifact/com.oneandone/typedrest-annotations)  
Annotations for data models to be used with TypedRest.

[![typedrest-vaadin](https://img.shields.io/maven-central/v/com.oneandone/typedrest-vaadin.svg?label=typedrest-vaadin)](https://mvnrepository.com/artifact/com.oneandone/typedrest-vaadin)  
Build [Vaadin](https://vaadin.com/) GUIs for TypedRest clients.

[![typedrest-archetype](https://img.shields.io/maven-central/v/com.oneandone/typedrest-archetype.svg?label=typedrest-archetype)](https://mvnrepository.com/artifact/com.oneandone/typedrest-archetype)  
[Maven archetype](https://maven.apache.org/guides/introduction/introduction-to-archetypes.html) (template) for creating TypedRest projects.


## Documentation

Read an **[Introduction](https://typedrest.net/introduction/)** to TypedRest or jump right in with the **[Getting started](https://typedrest.net/getting-started/java/)** guide.

For information about specific classes or interfaces you can read the **[API Documentation](https://java.typedrest.net/)**.
