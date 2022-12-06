import { expect } from 'chai'

import { CMDBError } from '../src/errors.js'

import * as groupsData from '../src/cmdb-data-mem.js'
import * as moviesData from '../src/cmdb-movies-data-mem.js'
import servicesInit from '../src/cmdb-services.js'

const services = servicesInit(groupsData, moviesData)
const g_token = '3fa85f64-5717-4562-b3fc-2c963f66afa6' // testing purposes

describe('Services tests', function () {
  describe('groups tests', function () {
    it('should GET all groups', async function () {
      let groups = await services.getAllGroups(g_token)
      expect(groups.length).to.be.above(0)
    })

    it('should GET group by id', async function () {
      const group = await services.getGroup(2, g_token)
      expect(group).to.eql({
        userId: 11,
        id: 2,
        name: 'ola',
        description: 'xd'
      })
    })

    it('should CREATE a group and READ it', async function () {
      const groupToCreate = {
        id: 123,
        name: 'aaa',
        description: 'abc',
        movies: []
      }

      let groupCreated = await services.createGroup(groupToCreate, g_token)
      expect(groupToCreate).to.equal(groupCreated)

      groupCreated = await services.getGroup(123, g_token)
      expect(groupToCreate).to.equal(groupCreated)
    })

    it('should CREATE a group and DELETE it', async function () {
      const groupToCreate = {
        id: 1234,
        name: 'aaa',
        description: 'abc',
        movies: []
      }

      const groupCreated = await services.createGroup(groupToCreate, g_token)
      expect(groupToCreate).to.equal(groupCreated)

      const deleted = await services.deleteGroup(groupToCreate.id, g_token)
      expect(groupToCreate).to.equal(deleted)

      try {
        await services.getGroup(groupToCreate.id, g_token)
      } catch (err) {
        expect(err).to.eql(CMDBError.GROUP_NOT_FOUND(groupToCreate.id))
      }
    })

    it('should CREATE a group and UPDATE it', async function () {
      const groupToCreate = {
        id: 12345,
        name: 'aaa',
        description: 'abc',
        movies: []
      }

      expect(await services.createGroup(groupToCreate, g_token)).to.equal(
        groupToCreate
      )

      expect(
        await services.updateGroup(groupToCreate.id, 'bbb', 'cba', g_token)
      ).to.eql({
        userId: 11,
        id: 12345,
        name: 'bbb',
        description: 'cba',
        movies: []
      })

      expect(await services.getGroup(groupToCreate.id, g_token)).to.eql({
        userId: 11,
        id: 12345,
        name: 'bbb',
        description: 'cba',
        movies: []
      })
    })

    it('should throw on READING because group does not exist', async function () {
      try {
        await services.getGroup(666, g_token)
      } catch (err) {
        expect(err).to.eql(CMDBError.GROUP_NOT_FOUND(666))
      }
    })

    it('should throw on DELETING because group does not exist', async function () {
      try {
        await services.deleteGroup(666, g_token)
      } catch (err) {
        expect(err).to.eql(CMDBError.GROUP_NOT_FOUND(666))
      }
    })

    it('should throw on UPDATING because group does not exist', async function () {
      try {
        await services.updateGroup(666, 'test', undefined, g_token)
      } catch (err) {
        expect(err).to.eql(CMDBError.GROUP_CANT_UPDATE(666))
      }
    })

    it('should throw on CREATING because invalid body', async function () {
      try {
        await services.createGroup({ id: 666, name: 'idk' }, g_token)
      } catch (err) {
        expect(err).to.eql(
          CMDBError.INVALID_BODY(`<id>;<name>;<description> needed`)
        )
      }

      try {
        await services.createGroup({ id: 666, description: 'idk' }, g_token)
      } catch (err) {
        expect(err).to.eql(
          CMDBError.INVALID_BODY(`<id>;<name>;<description> needed`)
        )
      }

      try {
        await services.createGroup({ id: 666 }, g_token)
      } catch (err) {
        expect(err).to.eql(
          CMDBError.INVALID_BODY(`<id>;<name>;<description> needed`)
        )
      }
    })

    it('should throw on CREATING because group already exists', async function () {
      try {
        await services.createGroup(
          { id: 1, name: 'a', description: 'b' },
          g_token
        )
      } catch (err) {
        expect(err).to.eql(CMDBError.GROUP_ALREADY_EXISTS(1))
      }
    })

    it('should throw on UPDATING because invalid body', async function () {
      try {
        await services.updateGroup(1, 'oi', undefined, g_token)
      } catch (err) {
        expect(err).to.eql(
          CMDBError.INVALID_BODY('at least <newName> or <newDesc> needed')
        )
      }

      try {
        await services.updateGroup(1, undefined, 'test', g_token)
      } catch (err) {
        expect(err).to.eql(
          CMDBError.INVALID_BODY('at least <newName> or <newDesc> needed')
        )
      }
    })
  })
  describe('group movies manipulation', function () {
    it('should CREATE group, and ADD a movie to it', async function () {
      const groupToCreate = {
        id: 555,
        name: 'aaa',
        description: 'abc',
        movies: []
      }
      expect(await services.createGroup(groupToCreate, g_token)).to.equal(
        groupToCreate
      )

      expect(
        await services.addMovieToGroup(groupToCreate.id, 1, g_token)
      ).to.equal(1)

      const group = await services.getGroup(groupToCreate.id, g_token)
      expect(group.movies.length).to.be.above(0)
      expect(group.movies.length).to.equal(1)
    })

    it('should CREATE group, ADD a movie, and then REMOVE the movie from it', async function () {
      const groupToCreate = {
        id: 444,
        name: 'aaa',
        description: 'abc',
        movies: []
      }
      expect(await services.createGroup(groupToCreate, g_token)).to.equal(
        groupToCreate
      )

      expect(
        await services.addMovieToGroup(groupToCreate.id, 1, g_token)
      ).to.equal(1)

      let group = await services.getGroup(groupToCreate.id, g_token)
      expect(group.movies.length).to.be.above(0)
      expect(group.movies.length).to.equal(1)

      await services.removeMovieFromGroup(groupToCreate.id, 1, g_token)
      group = await services.getGroup(groupToCreate.id, g_token)
      expect(group.movies.length).to.equal(0)
    })

    it('should throw on adding movie to group because group does not exist', async function () {
      try {
        await services.addMovieToGroup(4312451, 1, g_token)
      } catch (err) {
        expect(err).to.eql(CMDBError.GROUP_NOT_FOUND(4312451))
      }
    })

    it('should throw on removing movie from group because group does not exist', async function () {
      try {
        await services.removeMovieFromGroup(4312451, 1, g_token)
      } catch (err) {
        expect(err).to.eql(CMDBError.GROUP_NOT_FOUND(4312451))
      }
    })

    it('should throw on removing movie from group because movie does not exist', async function () {
      try {
        await services.removeMovieFromGroup(1, 4312451, g_token)
      } catch (err) {
        expect(err).to.eql(CMDBError.MOVIE_NOT_FOUND(4312451))
      }
    })

    it('should throw on adding movie to group because movie does not exist', async function () {
      try {
        await services.addMovieToGroup(1, 4312451, g_token)
      } catch (err) {
        expect(err).to.eql(CMDBError.MOVIE_NOT_FOUND(4312451))
      }
    })
  })
  describe('movies', function () {
    it('should GET popular movies', async function () {
      let movies = await services.getPopularMovies()
      expect(movies.length).to.be.above(0)
      expect(movies.length).to.equal(250)
    })

    it('should GET 10 popular movies', async function () {
      let movies = await services.getPopularMovies(10)
      expect(movies.length).to.be.above(0)
      expect(movies.length).to.equal(10)
    })

    it('should GET find movies by name', async function () {
      let movies = await services.getMoviesByName('Shawshank')
      expect(movies.length).to.be.above(0)
    })
  })
  describe('users', function () {
    it('should GET a user from memory', async function () {
      const user = await services.getUser(11)
      expect(user).to.eql({
        id: 11,
        name: 'Filipe',
        token: g_token
      })
    })

    it('should CREATE a user', async function () {
      const user = await services.addUser('123', 123, 'roby')
      expect(user).to.eql({
        id: 123,
        name: 'roby',
        token: '123'
      })
      expect(await services.getUser(123)).to.eql({
        id: 123,
        name: 'roby',
        token: '123'
      })
    })

    it('should throw on READING because user does not exit', async function () {
      try {
        await services.getUser(1337)
      } catch (err) {
        expect(err).to.eql(CMDBError.USER_NOT_FOUND(1337))
      }
    })

    it('should throw on CREATING because user already exists', async function () {
      try {
        await services.addUser('123', 11, 'roby')
      } catch (err) {
        expect(err).to.eql(CMDBError.USER_ALREADY_EXISTS(11))
      }
    })

    it('should throw on CREATING because invalid body', async function () {
      try {
        await services.addUser('123', 11)
      } catch (err) {
        expect(err).to.eql(CMDBError.INVALID_BODY('<id>;<name> needed'))
      }

      try {
        await services.addUser('123', undefined, 'roby')
      } catch (err) {
        expect(err).to.eql(CMDBError.INVALID_BODY('<id>;<name> needed'))
      }
    })
  })
  describe('group routes without auth', function () {
    it('get all groups not auth', async function () {
      try {
        await services.getAllGroups()
      } catch (err) {
        expect(err).to.eql(CMDBError.NOT_AUTHORIZED())
      }
    })
    it('get group not auth', async function () {
      try {
        await services.getGroup()
      } catch (err) {
        expect(err).to.eql(CMDBError.NOT_AUTHORIZED())
      }
    })
    it('create group not auth', async function () {
      try {
        await services.createGroup()
      } catch (err) {
        expect(err).to.eql(CMDBError.NOT_AUTHORIZED())
      }
    })
    it('update group not auth', async function () {
      try {
        await services.updateGroup()
      } catch (err) {
        expect(err).to.eql(CMDBError.NOT_AUTHORIZED())
      }
    })
    it('delete group not auth', async function () {
      try {
        await services.deleteGroup()
      } catch (err) {
        expect(err).to.eql(CMDBError.NOT_AUTHORIZED())
      }
    })
    it('add movie to group not auth', async function () {
      try {
        await services.addMovieToGroup()
      } catch (err) {
        expect(err).to.eql(CMDBError.NOT_AUTHORIZED())
      }
    })
    it('remove movie from group not auth', async function () {
      try {
        await services.removeMovieFromGroup()
      } catch (err) {
        expect(err).to.eql(CMDBError.NOT_AUTHORIZED())
      }
    })
  })
})
