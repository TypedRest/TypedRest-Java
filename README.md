# TypedRest

TypedRest helps you build type-safe fluent-style JSON REST API clients.

Maven package:
* Artifact `typedrest` in Group `com.1and1`


## Usecase sample

We'll use this simple POJO (Plain old Java object) class modelling software packages as an example:
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
  <version>1.0-SNAPSHOT</version>
</dependency>
```

You can then use the classes `RestEntryPoint`, `RestSet` and `RestElement` to build a local representation of a remote REST service. Based on our usecase sample this could look like this:
```java
class SampleRestEntryPoint : RestEntryPoint {
  public final RestSet<Package> packages = new RestSetImpl<>(this, "packages");

  public SampleRestEntryPoint(URI uri) {
    super(uri);
  }
}
```

You can then perform CRUD operations like this:
```java
SampleRestEntryPoint server = new SampleRestEntryPoint(URI.create("http://myservice/api/"));
Iterable<Package> packages = server.packages.readAll();
RestElement<Package> element = server.packages.create(new Package(...));
Package package = server.packages.get(1).read();
server.Packages.get(1).update(package);
server.Packages.get(1).delete();
```
