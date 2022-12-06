// Application Entry Point.
// Register all HTTP API routes and starts the server.

import express from 'express'

import * as dotenv from 'dotenv'
dotenv.config()

import * as groupsData from './cmdb-data-mem.js'
import * as moviesData from './cmdb-movies-data.js'
import servicesInit from './cmdb-services.js'
import apiInit from './cmdb-web-api.js'
import yaml from 'yamljs'
import swaggerUi from 'swagger-ui-express'

const services = servicesInit(groupsData, moviesData)
const api = apiInit(services)
const swagger = yaml.load('./docs/cmdb-api.yaml')

const app = express()
app.use(express.json())
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swagger))

// movies

app.get('/popularmovies', api.getPopularMovies)
app.get('/movies/:moviename', api.getMovieNames)

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

// start server

app.listen(process.env.PORT, () => {
  if (!process.env.PORT || !process.env.API_KEY) {
    throw Error('Configure your environment variables (PORT and API_KEY)')
  }

  console.log(`api running on localhost:${process.env.PORT}`)
})
