import React, { Component } from "react";
import "./IconButton.css";

class IconButton extends Component {
    renderIcon = () => {
        const {children} = this.props;
            return <span className="material-symbols-outlined">{children}</span>;
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
