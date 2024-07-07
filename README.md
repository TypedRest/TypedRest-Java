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
val client = MyClient(URI("http://example.com/"))

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

Read an **[Introduction](https://typedrest.net/introduction/)** to TypedRest or jump right in with the **[setup guide](https://typedrest.net/setup/java/)**.

For information about specific classes or interfaces you can read the **[API Documentation](https://java.typedrest.net/)**.

## Maven artifacts

Artifact group: [`net.typedrest`](https://mvnrepository.com/artifact/net.typedrest)

[![typedrest](https://img.shields.io/maven-central/v/net.typedrest/typedrest.svg?label=typedrest)](https://mvnrepository.com/artifact/net.typedrest/typedrest)  
The main TypedRest library.

[![typedrest-reactive](https://img.shields.io/maven-central/v/net.typedrest/typedrest-reactive.svg?label=typedrest-reactive)](https://mvnrepository.com/artifact/net.typedrest/typedrest-reactive)  
Adds support for streaming with [ReactiveX (Rx)](http://reactivex.io/).  
Create endpoints using the types in the `net.typedrest.endpoints.reactive` package.

[![typedrest-serializers-jackson](https://img.shields.io/maven-central/v/net.typedrest/typedrest-serializers-jackson.svg?label=typedrest-serializers-jackson)](https://mvnrepository.com/artifact/net.typedrest/typedrest-serializers-jackson)  
Adds support for serializing using [Jackson](https://github.com/FasterXML/jackson) instead of [kotlinx.serialization](https://kotlinlang.org/docs/serialization.html).  
Pass `new JacksonJsonSerializer()` to the `EntryEndpoint` constructor.

[![typedrest-serializers-moshi](https://img.shields.io/maven-central/v/net.typedrest/typedrest-serializers-moshi.svg?label=typedrest-serializers-moshi)](https://mvnrepository.com/artifact/net.typedrest/typedrest-serializers-moshi)  
Adds support for serializing using [Moshi](https://github.com/square/moshi) instead of [kotlinx.serialization](https://kotlinlang.org/docs/serialization.html).  
Pass `new MoshiJsonSerializer()` to the `EntryEndpoint` constructor.
