# (Java FX) PathGraph

PathGraph fork based on SmartGraph is an adapted library to work with path graphs that uses nodes, edges and associated costs.
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


### 1. Create Object

```java
SmartGraph gc = new SmartGraphUI(primaryStage, scene); // To apply UI features
```

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
> > It can be done in any time


### 2. Setup


```java
SmartGraph gc = new SmartGraphUI(primaryStage, scene); //To call only after calling (Stage) primaryStage.show()
```
> Setup is required to enable graph creation features.

   *    - WITH UI Features:
     *          SmartGraph gc = new SmartGraphUI(primaryStage, scene); (SmartGraphUI extends SmartGraph)
     *
     *    - WITHOUT UI Features:
     *          SmartGraph gc = new SmartGraph();
     *
     *      Note: If using WITHOUT UI features (new SmartGraph()), you need to manually configure the callbacks.
     *      To activate callbacks at any time, use:
     *          gc.setAllCallbacks(
     *              Runnable closeContextMenu,
     *              BiConsumer<MouseEvent, Edge<E, V>> onClickArrow,
     *              BiConsumer<MouseEvent, Vertex<V>> onClickNode,
     *              Consumer<MouseEvent> onClickBackground,
     *              Consumer<Double> onChangeZoom,
     *              Runnable doAdjustPosition
     *          );
     *
     *      - Ability to add the component (SmartGraph) in any container providing to full responsive experience
     *




PathGraph is an adapted version of the [SmartGraph](https://github.com/brunomnsilva/JavaFXSmartGraph) library, developed to implement new user-experience features in a stable user-friendly interface.

---
<img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/preview906.png" alt="Icona" width="100%"/>



Working on [PathGraph](https://github.com/vittorioPiotti/PathGraph-ForkBased)
