# ![TypedRest](logo.svg) for Java/Kotlin

TypedRest for Java/Kotlin helps you build type-safe, fluent-style REST API clients. Common REST patterns such as collections are represented as classes, allowing you to write more idiomatic code.

**Java**

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

**Kotlin**

```kotlin
val client = MyClient(URI.create("http://example.com/"))

// GET /contacts
val contactList: List<Contact> = client.contacts.readAll()

// POST /contacts -> Location: /contacts/1337
val smith: ContactEndpoint = client.contacts.create(Contact("Smith"))
//val smith: ContactEndpoint = client.contacts["1337"]

// GET /contacts/1337
val contact: Contact = smith.read()

// PUT /contacts/1337/note
smith.note.set(Note("some note"))

// GET /contacts/1337/note
val note: Note = smith.note.read()

// DELETE /contacts/1337
smith.delete()
```

Read an **[Introduction](https://typedrest.net/introduction/)** to TypedRest or jump right in with the **[Getting started](https://typedrest.net/getting-started/java/)** guide.

For information about specific Java classes or interfaces you can read the **[API Documentation](https://java.typedrest.net/)**.

## Maven artifacts

Artifact group: `io.typedrest`

[![typedrest-core](https://img.shields.io/maven-central/v/io.typedrest/typedrest.svg?label=typedrest)](https://mvnrepository.com/artifact/io.typedrest/typedrest)  
The main TypedRest library.

[![typedrest-annotations](https://img.shields.io/maven-central/v/io.typedrest/typedrest-annotations.svg?label=typedrest-annotations)](https://mvnrepository.com/artifact/io.typedrest/typedrest-annotations)  
Annotations for data models to be used with TypedRest.
