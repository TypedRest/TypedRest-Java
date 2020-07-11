# ![TypedRest](logo.svg) for Java

[![Build status](https://img.shields.io/appveyor/ci/TypedRest/TypedRest-Java.svg)](https://ci.appveyor.com/project/TypedRest/TypedRest-Java)  
TypedRest for Java helps you build type-safe, fluent-style REST API clients. Common REST patterns such as collections are represented as classes, allowing you to write more idiomatic code.

```java
MyClient client = new MyClient(URI.create("http://example.com/"));

// GET /contacts
List<Contact> contactList = client.getContacts().readAll();

// POST /contacts -> Location: /contacts/1337
ContactEndpoint smith = client.getContacts().create(new Contact("Smith"));
//ContactEndpoint smith = client.getContacts().get("1337");

// GET /contacts/1337
Contact contact = smith.read();

// PUT /contacts/1337/note
smith.getNote().set(new Note("some note"));

// GET /contacts/1337/note
Note note = smith.getNote().read();

// DELETE /contacts/1337
smith.delete();
```

Read an **[Introduction](https://typedrest.net/introduction/)** to TypedRest or jump right in with the **[Getting started](https://typedrest.net/getting-started/java/)** guide.

For information about specific Java classes or interfaces you can read the **[API Documentation](https://java.typedrest.net/)**.

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
