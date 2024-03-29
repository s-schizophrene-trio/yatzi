# Yatzi (JavaFX) 

[![Build Status](https://travis-ci.org/s-schizophrene-trio/yatzi.svg?branch=master)](https://travis-ci.org/s-schizophrene-trio/yatzi)

![Setup](docs/assets/yatzi.jpg) 

- [Getting Started](#getting-started)
  * [Setup Environment](#setup-environment)
  * [Setup Java FX](#setup-javafx-with-gradle)
  * [Setup Lombok](#setup-lombok)
  * [Debugging](#debugging)
- [User Interface](#user-interface)
  * [Java FX](#javafx)
  * [Screen Types](#screen-types)
  * [Screen Helper](#screen-helper)
  * [Image Handling](#image-handling)
- [Engine](#yatzigame)
  * [Yatzi Game](#yatzigame)
  * [Board](#board)
- [Networking](#networking-javanet)
  * [Implementation](#implementation)
  * [Server](#server)
  * [Client](#client)
  * [Data Transfer](#data-transfer)
  * [Protocol Functions](#transfer-functions)
- [Yatzi Project]()
  * [Naming Conventions](#naming-conventions)
  * [Design Guidelines](#identity)
- [Testing](#testing)
  * [Unit Tests](#unit-testing)
  * [Sonarqube](#static-code-testing-with-sonarqube)  
- [Configuration](#configuration)

![Setup Server](docs/assets/setup_server.png) 
![Setup Client](docs/assets/setup_client.png) 
![Board](docs/assets/board.png)
![Actions](docs/assets/choose-strocke.png)

## Getting Started

Read the `JavaDoc` at: https://s-schizophrene-trio.github.io/yatzi/

### Setup Environment

* Install `Gradle 5.3.1+`
* Install `JDK 11.0.2`
* Download `JavaFX 11`

### Setup JavaFX with gradle

* Download `SDK Version 11` at https://gluonhq.com/products/javafx/
https://openjfx.io/openjfx-docs/#gradle -> JavaFX and IntelliJ -> Non-modular with Gradle

### Setup Lombok


1. `Settings->Compiler->Annotation Processors`  [enable]
2. Install `lombok plugin` from marketplace to your idea. (restart)

#### Debugging

If you get this Error Message in IntelliJ...
```
Error: JavaFX runtime components are missing, and are required to run this application
```
You have to set some VM Options
```
--module-path /path/to/your/javafx-sdk-11.0.2/lib --add-modules=javafx.controls,javafx.fxml
```

## User Interface

### Application Architecture

![Application](docs/assets/yatzi_application.png)

### JavaFX

* https://openjfx.io/javadoc/11/javafx.graphics/javafx/scene/doc-files/cssref.html

![Main Controller](docs/assets/main_controller.png)

`MainController` is used to build, load and display a view. He is also used to initialize the board logic. 

`ScreenType` is an enum with all available Screens. Each `ScreenType` has to be implemented in the `loadScreen` method 
of the `MainController`

### Screen Types

| Type   | Description                                                                                 |
|--------|---------------------------------------------------------------------------------------------|
| BOARD  | The board view is the main view used for the yatzi game.                                |
| SETUP  | The setup view is used to initialize the game mode. The User can join or create a server. |
| STATUS | The status bar will be added on each view and can be access from each controller.         |


The `YatziApplication`, an instance of `javafx.application.Application` creates a global `ch.juventus.yatzi.ui.models.FXContext` which holds the 
main stage and the game instance of the application. The context will be accessed over the `ViewContext` interface. 
The context instance will be shared with the `MainController`. This is necessary to adapt the window-size
and other global attributes. Each `ScreenController` holds also an instance of the `ViewContext`. This means every 
component has access to global `ViewContext` and its child values. 

### Screen Helper

The `ScreenHelper` class can be loaded by a javaFX class and have access to the main stage.

![Screen Helper](docs/assets/screen_helper.png)

### Image Handling

#### Icons

* https://icons8.de/icon/set/server/ios
* https://icons8.de/icon/25226/verbunden
* http://www.holshousersoftware.com/glass

#### Get a Java Image from Resources

If you want to load an image from resource folder inside a `ScreenController`, use following methods.

```
// @param classloader The classloader of the context to access ressources
// @param subPath The sub-path in the base image folder eg. "icons/"
// @param key filename (lowercase)
// @param fileExt file ending eg. "png"
// @return A String with the relative image path
screenHelper.getImage(classloader, subpath, imageKey, fileExt)

//sample:
Image image1 = screenHelper.getImage(this.context.getClassloader(), "background/", "board_background", "jpg");

// render a image view based on this image:
ImageView imageView1 = screenHelper.renderImageView(context.getClassloader(), "icons/", "server", "png")
ImageView imageView1 = screenHelper.renderImageView(context.getClassloader(), "icons/", "server", "png", 200, 200)
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

## YatziGame

The Yatzi Game is a full instance of a game. This Object can be used on multiple clients. The communication 
between message handlers, allows the developer to to make async calls.

![Application](docs/assets/yatzi_game.png)

## Board

The Board represents the play ground of this game. The board knows all users and the state of the game.

![Board](docs/assets/board_logic.png)

## Networking (java.net)

By definition, a socket is one endpoint of a two-way communication link between two programs running on different computers on a network. A socket is bound to a port number so that the transport layer can identify the application that data is destined to be sent to.

__Sources__

* https://www.baeldung.com/a-guide-to-java-sockets

### Implementation

#### Overview

```
+---------------------------------------------------+
|                                                   |
|   Player 1 (Host)                                 |
|                                                   |
|           +-----------------+                     |
|   +-----> | Message Handler | <-------------+     |
|   |       +-----------------+               |     |
|   |                                         |     |
|   |                 +------------+      +---+--+  |
|   |   +-----------> |   Client   +----> | CT 1 |  |
|   |   |             +------------+      +------+  |
|  ++---+---+                                X      |          +-------------------------------------------+
|  |  Game  |                               TCP     |          |                                           |
|  ++---+---+                                X      |          |  Player 2                                 |
|   |   |             +------------+      +------+  |          |                                           |
|   |   +-----------> |   Server   +----> | CH 1 |  |          |                   +-----------------+     |
|   |                 +------------+      +--+----  |          |       +---------> | Message Handler |     |
|   |                                        |      |          |       |           +---------------+-+     |
|   |                +-----------------------+      |          |       |                           ^       |
|   |                |                              |          |       |                           |       |
|   |                |                    +------+  |          |   +---+--+     +--------+       +-+----+  |
|   |                |  +-----------------+ CH 2 |XX|XXXX_TCP_X|XXX| CT 1 | <---+ Client | <-----+ Game |  |
|   |                |  |                 +------+  |          |   +------+     +--------+       +------+  |
|   |                |  |                           |          |                                           |
|   |                |  |                           |          +-------------------------------------------+
|   |                |  |                           |
|   |                v  v                           |          +-------------------------------------------+
|   |                                               |          |                                           |
|   |        +-----------------+          +------+  |          |  +------+     +--------+       +------+   |
|   +------> | Message Handler | <--------+ CH n |XX|XXX_TCP_XX|XX| CT 1 | <---+ Client | <-----+ Game |   |
|            +-----------------+          +------+  |          |  +---+--+     +--------+       +-+----+   |
|                                                   |          |      |                           |        |
+---------------------------------------------------+          |      |                           v        |
                                                               |      |           +---------------+-+      |
                                                               |      +---------> | Message Handler |      |
                                                               |                  +-----------------+      |
                                                               |                                           |
                                                               |                                           |
                                                               |  Player n                                 |
 CT = Client Task                                              |                                           |
 CH = Client Handler                                           +-------------------------------------------+
```

![communication](docs/assets/communication.png)

#### Server

The Server holds all `client handlers`, and handles all messages bundled to one `message handler`.

![Server](docs/assets/server.png)

Each new client will effect in a new `client handler` thread. 

![Server](docs/assets/client_handler.png)
![Communication](docs/assets/server-communication.png) 

#### Client

The Client connects to a `server socket` and will be a member or player of the game.

![Client](docs/assets/client.png)

The `client` crates a new task to handle the `server <-> client` connection.

![Client](docs/assets/client_task.png)

### Data Transfer

| Attribute | Type   | Description                                                                          |
|-----------|--------|--------------------------------------------------------------------------------------|
| Sender    | UUID   | UUID of the sender user / client. The server does not need to provide the Sender ID. |
| Function  | String | Defines the reason for this network transfer.                                        |
| Body      | String | The needed Model translated to a JSON String                                         |
| Sent Time | Date   | Time when the transfer object was sent to the network.                               |

#### Transfer Functions

| Command             | Description                                                            | Body      |
|---------------------|------------------------------------------------------------------------|-----------|
| PLAYER_NEW          | A new player will join the the game.                                   | User      |
| MAX_PLAYERS_REACHED | The maximum of clients is reached.                                     | -         |
| WAIT_FOR_GAME_READY | The server is waiting for new players to join.                         | -         |
| GAME_READY          | The server has started the game.                                       | -         |
| CLIENT_READY        | The client is ready to start a new game.                               | -         |
| DICE_CHANGED        | The dice set has been changed.                                         | Dice[]    |
| ROUND_START         | Starts a new round of the game.                                        | YatziGame |
| GAME_CHANGED        | The game has been changed. (includes the currently active user)        | YatziGame |
| PLAYER_EXIT         | A player leaves the game early.                                        | User Id   |
| GAME_END            | The game is finished. The user can exit the game or start a new party. | -         |

### Transfer Body
The transfer body can hold every content as String. We use Json to transfer the body. A sample of our `game_changed` event.

```
{
   "board":{
      "scores":{
         "f27955ce-6a24-44a8-8dfa-e010e0e08982":{
            "TOTAL":{
               "fieldType":"TOTAL",
               "value":9,
               "isCalculated":true
            },
            "SUB_TOTAL":{
               "fieldType":"SUB_TOTAL",
               "value":9,
               "isCalculated":true
            },
            "THREES":{
               "fieldType":"THREES",
               "value":3,
               "isCalculated":false
            },
            "BONUS":{
               "fieldType":"BONUS",
               "value":0,
               "isCalculated":true
            },
            "TWOS":{
               "fieldType":"TWOS",
               "value":6,
               "isCalculated":false
            }
         },
         "efe1402d-2eb2-4570-b724-f9ce257a29a9":{
            "FOURS":{
               "fieldType":"FOURS",
               "value":4,
               "isCalculated":false
            },
            "SUB_TOTAL":{
               "fieldType":"SUB_TOTAL",
               "value":4,
               "isCalculated":true
            },
            "TOTAL":{
               "fieldType":"TOTAL",
               "value":4,
               "isCalculated":true
            },
            "BONUS":{
               "fieldType":"BONUS",
               "value":0,
               "isCalculated":true
            }
         }
      }
   },
   "activeUserId":"efe1402d-2eb2-4570-b724-f9ce257a29a9",
   "circleRoundPlayed":[
      "efe1402d-2eb2-4570-b724-f9ce257a29a9"
   ],
   "players":[
      {
         "userId":"f27955ce-6a24-44a8-8dfa-e010e0e08982",
         "userName":"client",
         "serveType":"CLIENT"
      },
      {
         "userId":"efe1402d-2eb2-4570-b724-f9ce257a29a9",
         "userName":"server",
         "serveType":"SERVER"
      }
   ]
}
```

#### Communication Flow
Each client opens a new connection to the server. The server tries to keep alive these connections. This is needed 
to prevent latency during game play due of connection establishing.

![java socket flow](https://www.oreilly.com/library/view/distributed-computing-in/9781787126992/assets/ea864328-5b66-4620-9dd8-9005c5af7986.png)

_https://www.oreilly.com/library/view/distributed-computing-in/9781787126992/assets/ea864328-5b66-4620-9dd8-9005c5af7986.png_

#### Client Handling
This Yatzi Game (Host) is able to manage multiple Clients (max 7). To make this possible, the Server
creates for each incoming Client a new Client Handler Thread. 
![java socket flow](https://cdncontribute.geeksforgeeks.org/wp-content/uploads/JavaSocketProgramming.png)

_https://cdncontribute.geeksforgeeks.org/wp-content/uploads/JavaSocketProgramming.png_

#### Configuration

Library: http://owner.aeonbits.org/docs/usage/

use the `application.properties` file in `src/main/resources/config` to configure the game parameters.

Each configuration property needs a method in the Config Interface in `src/main/java/ch/juventus/yatzi/config` 
```
/**
 * Timeout in seconds the client should wait until the timeout exceeded
 * @return The client timeout as counter
 */
@DefaultValue("30")
@Key("client.connection.timeout")
int clientTimeout();
```

### Generate JavaDoc

This project has its own javadoc theme. Use this theme for the docs assets. Add these java vm options to your start command. 

``
-encoding UTF-8 -docencoding utf-8 -charset utf-8 -stylesheetfile /path/to/your/yatzi/javadoc-style.css
``


### Build & Ship Info

* [How to run the application with gradle - stackoverflow](https://stackoverflow.com/a/52571719/5242747)

### Testing

#### Unit Testing

The Junit5 tests can be executed by `./gradlew clean test`

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
