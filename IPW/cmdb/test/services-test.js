import { expect } from 'chai'

import { CMDBError } from '../src/errors.js'

import * as groupsData from '../src/cmdb-data-mem.js'
import * as moviesData from '../src/cmdb-movies-data-mem.js'
import servicesInit from '../src/cmdb-services.js'

const services = servicesInit(groupsData, moviesData)

// testing purposes, do not change!

const g_token = '3fa85f64-5717-4562-b3fc-2c963f66afa6'
const g_token2 = '3fa85f64-5717-4562-b3fc-2c963f66afa7'

describe('Services tests', function () {
  describe('groups tests', function () {
    it('should GET all groups', async function () {
      let groups = await services.getAllGroups(g_token)
      expect(groups.length).to.be.above(0)
    })

    it('should GET group by id', async function () {
      const group = await services.getGroup(5, g_token)
      expect(group).to.eql({
        userId: 11,
        id: 5,
        name: 'aaa',
        description: 'aaa'
      })
    })

    it('should CREATE a group and READ it', async function () {
      const groupToCreate = {
        id: 123,
        name: 'aaa',
        description: 'abc'
      }

      let groupCreated = await services.createGroup(groupToCreate, g_token)
      groupToCreate.userId = 11
      expect(groupToCreate).to.eql(groupCreated)

      groupCreated = await services.getGroup(123, g_token)
      expect(groupToCreate).to.eql(groupCreated)
    })

    it('should CREATE a group and DELETE it', async function () {
      const groupToCreate = {
        id: 1234,
        name: 'aaa',
        description: 'abc'
      }

      const groupCreated = await services.createGroup(groupToCreate, g_token)
      groupToCreate.userId = 11
      expect(groupToCreate).to.eql(groupCreated)

      const deleted = await services.deleteGroup(groupToCreate.id, g_token)
      expect(groupToCreate).to.eql(deleted)

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
        userId: 11
      }

      const created = await services.createGroup(groupToCreate, g_token)
      expect(created).to.eql(groupToCreate)

      expect(
        await services.updateGroup(groupToCreate.id, 'bbb', 'cba', g_token)
      ).to.eql({
        userId: 11,
        id: 12345,
        name: 'bbb',
        description: 'cba'
      })

      expect(await services.getGroup(groupToCreate.id, g_token)).to.eql({
        userId: 11,
        id: 12345,
        name: 'bbb',
        description: 'cba'
      })
    })

    it('should CREATE two groups with the same name and desc and user', async function () {
      // functional requirements from A3
      const groupToCreate = {
        id: 333444,
        name: 'aaa',
        description: 'abc',
        userId: 11
      }

      const groupToCreate2 = {
        id: 444333,
        name: 'aaa',
        description: 'abc',
        userId: 11
      }

      // create and check return

      const groupCreated = await services.createGroup(groupToCreate, g_token)
      const groupCreated2 = await services.createGroup(groupToCreate2, g_token)
      expect(groupToCreate).to.eql(groupCreated)
      expect(groupToCreate2).to.eql(groupCreated2)

      // read and check if successfull created

      const getGroup = await services.getGroup(groupToCreate.id, g_token)
      const getGroup2 = await services.getGroup(groupToCreate2.id, g_token)
      expect(groupToCreate).to.eql(getGroup)
      expect(groupToCreate2).to.eql(getGroup2)
    })

    it('should CREATE two groups with the same name and desc but different user', async function () {
      // functional requirements from A3
      const groupToCreate = {
        id: 333444555,
        name: 'aaa',
        description: 'abc',
        userId: 11
      }

      const groupToCreate2 = {
        id: 444333555,
        name: 'aaa',
        description: 'abc',
        userId: 12
      }

      // create and check return

      const groupCreated = await services.createGroup(groupToCreate, g_token)
      const groupCreated2 = await services.createGroup(groupToCreate2, g_token2)
      expect(groupToCreate).to.eql(groupCreated)
      expect(groupToCreate2).to.eql(groupCreated2)

      // read and check if successfull created

      const getGroup = await services.getGroup(groupToCreate.id, g_token)
      const getGroup2 = await services.getGroup(groupToCreate2.id, g_token2)
      expect(groupToCreate).to.eql(getGroup)
      expect(groupToCreate2).to.eql(getGroup2)
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
        await services.updateGroup(66611, 'test', undefined, g_token)
      } catch (err) {
        expect(err).to.eql(CMDBError.GROUP_CANT_UPDATE(66611))
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
        userId: 11
      }
      expect(await services.createGroup(groupToCreate, g_token)).to.eql(
        groupToCreate
      )

      const added = await services.addMovieToGroup(
        groupToCreate.id,
        'tt0111161',
        g_token
      )
      expect(added.movies.length).to.be.above(0)

      const group = await services.getGroup(groupToCreate.id, g_token)
      expect(group.movies.length).to.be.above(0)
      expect(group.movies.length).to.eql(1)
    })

    it('should CREATE group, ADD a movie, and then REMOVE the movie from it', async function () {
      const groupToCreate = {
        id: 444,
        name: 'aaa',
        description: 'abc',
        userId: 11
      }
      expect(await services.createGroup(groupToCreate, g_token)).to.eql(
        groupToCreate
      )

      await services.addMovieToGroup(groupToCreate.id, 'tt0111161', g_token)

      let group = await services.getGroup(groupToCreate.id, g_token)
      expect(group.movies.length).to.be.above(0)
      expect(group.movies.length).to.eql(1)

      await services.removeMovieFromGroup(groupToCreate.id, 'tt0111161', g_token)
      group = await services.getGroup(groupToCreate.id, g_token)
      expect(group.movies.length).to.eql(0)
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

    it('should GET find movies by id', async function () {
      const movie = await services.getMovieById('tt0111161')
      expect(movie['id']).to.be.equal('tt0111161')
      expect(movie['rank']).to.be.equal('1')
      expect(movie['title']).to.be.equal('The Shawshank Redemption')
      expect(movie['fullTitle']).to.be.equal('The Shawshank Redemption (1994)')
      expect(movie['crew']).to.be.equal(
        'Frank Darabont (dir.), Tim Robbins, Morgan Freeman'
      )
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
