import './App.css';
import React from 'react';
import {
  BrowserRouter,
  Switch,
  Route,
  Link
} from "react-router-dom";
import  LoginPage from './pages/LoginPage';
import { Principal } from './pages/principal/principal';
/*import ResetPassword from './pages/passwordRecovery/PasswordReset';
import ConfirmPassword from './pages/passwordRecovery/PasswordConfirm';*/


function App() {
  return (
      <BrowserRouter>
        <Switch>
          <Route exact path="/" component={LoginPage}/>
          <Route exact path="/principal" component={Principal}/>
          {/*<Route exact path="/forgot" component={ResetPassword}/>
          <Route exact path="/reset/:token?" component={ConfirmPassword}/>*/}
        </Switch>
      </BrowserRouter>
  );
}

export default App;
