# The TypeScript Handbook for Studio Client Jangaroo Developers

## Basics

- `public` is the default in TypeScript, so all public modifiers are gone
- Class members do not use `function`, `var` or `const` keywords in TypeScript. Methods use parenthesis, const becomes readonly.
- The constructor is not named like the class but uses the well-known name constructor. No return type is specified.
- This built-in ActionScript classes `String`, `Number`, `Boolean` are usually used in the "unboxed", lower-case variants `string`, `number`, `boolean` in TypeScript.
- `this`  must always be used explicitly (like in JavaScript)
- `private` is available in TypeScript, but only affects the compiler. To be private for subclasses and at runtime, there is a new JavaScript syntax: Using a member name that starts with `#` makes it private.
- A set  accessor may not specify a void return value in TypeScript.
- The ActionScript `untyped type *` maps to TypeScript's any.
- ActionScript classes may contain "top-level" code, optionally enclosed in curly brackets, which is executed when the class is first loaded / used. In TypeScript, such code is put in a block prefixed by the `static` keyword.


<table>
<tr>
  <th>ActionScript </th>
  <th>TypeScript</th>
</tr>
<tr>
<td><pre>
public class Foo extends SuperFoo {
  public static const FOO: * = "FOO";
  public var foo: String;
  private var _bar: Number;
  
  public function Foo(newBar: Number) {
    super();
    _bar = newBar;
  }
  
  public function get bar(): Number {
    return _bar;
  }
  
  public function set bar(value: Number): void {
    _bar = value;
  }
  
  protected function hook(): Boolean {
    return false;
  }

  Registry.register(Foo);
}
</pre></td>

<td><pre>class Foo extends SuperFoo {
  static readonly FOO: any = "FOO";
  foo: string;
  #bar: number;
  
  constructor(newBar: number) {
    super();
    this.#bar = newBar;
  }
  
  get bar(): number {
    return this.#bar;
  }
  
  set bar(value: number) {
    this.#bar = value;
  }
  
  protected hook(): boolean {
    return false;
  }
  
  static { Registry.register(Foo); }
}</pre></td>

</tr>
<table>




## Interfaces

While ActionScript and TypeScript have a notion of interfaces and even use the same keyword, the semantics are different.
In ActionScript, an interface has to be implemented by a class explicitly. 
It can be checked at run-time whether an object is an instance of a given interface, 
using the ActionScript built-in operator `is`. This means that an interface must have some run-time representation.

In TypeScript, a class "automatically" implements an interface when it defines the same member signatures (duck typing). 
You can, however, use the keyword implements to explicitly state that your class intends to implement some interface. 
A TypeScript interface defines a so-called ambient type, that is, a type that is only relevant for 
the compiler / type checker, but not at run-time. Consequently, there is no built-in way to do an instance-of check. 
To simulate this, you must provide custom functions that test a given object (type assertions).

When converting code from ActionScript to TypeScript, we wanted to keep the ActionScript interface semantics, 
so we had to find some way to represent interfaces and the `is`  operator in TypeScript.

Since Jangaroo ActionScript is compiled to JavaScript using the Ext class system, 
there already is a solution at run-time. Interfaces are represented as "empty" Ext classes, 
that is, classes that have no members, but an identity. When a class `A` implements an interface `I`, in Ext, the class 
corresponding to `I` is mixed into `A`. The `is`  check is implemented by looking up the mixins hierarchy of the object's class.

We use a similar approach in TypeScript. An interface is represented as a completely abstract class, that is, an 
abstract class that only has abstract members. At run-time, again, only an empty class with an identity remains. 
When implementing an interface, this abstract class is implemented and mixed in. 
TypeScript allows to "implement a class", because a class defines two entities: 
a value (the "class object" that exists at run-time) and a type (only relevant for the compiler). 
If you use a class in an `implements` clause, only its type is used. The mixin aspect is 
represented in TypeScript by calling the runtime function `AS3.mixin`, which is usually imported as a named import, 
so you'll see it simply as `mixin(Clazz, Interface1, ..., InterfaceN)` after the class declaration.

Furthermore, in ActionScript interfaces, only methods may be declared. 
TypeScript allows to declare fields in interfaces, too. Note that a field and an accessor pair is 
considered two different things in both languages.

The following example illustrates the differences. Assume that accessor pair foo was meant 
as a field (which ActionScript cannot express in an interface), while bar was intended to be 
implemented as an accessor pair. The underscore prefix of `_bar`  is left out in TypeScript, as `#` private names 
do not clash with public names, anyway.

<table>
  <tr>
    <th>ActionScript</th>
    <th>TypeScript</th>
  </tr>
  <tr>
    <td><pre>interface IFoo {
function get foo(): String;
function set foo(value: String): void;

function get bar(): Number;
function set bar(value: Number): void;

function isAFoo(obj: *): Boolean;
}

public class Foo implements IFoo {
  private var _foo: String;
  private var _bar: Number;
  
  public function get foo(): String {
    return _foo;
  }
  
  public function set foo(value: String): void {
    _foo = value;
  }
  
  public function get bar(): Number {
    return _bar;
  }
  
  public function set bar(value: Number): void {
    _bar = value;
  }
  
  public function isAFoo(obj: *): Boolean {
    return obj is IFoo;
  }
}</pre></td>
    <td><pre>abstract class IFoo {
  abstract foo: string;
  
  abstract get bar(): number;
  abstract set bar(value: number);
  
  isAFoo(obj: any): boolean;
  }
  
  class Foo implements IFoo {
  foo: string;
  <code>#</code>bar: number;
  
  get bar(): number {
    return this.#bar;
  }
  
  set bar(value: number) {
    this.#bar = value;
  }
  
  isAFoo(obj: any): boolean {
    return AS3.is(obj, IFoo);
  }
}

AS3.mixin(Foo, IFoo);</pre></td>
  </tr>
</table>




Note that `AS3.is` and `AS3.mixin` are usually imported as a named import, so they can be used simply 
as `is` and `mixin`. See below for details about importing and exporting in TypeScript.


# Imports and Exports

In ActionScript, each compilation unit (usually a class, but interfaces and global variables, constants and 
functions are also compilation units) has a fully qualified name that is globally unique. 
This name is used to reference other compilation units when importing them.

As TypeScript is an extension of ECMAScript, it uses the ECMAScript module system. 
Since ES5, any source file that contains imports and/or exports is a module. 
In import statements, modules are references by file path without extension. 
This file path may either be relative to the current source file, starting with `./` or `../`, 
or it refers to an npm package name and then specifies the relative path within that package.

npm packages are the counterparts of Maven modules, and ActionScript packages correspond 
to TypeScript (ECMAScript) modules, so there is quite some potential for terminology confusion!

So each Jangaroo ActionScript Maven module has been mapped to a Jangaroo npm package, 
and each different ActionScript package within such module has been mapped to a relative 
path to a TypeScript module within that package.

Thus, a main difference between Jangaroo Maven/ActionScript and npm/TypeScript is that 
because ActionScript fully qualified names are globally unique, import references in ActionScript are 
agnostic of the Maven module (there only must be a Maven dependency), while import references in 
TypeScript contain the target's npm package name.

We map Jangaroo Maven module names to npm package names through some pattern-matching-magic, 
considering that npm package names should use a 
scope (here: `@coremedia` for core and `@coremeda-blueprint` for Blueprint packages), 
separated by a slash from the actual npm package name.

To avoid unnecessary deep paths for TypeScript modules in npm packages, we mapped the ActionScript 
package names within one Maven module to a shortened relative path in the npm package. 
We configured the longest possible common ActionScript package name prefix to be left out for the relative path.

As an example, see the Maven module `com.coremedia.ui.toolkit:client-core`, which is mapped 
to `@coremedia/studio-client.ext.client-core`. The Maven module contains ActionScript 
packages `com.coremedia.ui.data...` and `com.coremedia.ui.util`. We tell the `jangaroo-maven-plugin` that the
desired redundant prefix to remove during TypeScript/npm conversion is `com.coremedia.ui` through the 
configuration option `extNamespace`:

```xml
<plugin>
  <groupId>net.jangaroo</groupId>
  <artifactId>jangaroo-maven-plugin</artifactId>
  <extensions>true</extensions>
  <configuration>
    <extNamespace>com.coremedia.ui</extNamespace>
    ...
  </configuration>
</plugin>
```


## Imports

Taken all together, as an example, the ActionScript interface `com.coremedia.ui.data.RemoteBean` 
is converted to the TypeScript module reference `@coremedia/studio-client.ext.client-core/data/RemoteBean`.

Here is the ActionScript and TypeScript import syntax in direct comparison:

<table>
  <tr>
    <th>ActionScript</th>
    <th>TypeScript</th>
  </tr>
  <tr>
    <td><pre>
// from within the same Maven Module and ActionScript package:
// no import necessary!
// from with the same Maven Module, but ActionScript package com.coremedia.ui.util:
import com.coremedia.ui.data.RemoteBean;
// from another Maven module:
import com.coremedia.ui.data.RemoteBean;
    </pre></td>
    <td><pre>
// from within the same npm package and source folder:
import RemoteBean from "./RemoteBean";

// from within the same npm package, but source folder /util:
import RemoteBean from "../data/RemoteBean";

// from another npm package:
import RemoteBean from "@coremedia/studio-client.ext.client-core/data/RemoteBean";
</pre></td>
  </tr>
</table>
 	


# Export

In ActionScript, each compilation unit contains exactly one declaration that is visible 
from the outside. In TypeScript modules, it is possible to export multiple identifiers, 
but there is a default export. Therefore, when converting code from ActionScript, 
it is straight-forward to use this default export to export the primary declaration 
of the compilation unit, usually a class.

While it is possible to combine the declaration and the (default) export of a class, 
the conversion tool does not do so, because later you'll see cases where that is not 
possible or more redundant. So for consistency, 
the conversion tool always ends each source file with the default export:

```ecmascript 6
default export Foo;
```

### A Complete Example Class

Using all the ingredients of the previous sections, the complete example class could look like so:

```ecmascript 6
import { is, mixin } from "@jangaroo/runtime/AS3";
import SuperFoo from "./SuperFoo";
import IFoo from "../api/IFoo";

class Foo extends SuperFoo implements IFoo {
  static readonly FOO: any = "FOO";
  foo: string;
  <code>#</code>bar: number;
  
  constructor(newBar: number) {
  super();
    this.#bar = newBar;
  }
  
  get bar(): number {
    return this.#bar;
  }
  
  set bar(value: number) {
    this.#bar = value;
  }
  
  isAFoo(obj: any): boolean {
    return is(obj, IFoo);
  }
  
  protected hook(): boolean {
    return false;
  }
}

mixin(Foo, IFoo);

default export Foo;
```

# Mixins

Ext JS allows mixins to achieve multiple inheritance between classes. 
Since neither ActionScript nor TypeScript support mixins out of the box, 
we had to find some way to represent them in both languages.

### Mixins in ActionScript

In ActionScript, a class can only extend one other class, but it can implement multiple interfaces. 
So for a class to be used as a mixin, we extract an interface from that class with a custom 
ActionScript annotation `[Mixin("fully qualified name of mixin implementation class")]`. 
Any class `MixinClient` that implements such an interface `acme.IMyMixin` annotated with `[Mixin("acme.MyMixin")]` 
becomes a mixin client class. In other words, `MyMixin` is mixed into `MixinClient`. 
Because ActionScript tools (asdoc, IDEA) do not know of this magic annotation, `MixinClient` also 
must implement all `IMyMixin` methods to comply with ActionScript semantics. 
As of course we do not really want to implement these methods, we just declare them, using the `native` keyword.

```ecmascript 6
// ./acme/IMyMixin.as
package acme {

  [Mixin("acme.MyMixin")]
  public interface IMyMixin {
    
  [Bindable]
  function get mixinConfig(): String;
  
  [Bindable]
  function set mixinConfig(value: String): void;
  
  function doSomething(): Number;
  }
}
```

```ecmascript 6
// ./acme/MyMixin.as
package acme {

  public class MyMixin implements IMyMixin {
    private var _mixinConfig: String = "";
  
    [Bindable]
    public function get mixinConfig(): String {
      return _mixinConfig;
    }
    
    [Bindable]
    public function set mixinConfig(value: String): void {
      _mixinConfig = value;
    }
    
    public function doSomething(): Number {
      return _mixinConfig.length;
    }
  }
}
```

```ecmascript 6
// ./MixinClient.as
import acme.IMyMixin;
import ext.Component;

public class MixinClient extends Component implements IMyMixin {
  [Bindable]
  public native function get mixinConfig(): String;
  
  [Bindable]
  public native function set mixinConfig(value: String): void;
  
  public native function doSomething(): Number;
  
  public function MixinClient(config: Object = null) {
    super(config);
    doSomething();
  }
}
```


# Mixins in TypeScript

In TypeScript, we use a quite similar approach like in ActionScript, but fortunately, the syntax is much more elegant.

To understand how mixins work, it helps to know that in TypeScript, a class consists of a run-time JavaScript 
value and a type, which is only relevant for type checking / at compile-time.
The class identifier represents both aspects. Depending on context, it is clear whether the value, the type, 
or both are meant. When a `class A` extends another `class B`, in the extends clause, `B` refers to both 
the value (JavaScript `class A` will at run-time extend JavaScript `class B`) 
and the type (TypeScript type `A` will at compile-time be a sub-type of type `B`). 
When using a class identifier behind a colon or in the `implements` clause of a class, only its type aspect it used. 
This allows to use a class in an `implements` clause! This equals implementing the interface extracted from that class.

Another TypeScript concept that is relevant here and closely related is declaration merging. 
In TypeScript, a type with the same identifier can be declared multiple times, and all declarations are merged. 
Since a class declares a value and a type, and an interface only declares a type, 
you cannot declare the same class twice, but you can declare a class and an interface using the same identifier. 
What happens is that the interface extracted from the class is merged with the additionally declared interface. 
In this case, TypeScript does not complain about the class not implementing the additional interface methods. 
We like to call such an interface a companion interface of the class, as it comes together with the 
class and adds more declarations (the ones we had to declare as native  in ActionScript).

Using these ingredients, we can declare mixins in TypeScript as follows.

As in Ext JS, a mixin is a usual TypeScript class. A mixin client class implements the interface automatically 
extracted from the mixin class, in other words, it directly implements the mixin class!

But that does not suffice: We have to specify that we do not only want to use the interface, 
but also want to mix in the mixin's methods at run-time. We learned about the `AS3.mixin()` utility function 
in the interface chapter. Maybe now it becomes clear why it is called like that: 
it can do more than just mix in the identity of an interface: it actually mixes in any class with all 
its members into the client class!

Last thing to do is again to prevent the type checker from complaining about missing implementations of the mixin interface, 
since it does not know about the mixin magic. This is much more elegant in TypeScript than in ActionScript: 
Instead of re-declaring every single member using the native keyword, we just declare a companion interface 
of the mixin client class and let that extend the mixin class interface. We could even leave 
out the `implements` clause of the mixin client class itself, but to emphasize what's going on 
(and to help some IDEs that don't really support declaration merging completely), during conversion, 
we generate both clauses.

All in all, the above example results in the following quite more compact TypeScript code.

```ecmascript 6
// ./acme/MyMixin.ts
class MyMixin {
  #mixinConfig: string = "";
  
  get mixinConfig(): string {
    return this.#mixinConfig;
  }
  
  set mixinConfig(value: string) {
    this.#mixinConfig = value;
  }
  
  doSomething(): number {
  return this.#mixinConfig.length;
  }
}

export default MyMixin;
```

```ecmascript 6
// ./MixinClient.ts
import { mixin } from "@jangaroo/runtime/AS3";
import Component from "@jangaroo/ext-ts/Component";
import MyMixin from "./acme/MyMixin";

class MixinClient extends Component implements MyMixin {
  constructor(config: any = null) {
    super(config);
    this.doSomething();
  }
}
```

```ecmascript 6
// companion interface, so we don't need to re-declare all mixin members:
interface MixinClient extends MyMixin {}

// use Jangaroo utility method to perform mixin operation:
mixin(MixinClient, MyMixin);

export default MixinClient;
```

# Using the Ext Config System

A major part of Studio Client ActionScript/MXML code deals with Ext JS components, 
plugins, actions, and other Ext JS classes that have in common that they use the Ext Config system.

### How the Ext Config System Works

The Ext Config system is quite a beast, but we'll try to keep things as simple as possible here.

#### Simple Configs in Ext 3.4

When we started with Ext JS 3.4, Configs were a simple concept: To specify the properties of some object to create, 
plain JavaScript object literals are used – not really JSON, because their values may be more complex. 
These objects are passed around and eventually used to derive a class to instantiate, in Ext 3.4 based on 
their `xtype` property. The class constructor is then called with the Config object and 
essentially "applies" (copies) all properties onto itself.

For example, you could specify a button with a label as a config object and then let Ext create 
the actual `Ext.Button` instance from that Config:

```ecmascript 6
var buttonCfg = {
  xtype: "button",
  label: "Click me!"
};

var button = Ext.create(buttonCfg);
console.log(button.label); // logs "Click me!"
```

So in Ext 3.4, Configs were nothing but properties / fields of the target class which 
were "bulk applied" through a JSON-like object.

#### "Bindable" Configs in Ext 6

Things became more complicated with the new class and Config system introduced 
with Ext 4 (we upgraded directly to Ext 6, later to 7). Configs now can be declared for an 
Ext class and then trigger some magic: For every Config property `foo`, Ext generates methods `getFoo()`  
and `setFoo(value)`. 
Note that these are not accessors, but "normal" methods, as Ext 4 came out when browser support 
for accessors was not yet mainstream. They never managed to update their Config system to "real" accessors.

To make things "easier" (?) for the developer, things get even more complicated: 
the generated `setFoo(value)` method looks for two optional "hook" methods that allow to
- transform the value before it is stored: `updateFoo(value) { return transform(value) }`
- trigger side-effects after the value has been set: `applyFoo(value, oldValue)`

Neglecting this additional "convenience", this is how you could define a Config text, 
prevent anything that is not a `string` from being set into that Config 
(at least not when everybody uses the `setText(value)` method), and update the DOM of your 
component when the text is changed afterwards:

```ecmascript 6
Ext.define("acme.Label", {
  extend: "Ext.Component",
  xtype: "acme.label",
  config: {
    text: ""
  },
  
  setText(value) {
    this.value = typeof value === "string" ? value : value ? String(value) : "";
    // if rendered, update my DOM node with 'value'
  }
};
```

```ecmascript 6
var label = Ext.create({ xtype: "acme.label", text: "Hi!"});
label.setText(null);
console.log(button.getText()); // logs the empty string (""), not "null"
```


# Using the Ext 6 Config System in ActionScript

The goal of using ActionScript for Ext JS was to control the comprehensive framework with static typing.

So we really didn't like magically appearing methods, and we also didn't want the developer having 
to declare five (!) members for one config (the config property itself and its get-, set-, update- and apply-method). 
Thus, for ActionScript, we "faked" Ext being more modern than is was (and is) and pretended that you 
could use "real" accessors to access a Config's get- and set-method, and to overwrite these methods.

### Declaring Ext Configs in ActionScript

In ActionScript, the above-mentioned example would look like this:

```ecmascript 6
package acme {
public class Label extends Component {
  public static const xtype: String = "acme.label";
  
  private var _text: String = "";
  
  public function Label(config: Label = null) {
    super(config);
  }
  
  [Bindable]
  public function get text(): String {
    return _text;
  }
  
  [Bindable]
  public function set text(value: String): void {
    _text = typeof value === "string" ? value : value ? String(value) : "";
    // if rendered, update my DOM node with 'value'
  }
  }
}
```


```ecmascript 6
// ActionScript code using this component:
var label = new acme.Label(acme.Label({ text: "Hi!"}));
label.text = null; // <= !!! translated to JS: label.setText(null) !!!
console.log(button.text); // <= !!! translated to JS: label.getText() !!!
```


The `[Bindable]`  annotation is the hint for Jangaroo to convert ActionScript accessors not 
to JavaScript accessor, but to Ext get/set methods. So in ActionScript, the Config access 
looks like a property, but really calls the get/set methods. 
To be more precise, this decision is made at run-time, by rewriting such code to use the 
utility methods `AS3.getBindable(obj, config)` or `AS3.setBindable(obj, config, value)`.

To know when to replace what looks like a property access by a "bindable" access, 
the Jangaroo compiler resolves the declaration of the property and looks for a `[Bindable]` annotation. 
Unfortunately, in TypeScript, this is no longer possible, so we had to find a way to 
represent "bindable" Configs in TypeScript which 

a) looks like meaningful TypeScript code and

b) can be translated to the Ext semantics.


Long story short, we found a way to add "real" accessors for all Configs defined by ActionScript code at run-time! 
So in TypeScript, when we write `label.text`, it actually resolves to an accessor that delegates 
to the `getText` or `setText` method. We even introduced this change for the "normal" JavaScript output mode 
in Jangaroo 4.1, and it seems to work fine!

One more complication is that in real-world Studio ActionScript code, 
we used several same-same-but-different patterns to declare `[Bindable]` Configs. 
The code above is probably the most clean way to do it, but unfortunately another field `_text` 
is created in addition to the "internal" Config field `text`, which is less efficient and can even lead to confusion. 
We tried to capture all patterns we identified and automatically translate them to a 
single TypeScript pattern for bindable Configs, which is presented in the next section.

As you can see in the constructor of the example class, in ActionScript, we use the same type for the 
class and for the Config type. This is actually cheating. 
The class has far more properties (methods!) than the Config type, and in Ext, some class properties even 
have a different type than their Config property counterpart (for example, Container#items is an `Array` in the Config type, 
but a `MixedCollection` in the class).

The reason we do not declare two separate types (as we did in Jangaroo 2, by the way) is that ActionScript 
only allows one exported declaration per file. So to declare the Config type separately, 
every class would need two files, and both would still have to be 
consistent (extend the same superclass, use the same mixins). We did not want developers to deal with that.


## Creating Ext Config Objects in ActionScript

Ext often uses the concept of creating Config objects first, and instantiating them later. To be able to do so, a Config object must contain an property that indicates which class to instantiate (later). The three different special Ext properties available to specify the target class are

####   xtype 
The "classic" class hint. Each Ext class may specify a unique `xtype`, which is registered and 
referenced here to identify the class to instantiate. This indirection is meant to separate 
usage and implementation (a bit).

#### alias 
When Ext extended their Config System to more than just components, they though it would 
make sense to introduce prefixes for the different groups of classes. 
Components use `widget.<xtype>`, plugins use `plugin.<type>`, `GridColumns` use `gridcolumn.<type>`. 
The type property used for that purpose before introducing alias has been deprecated.

#### xclass
Introduced last, this is the most straight-forward way to specify the target class: 
Just give its fully-qualified name! Unfortunately, this property does not work everywhere 
in Ext's Classic Toolkit (the one CoreMedia Studio uses), so if a class has an `xtype` / `alias`, you should 
better use that, or even better, all possible meta-properties the class offers.

That said, in ActionScript, you need not worry about all that. 
We introduced special semantics to ActionScript type casts when using them on object literals. 
(This is quite some cheating, too.) To create a Config object for a `class MyClass`, 
instead of calling its constructor, you type-cast an object literal into that class:

```ecmascript 6
var myClassConfig: MyClass = MyClass({
  id: "4711",  // inherited from Component._
  configOption1: "bar",  // MyClass Config property
  configOption2: 42  // the other MyClass Config property
});
```    

This magically adds the appropriate `xtype`, `alias`, and/or `xclass` attributes.

However, in ActionScript, there is no way to type object literals. 
They are always of type Object and any properties are allowed. 
That's why you should use the following pattern to populate a Config object in ActionScript:

```ecmascript 6
var myClassConfig = MyClass({});
myClassConfig.id = "4711";
myClassConfig.configOption1 = "bar";
myClassConfig.configOption2 = 42;
```

While this adds a bit more code, you now have type checks and gain IDE support like completion, 
documentation lookup and navigation.

If needed, you can then use different ways to instantiate the corresponding class:

```ecmascript 6
var myClassInstance: MyClass = new MyClass(myClassConfig);
// OR
var myClassInstance: MyClass = Ext.create(myClassConfig); // careful: no type check!
// OR
var myClassInstance: MyClass = MyClass(Ext.create(myClassConfig)); // at least a run-time type check
```

# Using the Ext 6 Config System in TypeScript

### Declaring the Config Type in TypeScript

In TypeScript, each class using the Ext Config system needs an additional 
interface that describes its Config options. The design goal for the representation of this Config 
interface is to only declare and document Config properties once, although they re-appear on the class itself. 
Also, we need to distinguish simple Configs and bindable Configs. 
Last but not least, Config objects usually only contain a subset of all possible properties.

Here, the TypeScript utility types `Pick` and `Partial` come in handy. `Pick` allows to pick a list of 
specified member declarations from another type. 
`Partial` creates a new type that is exactly like the source type, only that all members are optional, 
as if they were declared with `?`.

All Config properties are declared in the class itself. 
"Simple" Config properties are just properties with an optional default value, 
while bindable Config properties must be specified as an accessor pair, 
typically encapsulating a private field. 
The additional Config type is then declared as an interface using the partial type of 
picking those Config properties from the class. 
By convention, we name this interface like the class, suffixed with Config.

```ecmascript 6
import Component from "@jangaroo/ext-ts/Component";

interface MyClassConfig extends Partial<Pick<MyClass, "configOption1" | "configOption2">> {}

class MyClass extends Component {
 /**
  * Simple Config property.
  */
  configOption1: string = "foo";

  #configOption2: number[] = [42];
  /**
  * Bindable Config property.
  */
  get configOption2(): number[] {
    return this.#configOption2;
  }
  set configOption2(value: number[]) {
    this.#configOption2 = value;
  }
    
  constructor(config: MyClassConfig) {
    super(config);
  }
}
```

To also export the additional interface, the most straight-forward option seemed like using a named export. 
But this has disadvantages when using both the class and its Config type, because you need two 
import identifiers, especially when there is a name clash, because you need to rename both. 
So we decided to assign the Config type to the class, which can be done in TypeScript by 
declaring a "virtual" class member named `Config`.

```ecmascript 6
interface MyClassConfig ...

class MyClass ... {
  declare Config: MyClassConfig;
  ...
}
```

This allows to access the Config type by importing the class and then use the
utility type also called `Config` (`import from @jangaroo/runtime/AS3/Config`). 
As this pattern is followed by all classes using the Ext Config System, 
also the Ext framework components, we can complement the example by extending the superclass Config type:

```ecmascript 6
import Config from "@jangaroo/runtime/AS3/Config";
import Component from "@jangaroo/ext-ts/Component";

interface MyClassConfig extends Config<Component>, Partial<Pick<MyClass, "configOption1" | "configOption2">> {}

...
```

# Specifying Strictly Typed Config Objects in TypeScript

Having a Config type allows to specify typed Config objects in TypeScript by using a 
type assertion (we use the <...>  syntax here to place the type in front), 
taking advantage of type checks and IDE support. The following example shows that 
type errors are detected for existing properties, however, arbitrary undeclared properties can be 
added without a type error:

```ecmascript 6
import Config from "@jangaroo/runtime/AS3/Config";
import MyClass from "./MyClass";

...
const myClassConfig = <Config<MyClass>>{
  id: "4711",  // inherited from Component._
  configOption1: "bar",  // MyClass Config property
  untyped: new Date(),  // an undeclared property does *not* lead to a type error!
  configOption2: "42"  // type error: '"42" is not assignable to type number[]'
};
...
```

Being able to use undeclared properties without warning is not desirable. 
Fortunately, in TypeScript, it is possible to specify the signature of a 
generic Config type-check function to prevent using untyped properties. 
You get access to this function through the same imported Config identifier 
(remember, TypeScript allows to declare a value and a type with the same identifier).

```ecmascript 6
import Config from "@jangaroo/runtime/AS3/Config";
import MyClass from "./MyClass";

...
const myClassConfig: Config<MyClass> = Config<MyClass>({ // first 'Config' is the utility type, second a function!
  id: "4711",  // inherited from Component._
  configOption1: "bar",  // MyClass Config property
  untyped: new Date(),  // an undeclared property now *does* lead to a type error!
  configOption2: "42"  // type error: '"42" is not assignable to type number[]'
});
...
```

We just added the type of myClassConfig  for clarity, you can leave that to TypeScript's type inference.

The first Config  (after the colon) is the utility type from above, but the second Config 
is a call to the generic Config type-check function, which takes as an argument a Config object of the 
corresponding Config type `MyClassConfig` and magically returns that Config object 
complemented by `xclass` / `alias` / `xtype` properties.

Since TypeScript is more strict when checking the type of a function argument than when a type assertion is used, 
this solution prevents accidental access to untyped properties. 
In the example, the property untyped would now be marked as an error, because it does not exist in the Config type.


# Creating Ext Config Objects in TypeScript

Now we have strictly typed Config objects, but they lack `xclass` / `alias` / `xtype` properties, 
which Ext uses to determine the target class when instantiating a Config 
object later (see "Creating Ext Config Objects in ActionScript"). 
Therefore, we need a counterpart of the special type cast semantics we introduced in ActionScript.

To this end, the generic Config function supports an overloaded signature which takes as first 
argument the target class which must define a Config type and as second (optional) argument 
a Config object of the corresponding Config type, and magically returns that 
Config object complemented by `xclass` / `alias` / `xtype` properties taken from the class.

With this new usage of the Config function, you can now create Ext Config objects like so:

```ecmascript 6
import Config from "@jangaroo/runtime/AS3/Config";
import MyClass from "./MyClass";

...
const myClassConfig: Config<MyClass> = Config(MyClass, { // use Config function with target class + config object
  id: "4711",  // inherited from Component._
  configOption1: "bar",  // MyClass Config property
  untyped: new Date(),  // an undeclared property now *does* lead to a type error!
  configOption2: "42"  // type error: '"42" is not assignable to type number[]'
});
...
```

As you can see, the syntax is very similar to using Config for a strict type-check. 
The crucial difference is that `MyClass` is not a type parameter 
(which is just a compiler hint and only relevant for type checking), 
but an argument of the function call. The class reference is needed at runtime to determine the `xclass` etc. 
and add it to the config object. Although this Config signature still has a type parameter, 
is should never be necessary to specify it explicitly, just leave it to the type inference.

If you use a class as first argument, but leave out the second one, the `Config` function 
returns an empty `Config` object with just the target class marker (`xclass`, `xtype`, ...). 
This comes in handy for simple components like `Config(Separator)`. 
TypeScript automatically distinguishes the two one-argument usages of `Config` by overloaded signatures, 
one with a `Config` object, the other with a class that declares a `Config` type.

As TypeScript can type object literals, it is no longer recommended to populate a `Config` object 
property-by-property (see section "Creating Ext Config Objects in ActionScript"). 
"Not recommended" means, this is of course still possible, and still results in strictly typed code. 
Note that the AS→TS conversion compiler does not (yet?) rewrite such code to use object literals.

In the rare case you need to instantiate the "real" object from a given Config object, you have different options:

```ecmascript 6
import { cast } from "@jangaroo/runtime";
import Ext from "@jangaroo/ext-ts";

const myClassInstance: MyClass = new MyClass(myClassConfig); // xclass of Config object is ignored
// OR
const myClassInstance: MyClass = Ext.create(MyClass, myClassConfig); // xclass of Config object is ignored
// OR
const myClassInstance: MyClass = Ext.create(myClassConfig); // must repeat target class, but incompatible class and Config type would be reported
// OR
const myClassInstance = Ext.create<MyClass>(myClassConfig)); // must repeat target class, but incompatible class and Config type would be reported
```

The first two usages are when you know which target class to create, anyway, 
so you would construct `myClassConfig` without any `xclass` , but just use the strict Config type function.

The latter two usages are when the Config object might have its own `xclass` of some `MyClass` subclass.
`Ext.create()` uses the `xclass` to instantiate the corresponding class, and the resulting object 
is type-compatible with `MyClass`. This is the kind of mechanism used by `Ext.Container` to instantiate its items.

But the best thing is that if you want to create an instance directly, 
you can now do so in a strongly typed fashion with full IDE support using an inline, 
ad-hoc Config object, which does not need any Config usage:

```ecmascript 6
const myClassInstance: MyClass = new MyClass({
  id: "4711",
  configOption1: "bar",
  configOption2: [42, 24]
});
```

In other words, the difference between creating a Config object and creating an instance 
is just using `Config(MyClass, ...)` versus using `new MyClass(...)`.


# Merging Config Objects

When receiving a Config object, the typical things a constructor does is:

- Apply the received config on its own Config defaults
- Hand through the resulting Config to its super constructor

In TypeScript code, this could be done like this:

```ecmascript 6
constructor(config: Config<MyClass>) {
  super(Object.assign(Config<MyClass>({
    id: "4711",
    configOption1: "bar",
    configOption2: [42, 24]
  }), config));
}
```

However, there is a special utility class named `ConfigUtils` (formerly in ActionScript: Exml) 
that helps implementing a specific merge logic. For array-valued properties, 
it should be possible to, instead of replacing the whole array, append or prepend to the existing value. 
The concrete use cases where this often makes sense are Ext component's plugins and items properties. 
So at least if your class has any array-valued properties, in your constructor, you should use

```ecmascript 6
import ConfigUtils from "@jangaroo/runtime/joo/ConfigUtils";
...
constructor(config: Config<MyClass>) {
  super(ConfigUtils.apply(Config<MyClass>({
  id: "4711",
  configOption1: "bar",
  configOption2: [42, 24]
  }), config));
}
```

Any client using such a component can then use

```ecmascript 6
import ConfigUtils from "@jangaroo/runtime/joo/ConfigUtils";
...
Config(MyClass, {
  id: "4711",
  configOption1: "bar",
  ...ConfigUtils.append({
  configOption2: [12]
  }
  }), config));
}
```


The resulting value of `configOption2` after merging via `ConfigUtils.apply()` will be `[42, 24, 12]`. 
There is an analog utility method `ConfigUtils.prepend()`. 
Both return an object, handing through the given property, complementing it by an internal marker property 
that specifies where to insert the value into the previous value. 
To "lift" these properties into surrounding object literal, the spread operator `...` is used.

# MXML → TypeScript

MXML is part of Flex and used as a more declarative alternative to specify ActionScript classes. 
Each MXML source file is internally translated to ActionScript.

This is also true for the conversion to TypeScript: MXML is first internally converted to ActionScript and 
then to TypeScript. However, there are some specialties that only apply in this mode, described in this chapter.

# How MXML Translates to ActionScript

MXML is, like the name suggests, an XML format. This is also the file extension used, 
so the following example would be located in `acme/MyMxmlClass.mxml`. Let's have a look at its basic structure:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!---
 Class documentation, corresponds to /** .. */.
-->
<acme:MyClass xmlns:fx="http://ns.adobe.com/mxml/2009"
xmlns:exml="http://www.jangaroo.net/exml/0.8"
xmlns="exml:ext.config"
xmlns:acme="acme.*"
configOption1="foo"
configOption2="{41 + 1}">
<fx:Script><![CDATA[
public static const xtype:String = "acme.myMxmlClass";

  private var config: MyMxmlClass;

  public native function MyMxmlClass(config: MyMxmlClass = null);
  ]]></fx:Script>

  <fx:Declarations>
  <!---
   Config option 3 documentation.
  -->
    <fx:String id="configOption3">default value for 3</fx:String>
  </fx:Declarations>
  <!-- complex Config property values are specified as nested elements: -->
  <acme:items>
    <!-- multiple elements result in an Array: -->
    <exml:Button id="myButton" label="Click me!"/>
    <exml:Separator/>
  </acme:items>         
  <acme:plugins exml:mode="append">
    <acme:MyPlugin .../>
  </acme:plugins>
</acme:MyClass>
```
This example defines an ActionScript class MyMxmlClass in MXML:

- Several namespaces are declared to access ActionScript packages ("acme.*") and so-called libraries (URL format). Libraries aggregate several classes from different packages and may define an alias for the class name (to prevent name-clashes).
- MyMxmlClass  inherits from MyClass - this is specified by using MyClass  as its root node.
- Config properties are given as either XML attributes (`configOption1`, `configOption2`) or as XML sub-elements ( <acme:items>, <acme:plugins>).
- Interpolations are used to compute property values. They use curly brackets `{ ... }` that can contain arbitrary ActionScript expressions.
- The `<fx:Script>` element contains additional class members as ActionScript code. "Real" Flex MXML may not define a custom constructor, but to declare an Ext JS specific constructor signature, receiving the config  argument, it is possible to declare a native  constructor. To be able to access this config parameter in interpolation expressions, it is re-declared as a "virtual" field.
- Since you cannot define static members in declarative MXML, the script block is also used to set the component's xtype.
- Additional class fields can be declared and/or initialized using the `<fx:Declarations>` element. The `id` attribute specifies the name of the field. The initial field value is given like any other MXML element, that is, the type or class to instantiate is determined through the element name, and the value or the constructor parameter's Config properties are given as XML attributes or sub-elements. If a field with the given name already exists (either inherited or defined in the `<fx:Script>` block), only this initial value is used and assigned to the existing field in generated constructor code.
- Finally, there is a Jangaroo addition to MXML, the property element attribute `exml:mode`. Together with utility functions in class `net.jangaroo.exml.Exml`, it takes care of the given array value being appended or prepended to the property's current value, rather than replacing it (see section "Merging Config Objects").

