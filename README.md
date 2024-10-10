# (Java FX) PathGraph

<img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/preview906.png" alt="Icona" width="100%"/>


---
  
PathGraph fork based on [SmartGraph](https://github.com/brunomnsilva/JavaFXSmartGraph) is an adapted library to work with path graphs that uses nodes, edges and associated costs.
Provided to user-friendly interface in a stable user-experience in witch mange dynamically the path graphs.
Ability of find and show the shortest path, make screenshots and upload or download json of the graphs.

## Index

 1. [About](#about)
 2. [User Experience](#user-experience)
 3. [Get Started](#get-started)
 4. [Basic Usage](#basic-usage)
 5. [Advanced Usage](#advanced-usage)
 6. [Licenses](#licenses)


## About


<details>
  
<summary>
  <strong> Why PathGraph</strong>
</summary>

&nbsp;

If you need a ready-to-use library for user-side representing path graphs in which there are nodes, edges, and associated weights, which offers a user-friendly interface to represent, manage, and interact with path graphs, then Path is the right solution for you.

&nbsp;

</details>

<details>


<summary>
  <strong> Fork Based Project</strong>
</summary>

&nbsp;

This library is a fork based on the source code of the [SmartGraph](https://github.com/brunomnsilva/JavaFXSmart) [v2.0.0](https://github.com/brunomnsilva/JavaFXSmart/releases/tag/v2.0.0) library on which existing classes have been modified and new ones have been added. Path is therefore the adaptation of Smart to improve the user-experience in a user-firendly interface.

&nbsp;

</details>


<details>


<summary>
  <strong> How Work </strong>
</summary>

&nbsp;



Provided to represent path graphs through nodes, edges, and associated costs with the following logic:

* Nodes can be only characters with uppercase alphabet letters to maxium 26 nodes
* Not provided over limits of two edges with same direction between two same nodes
* Not provided loops creation
* Edges can be directed:



    
  
    ```java
    /* Edge without direction */
    (int) SmartEdgeBase.DIRECTION_BIDIRECTIONAL;
    ```

   
    
    ```java
    */ Edge in natural direction */
    (int) SmartEdgeBase.DIRECTION_FIRST;
    ```
    
     
    
    ```java
    */ Edge in opposite direction */
    (int) SmartEdgeBase.DIRECTION_SECOND;
    ```




> Adding new edge to nodes with one bidirectional edge implies the automatic adjustment of the bidirectional edge to redirect it at the opposite direction of the new edge

> Adding new bidirectional edge to nodes with one bidirectional edge implies the automatic adjustment of both edges to redirect them at the preferred direction to make them in opposite direction from each other



&nbsp;

</details>








## User Experience



<details>
  
<summary>
  <strong> Graph Interactions</strong>
</summary>

&nbsp;

The user make **Douple Click** or **Right Click** on one of this components of the  to do:
| Background| Node | Edge |
| ------------ | ------------ | ------------ |
| <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/clickBackground.gif" alt="Icona" width="100%"/> | <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/clickNode.gif" alt="Icona" width="100%"/> | <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/clickEdge.gif" alt="Icona" width="100%"/>|
| New Node | New Edge | Delete Edge |
|  | Delete Node | Direct Edge |
|  |  | Split Edge |
|  |  | Edit Edge |

> Empty callbacks to disable interactions on graph

> Ability to create and use custom callbacks of the graph interactions 



&nbsp;

</details>




<details>

  
<summary>
  <strong> UI Interactions</strong>
</summary>

&nbsp;



&nbsp;

</details>






## Get Started



<details>
  
<summary>
  <strong> 1. Requirements</strong>
</summary>

&nbsp;

 * Java Version: `21.0.3` [(link)](https://www.oracle.com/java/technologies/downloads/#java21)
 * JavaFX Version: `22.0.2+4` [(link)](https://gluonhq.com/products/javafx/)

&nbsp;

</details>



<details>
  
<summary>
  <strong> 2. Import</strong>
</summary>

&nbsp;

_Working on to upload library on maven dipencencies to available the library distribution_


&nbsp;

</details>




<details>
  
<summary>
  <strong> 3. Prepare</strong>
</summary>

&nbsp;



```java
/* To apply default UI features */
PathGraph pg = (
  new PathGraphUI(
    (Stage) primaryStage,
    (Scene) scene
  )
); 
```


```java
/* To apply custom UI features */
PathGraph pg = new PathGraph()                                           
```

> Manual callback configurations if using `PathGraph` instead of `PathGraphUI`
>
> ```java
> pg.setAllCallbacks(
> 
>     /* to possibly close an open `ContextMenu` */
>     Runnable closeContextMenu,
> 
>     /* action to perform on arrow edge event   */              
>     BiConsumer<MouseEvent, Edge<E, V>> onClickArrow,
> 
>     /* action to perform on node event         */
>     BiConsumer<MouseEvent, Vertex<V>> onClickNode,
> 
>     /* action to perform on click background   */
>     Consumer<MouseEvent> onClickBackground,
> 
>     /* action to perform on scroll background  */            
>     Consumer<Double> onChangeZoom,
> 
>     /* action to perform on drag background    */        
>     Runnable doAdjustPosition
> 
> );
> ```
> > It can be done in any time and in any case
>
> > Configure empty callbacks to disable graph interactions


&nbsp;

</details>



  
<details>
  
<summary>
  <strong> 4. Setup</strong>
</summary>

&nbsp;

   
```java
pg.setup();
```

> Setup is required **to enable the use** of library

> To call **only after** called `(Stage) primaryStage.show()`

&nbsp;

</details>


<details>
  
<summary>
  <strong> 5. Ready</strong>
</summary>

&nbsp;

> Operations are available **only** after calling `pg.setup()`

&nbsp;

</details>






## Usage


> Every method is a function type and return the status of the operation

> Each method automatically update the graph

> Operations are available **only** after calling `pg.setup()`
   

### Node

<details>
  
<summary>
  <strong> New Node</strong>
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
  <strong> Rename Node</strong>
</summary>

&nbsp;

```java
boolean flag = (
  pg.renameNode(
    /* last node name */
    (char)'A',
    /* new node name */
    (char)'B'
  )
);
```

&nbsp;

</details>



<details>
  
<summary>
  <strong> Delete Node</strong>
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


### Edge

<details>
  
<summary>
  <strong> New Edge</strong>
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
  <strong> Delete Edge</strong>
</summary>

&nbsp;

```java
boolean flag = (
  pg.deleteEdge(
    /* start node */
    (char) 'A',
    /* end node */
    (char) 'Z'
  )
);
```



&nbsp;

</details>



<details>
  
<summary>
  <strong> Rotate Edge</strong>
</summary>

&nbsp;

```java
/* Default rotation */
boolean flag = (
  pg.rotateEdge(
    /* start node */
    (char) 'Z',
    /* end node */
    (char) 'C'
  )
);                                                   
```


```java
/* Rotation with specific direction */
boolean flag = (
  pg.rotateEdge(
    /* start node */
    (char) 'Z',
    /* end node */
    (char) 'C',
    /* edge direction */
    (int) SmartGraphEdgeBase.DIRECTION_FIRST
  )
);     
```


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
  <strong> Split Edge</strong>
</summary>

&nbsp;

```java
boolean flag = (
  pg.splitEdge(
    /* start node */
    (char) 'Z',
    /* end node */
    (char) 'C'
  )
);   
```


```java
boolean flag = pg.splitEdge((PseudoEdge) e);                                           
```

&nbsp;

</details>




<details>
  
<summary>
  <strong> Set Cost</strong>
</summary>

&nbsp;

```java
boolean flag = (
  pg.splitEdge(
    /* start node */
    (char) 'Z',
    /* end node */
    (char) 'C',
    /* edge cost */
    (int) 200
  )
);  
```


&nbsp;
   
</details>







### Graph



<details>
  
<summary>
  <strong>  Show Path</strong>
</summary>

&nbsp;


&nbsp;
   
</details>

<details>
  
<summary>
  <strong>  Clear Graph</strong>
</summary>

&nbsp;


&nbsp;
   
</details>


<details>
  
<summary>
  <strong> Take Screenshot</strong>
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

> Handle both asynchronous operations
>
> ```java
> pg.takeScreenshot().thenAccept(status -> {
>   /* Use flag to check operation */
>   int flag = (int) status; 
> );
> ```

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
  <strong> Download JSON</strong>
</summary>

&nbsp;

```java
/* floating file chooser */
int flag = pg.downloadJSON();             
```

```java
/* fixed file chooser */
int flag = pg.downloadJSON((Scene) scene); 
```

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
  <strong>  Upload JSON</strong>
</summary>

&nbsp;

```java
/* floating file chooser */
int flag = pg.uploadJSON();               
```

```java
/* fixed file chooser */
int flag = pg.uploadJSON((Scene)scene);  
```


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


## Licenses

| Component          | Version         | Copyright                                      | License                                                                                            |
|---------------------|------------------|------------------------------------------------|----------------------------------------------------------------------------------------------------|
| [PathGraph](https://github.com/vittorioPiotti/JavaFXPathGraph) | Not released    | 2024 Vittorio Piotti [(GitHub page)](https://github.com/vittorioPiotti) [(Personal page)](https://vittoriopiotti.altervista.org/)            | [GPL-3.0 ](https://github.com/vittorioPiotti/JavaFXPathGraph/blob/master/LICENSE.txt) |
| [JavaFXSmartGraph](https://github.com/brunomnsilva/JavaFXSmartGraph)        | [v2.0.0](https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)           | 2019 - 2024 Bruno Silva [(GitHub page)](https://github.com/brunomnsilva) [(Personal page)](https://www.brunomnsilva.com/)                          | [MIT](https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)       |
| Bootstrap          | v4.0.0    | 2011-2018 The Bootstrap Authors   | [MIT ](https://github.com/twbs/bootstrap/blob/master/LICENSE) |



> [!NOTE]
> SVG icons from **Bootstrap**



