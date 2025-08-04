import React from "react";
import "./SideBarOptions.css";
import IconButton from "../common/IconButton";


function SideBarOptions({ text, icon, isActive, isCompose, onClick,onOptionClick }) {
    const handleClick = () => {
        if (onClick) {
            onClick();
        }
    };
    return (
        <div className={`sideBarOption ${isActive ? 'selected' : ''} ${isCompose ? "compose" : ""}`} onClick={handleClick}>
            <span className={"material-symbols-outlined icon-sidebar"}>{icon}</span>
            <span className="sideBarText">{text}</span>
            <div className="icon-more-container">
            {icon ==='label' && <IconButton className="icon-more" onClick={(e) => { e.stopPropagation(); onOptionClick && onOptionClick(e); }} children="more_vert" />}
            </div>
        </div>
    );
}

export default SideBarOptions;