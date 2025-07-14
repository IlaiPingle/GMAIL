import React from 'react';
import IconButton from '../common/IconButton';
import './userActions.css';
const userActions = ({userName,userImage}) => {
  return (
    <div className="userActions">
      <IconButton onClick={() => console.log("Help clicked")}>
        bi bi-question-circle
      </IconButton>
      <IconButton onClick={() => console.log("Help clicked")}>
        bi bi-gear
      </IconButton>
      <IconButton onClick={() => console.log("Help clicked")}>
        bi bi-grid-3x3-gap-fill
      </IconButton>
      <botton id="userImage" title={`${userName}@gmail.com`}>
        <img
          id="Avatar"
          src={userImage}
          alt={`${userName}@gmail.com`}
          className="userImage"
        />
      </botton>
    </div>
  );
}

export default userActions;