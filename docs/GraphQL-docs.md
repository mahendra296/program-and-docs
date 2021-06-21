# GraphQL Guide

## Table of Contents
- [Root Types](#root-types)
- [Query Operations](#query-operations)
- [Mutation Operations](#mutation-operations)
- [Response Handling](#response-handling)
- [Spring Boot Implementation](#spring-boot-implementation)
- [Microservice Communication](#microservice-communication)

---

## Root Types

GraphQL has three special root types that define the operations available in your API:

| Type | Purpose | HTTP Equivalent |
|------|---------|----------------|
| **Query** | Reading data | GET operations |
| **Mutation** | Modifying data | POST/PUT/DELETE operations |
| **Subscription** | Real-time updates | WebSocket connections |

---

## Query Operations

### Basic Query

**Request:**
```graphql
query {
  users {
    id
    name
    email
  }
}
```

**Response:**
```json
{
  "data": {
    "users": [
      {
        "id": "1",
        "name": "John Doe",
        "email": "john@example.com"
      }
    ]
  }
}
```

### Query with Variables

**Query Definition:**
```graphql
query GetUser($userId: ID!) {
  user(id: $userId) {
    id
    name
    email
    posts {
      title
    }
  }
}
```

**Variables:**
```json
{
  "userId": "1"
}
```

---

## Mutation Operations

### Basic Mutation (Inline Arguments)

**Request:**
```graphql
mutation {
  createUser(
    name: "Alice Cooper"
    email: "alice@example.com"
    age: 28
  ) {
    id
    name
    email
    age
  }
}
```

**Response:**
```json
{
  "data": {
    "createUser": {
      "id": "4",
      "name": "Alice Cooper",
      "email": "alice@example.com",
      "age": 28
    }
  }
}
```

### Mutation with Input Type

**Request:**
```graphql
mutation {
  createUser(input: {
    name: "John Doe"
    email: "john@example.com"
    age: 30
  }) {
    id
    name
    email
    age
  }
}
```

### Mutation with Variables

**Mutation Definition:**
```graphql
mutation CreateNewPost($title: String!, $content: String!, $authorId: ID!) {
  createPost(
    title: $title
    content: $content
    authorId: $authorId
  ) {
    id
    title
    content
    author {
      name
    }
  }
}
```

**Variables:**
```json
{
  "title": "My First Post",
  "content": "Hello World!",
  "authorId": "1"
}
```

---

## Response Handling

GraphQL responses can have different patterns based on success or failure:

### 1. Successful Response (No Errors)

```json
{
  "data": {
    "user": {
      "id": "1",
      "name": "John Doe"
    }
  }
}
```

> **Note:** No `errors` field is present when everything succeeds.

### 2. Partial Success (Some Fields Failed)

```json
{
  "data": {
    "user": {
      "id": "1",
      "name": "John Doe",
      "email": null
    }
  },
  "errors": [
    {
      "message": "Not authorized to view email",
      "path": ["user", "email"]
    }
  ]
}
```

> **Note:** Both `data` and `errors` are present.

### 3. Complete Failure (Query Couldn't Execute)

```json
{
  "data": {
    "user": null
  },
  "errors": [
    {
      "message": "User with ID 999 not found",
      "locations": [
        {
          "line": 2,
          "column": 3
        }
      ],
      "path": ["user"]
    }
  ]
}
```

> **Note:** `data` is null, but `errors` explains why.

### 4. Validation Error (Before Execution)

```json
{
  "errors": [
    {
      "message": "Cannot query field 'invalidField' on type 'User'",
      "locations": [
        {
          "line": 3,
          "column": 5
        }
      ]
    }
  ]
}
```

> **Note:** No `data` field at all (query didn't even execute).

---

## Spring Boot Implementation

### Configuration

Add this to your `application.properties`:

```properties
# GraphQL Configuration
spring.graphql.graphiql.enabled=true
spring.graphql.graphiql.path=/graphiql
```

### Approach 1: Using @Controller (Modern Spring Approach)

#### Define Schema

Create `src/main/resources/graphql/schema.graphqls`:

```graphql
type Post {
  id: ID!
  title: String!
  content: String!
  published: Boolean!
}

type Query {
  post(id: ID!): Post
  posts: [Post!]!
}

type Mutation {
  createPost(input: CreatePostInput!): Post!
}

input CreatePostInput {
  title: String!
  content: String!
  authorId: ID!
}
```

#### Controller Implementation

```java
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class PostController {
    
    private final PostRepository postRepository;
    
    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    @QueryMapping
    public Post post(@Argument Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post with ID " + id + " not found"));
    }
    
    @QueryMapping
    public List<Post> posts() {
        return postRepository.findAll();
    }
    
    @MutationMapping
    public Post createPost(@Argument CreatePostInput input) {
        Post post = new Post();
        post.setTitle(input.getTitle());
        post.setContent(input.getContent());
        // ... set other fields
        return postRepository.save(post);
    }
}
```

---

### Approach 2: Using Query/Mutation Resolvers (GraphQL-Native Approach)

#### Dependencies

Add to `pom.xml`:

```xml
<dependency>
    <groupId>com.graphql-java-kickstart</groupId>
    <artifactId>graphql-spring-boot-starter</artifactId>
    <version>15.0.0</version>
</dependency>
```

#### Define Schema

```graphql
type User {
  id: ID!
  name: String!
  email: String!
  age: Int
}

type Query {
  users: [User!]!
  user(id: ID!): User
}

type Mutation {
  createUser(name: String!, email: String!, age: Int): User!
}
```

#### Query Resolver

```java
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

@Component
public class UserQueryResolver implements GraphQLQueryResolver {
    
    private final UserRepository userRepository;
    
    public UserQueryResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    // Query: users
    public List<User> users() {
        return userRepository.findAll();
    }
    
    // Query: user(id: ID!)
    public User user(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with ID " + id + " not found"));
    }
}
```

#### Mutation Resolver

```java
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Component;

@Component
public class UserMutationResolver implements GraphQLMutationResolver {
    
    private final UserRepository userRepository;
    
    public UserMutationResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    // Mutation: createUser
    public User createUser(String name, String email, Integer age) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        
        return userRepository.save(user);
    }
}
```

---

### Comparison: Controller vs Resolver Approach

| Aspect | @Controller Approach | Resolver Interface Approach |
|--------|---------------------|----------------------------|
| **Annotation** | `@QueryMapping`, `@MutationMapping` | `implements GraphQLQueryResolver` |
| **Method Names** | Can be anything | Must match schema field names |
| **Flexibility** | More Spring-integrated | More GraphQL-native |
| **Learning Curve** | Easier for Spring developers | Easier for GraphQL developers |
| **Library** | Spring for GraphQL (built-in) | GraphQL Java Kickstart (external) |

---

## Microservice Communication

### Using WebClient (Recommended - Modern Approach)

WebClient is the modern, non-blocking HTTP client from Spring WebFlux.

#### Add Dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

#### GraphQL Client Service

```java
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class GraphQLClientService {
    
    private final WebClient webClient;
    
    public GraphQLClientService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://user-service:8081/graphql")
                .build();
    }
    
    // Execute GraphQL query
    public <T> T executeQuery(String query, Map<String, Object> variables, Class<T> responseType) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        
        if (variables != null && !variables.isEmpty()) {
            requestBody.put("variables", variables);
        }
        
        return webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }
}
```

#### Usage Example

```java
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    
    private final GraphQLClientService graphQLClient;
    
    public UserService(GraphQLClientService graphQLClient) {
        this.graphQLClient = graphQLClient;
    }
    
    public UserData getUserById(Long userId) {
        String query = """
            query GetUser($id: ID!) {
                user(id: $id) {
                    id
                    name
                    email
                }
            }
            """;
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", userId);
        
        GraphQLResponse<UserData> response = graphQLClient.executeQuery(
            query, 
            variables, 
            GraphQLResponse.class
        );
        
        if (response.getErrors() != null && !response.getErrors().isEmpty()) {
            throw new RuntimeException("GraphQL Error: " + response.getErrors().get(0).getMessage());
        }
        
        return response.getData();
    }
}
```

#### Response DTO

```java
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraphQLResponse<T> {
    private T data;
    private List<GraphQLError> errors;
}

@Data
class GraphQLError {
    private String message;
    private List<Location> locations;
    private List<String> path;
}

@Data
class Location {
    private int line;
    private int column;
}
```

---

## Key Points to Remember

1. **Single Endpoint**: All GraphQL operations (Query, Mutation, Subscription) use the same endpoint: `/graphql`

2. **POST Method**: Unlike REST, GraphQL always uses POST requests, even for queries

3. **Error Handling**: The `errors` field only appears when something goes wrong

4. **Variables**: Use variables for dynamic values instead of string interpolation

5. **Type Safety**: GraphQL schemas provide strong typing and validation

6. **Microservices**: Use WebClient for inter-service GraphQL communication

7. **Method Names**: 
   - With `@Controller`: Method names can be anything
   - With Resolvers: Method names must match schema field names exactly

---

## Best Practices

- ✅ Always use variables for dynamic queries
- ✅ Handle both `data` and `errors` in responses
- ✅ Use Input types for complex mutation arguments
- ✅ Implement proper error handling in resolvers
- ✅ Use WebClient for non-blocking microservice calls
- ✅ Enable GraphiQL for API exploration during development
- ✅ Keep queries specific - request only the fields you need

---

## Resources

- [GraphQL Official Documentation](https://graphql.org/)
- [Spring for GraphQL](https://spring.io/projects/spring-graphql)
- [GraphQL Java Kickstart](https://www.graphql-java-kickstart.com/)

---

**Last Updated:** December 2024