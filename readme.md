# Voting app REST API

### 1. API

1.1 Resources
**GET /api/subject** get all subjects.

Response:
```
[
  ...
  {
    "id": number,
    "title": string,
    "description": string,
    "votingStart": datetime 2019-05-31T00:00:00.000+0000,
    "votingEnd": datetime
  }
  ...
]
```

**POST /api/subject** create new subject

Request: 
```
{
    "title": string,
    "description": string,
    "votingStart": datetime yyyy-MM-dd'T'HH:mm:ss.SSSZ,
    "votingEnd": datetime yyyy-MM-dd'T'HH:mm:ss.SSSZ
  }
```

Response: header with proper location for example *Location: http://localhost:8080/api/{subjectId}*

GET /api/subject/{subjectId}/voting - voting details 
```json
{
  "inFavor": "number",
  "against": "number",
  "voted": "boolean",
  "locked": "boolean"
}
```
POST /api/subject/{subjectId}/vote - vote to subject
Request
```json
{
  "inFavor": "boolean"
}
```
PUT /api/subject/{subjectId}/vote - change your vote (vote id is not set in url because there might be only one vote per subject for given user so we are able to retrieve it)
```json
{
  "inFavor": "boolean"
}
```
POST /user - creates new user

1.2 validation

1.3 static resources

1.4 exception mapping handling

### 2. security
only user service is used, deafult dao provider used, bcrypt as password encoder, csrf in json response

1.5 passing ids from database

### 3. database
has to change db dump because structure has changed.
SET @@global.time_zone = '+00:00';
* ...

