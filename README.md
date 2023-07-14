# Mindsync 

[![CI Build](https://github.com/yacosta738/mindsync/actions/workflows/continuous-integration.yml/badge.svg)](https://github.com/yacosta738/mindsync/actions/workflows/continuous-integration.yml)
![coverage](.github/badges/jacoco.svg)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=yacosta738_mindsync&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=yacosta738_mindsync)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=yacosta738_mindsync&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=yacosta738_mindsync)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=yacosta738_mindsync&metric=bugs)](https://sonarcloud.io/summary/new_code?id=yacosta738_mindsync)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=yacosta738_mindsync&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=yacosta738_mindsync)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=yacosta738_mindsync&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=yacosta738_mindsync)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=yacosta738_mindsync&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=yacosta738_mindsync)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=yacosta738_mindsync&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=yacosta738_mindsync)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=yacosta738_mindsync&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=yacosta738_mindsync)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=yacosta738_mindsync&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=yacosta738_mindsync)
[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=yacosta738_mindsync)](https://sonarcloud.io/summary/new_code?id=yacosta738_mindsync)

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-black.svg)](https://sonarcloud.io/summary/new_code?id=yacosta738_mindsync)

## Why Spring Webflux?

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

## Technologies

- SpringBoot ( Webflux )
- Using NoSQl db Neo4j ) 
