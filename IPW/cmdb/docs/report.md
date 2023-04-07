# Chelas Movies API Report

Project done by:
```
- Ruben Louro (a48926)
- Roberto Petrisoru (a49418)
```

CMDB (Chelas Movies Database) is an API where you can Search For Movies and Create an Account.

With An Account, You can Manage Groups of Movies, by adding or removing Movies. You can also give a name and description to your Groups, so you can keep track of which Group is which.

## Index

- [Chelas Movies API Report](#chelas-movies-api-report)
  - [Index](#index)
  - [Description](#description)
    - [Server](#server)
    - [Client](#client)
  - [Data storage design](#data-storage-design)
  - [Object model mapping](#object-model-mapping)
    - [Objects](#objects)
    - [Arrays](#arrays)
  - [Server API documentation](#server-api-documentation)
  - [Instructions](#instructions)
    - [Running the API (production)](#running-the-api-production)
      - [Using memory data instead of ElasticSearch](#using-memory-data-instead-of-elasticsearch)
      - [Using hardcoded movies instead of IMDB](#using-hardcoded-movies-instead-of-imdb)
    - [Running the API (development)](#running-the-api-development)
    - [Testing the API](#testing-the-api)

## Description

### Server

The API follows a simple design, which is structured in 3 main layers:
- Server: setups API and all the possible routes/endpoints and listens for requests.
- Web API : API wrapper containing functions for each route/endpoint.
- Services : Processes data from route and uses Model layer to store it.
- Data (Model) : Receives data and stores it somewhere.

Here is a simple example on how this design can be put into practice:

- `server.js`
```js
import * as groupsData from './cmdb-groups-data-mem.js'
import * as moviesData from './cmdb-movies-data-mem.js'
import servicesInit from './cmdb-services.js'
import apiInit from './cmdb-web-api.js'

const services = servicesInit(groupsData, moviesData)
const api = apiInit(services)

//...

app.get('/popularmovies', api.getPopularMovies)

app.listen(process.env.PORT, () => {})
```

- `cmdb-web-api.js`
```js
export default function (services) {
  return {
    getPopularMovies
  }

  async function getPopularMovies (request, response) {
    try {
        const result = await services.getPopularMovies(request.query.nr)
        response.json({ movies: result })
    } catch (error) {
        response.status(error.code).send({
            error: error.msg
        })`
    }
  }
```

- `cmdb-services.js`
```js
export default function (groupsData, moviesData) {
  return {
    getPopularMovies
  }

  async function getPopularMovies (limit) {
    const movies = await moviesData.getPopularMovies(limit)
    return movies
  }
}
```

- `cmdb-movies-data-mem.js`
```js
export async function getPopularMovies (limit) {
  const data = await getDummyData()
  const movies = await JSON.parse(data).items.slice(0, limit)
  return movies
}
```

As you can see, each layer uses the layer below, this design is also named **"dependency injection"**, since we are *injecting* dependencies from layer to layer.


### Client

For the client part, we introduce another layer:
- Web UI : Contains presentation files for each route/endpoint.

Obviously, this layer also has to relies on dependency injection.
```js
const services = servicesInit(groupsData, moviesData)
const api = apiInit(services)
const ui = websiteInit(services, api)
```

It also has separate routes for presentating the API content to the user. Example:

- `server.js`
```js
app.get('/popular/movies', ui.getPopularMovies)
```

- `cmdb-web-site.js`
```js
export default function websiteInit (services, web) {
  return {
    getPopularMovies: handleRequest(getPopularMovies)
  }
   
  async function getPopularMovies (request, response) {
    try {
      const movies = await services.getPopularMovies(request.query.nr)
      return { name: 'showMovies', data: { movies } }
    } catch (error) {
      return { name: 'Error', data: error }
    }
  }

  function handleRequest(handler) {
    return async function (req, res) {
      try {
        const validate =  validateUser(req, res)
        if (validate != undefined) {
          let view = await handler(req, res)
          if (view) {
            res.render(view.name, view.data)
          }
        } else {
          res.redirect("/users/login")
        }
      } catch (e) {
        const error = {code: e.code, msg: e.msg}
        res.render("error", error)
        console.log(e)
      }
    }
  }
}
```


## Data storage design
The Data Storage Desing Responds to The Layer 'Services' and is split in 2 Sections:
- The Movies data (IMDB API or Memory);
- The User related data;

The installation of 'ElasticSearch' is required for the normal function of User Data.

In terms of The Movie data, it is done by sending requests to the IMDB API, for example:
- `cmdb-movies-data.js`
```js
export async function getPopularMovies(limit) {
  try {
    const data = await fetch(`https://imdb-api.com/en/API/Top250Movies/${apiKey}`)
    const movies = await data.json()
    return movies.items.slice(0, limit)
  } catch (e) {
    console.log(e);
    throw CMDBError.INTERNAL_ERROR("Could not fetch API...")
  }
}
```
The Movie data is also implemented in memory, for testing purposes, which means that the requests aren't sent to the IMDB API, but instead to local memory, as shown in the example:
- `cmdb-movies-data-mem.js`
```js
export async function getPopularMovies (limit) {
  const data = await getDummyData()
  const movies = await JSON.parse(data).items.slice(0, limit)
  return movies
}
```
The User Information is implemented with ElasticSearch, like the following example:
- `cmdb-data-elastic.js`
```js
const BASE_URL = 'http://localhost:9200/'
export async function checkUserInfo (id, name) {
  const data = await fetch(BASE_URL + `users/_doc/${id}`, {
    headers: { Accept: 'application/json' }
  })

  const body = await data.json()

  if (!body.found) {
    return undefined
  }
  if (body._source.name != name) {
    return undefined
  }

  return body
}
```

## Object model mapping
In order to use ElasticSearch, there are some important notes that we should refer.
ElasticSearch uses JSON, while our application uses JavaScript objects, in order to store/get data, we have to "transform it", this process is also known as **"serialization/deserialization"**.

### Objects
So, in our application, we may have objects like this:
```js
{ 
  id: 11, 
  name: 'Filipe', 
  token: '3fa85f64-5717-4562-b3fc-2c963f66afa6'
}
```
but in order to store it in ElasticSearch, we have to convert it to JSON, so a quick example, would look something like this:
```js
export async function addUser (token, id, name) {
  // the object that will be converted to json
  const insertData = { name, id, token }
  
  const result = await fetch(BASE_URL + `users/_doc/${id}`, {
    method: 'POST',
    body: JSON.stringify(insertData), // converted to JSON (aKa SERIALIZE the object)
    headers: {
      'Content-Type': 'application/json', // the content we send is JSON
      Accept: 'application/json'
    }
  })

  return insertData
}
```
This function will add a user to ElasticSearch, and return it.

Now, when we want to fetch data from ElasticSearch, we need to be careful. For example, if we do:
```js
export async function getUser (id) {
  const data = await fetch(BASE_URL + `users/_doc/${id}`, {
    headers: { Accept: 'application/json' }
  })
// ...
```
the `data` will look something like:
```
{
  "_index":"users",
  "_id":"1",
  "_version":1,
  "_seq_no":5,
  "_primary_term":1,
  "found":true,
  "_source":
    {
      "name":"rob",
      "token":"xx123"
    }
}
```
in order to get the important data we want (which is inside `_source`) we have to first, deserialize this JSON object:
```js
  const body = await data.json()
```
Now `body` is a JavaScript object containing all the `data` fields. Now we can access the data we want and return it:
```js
  return {
    id: id,
    name: body._source.name,
    token: body._source.token
  }
}
```

### Arrays
In ElasticSearch, we can also use the `_search` route, which returns all the data from the index, for example:
`http://localhost:9200/groups/_search` would return:
```
{
  "took": 21,
  "timed_out": false,
  "_shards": {
    "total": 1,
    "successful": 1,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 2,
      "relation": "eq"
    },
    "max_score": 1,
    "hits": [
      {
        "_index": "groups",
        "_id": "1233",
        "_score": 1,
        "_source": {
          "name": "asdfasdf",
          "description": "ok",
          "movies": [
            {
              "id": "2",
              "name": "007",
              "duration": 120
            }
          ]
        }
      },
      {
        "_index": "groups",
        "_id": "1231143",
        "_score": 1,
        "_source": {
          "id": "1231143",
          "name": "ola",
          "description": "aaa",
          "userId": "11",
          "movies": []
        }
      }
    ]
  }
}
```
As you can see, here we do not access `_source` only anymore, but instead, we access `hits`, and then access `_source` for each object of the array.
Quick example on how we fetch this data using JavaScript objects:
```js
export async function getAllGroups () {
  const data = await fetch(BASE_URL + `groups/_search`, {
    headers: { Accept: 'application/json' }
  })
  const body = await data.json() // deserialize into JS object

  if (body.hits.total.value == 0) {
    return undefined
  }

  return body.hits.hits.map(m => m._source)
}
```
This function would return a JavaScript array, like the following:
```js
[
{
    name: "asdfasdf",
    description: "ok",
    movies: [
      {
        id: "2",
        name: "007",
        duration: 120
      }
    ]
},
{
    id: "1231143",
    name: "ola",
    description: "aaa",
    userId: "11",
    movies: []
}
]
```

## Server API documentation
Routes:
- "/popularmovies" 
  - method: Get
  - description: Gets Popular Movies
  - parameter
    - name: limit
    - in: query
- "/movies/:id"
  - method: Get
  - description: Gets a Movie
  - parameter:
    - name: id
    - in: path
- "/movies/search/:moviename"
  - method: Get
  - description: Gets a Movie by the name
  - parameter:
    - name: moviename
    - in: path
  - parameter:
    - name: max
    - in: query
- "/group"
  - method: Get
  - description: Gets all Groups
  - required: 
    - Token in Header 'Authorization'
- "/group/:id"
  - method: Get
  - description: Gets a Group
  - parameter:
    - name: id
    - in: path
  - required: 
    - Token in Header 'Authorization'
- "/group"
  - method: Post
  - description: Creates a new Group
  - required: 
    - Token in Header 'Authorization'
    - Body with Group id, name and description
- "/group/:id"
  - method: Put
  - description: Updates Group
  - parameter:
    - name: id
    - in: path
  - required: 
    - Token in Header 'Authorization'
    - Body with new Name(newName) and/or new Description(newDesc)
- "/group/:id"
  - method: Delete
  - description: Deletes a Group
- "/group/:groupId/movie/:movieId"
  - method: Put
  - required: 
    - Token in Header 'Authorization'
- "/group/:groupId/movie/:movieId"
  - method: Delete
  - paremeter:
    - name: id
    - in: path
  - required: 
    - Token in Header 'Authorization'
- "/user/:userId"
  - method: Get
  - description: Gets a User
  - parameter:
    - name: userId
    - in: path
- "/user"
  - method: Post
  - required:
    - Body with id and name
- "/user/create"
  - method: Post
  - required:
    - Body with Username and Password

- "/"
  - method: Get
  - description: Gets Home page
- "/home"
  - method: Get
  - description: Gets Home page
- "/site.css"
  - method: Get
- "/login"
  - method: Get
  - description: Gets Login page
- "/users/createUser"
  - method: Get
  - description: Gets Create User Page
- "/home/groups"
  - method: Get
  - description: Gets Groups Page
- "/home/userDetails"
  - method: Get
  - description: User Details Page
- "/popular/movies"
  - method: Get
  - description: Gets Page with Popular Movies
  - parameter:
    - name: nr
    - in: query
- "/id/movies/:id"
  - method: Get
  - description: Gets a Movie Page
  - parameter:
    - name: id
    - in: path
- "/name/movies/:moviename"
  - method: Get
  - description: Gets a Movie Page
  - parameter:
    - name: moviename
    - in: path
- "/groups/delete"
  - method: Post
  - description: Deletes a Group
  - required:
    - Sucessful Login
    - Body with Group id
- "/groups/update"
  - method: Post
  - description: Updates a Group
  - required:
    - Sucessful Login
    - Body with The new Name and new Description
- "/groups/:id/movies/add"
  - method: Post
  - description: Adds a Movie to a Group
  - required:
    - Sucessful Login
    - Body with Group id and Movie id
- "/groups/:id/movies/delete"
  - method: Post
  - description: Removes Movie From Group
  - required:
    - Sucessful Login
    - Body with Group id and Movie id
- "/groups/all"
  - method: Get
  - description: Gets All Groups From Logged On User
  - required:
    - Sucessful Login
- "/groups/find"
  - method: Post
  - description: Gets a Group from Logged On User By Id
  - required:
    - Sucessful Login
    - Body with Group id
- "/groups/create"
  - method: Post
  - description: Creates a new Group to the Logged On User
  - required:
    - Sucessful Login
    - Body with Group id, name and description
- "/groups/name/find"
  - method: Post
  - description: Gets a Group from Logged on User By Name
  - required:
    - Sucessful Login
    - Body with Group name
- "/users/login"
  - method: Get
  - description: Gets Login Page
- "/users/login"
  - method: Post
  - description: Logs In The User
  - required:
    - Body with correct Id and Name
- "/users/logout"
  - method: Get
  - description: Logs Out The Logged In User
## Instructions

In order to run the application, you need the following dependencies installed on your machine:
- git
- npm
- NodeJS
- ElasticSearch (with `xpack.security.enabled: false`)
- IMDB API Key
### Running the API (production)
```sh
git clone https://github.com/isel-leic-ipw/cmdb-ipw-leic-2223i-ipw32d-g12
cd cmdb-ipw-leic-2223i-ipw32d-g12
npm install
# run ELASTIC_PATH/bin/elasticsearch (ELASTIC_PATH is where you installed ElasticSearch)
```

Make sure you create a `.env` file, like the following:
```
API_KEY=<your_imdb_api_key>
PORT=8080
```
We use environment files in order to not leak our private api keys.

After this, running `npm run start` should start the API and you can access the API routes/endpoints.

#### Using memory data instead of ElasticSearch
In case you want to store data in memory instead of ElasticSearch, all you have to do is replace:
```js
import * as groupsData from './cmdb-data-elastic.js'
```
with
```js
import * as groupsData from './cmdb-data-mem.js'
```
in `server.js`.

#### Using hardcoded movies instead of IMDB
In case you want to use hardcoded movies from memory instead of IMDB, all you have to do is replace:
```js
import * as moviesData from './cmdb-movies-data.js' 
```
with
```js
import * as moviesData from './cmdb-movies-data-mem.js' 
```
in `server.js`.

You can change those hardcoded movies inside `cmdb-movies-data-mem.js`.

### Running the API (development)

You can also run the API in "development" mode with `npm run dev`.

This allows you to edit the application files and the server will restart automatically.


### Testing the API

The project has lots of unit tests to check that the application is working fine.

```sh
git clone https://github.com/isel-leic-ipw/cmdb-ipw-leic-2223i-ipw32d-g12
cd cmdb-ipw-leic-2223i-ipw32d-g12
npm test
```
*The unit tests store data in memory and use hardcoded movies.*

Test frameworks used:
- mocha
- chai

