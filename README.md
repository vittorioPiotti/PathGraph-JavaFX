# (Java FX) PathGraph

<img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/preview906.png" alt="Icona" width="100%"/>


---
  
PathGraph fork based on [SmartGraph](https://github.com/brunomnsilva/JavaFXSmartGraph) is an adapted library to work with path graphs that uses nodes, edges and associated costs.
Provided to user-friendly interface in a stable user-experience in witch mange dynamically the path graphs.
Ability of find and show the shortest path, make screenshots and upload or download json of the graphs.



Working on [PathGraph](https://github.com/vittorioPiotti/PathGraph-ForkBased)

## GET STARTED

Provided to represent path graphs through nodes, edges, and associated costs with the following logic:


* Not provided over limits of two edges with same direction
* Not provided loops creation
* Edges can be directed:

    ```java
    (int) SmartGraphEdgeBase.DIRECTION_BIDIRECTIONAL = 0;   // Edge without direction (no arrow).
    ```

    ```java
    (int) SmartGraphEdgeBase.DIRECTION_FIRST = 1;           // Edge in the natural direction (with an arrow).
    ```

    ```java
    (int) SmartGraphEdgeBase.DIRECTION_SECOND = 2;          // Edge in the opposite direction (with an arrow).
    ```



> Adding new edge to nodes with one bidirectional edge implies the automatic adjustment of the bidirectional edge to redirect it at the opposite direction of the new edge

> Adding new bidirectional edge to nodes with one bidirectional edge implies the automatic adjustment of both edges to redirect them at the preferred direction to make them in opposite direction from each other


### 1. Prepare 

```java
SmartGraph gc = new SmartGraphUI(primaryStage, scene); // To apply UI features (extends SmartGraph)
```

_or_


```java
SmartGraph gc = new SmartGraph() // To apply Custom UI features
```
> [!NOTE]  
> Manual callback configurations if using `SmartGraph` instead of `SmartGraphUI`
>
> ```java
> gc.setAllCallbacks(
>     Runnable closeContextMenu,                         // to possibly close an open `ContextMenu`
>     BiConsumer<MouseEvent, Edge<E, V>> onClickArrow,   // action to perform on arrow edge event
>     BiConsumer<MouseEvent, Vertex<V>> onClickNode,     // action to perform on node event
>     Consumer<MouseEvent> onClickBackground,            // action to perform on click background
>     Consumer<Double> onChangeZoom,                     // action to perform on scroll background
>     Runnable doAdjustPosition                          // action to perform on drag background
> );
> ```
> > It can be done in any time and in any case


### 2. Setup

 * Setup is required to enable graph creation features
 * To call only after called `(Stage) primaryStage.show()`
   
```java
gc.setup();
```

### 3. Make

 * Operations are available **only** after calling `gc.setup()`
 * Every method is a function type and return the status of the operation



#### Node

<details>
  
<summary>
  <strong> 3.1. New Node</strong>
</summary>

&nbsp;


```java
boolean flag = gc.newNode("A");
```

&nbsp;

</details>


<details>
  
<summary>
  <strong> 3.3. Rename Node</strong>
</summary>

&nbsp;

```java
boolean flag = gc.renameNode("A", "K");
```

&nbsp;

</details>



<details>
  
<summary>
  <strong> 3.3. Delete Node</strong>
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
  <strong> 3.4. New Edge</strong>
</summary>

&nbsp;

```java
boolean flag = gc.newEdge("A", "Z", 23); // Default not bidirectional direction
```




```java
boolean flag = gc.newEdge("A", "Z", 23, true); // Default direction (can be bidirectional)
```



```java
boolean flag = gc.newEdge("A", "Z", 23, SmartGraphEdgeBase.DIRECTION_SECOND); // Custom direction
```

&nbsp;

</details>





<details>
<summary>
  <strong> 3.5. Delete Edge</strong>
</summary>

&nbsp;

```java
boolean flag = gc.deleteEdge("A", "Z");
```

&nbsp;

</details>



<details>
  
<summary>
  <strong> 3.6. Rotate Edge</strong>
</summary>

&nbsp;

```java
boolean flag = gc.rotateEdge("Z", "C"); // Default rotation
```



```java
boolean flag = gc.rotateEdge("Z", "C", SmartGraphEdgeBase.DIRECTION_FIRST); // Rotation with specific direction
```

&nbsp;

</details>




<details>
  
<summary>
  <strong> 3.7. Split Edge</strong>
</summary>

&nbsp;

```java
boolean flag = gc.splitEdge("Z", "C");
```

&nbsp;

</details>




<details>
  
<summary>
  <strong> 3.8. Change Cost</strong>
</summary>

&nbsp;

```java
boolean flag = gc.changeCost("Z", "C", 200);
```

&nbsp;
   
</details>


#### Other

<details>
  
<summary>
  <strong> 3.9. Take Screenshot</strong>
</summary>

&nbsp;

```java
(CompletableFuture<Integer>) gc.takeScreenshot(); // with animation
```

```java
(CompletableFuture<Integer>) gc.takeScreenshot(false); // set if is animated
```

> Handle asynchronous operation
>
> ```java
> gc.takeScreenshot().thenAccept(status -> {
>     int flag = (int) status; // Use flag to check operation
> );
> ```

> The status can be
>
> ```java
> (int) UtilitiesCapture.INTERRUPT = 0;
> ```
> 
> ```java
> (int) UtilitiesCapture.SUCCESS = 1;
> ```
>
> ```java
> (int) UtilitiesCapture.ERROR = 2;
> ```


&nbsp;
  
</details>



<details>
  
<summary>
  <strong> 3.10. Download JSON</strong>
</summary>

&nbsp;

```java
int flag = gc.downloadJSON();       // floating file chooser
```

```java
int flag = gc.downloadJSON((Scene)scene); // fixed file chooser
```

> The status can be
>
> ```java
> (int) UtilitiesParser.INTERRUPT = 0;
> ```
> 
> ```java
> (int) UtilitiesParser.SUCCESS = 1;
> ```
>
> ```java
> (int) UtilitiesParser.ERROR = 2;
> ```


&nbsp;
   
</details>



<details>
  
<summary>
  <strong> 3.11. Upload JSON</strong>
</summary>

&nbsp;

```java
int flag = gc.uploadJSON();       // floating file chooser
```

```java
int flag = gc.uploadJSON((Scene)scene); // fixed file chooser
```


> The status can be
>
> ```java
> (int) UtilitiesParser.INTERRUPT = 0;
> ```
> 
> ```java
> (int) UtilitiesParser.SUCCESS = 1;
> ```
>
> ```java
> (int) UtilitiesParser.ERROR = 2;
> ```

&nbsp;
   
</details>



