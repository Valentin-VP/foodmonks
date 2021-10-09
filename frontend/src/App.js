import logo from './logo.svg';
import './App.css';
import {
  BrowserRouter,
  Switch,
  Route,
  Link
} from "react-router-dom";
import  LoginPage from './pages/LoginPage';
import { Dashboard } from './pages/dashboard/dashboard';
import { ShowUsers } from './pages/showUsers/showUsers';
import ResetPassword from './pages/passwordRecovery/PasswordReset';
import ConfirmPassword from './pages/passwordRecovery/PasswordConfirm';


function App() {
  return (
      <BrowserRouter>
        <Switch>
          <Route exact path="/" component={LoginPage}/>
          <Route exact path="/dashboard" component={Dashboard}/>
          <Route exact path="/showUsers" component={ShowUsers}/>
          {/*<Route exact path="/forgot" component={ResetPassword}/>
          <Route exact path="/reset/:token?" component={ConfirmPassword}/>*/}
        </Switch>
      </BrowserRouter>
  );
}

export default App;
