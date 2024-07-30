<p align="center">
<h3 align="center">Reflect</h3>

------

<p align="center">
Reflect is a lightweight Java library designed to simplify and enhance the use of reflection. It provides a fluent and intuitive API for accessing classes, methods, fields, constructors, and annotations.
</p>

<p align="center">
<img alt="License" src="https://img.shields.io/github/license/CKATEPTb-commons/Reflect">
<a href="https://docs.gradle.org/7.5/release-notes.html"><img src="https://img.shields.io/badge/Gradle-7.4-brightgreen.svg?colorB=469C00&logo=gradle"></a>
<a href="https://discord.gg/P7FaqjcATp" target="_blank"><img alt="Discord" src="https://img.shields.io/discord/925686623222505482?label=discord"></a>
<a href="https://repo.jyraf.com/service/rest/v1/search/assets/download?sort=version&repository=maven-snapshots&maven.groupId=dev.ckateptb&maven.artifactId=Reflect&maven.extension=jar" target="_blank"><img alt="Download" src="https://img.shields.io/nexus/s/dev.ckateptb/Reflect?server=https%3A%2F%2Frepo.jyraf.com"></a>
</p>

------

# Versioning

We use [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html) to manage our releases.

# Features
- [X] **Easy Class and Object Reflection:** Access class and object properties effortlessly.
- [X] **Method and Field Access:** Invoke methods and manipulate fields dynamically.
- [X] **Constructor Handling:** Create new instances using constructors with or without parameters.
- [X] **Annotation Inspection:** Inspect methods, fields, and constructors for specific annotations.
- [X] **Caching:** Efficient reflection with caching of class information.

------

# Installation

To use the Reflect library, include it in your project as a dependency.

```groovy
repositories {
    maven("https://repo.jyraf.com/repository/maven-snapshots/")
}

dependencies {
    implementation("dev.ckateptb:Reflect:<version>")
}
```

------

# Usage

```java
import dev.ckateptb.reflection.Reflect;

// Reflecting on an object
MyClass instance = new MyClass();
Reflect<MyClass> reflect = Reflect.on(instance);

// Reflection on a class
Reflect<MyClass> reflect = Reflect.on(MyClass.class);

// Accessing fields
String fieldValue = reflect.field("fieldName").get(); // or set

// Invoking methods
reflect.method("methodName").call();

// Creating new instances
MyClass newInstance = reflect.constructor().newInstance().get();
```

------

# License
This project is licensed under the [GPL-3.0 license](https://github.com/CKATEPTb-commons/Reflect/blob/development/LICENSE.md).

# Contributing
Contributions are welcome! Feel free to open an issue or submit a pull request.

# Acknowledgements
Special thanks to the developers and contributors of the open-source Java reflection libraries that inspired the creation of Reflect.