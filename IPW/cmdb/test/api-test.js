import { expect } from 'chai'
import express from 'express'
import request from 'supertest'

import { CMDBError } from '../src/errors.js'

import * as groupsData from '../src/cmdb-data-mem.js'
import * as moviesData from '../src/cmdb-movies-data-mem.js'
import servicesInit from '../src/cmdb-services.js'
import apiInit from '../src/cmdb-web-api.js'

const services = servicesInit(groupsData, moviesData)
const api = apiInit(services)

const g_token = '3fa85f64-5717-4562-b3fc-2c963f66afa6'

const app = express()
app.use(express.json())

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

describe('API Tests', () => {
  describe('movies routes tests', function () {
    it('get popular movies without limit', () => {
      return request(app)
        .get('/popularmovies')
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(response => {
          const movies = response.body.movies
          expect(movies.length).to.be.above(0)
          expect(movies.length).to.equal(250)
        })
    })

    it('get popular movies with limit', () => {
      return request(app)
        .get('/popularmovies?nr=3')
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(response => {
          const movies = response.body.movies
          expect(movies.length).to.be.above(0)
          expect(movies.length).to.equal(3)
        })
    })

    it('get movie by id', () => {
      return request(app)
        .get('/movies/tt0111161')
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(response => {
          const movie = response.body
          expect(movie['id']).to.be.equal('tt0111161')
          expect(movie['rank']).to.be.equal('1')
          expect(movie['title']).to.be.equal('The Shawshank Redemption')
          expect(movie['fullTitle']).to.be.equal(
            'The Shawshank Redemption (1994)'
          )
          expect(movie['crew']).to.be.equal(
            'Frank Darabont (dir.), Tim Robbins, Morgan Freeman'
          )
        })
    })

    it('error on get movie because invalid id', () => {
      const err = CMDBError.MOVIE_NOT_FOUND(54542)

      return request(app)
        .get('/movies/54542')
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(err.code)
        .then(response => {
          expect(response.body.error).to.be.equal(err.msg)
        })
    })

    it('get movie by name ', () => {
      return request(app)
        .get('/movies/search/Shawshank')
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(response => {
          expect(response.body.movies.length).to.be.above(0)
        })
    })

    it('get movie by invalid name ', () => {
      return request(app)
        .get('/movies/search/iasdfksadkfasfd')
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(response => {
          expect(response.body.movies.length).to.be.equal(0)
        })
    })
  })

  describe('groups routes tests', function () {
    it('get all groups', () => {
      return request(app)
        .get('/group')
        .set('Authorization', g_token)
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(response => {
          expect(response.body.groups.length).to.be.above(0)
        })
    })

    it('get group by id', () => {
      return request(app)
        .get('/group/2')
        .set('Authorization', g_token)
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(response => {
          expect(response.body).to.eql({
            userId: 11,
            id: 2,
            name: 'ola',
            description: 'xd'
          })
        })
    })

    it('error on get group because does not exist', () => {
      const err = CMDBError.GROUP_NOT_FOUND(2000)

      return request(app)
        .get('/group/2000')
        .set('Authorization', g_token)
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(err.code)
        .then(response => {
          expect(response.body.error).to.be.equal(err.msg)
        })
    })

    it('create group', () => {
      const groupToCreate = {
        id: 666000,
        name: '666000',
        description: '666000'
      }

      return request(app)
        .post('/group')
        .set('Authorization', g_token)
        .set('Accept', 'application/json')
        .set('Content-Type', 'application/json')
        .send(groupToCreate)
        .expect(200)
        .then(response => {
          groupToCreate.userId = 11 // g_token's user ID
          expect(response.body).to.be.eql(groupToCreate)
        })
    })

    it('error on create group because already exists', () => {
      const groupToCreate = {
        id: 666000,
        name: '666000',
        description: '666000'
      }

      const err = CMDBError.GROUP_ALREADY_EXISTS(groupToCreate.id)

      return request(app)
        .post('/group')
        .set('Authorization', g_token)
        .set('Accept', 'application/json')
        .set('Content-Type', 'application/json')
        .send(groupToCreate)
        .expect(err.code)
        .then(response => {
          expect(response.body.error).to.be.equal(err.msg)
        })
    })

    it('delete group', () => {
      return request(app)
        .delete('/group/4')
        .set('Authorization', g_token)
        .set('Accept', 'application/json')
        .expect(200)
    })

    it('error on delete group because does not exist', () => {
      const err = CMDBError.GROUP_NOT_FOUND(18881)
      return request(app)
        .delete('/group/18881')
        .set('Authorization', g_token)
        .set('Content-Type', 'application/json')
        .set('Accept', 'application/json')
        .expect(err.code)
        .then(response => {
          expect(response.body.error).to.be.equal(err.msg)
        })
    })

    it('update group', () => {
      return request(app)
        .put('/group/2')
        .set('Authorization', g_token)
        .set('Content-Type', 'application/json')
        .set('Accept', 'application/json')
        .send({
          newName: 'hahaxd',
          newDesc: 'ok'
        })
        .expect(200)
        .then(response => {
          expect(response.body).to.be.eql({
            userId: 11,
            id: '2',
            name: 'hahaxd',
            description: 'ok'
          })
        })
    })

    it('error on update group because id does not exist', () => {
      const err = CMDBError.GROUP_CANT_UPDATE(11231231)
      return request(app)
        .put('/group/11231231')
        .set('Authorization', g_token)
        .set('Content-Type', 'application/json')
        .set('Accept', 'application/json')
        .send({
          newName: 'hahaxd',
          newDesc: 'ok'
        })
        .expect(err.code)
        .then(response => {
          expect(response.body.error).to.be.equal(err.msg)
        })
    })

  })

  describe('user routes tests', function () {
    it('get user', () => {
      return request(app)
        .get('/user/11')
        .set('Accept', 'application/json')
        .set('Content-Type', 'application/json')
        .expect(200)
        .then(response => {
          expect(response.body).to.be.eql({
            id: 11,
            name: 'Filipe',
            token: '3fa85f64-5717-4562-b3fc-2c963f66afa6'
          })
        })
    })

    it('error on get user does not exist', () => {
      const err = CMDBError.USER_NOT_FOUND(111)
      return request(app)
        .get('/user/111')
        .set('Accept', 'application/json')
        .set('Content-Type', 'application/json')
        .expect(err.code)
        .then(response => {
          expect(response.body.error).to.be.equal(err.msg)
        })
    })

    it('create user', () => {
      const user = {
        id: 1234566,
        name: 'roby'
      }
      return request(app)
        .post('/user')
        .set('Accept', 'application/json')
        .set('Content-Type', 'application/json')
        .send(user)
        .expect(200)
        .then(response => {
          expect(response.body['id']).to.be.eql(1234566)
          expect(response.body['name']).to.be.eql('roby')
          // token is randomly generated
        })
    })

    it('create user already exists', () => {
      const err = CMDBError.USER_ALREADY_EXISTS(1234566)
      const user = {
        id: 1234566,
        name: 'roby',
        token: '123'
      }
      return request(app)
        .post('/user')
        .set('Accept', 'application/json')
        .set('Content-Type', 'application/json')
        .send(user)
        .expect(err.code)
        .then(response => {
          expect(response.body.error).to.be.equal(err.msg)
        })
    })
  })

  describe('group movies manipulation', function () {
    it('add movie to group', () => {
      return request(app)
        .put('/group/1/movie/tt0111161')
        .set('Authorization', g_token)
        .set('Accept', 'application/json')
        .set('Content-Type', 'application/json')
        .expect(200)
    })

    it('remove movie from group', () => {
      return request(app)
        .delete('/group/1/movie/tt0111161')
        .set('Authorization', g_token)
        .set('Accept', 'application/json')
        .set('Content-Type', 'application/json')
        .expect(200)
    })

  })

  describe('groups routes tests with no authorization', function () {
    const err = CMDBError.NOT_AUTHORIZED()

    it('get all groups', () => {
      return request(app)
        .get('/group')
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(err.code)
        .then(response => {
          expect(response.body.error).to.be.equal(err.msg)
        })
    })

    it('get group by id', () => {
      return request(app)
        .get('/group/2')
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(err.code)
        .then(response => {
          expect(response.body.error).to.be.equal(err.msg)
        })
    })

    it('create group', () => {
      const groupToCreate = {
        id: 666000,
        name: '666000',
        description: '666000'
      }

      return request(app)
        .post('/group')
        .set('Accept', 'application/json')
        .set('Content-Type', 'application/json')
        .send(groupToCreate)
        .expect(err.code)
        .then(response => {
          expect(response.body.error).to.be.equal(err.msg)
        })
    })

    it('delete group', () => {
      return request(app)
        .delete('/group/666000')
        .set('Accept', 'application/json')
        .expect(err.code)
        .then(response => {
          expect(response.body.error).to.be.equal(err.msg)
        })
    })

    it('update group', () => {
      return request(app)
        .put('/group/1')
        .set('Content-Type', 'application/json')
        .set('Accept', 'application/json')
        .send({
          newName: 'hahaxd',
          newDesc: 'ok'
        })
        .expect(err.code)
        .then(response => {
          expect(response.body.error).to.be.equal(err.msg)
        })
    })

    
  })

 
})
