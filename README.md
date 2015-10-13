# TypedRest

TypedRest helps you build type-safe fluent-style JSON REST API clients.

Maven artifact:
* `com.1and1.typedrest`


## Nomenclature

We use the following terms in the library and documentation:
* An __entity__ is a data transfer object that can be serialized as JSON.
* An __endpoint__ is a REST resource at a specific URI.
* An __entry point__ is an _endpoint_ that is the top-level URI of a REST interface.
* An __element__ is an _endpoint_ that represents a single _entity_.
* A __set__ is an _endpoint_ that represents a collection of _entities_ and provides an _element_ for each of them.


## Usecase sample

We'll use this simple POJO (Plain old Java object) class modelling software packages as a sample _entity_ type:
```java
class Package {
  private int id;
  public int getId() { return id; }
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
  <groupId>com.1and1</groupId>
  <artifactId>typedrest</artifactId>
  <version>0.1</version>
</dependency>
```

You can then use the classes `EntryPoint`, `CollectionEndpointImpl` and `ElementEndpointImpl` to build a local representation of a remote REST service. Based on our usecase sample this could look like this:
```java
class SampleEntryEndpoint extends EntryEndpoint {
  public final CollectionEndpoint<Package> packages = new CollectionEndpointImpl<>(this, "packages", Package.class);

  public SampleEntryEndpoint(URI uri) {
    super(uri);
  }
}
```

You can then perform CRUD operations like this:
```java
SampleEntryEndpoint server = new SampleEntryEndpoint(URI.create("http://myservice/api/"));
Iterable<Package> packages = server.packages.readAll();
ElementEndpoint<Package> element = server.packages.create(new Package(...));
Package package = server.packages.get(1).read();
server.Packages.get(1).update(package);
server.Packages.get(1).delete();
```
