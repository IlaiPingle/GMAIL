import React, { Component } from "react";
import "./IconButton.css";

class IconButton extends Component {
    renderIcon = () => {
        const { iconType = "bootstrap", children } = this.props;
        
        if (iconType === "material") {
            return <span className="material-symbols-outlined">{children}</span>;
        }
        else {
            // Default Bootstrap icons
            return <i className={children} style={{ fontSize: "20px", color: "#5f6368" }}></i>;
        }
    };

    render() {
        const { onClick } = this.props;
        
        return (
            <button className="iconButton" onClick={onClick}>
                {this.renderIcon()}
            </button>
        );
    }
}

export default IconButton;
