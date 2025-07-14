import React from "react";
import "./IconButton.css";

const IconButton = ({children,onClick}) => {
    return (
      <button className="iconButton" onClick={onClick}>
        <i class={children} style={{ fontSize: "20px", color: "#5f6368" }}></i>
      </button>
    );
}
export default IconButton;
