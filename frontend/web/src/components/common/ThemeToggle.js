import React from "react";
import "./ThemeToggle.css";
import { useTheme } from "../../contexts/ThemeContext";

function ThemeToggle() {
  const { darkMode, toggleTheme } = useTheme();

  return (
    <div className="theme-toggle">
      <label className="theme-switch">
        <input type="checkbox" checked={darkMode} onChange={toggleTheme} />
        <span className="slider" />
      </label>
      <span className={`label-text ${darkMode ? "Dark" : ""}`}>
        {darkMode ? "Dark Mode" : "Light Mode"}
      </span>
    </div>
  );
}

export default ThemeToggle;
