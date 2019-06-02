# Voting app API

Backed application responsible for serving REST API based on the data stored in MySQL database. 
### 0. How to run it?

#### 0.1 Apply database dump
Path is *resources/script/\*.sql*

#### 0.2 Build backend project
There are two ways, depending on how we will want to run app.  
If you want to deploy it on tomcat server, then just *mvn clean install* + copying it to tomcat do the job.  
If you want to run it as standalone executable war, then:
 - Please remove this dependency from pom
```
...
    <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency> <-- this one !!!!!!!!!!!!!!
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
...
```
- mvn clean install
- java -jar voting-app-api-1.0.0.war

#### 0.2 Go to login page
As asked in the mail, please ensure that app is deployed at root context path ("/").
Login page will be available under:  
*protocol://host:port/login.html*

### 1. REST API

#### 1.1 Resources
##### 1.1.1 **GET /api/subject**
Gets all voting subjects.

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

##### 1.1.2 **POST /api/subject** 
Creates new subject.

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
*Location: http://localhost:8080/api/subject/{subjectId}*

##### 1.1.3 **GET /api/subject/{subjectId}/voting**
Gets detail information about subjects

Response:
```
{
  "inFavor": number,
  "against": number,
  "voted": boolean,
  "locked": boolean
}
```
##### 1.1.4 **POST /api/subject/{subjectId}/vote** 
Votes for given subjects

Request
```
{
  "inFavor": boolean
}
```

Response:  
Header with proper location for example  
*Location: http://localhost:8080/api/subject/{subjectId}/vote/{voteId}*

##### 1.1.5 **PUT /api/subject/{subjectId}/vote**
Changes your vote 

Vote id is not set in url because there might be only one vote per subject for given user,
so we are able to retrieve it from there.

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

##### 1.1.6 **POST /user**
Creates new user

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

### 3. Database
Three database tables has been created. **user**, **vote** and **vote_subject**. Firs one is responsible for storing
application users, **vote_subject** contains data for each subject. **vote** is a helper table for mapping many to many relation
between previous two tables. Also it contains information about the vote state.

Database dump can be found in *resources/scripts* folder.

Default Spring Boot connection has been created setting only right values in applications.properties (this is the fastest solution).  
Default values are:
```properties
spring.jpa.hibernate.ddl-auto=none
spring.datasource.url=jdbc:mysql://localhost:3306/voting_app?serverTimezone=UTC
spring.datasource.username=app_user
spring.datasource.password=app_user
```

#### 3.1 User table
|column name|value type|constraints|remarks|
|-----------|----------|-----------|-------|
|id|int|PK, NN, UQ||
|first_name|varchar(45)|NN||
|last_name|varchar(45)|NN||
|login|varchar(45)|NN, UQ||
|password|varchar(60)|NN|Stored as encoded value|

Default indexes has been set on primary key and unique values. No triggers.

### 3.2 Vote subject table
|column name|value type|constraints|remarks|
|-----------|----------|-----------|-------|
|id|int|PK, NN, UQ||
|owner_id|int|FK(user), NN| This field doesn't store vote relations! It is just find user who created given subject (nice to have)|
|title|varchar(200)|NN, UQ||
|description|varchar(2000)|NN||
|voting_start|date|NN|
|voting_end|date|NN|

Default indexes has been set on primary key, foreign key and unique values. No triggers.

### 3.2 Vote table
|column name|value type|constraints|remarks|
|-----------|----------|-----------|-------|
|id|int|PK, NN, UQ|Why compound key not used instead? To have possibility to locate resource using just one value. In future we might want to reach it directly, not through subject or user.|
|user_id|int|FK, NN, UQ(user_id, vote_subject_id)|Index set on this unique constraint is **EXTREMELY IMPORTANT!** because we are using it in one, heavy query.|
|vote_subject_id|int|FK, NN, UQ(user_id, vote_subject_id)|Same as above.|
|in_favor|boolean|NN, trigger check|Used trigger to check if we can update this filed. If locked == true then not.|
|locked|boolean|NN||

Table which stores m:n relation, little bit more complicated than previous ones. Trigger was created to support "only one vote update available" feature. 

**Last remark:** primary keys from database usually shouldn't be used as ids to locate REST resources. It was just created like this to save some time.
