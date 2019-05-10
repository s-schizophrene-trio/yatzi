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

### JavaFX

* https://openjfx.io/javadoc/11/javafx.graphics/javafx/scene/doc-files/cssref.html

```
         +------------------+
         |  MainController  |
         +------------------+
                 ||
                 ||
+------------+   ||    +-------------+
|SetupScreen +<------->+ BoardScreen |
+------------+         +-------------+
```

`MainController` is used to build, load and display a screen. He is also used to initialize the board logic. 

`ScreenType` is an enum with all available Screens. Each `ScreenType` has to be implemented in the `loadScreen` method 
of the `MainController`

| Type  | Description                                                                                 |
|-------|---------------------------------------------------------------------------------------------|
| BOARD | The board screen is the main screen used for the yatzi game.                                |
| SETUP | The setup screen is used to initialize the game mode. The User can join or create a server. |

#### Icons

* https://icons8.de/icon/set/server/ios
* https://icons8.de/icon/25226/verbunden

#### Get a Java Image from Resources

If you want to load an image from resource folder inside a `ScreenController`, use following methods.

```
// @param subPath The sub-path in the base image folder eg. "icons/"
// @param key filename (lowercase)
// @param fileExt file ending eg. "png"
// @return A String with the relative image path
this.mainController.getImage("icons/", imageKey, fileExt)

//sample:
Image image1 = this.mainController.getImage("icons/", "server", "png")

// render a image view based on this image:
new ImageView(this.mainController.getImage("icons/", imageKey, fileExt));
```

### Naming Conventions

```
Logs
=====
* all characters should be writtern small
* LOGGER.debug("application started in {}ms", elapsedTime);
```



