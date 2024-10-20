# (Java FX) PathGraph







<img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/sp.gif" alt="Icona" width="400"/>


**What is PathGraph**

Path Graph is a library with all the tools necessary to create and work both path and walk graphs in a stable and simple way.

**Why PathGraph**

If you need a ready-to-use library for user-side representing path graphs in which there are nodes, edges, and associated costs, which offers a user-friendly  to represent, manage, and interact graphs, then this it's the right solution. 


**Fork-Based Project**

This library is a fork based on the source code of the [SmartGraph](https://github.com/brunomnsilva/JavaFXSmartGraph) [v2.0.0](https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0). It is modified to suite in specific path graphs features in a stable interface.

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
 5. [Examples](#examples)
 6. [Callbacks](#callbacks)
 7. [DTO · Data Transfer Object](#data-transfer-object)
 8. [JSON · Data Management](#json-data-management)
 9. [Configuration and Styling](#configurations-and-styling)
 10. [Licenses](#licenses)

[_Fork-Based On SmartGraph_](#fork-based-on-smartgraph)



---







## 1. Features <div id="features"/>




| <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/clickBackground.gif" alt="Icona" width="100%"/> | <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/test7ui.gif" alt="Icona" width="100%"/> | <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/test8ui.gif" alt="Icona" width="100%"/>|
| ------------ | ------------ | ------------ |
| <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/test5ui.gif" alt="Icona" width="100%"/> | <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/test1ui.gif" alt="Icona" width="100%"/> | <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/test4ui.gif" alt="Icona" width="100%"/>|

 * **Nodes:** [`New Node`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#newNode()), [`Rename Node`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#renameNode(char,char)), [`Delete Node`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#deleteNode(char))
   
 * **Edges:** [`New Edge`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#newEdge()), [`Delete Edge`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#deleteEdge(char)), [`Rotate Edge`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#rotateEdge(char,char)), [`Split Edge`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#splitEdge(char,char)), [`Set Cost`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#setCost(char,char,int))
   
 * **Graph:** [`Upload JSON`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#uploadJSON(java.io.File)), [`Download JSON`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#downloadJSON(java.io.File)), [`Clear Graph`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#clearGraph()), [`Show Path`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraphUI.html#showUI()), [`Take Screenshot`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#takeScreenshot()), [`Drag`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#takeScreenshot()), [`Zoom`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#takeScreenshot())
 
 









## 2. Graph Logic <div id="graph-logic"/>

* Limit of 26 Nodes nameable only with uppercase characters
* Limit of two edges with opposite directions beetween two nodes
* Loop creation is not allowed
* Edge cost is an integer number
* Edge directions can be: [`Bidirectional`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/Constants.html#BIDIRECTIONAL), [`Natural Direction`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/Constants.html#BIDIRECTIONAL), [`Opposite Direction`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/Constants.html#BIDIRECTIONAL)





&nbsp;



## 3. Get Started <div id="get-started"/>

### Requirements


[`Java-21`](https://www.oracle.com/java/technologies/downloads/#java21), [`JavaFX-22`](https://gluonhq.com/products/javafx/)

_Forward-compatible_

### Dependencies

**Import External Dependencies**

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
   
   Download and import jar in your module dependencies: [`PathGraph-JavaFX-1.0.5.jar`](https://github.com/vittorioPiotti/PathGraph-JavaFX/releases/tag/1.0.5)



<details>
  
<summary>
   <strong>Show POM.xml dependencies</strong>
</summary>



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




</details>


## 4. Ready to Code <div id="ready-to-code"/>

### Import Component

 ```java
 import com.vittoriopiotti.pathgraph.app.*;
 ```

### Instance Object

> [!NOTE]
>  * `PathGraph` to create your custom interface
>  * `PathGraphUI`for ready-to-use interface

**PathGraph**


Vanilla configurations to use in your project in which create your custom UI.


Handles the graph's display and logic independently of the user interface, acting as a standalone component without any user interface restrictions, provifind all necessary features.

```java
PathGraph pg = new PathGraph();
```

> Empty callbacks

_or_



```java
PathGraph pg = new PathGraph(
    (ContextMenuCallback) ()->{},
    (EdgeCallback) (MouseEvent e, Character c1, Character c2)->{},
    (NodeCallback) (MouseEvent e, Character c1, Character c2)->{},
    (BackgroundCallback) (MouseEvent e)->{}, 
    (ZoomCallback) (Double n)->{},
    (AdjustPositionCallback) ()->{}
);
```

> With callbacks





**PathGraphUI**

Ready-to-use configuration with default UI.

Extends PathGraph to provide a layer on top of the graph management functionalities. It allows for the interaction with the graph through a visual interface over the underlying graph logic.


```java
PathGraphUI pg = new PathGraphUI(
    (Stage) stage,
    (Scene) scene
);
```

> With default UI


_or_



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

> With custom UI



In both cases are customizable the visibility of the UI and its components only with an instance of `PathGraphUI`:

[`Hide UI`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraphUI.html#hideUI()), [`Show UI`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraphUI.html#hideUI()), [`Toggle UI`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraphUI.html#hideUI()), [`Set UI`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraphUI.html#hideUI())


### Setup  <div id="setup"/>


> [!NOTE]
> **To enable the use of library** `setup` must be called **only one time after making stage visible** with `(Stage) stage.show()`
>
> _Before calling setup, no operations of any kind can be performed on the graph_


```java
pg.setup();
```



_or_



```java
pg.setup().thenRun(() -> {

  /* actions to perform on first load */
  /* e.g. put here components, callbacks setting, graph configurations */

});
```

Graph operations are limited to these contexts:

 * **Setup:** ensures execution post-initialization in `pg.setup().thenRun(()->{})`.
 * **Event Handlers:** Safe within JavaFX event actions.
 * **JavaFX Timers:** Use for delayed, thread-safe execution.


## 5. Examples <div id="examples"/>


### PathGraph

Vanilla configurations to use in your project in which create your custom UI.



```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import com.vittoriopiotti.pathgraph.app.PathGraph;

public class ExampleOfPathGraph extends Application {

    @Override
    public void start(Stage primaryStage) {

        /* 1. Create javafx window */
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root,500,300);
        primaryStage.setScene(scene);

        /* 2. Show primary stage */
        primaryStage.show();

        /* 3. Instance object */
        PathGraph pg = new PathGraph();

        /* 4. Add PathGraph in a container */
        root.setCenter(pg);

        /* 6. Setup */
        pg.setup().thenRun(() -> {

            /* 5. Custom configurations  */
            pg.enableListenersGraph(true);
            pg.enableListenersPane(true);
            pg.setAutomaticLayout(true);

            /* Set callbacks */
            pg.setBackgroundCallback(event -> {
                pg.newNode();
                event.consume();
            });
            pg.setNodeCallback((event,label) -> {
                pg.newEdge(label);
                event.consume();
            });
            pg.setEdgeCallback((event,start,end) -> {
                pg.deleteEdge(start,end);
                event.consume();
            });
            
            /* 7. Make Graphs */
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


### PathGraphUI

Ready-to-use configuration with default UI.


```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import com.vittoriopiotti.pathgraph.app.PathGraphUI;

public class ExampleOfPathGraphUI extends Application {

    @Override
    public void start(Stage primaryStage) {

        /* 1. Create javafx window */
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root,500,300);
        primaryStage.setScene(scene);

        /* 2. Show primary stage */
        primaryStage.show();

        /* 3. Instance object */
        PathGraphUI pg = new PathGraphUI(primaryStage,scene);

        /* 4. Add PathGraph in a container */
        root.setCenter(pg);

        /* 5. Setup */
        pg.setup().thenRun(() -> {

            /* 6. Make Graphs */
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


## 6. Callbacks <div id="callbacks"/>

>[!NOTE]
> Customizable callbacks only with an instance of `PathGraph` _(`PathGraphUI`is a ready-to-use configuration)._

**Configuration:**

1. Import package to use callback objects:

      ```java
      import com.vittoriopiotti.pathgraph.callbacks.*;
      ```
      
2. Call setter methods to apply new callbacks passing callback objects:
   
     [`Set All Callbacks`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#setAllCallbacks(com.vittoriopiotti.pathgraph.callbacks.ContextMenuCallback,com.vittoriopiotti.pathgraph.callbacks.EdgeCallback,com.vittoriopiotti.pathgraph.callbacks.NodeCallback,com.vittoriopiotti.pathgraph.callbacks.BackgroundCallback,com.vittoriopiotti.pathgraph.callbacks.ZoomCallback,com.vittoriopiotti.pathgraph.callbacks.AdjustPositionCallback)), [`Set Context Menu Callback`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#setAllCallbacks(com.vittoriopiotti.pathgraph.callbacks.ContextMenuCallback,com.vittoriopiotti.pathgraph.callbacks.EdgeCallback,com.vittoriopiotti.pathgraph.callbacks.NodeCallback,com.vittoriopiotti.pathgraph.callbacks.BackgroundCallback,com.vittoriopiotti.pathgraph.callbacks.ZoomCallback,com.vittoriopiotti.pathgraph.callbacks.AdjustPositionCallback)), [`Set Edge Callback`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#setAllCallbacks(com.vittoriopiotti.pathgraph.callbacks.ContextMenuCallback,com.vittoriopiotti.pathgraph.callbacks.EdgeCallback,com.vittoriopiotti.pathgraph.callbacks.NodeCallback,com.vittoriopiotti.pathgraph.callbacks.BackgroundCallback,com.vittoriopiotti.pathgraph.callbacks.ZoomCallback,com.vittoriopiotti.pathgraph.callbacks.AdjustPositionCallback)), [`Set Node Callback`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#setAllCallbacks(com.vittoriopiotti.pathgraph.callbacks.ContextMenuCallback,com.vittoriopiotti.pathgraph.callbacks.EdgeCallback,com.vittoriopiotti.pathgraph.callbacks.NodeCallback,com.vittoriopiotti.pathgraph.callbacks.BackgroundCallback,com.vittoriopiotti.pathgraph.callbacks.ZoomCallback,com.vittoriopiotti.pathgraph.callbacks.AdjustPositionCallback)), [`Set Background Callback`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#setAllCallbacks(com.vittoriopiotti.pathgraph.callbacks.ContextMenuCallback,com.vittoriopiotti.pathgraph.callbacks.EdgeCallback,com.vittoriopiotti.pathgraph.callbacks.NodeCallback,com.vittoriopiotti.pathgraph.callbacks.BackgroundCallback,com.vittoriopiotti.pathgraph.callbacks.ZoomCallback,com.vittoriopiotti.pathgraph.callbacks.AdjustPositionCallback)), [`Set Zoom Callback`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#setAllCallbacks(com.vittoriopiotti.pathgraph.callbacks.ContextMenuCallback,com.vittoriopiotti.pathgraph.callbacks.EdgeCallback,com.vittoriopiotti.pathgraph.callbacks.NodeCallback,com.vittoriopiotti.pathgraph.callbacks.BackgroundCallback,com.vittoriopiotti.pathgraph.callbacks.ZoomCallback,com.vittoriopiotti.pathgraph.callbacks.AdjustPositionCallback)), [`Set Adjust Position Callback`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#setAllCallbacks(com.vittoriopiotti.pathgraph.callbacks.ContextMenuCallback,com.vittoriopiotti.pathgraph.callbacks.EdgeCallback,com.vittoriopiotti.pathgraph.callbacks.NodeCallback,com.vittoriopiotti.pathgraph.callbacks.BackgroundCallback,com.vittoriopiotti.pathgraph.callbacks.ZoomCallback,com.vittoriopiotti.pathgraph.callbacks.AdjustPositionCallback))

**Suggestions:**

 1. Use of a `ContextMenu` with custom `MenuItem` or `Button` to perform the actions

 2. Use `event.consume()` to prevent the propagation of the event




<details>
  
<summary>
   <strong>Show an example of callback usage</strong>
</summary>



```java
EdgeCallback ec = (event,start,end) ->{
    if (event.getButton() == MouseButton.SECONDARY) {
        System.out.println(
                pg.rotateEdge(start,end) ?
                        "rotate edge successfully" :
                        "rotate edge  error"
        );
    }else if (event.getButton() == MouseButton.PRIMARY) {
        System.out.println(
                pg.deleteEdge(start,end) ?
                        "delete edge successfully" :
                        "delete edge error"
        );
    }
    event.consume();
};

(PathGraph) pg.setEdgeCallback(ec);
```


</details>


## 7. DTO · Data Transfer Objects <div id="data-transfer-object"/>
 
> [!NOTE]
> Read the **Javadoc** for more technicals details: [(see)](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/spring-core)


Providing to rappresent graph components in a simple and serializable structure.

**Import package:**

```java
import com.vittoriopiotti.pathgraph.dto.*;
```

**Usage:**

 * Converting graph data into JSON format
 * Reconstructing graph data from JSON
 * Support structure for graph operations





## 8. JSON · Data Management <div id="json-data-management"/>

### Methods

[`Get Nodes Json`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#setAllCallbacks(com.vittoriopiotti.pathgraph.callbacks.ContextMenuCallback,com.vittoriopiotti.pathgraph.callbacks.EdgeCallback,com.vittoriopiotti.pathgraph.callbacks.NodeCallback,com.vittoriopiotti.pathgraph.callbacks.BackgroundCallback,com.vittoriopiotti.pathgraph.callbacks.ZoomCallback,com.vittoriopiotti.pathgraph.callbacks.AdjustPositionCallback)), [`Get Edges Json`](), [`Get Connections Json`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#setAllCallbacks(com.vittoriopiotti.pathgraph.callbacks.ContextMenuCallback,com.vittoriopiotti.pathgraph.callbacks.EdgeCallback,com.vittoriopiotti.pathgraph.callbacks.NodeCallback,com.vittoriopiotti.pathgraph.callbacks.BackgroundCallback,com.vittoriopiotti.pathgraph.callbacks.ZoomCallback,com.vittoriopiotti.pathgraph.callbacks.AdjustPositionCallback)), [`Get Graph Json`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#setAllCallbacks(com.vittoriopiotti.pathgraph.callbacks.ContextMenuCallback,com.vittoriopiotti.pathgraph.callbacks.EdgeCallback,com.vittoriopiotti.pathgraph.callbacks.NodeCallback,com.vittoriopiotti.pathgraph.callbacks.BackgroundCallback,com.vittoriopiotti.pathgraph.callbacks.ZoomCallback,com.vittoriopiotti.pathgraph.callbacks.AdjustPositionCallback)), [`Upload Json`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#setAllCallbacks(com.vittoriopiotti.pathgraph.callbacks.ContextMenuCallback,com.vittoriopiotti.pathgraph.callbacks.EdgeCallback,com.vittoriopiotti.pathgraph.callbacks.NodeCallback,com.vittoriopiotti.pathgraph.callbacks.BackgroundCallback,com.vittoriopiotti.pathgraph.callbacks.ZoomCallback,com.vittoriopiotti.pathgraph.callbacks.AdjustPositionCallback)), [`Download Json`](https://javadoc.io/doc/io.github.vittoriopiotti/PathGraph-JavaFX/latest/com.vittoriopiotti.pathgraph/com/vittoriopiotti/pathgraph/app/PathGraph.html#setAllCallbacks(com.vittoriopiotti.pathgraph.callbacks.ContextMenuCallback,com.vittoriopiotti.pathgraph.callbacks.EdgeCallback,com.vittoriopiotti.pathgraph.callbacks.NodeCallback,com.vittoriopiotti.pathgraph.callbacks.BackgroundCallback,com.vittoriopiotti.pathgraph.callbacks.ZoomCallback,com.vittoriopiotti.pathgraph.callbacks.AdjustPositionCallback))

### Structure

**Graph**

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
    }
  ]
}
```

<details>
  
<summary>
   <strong>Nodes</strong>
</summary>

```json
{
  "nodes": [
    "A",
    "C",
    "B"
  ]
}
```

</details>


<details>
  
<summary>
   <strong>Edges</strong>
</summary>

```json
{
  "edges": [
    {
      "from": "A",
      "to": "B",
      "cost": 1,
      "isArrowed": true
    },
    {
      "from": "C",
      "to": "A",
      "cost": 2,
      "isArrowed": false
    }
  ]
}
```

</details>


<details>
  
<summary>
   <strong>Connections</strong>
</summary>

```json
{
  "connections": [
    {
      "node": "B",
      "edges": [
      ]
    },
    {
      "node": "A",
      "edges": [
        {
          "to": "B",
          "cost": 1
        },
        {
          "to": "C",
          "cost": 2
        }
      ]
    },
    {
      "node": "C",
      "edges": [
        {
          "to": "A",
          "cost": 2
        }
      ]
    }
  ]
}
```

</details>




## 9. Configuration and Styling <div id="configurations-and-styling"/>


In future versions will be optimized the management of configurations and styles similar to the original project of the fork [(see)](https://github.com/brunomnsilva/JavaFXSmartGraph#configuration-and-styling).

Currently, the styles and configurations are preset and cannot be modified.



## 10. Licenses <div id="licenses"/>


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























































