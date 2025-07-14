import React, { useState, useEffect } from "react";
import "./SideBar.css";
import IconButton from "../common/IconButton";
import SideBarOptions from "./SideBarOptions";
import { useNavigate } from 'react-router-dom';

function SideBar() {
    const navigate = useNavigate();
    const [labels, setLabels] = useState([]);

    // Fetch labels on mount
    useEffect(() => {
      const fetchLabels = async () => {
        try {
          const res = await fetch('http://localhost:8080/api/labels', {
            headers: { 'user-id': '2' }
          });
          if (res.ok) {
            const data = await res.json();
            setLabels(data);
          }
        } catch (err) {
          console.error('Error fetching labels', err);
        }
      };
      fetchLabels();
    }, []);

    // Open Create Label modal via state
    const openCreateLabel = () => {
      navigate('/', { state: { newLabel: true } });
    };

    return (
        <div className="sideBar">
            <SideBarOptions 
                id="compose" 
                icon="bi bi-pencil"
                text="Compose"
                onClick={() => navigate('/', { state: { compose: true } })}
            />
            <SideBarOptions 
                icon="bi bi-inbox"
                text="Inbox"
                onClick={() => navigate('/')}
            />
            <SideBarOptions 
                icon="bi bi-star"
                text="Starred"
                onClick={() => console.log("Starred clicked")}
            />
            <SideBarOptions 
                icon="bi bi-send"
                text="Sent"
                onClick={() => console.log("Sent clicked")}
            />
            <SideBarOptions 
                icon="bi bi-file-earmark"
                text="Drafts"
                onClick={() => console.log("Drafts clicked")}
            />
            <SideBarOptions 
                icon="bi bi-trash"
                text="Trash"
                onClick={() => console.log("Trash clicked")}
            />
            <SideBarOptions 
                icon="bi bi-archive"
                text="Archive"
                onClick={() => console.log("Archive clicked")}
            />
            <div className="LabelsHeader">
                <span className="LabelsHeaderText">Labels</span>
                <IconButton onClick={openCreateLabel}>
                    bi bi-plus
                </IconButton>
            </div>
            {/* Render dynamic labels */}
            {labels.map(label => (
              <SideBarOptions
                key={label.id}
                icon="bi bi-tags"
                text={label.name}
                onClick={() => navigate(`/?label=${label.id}`)}
              />
            ))}
        </div>
    );

}

function handleCompose() {
    console.log("Compose clicked");
}
export default SideBar;