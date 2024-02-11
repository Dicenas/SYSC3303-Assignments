
# SYSC3303 Assignment 2 — Introduction to UDP
___
This project showcases sending and receiving datagram packets through UDP sockets in Java.

## Table of Contents
___
- Description
  - UML Class Diagrams
  - UML Sequence Diagram
- Getting started
  - Prerequisites
  - Setup
  - Usage
- Credits

## Description
___
This project consists of three main components: a `Client` that sends requests, an `IntermediateHost` that acts as a relay between the client and the server, and a `Server` that processes requests and sends responses. 


### UML Class Diagrams
![UML Class1](/Client%20UML%20Class%20Diagram.png)
![UML Class2](/IntermediateHost%20UML%20Class%20Diagram.png)
![UML Class3](/Server%20UML%20Class%20Diagram.png)

### UML Sequence Diagram
![UML Sequence](/UML%20Sequence%20Diagram.png)

## Geting Started
___
### Prerequisites
To run this project, you will need Java Development Kit (JDK) 8 or higher. 

### Setup
Navigate to the project directory in your terminal, and compile `Client.java`, `IntermediateHost.java`, and `Server.java` using the javac command:

```
javac Client.java
javac IntermediateHost.java
javac Server.java
```

### Usage
1. In a terminal window, navigate to the project directory, and start the server:

```java Server```
2. In a new terminal window or tab, navigate to the project directory, and start the intermediate host:

```java IntermediateHost```
3. In another terminal window or tab, navigate to the project directory, and start the client:

```java Client```

The client sends requests to the intermediate host, which sends them
on to the server. The server sends responses to the intermediate host, which sends them on to the
client. From the client's point of view, the intermediate host appears to be the server. From the server's
point of view, the intermediate host appears to be the client. On the 11th request, the Client will send an invalid request.

## Credits
___
- Daniel Godfrey


