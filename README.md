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

`MainController` is used to build, load and display a view. He is also used to initialize the board logic. 

`ScreenType` is an enum with all available Screens. Each `ScreenType` has to be implemented in the `loadScreen` method 
of the `MainController`


| Type   | Description                                                                                 |
|--------|---------------------------------------------------------------------------------------------|
| BOARD  | The board view is the main view used for the yatzi game.                                |
| SETUP  | The setup view is used to initialize the game mode. The User can join or create a server. |
| STATUS | The status bar will be added on each view and can be access from each controller.         |


The `YatziApplication` represents the global context and holds the main stage of the application. 
The context of this class will be shared with the `MainController`. This is necessary to adapt the window-size
and other global attributes. Each `ScreenController` holds an instance of the `MainController`. This means every 
component has access to `MainController` and `YatziApplication`. 

#### Icons

* https://icons8.de/icon/set/server/ios
* https://icons8.de/icon/25226/verbunden
* http://www.holshousersoftware.com/glass

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
------------------------------------------------------------------
* all characters should be written in lowercase
* LOGGER.debug("application started in {}ms", elapsedTime);

Comments
------------------------------------------------------------------
// the whole comment should be written in lowercase

JavaDoc
------------------------------------------------------------------
/**
 * Describes the funcionality of the method.
 * @param param1 Each Text should start with a Uppercase Character
 * @param param2 Each Parameter has to be described
 * @return Describes the Return Value
 */
 public String testMethod(String param1, String param2) {
    return new String("test-string");
 }
```

### Networking (java.net)

By definition, a socket is one endpoint of a two-way communication link between two programs running on different computers on a network. A socket is bound to a port number so that the transport layer can identify the application that data is destined to be sent to.

__Sources__

* https://www.baeldung.com/a-guide-to-java-sockets

#### Communication Flow
In this illustration the java socket flow is visualized. (Single Server Socket and Client Socket) 
![java socket flow](https://vichargrave.github.io/assets/images/Socket-Workflow.png)

#### Client Handling
This Yatzi Game (Host) is able to manage multiple Clients (max 7). To make this possible, the Server
creates for each incoming Client a new Client Handler Thread. 
![java socket flow](https://cdncontribute.geeksforgeeks.org/wp-content/uploads/JavaSocketProgramming.png)

### Build & Ship Info

* [How to run the application with gradle - stackoverflow](https://stackoverflow.com/a/52571719/5242747)

#### Static Code testing with SonarQube
```
$ docker-compose up
$ ./gradlew sonarqube \
   -Dsonar.host.url=http://localhost:9000 \
   -Dsonar.login=<your-token>
```

### Identity

__Colors__

* yatzi-blue: __#113f89__
* yatzi-gray: __#dbdbdb__
