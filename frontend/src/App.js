import React from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import Stream from './components/Stream';
import CreatePost from './components/CreatePost';
import Header from './components/Header';
import './App.css';

function App() {
  const { isAuthenticated, isLoading } = useAuth0();

  if (isLoading) {
    return (
      <div className="loading">
        <p>Loading...</p>
      </div>
    );
  }

  return (
    <div className="App">
      <Header />
      <div className="stream-container">
        {isAuthenticated && <CreatePost />}
        <Stream />
      </div>
    </div>
  );
}

export default App;
