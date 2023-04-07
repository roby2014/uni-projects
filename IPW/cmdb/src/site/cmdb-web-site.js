import { group } from 'console'
import { json } from 'express'
import url from 'url'
import crypto from 'crypto'
const __dirname = url.fileURLToPath(new URL('.', import.meta.url))
let authentication = false
export default function websiteInit (services, web) {
  return {
    getHome,
    getCss,
    getLogin,
    getMovies,
    getGroups,
    getLoginForm,
    getValidateLogin,
    getUserDetails,
    logout,
    getCreateUser,
    createUser,
    createGroup: handleRequest(createGroup),
    getGroup: handleRequest(getGroup),
    getGroupByName: handleRequest(getGroupByName),
    getAllGroups: handleRequest(getAllGroups),
    deleteGroup: handleRequest(deleteGroup),
    updateGroup: handleRequest(updateGroup),
    addMovietoGroup: handleRequest(addMovietoGroup),
    removeMoviefromGroup: handleRequest(removeMoviefromGroup),
    getPopularMovies: handleRequest(getPopularMovies),
    getMovie: handleRequest(getMovie),
    getMovieNames: handleRequest(getMovieNames)
  }

  async function getHome (request, response) {
    response.render('home', { title: 'Home', home: true, auth: authentication, user:request.user })
  }

  async function getCss (request, response) {
    try {
      const htmlFileLocation = __dirname + 'public/site.css'
      response.sendFile(htmlFileLocation)
    } catch (error) {
      response.status(error.code).send({ error: error.msg })
    }
  }

  async function getUserDetails (request, response) {
    response.render('UserDetails')
  }

  // groups
async function getGroupByName(request, response){
  try{
    const token = request.user.token
  const groups = await services.getAllGroups(token)
  const gName = request.body.name
  const group = groups.find(it=> it.name == gName)

    if (group == undefined) return {name: 'Error', data:{code:400, msg:`Movie with name ${gName} not found!`}}

  return {name: 'group', data: {
    id: group.id,
    name: group.name,
    description: group.description,
    movies: group.movies
  }
  }
}
  catch(e){
    return {name: 'Error', data:{code:e.code, msg:e.msg}}
  }
}
  async function getGroup (request, response) {
    try {
      const id = request.body.id
      const token = request.user.token //request.header('Authorization')
      const group = await services.getGroup(id, token)
      return {
        name: 'group',
        data: {
          id: group.id,
          name: group.name,
          description: group.description,
          movies: group.movies
        }
      }
    } catch (error) {
      return { name: 'Error', data: error }
    }
  }

  async function createGroup (request, response) {
    try {
      const id = crypto.randomUUID()
      const name = request.body.name
      const description = request.body.description
      const token = request.user.token
      const group = await services.createGroup({ id, name, description }, token)
      return {
        name: 'group',
        data: {
          id: group.id,
          name: group.name,
          description: group.description,
          movies: group.movies
        }
      }
    } catch (error) {
      console.log(error)
      return { name: 'Error', data: {msg:error.msg, code: error.code }
    }
  }
}

  async function getAllGroups (request, response) {
    try {
      const token = request.user.token
      const groups = await services.getAllGroups(token)
      return { name: 'allGroups', data: { groups } }
    } catch (error) {
      return { name: 'Error', data: error }
    }
  }

  async function updateGroup (request, response) {
    try {
      const id = request.body.id
      const newName = request.body.newName
      const newDesc = request.body.newDesc
      const token = request.user.token //request.header('Authorization')
      const group = await services.updateGroup(id, newName, newDesc, token)
      return {
        name: 'group',
        data: {
          id: group.id,
          name: group.name,
          description: group.description,
          movies: group.movies
        }
      }
    } catch (error) {
      return { name: 'Error', data: error }
    }
  }

  async function deleteGroup (request, response) {
    try {
      const id = request.body.id
      const token = request.user.token
      await services.deleteGroup(id, token)
      response.redirect('/home/groups')
    } catch (error) {
      return { name: 'Error', data: error }
    }
  }

  async function addMovietoGroup (request, response) {
    try {
      const movieId = request.body.movieId
      const groupId = request.body.groupId
      const token = request.user.token //request.header('Authorization')
      const addedMovie = await services.addMovieToGroup(groupId, movieId, token)
      return {
        name: 'group',
        data: {
          id: addedMovie.id,
          name: addedMovie.name,
          description: addedMovie.description,
          movies: addedMovie.movies
        }
      }
    } catch (error) {
      return { name: 'Error', data: error }
    }
  }

  async function removeMoviefromGroup (request, response) {
    try {
      const groupid = request.body.groupId
      const movieId = request.body.movieId
      const token = request.user.token 
      const removedMovie = await services.removeMovieFromGroup(
        groupid,
        movieId,
        token
      )
      return {
        name: 'group',
        data: {
          id: removedMovie.id,
          name: removedMovie.name,
          description: removedMovie.description,
          movies: removedMovie.movies
        }
      }
    } catch (error) {
      return { name: 'Error', data: error }
    }
  }

  // movies

  async function getPopularMovies (request, response) {
    try {
      const movies = await services.getPopularMovies(request.query.nr)
      return { name: 'showMovies', data: { movies } }
    } catch (error) {
      return { name: 'Error', data: error }
    }
  }

  async function getMovie (request, response) {
    try {
      const id = request.params.id
      const movies = await services.getMovieById(id)
      return { name: 'showMovies', data: { movies: [movies] } }
    } catch (error) {
      return { name: 'Error', data: error }
    }
  }

  async function getMovieNames (request, response) {
    try {
      const movie = request.params.moviename
      const movies = await services.getMoviesByName(movie, request.query.nr)
      return { name: 'showMovies', data: { movies } }
    } catch (error) {
      return { name: 'Error', data: error }
    }
  }
 // User
 async function createUser(request, response){
  try{
      const token = crypto.randomUUID()
      const username = request.body.username
      const password = request.body.password
      const user = await services.addUser(token, username, password)
      if (user == undefined){
        return {name: 'Error', data: {msg: 'Couldnt Create User', code:400}}
      }
      response.redirect('/home')
  }
   catch(e)
   {
    response.render('Error', {code:400, msg:'Coudnt Create Account'})
   }
 }
 
  async function getCreateUser (request, response) {
    response.render('CreateUser')
  }
  async function getLogin (request, response) {
    response.render('UserLogin')
  }

  async function getMovies (request, response) {
    response.render('movies')
  }

  async function getGroups (request, response) {
    response.render('groups')
  }

  async function getLoginForm (request, response) {
    response.render('UserLogin')
  }

  async function logout (request, response) {
    response.render('UserLogout')
  }

  async function getValidateLogin (request, response) {
    const user = await checkUser(request.body.id, request.body.name)
    if (user == undefined) {
      response.render('UserLogin')
      return
    }

    authentication = true
    if (user._source != undefined)
      request.login(user._source, () => response.redirect('/home'))
    else 
      request.login(user, () => response.redirect('/home'))
  }

  async function checkUser (id, name) {
    const check = await services.checkUserInfo(id, name)
    return check
  }

  function logout (req, rsp) {
    authentication = false
    req.logout(err => {
      rsp.redirect('/users/login')
    })
    
  }

  function validateUser(req, res) {
    return req.user
  }

  function handleRequest(handler) {
    return async function (req, res) {
      try {
        const validate = validateUser(req, res)
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
