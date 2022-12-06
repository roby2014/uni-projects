# IPW A1 PART 2 (Node application)

<div style="text-align: center">
   Instituto Superior de Engenharia de Lisboa
   <div style="font-size: 80%">
   Bachelor in Computer Science and Computer Engineering
   <br>Bachelor in Informatics, Networks and Telecommunications Engineering
   </div>
   Internet Programming/Introduction to Web Programing

   Winter Semester of 2022/2023 – **1st practical assignment**
</div>

# Part 2 - Node application

## Foreword

This part requires that your code makes HTTP requests to the [IMDb API](https://imdb-api.com).
To access this API, each student must request the API key that identifies the student application's requests. That key must be included in the URL of each HTTP request. The description of the necessary steps to create the token are described [here at the bottom](https://imdb-api.com). Note that this is a free API, and therefore it is expected that you comply with the [Rate Limits and Good Citizenship rules](https://imdb-api.com/pricing).

## Application requirements

This application reads movie ids from a json file, with the following format:

```json
{
   "movie-ids" : [
       "tt0111161",
       "tt0068646",
       "tt0468569",
   ]
}

```

  and produces another JSON file containing each movie id, title, duration, and the total duration of those movies, as shown in following example:

```json
{
    "total-duration": 469,
    "movies": [
        {
            "id": "tt0111161",
            "title": "The Shawshank Redemption",
            "duration": 142
        },
        {
            "id": "tt0068646",
            "title": "The Godfather",
            "duration": 175
        },
        {
            "id": "tt0468569",
            "title": "Inception",
            "duration": 152
        }
    ]
}

```

Implement two versions of the application:

1. Using Promises explicitly
2. Using the async/await style

REMARK: The students should consider the limitations of this free API, namely the maximum number of requestas per day (100).
