// Application Entry Point.
// Register all HTTP API routes and starts the server.

import express from 'express'

import * as dotenv from 'dotenv'
dotenv.config()

import * as groupsData from './cmdb-data-mem.js'
//import * as groupsData from './cmdb-data-elastic.js'
import * as moviesData from './cmdb-movies-data-mem.js' // use this if no more api requests available
// import * as moviesData from './cmdb-movies-data.js'

import servicesInit from './cmdb-services.js'
import apiInit from './cmdb-web-api.js'
import websiteInit from './site/cmdb-web-site.js'
import passport from 'passport'
import yaml from 'yamljs'
import swaggerUi from 'swagger-ui-express'
import session from 'express-session'
import fileStore from 'session-file-store'
import hbs from 'hbs'
import { fileURLToPath } from 'url'
import path from 'path'

// dependency injection
const services = servicesInit(groupsData, moviesData)
const api = apiInit(services)
const ui = websiteInit(services, api)

// create express app
const app = express()
app.use(express.json())

// view
const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)
const viewPath = path.join(__dirname, 'site', 'public')
app.set('views', viewPath)
app.set('view engine', 'hbs')
hbs.registerPartials(viewPath + '/partials')

// swagger
const swagger = yaml.load('./docs/cmdb-api.yaml')
const fileStorage = fileStore(session)
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swagger))
app.use(
  session({
    secret: 'Anything',
    store: new fileStorage(),
    resave: false,
    saveUninitialized: false
  })
)

// passport
app.use(passport.session())
app.use(passport.initialize())
app.use(express.urlencoded({ extended: false }))
passport.serializeUser(serializeUserDeserializeUser)
passport.deserializeUser(serializeUserDeserializeUser)

function serializeUserDeserializeUser (user, done) {
  done(null, user)
}

// ------- api -------

// movies
app.get('/popularmovies', api.getPopularMovies)
app.get('/movies/:id', api.getMovie)
app.get('/movies/search/:moviename', api.getMovieNames)

// groups
app.get('/group', api.getAllGroups)
app.get('/group/:id', api.getGroup)
app.post('/group', api.createGroup)
app.put('/group/:id', api.updateGroup)
app.delete('/group/:id', api.deleteGroup)
app.put('/group/:groupId/movie/:movieId', api.addMovieToGroup)
app.delete('/group/:groupId/movie/:movieId', api.removeMovieFromGroup)

// user
app.get('/user/:userId', api.getUser)
app.post('/user', api.createUser)

// ------- web ui -------
app.post('/user/create', ui.createUser)
// home
app.get('/', ui.getHome)
app.get('/home', ui.getHome)
app.get('/site.css', ui.getCss)
app.get('/login', ui.getLogin)
app.get('/users/createUser', ui.getCreateUser)
app.get('/home/movies', ui.getMovies)
app.get('/home/groups', ui.getGroups)
app.get('/home/userDetails', ui.getUserDetails)

// movies view
app.get('/popular/movies', ui.getPopularMovies)
app.get('/id/movies/:id', ui.getMovie)
app.get('/name/movies/:moviename', ui.getMovieNames)

// group view
app.post('/groups/delete', ui.deleteGroup)
app.post('/groups/update', ui.updateGroup)
app.post('/groups/:id/movies/add', ui.addMovietoGroup)
app.post('/groups/:id/movies/delete', ui.removeMoviefromGroup)
app.get('/groups/all', ui.getAllGroups)
app.post('/groups/find', ui.getGroup)
app.post('/groups/create', ui.createGroup)
app.post('/groups/name/find', ui.getGroupByName)

// login
app.get('/users/login', ui.getLoginForm)
app.post('/users/login', ui.getValidateLogin)
app.get('/users/logout', ui.logout)

// start server
app.listen(process.env.PORT, () => {
  if (!process.env.PORT || !process.env.API_KEY) {
    throw Error('Configure your environment variables (PORT and API_KEY)')
  }

  console.log(`api running on localhost:${process.env.PORT}`)
})
