# Voting app REST API

### 1. API

1.1 resource

- GET /api/subject - get all subjects
```json
{

}
```
POST /api/subject - create new subject
GET /api/subject/{subjectId}/vote - get all votes for subject
POST /api/subject/{subjectId}/vote - vote to subject
PUT /api/subject/{subjectId/vote - change your vote
POST /user - creates new user

1.2 validation

1.3 static resources

### security
only user service is used, deafult dao provider used, bcrypt as password encoder, csrf in json response

### DB
change db dump because structure has changed.

* ...

