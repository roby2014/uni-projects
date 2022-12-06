// Module contains all task management logic

import { CMDBError } from './errors.js'

const MAX_VALUE = 250

export default function (groupsData, moviesData) {
  if (!groupsData || !moviesData) {
    throw Error("Can't inject groups/movies depedencies (missing parameters)")
  }

  return {
    getPopularMovies,
    getMoviesByName,
    getAllGroups,
    getGroup,
    createGroup,
    updateGroup,
    deleteGroup,
    addMovieToGroup,
    removeMovieFromGroup,
    addUser,
    getUser
  }

  /// movies

  async function getPopularMovies (limit) {
    limit = limit == undefined ? MAX_VALUE : parseInt(limit, 10)

    if (isNaN(limit)) {
      throw CMDBError.INVALID_QUERY_PARAMS(
        "query param 'nr' has to be a number"
      )
    }

    if (limit > MAX_VALUE) {
      throw CMDBError.INVALID_QUERY_PARAMS(
        `Query 'nr' can't be greater than ${MAX_VALUE}`
      )
    }

    const movies = await moviesData.getPopularMovies(limit)
    return movies
  }

  async function getMoviesByName (movieName, limit) {
    limit = limit == undefined ? MAX_VALUE : parseInt(limit, 10)

    if (isNaN(limit)) {
      throw CMDBError.INVALID_QUERY_PARAMS(
        "query param 'nr' has to be a number"
      )
    }

    if (limit > MAX_VALUE) {
      throw CMDBError.INVALID_QUERY_PARAMS(
        `Query 'nr' can't be greater than ${MAX_VALUE}`
      )
    }

    const movies = await moviesData.getMoviesByName(movieName, limit)
    return movies
  }

  /// groups

  async function getAllGroups (token) {
    if (!groupsData.isValidToken(token)) {
      throw CMDBError.NOT_AUTHORIZED()
    }

    const user = await groupsData.getUserByToken(token)
    const allGroups = await groupsData.getAllGroups()
    return allGroups.filter(it => it.userId == user.id)
  }

  async function getGroup (id, token) {
    if (!groupsData.isValidToken(token)) {
      throw CMDBError.NOT_AUTHORIZED()
    }

    const user = await groupsData.getUserByToken(token)
    const groupFound = await groupsData.getGroup(id, user.id)
    if (groupFound == undefined) {
      throw CMDBError.GROUP_NOT_FOUND(id)
    }

    return groupFound
  }

  async function createGroup (group, token) {
    if (!groupsData.isValidToken(token)) {
      throw CMDBError.NOT_AUTHORIZED()
    }

    if (
      group.id == undefined ||
      group.name == undefined ||
      group.description == undefined
    ) {
      throw CMDBError.INVALID_BODY(`<id>;<name>;<description> needed`)
    }

    const groupExists = await groupsData.getGroup(group.id)
    if (groupExists != undefined) {
      throw CMDBError.GROUP_ALREADY_EXISTS(group.id)
    }

    const user = await groupsData.getUserByToken(token)
    group.userId = user.id
    return groupsData.createGroup(group)
  }

  async function updateGroup (id, newName, newDesc, token) {
    if (!groupsData.isValidToken(token)) {
      throw CMDBError.NOT_AUTHORIZED()
    }

    if (newName == undefined && newDesc == undefined) {
      throw CMDBError.INVALID_BODY('at least <newName> or <newDesc> needed')
    }

    const user = await groupsData.getUserByToken(token)
    const updated = await groupsData.updateGroup(
      id,
      {
        id,
        name: newName,
        description: newDesc
      },
      user.id
    )

    if (updated == undefined) {
      throw CMDBError.GROUP_CANT_UPDATE(id)
    }

    return updated
  }

  async function deleteGroup (id, token) {
    if (!groupsData.isValidToken(token)) {
      throw CMDBError.NOT_AUTHORIZED()
    }

    const user = await groupsData.getUserByToken(token)
    const removed = await groupsData.deleteGroup(id, user.id)
    if (removed == undefined) {
      throw CMDBError.GROUP_NOT_FOUND(id)
    }

    return removed
  }

  async function addMovieToGroup (groupId, movieId, token) {
    if (!groupsData.isValidToken(token)) {
      throw CMDBError.NOT_AUTHORIZED()
    }

    const user = await groupsData.getUserByToken(token)
    const group = await groupsData.getGroup(groupId, user.id)
    if (group == undefined) {
      throw CMDBError.GROUP_NOT_FOUND(groupId)
    }

    const movie = await groupsData.getMovieWithId(movieId)
    if (movie == undefined) {
      throw CMDBError.MOVIE_NOT_FOUND(movieId)
    }

    const added = await groupsData.addMovieToGroup(group, movie)
    if (added == 0) {
      throw CMDBError.CANT_ADD_MOVIE_TO_GROUP(movieId, groupId)
    }

    return added
  }

  async function removeMovieFromGroup (groupId, movieId, token) {
    if (!groupsData.isValidToken(token)) {
      throw CMDBError.NOT_AUTHORIZED()
    }

    const user = await groupsData.getUserByToken(token)
    const group = await groupsData.getGroup(groupId, user.id)
    if (group == undefined) {
      throw CMDBError.GROUP_NOT_FOUND(groupId)
    }

    const movie = await groupsData.getMovieWithId(movieId)
    if (movie == undefined) {
      throw CMDBError.MOVIE_NOT_FOUND(movieId)
    }

    const removed = await groupsData.removeMovieFromGroup(group, movieId)
    if (removed == undefined) {
      throw CMDBError.CANT_REMOVE_MOVIE_FROM_GROUP(movieId, groupId)
    }

    return removed
  }

  // users

  async function addUser (uuid, userId, name) {
    if (userId == undefined || name == undefined) {
      throw CMDBError.INVALID_BODY('<id>;<name> needed')
    }

    const user = await groupsData.addUser(uuid, userId, name)
    if (user == -1) {
      throw CMDBError.USER_ALREADY_EXISTS(userId)
    }

    return user
  }

  async function getUser (userId) {
    const user = await groupsData.getUser(userId)
    if (user == undefined) {
      throw CMDBError.USER_NOT_FOUND(userId)
    }

    return user
  }
}
