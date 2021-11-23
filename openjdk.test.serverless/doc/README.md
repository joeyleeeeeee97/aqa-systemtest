# Serverless Tests

## Contents
  * Overview
  * How is it implemented?
  * Test Automation
  
## Overview 
### AppCDS 
To improve startup and footprint, extend the existing Class-Data Sharing ("[CDS](https://openjdk.java.net/jeps/350)") feature to allow application classes to be placed in the shared archive,
which is named [AppCDS](https://wiki.openjdk.java.net/display/HotSpot/Application+Class+Data+Sharing+-+AppCDS).

### Spring Petclinic
Spring petclininc is a minimal application `canonical` mplementation, currently based on Spring Boot and Thymeleaf.

### What we Test
- AppCDS improves startup time in Spring Petclinic.
> Assert application startup speed ip with AppCDS on
- AppCDS includes application classes. 
> Assert application classes included in the 'fast' path by analyzing logs.

### Non-Goals
`Not` to compare start up performance between implementations.
`Not` to verify any internal logic after Petclinic start up.

## How is it implemented?
 
## Test Automation
These tests are only run as part of the STF load testing framework stf.load. You can find the load tests which are defined to use these test.lambda API tests, such as LambdaLoadTest.java, within the openjdk.test.load package.