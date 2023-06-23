# Why Spring Webflux?

There are several reasons why you might choose to use WebFlux (Project Reactor) instead of the traditional Spring MVC in
your application:

1)Non-blocking: WebFlux is based on a non-blocking programming model, meaning it can handle a large number of concurrent
requests without the need for thread blocking. This can result in better performance and scalability for
high-concurrency applications.
2)Reactive programming: WebFlux is built on top of Project Reactor, a reactive programming library. This allows you to
write code that responds to changes in data streams in a more efficient and predictable way.
3)Full asynchronous support: WebFlux supports both asynchronous and synchronous request handling. It allows you to fully
leverage the benefits of asynchronous programming, such as better resource utilization and lower latency.
4)Better error handling: WebFlux has built-in support for handling errors and exceptions in a more consistent and
efficient way.
5)Improved testability: WebFlux's non-blocking and reactive programming model makes it easier to test, especially when
it comes to testing asynchronous code.

That being said, Spring MVC is still a good choice for many types of applications, especially those with simpler
requirements or where performance is not as critical.

# Technologies

- SpringBoot ( Webflux )
- Using NoSQl db Neo4j ) 
