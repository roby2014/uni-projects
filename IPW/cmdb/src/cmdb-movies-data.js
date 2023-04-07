// Get movies data using the imdb api (small wrapper)

import { CMDBError } from './errors.js'
import fetch from "node-fetch";

import * as dotenv from 'dotenv'
dotenv.config()
const apiKey = process.env.API_KEY

export async function getPopularMovies(limit) {
  try {
    const data = await fetch(`https://imdb-api.com/en/API/Top250Movies/${apiKey}`)
    const movies = await data.json()
    return movies.items.slice(0, limit)
  } catch (e) {
    console.log(e);
    throw CMDBError.INTERNAL_ERROR("Could not fetch API...")
  }
}

export async function getMovieById(id) {
  try {
    const data = await fetch(`https://imdb-api.com/en/API/Title/${apiKey}/${id}`)
    const movie = await data.json()
    return movie
  } catch (e) {
    console.log(e);
    throw CMDBError.INTERNAL_ERROR("Could not fetch API...")
  }
}

export async function getMoviesByName(name, limit) {
  try {
    const data = await fetch(`https://imdb-api.com/API/Search/${apiKey}/${name}`)
    const movies = await data.json()
    return movies.results.slice(0, limit)
  } catch (e) {
    console.log(e);
    throw CMDBError.INTERNAL_ERROR("Could not fetch API...")
  }
}
