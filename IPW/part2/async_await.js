import fetch from 'node-fetch'
import { promises as fs } from 'fs'

const key = 'k_1dus00v8'

// Will store the final object result
const result = {
  total_duration: 0,
  movies: []
}

/// run "main" function
run()

/// Read file, get movie ids, get each movie information, store in the new JSON file
async function run () {
  const data = await fs.readFile('./movie_ids.json', 'binary')
  const movie_ids = JSON.parse(data)['movie-ids']

  for (const movie_id of movie_ids) {
    const movie = await get_movie_info(movie_id)
    result.movies.push({
      id: movie.id,
      title: movie.title,
      duration: Number(movie.runtimeMins)
    })
    result.total_duration += Number(movie.runtimeMins)
  }

  await fs.writeFile('./result_async_await.json', JSON.stringify(result))
  console.log('Done...')
}

/// Returns JSON data info about [movie_id]
async function get_movie_info (movie_id) {
  const movie = await fetch(`https://imdb-api.com/API/Title/${key}/${movie_id}`)
  return movie.json()
}
