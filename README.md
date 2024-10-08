# (Java FX) PathGraph



  
PathGraph fork based on [SmartGraph](https://github.com/brunomnsilva/JavaFXSmartGraph) is an adapted library to work with path graphs that uses nodes, edges and associated costs.
Provided to user-friendly interface in a stable user-experience in witch mange dynamically the path graphs.
Ability of find and show the shortest path, make screenshots and upload or download json of the graphs.

## GET STARTED

Provided to represent path graphs through nodes, edges, and associated costs with the following logic:


* Not provided over limits of two edges with same direction
* Not provided loops creation
* Edges can be directed:


    ```java
    SmartGraphEdgeBase.DIRECTION_FIRST; // Edge in the natural direction (with an arrow).
    ```

    ```java
    SmartGraphEdgeBase.DIRECTION_SECOND; // Edge in the opposite direction (with an arrow).
    ```

    ```java
    SmartGraphEdgeBase.DIRECTION_BIDIRECTIONAL; // Edge without direction (no arrow).
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
>     Runnable closeContextMenu,
>     BiConsumer<MouseEvent, Edge<E, V>> onClickArrow,
>     BiConsumer<MouseEvent, Vertex<V>> onClickNode,
>     Consumer<MouseEvent> onClickBackground,
>     Consumer<Double> onChangeZoom,
>     Runnable doAdjustPosition
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

<details>
  
<summary>
  <strong> 3.1. New Node</strong>
</summary>


```java
boolean flag = gc.newNode("A");
```


</details>


<details>
  
<summary>
  <strong> 3.2. New Edge</strong>
</summary>


```java
boolean flag = gc.newEdge("A", "Z", 23); // Default not bidirectional direction
```

```java
boolean flag = gc.newEdge("A", "Z", 23, true); // Default direction (can be bidirectional)
```

```java
boolean flag = gc.newEdge("A", "Z", 23, SmartGraphEdgeBase.DIRECTION_SECOND); // Custom direction
```

</details>


<details>
  
<summary>
  <strong> 3.3. Rename Node</strong>
</summary>


```java
boolean flag = gc.renameNode("A", "K");
```

</details>



<details>
<summary>
  <strong> 3.4. Delete Edge</strong>
</summary>


```java
boolean flag = gc.deleteEdge("A", "Z");
```

</details>



<details>
  
<summary>
  <strong> 3.5. Rotate Edge</strong>
</summary>


```java
boolean flag = gc.rotateEdge("Z", "C"); // Default rotation
```

```java
boolean flag = gc.rotateEdge("Z", "C", SmartGraphEdgeBase.DIRECTION_FIRST); // Rotation with specific direction
```

</details>




<details>
  
<summary>
  <strong> 3.6. Split Edge</strong>
</summary>


```java
boolean flag = gc.splitEdge("Z", "C");
```

</details>




<details>
  
<summary>
  <strong> 3.7. Change Cost</strong>
</summary>


```java
boolean flag = gc.changeCost("Z", "C", 200);
```
   
</details>


<details>
  
<summary>
  <strong> 3.8. Take Screenshot</strong>
</summary>

```java
(CompletableFuture<Integer>) gc.takeScreenshot(); // with animation
```

```java
(CompletableFuture<Integer>) gc.takeScreenshot(false); // set if is animated
```

> [!NOTE]  
> Handle asynchronous operation
>
> ```java
> gc.downloadPNG().thenAccept(status -> {
>     // Use status to check operation
> );
> ```



   
</details>



---
<img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/preview906.png" alt="Icona" width="100%"/>



Working on [PathGraph](https://github.com/vittorioPiotti/PathGraph-ForkBased)
