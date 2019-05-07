# Yatzi (JavaFX)

### Getting Started

* Install Gradle 5.3.1+
* Install JDK 11.0.2
* Download JavaFX 11


### Setup JavaFX with gradle

https://openjfx.io/openjfx-docs/#gradle -> JavaFX and IntelliJ -> Non-modular with Gradle

__Debugging__

If you get this Error Message in IntelliJ...
```
Error: JavaFX runtime components are missing, and are required to run this application
```
You have to set some VM Options
```
--module-path /path/to/your/javafx-sdk-11.0.2/lib --add-modules=javafx.controls,javafx.fxml
```

### Java FX Styling

* https://openjfx.io/javadoc/11/javafx.graphics/javafx/scene/doc-files/cssref.html

### Naming Conventions

```
Logs
=====
* all characters should be writtern small
* LOGGER.debug("application started in {}ms", elapsedTime);
```

