import update from 'react-addons-update';

import {
  REQUEST_USERS, RECEIVE_USERS, RECEIVE_USER, INVALIDATE_USERS
} from '../actions'

function users(
  state = {
	  isFetching: false,
	  didInvalidate: false,
	  users: []
	},
	action
) {
  switch (action.type) {
    case INVALIDATE_USERS:
      return Object.assign({}, state, {
        didInvalidate: true
      })
    case REQUEST_USERS:
      return Object.assign({}, state, {
	        isFetching: true,
	        didInvalidate: false
        })
    case RECEIVE_USERS:
      return Object.assign({}, state, {
		    isFetching: false,
		    didInvalidate: false,
		    users: action.users,
		    lastUpdated: action.receivedAt
      })
    case RECEIVE_USER:
      let users = state.users
      const userIdx = users.findIndex(u => u.id === action.user.id)
      let newUsers
      if (userIdx < 0) {
        newUsers = update(users, {
          $push: [ action.user]
        });
        newUsers.sort((a, b) => {
          return a.username.localeCompare(b.username)
        })
      } else {
        newUsers = update(users, {
          $splice: [[userIdx, 1, action.user]]
        });
      }
      return Object.assign({}, state, {
        users: newUsers,
        lastUpdated: action.receivedAt
      })
    default:
      return state
  }
}

export default users