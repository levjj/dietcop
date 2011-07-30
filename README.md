Description
===========

DietCOP is a crude implementation of basic [Context-Oriented Programming][1]
features in plain Java using Java's own reflection mechanisms.

In contrast to real *COP* solutions for Java like [JCop][2], *DietCOP* tries
to avoid implementation techniques that require a special compiler, a
language extension or a customized JVM. It uses a java.reflect.Proxy to
add context-dependent behavior to objects as long as these objects are
accessed through an interface. This approach is similar to the one used by
[SpringAOP][3] to enable aspect-oriented programming for plain Java. It is
therefore hard to apply to big projects without *dependecy injection* or an
*Inversion of Control* container.

Comparison with ContextJ*
-------------------------

There is another minimal, plain-Java COP solution called [ContextJ*][4]
which has a similar goal and code size. The same example application is
used in both projects which shows the differences in the usage. With
ContextJ*, the domain classes need to use a special syntax for defining
layered methods as well as all the layers. In DietCOP on the other hand,
the domain classes do not need to be altered in any way. The
context-dependent code is instead written in separate layer classes.

Features
========

* Writing context-dependend behavior in partial methods
* These methods are enclosed in separate layer classes
* Activating layers for a control flow by using the **with** keyword
* Wrapping objects with proxies for context-dependent method invocation
* Passing on the control flow to the layered method by using **proceed**

Usage
=====

The usage is shown in the example used in the test cases and is similar
to other COP solutions with the exception of the **wrap** method.

First, you need a class with a context-dependent method with a corresponding
interface.

	public class PersonImpl implements Person {
	  @Override
	  public String getName() {  ... }
	  ...
	}
	
	public interface Person {
	  String getName();
	}

Then, you need to write a layer that extends the **Layer** base class
and annotate the partial methods with the class they are meant to apply to.

	public class SuffixLayer extends Layer {
	  @AppliesTo(Person.class)
	  public String getName(Callable<String> proceed, Person self) throws Exception {
	    return proceed.call() + " " + self.getSuffix();
	  }
	}

The last step is to execute the application code. Direct method calls
will not be affected by layers, so all method invocation should go
through a proxy which is can be acquired by using the **wrap** method.

	Person person = wrap(new PersonImpl("John Doe", "PhD"));

The following statement will just print the name in the normal way
without layers.

	System.out.println(person.getName());

The activation of layers is then done by putting the context-dependent code
in a *Java Runnable* and execute it **with** a certain set of layers. The
following code will not only display the name but also the suffix.
	
	with(new SuffixLayer()).eval(new Runnable() {
	  public void run() {
	    System.out.println(person.getName());
	  }
	}

Requirements
============

* Java 1.5.0 or later
* Ant 1.8.2 *for building*
* JUnit 4 *for running the tests*

Building
========

Make sure the dependencies are installed and **ant** is in your PATH.
Then use

	$ ant dist

to create a JAR archive that can be used in other Java
projects,

	$ ant test

to run the test cases and

	$ ant run
	
to execute the provided example application.

Acknowledgement
===============

This project is inspired by the [Context-oriented programming][1] features in
[ContextJ*][4] and [JCop][2] and the AOP implementation of [SpringAOP][3].

[1]: http://www.jot.fm/issues/issue_2008_03/article4/
[2]: https://www.hpi.uni-potsdam.de/hirschfeld/trac/Cop/wiki/JCop
[3]: http://static.springsource.org/spring/docs/2.5.x/reference/aop.html
[4]: http://soft.vub.ac.be/~pcostanz/contextj.html
