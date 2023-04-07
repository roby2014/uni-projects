// Module manages application data.
// In this specific module, data is stored in database (elastic search) (groups (and some basic movies) + users)
// Functions should provide same data/functionality from cmdb-data-mem.js

import fetch from 'node-fetch'
const BASE_URL = 'http://localhost:9200/'

// users

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

export async function isValidToken (token) {
  const data = await fetch(BASE_URL + `users/_search?q=token:"${token}"`, {
    headers: { Accept: 'application/json' }
  })
  const body = await data.json()
  return body.hits.total.value != 0
}

export async function getUserByToken (token) {
  const data = await fetch(BASE_URL + `users/_search?q=token:"${token}"`, {
    headers: { Accept: 'application/json' }
  })
  const body = await data.json()

  if (body.hits.total.value == 0) {
    return undefined
  }

  return {
    id: body.hits.hits[0]._id,
    name: body.hits.hits[0]._source.name,
    token: body.hits.hits[0]._source.token
  }
}

export async function getUser (id) {
  const data = await fetch(BASE_URL + `users/_doc/${id}`, {
    headers: { Accept: 'application/json' }
  })
  const body = await data.json()

  if (!body.found) {
    return undefined
  }

  return {
    id: id,
    name: body._source.name,
    token: body._source.token
  }
}

export async function addUser (token, id, name) {
  const user = await getUser(id)
  if (user != undefined) {
    return -1 // already exists
  }

  const insertData = {
    name: name,
    id: id,
    token: token
  }
  const result = await fetch(BASE_URL + `users/_doc/${id}`, {
    method: 'POST',
    body: JSON.stringify(insertData),
    headers: {
      'Content-Type': 'application/json',
      Accept: 'application/json'
    }
  })

  insertData.id = id
  return insertData
}

// groups

export async function getAllGroups () {
  const data = await fetch(BASE_URL + `groups/_search`, {
    headers: { Accept: 'application/json' }
  })
  const body = await data.json()

  if (body.hits.total.value == 0) {
    return undefined
  }

  return body.hits.hits.map(m => m._source)
}

export async function getGroup (id, userId) {
  const data = await fetch(BASE_URL + `groups/_doc/${id}`, {
    headers: { Accept: 'application/json' }
  })
  const body = await data.json()

  if (!body.found) {
    return undefined
  }

  body._source.userId = userId
  body._source.id = id

  return body._source
}

export async function createGroup (group) {
  const exists = await getGroup(group.id)
  if (exists != undefined) {
    return -1 // already exists
  }

  const result = await fetch(BASE_URL + `groups/_doc/${group.id}`, {
    method: 'POST',
    body: JSON.stringify(group),
    headers: {
      'Content-Type': 'application/json',
      Accept: 'application/json'
    }
  })

  return group
}

export async function updateGroup (id, newGroup, userId) {
  const result = await fetch(BASE_URL + `groups/_doc/${id}`, {
    method: 'PUT',
    body: JSON.stringify(newGroup),
    headers: {
      'Content-Type': 'application/json',
      Accept: 'application/json'
    }
  })

  return newGroup
}

export async function deleteGroup (id, userId) {
  const group = await getGroup(id, userId)
  if (group == undefined) {
    return -1 // does not exist
  }

  const result = await fetch(BASE_URL + `groups/_doc/${id}`, {
    method: 'DELETE'
  })

  return group
}

// movies

export async function getMovieWithId (id) {
  const data = await fetch(BASE_URL + `movies/_doc/${id}`, {
    headers: { Accept: 'application/json' }
  })
  const body = await data.json()

  if (!body.found) {
    return undefined
  }

  return {
    id: id,
    name: body._source.name,
    duration: body._source.duration
  }
}

export async function addMovieToGroup (groupId, movie, userId) {
  const group = await getGroup(groupId, userId)
  if (group.movies == undefined) {
    group.movies = [movie]
  } else {
    const findMovie = group.movies.find(it => it.id == movie.id)
    if (findMovie == undefined) group.movies.push(movie)
  }
  const result = await updateGroup(
    groupId,
    {
      id: group.id,
      name: group.name,
      description: group.description,
      userId: group.userId,
      movies: group.movies
    },
    group.userId
  )
  return result
}

export async function removeMovieFromGroup (groupId, movie, userId) {
  const group = await getGroup(groupId, userId)

  const newGroup = {
    id: group.id,
    name: group.name,
    description: group.description,
    userId: group.userId,
    movies:
      group.movies != undefined
        ? group.movies.filter(it => it.id != movie.id)
        : []
  }

  const result = await updateGroup(groupId, newGroup, group.userId)
  return result
}
