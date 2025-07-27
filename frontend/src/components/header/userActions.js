import React from 'react';
import IconButton from '../common/IconButton';
import './userActions.css';

function UserActions({ userName, userImage }) {
  const handleHelpClick = () => {
    console.log("Help-clicked");
  };

  const handleSettingsClick = () => {
    console.log("settings-clicked");
  };

  const handleAppsClick = () => {
    console.log("apps-clicked");
  };

  return (
    <div className="userActions">
      <IconButton 
        iconType="material"
        onClick={handleHelpClick}>
        help
      </IconButton>
      <IconButton 
        iconType='material' 
        onClick={handleSettingsClick}>
        settings
      </IconButton>
      <IconButton 
        iconType="material"
        onClick={handleAppsClick}>
        apps
      </IconButton>
      <button id="userImage" title={`${userName}@gmail.com`}>
        <img
          id="Avatar"
          src={userImage}
          alt={`${userName}@gmail.com`}
          className="userImage"
        />
      </button>
    </div>
  );
}

export default UserActions;