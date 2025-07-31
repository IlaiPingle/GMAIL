import React from "react";
import "./SideBarOptions.css";


function SideBarOptions({ text, icon, isActive, isCompose, onClick }) {
    const handleClick = () => {
        if (onClick) {
            onClick();
        }
    };
    return (
        <button className={`sideBarOption ${isActive ? 'selected' : ''} ${isCompose ? "compose" : ""}`} onClick={handleClick}>
            <span className={"material-symbols-outlined icon-sidebar"}>{icon}</span>
            <span className="sideBarText">{text}</span>
        </button>
    );
}

export default SideBarOptions;