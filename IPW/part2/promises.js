import fetch from 'node-fetch'
import fs from 'fs'

const key = 'k_1dus00v8'

// Will store the final object result
const result = {
  total_duration: 0,
  movies: []
}

/// Reads the input file, processes and stores the data
fs.readFile('./movie_ids.json', (error, data) => {
  if (error) throw err

  Promise.all(process_data(data))
    .then(results => store_results(results))
    .catch(error => console.error(error))
})

/// This function receives [data], which should be the array with the movie ids.
/// It will create a promise for each movie information request
/// Returns an array of promises
function process_data (data) {
  let promises_arr = []

  JSON.parse(data)['movie-ids'].forEach(element => {
    promises_arr.push(
      get_movie_info(element)
        .then(response => {
          const obj = {
            id: response.id,
            title: response.title,
            duration: Number(response.runtimeMins)
          }
          return obj
        })
        .catch(err => {
          throw err
        })
    )
  })

  return promises_arr
}

/// Returns a promise, that returns the JSON data about the [movie_id] movie
function get_movie_info (movie_id) {
  return fetch(`https://imdb-api.com/API/Title/${key}/${movie_id}`)
    .then(response => {
      return response.json()
    })
    .catch(err => {
      throw err
    })
}

/// Receives API results and produces the expected JSON file with
/// each movie id, title, duration, and the total duration of those movies
function store_results (results) {
  results.forEach(movie => {
    result.total_duration += movie.duration
    result.movies.push(movie)
  })

  fs.writeFile('result_promises.json', JSON.stringify(result), error => {
    if (error) return console.error(error)
    console.log('Done...')
  })
}
