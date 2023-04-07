# shell script for inserting dummy data
# can be useful for testing/development

# curl -X DELETE http://localhost:9200/users
curl -X PUT http://localhost:9200/users
curl -X PUT --data '{ "name": "Filipe", "token": "3fa85f64-5717-4562-b3fc-2c963f66afa6" }' -H "Content-Type: application/json" http://localhost:9200/users/_doc/11
curl -X PUT --data '{ "name" : "Joao", "token" : "3fa85f64-5717-4562-b3fc-2c963f66afa7"  }' -H "Content-Type: application/json" http://localhost:9200/users/_doc/12

# curl -X DELETE http://localhost:9200/movies
curl -X PUT http://localhost:9200/movies
curl -X POST --data '{ "name": "James bond", "duration": 90 }' -H "Content-Type: application/json" http://localhost:9200/movies/_doc/1
curl -X POST --data '{ "name": "007", "duration": 120 }' -H "Content-Type: application/json" http://localhost:9200/movies/_doc/2
curl -X POST --data '{ "name": "sei la", "duration": 30 }' -H "Content-Type: application/json" http://localhost:9200/movies/_doc/3

# curl -X DELETE http://localhost:9200/groups
curl -X PUT http://localhost:9200/groups
curl -X POST --data '{ "userId": 11, "name": "aaa", "description": "abc", "movies": [ {  "id": 1, "name": "James bond", "duration": 90 } ] }' -H "Content-Type: application/json" http://localhost:9200/groups/_doc/1
curl -X POST --data '{ "userId": 11, "name": "ola", "description": "xd", "movies": [] }' -H "Content-Type: application/json" http://localhost:9200/groups/_doc/2
curl -X POST --data '{ "userId": 12, "name": "coca", "description": "cola", "movies": [] }' -H "Content-Type: application/json" http://localhost:9200/groups/_doc/3

