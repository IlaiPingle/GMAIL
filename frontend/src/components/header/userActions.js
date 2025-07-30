import React , {useState} from 'react';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../../contexts/UserContext'; 
import {useTheme} from '../../contexts/ThemeContext';
import IconButton from '../common/IconButton';
import Client from '../../services/Client';
import ThemeToggle from '../common/ThemeToggle';
import './userActions.css';

function UserActions({ userName, userImage }) {
  const [toggleSettings, setToggleSettings] = useState(false);
  const [toggleImage, setToggleImage] = useState(false);
  const { setUser } = useUser();
  const { darkMode, toggleTheme } = useTheme();
  const navigate = useNavigate();

  const handleToggleImage = () => {
    setToggleImage(!toggleImage);
  };

  const handleLogOut = async () => {
    try {
      await Client.logout();
      setUser(null);
      navigate('/login');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  const handleHelpClick = () => {
    console.log("Help-clicked");
  };

  const handleSettingsClick = () => {
    setToggleSettings(!toggleSettings);
  };

  const handleAppsClick = () => {
    console.log("apps-clicked");
  };

  return (
    <div className="userActions">
      <IconButton iconType="material" onClick={handleHelpClick}>
        help
      </IconButton>
      <IconButton iconType="material" onClick={handleSettingsClick}>
        settings
      </IconButton>
      {toggleSettings && (
        <div className="settingsDropdown">
          <button className="exitToggleSettings" onClick={handleSettingsClick}>
            ×
          </button>
          <div className="settingsOptions">
           <ThemeToggle />
          </div>
        </div>
      )}
      <IconButton iconType="material" onClick={handleAppsClick}>
        apps
      </IconButton>
      <button
        id="userImage"
        title={`${userName}@gmail.com`}
        onClick={handleToggleImage}
      >
        <img
          id="Avatar"
          src={userImage}
          alt={`${userName}@gmail.com`}
          className="userImage"
        />
      </button>
      {toggleImage && (
        <div className="userImageDropdown">
          <button className="exitToggleImg" onClick={handleToggleImage}>
            ×
          </button>
          <div className="userImageDetails">
            <img src={userImage} alt="Profile" />
            <div className="user-name">hello, {userName}!</div>
            <div className="user-email">{userName}@gmail.com</div>
          </div>
          <button className="logoutButton" onClick={handleLogOut}>
            Sign out
          </button>
        </div>
      )}
    </div>
  );
}

export default UserActions;