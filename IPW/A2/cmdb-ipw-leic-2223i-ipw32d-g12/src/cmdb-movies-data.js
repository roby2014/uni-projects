// get movies data using the imdb api

import * as dotenv from 'dotenv'
dotenv.config()
const apiKey = process.env.API_KEY

export async function getPopularMovies (limit) {
  const data = await fetch(`https://imdb-api.com/en/API/Top250Movies/${apiKey}`)
  const movies = await data.json()
  return movies.items.slice(0, limit)
}

export async function getMoviesByName (name, limit) {
  const data = await fetch(`https://imdb-api.com/API/Search/${apiKey}/${name}`)
  const movies = await data.json()
  return movies.results.slice(0, limit)
}
