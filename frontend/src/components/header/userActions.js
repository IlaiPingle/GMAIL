import React, { Component } from 'react';
import IconButton from '../common/IconButton';
import './userActions.css';

class UserActions extends Component {
  handleHelpClick = () => {
    console.log("Help-clicked");
  };

  handleSettingsClick = () => {
    console.log("settings-clicked");
  };

  handleAppsClick = () => {
    console.log("apps-clicked");
  };

  render() {
    const { userName, userImage } = this.props;
    
    return (
      <div className="userActions">
        <IconButton 
          iconType="material"
          onClick={this.handleHelpClick}>
          help
        </IconButton>
        <IconButton 
          iconType='material' 
          onClick={this.handleSettingsClick}>
          settings
        </IconButton>
        <IconButton 
          iconType="material"
          onClick={this.handleAppsClick}>
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
}

export default UserActions;