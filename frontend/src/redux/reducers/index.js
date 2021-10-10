import { combineReducers } from "redux";

import auth from './auth';
import resetPwd from './resetPwd'

export default combineReducers({
    auth, resetPwd
})