# Voting app API

Backed application responsible for serving REST API based on the data stored in MySQL database. 
### 0. How to run it?
### 1. REST API

####1.1 Resources
**GET /api/subject** gets all voting subjects.

Response:
```
[
  ...
  {
    "id": number,
    "title": string,
    "description": string,
    "votingStart": datetime,
    "votingEnd": datetime
  }
  ...
]
```

**POST /api/subject** creates new subject

Request: 
```
{
    "title": string,
    "description": string,
    "votingStart": datetime,
    "votingEnd": datetime
  }
```

Response:  
Header with proper location for example  
*Location: http://localhost:8080/api/{subjectId}*

**GET /api/subject/{subjectId}/voting** - gets detail information about subjects

Response:
```
{
  "inFavor": number,
  "against": number,
  "voted": boolean,
  "locked": boolean
}
```
**POST /api/subject/{subjectId}/vote** - votes for given subjects

Request
```
{
  "inFavor": boolean
}
```

Response:  
Header with proper location for example  
*Location: http://localhost:8080/api/subject/{subjectId}/vote/{voteId}*

PUT /api/subject/{subjectId}/vote - changes your vote 

Vote id is not set in url because there might be only one vote per subject for given user,
so we are able to retrieve it from there

Request:
```
{
  "inFavor": boolean
}
```

Response:
```
{
  "inFavor": boolean
}
```

POST /user - creates new user

Request:
```
{
  "username": string,
  "password": string,
  "firstName": string,
  "lastName": string
}
```

Response:  
Header with proper location for example  
*Location: http://localhost:8080/api/user/{userId}*

#### 1.2 Validation
Validation is based on JSR-303 annotations, which are resolved before actual call on controller's method. 
Because of time issues and complexity of the project, there is no exception error mappings to
REST responses. So when for example provided login already exists in DB or title of the subject has length 
longer than a filed in database, this exception will be wrapped into json default message prepared by Spring.

Because default annotation list does not contain case for date period validation, new one called *VotingPeriod* 
was created. It is used to validate whether subject vote starting date and end date
creates valid period of time (more than 7 days less than month). Belowe usage
of this annotation in *SubjectResource* class:
```java

@VotingPeriod(
        votingStart = "votingStart",
        votingEnd =  "votingEnd",
        message = "Invalid votingStart or votingEnd parameters"
)
public class SubjectResource {

    @Null
    private Long id;
    
    ...
    
    @NotNull
    private Date votingStart;

    @NotNull
    private Date votingEnd;
    
    ...
}
``` 

#### 1.3 Static web resources
All pages, scripts and css are served as static resources from *resources/public* folder. All these files were built
in frontend project and just copy pasted into this one.

Available path are:
- /login.html - login page
- /createAccount.html - create account page
- /app.html - voting application page

#### 1.4 Exception handling
As said before there was no time to prepare right implementation of validation with proper mappings. However few
new exceptions has been created to support some invalid states on the backend side. All these exception
has a *@ResponseStatus* annotation with message set and http status code to return.

- *AlreadyVotedException* - raised when user tries to create new vote resource for subject
for which he already created one. 
- *NotVotedYetException* - raised when user tries to update vote resource for subject for which
he didn't create one yet.
- *SubjectDoesNotExists* - raised when procided subject id from request doesn't correspond to any subject from the 
database.
- *UserAlreadyExistsException* - raised when user tries to create an account with login which is already stored in the database.
- *UserNotFoundException* - raised when user in not present in the database but it is need for further processing.
This exception should be never thrown because always first we have to pass security check which would fail in such case.
- *VoteLockedException* - raised when user tries update his vote more than once.  

### 2. Security
Security has been implemented using Spring Security like it was requested in the task.
Security configuration relies heavily on Spring Boot default configuration. Default *DaoAuthenticationProvider*, for which 
custom implementation of the *UserDetailsService* has been created. Also password encoding has been explicitly set
to *BCryptPasswordEncoder* (password are not stored in database as plain text).

Secured access has been set for these resource mappings:
- /api/**
- /app*

Access to login page, create user page and all other possible paths is granted for unauthenticated users.
At this moment there is no log out functionality so the best way to drop session is just to remove cookie. It was not handled
because of lack of time.

### 3. database
gdzie dump
jak ustawic polaczenie
has to change db dump because structure has changed.
SET @@global.time_zone = '+00:00';
INDEX NA USER_ID SOUBJECTID w votes bo po tym wyszukujemy!
* ...

passing ids from database
