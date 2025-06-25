<p align="center">
<h3 align="center">Reflect</h3>

------

<p align="center">
A lightweight Java reflection utility library providing a fluent, cached API for inspecting and manipulating classes, fields, methods, constructors, annotations, and modifiers.
</p>

------

<p align="center">
<img alt="License" src="https://img.shields.io/github/license/CKATEPTb-commons/Reflect">
<a href="https://docs.gradle.org/8.14/release-notes.html"><img src="https://img.shields.io/badge/Gradle-8.14-brightgreen.svg?colorB=469C00&logo=gradle" alt=""></a>
<a href="https://repo.jyraf.com/service/rest/v1/search/assets/download?sort=version&repository=maven-releases&maven.groupId=dev.ckateptb.commons&maven.artifactId=Reflect&maven.extension=jar&maven.classifier=" target="_blank"><img alt="Download" src="https://img.shields.io/nexus/r/dev.ckateptb.commons/Reflect?server=https%3A%2F%2Frepo.jyraf.com"></a>
</p>

---

## ⚙️ Requirements

* Java 11 or above

## ✨ Features

* Fluent API: Chainable methods for inspecting classes, fields, methods, and constructors.

* Instance Binding: Easily bind and operate on existing object instances.

* Type-Safe Wrappers: Reflective handles provide compile-time safety and automatic generic casting.

* High Performance: Internal caching of reflection metadata and use of MethodHandle/VarHandle optimizations.

* Annotation & Modifier Queries: Simplified APIs to check annotations and modifiers on members.

* Functional Utilities: map, flatMap, and peek for functional-style reflection flows.

## 📦 Installation

⚙️ Gradle (Kotlin DSL – build.gradle.kts)

```kts
repositories {
    maven("https://repo.jyraf.com/repository/maven-public/")
}

dependencies {
    implementation("dev.ckateptb.commons:Reflect:<version>")
}
```

⚙️ Gradle (Groovy DSL – build.gradle)

```groovy
repositories {
    maven {
        url 'https://repo.jyraf.com/repository/maven-public/'
    }
}

dependencies {
    implementation "dev.ckateptb.commons:Reflect:<version>"
}
```

## 🧰 Usage

This guide walks you through the most common reflection tasks using the Reflect API, organized by categories. Inline
comments explain each step for clarity.

### 📦 Example Domain Classes

```java

@AllArgsConstructor
public class Person {
    private final String name;         // immutable name field
    private int age;                   // mutable age field

    public String greet(String other) { // simple greeting method
        return "Hello, " + other + "! I'm " + name;
    }
}
```

### 🔨 Constructors

```java
// 1. Get all declared constructors
Reflect.on(Person .class)
       .

getConstructors();

// 2. Find constructor by parameter types
Reflect.

on(Person .class)
       .

getConstructorWithParameters(String .class, int.class)
       .

ifPresent(ctor -> /* use ctor */);

// 3. Filter constructors (e.g., two-arg only)
        Reflect.

on(Person .class)
       .

getConstructorsByFilter(c ->c.

getParameters().

size() ==2);

// 4. Default (no-arg) constructor
        Reflect.

on(Person .class)
       .

getDefaultConstructor();
```

Creating Instances

```java
// Create a new Person using the (String, int) constructor
Person p = Reflect.on(Person.class)
                .getConstructorWithParameters(String.class, int.class)
                .orElseThrow()
                .invoke("Reflect", 3)   // returns IReflectClass<Person>
                .getValue();            // unwrap to Person
```

🧍 Instance Binding

```java
// Bind an existing instance for field/method ops
Person existing = new Person("Alice", 30);
IReflectClass<Person> ctx = Reflect.on(existing);
// or equivalently:
ctx =Reflect.

on(Person .class)
         .

setValue(existing);
```

### 🔍 Fields

```java
// List all fields
Reflect.on(existing).

getFields();

// String-typed fields only
Reflect.

on(Person .class)
       .

getFieldsWithType(String .class);

// Fields annotated with @Nullable
Reflect.

on(Person .class)
       .

getFieldsWithAnnotation(Nullable .class);

// Field lookup by name
Reflect.

on(Person .class)
       .

getFieldWithName("name")
       .

ifPresent(field -> /* use field */);
```

Getting and Setting Values

```java
// Update an instance field value
Reflect.on(existing)
       .

getFieldWithName("name")
       .

orElseThrow()
       .

setValue("Bob")                     // set field to "Bob"
       .

updateValue(old ->old +" Smith") // append suffix
        .

getValue();                          // read back: "Bob Smith"
```

### 🚀 Methods

```java
// List all methods
Reflect.on(Person .class).

getMethods();

// Find by name
Reflect.

on(Person .class)
       .

getMethodsWithName("greet");

// Methods returning String
Reflect.

on(Person .class)
       .

getMethodsWithReturnType(String .class);

// Methods taking a single String parameter
Reflect.

on(Person .class)
       .

getMethodsWithParameters(String .class);

// Lookup by parameter name
Reflect.

on(Person .class)
       .

getMethodsWithParameters("other");
```

Invocation Example

```java
String msg = Reflect.on(existing)
        .getMethodWithNameAndParameters("greet", String.class)
        .orElseThrow()
        .invoke("World")         // returns IReflectClass<String>
        .<String>cast()          // cast reflector to String type
        .getValue();             // unwrap: "Hello, World! I'm Alice"
```

### 🤝 Fluent Utilities

```java
// Peek into the reflective chain for debugging
Reflect.on(Person .class)
       .

peek(rc ->System.out.

println("Type: "+rc.getType()))
        .

getFields()
       .

forEach(f ->System.out.

println(f.getName()));

// Map and flatMap for functional flows
int age = Reflect.on(existing)
        .map(rc -> rc.getFieldWithName("age").orElseThrow().getValue())
        .peek(a -> System.out.println("Age before: " + a))
        .flatMap(a -> Reflect.on(a + 5))
        .getValue();  // computes new age value
```

### ⚠️ Note: For more detailed functionality and advanced use cases, please refer to the generated Javadoc or explore the full source code in the repository.

------

# License

This project is licensed under
the [LGPL-3.0 license](https://github.com/CKATEPTb-commons/Reflect/blob/development/LICENSE.md).

# 🛠️ Contributing

Contributions are welcome! Feel free to open an issue or submit a pull request.