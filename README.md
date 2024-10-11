# (Java FX) PathGraph

<img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/socialpreview26.png" alt="Icona" width="100%"/>


---
  
PathGraph fork based on [SmartGraph](https://github.com/brunomnsilva/JavaFXSmartGraph) is an adapted library to work with path graphs that uses nodes, edges and associated costs.
Provided to user-friendly interface in a stable user-experience in witch mange dynamically the path graphs.
Ability of find and show the shortest path, make screenshots and upload or download json of the graphs.

## Main Index

 1. [About](#about)
 2. [User Experience](#user-experience)
 3. [Get Started](#get-started)
 4. [Usage](#usage)
 5. [Licenses](#licenses)


## 1. About <div id="about"/>


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

This library is a fork based on the source code of the [SmartGraph](https://github.com/brunomnsilva/JavaFXSmart) [v2.0.0](https://github.com/brunomnsilva/JavaFXSmart/releases/tag/v2.0.0) library on which existing classes have been modified and new ones have been added. PathGraph is therefore the adaptation of SmartGraph to specific path graphs features in a stable user interface.


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








## 2. User Experience <div id="user-experience"/>

<img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/sp.gif" alt="Icona" width="400"/>


<details>
  
<summary>
  <strong> Graph Interactions</strong>
</summary>

&nbsp;

The user make **Douple Click** or **Right Click** on one of this components of the  to do:
| Background| Node | Edge |
| ------------ | ------------ | ------------ |
| <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/clickBackground.gif" alt="Icona" width="100%"/> | <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/test7ui.gif" alt="Icona" width="100%"/> | <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/test8ui.gif" alt="Icona" width="100%"/>|
| [New Node](#new-node) | [New Edge](#new-edge) | [Delete Edge](#delete-edge) |
|  | [Delete Node](#delete-node) | [Rotate Edge](#rotate-edge) |
|  |  | [Split Edge](#split-edge) |
|  |  | [Set Cost](#set-cost) |

> Empty callbacks to disable interactions on graph

> Ability to create and use custom callbacks of the graph interactions 



&nbsp;

</details>




<details>

  
<summary>
  <strong> UI Interactions</strong>
</summary>

&nbsp;


| Advanced| Edge and Nodes | Graph |
| ------------ | ------------ | ------------ |
| <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/test5ui.gif" alt="Icona" width="100%"/> | <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/test1ui.gif" alt="Icona" width="100%"/> | <img src="https://github.com/vittorioPiotti/PathGraph-ForkBased/blob/master/github/test4ui.gif" alt="Icona" width="100%"/>|
| Upload | [New Node](#new-node)  |  Clear Graph |
| Download| [Delete Node](#delete-node) |  Drag  |
| | [Rename Node](#rename-node) |   Adjust Position  |
| |[New Edge](#new-edge) |  Take Screenshot |
| | [Delete Edge](#delete-edge)  |  Zoom | 
| | [Rotate Edge](#rotate-edge) |   |
| | [Set Cost](#set-cost) |  |
| | [Split Edge](#split-edge) |  |
| | [Show Path](#show-path)  |  |


&nbsp;

</details>






## 3. Get Started <div id="get-started"/>



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
  <strong> 2. Dependencies</strong>
</summary>

&nbsp;

_Working on to upload library on maven dipencencies to available the library distribution_


&nbsp;

</details>




<details>
  
<summary>
  <strong> 3. Prepare <div id="prepare"/></strong>
</summary>

&nbsp;


### UI 



#### PathGraph 

```java
/* only graph without interactions */
PathGraph pg = new PathGraph()                                           
```

> Manual graph callbacks configuration to enable custom interactions
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



#### PathGraphUI

```java
PathGraphUI pg = (
  new PathGraphUI(
    (Stage) primaryStage,
    (Scene) scene
  )
); 
```



> Custom interactions automatically configurated

> Manual UI configurations to enable custom components
>
> ```java
> pg.setUI(
> 
>     /* is enabled top-left menu */
>     (boolean) true,
> 
>     /* is enabled bot-left menu */              
>    (boolean) true,
> 
>     /* is enabled bot-mid menu */
>     (boolean) false,
> 
>      /* is enabled right-mid menu */
>     (boolean) false,
> 
>     /* is enabled top-right menu */           
>     C(boolean) false,
> 
>     /* is hide UI */        
>     (boolean) true
> 
> );
> ```






#### Hybrid

```java
PathGraph pg = (
  new PathGraphUI(
    (Stage) primaryStage,
    (Scene) scene
  )
); 
```







```java
/* to set use and customize UI features     */
/* all of the UI components are enabled */
PathGraphUI pg = (
  new PathGraphUI(
    (Stage) primaryStage,
    (Scene) scene,

    /* configure UI features */
    (Boolean[]) uiFeatures,

    /* configure graph features */
    (Boolean[]) graphFeatures,

    /* configure graph styles */
    (String[]) graphStyles,

  )
);
```


```java
/* to use and customize UI features     */
/* all of the UI components are enabled */
PathGraphUI pg = (
  new PathGraphUI(
    (Stage) primaryStage,
    (Scene) scene,
  )
);
```







&nbsp;

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
  <strong> 4. Setup <div id="setup"/></strong>
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





## 4. Usage <div id="usage"/>



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
    (char) 'Z'

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
    (char) 'Z',

    /* end node */
    (char) 'C'

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







### 4.3. Graph <div id="graph"/>


<details>

<summary>
  <strong>  Get Nodes</strong>
</summary>

&nbsp;


&nbsp;
   
</details>


<details>

<summary>
  <strong>  Get Edges</strong>
</summary>

&nbsp;


&nbsp;
   
</details>

<details>

<summary>
  <strong>  Get Connections</strong>
</summary>

&nbsp;


&nbsp;
   
</details>


<details>
  
<summary>
  <strong>  Get Graph</strong>
</summary>

&nbsp;


&nbsp;
   
</details>



<details>

<summary>
  <strong>  Get Path</strong>
</summary>

&nbsp;


&nbsp;
   
</details>



<details>
  
<summary>
  <strong>  Show Path <div id="show-path"/> </strong> 
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


### 4.4. UI <div id="ui"/>


> [!NOTE]
> Features only avaible on `PathGraphUI` class type [(see)](#prepare)
> 
> ```java
> PathGraphUI pg = (
>   new PathGraphUI(
>     primaryStage,
>     scene
>   )
> );
> ```
>
> > Not is supported using `PathGraph` class type

<details>
  
<summary>
  <strong> Set UI <div id="set-ui"/> </strong>
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




<details>
  
<summary>
  <strong> Enable UI <div id="toggle-ui"/> </strong>
</summary>

&nbsp;


```java
pg.enableUI();
```

&nbsp;

</details>



<details>
  
<summary>
  <strong> Disable UI <div id="toggle-ui"/> </strong>
</summary>

&nbsp;


```java
pg.disableUI();
```

&nbsp;

</details>



## 5. Licenses <div id="licenses"/>



> [!NOTE]
> SVG icons from **Bootstrap**




<details>
  
<summary>
  <strong> PathGraph</strong>
</summary>

&nbsp;

**Copyright** 2024 Vittorio Piotti [(GitHub page)](https://github.com/vittorioPiotti) [(Personal page)](https://vittoriopiotti.altervista.org/) 

**Version** Not released

**License** [GPL-3.0](https://github.com/vittorioPiotti/JavaFXPathGraph/blob/master/LICENSE.txt)

&nbsp;

</details>



<details>
  
<summary>
  <strong> SmartGrah</strong>
</summary>

&nbsp;


**Copyright** 2019 - 2024 Bruno Silva [(GitHub page)](https://github.com/brunomnsilva) [(Personal page)](https://www.brunomnsilva.com/) 

**Version** [v2.0.0](https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)

**License** [MIT](https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)


&nbsp;

</details>


<details>
  
<summary>
  <strong> Bootstrap</strong>
</summary>

&nbsp;

**Copyright** 2011-2018 The Bootstrap Authors 

**Version** v4.0.0

**License** [MIT](https://github.com/twbs/bootstrap/blob/master/LICENSE)


&nbsp;

</details>








