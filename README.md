# ![TypedRest](logo.svg) for Java/Kotlin

[![Build](https://github.com/TypedRest/TypedRest-Java/actions/workflows/build.yml/badge.svg)](https://github.com/TypedRest/TypedRest-Java/actions/workflows/build.yml)
[![API documentation](https://img.shields.io/badge/api-docs-orange.svg)](https://java.typedrest.net/)

**Describe your REST API as a tree of endpoints, not a list of HTTP calls.**

TypedRest for Java/Kotlin helps you build type-safe, fluent-style REST API clients. Common REST patterns are represented as classes that you compose to mirror the URL structure of your API.

Tell TypedRest that `/contacts` is a collection and get type-safe listing, creating, reading, updating and deleting in return. Unlike typical generated clients, which give you one flat method per operation, TypedRest gives you a navigable tree of endpoints.

## Define your client

A few lines are enough to describe an API with a collection of contacts plus a "notes" endpoint nested underneath:

**Kotlin**

```kotlin
class MyClient(uri: URI) : EntryEndpoint(uri) {
  val contacts: ContactCollectionEndpoint
    get() = ContactCollectionEndpoint(this)
}

class ContactCollectionEndpoint(referrer: Endpoint) :
  GenericCollectionEndpointImpl<Contact, ContactEndpoint>(referrer, "./contacts", Contact::class.java, ::ContactEndpoint)

class ContactEndpoint(referrer: Endpoint, relativeUri: URI) :
  ElementEndpointImpl<Contact>(referrer, relativeUri, Contact::class.java) {
  val note: ElementEndpoint<Note>
    get() = ElementEndpointImpl(this, "./note", Note::class.java)
}
```

<details>
<summary><b>Java</b></summary>

```java
class MyClient extends EntryEndpoint {
  public MyClient(URI uri) {
    super(uri);
  }

  public ContactCollectionEndpoint getContacts() {
    return new ContactCollectionEndpoint(this);
  }
}

class ContactCollectionEndpoint extends GenericCollectionEndpointImpl<Contact, ContactEndpoint> {
  public ContactCollectionEndpoint(Endpoint referrer) {
    super(referrer, "./contacts", Contact.class, ContactEndpoint::new);
  }
}

class ContactEndpoint extends ElementEndpointImpl<Contact> {
  public ContactEndpoint(Endpoint referrer, URI relativeUri) {
    super(referrer, relativeUri, Contact.class);
  }

  public ElementEndpoint<Note> getNote() {
    return new ElementEndpointImpl<>(this, "./note", Note.class);
  }
}
```

</details>

## Use it

Navigate the API like an object model, with code completion guiding the way:

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

<details>
<summary><b>Java</b></summary>

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

</details>

## Learn more

Read a more detailed **[Introduction](https://typedrest.net/introduction/)** to TypedRest or jump right in with the **[setup guide](https://typedrest.net/setup/java/)**.  
You can also **[build your first client](https://typedrest.net/tutorials/first-client/)** step by step or **[generate one from OpenAPI](https://typedrest.net/tutorials/from-openapi/)**.

Take a look at our [Sample project](https://github.com/TypedRest/Sample-Kotlin) to see TypedRest in action.

## Maven artifacts

Artifact group: [`net.typedrest`](https://central.sonatype.com/artifact/net.typedrest)

[![typedrest](https://img.shields.io/maven-central/v/net.typedrest/typedrest.svg?label=typedrest)](https://central.sonatype.com/artifact/net.typedrest/typedrest)  
The main TypedRest library.

[![typedrest-reactive](https://img.shields.io/maven-central/v/net.typedrest/typedrest-reactive.svg?label=typedrest-reactive)](https://mvnrepository.com/artifact/net.typedrest/typedrest-reactive)  
Adds support for streaming with [ReactiveX (Rx)](http://reactivex.io/).  
Create endpoints using the types in the `net.typedrest.endpoints.reactive` package.

[![typedrest-serializers-moshi](https://img.shields.io/maven-central/v/net.typedrest/typedrest-serializers-moshi.svg?label=typedrest-serializers-moshi)](https://central.sonatype.com/artifact/net.typedrest/typedrest-serializers-moshi)  
Adds support for serializing to JSON using [Moshi](https://github.com/square/moshi) instead of [kotlinx.serialization](https://kotlinlang.org/docs/serialization.html).  
Pass `new MoshiJsonSerializer()` to the `EntryEndpoint` constructor.

[![typedrest-serializers-jackson](https://img.shields.io/maven-central/v/net.typedrest/typedrest-serializers-jackson.svg?label=typedrest-serializers-jackson)](https://central.sonatype.com/artifact/net.typedrest/typedrest-serializers-jackson)  
Adds support for serializing to JSON using [Jackson](https://github.com/FasterXML/jackson) instead of [kotlinx.serialization](https://kotlinlang.org/docs/serialization.html).  
Pass `new JacksonJsonSerializer()` to the `EntryEndpoint` constructor.

[![typedrest-serializers-jackson-xml](https://img.shields.io/maven-central/v/net.typedrest/typedrest-serializers-jackson-xml.svg?label=typedrest-serializers-jackson-xml)](https://central.sonatype.com/artifact/net.typedrest/typedrest-serializers-jackson-xml)  
Adds support for serializing to XML using [Jackson XML](https://github.com/FasterXML/jackson-dataformat-xml).  
Pass `new JacksonXmlSerializer()` to the `EntryEndpoint` constructor.

[![typedrest-serializers-xmlutil](https://img.shields.io/maven-central/v/net.typedrest/typedrest-serializers-xmlutil.svg?label=typedrest-serializers-xmlutil)](https://central.sonatype.com/artifact/net.typedrest/typedrest-serializers-xmlutil)  
Adds support for serializing to XML using [XmlUtil](https://github.com/pdvrieze/xmlutil).  
Pass `new XmlUtilSerializer()` to the `EntryEndpoint` constructor.

[![typedrest-serializers-jaxb](https://img.shields.io/maven-central/v/net.typedrest/typedrest-serializers-jaxb.svg?label=typedrest-serializers-jaxb)](https://central.sonatype.com/artifact/net.typedrest/typedrest-serializers-jaxb)  
Adds support for serializing to XML using [JAXB](https://github.com/eclipse-ee4j/jaxb-ri).  
Pass `new JaxbSerializer()` to the `EntryEndpoint` constructor.
