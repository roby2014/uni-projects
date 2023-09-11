## Introduction

This document contains the relevant design and implementation aspects of LS project.

## Modeling the database
### Conceptual model

The following diagram holds the Entity-Relationship model for the information managed by the system.

<img src="./phase1_diagram.png" width="600" />

I highlight the following aspects:
- `list_cards` and `board_users` tables were created in order to create a `N-N` relationship.

The conceptual model has the following restrictions:
- The `board_users` table allows a user to be associated with multiple boards, but there is no way to distinguish between different types of board access (e.g. read-only vs. read-write).
- The `cards` table stores the creation date and conclusion date of each card, but there is no way to associate these dates with a specific user. This means that it is not possible to track the history of changes to a card over time.

### Physical Model

The physical model of the database is available in [create_schema.sql](../src/main/sql/createSchema.sql).

We highlight the following aspects of this model:
- The `users` table stores information about users, including their unique email and a token used for authentication.
- The `boards` table stores information about boards, including their name and description.
- The `board_users` table is a join table that relates users to the boards they have access to.
- The `lists` table stores information about lists, including their name, position, and the board they belong to.
- The `cards` table stores information about cards, including their name, description, creation date, and conclusion date.
- The `list_cards` table is a join table that relates cards to the lists they belong to.

## Open-API Specification

The Open-API specification can be accessed through `localhost:9000/api/docs` route.
It also supports SWAGGER documentation at `localhost:9000/swagger` route.
For the sake of the phases, I'll be exporting and updating it to a separate file: [docs.json](./docs.json)

## Request Details

When a request is made, the resource router will manage it, lets say a `GET` request is sent to `/users/{id}`:
- `UserRouter` receives the request:
```kt
// UserRouter.kt

// GET /users/{id}
"/users" / idLens meta {
    summary = "Get user details"
    returning(
        Status.BAD_REQUEST to "Invalid id",
        Status.NOT_FOUND to "User not found"
    )
} bindContract GET to { id -> { req -> getUser(req, id) } },
```
as you can see, `getUser` function will be called:
```kt
// UserRouter.kt
private fun getUser(req: Request, userId: Int?): Response = errorHandler {
    // ...
    val res = services.getUser(userId)
    // ...

    Response(Status.OK)
        .header("content-type", "application/json")
        .body(Json.encodeToString(res))
}
```
- The router has a property with all the resource services (in this case, `UserService`), so we use the service to access the data layer:
```kt
// UserService.kt
fun getUser(userId: Int): User {
    return storage.getUser(userId) ?: throw NotFoundException("User with id '$userId' not found.")
}
```
- The service has a storage property, to know from where to fetch the data, we can take a look at the in memory storage implementation:
```kt
// DataMemoryStorage.kt
override fun getUser(userId: Int): User? {
    return users[userId]
}
```

This is the usual full path when a request is made, so in short we have:
- API / Router layer : handles the request, uses the service, and responds back
- Service : Implement business logic and access data layer
- Storage / Data : Manipulate (read/write) data in memory / remotely

The request parameters by the api / router layer.

- For query parameters, the handlers will do it: 
```kt
// BoardRouter.kt
    // ...
    val listName = req.query("name")
        ?: throw BadRequestException("Invalid query parameter 'name'")
    // ...
}
```
In this case, we are returning `BAD_REQUEST` if query parameter `name` is not sent.

- For path parameters:
```kt
// GET /boards/{id}/lists
"/boards" / Path.of("id") / "lists" meta {
    summary = "Get the lists of a board"
    returning(
        Status.OK to "Board lists",
        Status.BAD_REQUEST to "Invalid board id",
        Status.NOT_FOUND to "Board not found"
    )
} bindContract Method.GET to { id, _ -> { req -> getBoardLists(req, id.toIntOrNull()) } }

// ...

private fun getBoardLists(req: Request, boardId: Int?): Response = errorHandler {
    // ...
    boardId ?: throw BadRequestException("Invalid 'id' parameter")
    // ...
}

```
if `{id}` parameter is not present or not an `integer`, the api will handle it and return a `400 BAD REQUEST` response.

- For body data, it's also router's handlers job:
```kt
private fun createBoard(req: Request): Response = errorHandler {
    val boardJson = Json.decodeFromString<BoardDTO>(req.bodyString())
    val res = services.createBoard(boardJson.name, boardJson.description)

    Response(Status.CREATED)
        .header("content-type", "application/json")
        .body(Json.encodeToString(res))
}
```
If `req.bodyString()` is not a valid `BoardDTO` object, this will return an error to the client.

## Connection Management

The server only connects to database when we want to query/manipulate data from it:
```kt
override fun addUserToBoard(userId: Int, boardId: Int) {
    dataSource.connection.use { conn ->
        val query = "INSERT INTO board_users (board_id, user_id) VALUES (?, ?)"
        val stm = conn.prepareStatement(query)
        stm.setInt(1, boardId)
        stm.setInt(2, userId)
        stm.executeUpdate()
    }
}
```

`.use` function receives a lambda that is executed after database connection, after lambda is executed, the connection is closed.

## Data Access

In order to access data, I've created a [`IStorage`](../src/main/kotlin/pt/isel/ls/storage/IStorage.kt) interface, that defined all the operations a data layer should have, e.g:
```kt
/** Storage contract for the tasks API. Defines the storage r/w access methods. */
interface IStorage {
    // / User management

    fun createUser(name: String, email: String, token: UUID): User?
    fun getUser(userId: Int): User?

    // / Board management

    fun getUserBoards(userId: Int): List<Board>
    fun addUserToBoard(userId: Int, boardId: Int)

    fun createBoard(name: String, description: String): Board
    fun getBoard(boardId: Int): Board?

    // / List management

    fun createBoardList(name: String, boardId: Int): BoardList
    fun getBoardLists(boardId: Int): List<BoardList>

    fun addListToBoard(boardId: Int, list: BoardList)
    fun getCardsFromList(listId: Int): List<Card>
    fun getList(listId: Int): BoardList?

    // / Card management

    fun createCard(
        listId: Int,
        name: String,
        description: String,
        creationDate: LocalDate,
        conclusionDate: LocalDate?
    ): Card

    fun getCard(cardId: Int): Card?
    fun moveCard(cardId: Int, listId: Int, newIndex: Int)
    fun deleteCard(cardId: Int)
}
```

This way, we can create our different types of data access classes (in memory, sql database, mongo, etc...) and we know they will all implement these functions.

In this phase I've created two types of data access:
- [`DataMemoryStorage`](../src/main/kotlin/pt/isel/ls/storage/DataMemoryStorage.kt) - Data stored in program memory
- [`PostgresStorage`](../src/main/kotlin/pt/isel/ls/storage/PostgresStorage.kt) - Data stored in a Postgres database

## Error Handling/Processing

In order to handle errors (#8), I've implemented a mechanism that relays on exceptions and a simple catcher.
In short, this is how it works:

`errorHandler.kt`:
```kt
fun errorHandler(fn: () -> Response): Response {
    val result = try {
        return fn()
    } catch (ex: NotFoundException) {
        Status.NOT_FOUND to ex.message
    } catch (ex: InternalServerError) {
        Status.INTERNAL_SERVER_ERROR to ex.message
    } catch (ex: BadRequestException) {
        Status.BAD_REQUEST to ex.message
    } catch (ex: MissingFieldException) {
        Status.BAD_REQUEST to ex.message
    } catch (ex: Exception) {
        Status.INTERNAL_SERVER_ERROR to "SOMETHING WENT WRONG :( -> ${ex.message}"
    }
    // catch more types...

    val (status, msg) = result
    return Response(status).body(Json.encodeToString(ErrorDTO(status.code, msg)))
}
```
with this, the API can use this wrapper to handle all the API errors:

`BoardRouter.kt`:
```kt
private fun createBoardList(req: Request, boardId: Int): Response = errorHandler {
    val listName = req.query("name")
        ?: throw BadRequestException("Invalid query parameter 'name'") // this will be catched by errorHandler

    val res = services.createBoardList(boardId, listName)

    // ....
}
```

and Services can throw as well:

`BoardService.kt`:
```kt
fun createBoardList(boardId: Int, listName: String): Int {
    storage.getBoard(boardId)
        ?: throw NotFoundException("Board not found.") // handled by errorHandler

    // ....
}
```

This way, all errors are treated as exceptions, catched by `errorHandler`, and responses are sent back to the client, with an `ErrorDTO` object:
```kt
data class ErrorDTO(val errorCode: Int, val errorDescription: String? = "")
```

## Searching boards

There is an available API route for searching a board, via some input data. The API will search for boards that match the input data via their IDs, names or descriptions. 

## Pagination

All API GET operations that return a sequence support paging, i.e., the ability to return a subsequence from the overall sequence. This paging is defined by two parameters:
- `limit` - length of the subsequence to return.
- `skip` - start position of the subsequence to return.

For example, the request `GET /boards/{id}/users?skip=6&limit=3` returns the users subsequence starting at the seventh user and ending on the ninth user.

Current routes that support pagination:
- `GET /boards/{id}/lists`
- `GET /boards/{id}/users`
- `GET /lists/{id}/cards`
- `GET /users/{id}/boards`

## Single Page Application (SPA)

The application also has an SPA (Single Page Application), it implements the following view design:
<img src="./spa_design.png" />

It can be accessed at `localhost:9000/` and it supports all the views for the current `GET` operations.

### Routing

SPA routing is handled by the following function:
```js
function getRouteObj(routeUrl) {
    let handler = notFoundRouteHandler;
    let params = {};

    routes.forEach(r => {
        const matchedRoute = matchRoute(r.path, routeUrl);
        if (matchedRoute.match) {
            handler = r.handler;
            params = matchedRoute.params;
            return;
        }
    });

    return { handler, params };
}
```
This function returns a route object with its handler and path parameters.
Path parameters are obtained via `matchRoute`, which tries to match the user path with the available paths.
E.g: we have these routes:
```
router.addRouteHandler("home", handlers.getHome);
router.addRouteHandler("user/:id", handlers.userDetails);
```
If user enters `xxx/user/3`, `matchRoute` should return an object with the path parameters (`{id:3}` in this case).

## Integration tests

Testing is a very important part of an application. This API has tests for every endpoint and separated by domain (such as UserTests, BoardTests, etc...). It uses local storage by default.
For the available tests, check the [test directory](../src/test/kotlin/pt/isel/ls/).

## Critical Evaluation

The API looks decent to be used as a backend server and the SPA already has useful endpoints to view data. There are still some small things that need to be reworked on (code design, error handling, more tests).