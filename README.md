# (Java FX) PathGraph







<img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/sp.gif" alt="Icona" width="400"/>


**What is PathGraph**

Path Graph is a library with all the tools necessary to create and work both path and walk graphs in a stable and simple way.

**Why PathGraph**

If you need a ready-to-use library for user-side representing path graphs in which there are nodes, edges, and associated costs, which offers a user-friendly  to represent, manage, and interact graphs, then this it's the right solution. 


**Fork-Based Project**

This library is a fork based on the source code of the [SmartGraph](https://github.com/brunomnsilva/JavaFXSmart) [v2.0.0](https://github.com/brunomnsilva/JavaFXSmart/releases/tag/v2.0.0). It is modified to suite in specific path graphs features in a stable interface.

---


> [!NOTE]
> Read the **Javadoc** for more technicals details: [`PathGraph-JavaFX-1.0.5-javadoc`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/spring-core)


> [!NOTE]
> Library available on **Maven Central:** [`PathGraph-JavaFX-1.0.5`](https://central.sonatype.com/artifact/io.github.vittoriopiotti/PathGraph-JavaFX/1.0.5/overview)
> ```xml
> <dependency>
>   <groupId>io.github.vittoriopiotti</groupId>
>   <artifactId>PathGraph-JavaFX</artifactId>
>   <version>1.0.5</version>
> </dependency>
> ```

## Index

 1. [Features](#features)
 2. [Graph Logic](#graph-logic)
 3. [Get Started](#get-started)
 4. [Ready to Code](#ready-to-code)
 5. [Usage](#usage)
 6. [DTO · Data Transfer Object](#data-transfer-object)
 7. [JSON · Data Management](#json-data-management)
 8. [Licenses](#licenses)

[_Fork-Based On SmartGraph_](#fork-based-on-smartgraph)



---







## 1. Features <div id="features"/>




| <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/clickBackground.gif" alt="Icona" width="100%"/> | <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/test7ui.gif" alt="Icona" width="100%"/> | <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/test8ui.gif" alt="Icona" width="100%"/>|
| ------------ | ------------ | ------------ |
| <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/test5ui.gif" alt="Icona" width="100%"/> | <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/test1ui.gif" alt="Icona" width="100%"/> | <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/test4ui.gif" alt="Icona" width="100%"/>|

 * **Nodes:** [`New Node`](#new-node), [`Rename Node`](#rename-node), [`Delete Node`](#delete-node)
   
 * **Edges:** [`New Edge`](#new-edge), [`Delete Edge`](#delete-edge), [`Rotate Edge`](#rotate-edge), [`Split Edge`](#split-edge), [`Set Cost`](#set-cost)
   
 * **Graph:** [`Upload JSON`](#upload), [`Download JSON`](#download), [`Clear Graph`](#clear-graph), [`Show Path`](#show-path), [`Take Screenshot`](#take-screenshot), [`Drag`](#drag), [`Zoom`](#zoom)
 
 









## 2. Graph Logic <div id="graph-logic"/>

* Limit of 26 Nodes nameable only with uppercase characters
* Limit of two edges with opposite directions beetween two nodes
* Loop creation is not allowed
* Edge cost is an integer number
* Edge directions can be: [`Bidirected`](#), [`Natural Direction`](#), [`Opposite Direction`](#)





&nbsp;



## 3. Get Started <div id="get-started"/>

### Requirements


[`Java-21`](https://www.oracle.com/java/technologies/downloads/#java21), [`JavaFX-22`](https://gluonhq.com/products/javafx/)

_Forward-compatible_

### Dependencies

**Import External Dipendencies**

[`JavaFX-Swing-22`](https://mvnrepository.com/artifact/org.openjfx/javafx-swing/22), ​[`JavaFX-Controls-22`](https://mvnrepository.com/artifact/org.openjfx/javafx-controls/22), ​ [`JavaFX-FXML-22`](https://mvnrepository.com/artifact/org.openjfx/javafx-fxml/22)

**Import Library**

[`PathGraph-JavaFX-1.0.5`](https://central.sonatype.com/artifact/io.github.vittoriopiotti/PathGraph-JavaFX/1.0.5/overview)


_Able to:_

 * POM configuration:

   ```xml
   <dependency>
       <groupId>io.github.vittoriopiotti</groupId>
       <artifactId>PathGraph-JavaFX</artifactId>
       <version>1.0.5</version>
   </dependency>
   ```

        
 * Manual configuration:
   
   Download and import jar in your module dipendencies: [`PathGraph-JavaFX-1.0.5.jar`](https://github.com/vittorioPiotti/PathGraph-JavaFX/releases/tag/1.0.5)



<details>
  
<summary>
   <strong>Show POM.xml dipendencies</strong>
</summary>

&nbsp;

```xml
<dependency>
    <groupId>io.github.vittoriopiotti</groupId>
    <artifactId>PathGraph-JavaFX</artifactId>
    <version>1.0.5</version>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-swing</artifactId>
    <version>22</version>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>22</version>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-fxml</artifactId>
    <version>22</version>
</dependency>


```


&nbsp;

</details>


## 4. Ready to Code <div id="ready-to-code"/>

### Import Component

 ```java
 import com.vittoriopiotti.pathgraph.containers.*;
 ```

### Instance Object

> [!NOTE]
> [**PathGraph**](#)
>  * Vanilla configurations to use in your project in which create your custom UI
>  * Handles the graph's display and logic independently of the user interface, acting as a standalone component without any user interface restrictions, provifind all necessary features.
> 
> [**PathGraphUI**](#)
>  * Ready-to-use configuration with default UI
>  * Extends PathGraph to provide a layer on top of the graph management functionalities. It allows for the interaction with the graph through a visual interface over the underlying graph logic.

**PathGraph**

> [!NOTE]
>  * Vanilla configurations to use in your project in which create your custom UI
>  * Handles the graph's display and logic independently of the user interface, acting as a standalone component without any user interface restrictions, provifind all necessary features.

```java
PathGraph pg = new PathGraph();
```

> Empty callbacks

_or_



```java
PathGraph pg = new PathGraph(
    (ContextMenuCallback) ()->{},
    (EdgeCallback), (MouseEvent e, Character c1, Character c2)->{},
    (NodeCallback), (MouseEvent e, Character c1, Character c2)->{},
    (BackgroundCallback) (MouseEvent e)->{}, 
    (ZoomCallback), (Double n)->{},
    (AdjustPositionCallback) ()->{}
);
```

> With callbacks


**PathGraphUI with default UI**

```java
PathGraphUI pg = new PathGraphUI(
    (Stage) stage,
    (Scene) scene
);
```



**PathGraphUI with custom UI**

```java
PathGraphUI pg = new PathGraphUI(
    (Stage) stage,
    (Scene) scene,
    
    /* is enabled top-left menu */
    true,
    
    /* is enabled bot-left menu */
    true,
    
    /* is enabled bot-mid menu */
    true,
    
    /* is enabled right-mid menu */
    true,
    
    /* is enabled top-right menu */
    true,
    
    /* is hide UI */
    false

);
```








## 3. Get Started <div id="get-started2"/>



<details>
  
<summary>
  <strong> 1. Requirements</strong>
</summary>

&nbsp;

**Java** from: `v.21` _or higher_  [(link)](https://www.oracle.com/java/technologies/downloads/#java21)

**JavaFX** from: `v.22` _or higher_  [(link)](https://gluonhq.com/products/javafx/)

&nbsp;

</details>



<details>
  
<summary>
  <strong> 2. Dependencies</strong>
</summary>

&nbsp;

**Import PathGraph**

&nbsp;

```xml
<!-- Import using Maven Central -->
<dependency>
    <groupId>io.github.vittoriopiotti</groupId>
    <artifactId>PathGraph-JavaFX</artifactId>
    <version>1.0.4</version>
</dependency>
```

&nbsp;

_or_

&nbsp;

Manually library import [(download jar)]()


> In both of cases is necessary to configure external dependencies
> ```xml
>   <dependencies>
>        <dependency>
>            <groupId>org.openjfx</groupId>
>            <artifactId>javafx-controls</artifactId>
>            <version>${javafx.version}</version> 
>        </dependency>
>        <dependency>
>            <groupId>org.openjfx</groupId>
>            <artifactId>javafx-fxml</artifactId>
>            <version>${javafx.version}</version> 
>        </dependency>
>        <dependency>
>            <groupId>org.controlsfx</groupId>
>            <artifactId>controlsfx</artifactId>
>            <version>11.2.1</version>
>        </dependency>
>        <dependency>
>            <groupId>org.junit.jupiter</groupId>
>            <artifactId>junit-jupiter-api</artifactId>
>            <version>${junit.version}</version>
>            <scope>test</scope>
>        </dependency>
>        <dependency>
>            <groupId>org.junit.jupiter</groupId>
>            <artifactId>junit-jupiter-engine</artifactId>
>            <version>${junit.version}</version>
>            <scope>test</scope>
>        </dependency>
>    </dependencies>
> ```

&nbsp;


**Example of Pom Configuration**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId> <!-- Replace with your group ID -->
    <artifactId>your-artifact-id</artifactId> <!-- Replace with your artifact ID -->
    <version>1.0-SNAPSHOT</version>
    <name>Your Project Name</name> <!-- Replace with your project name -->

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <junit.version>5.10.2</junit.version>
        <javafx.version>22</javafx.version> <!-- Define JavaFX version here -->
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version> 
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>${javafx.version}</version> 
        </dependency>
        <dependency>
            <groupId>org.controlsfx</groupId>
            <artifactId>controlsfx</artifactId>
            <version>11.2.1</version>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version> <!-- Updated version -->
                <configuration>
                    <source>21</source>
                    <target>21</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>
                <executions>
                    <execution>
                        <id>default-cli</id>
                        <configuration>
                            <mainClass>com.example.yourpath.YourMainClass</mainClass> <!-- Replace with your main class path -->
                            <launcher>app</launcher>
                            <jlinkZipName>app</jlinkZipName>
                            <jlinkImageName>app</jlinkImageName>
                            <noManPages>true</noManPages>
                            <stripDebug>true</stripDebug>
                            <noHeaderFiles>true</noHeaderFiles>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```


&nbsp;

</details>




<details>
  
<summary>
  <strong> 3. Prepare <div id="prepare"/></strong>
</summary>

&nbsp;

### Available Imports

&nbsp;




<details>


<summary>
  <strong> Containers</strong>
</summary>

&nbsp;

 ```java
 import com.vittoriopiotti.pathgraph.containers.PathGraphUI;
 ```

 ```java
 import com.vittoriopiotti.pathgraph.containers.PathGraph;
 ```

&nbsp;

</details>



<details>


<summary>
  <strong> Constants</strong>
</summary>

&nbsp;

 ```java
 import com.vittoriopiotti.pathgraph.constants.AppConstants;
 ```

 ```java
 import com.vittoriopiotti.pathgraph.constants.SvgConstants;
 ```

&nbsp;

</details>



<details>


<summary>
  <strong> Data Transfer Objects</strong>
</summary>

&nbsp;



 ```java
 import com.vittoriopiotti.pathgraph.dto.NodeDTO;
 ```
 
 ```java
 import com.vittoriopiotti.pathgraph.dto.EdgeDTO;
 ```
 
 ```java
 import com.vittoriopiotti.pathgraph.dto.ConnectionDTO;
 ```
 
 ```java
 import com.vittoriopiotti.pathgraph.dto.GraphDTO;
 ```

_Major details on [Data Transfer Objects](#data-transfer-object)_


&nbsp;

</details>


&nbsp;


 





### PathGraph Creation


&nbsp;

```java
/* Vanilla configuration */
PathGraph pg = new PathGraph()                                           
```

> **Create your Custom UI** using PathGraph tools

> **Confirue Graph Settings** calling `pg.setCallbacks()` [(see)](#setcallbacks)

&nbsp;

_or_

&nbsp;

```java
/* Ready-To-Use configuration */
PathGraphUI pg = (
  new PathGraphUI(
    (Stage) primaryStage,
    (Scene) scene
  )
); 
```

> **Graph with Defaul Settings** automatically configurated

> **UI with Default Settings:** all components are enabled

> **Graph Settings Customizable** calling `pg.setCallbacks()` [(see)](#setcallbacks)

> **UI Settings Customizable** calling `pg.setUI()`  [(see)](#setui)

&nbsp;

_or_

&nbsp;

```java
/* Ready-To-Use with custom configuration */
PathGraphUI pg = (
  new PathGraphUI(
    (Stage) primaryStage,
    (Scene) scene,

    /* is enabled top-left menu */
    true,
  
    /* is enabled bot-left menu */
    true,
  
    /* is enabled bot-mid menu */
    true,
  
    /* is enabled right-mid menu */
    true,
  
    /* is enabled top-right menu */
    true,
  
    /* is hide UI */
    false

  )
); 
```

> **Graph with Defaul Settings** automatically configurated

> **UI with Custom Settings:** choice which components to show

> **Graph Settings Customizable** calling `pg.setCallbacks()` [(see)](#setcallbacks)

> **UI Settings Customizable** calling `pg.setUI()`  [(see)](#setui)






&nbsp;

</details>



  
<details>
  
<summary>
  <strong> 4. Setup <div id="setup"/></strong>
</summary>

&nbsp;

   
```java
pg.setup();
```

&nbsp;

_or_

&nbsp;

```java
pg.setup().thenRun(() -> {

  /* action to perform on first load */
  /* e.g. put here components to add onload */

});
```

&nbsp;


In both of cases:

> Setup is required **to enable the use** of library

> To call **only after** called `(Stage) primaryStage.show()`

&nbsp;

</details>


<details>
  
<summary>
  <strong> 5. Ready</strong>
</summary>

&nbsp;


```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;

/* PathGraph import */
import com.vittoriopiotti.pathgraph.containers.PathGraphUI;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

        /* 1. Create javafx window */
        BorderPane root = new BorderPane();
        root.setBackground(Background.fill(Color.web("#dee2e6")));
        Scene scene = new Scene(root, 750, 550);
        primaryStage.setScene(scene);

        /* 2. Show primary stage */
        primaryStage.show();

        /* 3. Create PathGraph object */
        PathGraphUI pg = new PathGraphUI(
                primaryStage,
                scene
        );

        /* 4. Add PathGraph in a container */
        root.setCenter(pg);

        /* 5. Custom configurations PathGraph    */
        pg.enableListenersGraph(true);
        pg.enableListenersPane(true);
        pg.setAutomaticLayout(true);

        /* 6. Setup PathGraph */
        pg.setup().thenRun(() -> {

            /* 7. Make Graphs with PathGraph */
            pg.newNode('A');
            pg.newNode('B');
            pg.newNode('C');
            pg.newEdge('A', 'B', 1);
            pg.newEdge('C', 'A', 2, false);

        });

    }


    public static void main(String[] args) {
        launch();
    }
    
}

```

> Operations are available **only** after calling `pg.setup()`


&nbsp;

</details>






<details>
  
<summary>
  <strong> 6. Configuration and Styling</strong>
</summary>

&nbsp;


In future versions will be optimized the management of configurations and styles similar to the original project of the fork [(see)](https://github.com/brunomnsilva/JavaFXSmartGraph#configuration-and-styling).

Currently, the styles and configurations are preset and cannot be modified.



&nbsp;

</details>






## 4. Usage <div id="usage"/>


> [!NOTE]
> Read the **Javadoc** for more technicals details: [(see)](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/spring-core)



> [!NOTE]
> Operations are available **only** after calling `pg.setup()` [(see)](#setup)




### Usage Index

 1. [Node](#node)
 2. [Edge](#edge)
 3. [Graph](#graph)
 4. [UI](#ui)






### 4.1. Node <div id="node"/>


> Every method is a function type and return the status of the operation

> Each method automatically update the graph

<details>
  
<summary>
  <strong> New Node <div id="new-node"/> </strong>
</summary>

&nbsp;


```java
boolean flag = (
  pg.newNode(
    /* node name to create */
    (char) 'A'
  )
);
```

&nbsp;

</details>


<details>
  
<summary>
  <strong> Rename Node <div id="rename-node"/></strong>
</summary>

&nbsp;

```java
boolean flag = (
  pg.renameNode(

    /* last node name */
    (char) 'A',

    /* new node name */
    (char) 'B'

  )
);
```

&nbsp;

</details>



<details>
  
<summary>
  <strong> Delete Node <div id="delete-node"/> </strong>
</summary>

&nbsp;

```java
boolean flag = (
  pg.deleteNode(
    /* name of the node to remove */
    (char) 'A'
  )
);
```

&nbsp;

</details>


### 4.2. Edge <div id="edge"/>


> Every method is a function type and return the status of the operation

> Each method automatically update the graph

<details>
  
<summary>
  <strong> New Edge <div id="new-edge"/> </strong>
</summary>

&nbsp;




```java
/* Default direction without bidirectional */
boolean flag = (
  pg.newEdge(

    /* start node name */
    (char) 'A',

    /* end node name */
    (char) 'B',

    /* edge cost */
    (int) 23

  )
);
```


```java
/* Default direction with bidirectionality */
boolean flag = (
  pg.newEdge(

    /* start node name */
    (char) 'A',

    /* end node name */
    (char) 'B',

    /* edge cost */
    (int) 23,

    /* edge with arrow */
    (boolean) true

  )
);
```


```java
/* Custom direction */                        
boolean flag = (
  pg.newEdge(

    /* start node name */
    (char) 'A',

    /* end node name */
    (char) 'B',

    /* edge cost */
    (int) 23,

    /* edge direction */
    (int) SmartGraphEdgeBase.DIRECTION_SECOND

  )
);
```

&nbsp;


> The direction can be
>
> ```java
> /* Edge without direction */
> (int) SmartEdgeBase.DIRECTION_BIDIRECTIONAL;
> ```
> 
> ```java
> */ Edge in natural direction */
> (int) SmartEdgeBase.DIRECTION_FIRST;
> ```
> 
> ```java
> */ Edge in opposite direction */
> (int) SmartEdgeBase.DIRECTION_SECOND;

&nbsp;


</details>

<details>
<summary>
  <strong> Delete Edge <div id="delete-edge"/> </strong>
</summary>

&nbsp;

```java
boolean flag = (
  pg.deleteEdge(

    /* start node */
    (char) 'A',

    /* end node */
    (char) 'B'

  )
);
```



&nbsp;

</details>



<details>
  
<summary>
  <strong> Rotate Edge <div id="rotate-edge"/> </strong>
</summary>

&nbsp;

```java
/* Default rotation */
boolean flag = (
  pg.rotateEdge(

    /* start node */
    (char) 'A',

    /* end node */
    (char) 'B'

  )
);                                                   
```


```java
/* Rotation with specific direction */
boolean flag = (
  pg.rotateEdge(

    /* start node */
    (char) 'A',

    /* end node */
    (char) 'B',

    /* edge direction */
    (int) SmartGraphEdgeBase.DIRECTION_FIRST

  )
);     
```


&nbsp;


> The direction can be
>
> ```java
> /* Edge without direction */
> (int) SmartEdgeBase.DIRECTION_BIDIRECTIONAL;
> ```
> 
> ```java
> */ Edge in natural direction */
> (int) SmartEdgeBase.DIRECTION_FIRST;
> ```
> 
> ```java
> */ Edge in opposite direction */
> (int) SmartEdgeBase.DIRECTION_SECOND;


&nbsp;

</details>




<details>
  
<summary>
  <strong> Split Edge <div id="split-edge"/> </strong>
</summary>

&nbsp;

```java
boolean flag = (
  pg.splitEdge(

    /* start node */
    (char) 'A',

    /* end node */
    (char) 'B'

  )
);   
```



&nbsp;

</details>



<details>
<summary>
  <strong> Set Arrow <div id="set-arrow"/> </strong>
</summary>

&nbsp;

```java
boolean flag = (
  pg.rotateEdge(

    /* start node */
    (char) 'A',

    /* end node */
    (char) 'B',

    /* is arrowed edge */
    (boolean) false

  )
); 
```



&nbsp;

</details>




<details>
  
<summary>
  <strong> Set Cost <div id="set-cost"/> </strong>
</summary>

&nbsp;

```java
boolean flag = (
  pg.splitEdge(

    /* start node */
    (char) 'A',

    /* end node */
    (char) 'B',

    /* edge cost */
    (int) 200

  )
);  
```


&nbsp;
   
</details>







### 4.3. Graph <div id="graph"/>



<details>
  
<summary>
  <strong>Enable Listeners Graph <div id="enablelistenersgraph"/></strong>
</summary>

&nbsp;

```java
pg.enableListenersGraph((boolean) true);
```



&nbsp;

</details>

<details>
  
<summary>
  <strong>Enable Listeners Pane <div id="enablelistenerspane"/></strong>
</summary>

&nbsp;

```java
pg.enableListenersPane((boolean) true);
```



&nbsp;

</details>



<details>
  
<summary>
  <strong>  Set Graph</strong>
</summary>

&nbsp;

```java
pg.setGraph(
  /* meta data of the graph to upload */
  (GraphDTO) mt
);
```

```java
pg.setGraph(

  /* meta data of the graph to upload */
  (GraphDTO) mt,

  /* is animated upload of new graph */
  (boolean) true
  
);
```

&nbsp;

</details>




<details>
  
<summary>
  <strong>Set Automatic Layout <div id="setdynamiclayout"/></strong>
</summary>

&nbsp;

```java
pg.setAutomaticLayout();
```

> _Original method of SmartGraph [(see)](https://github.com/brunomnsilva/JavaFXSmartGraph#basic-usage)_



&nbsp;

</details>



<details>
  
<summary>
  <strong>Set Callbacks <div id="setcallbacks"/></strong>
</summary>

&nbsp;

```java
pg.setCallbacks(

   /* to possibly close an open `ContextMenu` */
   Runnable closeContextMenu,

   /* action to perform on arrow edge event   */              
   BiConsumer<MouseEvent, Edge<E, V>> onClickArrow,

   /* action to perform on node event         */
   BiConsumer<MouseEvent, Vertex<V>> onClickNode,

   /* action to perform on click background   */
   Consumer<MouseEvent> onClickBackground,

   /* action to perform on scroll background  */            
   Consumer<Double> onChangeZoom,

   /* action to perform on drag background    */        
   Runnable doAdjustPosition

);
```



&nbsp;

</details>



<details>
  
<summary>
  <strong>  Get Graph <div id="getgraph"/></strong>
</summary>

&nbsp;

```java
GraphDTO gto = pg.getGraph();
```

&nbsp;
   
</details>

<details>
  
<summary>
  <strong>  Show Path <div id="show-path"/> </strong> 
</summary>

&nbsp;

```java
pg.showPath((List<NodeDTO>) lpn);
```

&nbsp;
   
</details>



<details>
  
<summary>
  <strong>  Clear Graph <div id="cleargraph"/> </strong>
</summary>

&nbsp;

```java
pg.clearGraph();
```


&nbsp;
   
</details>

<details>
  
<summary>
  <strong>  Adjust Position <div id="adjustPosition"/> </strong>
</summary>

&nbsp;

```java
pg.adjustPosition((double) 0.7);
```

&nbsp;
   
</details>



<details>
  
<summary>
  <strong> Take Screenshot <div id="screenshot"/> </strong>
</summary>

&nbsp;

```java
/* with animation */
CompletableFuture<Integer> future = (
  pg.takeScreenshot();
);
```

```java
/* set animation state */
CompletableFuture<Integer> future = (
  pg.takeScreenshot(
    (boolean)false
  )
);
```


&nbsp;


> Handle both asynchronous operations
>
> ```java
> pg.takeScreenshot().thenAccept(status -> {
> 
>   /* Use flag to check operation */
>   int flag = (int) status;
> 
> );
> ```

&nbsp;


> The response status can be
>
> ```java
> /* process interrupted */
> (int) UtilitiesCapture.INTERRUPT;       
> ```
> 
> ```java
> /* process succeeded */
> (int) UtilitiesCapture.SUCCESS;
> ```
>
> ```java
> /* process error */
> (int) UtilitiesCapture.ERROR; 
> ```



&nbsp;
  
</details>



<details>
  
<summary>
  <strong> Download JSON <div id="download"/></strong>
</summary>

&nbsp;


```java
/* fixed or floating file chooser */
int flag = pg.downloadJSON((Window) window); 
```

```java
/* whithout file chooser: file alredy chosen */
int flag = pg.downloadJSON((File) file);             
```

```java
/* using data transfer objects */
/* fixed or floating file chooser */
int flag = pg.downloadJSON(

  /* file chooser dialog owner window */
  (Window) window,

  /* data transfer object */
  (GraphDTO) dto


);             
```


```java
/* using data transfer objects */
/* whithout file chooser: file alredy chosen */
int flag = pg.downloadJSON(

  /* file in which save json graph */
  new File("path/to/file.json"),

  /* data transfer object */
  (GraphDTO) dto

);             
```



&nbsp;

> The response status can be
>
> ```java
> /* process interrupted */
> (int) UtilitiesParser.INTERRUPT;       
> ```
> 
> ```java
> /* process succeeded */
> (int) UtilitiesParser.SUCCESS;
> ```
>
> ```java
> /* process error */
> (int) UtilitiesParser.ERROR; 
> ```




&nbsp;
   
</details>



<details>
  
<summary>
  <strong>  Upload JSON <div id="upload"/></strong>
</summary>

&nbsp;



```java
/* fixed or floating file chooser*/
int flag = pg.uploadJSON((Window) window);  
```

```java
/* whithout file chooser: file alredy chosen */
int flag = pg.uploadJSON((File) file);  
```

&nbsp;

> The response status can be
>
> ```java
> /* process interrupted */
> (int) UtilitiesParser.INTERRUPT;       
> ```
> 
> ```java
> /* process succeeded */
> (int) UtilitiesParser.SUCCESS;
> ```
>
> ```java
> /* process error */
> (int) UtilitiesParser.ERROR; 
> ```

&nbsp;

> Ability to upload json also without standard methods using DTO [(see)](#data-transfer-object)
>
> ```java       
> /* set new graph */
> pg.setGraph(
> 
>   /* DTO created from json content or json file */
>   (GraphDTO) dto,
> 
>    /* is animated grah setting */
>   (boolean) true
> 
> );
> ```



&nbsp;
   
</details>







### 4.4. UI <div id="ui"/>


> [!NOTE]
>
> Only with `PathGraphUI` class type [(see)](#prepare)









<details>
  
<summary>
  <strong> Do Zoom <div id="dozoom"/> </strong>
</summary>

&nbsp;


```java
pg.doZoom((dooble) 2.0);
```

&nbsp;

</details>


<details>
  
<summary>
  <strong> Do Drag <div id="dodrag"/> </strong>
</summary>

&nbsp;


```java
pg.doDrag((dooble) 20.0, (double) -10.0);
```

&nbsp;

</details>

<details>
  
<summary>
  <strong> Set UI <div id="setui"/> </strong>
</summary>

&nbsp;


```java
pg.setUI(

  /* is enabled top-left menu */
  true,

  /* is enabled bot-left menu */
  true,

  /* is enabled bot-mid menu */
  true,

  /* is enabled right-mid menu */
  true,

  /* is enabled top-right menu */
  true,

  /* is hide UI */
  false

);
```

&nbsp;

</details>




<details>
  
<summary>
  <strong> Show UI <div id="show-ui"/> </strong>
</summary>

&nbsp;


```java
pg.showUI();
```

&nbsp;

</details>

<details>
  
<summary>
  <strong> Hide UI <div id="hide-ui"/> </strong>
</summary>

&nbsp;


```java
pg.hideUI();
```

&nbsp;

</details>


<details>
  
<summary>
  <strong> Toggle UI <div id="toggle-ui"/> </strong>
</summary>

&nbsp;


```java
pg.toggleUI();
```

&nbsp;

</details>




## 5. DTO · Data Transfer Objects <div id="data-transfer-object"/>
 
> [!NOTE]
> Read the **Javadoc** for more technicals details: [(see)](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/spring-core)




Represent the components of the graph providing a simple and serializable structure that allows for:

 * Converting graph data into JSON format
 * Reconstructing graph data from JSON
 * Support structure for graph operations



<details>
  
<summary>
  <strong>Node</strong>
</summary>

&nbsp;

**Constructor**

```java
NodeDTO ndto = (
  new NodeDTO(
    /* node name */
    (char) 'A'
  )
);
```

**Method**

```java
char label = ndto.getLabel();
```

&nbsp;

</details>



<details>
  
<summary>
  <strong>Edge</strong>
</summary>

&nbsp;



**Constructors**



```java
EdgeDTO edto = (
  new EdgeDTO(

    /* start node */
    (char) 'A',

    /* end node */
    (char) 'B',

    /* edge cost */
    (int) 3,

    /* is arrowed edge */
    (boolean) false

  )
);
```




```java
EdgeDTO edto = (
  new EdgeDTO(

    /* start node */
    (char) 'A',

    /* end node */
    (char) 'B',

    /* edge cost */
    (int) 3,

    /* edge direction */
    (int) SmartGraphEdgeBase.BIDIRECTIONAL

  )
);
```


**Methods**


```java
char from = edto.getFrom();
```

```java
char to = edto.getTo();
```

```java
int cost = edto.getCost();
```

```java
boolean isArrowed = edto.isArrowed();
```


&nbsp;

</details>



<details>
  
<summary>
  <strong>Connection</strong>
</summary>

&nbsp;

**Constructor**


```java
ConnectionDTO ndto = (
  new ConnectionDTO(

    /* connected node */
    (char) 'A',

    /* edge cost */
    (int) 3,

  )
);
```



**Methods**


```java
char label = edto.getLabel();
```

```java
int cost = edto.getCost();
```


&nbsp;

</details>








<details>
  
<summary>
  <strong>Graph</strong>
</summary>

&nbsp;


**Constructors**

```java
GraphDTO gdto = (
  new GraphDTO(
    /* file with json */
    (File) new File("path/to/file.json")
  )
);
```


```java
GraphDTO gdto = (
  new GraphDTO(
    /* String with json content */
    (String) jsonContent
  )
);
```


```java
GraphDTO gdto = (
  new GraphDTO(

    /* nodes DTO */
    List<NodeDTO> nodes, 

    /* edges DTO */
    List<EdgeDTO> edges

  )
);
```

**Methods**


```java
List<NodeDTO> path = (
  gdto.findPath(
    
    /* start node */
    (char) 'A',

    /* end node */
    (char) 'B',

  )
);
```

```java
String json = gdto.getJson();
```

```java
List<NodesDTO> lndto = (
  gdto.getNodes())
);
```

```java
List<EdgeDTO> ledto = (
  gdto.getEdges()
);
```


```java
List<ConnectionDTO> lcdto = (
  gdto.getConnections()
);
```








&nbsp;

</details>









## 6. JSON · Data Management <div id="json-data-management"/>


```json
{
  "nodes": ["A", "B","C"],
  "edges": [
    {
      "from": "A",
      "to": "B",
      "cost": 1,
      "isArrowed": true
    },
    {
      "from": "B",
      "to": "A",
      "cost": 10,
      "isArrowed": true
    },
    {
      "from": "B",
      "to": "C",
      "cost": 2,
      "isArrowed": false
    },
  ]
}
```

## 7. Licenses <div id="licenses"/>


> [!NOTE]
>  SVG icons from **Bootstrap**







---

### PathGraph

**Copyright** 2024 Vittorio Piotti [(GitHub page)](https://github.com/vittorioPiotti) [(Personal page)](https://vittoriopiotti.altervista.org/) 

**Version** [v1.0.4](https://github.com/vittorioPiotti/PathGraph-JavaFX/releases/tag/1.0.4)

**License** [GPL-3.0](https://github.com/vittorioPiotti/JavaFXPathGraph/blob/master/LICENSE.txt)





---

### SmartGraph

**Copyright** 2019 - 2024 Bruno Silva [(GitHub page)](https://github.com/brunomnsilva) [(Personal page)](https://www.brunomnsilva.com/) 

**Version** [v2.0.0](https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)

**License** [MIT](https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)



---

### Bootstrap Icons

**Copyright** 2011-2018 The Bootstrap Authors 

**Version** [v1.11.0](https://blog.getbootstrap.com/2023/09/12/bootstrap-icons-1-11-0/)

**License** [MIT](https://github.com/twbs/icons/blob/main/LICENSE)



---


## Fork-Based On SmartGraph <div id="fork-based-on-smartgraph"/>


This library is a fork based on the source code of the [SmartGraph](https://github.com/brunomnsilva/JavaFXSmart) [v2.0.0](https://github.com/brunomnsilva/JavaFXSmart/releases/tag/v2.0.0) library on which existing classes have been modified and new ones have been added. PathGraph is therefore the adaptation of SmartGraph to specific path graphs features in a stable user interface.

[(See SmartGraph)](https://github.com/brunomnsilva/JavaFXSmartGraph)























































