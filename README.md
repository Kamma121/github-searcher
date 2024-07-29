
# Github Searcher🕵️
This project provides an API that allows consumers to list all repositories of a given GitHub user that are not forks. It also provides detailed information about each repository's branches, including branch names and the last commit SHA.




## Endpoints

`GET`  `/github/search/{username}`

Fetches details of all repositories for a specified  **username**

#### Path parameters
- `username` -  The Github username to search repositories for.

## Request Headers
- `Accept: application/json` - This header specifies that the response should be in JSON format.


## Response Structure 
`Success Response (200)` - The API returns a JSON array of repositories. 

Each repository object contains:
- `repositoryName` - The name of the repository.
- `ownerLogin` - The login of the owner of the repository.
- `branches` - An array of branch objects. Each branch object contains:
    - `name` - The name of the branch.
    - `commitSha` - The SHA of the last commit on the branch.

#### Example success response: 
```json
[
    {
        "repositoryName": "repository-1",
        "ownerLogin": "exampleOwner",
        "branches": [
            {
                "name": "master",
                "commitSha": "d31e86ef6377cbd1"
            },
            {
                "name": "develop",
                "commitSha": "2516c5adaa1cd10z"
            },
            {
                "name": "feature",
                "commitSha": "508efc09cce9f1g3"
            }
        ]
    }
]            
```

`Error Response` - The API handles errors by returning a JSON object with the following structure:
 - `status` - The HTTP response status code.
- `message` - A message explaining the error.


#### Example error response:
```json 
{
    "status": 404,
    "message": "User not found"
}
```

## Error Handling 
The API handles errors by returning appropriate HTTP status codes and error messages in JSON format.


**1. User Not Found**
- Status: 404 Not Found
- Description: This error occurs when the specified GitHub user does not exist.
- Response example:
    ```json
    {
        "status": 404,
        "message": "User not found"
    }
    ```
**2. Rate Limit Exceeded**
- Status: 403 Forbidden
- Description: This error occurs when the GitHub API rate limit has been exceeded.
- Response example:
    ```json
    {
        "status": 403,
        "message": "API rate limit exceeded"
    }
    ```
**3. Internal Server Error**
- Status: 500 Internal Server Error
- Description: This error occurs when there is an internal server error during the processing of the request.
- Response example:
    ```json
    {
        "status": 500,
        "message": "An internal server error occurred"
    }
    ```
**4. Not Acceptable**
- Status: 406 Not Acceptable
- Description: This error occurs when the requested media type is not acceptable.
- Response example:
    ```json
    {
        "status": 406,
        "message": "The requested media type is not acceptable"
    }
    ```




## Technologies
This API is built using the following technologies:
- **Java 21** - The programming language used for the implementation.
- **Spring Boot 3** - A Java framework used to simplify the development of the API.
- **OkHttp** - A networking library used to make HTTP requests.
- **Gson** - A library used for JSON serialization and deserialization.
- **Lombok** - A library used to reduce boilerplate code.
