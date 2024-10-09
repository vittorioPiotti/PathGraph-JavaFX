# (Java FX) PathGraph

<img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/preview906.png" alt="Icona" width="100%"/>


---
  
PathGraph fork based on [SmartGraph](https://github.com/brunomnsilva/JavaFXSmartGraph) is an adapted library to work with path graphs that uses nodes, edges and associated costs.
Provided to user-friendly interface in a stable user-experience in witch mange dynamically the path graphs.
Ability of find and show the shortest path, make screenshots and upload or download json of the graphs.



Working on [PathGraph](https://github.com/vittorioPiotti/PathGraph-ForkBased)

## GET STARTED

Provided to represent path graphs through nodes, edges, and associated costs with the following logic:

* Nodes can be only characters with uppercase alphabet letters to maxium 26 nodes
* Not provided over limits of two edges with same direction between two same nodes
* Not provided loops creation
* Edges can be directed:

    ```java
    (int) SmartGraphEdgeBase.DIRECTION_BIDIRECTIONAL;     /* (0) Edge without direction (no arrow).             */
    ```

    ```java
    (int) SmartGraphEdgeBase.DIRECTION_FIRST;             /* (1) Edge in the natural direction (with an arrow). */
    ```

    ```java
    (int) SmartGraphEdgeBase.DIRECTION_SECOND;            /* (2) Edge in the opposite direction (with an arrow). */
    ```



> Adding new edge to nodes with one bidirectional edge implies the automatic adjustment of the bidirectional edge to redirect it at the opposite direction of the new edge

> Adding new bidirectional edge to nodes with one bidirectional edge implies the automatic adjustment of both edges to redirect them at the preferred direction to make them in opposite direction from each other

### 1. Requirements

 * Java Version: `21.0.3` [(link)](https://www.oracle.com/java/technologies/downloads/#java21)
 * JavaFX Version: `22.0.2+4` [(link)](https://gluonhq.com/products/javafx/)

### 2. Import

_Working on to upload library on maven dipencencies to available the library distribution_
   
### 3. Prepare 

```java
SmartGraph gc = new SmartGraphUI((Stage) primaryStage, (Scene) scene); /* To apply UI features (extends SmartGraph) */
```

_or_


```java
SmartGraph gc = new SmartGraph()                                       /* To apply Custom UI features               */
```
> [!NOTE]  
> Manual callback configurations if using `SmartGraph` instead of `SmartGraphUI`
>
> ```java
> gc.setAllCallbacks(
>     Runnable closeContextMenu,                         /* to possibly close an open `ContextMenu` */
>     BiConsumer<MouseEvent, Edge<E, V>> onClickArrow,   /* action to perform on arrow edge event   */
>     BiConsumer<MouseEvent, Vertex<V>> onClickNode,     /* action to perform on node event         */
>     Consumer<MouseEvent> onClickBackground,            /* action to perform on click background   */
>     Consumer<Double> onChangeZoom,                     /* action to perform on scroll background  */
>     Runnable doAdjustPosition                          /* action to perform on drag background    */
> );
> ```
> > It can be done in any time and in any case
>
> > Configure empty callbacks to disable graph interactions

### 4. Setup

 * Setup is required to enable graph creation features
 * To call only after called `(Stage) primaryStage.show()`
   
```java
gc.setup();
```

### 5. Make

 * Operations are available **only** after calling `gc.setup()`
 * Every method is a function type and return the status of the operation
 * Each method automatically update the graph


#### Node

<details>
  
<summary>
  <strong> 5.1. New Node</strong>
</summary>

&nbsp;


```java
boolean flag = gc.newNode("A");
```

&nbsp;

</details>


<details>
  
<summary>
  <strong> 5.2. Rename Node</strong>
</summary>

&nbsp;

```java
boolean flag = gc.renameNode("A", "K");
```

&nbsp;

</details>



<details>
  
<summary>
  <strong> 5.3. Delete Node</strong>
</summary>

&nbsp;

```java
boolean flag = gc.deleteNode("A");
```

&nbsp;

</details>

#### Edge

<details>
  
<summary>
  <strong> 5.4. New Edge</strong>
</summary>

&nbsp;

```java
boolean flag = gc.newEdge("A", "Z", 23);                                     /* Default not bidirectional direction      */
```




```java
boolean flag = gc.newEdge("A", "Z", 23, true);                               /* Default direction (can be bidirectional)  */
```



```java
boolean flag = gc.newEdge("A", "Z", 23, SmartGraphEdgeBase.DIRECTION_SECOND); /* Custom direction                         */
```

> The direction can be
>
> ```java
> (int) SmartGraphEdgeBase.DIRECTION_BIDIRECTIONAL;     /* (0) Edge without direction (no arrow).              */
> ```
> 
> ```java
> (int) SmartGraphEdgeBase.DIRECTION_FIRST;             /* (1) Edge in the natural direction (with an arrow).  */
> ```
>
> ```java
> (int) SmartGraphEdgeBase.DIRECTION_SECOND;            /* (2) Edge in the opposite direction (with an arrow). */
> ```



&nbsp;

</details>





<details>
<summary>
  <strong> 5.5. Delete Edge</strong>
</summary>

&nbsp;

```java
boolean flag = gc.deleteEdge("A", "Z");
```

&nbsp;

</details>



<details>
  
<summary>
  <strong> 5.6. Rotate Edge</strong>
</summary>

&nbsp;

```java
boolean flag = gc.rotateEdge("Z", "C");                                     /* Default rotation */
```



```java
boolean flag = gc.rotateEdge("Z", "C", SmartGraphEdgeBase.DIRECTION_FIRST); /* Rotation with specific direction */
```


> The direction can be
>
> ```java
> (int) SmartGraphEdgeBase.DIRECTION_BIDIRECTIONAL;     /* (0) Edge without direction (no arrow).             */
> ```
> 
> ```java
> (int) SmartGraphEdgeBase.DIRECTION_FIRST;             /* (1) Edge in the natural direction (with an arrow). */
> ```
>
> ```java
> (int) SmartGraphEdgeBase.DIRECTION_SECOND;            /* (2) Edge in the opposite direction (with an arrow). */
> ```


&nbsp;

</details>




<details>
  
<summary>
  <strong> 5.7. Split Edge</strong>
</summary>

&nbsp;

```java
boolean flag = gc.splitEdge("Z", "C");
```

&nbsp;

</details>




<details>
  
<summary>
  <strong> 5.8. Set Cost</strong>
</summary>

&nbsp;

```java
boolean flag = gc.setCost("Z", "C", 200);
```

&nbsp;
   
</details>



#### Edge Utilities

<details>
  
<summary>
  <strong> 5.1. Is Edge</strong>
</summary>

&nbsp;


```java
boolean flag = gc.isEdge((char) 'Z', (char) 'C' );                                                       /* Check validity of edge existance                        */
```

&nbsp;

</details>

<details>
  
<summary>
  <strong> 5.1. Is Double</strong>
</summary>

&nbsp;


```java
boolean flag = gc.isDouble( (char) 'Z', (char) 'C' );                                                     /* if true there are two edges between the same two nodes  */
```

&nbsp;

</details>


<details>
  
<summary>
  <strong> 5.1. Is Arrowed</strong>
</summary>

&nbsp;


```java
boolean flag = gc.isArrowed( (char) 'Z', (char) 'C' );                                                    /* if  false edge is bidirectional                         */
```

&nbsp;

</details>



<details>
  
<summary>
  <strong> 5.1. Check Direction</strong>
</summary>

&nbsp;


```java
boolean flag = gc.checkDirection((char) 'Z', (char) 'C', (int) SmartGraphEdgeBase.DIRECTION_FIRST );    /* Check validity of edge direction                        */
```

&nbsp;

</details>


<details>
  
<summary>
  <strong> 5.1. Check Start</strong>
</summary>

&nbsp;


```java
boolean flag = gc.checkStart( (char) 'Z', (char) 'C', (char) 'G');                                      /* Check validity of end start                             */
```

&nbsp;

</details>


<details>
  
<summary>
  <strong> 5.1. Check End</strong>
</summary>

&nbsp;


```java
boolean flag = gc.checkEnd( (char) 'Z', (char) 'C', (char) 'G');                                        /* Check validity of end node in edge                      */
```

&nbsp;

</details>

<details>
  
<summary>
  <strong> 5.1. Check Cost</strong>
</summary>

&nbsp;


```java
boolean flag = gc.checkCost( (char) 'Z', (char) 'C', (int) 12);                                        /* Check validity of end node in edge                        */
```

&nbsp;

</details>


<details>
  
<summary>
  <strong> 5.1. Set Start</strong>
</summary>

&nbsp;


```java
Char res = gc.setStart( (char) 'Z', (char) 'C', (char) 'G' );                                             /* Set edge start                                            */
```

&nbsp;

</details>



<details>
  
<summary>
  <strong> 5.1. Set End</strong>
</summary>

&nbsp;


```java
Char res = gc.setEnd( (char) 'Z', (char) 'C', (char) 'H'  );                                              /* Set edge end                                              */
```

&nbsp;

</details>






#### Graph

<details>
  
<summary>
  <strong> 5.9. Take Screenshot</strong>
</summary>

&nbsp;

```java
(CompletableFuture<Integer>) gc.takeScreenshot();         /* with animation     */
```

```java
(CompletableFuture<Integer>) gc.takeScreenshot(false);    /* set if is animated */
```

> Handle both asynchronous operations
>
> ```java
> gc.takeScreenshot().thenAccept(status -> {
>     int flag = (int) status; /* Use flag to check operation */
> );
> ```

> The response status can be
>
> ```java
> (int) UtilitiesCapture.INTERRUPT;       /* (0) process interrupted */
> ```
> 
> ```java
> (int) UtilitiesCapture.SUCCESS;         /* (1) process successfull */
> ```
>
> ```java
> (int) UtilitiesCapture.ERROR;           /* (2) process error       */
> ```



&nbsp;
  
</details>



<details>
  
<summary>
  <strong> 5.10. Download JSON</strong>
</summary>

&nbsp;

```java
int flag = gc.downloadJSON();             /* floating file chooser   */
```

```java
int flag = gc.downloadJSON((Scene)scene); /* fixed file chooser      */
```

> The return status can be
>
> ```java
> (int) UtilitiesParser.INTERRUPT;        /* (0) process interrupted */
> ```
> 
> ```java
> (int) UtilitiesParser.SUCCESS;          /* (1) process successfull */
> ```
>
> ```java
> (int) UtilitiesParser.ERROR;            /* (2) process error       */
> ```



&nbsp;
   
</details>



<details>
  
<summary>
  <strong> 5.11. Upload JSON</strong>
</summary>

&nbsp;

```java
int flag = gc.uploadJSON();               /* floating file chooser   */
```

```java
int flag = gc.uploadJSON((Scene)scene);   /* fixed file chooser      */
```


> The return status can be
>
> ```java
> (int) UtilitiesParser.INTERRUPT;        /* (0) process interrupted  */
> ```
> 
> ```java
> (int) UtilitiesParser.SUCCESS;          /* (1) process successfull  */
> ```
>
> ```java
> (int) UtilitiesParser.ERROR;            /* (2) process error        */
> ```


&nbsp;
   
</details>



