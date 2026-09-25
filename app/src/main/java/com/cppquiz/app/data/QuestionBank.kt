package com.cppquiz.app.data

/**
 * Repository class containing all quiz questions
 * Think of this as your data source (like a database or API in web dev)
 *
 * Note: the `languageVersion` field is reused here to hold the C++ standard
 * ("Core", "11", "14", "17", "20", "23").
 */
object QuestionBank {

    fun getAllQuestions(): List<Question> {
        return listOf(
            // ===================== Core Concepts =====================
            Question(
                id = 1,
                questionText = "What is C++?",
                options = listOf(
                    "A purely functional programming language",
                    "A general-purpose, compiled language that extends C with object-oriented and generic programming features",
                    "A scripting language that runs inside a browser",
                    "A markup language for describing documents"
                ),
                correctAnswerIndex = 1,
                explanation = "C++ is a general-purpose programming language created as an extension of C, adding object-oriented, generic, and (later) functional features while keeping low-level memory control and high performance.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 2,
                questionText = "What is the main difference between C and C++?",
                options = listOf(
                    "C++ is interpreted while C is compiled",
                    "C++ adds object-oriented programming, templates, and the STL on top of C's procedural model",
                    "C and C++ are the same language with different file extensions",
                    "C++ cannot call C libraries"
                ),
                correctAnswerIndex = 1,
                explanation = "C++ was designed as 'C with Classes'. It keeps C's low-level capabilities but adds classes, inheritance, templates, exceptions, and the Standard Template Library.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 3,
                questionText = "What is a pointer in C++?",
                options = listOf(
                    "A variable that stores a fixed value",
                    "A variable that stores the memory address of another variable",
                    "A type of array",
                    "A function that returns void"
                ),
                correctAnswerIndex = 1,
                explanation = "A pointer holds the memory address of another variable. It is declared using the * symbol, e.g. int* ptr, and can be dereferenced with * to access the value it points to.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 4,
                questionText = "What is a reference in C++?",
                options = listOf(
                    "A pointer that can be reassigned freely",
                    "An alias for an existing variable that must be initialized when declared and cannot be null",
                    "A copy of a variable's value",
                    "A keyword used only in templates"
                ),
                correctAnswerIndex = 1,
                explanation = "A reference (declared with &) is an alias for an existing object. Unlike a pointer, it must be bound to a valid object at declaration and cannot later refer to a different object or be null.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 5,
                questionText = "What is a key difference between a pointer and a reference?",
                options = listOf(
                    "References can be reseated to point elsewhere; pointers cannot",
                    "Pointers can be null and reassigned; references must be initialized and always refer to the same object",
                    "There is no difference, they are interchangeable in every context",
                    "References use more memory than pointers"
                ),
                correctAnswerIndex = 1,
                explanation = "Pointers can be null, reassigned, and support pointer arithmetic. References must be bound at initialization, cannot be null, and cannot be made to refer to a different object afterward.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 6,
                questionText = "What does the const keyword do in C++?",
                options = listOf(
                    "It marks a variable as thread-local",
                    "It indicates that a variable, parameter, or method promises not to modify the value it applies to",
                    "It makes a variable global",
                    "It allocates memory on the heap"
                ),
                correctAnswerIndex = 1,
                explanation = "const declares that a value cannot be modified after initialization. Applied to a member function (e.g. void foo() const), it promises the method will not modify the object's state.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 7,
                questionText = "What does the static keyword mean when applied to a class member in C++?",
                options = listOf(
                    "The member can only be used once",
                    "The member belongs to the class itself rather than to any single instance, and is shared across all instances",
                    "The member is automatically const",
                    "The member is stored on the stack instead of the heap"
                ),
                correctAnswerIndex = 1,
                explanation = "A static member belongs to the class rather than to individual objects. There is exactly one copy shared by all instances, and it can be accessed without creating an object.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 8,
                questionText = "What is the purpose of a header file (.h / .hpp) in C++?",
                options = listOf(
                    "It contains compiled machine code",
                    "It declares interfaces (classes, functions, constants) so multiple source files can share them via #include",
                    "It stores runtime configuration",
                    "It replaces the need for a compiler"
                ),
                correctAnswerIndex = 1,
                explanation = "Header files contain declarations (function prototypes, class definitions, constants) that are shared between translation units using #include, separating interface from implementation.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 9,
                questionText = "What are the main stages of building a C++ program?",
                options = listOf(
                    "Writing and running only",
                    "Preprocessing, compilation, assembly, and linking",
                    "Interpretation and garbage collection",
                    "Bytecode generation and JIT compilation"
                ),
                correctAnswerIndex = 1,
                explanation = "A C++ build pipeline preprocesses source (handling #include/#define), compiles it to assembly, assembles it to object code, then links object files and libraries into an executable.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 10,
                questionText = "What is the standard signature of the main function in C++?",
                options = listOf(
                    "void main()",
                    "int main() or int main(int argc, char* argv[])",
                    "public static void main(String[] args)",
                    "def main():"
                ),
                correctAnswerIndex = 1,
                explanation = "The C++ standard requires main to return an int, either taking no parameters or taking argc/argv for command-line arguments. Returning 0 conventionally signals success.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 11,
                questionText = "What is the difference between struct and class in C++?",
                options = listOf(
                    "struct cannot have member functions",
                    "The only difference is the default access level: public for struct, private for class",
                    "class cannot be used with templates",
                    "struct is only for C compatibility and has no other use"
                ),
                correctAnswerIndex = 1,
                explanation = "In C++, struct and class are almost identical; the only difference is default member and inheritance access, which is public for struct and private for class.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 12,
                questionText = "What is function overloading in C++?",
                options = listOf(
                    "Calling a function too many times",
                    "Defining multiple functions with the same name but different parameter lists",
                    "Overriding a virtual function in a derived class",
                    "Using too many arguments in a single function"
                ),
                correctAnswerIndex = 1,
                explanation = "Function overloading allows multiple functions to share a name as long as their parameter lists differ in number or type, letting the compiler pick the right one at compile time.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 13,
                questionText = "What is a namespace in C++?",
                options = listOf(
                    "A block of memory reserved at runtime",
                    "A declarative region that provides a scope to group identifiers and avoid naming collisions",
                    "A type of template",
                    "A synonym for a header file"
                ),
                correctAnswerIndex = 1,
                explanation = "A namespace (e.g. namespace std { ... }) groups related identifiers under a named scope, preventing name clashes between libraries or modules.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 14,
                questionText = "What are default parameters in C++?",
                options = listOf(
                    "Parameters that are always ignored",
                    "Function parameters that are given a default value used when the caller omits an argument",
                    "Parameters that must be pointers",
                    "Parameters automatically generated by the compiler for every function"
                ),
                correctAnswerIndex = 1,
                explanation = "A default parameter (e.g. void greet(std::string name = \"World\")) supplies a value used automatically when the caller does not provide one, reducing the need for overloads.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 15,
                questionText = "What is the this pointer in C++?",
                options = listOf(
                    "A pointer to the parent class",
                    "An implicit pointer available in non-static member functions that points to the object the method was called on",
                    "A pointer to the first element of an array",
                    "A global pointer to the main function"
                ),
                correctAnswerIndex = 1,
                explanation = "Inside a non-static member function, this is an implicit pointer to the calling object, used to access its members or to distinguish them from same-named parameters.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),

            // ===================== OOP =====================
            Question(
                id = 16,
                questionText = "What is Object-Oriented Programming in the context of C++?",
                options = listOf(
                    "Programming that only uses objects, never functions",
                    "A paradigm organizing code into classes and objects that bundle data with the operations on that data",
                    "A style unique to Java that C++ merely imitates",
                    "A programming style with no data types"
                ),
                correctAnswerIndex = 1,
                explanation = "C++ supports OOP by letting you define classes that bundle data (member variables) with behavior (member functions), enabling encapsulation, inheritance, and polymorphism.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 17,
                questionText = "What are the four main pillars of OOP in C++?",
                options = listOf(
                    "Compilation, Linking, Loading, Execution",
                    "Encapsulation, Abstraction, Inheritance, Polymorphism",
                    "Public, Private, Protected, Static",
                    "Stack, Heap, Global, Local"
                ),
                correctAnswerIndex = 1,
                explanation = "Encapsulation hides internal state, abstraction exposes only essential details, inheritance lets classes derive from others, and polymorphism lets one interface work with multiple types.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 18,
                questionText = "What is inheritance in C++?",
                options = listOf(
                    "Copying a class's code manually into another file",
                    "A mechanism where a derived class acquires members and behavior from a base class",
                    "A way to import a library",
                    "A feature only available for structs, not classes"
                ),
                correctAnswerIndex = 1,
                explanation = "Inheritance lets a derived class reuse and extend the members of a base class, expressed with class Derived : public Base { ... } for public inheritance.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 19,
                questionText = "What is polymorphism in C++?",
                options = listOf(
                    "Having several variables with the same name",
                    "The ability to call the correct derived-class implementation of a function through a base-class pointer or reference",
                    "Defining a class with multiple constructors",
                    "Using multiple namespaces in one file"
                ),
                correctAnswerIndex = 1,
                explanation = "Runtime polymorphism in C++ lets code that holds a base-class pointer or reference invoke the actual derived-class implementation of a virtual function, chosen at runtime.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 20,
                questionText = "What is a virtual function in C++?",
                options = listOf(
                    "A function that exists only during debugging",
                    "A member function declared in a base class that can be overridden in derived classes and is resolved at runtime via dynamic dispatch",
                    "A function that cannot be called directly",
                    "A function that runs on a separate thread"
                ),
                correctAnswerIndex = 1,
                explanation = "Declaring a base-class method virtual enables dynamic dispatch: calling it through a base pointer/reference invokes the most-derived override, using the object's vtable at runtime.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 21,
                questionText = "What is a pure virtual function and how does it create an abstract class?",
                options = listOf(
                    "A virtual function with an empty body that behaves like any normal function",
                    "A virtual function declared with = 0, which has no implementation in that class and forces any concrete derived class to override it, making the class abstract",
                    "A function that can only be called from main()",
                    "A static function that cannot be inherited"
                ),
                correctAnswerIndex = 1,
                explanation = "Declaring virtual void foo() = 0; makes foo a pure virtual function. A class with at least one pure virtual function becomes abstract and cannot be instantiated directly.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 22,
                questionText = "What is operator overloading in C++?",
                options = listOf(
                    "Using too many operators in one expression",
                    "Defining custom behavior for operators (like +, ==, <<) when applied to user-defined types",
                    "A compiler error caused by ambiguous operators",
                    "Overloading only arithmetic operators is allowed"
                ),
                correctAnswerIndex = 1,
                explanation = "C++ allows you to redefine what operators like +, ==, or << do for your own classes, e.g. operator+(const T& other), so objects can be used naturally with familiar syntax.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 23,
                questionText = "What is the diamond problem in C++?",
                options = listOf(
                    "A performance issue related to nested loops",
                    "An ambiguity that arises with multiple inheritance when a class inherits from two classes that share a common base, causing duplicate base subobjects",
                    "A syntax error when using templates",
                    "A memory alignment issue with structs"
                ),
                correctAnswerIndex = 1,
                explanation = "If classes B and C both inherit from A, and D inherits from both B and C, D ends up with two copies of A's members unless A is inherited virtually (virtual inheritance).",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 24,
                questionText = "What is encapsulation in C++?",
                options = listOf(
                    "Combining every class into a single file",
                    "Bundling data and the methods that operate on it together, restricting direct access to internal state via access specifiers like private",
                    "Compiling code into a single binary",
                    "Wrapping a function in a try/catch block"
                ),
                correctAnswerIndex = 1,
                explanation = "Encapsulation groups data and behavior inside a class and controls access using public, protected, and private specifiers, protecting internal state from unintended external modification.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),
            Question(
                id = 25,
                questionText = "What are constructors and destructors in C++?",
                options = listOf(
                    "Functions that must be called manually to create and delete objects",
                    "Special member functions: a constructor initializes an object when it is created, a destructor (~ClassName) cleans up when it is destroyed",
                    "Keywords used only with pointers",
                    "Functions that only exist for structs"
                ),
                correctAnswerIndex = 1,
                explanation = "A constructor (same name as the class) runs automatically when an object is created to initialize it; a destructor (prefixed with ~) runs automatically when the object goes out of scope or is deleted, freeing resources.",
                languageVersion = "Core",
                category = "Core Concepts"
            ),

            // ===================== Memory Management (Advanced) =====================
            Question(
                id = 26,
                questionText = "What is RAII in C++?",
                options = listOf(
                    "A naming convention for private members",
                    "Resource Acquisition Is Initialization: tying a resource's lifetime to an object's lifetime so it is automatically released when the object is destroyed",
                    "A type of runtime exception",
                    "A keyword for declaring arrays"
                ),
                correctAnswerIndex = 1,
                explanation = "RAII acquires a resource (memory, file handle, lock) in a constructor and releases it in the destructor, so resources are automatically freed when the owning object goes out of scope, even during exceptions.",
                languageVersion = "Core",
                category = "Advanced"
            ),
            Question(
                id = 27,
                questionText = "What is the difference between new/delete and malloc/free in C++?",
                options = listOf(
                    "They are identical and interchangeable in every case",
                    "new/delete call constructors and destructors and are type-safe; malloc/free only allocate raw memory without initialization",
                    "malloc is faster in all cases so it should always be preferred",
                    "delete cannot free memory allocated by new"
                ),
                correctAnswerIndex = 1,
                explanation = "new invokes the type's constructor after allocating memory and delete invokes the destructor before freeing it. malloc/free (inherited from C) only manage raw memory and know nothing about object construction.",
                languageVersion = "Core",
                category = "Advanced"
            ),
            Question(
                id = 28,
                questionText = "What is a memory leak in C++?",
                options = listOf(
                    "When a program uses too much CPU",
                    "When dynamically allocated memory is never freed, so it remains unreachable but still reserved for the program's lifetime",
                    "When a variable is declared but never used",
                    "When a file is opened but never read"
                ),
                correctAnswerIndex = 1,
                explanation = "A memory leak occurs when memory allocated with new (or malloc) is never released with delete (or free), and no pointer to it remains, so it cannot be reclaimed until the process exits.",
                languageVersion = "Core",
                category = "Advanced"
            ),
            Question(
                id = 29,
                questionText = "What is a dangling pointer?",
                options = listOf(
                    "A pointer that has never been initialized",
                    "A pointer that still refers to memory that has already been freed or gone out of scope",
                    "A pointer that points to a constant",
                    "A pointer used inside a template"
                ),
                correctAnswerIndex = 1,
                explanation = "A dangling pointer points to memory that has been deallocated (e.g. after delete) or to a local variable that has gone out of scope. Dereferencing it causes undefined behavior.",
                languageVersion = "Core",
                category = "Advanced"
            ),
            Question(
                id = 30,
                questionText = "What is std::unique_ptr (C++11)?",
                options = listOf(
                    "A pointer that can be shared by multiple owners simultaneously",
                    "A smart pointer that exclusively owns a dynamically allocated object and automatically deletes it when it goes out of scope",
                    "A raw pointer wrapper with no automatic cleanup",
                    "A pointer only usable inside templates"
                ),
                correctAnswerIndex = 1,
                explanation = "std::unique_ptr provides exclusive ownership of a heap object; it cannot be copied (only moved) and automatically calls delete on the owned object when the unique_ptr is destroyed.",
                languageVersion = "11",
                category = "Advanced"
            ),
            Question(
                id = 31,
                questionText = "What is std::shared_ptr (C++11)?",
                options = listOf(
                    "A pointer that must be manually reference-counted by the programmer",
                    "A smart pointer that allows multiple owners of the same object via reference counting, deleting it once the last owner is destroyed",
                    "A pointer that can never be null",
                    "A pointer that only works with arrays"
                ),
                correctAnswerIndex = 1,
                explanation = "std::shared_ptr uses an internal reference count so multiple shared_ptr instances can co-own the same object; the object is deleted automatically once the last shared_ptr referencing it is destroyed.",
                languageVersion = "11",
                category = "Advanced"
            ),
            Question(
                id = 32,
                questionText = "What is the Rule of Three (and Rule of Five) in C++?",
                options = listOf(
                    "A rule limiting classes to three member functions",
                    "If a class manages a resource, it should define its own copy constructor, copy assignment operator, and destructor (and, since C++11, also the move constructor and move assignment operator)",
                    "A rule about how many times a loop can run",
                    "A naming convention for template parameters"
                ),
                correctAnswerIndex = 1,
                explanation = "If you must define one of the destructor, copy constructor, or copy assignment operator because a class owns a resource, you generally need all three (Rule of Three); C++11 extends this to five by adding the move constructor and move assignment operator.",
                languageVersion = "Core",
                category = "Advanced"
            ),

            // ===================== STL / Collections =====================
            Question(
                id = 33,
                questionText = "What is the STL in C++?",
                options = listOf(
                    "A build tool for compiling C++ programs",
                    "The Standard Template Library: a collection of generic containers, iterators, and algorithms",
                    "A GUI framework bundled with the compiler",
                    "A networking library"
                ),
                correctAnswerIndex = 1,
                explanation = "The Standard Template Library (STL) provides generic, reusable containers (vector, map, set...), iterators to traverse them, and algorithms (sort, find, accumulate...) that work across container types.",
                languageVersion = "Core",
                category = "Collections"
            ),
            Question(
                id = 34,
                questionText = "What is the difference between std::vector and std::array?",
                options = listOf(
                    "They are exactly the same",
                    "std::vector is a dynamically resizable array on the heap; std::array is a fixed-size array whose size is known at compile time",
                    "std::array can grow at runtime while std::vector cannot",
                    "std::vector cannot store primitive types"
                ),
                correctAnswerIndex = 1,
                explanation = "std::vector manages a dynamically resizable, heap-allocated array that can grow or shrink at runtime. std::array (C++11) is a fixed-size, stack-allocated array whose size is a compile-time constant.",
                languageVersion = "Core",
                category = "Collections"
            ),
            Question(
                id = 35,
                questionText = "What is the difference between std::vector and std::list?",
                options = listOf(
                    "They provide identical performance for every operation",
                    "std::vector stores elements contiguously (fast random access, slower middle insert/erase); std::list is a doubly-linked list (fast insert/erase anywhere, no random access)",
                    "std::list cannot store custom objects",
                    "std::vector is always slower than std::list"
                ),
                correctAnswerIndex = 1,
                explanation = "std::vector keeps elements in contiguous memory, giving O(1) random access but O(n) insertion/removal in the middle. std::list is a doubly-linked list with O(1) insertion/removal anywhere but only sequential access.",
                languageVersion = "Core",
                category = "Collections"
            ),
            Question(
                id = 36,
                questionText = "What is std::map in C++?",
                options = listOf(
                    "An unordered hash table with no sorting guarantees",
                    "An associative container that stores key-value pairs sorted by key, typically implemented as a balanced binary search tree",
                    "A container that can only store strings",
                    "A synonym for std::vector"
                ),
                correctAnswerIndex = 1,
                explanation = "std::map stores unique keys mapped to values, keeping them sorted by key (usually via a red-black tree), giving O(log n) lookup, insertion, and removal.",
                languageVersion = "Core",
                category = "Collections"
            ),
            Question(
                id = 37,
                questionText = "What is an iterator in C++?",
                options = listOf(
                    "A function that repeats a loop a fixed number of times",
                    "An object that behaves like a pointer, used to traverse elements of a container without exposing its internal representation",
                    "A keyword for declaring a for loop",
                    "A container that stores only integers"
                ),
                correctAnswerIndex = 1,
                explanation = "Iterators provide a uniform, pointer-like interface (operator++, operator*, etc.) for traversing containers, letting STL algorithms work with vectors, lists, maps, and more without knowing their internals.",
                languageVersion = "Core",
                category = "Collections"
            ),
            Question(
                id = 38,
                questionText = "What does std::sort do, and where does it live?",
                options = listOf(
                    "It is a member function of every container defined in <vector>",
                    "It is a generic algorithm in <algorithm> that sorts a range given by two iterators, typically using an introsort-based approach",
                    "It only works on arrays of integers",
                    "It permanently modifies the type of the container"
                ),
                correctAnswerIndex = 1,
                explanation = "std::sort, declared in <algorithm>, sorts elements in the range [first, last) given by iterators, and works with any container offering random-access iterators, such as std::vector.",
                languageVersion = "Core",
                category = "Collections"
            ),
            Question(
                id = 39,
                questionText = "What is the difference between std::map and std::unordered_map?",
                options = listOf(
                    "There is no meaningful difference",
                    "std::map keeps keys sorted using a tree (O(log n) operations); std::unordered_map uses a hash table for average O(1) operations but no ordering",
                    "std::unordered_map can only store one element",
                    "std::map cannot use custom key types"
                ),
                correctAnswerIndex = 1,
                explanation = "std::map maintains keys in sorted order via a balanced tree, giving O(log n) operations. std::unordered_map (C++11) uses a hash table for average O(1) operations but does not maintain any particular order.",
                languageVersion = "11",
                category = "Collections"
            ),
            Question(
                id = 40,
                questionText = "What is std::pair (and std::tuple) used for?",
                options = listOf(
                    "Only for storing two numbers",
                    "std::pair bundles exactly two values of possibly different types; std::tuple generalizes this to any fixed number of values",
                    "They are containers that can grow dynamically like std::vector",
                    "They replace the need for structs entirely"
                ),
                correctAnswerIndex = 1,
                explanation = "std::pair<T1, T2> groups two heterogeneous values (e.g. map entries are std::pair<Key, Value>). std::tuple extends this idea to an arbitrary fixed number of heterogeneous values.",
                languageVersion = "Core",
                category = "Collections"
            ),

            // ===================== C++11 =====================
            Question(
                id = 41,
                questionText = "What does the auto keyword do in C++11?",
                options = listOf(
                    "It marks a variable for automatic garbage collection",
                    "It tells the compiler to deduce a variable's type automatically from its initializer",
                    "It makes a variable thread-local",
                    "It is only usable for function return types before C++14"
                ),
                correctAnswerIndex = 1,
                explanation = "auto instructs the compiler to infer the variable's type from its initializer at compile time, e.g. auto x = 5; deduces x as int. It does not mean dynamic typing.",
                languageVersion = "11",
                category = "Language Features"
            ),
            Question(
                id = 42,
                questionText = "What is a range-based for loop, introduced in C++11?",
                options = listOf(
                    "A loop that can only iterate over arrays of fixed size",
                    "A concise for-loop syntax that iterates directly over the elements of a container or array without explicit indices or iterators",
                    "A loop that automatically parallelizes across threads",
                    "A loop that replaces while loops entirely"
                ),
                correctAnswerIndex = 1,
                explanation = "Range-based for loops, written as for (auto& elem : container), iterate over every element of a range (container, array, initializer list) without manually managing indices or iterators.",
                languageVersion = "11",
                category = "Language Features"
            ),
            Question(
                id = 43,
                questionText = "What is a lambda expression in C++11?",
                options = listOf(
                    "A macro that expands at compile time",
                    "An anonymous, inline function object that can capture variables from its enclosing scope",
                    "A special kind of class template",
                    "A function that can only be called once"
                ),
                correctAnswerIndex = 1,
                explanation = "A lambda, e.g. [x](int y) { return x + y; }, defines an unnamed function object inline. The capture list [x] specifies which enclosing variables it can use, by value or reference.",
                languageVersion = "11",
                category = "Language Features"
            ),
            Question(
                id = 44,
                questionText = "What is nullptr, introduced in C++11?",
                options = listOf(
                    "A macro equal to the integer 0, identical to the old NULL",
                    "A type-safe keyword representing a null pointer value, distinct from integer 0, that avoids overload-resolution ambiguity",
                    "A pointer that always points to the first element of an array",
                    "A reserved variable name that cannot be reassigned"
                ),
                correctAnswerIndex = 1,
                explanation = "nullptr has its own type (std::nullptr_t) and unambiguously represents a null pointer, fixing issues where the old NULL macro (often defined as 0) could be confused with an integer in overload resolution.",
                languageVersion = "11",
                category = "Language Features"
            ),
            Question(
                id = 45,
                questionText = "What are move semantics and rvalue references (C++11)?",
                options = listOf(
                    "A way to physically relocate objects in memory automatically",
                    "A mechanism (using && rvalue references) that lets resources be transferred from a temporary or expiring object instead of deep-copied, improving performance",
                    "A restriction preventing objects from being copied at all",
                    "A feature exclusive to primitive types like int and double"
                ),
                correctAnswerIndex = 1,
                explanation = "Rvalue references (T&&) let code detect temporary (movable) objects and 'steal' their internal resources via a move constructor/assignment instead of performing an expensive deep copy.",
                languageVersion = "11",
                category = "Language Features"
            ),
            Question(
                id = 46,
                questionText = "What are variadic templates in C++11?",
                options = listOf(
                    "Templates that can only accept exactly two type parameters",
                    "Templates that accept an arbitrary number of template arguments of possibly different types",
                    "A way to define templates without any parameters",
                    "Templates restricted to numeric types only"
                ),
                correctAnswerIndex = 1,
                explanation = "Variadic templates, using a parameter pack (typename... Args), let a template function or class accept any number of arguments of varying types, enabling things like a type-safe printf replacement.",
                languageVersion = "11",
                category = "Language Features"
            ),
            Question(
                id = 47,
                questionText = "What is an enum class (scoped enumeration) in C++11?",
                options = listOf(
                    "An enum whose values can implicitly convert to int and pollute the surrounding scope",
                    "A strongly-typed enumeration whose enumerators are scoped to the enum's name and do not implicitly convert to int",
                    "A class that behaves exactly like a plain enum",
                    "An enum that can only hold string values"
                ),
                correctAnswerIndex = 1,
                explanation = "enum class Color { Red, Green }; requires Color::Red to access enumerators, and disallows implicit conversion to int, avoiding the naming collisions and unsafe conversions of plain C-style enums.",
                languageVersion = "11",
                category = "Language Features"
            ),

            // ===================== C++14 =====================
            Question(
                id = 48,
                questionText = "What are generic lambdas, introduced in C++14?",
                options = listOf(
                    "Lambdas that can never capture variables",
                    "Lambdas whose parameters can be declared auto, letting a single lambda work with multiple argument types like a template",
                    "Lambdas that must specify every type explicitly",
                    "A feature that lets lambdas be recursive by default"
                ),
                correctAnswerIndex = 1,
                explanation = "C++14 allows lambda parameters to be declared auto, e.g. [](auto a, auto b) { return a + b; }, making the lambda's operator() effectively a template that works with any compatible types.",
                languageVersion = "14",
                category = "Language Features"
            ),
            Question(
                id = 49,
                questionText = "What does std::make_unique do, added in C++14?",
                options = listOf(
                    "It creates a std::shared_ptr",
                    "It safely constructs an object and wraps it in a std::unique_ptr in a single, exception-safe expression",
                    "It converts a raw pointer into a reference",
                    "It duplicates an existing unique_ptr"
                ),
                correctAnswerIndex = 1,
                explanation = "std::make_unique<T>(args...) allocates and constructs a T and returns it owned by a std::unique_ptr, avoiding manual new calls and certain exception-safety pitfalls. (std::make_shared existed since C++11.)",
                languageVersion = "14",
                category = "Language Features"
            ),
            Question(
                id = 50,
                questionText = "What changed with constexpr functions in C++14?",
                options = listOf(
                    "constexpr functions were removed entirely",
                    "Restrictions were relaxed so constexpr functions could contain loops, local variables, and multiple statements, not just a single return expression",
                    "constexpr became the default for every function",
                    "constexpr functions can now throw exceptions at compile time"
                ),
                correctAnswerIndex = 1,
                explanation = "C++11 constexpr functions were limited to essentially a single return statement. C++14 relaxed this, allowing loops, conditionals, and multiple local variables inside constexpr functions.",
                languageVersion = "14",
                category = "Language Features"
            ),

            // ===================== C++17 =====================
            Question(
                id = 51,
                questionText = "What are structured bindings, introduced in C++17?",
                options = listOf(
                    "A way to bind a function to a specific thread",
                    "Syntax that lets you unpack multiple values from a pair, tuple, struct, or array into individually named variables in one declaration",
                    "A feature restricted to arrays of exactly three elements",
                    "A replacement for the auto keyword"
                ),
                correctAnswerIndex = 1,
                explanation = "Structured bindings, e.g. auto [key, value] = *mapIterator;, let you destructure a pair, tuple, struct, or array into named variables in a single statement.",
                languageVersion = "17",
                category = "Language Features"
            ),
            Question(
                id = 52,
                questionText = "What is std::optional, introduced in C++17?",
                options = listOf(
                    "A container that can hold an unlimited number of values",
                    "A wrapper type that may or may not contain a value, providing a type-safe alternative to using a sentinel value or a pointer to represent 'no value'",
                    "A replacement for exceptions",
                    "A pointer that is optional to dereference"
                ),
                correctAnswerIndex = 1,
                explanation = "std::optional<T> represents a value that may or may not be present, avoiding the need for sentinel values (like -1) or nullable pointers to express 'no result' in a type-safe way.",
                languageVersion = "17",
                category = "Language Features"
            ),
            Question(
                id = 53,
                questionText = "What does if constexpr do in C++17?",
                options = listOf(
                    "It makes a normal if statement run faster at runtime",
                    "It evaluates a condition at compile time and discards the untaken branch entirely, commonly used in templates to select code per type",
                    "It replaces the switch statement",
                    "It forces both branches of an if to always execute"
                ),
                correctAnswerIndex = 1,
                explanation = "if constexpr (condition) evaluates the condition at compile time; the branch not taken is discarded from compilation entirely, which is especially useful for writing templates that behave differently per type without SFINAE tricks.",
                languageVersion = "17",
                category = "Language Features"
            ),
            Question(
                id = 54,
                questionText = "What is std::string_view, introduced in C++17?",
                options = listOf(
                    "A mutable, owning string type that replaces std::string",
                    "A lightweight, non-owning view over a contiguous sequence of characters, avoiding unnecessary string copies",
                    "A view that can only be created from string literals",
                    "A synchronization primitive for strings shared between threads"
                ),
                correctAnswerIndex = 1,
                explanation = "std::string_view holds a pointer and length referring to existing character data without owning or copying it, making functions that only need to read a string cheaper to call.",
                languageVersion = "17",
                category = "Language Features"
            ),
            Question(
                id = 55,
                questionText = "What is std::variant, introduced in C++17?",
                options = listOf(
                    "A container that stores multiple values of the same type",
                    "A type-safe tagged union that holds a value which can be one of several specified alternative types",
                    "A replacement for std::optional",
                    "A type used exclusively for error codes"
                ),
                correctAnswerIndex = 1,
                explanation = "std::variant<T1, T2, ...> can hold a value of exactly one of its listed alternative types at a time, giving a type-safe alternative to a raw C-style union.",
                languageVersion = "17",
                category = "Language Features"
            ),

            // ===================== C++20 =====================
            Question(
                id = 56,
                questionText = "What are concepts, introduced in C++20?",
                options = listOf(
                    "A runtime type-checking mechanism",
                    "Named compile-time predicates that constrain what types a template can accept, producing clearer errors than raw SFINAE",
                    "A new kind of comment syntax",
                    "A replacement for the auto keyword everywhere"
                ),
                correctAnswerIndex = 1,
                explanation = "Concepts let you express requirements on template parameters directly, e.g. template<std::integral T>, giving readable compile-time constraints and far clearer error messages than traditional SFINAE-based techniques.",
                languageVersion = "20",
                category = "Language Features"
            ),
            Question(
                id = 57,
                questionText = "What does the Ranges library add in C++20?",
                options = listOf(
                    "A way to define numeric ranges like Python's range() only",
                    "Composable, lazily-evaluated views and adaptors that let algorithms operate directly on ranges (containers) instead of requiring begin/end iterator pairs, and can be chained with the pipe operator",
                    "A replacement for all STL containers",
                    "A networking API for HTTP ranges"
                ),
                correctAnswerIndex = 1,
                explanation = "std::ranges lets algorithms work directly on a whole range instead of iterator pairs, and range adaptors (like views::filter, views::transform) can be composed lazily with the | operator.",
                languageVersion = "20",
                category = "Language Features"
            ),
            Question(
                id = 58,
                questionText = "What are coroutines, introduced in C++20?",
                options = listOf(
                    "A way to run multiple threads simultaneously with automatic synchronization",
                    "Functions that can suspend execution and later resume, using co_await, co_yield, or co_return, enabling asynchronous and generator-style code",
                    "A feature exclusive to lambda expressions",
                    "A new kind of exception-handling mechanism"
                ),
                correctAnswerIndex = 1,
                explanation = "Coroutines are functions whose execution can be suspended with co_await or co_yield and resumed later, providing language-level support for asynchronous operations and generators without manual state machines.",
                languageVersion = "20",
                category = "Concurrency"
            ),
            Question(
                id = 59,
                questionText = "What is the spaceship operator (<=>), introduced in C++20?",
                options = listOf(
                    "An operator used only for comparing pointers",
                    "The three-way comparison operator, which can be defaulted to auto-generate all six relational operators (<, <=, >, >=, ==, !=) for a type",
                    "A bitwise operator for shifting bits",
                    "An operator that replaces the ternary conditional operator"
                ),
                correctAnswerIndex = 1,
                explanation = "The three-way comparison operator <=> returns an ordering result, and declaring auto operator<=>(const T&) const = default; lets the compiler generate all the relational operators automatically.",
                languageVersion = "20",
                category = "Language Features"
            ),

            // ===================== C++23 =====================
            Question(
                id = 60,
                questionText = "What does std::expected, introduced in C++23, provide?",
                options = listOf(
                    "A container that guarantees a value will never be missing",
                    "A type that holds either an expected value or an error, offering an alternative to exceptions for representing recoverable failures",
                    "A synonym for std::optional with no real difference",
                    "A type used exclusively for parsing JSON"
                ),
                correctAnswerIndex = 1,
                explanation = "std::expected<T, E> holds either a valid value of type T or an error of type E, letting functions report recoverable errors as part of their return type instead of throwing exceptions.",
                languageVersion = "23",
                category = "Language Features"
            ),
            Question(
                id = 61,
                questionText = "What is 'deducing this', introduced in C++23?",
                options = listOf(
                    "A way to remove the this pointer from member functions entirely",
                    "A feature allowing member functions to declare an explicit object parameter, letting a single function template deduce const-ness, value category, and even the derived type of the caller",
                    "A debugging tool for inspecting the this pointer at runtime",
                    "A macro that renames this to self"
                ),
                correctAnswerIndex = 1,
                explanation = "Deducing this lets you write an explicit object parameter (e.g. void foo(this Self&& self)) so one function template can replace multiple const/non-const or lvalue/rvalue overloads, and enables patterns like CRTP without inheritance boilerplate.",
                languageVersion = "23",
                category = "Language Features"
            )
        )
    }

    /**
     * Get a random subset of questions for a quiz
     */
    fun getRandomQuestions(count: Int): List<Question> {
        return getAllQuestions().shuffled().take(count)
    }

    /**
     * Get all questions filtered by C++ standard
     * @param languageVersion The standard to filter by ("Core", "11", "14", "17", "20", "23", or null/"All" for all)
     */
    fun getQuestionsByVersion(languageVersion: String?): List<Question> {
        val allQuestions = getAllQuestions()
        return if (languageVersion == null || languageVersion == "All") {
            allQuestions
        } else {
            allQuestions.filter { it.languageVersion == languageVersion }
        }
    }

    /**
     * Get a random subset of questions filtered by C++ standard
     * @param count Number of questions to return
     * @param languageVersion The standard to filter by ("Core", "11", "14", "17", "20", "23", or null/"All" for all)
     */
    fun getRandomQuestionsByVersion(count: Int, languageVersion: String?): List<Question> {
        return getQuestionsByVersion(languageVersion).shuffled().take(count)
    }

    /**
     * Get available C++ standard categories
     * Returns versions sorted: Core first, then numeric standards in descending order (23, 20, 17, 14, 11)
     */
    fun getAvailableVersions(): List<String> {
        val versions = getAllQuestions()
            .map { it.languageVersion }
            .distinct()

        val coreVersions = versions.filter { it == "Core" }
        val numericVersions = versions
            .filter { it != "Core" && it != "All" }
            .mapNotNull { it.toIntOrNull() }
            .sortedDescending()
            .map { it.toString() }

        return coreVersions + numericVersions
    }

    /**
     * Get all questions in a given category (e.g. "Core Concepts", "Advanced", "Collections")
     */
    fun getQuestionsByCategory(category: String?): List<Question> {
        val allQuestions = getAllQuestions()
        return if (category == null || category == "All") {
            allQuestions
        } else {
            allQuestions.filter { it.category == category }
        }
    }

    /**
     * Get the distinct categories present in the question bank, in first-seen order
     */
    fun getAvailableCategories(): List<String> {
        return getAllQuestions().map { it.category }.distinct()
    }
}