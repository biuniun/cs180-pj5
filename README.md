# CS180 Project 5:

The project is fitted into Option 2. The team decided to develop a messaging system. The system is extend with GUI and multi-threading based on Project 4.
 
Two Selection methods are implemented: 
1. Block/Invisible Users 
2. Import/Export Conversations

## Compile and Run
The main function of Cliengt is located at Start.java. However, the server instance must be started before the clinet can be start. The start of the server is located at Session.java. The minimal requirement of Java version is OpenJDK 17.

## Submission
Vocareum: LK Niu niu61
Lab Report: Matthew Rops mjrops

## Functionalities of Classes
Functionalities of the system

### Client.java
The running and coordination class, it also contains the functionality of file import and export. 
### Start.java
The starter class
### Session.java
The Server hendel mutiple access at the same time.
### Login.java
Handling login and user operations.
### Message.java
The message data sturcture. It contains necessary methods for a message to be read and store.
### NotSCException
Throws when same roles user sending message to each others.
### Role
the enumerate class identify the role of users.
### Seller.java(Extended User)
The seller account and formats with specified methods further implement or overwrite the User class.
### Store.java (Extended User)
The store encapsulate the sellers with specified methods further implement or overwrite the User class.
### User.java
The class encapsulate users activity and service.
### UserTest.java 
The class of test case.




