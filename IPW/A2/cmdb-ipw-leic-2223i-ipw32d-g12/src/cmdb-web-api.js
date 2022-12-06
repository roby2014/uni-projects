// Module that contains all the functions callbacks that handle the HTTP API requests.

import crypto from 'crypto'

export default function (services) {
  if (!services) {
    throw Error("Can't inject <services> depedencies (missing parameters)")
  }

  return {
    getPopularMovies,
    getMovieNames,
    getAllGroups,
    getGroup,
    createGroup,
    updateGroup,
    deleteGroup,
    addMovieToGroup,
    removeMovieFromGroup,
    createUser,
    getUser
  }

  async function getPopularMovies (request, response) {
    try {
      const result = await services.getPopularMovies(request.query.nr)
      response.json({ movies: result })
    } catch (error) {
      response.status(error.code).send({
        error: error.msg
      })
    }
  }

  async function getMovieNames (request, response) {
    try {
      const movie = request.params.moviename
      const result = await services.getMoviesByName(movie, request.query.nr)
      response.json({ movies: result })
    } catch (error) {
      response.status(error.code).send({
        error: error.msg
      })
    }
  }

  async function getAllGroups (request, response) {
    try {
      const token = request.header('Authorization')
      const allGroups = await services.getAllGroups(token)
      return response.json({ groups: allGroups })
    } catch (error) {
      response.status(error.code).send({
        error: error.msg
      })
    }
  }

  async function getGroup (request, response) {
    try {
      const id = request.params.id
      const token = request.header('Authorization')
      const group = await services.getGroup(id, token)
      response.json(group)
    } catch (error) {
      response.status(error.code).send({
        error: error.msg
      })
    }
  }

  async function createGroup (request, response) {
    try {
      const id = request.body.id
      const name = request.body.name
      const description = request.body.description
      const token = request.header('Authorization')
      const group = await services.createGroup({ id, name, description }, token)
      response.json(group)
    } catch (error) {
      response.status(error.code).send({
        error: error.msg
      })
    }
  }

  async function updateGroup (request, response) {
    try {
      const id = request.params.id
      const newName = request.body.newName
      const newDesc = request.body.newDesc
      const token = request.header('Authorization')
      const groupUpdated = await services.updateGroup(
        id,
        newName,
        newDesc,
        token
      )
      response.json(groupUpdated)
    } catch (error) {
      response.status(error.code).send({
        error: error.msg
      })
    }
  }

  async function deleteGroup (request, response) {
    try {
      const id = request.params.id
      const token = request.header('Authorization')
      const groupDeleted = await services.deleteGroup(id, token)
      response.json(groupDeleted[0])
    } catch (error) {
      response.status(error.code).send({
        error: error.msg
      })
    }
  }

  async function addMovieToGroup (request, response) {
    try {
      const movieId = request.params.movieId
      const groupId = request.params.groupId
      const token = request.header('Authorization')
      const addedMovie = await services.addMovieToGroup(groupId, movieId, token)
      response.json(addedMovie)
    } catch (error) {
      response.status(error.code).send({ error: error.msg })
    }
  }

  async function removeMovieFromGroup (request, response) {
    try {
      const groupid = request.params.groupId
      const movieId = request.params.movieId
      const token = request.header('Authorization')
      const removedMovie = await services.removeMovieFromGroup(
        groupid,
        movieId,
        token
      )
      response.json(removedMovie)
    } catch (error) {
      response.status(error.code).send({ error: error.msg })
    }
  }

  // users

  async function getUser (request, response) {
    try {
      const userId = request.params.userId
      const result = await services.getUser(userId)
      response.json(result)
    } catch (error) {
      response.status(error.code).send({ error: error.msg })
    }
  }

  async function createUser (request, response) {
    try {
      const uuid = crypto.randomUUID()
      const userId = request.body.id
      const name = request.body.name
      const result = await services.addUser(uuid, userId, name)
      response.json(result)
    } catch (error) {
      console.log(error)
      response.status(error.code).send({ error: error.msg })
    }
  }
}
