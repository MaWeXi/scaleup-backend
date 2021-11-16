# scaleup-backend

##

### User

| HTTP | URL | Example | Data |
| --- | ----------- | ------ | ------ |
| GET   | https://localhost:8080/api/v1/user/all | / | List<User object>
| GET | https://localhost:8080/api/v1/user/all/id | / | User object
| POST    | https://localhost:8080/api/v1/user/all | { "id": "15787aab-9ec8-48c8-9535-ba1ff585b16a", "username": "horst" } | User object
| PUT    | https://localhost:8080/api/v1/user/all/id | { "id": "15787aab-9ec8-48c8-9535-ba1ff585b16a", "username": "hans" } | User object
