import React, { useState, useEffect } from "react";
import "./SideBar.css";
import IconButton from "../common/IconButton";
import SideBarOptions from "./SideBarOptions";
import { useNavigate, useLocation } from 'react-router-dom';
import Client from "../../services/Client";

const SYSTEM_LABELS = ['inbox','starred', 'sent', 'drafts','all mail','spam', 'bin'];
const boxMap = new Map([
      ["compose", "edit"],
      ["inbox", "inbox"],
      ["starred", "star_border"],
      ["sent", "send"],
      ["drafts", "draft"],
      ["all mail", "stacked_email"],
      ["spam", "report"],
      ["bin", "delete"],
    ]); 

function SideBar({ isOpen , onOpenCreateLabel, onLabelCreated ,resetLabelCreated }) {
    const [labels, setLabels] = useState([]);
    const navigate = useNavigate();
    const location = useLocation();

    // Fetch labels on mount
    useEffect(() => {
        fetchLabels();
    }, []);

    useEffect(() => {
      if (onLabelCreated) {
         fetchLabels();
        resetLabelCreated(false);
      }
    }, [onLabelCreated, resetLabelCreated]);

    
    const fetchLabels = async () => {
        try {
            const data = await Client.getLabels();
            setLabels(data);
        } catch (err) {
            console.error('Error fetching labels', err);
        }
    };

    const currentPath = location.pathname;

    return (
      <div className={`sideBar ${isOpen ? "open" : ""}`}>
        <SideBarOptions
          icon="edit"
          text="Compose"
          isActive={false}
          isCompose={true}
          onClick={() => { 
            const params = new URLSearchParams(location.search);
            params.set("compose", "new");
            navigate(`${currentPath}?${params.toString()}`);
          }}>
        </SideBarOptions>
        <div className="systemLabels">
          
          {SYSTEM_LABELS.map((label) => (
            <SideBarOptions
              key={label}
              icon={boxMap.get(label)}
              text={label.charAt(0).toUpperCase() + label.slice(1)}
              isActive={currentPath === `/${label.split(' ')[0]}`}
              isCompose={false}
              onClick={() => navigate(`/${label.split(' ')[0]}`)}
            />
          ))}
        </div>
        <div className="LabelsHeader">
          <span className="LabelsHeaderText">Labels</span>
          <IconButton onClick={onOpenCreateLabel}>
            add
          </IconButton>
        </div>
        {/* Render dynamic labels */}
        {labels.map((label) => (
          <SideBarOptions
            key={label}
            icon="label"
            text={label}
            isActive={currentPath === `/label/${encodeURIComponent(label)}`}
            isCompose={false}
            onClick={() => navigate(`/label/${encodeURIComponent(label)}`)}
          />
        ))}
      </div>
    );
}

export default SideBar;