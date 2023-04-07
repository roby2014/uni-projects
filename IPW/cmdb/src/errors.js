export const CMDBError = {
  INTERNAL_ERROR: internalError,

  INVALID_BODY: invalidBody,
  INVALID_QUERY_PARAMS: invalidQueryParams,

  GROUP_NOT_FOUND: groupNotFound,
  GROUP_ALREADY_EXISTS: groupAlreadyExists,
  GROUP_CANT_UPDATE: groupCantUpdate,

  MOVIE_NOT_FOUND: movieNotFound,
  CANT_ADD_MOVIE_TO_GROUP: cantAddMovieToGroup,
  CANT_DELETE_MOVIE_FROM_GROUP: cantDeleteMovieFromGroup,

  USER_NOT_FOUND: userNotFound,
  CANT_ADD_USER: cantAddUser,
  USER_ALREADY_EXISTS: userAlreadyExists,

  NOT_AUTHORIZED: notAuthorized
}

function internalError(info) {
  return {
    code: 500,
    msg: `Internal server error: ${info}`
  }
}

function invalidBody(info) {
  return {
    code: 400,
    msg: `Invalid body, check documentation (${info})`
  }
}

function invalidQueryParams(info) {
  return {
    code: 400,
    msg: `Invalid query parameters, check documentation (${info})`
  }
}

function groupNotFound(id) {
  return {
    code: 404,
    msg: `Group with 'id' ${id} does not exist`
  }
}

function groupAlreadyExists(id) {
  return {
    code: 400,
    msg: `Group with 'id' ${id} already exists`
  }
}

function groupCantUpdate(id) {
  return {
    code: 400,
    msg: `Could not update group with 'id' ${id}, does it exist?`
  }
}

function movieNotFound(id) {
  return {
    code: 404,
    msg: id == undefined ? `Movie not found` : `Movie with 'id' ${id} does not exist`
  }
}

function cantAddMovieToGroup(movieId, groupId) {
  return {
    msg: `Could not add Movie with 'id' ${movieId} to Group with 'id' ${groupId}`,
    code: 400
  }
}

function cantDeleteMovieFromGroup(movieId, groupId) {
  return {
    msg: `Could not remove Movie with 'id' ${movieId} from Group with 'id' ${groupId}`,
    code: 400
  }
}

function userNotFound(id) {
  return {
    code: 404,
    msg: `User with 'id' ${id} does not exist`
  }
}

function cantAddUser(id) {
  return {
    code: 400,
    msg: `Cant add user with 'id' ${id}`
  }
}

function userAlreadyExists(id) {
  return {
    code: 400,
    msg: `User with 'id' ${id} already exists`
  }
}

function notAuthorized() {
  return {
    code: 401,
    msg: `Not authorized`
  }
}
