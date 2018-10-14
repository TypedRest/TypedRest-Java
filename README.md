# TypedRest for Java

[![Build status](https://img.shields.io/appveyor/ci/TypedRest/typedrest-java.svg)](https://ci.appveyor.com/project/TypedRest/typedrest-java)

TypedRest helps you build type-safe fluent-style REST API clients.

Maven artifacts (group `com.oneandone`):  
[![typedrest-annotations](https://img.shields.io/maven-central/v/com.oneandone/typedrest-annotations.svg?label=typedrest-annotations)](https://mvnrepository.com/artifact/com.oneandone/typedrest-annotations)
[![typedrest-core](https://img.shields.io/maven-central/v/com.oneandone/typedrest-core.svg?label=typedrest-core)](https://mvnrepository.com/artifact/com.oneandone/typedrest-core)
[![typedrest-vaadin](https://img.shields.io/maven-central/v/com.oneandone/typedrest-vaadin.svg?label=typedrest-vaadin)](https://mvnrepository.com/artifact/com.oneandone/typedrest-vaadin)
[![typedrest-archetype](https://img.shields.io/maven-central/v/com.oneandone/typedrest-archetype.svg?label=typedrest-archetype)](https://mvnrepository.com/artifact/com.oneandone/typedrest-archetype)

**Important:** [Lombok](https://projectlombok.org/), a build-time dependency, does not support Java 10 yet. However, the resulting artifact will work on Java 10.


## Nomenclature

We use the following terms in the library and documentation:
* An __entity__ is a data transfer object that can be serialized (usually as JSON).
* An __endpoint__ is a REST resource at a specific URI.
* An __entry endpoint__ is an _endpoint_ that is the top-level URI of a REST interface.
* An __element endpoint__ is an _endpoint_ that represents a single _entity_.
* A __collection endpoint__ is an _endpoint_ that represents a collection of _entities_ and provides an _element endpoint_ for each of them.
* A __trigger endpoint__ is an _endpoint_ that represents an RPC call to trigger a single action (intentionally un-RESTful).


## Usecase sample

We'll use this simple POJO (Plain old Java object) class modeling software packages as a sample _entity_ type:
```java
class PackageEntity {
  private int id;
  @Id public int getId() { return id; }
  public void setId(int id) { this.id = id; }

  private String name;
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
}
```


## Getting started

Include this in your Maven ```pom.xml``` to use the library:
```xml
<dependency>
  <groupId>com.oneandone</groupId>
  <artifactId>typedrest-core</artifactId>
  <version>0.30</version>
</dependency>
```

You can then use the classes `EntryEndpoint`, `CollectionEndpointImpl`, `ElementEndpointImpl`, `PollingEndpointImpl`, `ActionEndpointImpl`, `PaginationEndpointImpl`, `StreamEndpointImpl` and `BlobEndpointImpl` to build a local representation of a remote REST service. Based on our usecase sample this could look like this:
```java
class SampleEntryEndpoint extends EntryEndpoint {
  public SampleEntryEndpoint(URI uri) {
    super(uri);
  }

  public CollectionEndpoint<PackageEntity> getPackages() {
    return new CollectionEndpointImpl<>(this, "packages", PackageEntity.class);
  }
}
```

You can then perform CRUD operations like this:
```java
SampleEntryEndpoint server = new SampleEntryEndpoint(URI.create("http://myservice/api/"));
List<PackageEntity> packages = server.packages.readAll();
ElementEndpoint<PackageEntity> element = server.packages.create(new PackageEntity(...));
PackageEntity pack = server.packages.get(1).read();
server.Packages.get(1).update(pack);
server.Packages.get(1).delete();
```


## Build GUI clients

Include this in your Maven ```pom.xml``` to build GUIs with [Vaadin](https://vaadin.com/):
```xml
<dependency>
  <groupId>com.oneandone</groupId>
  <artifactId>typedrest-vaadin</artifactId>
  <version>0.27</version>
</dependency>
```
