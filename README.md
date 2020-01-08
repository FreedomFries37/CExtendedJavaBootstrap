
# CExtended (Jodin) Java Bootstrap

> The bootstrap for the Jodin language.

> Eventually, this entire app will be replaced with a compiler written in Jodin

### Table of Contents

1. [Basics](#Basics)
2. [Main Additions to C](#Main additions to C)
    1. [Classes](#Classes)
    2. [Namespaces](#Namespaces)
3. [Future Plans](#Future Plans)
    - [Jodin Compiler written in Jodin](#Jodin Compiler written in Jodin)
    - [Finely Tuned Importing](#Finely Tuned Importing)
4. [Code Examples](#Code Examples)
4. [Experimental Features](#Experimental Features)
    

### Basics

What is Jodin? Jodin is an Object-Oriented language, built off of the C-Language.
Theoretically, any program written in C should result in a 1-1[<sup>1</sup>](#Footnotes) mapping after compilation.
> In fact, using the C Backend, a program outputed by the Jodin Compiler can be sent back into the compiler, and shoud
result in the same file.

### Main Additions To C
These are currently 

#### Classes

Class are written using the follow syntax

```
class IDENTIFIER (: PARENT_IDENTIFIER)? {
    (public|private)? type field_name;
            or
    (public|private)? CLASSNAME(parameters?);
            or
    (public|private)? CLASSNAME(parameters?) (: (this|super)(arguments?))? {
        /* snip */
    }
            or
    (virtual)? (public|private)? returntype methodName(parameters?);
            or
    (virtual)? (public|private)? returntype methodName(parameters?) {
        /* snip */
    }
    .
    .
    .
};
```

Once implement blocks are fully working, you can declare functions in the class header,
then define them using the following syntax:

```cpp
implement CLASSNAME returntype methodName(parameters?) {
    /* snip */
}
implement CLASSNAME CLASSNAME(parameters?) (: (this|super)(arguments?))?  {
    /* snip */
}
```
or you can do multiple implementations with
```cpp
implement CLASSNAME {
    returntype methodName(parameters?) {
        /* snip */
    }
    
    CLASSNAME(parameters?) (: (this|super)(arguments?))?  {
        /* snip */
    }
    .
    .
    .
}
```

> Constructors call prior constructors using the `: (this|super)(arguments?)` syntax. Although its not necessary to actually
>use prior constructors[<sup>2</sup>](#Footnotes)

> Constructors return pointers to the heap.

#### Namespaces

In order to facilitate cleaner and clearer programs, classes can be declared within namespaces using
the `in IDENTIFER` command

The `in` command can either be followed by a class or by a block containing multiple classes and functions.
Functions names are not affected by it's namespace, currently.

Classes within a namespace can be referred to outside of it's namespace by using `namspace::classname`.

Namespaces can also be nested, for example:
```
in animals {
    in mammals {

        class Dolphin {

            /* snip */

        }
    }
}
```
In this example, outside of the namespaces, you would refer to the Dolphin class as `animals::mammals::Dolphin`.
If you are in a function that's declared in a `in animals` block, it could be referred to using just `mammals::Dolphin`
Namespace paths are relative to the current namespace, however all any namespace path also checks for an absolute instance,
so you could still refer to it using `animals::mammals::Dolphin` within the `in animals` block.

>Classes can't be have two different definitions within the same namespace, but in the case that a namespace path determines
>that there could be multiple classes being referred to by a path, the compiler will spit out an error, like so:
>In namespace.cx:
>```
>radin.core.semantics.types.AmbiguousIdentifierError: Ambiguous Identifier
>    |
> 18 |     ko::object* o; // Should be an ambiguous reference
>    |            ^_________________________________________________ Could be CXClass ko::object or CXClass std::ko::object
>```

Code Examples
---

##### Inheritance Example, along with namespaces
```
void print(char* name);
void println(char* name);

in animals {
	class animal {
		private char* species;
		private int numberOfLegs;

		public animal(char* species, int numberOfLegs) {
			this->species = species;
			this->numberOfLegs = numberOfLegs;
		}

		public int getNumberOfLegs() {
			return this->numberOfLegs;
		}

		virtual public void says() {
			print(this->species);
			print(" says ");
		}
	};

	class quadAnimal : animal {

		public quadAnimal(char* name) : super(name, 4) {}

		virtual public void says() {
			super->says();
			print("I have 4 legs!");
        }
	};

	class domesticated : quadAnimal {
		private char* name;

		public domesticated(char* name, char* species) : super(species) {
			this->name = name;
		}

		public char* getName() {
			return this->name;
		}

		virtual public void says() {
			print(this->getName());
			print(" the ");
			super->says();
			print(", also ARF");
		}
	};

	class dog : domesticated {

		public dog(char* name) : super(name, "dog") { }

		public dog() : this("unknown") { }


	};
}

class cat : animals::domesticated {

	public cat(char* name) : super(name, "cat") {}

	virtual public void says() {
		println("I'm a cat, shove off");
	}
};


int main() {

	animals::animal griff = new animals::dog("The Griff");

	griff->says();
	println("");
	griff->says();
	println("");


	animals::domesticated myCat = new cat("jeff");
	myCat->says();

	return 0;
}
```
Upon compiling this code into C, then compiling that into an executable, this is what is outputted:
```
The Griff the dog says I have 4 legs!, also ARF
The Griff the dog says I have 4 legs!, also ARF
I'm a cat, shove off
```

Future Plans
---
There are listed in no particular order.

#### Jodin Compiler written in Jodin
This is the next big step for Jodin, and will signify that it's hit a "mature" state. Once a compiler is
successfuly written in Jodin that is either equal or a superset of the Java bootstrap, this will be
the "beta" point of Jodin.

##### Features needed for a self-hosted compiler
These are the features that I have determined are necessary for work on the self-hosted compiler to begin.

| Feature           | Implementation Status |
|:------------------|----------------------:|
| Generics          | None                  |
| Compilation Tags  | None                  |
| Preamble          | Bare minimum          |
| Object base class | None                  |
| String class      | None                  |
| Exception Handling| None                  |
| Interfaces        | None                  |
| Implement Block   | AST                   |
| Runtime Class Info| None                  |

##### Preamable
The preamble AST will be attached to the top of every file being compiled.

The preamble will contain import important information, but most importantly will
bring the `std::Object` base class into the file with `using std::Object as Object`, and set the
default inherit to `std::Object`.

The definition of Object will be as followed
```

in std class ClassInfo;

in std class Object { // default inheritence is initially null
    private ClassInfo info;
    private long references; // if using garbage collection

    public Object() {
        this->info = __GET_CLASS_INFO__(classid Object);
    }

    virtual public int hashcode() {
        return (int) this;
    }

    virtual public int equals(Object other) {
        return this == other;
    }

    [unsafe] // compilation tag that prevents typechecking
    virtual public void drop() {
        free (this->vtable);
        free (this);
    }
};
```


#### Finely Tuned Importing
Using the statement as a top level declaration `using namespace::identifer;` will automatically bring the declarations
and fields of a class into the file. If the identifier has not been found yet, the file being compiled will be put
on hold until the declaration is found.
>In the case of a circular dependency, the compiler will attempt to be break the circle by checking if the class is ever
>actually interacted with. If it isn't, its replaced with `in namespace class identifier;`. Otherwise it reports as an
>error

> If the namespace is `std`, then it will find the appropriate standard file in the standard library to include in the
>output.


Experimental Features
---

#### Stack trace
Section coming son

#### Reference Counting Garbage collectoin

---
### Footnotes
1. There will be some variation in the code, ie: all operations are put into parentheses, as
the actual parentheses are currently lost in parsing
2. This may change at a later time to require calling prior constructors if the super class doesn't contain any
`(void)` constructors

