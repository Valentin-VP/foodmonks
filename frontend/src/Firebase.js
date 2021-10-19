import firebase from "firebase/compat/app";
import "firebase/compat/storage";

const firebaseConfig = {
  apiKey: "AIzaSyDvrThrk2nY5W72KxDIGzJWfknCT6NCCVA",
  authDomain: "foodmonks-70c28.firebaseapp.com",
  projectId: "foodmonks-70c28",
  storageBucket: "foodmonks-70c28.appspot.com",
  messagingSenderId: "655150989440",
  appId: "1:655150989440:web:22d752a743e8df5e592665",
};


firebase.initializeApp(firebaseConfig);

const storage = firebase.storage();

export { storage, firebase as default };
