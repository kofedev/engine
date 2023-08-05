## Kofe Simple Engine / Kalba Engine

<p>This code is a demo fragment of an e-shop full-stack prototype engine that
was developed for Kalba Lab startup. The prototype is designed for developing
a customizable multilingual web shop, allowing administrators without programming
knowledge to manage system languages, product and category descriptions,
and UI text elements in various languages.</p>
The prototype was developed using Java Spring Boot and Angular. Links to demo:<br>
<li><a href="https://kalbaengine.shop/">shop prototype (kalbaengine.shop)</a></li>
<li><a href="https://kalbaengine.services/">admin panel prototype (kalbaengine.services)</a></li>

## Base conceptions

<b>Descriptors</b><br><br>
The engine operates on a fundamental concept of descriptors.
Each <i>node</i> (which can be either a "category" or a "product") is associated with
a specific set of descriptors.
A crucial rule is followed, which states that there must be one descriptor for each language.

```mermaid
graph LR
    A[Node] --- B[Descriptor on language A]
    A --- C[Descriptor on language B]
    A --- D[Descriptor on language C]
    A --- E[Descriptor on language ...]
```

<p>For example, if the system has three languages, Lithuanian, English, and German,
a product like "Book" will have three descriptors providing information in each
of the three languages. When an administrator adds a new language,
the engine automatically expands all descriptors to include the new language.</p>

```mermaid
graph TD
    A[Book] --> B[Descriptor on Lithuanian]
    A --> C[Descriptor on English]
    A --> D[Descriptor on German]

```

<p>Each descriptor contains three information fields: a title, a brief description,
and a full description (a link to the .html file with "full" description).</p>

```mermaid
graph TD
    A[Descriptor] --> B[Title]
    A --> C[Brief]
    A --> D[Full]
    D --- J[link to .html file<br>with long description]
```

<p>
<b>Node</b><br><br>
<i>Node</i> is a data "carrier" that forms a hierarchical system similar to a file system.
</p>

```mermaid
graph TD
    A[Node] -.- A[Node]
    A -.- B[Descriptor]
    B -.- C[Language]
```

<p><i>Node</i> can be interpreted as a "category" or as a "product".</p>

```mermaid
graph TD
    A[Node] --> B[Node]
    A --> C[Node]
    A --> D[Node]
    B --> E[Node]
    B --> F[Node]
    F --> J[Node]
    J --> O[Node]
    J --> P[Node]
    J --> R[Node]
    J --> S[Node]
```

```mermaid
graph TD
    A[Category] --> B[Category]
    A --> C[Category]
    A --> D[Category]
    B --> E[Category]
    B --> F[Category]
    F --> J[Category]
    J --> O[Product]
    J --> P[Product]
    J --> R[Product]
    J --> S[Product]
```




