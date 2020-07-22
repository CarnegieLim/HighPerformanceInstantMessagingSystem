# High Performance Instant Messaging System

## Introduction
This High Performance Instant Messaging System is currently developed based on Netty and UDP. Because of its lightweight, low overhead, and low latency, UDP is a good protocol for communication on unstable network. The following is my main work in this project.

* Implemented two kinds of protocol(GRPC and JSON) based on UDP protocol, which can take advantage of UDP(lightweight, low overhead, and low latency). Used periodic heartbeat to keep alive.
* Leveraged Netty to build a fully asynchronous, event-driven, and high-performance instant messaging server while decoupling the encoding, decoding and message storing modules from each other.
* Developed a QoS(Quality of Service) mechanism to ensure delivery of messages.
* Developed a Timeline model(a write diffusion model) to achieve the incremental synchronization of multi-client and offline message roaming.
* Used AWS DynamoDB to persistently store the messages. Encapsulated related code by leveraging AWS Developer SDK and Enhanced DynamoDB Client.
* Developed a Flutter SDK to fully manage the connection, heartbeat, and QoS messages between the Server and Client. Use callbacks to notify the app once receiving chat messages.
* Use Spring Boot to manage the configuration. Use RabbitMQ to improve system capacity.
* Test correctness and performance with simulated Java client and flutter app.

## Main Goals
* Ensuring the reliability of message delivery is the primary goal. In order to make up for the unreliability of UDP communication, I adopt QoS(Quality of Service) mechanism to ensure the delivery of messages. Each message sent by the client must be put into the QoS queue to wait for the ack package from the server. Also, before the server sends back an ack package of a message, this message must be reliably stored on the server. Ensure that messages will not be lost is the first priority. Online pushing is just an optimal way to achieve real-time delivery. The reliable way to delivery message is incremental synchronization.
* Modularity is the second goal. Because the entire system is complex, modularity is very important. With modularity, debugging, horizontal scaling, and vertical scaling is relatively easy in the future. In this system, RabbitMQ is a key component which can help decoupling.

## Why this project
There is a saying that, modern Internet applications are more and more like IM, however, IM system has become more and more unlike IM. What does this mean? For example, Google Docs is an online office application on which everyone can work together in the same document at the same time. Essentially, multi-person collaboration is a group chat and content synchronization in Google Docs is done by a very complex IM system. Nowadays, more and more modern Internet application rely on high-performance IM system to achieve real-time interaction. And, modern IM systems
have become more and more unlike IM, because modern IM need more powerful features to support modern Internet application.

## To Run
Clone this repo, then Run on Intellij Idea.

## Thank you
* Thank you for your time and attention!
* I'm so sorry the documentation and comments are not good at the moment.
* In the near future, I will further improve this system, and the (English and Chinese version) documentation and comments will be more detailed. 