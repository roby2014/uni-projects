/// Returns a function that takes a list of valid users as param,
/// and returns true if all of the supplied users exist in the original list of users,
/// or false otherwise.
export default function checkUsersValid (goodUsers) {
  return users => {
    for (const usr of users) {
      const found = goodUsers.find(u => usr.id == u.id)
      if (found == undefined) return false
    }
    return true
  }
}
