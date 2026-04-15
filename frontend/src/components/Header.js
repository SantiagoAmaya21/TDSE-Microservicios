import React from 'react';
import { useAuth0 } from '@auth0/auth0-react';

const Header = () => {
  const { user, isAuthenticated, loginWithRedirect, logout } = useAuth0();

  return (
    <header className="header">
      <div className="header-content">
        <div className="logo">
          TDSE Twitter
        </div>
        <div className="user-menu">
          {isAuthenticated ? (
            <>
              <div className="flex items-center space-x-3">
                {user?.picture && (
                  <img
                    src={user.picture}
                    alt={user.name}
                    className="w-8 h-8 rounded-full"
                  />
                )}
                <span className="text-sm font-medium text-gray-700">
                  {user?.name}
                </span>
              </div>
              <button
                onClick={() => logout({ logoutParams: { returnTo: window.location.origin } })}
                className="logout-button"
              >
                Logout
              </button>
            </>
          ) : (
            <button
              onClick={() => loginWithRedirect()}
              className="login-button"
            >
              Login
            </button>
          )}
        </div>
      </div>
    </header>
  );
};

export default Header;
