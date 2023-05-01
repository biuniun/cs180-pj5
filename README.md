# CS180 Project 4:

The project is fitted into Option 2. The team decided to develop a messaging system. The system will be extend with GUI and multi-threading in the coming Project 5.
 
Two Selection methods are implemented: 
1. Block/Invisible Users 
2. Import/Export Conversations

## Compile and Run
The main function is located at Client Start. Running should be started from that location. The "file" directory should be located under the home directory of running. Pre-testing data stored in the diretcory, and can be removed if needed, while the structure of the directory must stays.

## Submission
Vocareum: LK Niu niu61
Lab Report: Matthew Rops mjrops

## Functionalities of Classes
Functionalities of the system

### CLIClient.java
The running and coordination class, it also contains the functionality of file import and export. 
### ClientStart.java
The starter class
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




