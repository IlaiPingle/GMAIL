import React, { useState, useEffect } from "react";
import "./SideBar.css";
import IconButton from "../common/IconButton";
import SideBarOptions from "./SideBarOptions";
import { useNavigate, useLocation } from 'react-router-dom';
import Client from "../../services/Client";

const SYSTEM_LABELS = ['inbox','starred', 'sent', 'drafts','all mail','spam', 'bin'];

function SideBar() {
    const [labels, setLabels] = useState([]);
    const navigate = useNavigate();
    const location = useLocation();

    // Fetch labels on mount
    useEffect(() => {
        fetchLabels();
    }, []);

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
      <div className="sideBar">
        <SideBarOptions
          id="compose"
          icon="edit"
          text="Compose"
          isActive={false}
          onClick={() => { 
            const params = new URLSearchParams(location.search);
            params.set("compose", "new");
            navigate(`${currentPath}?${params.toString()}`);
          }}
        />
        <SideBarOptions
          icon="inbox"
          text="Inbox"
          isActive={currentPath === "/inbox"}
          onClick={() => navigate("/inbox")}
        />
        <SideBarOptions
          icon="star_border"
          text="Starred"
          isActive={currentPath === "/starred"}
          onClick={() => navigate("/starred")}
        />
        <SideBarOptions
          icon="send"
          text="Sent"
          isActive={currentPath === "/sent"}
          onClick={() => navigate("/sent")}
        />
        <SideBarOptions
          icon="draft"
          text="Drafts"
          isActive={currentPath === "/drafts"}
          onClick={() => navigate("/drafts")}
        />
          <SideBarOptions
            icon="stacked_email"
            text="all mail"
            isActive={currentPath === "/all"}
            onClick={() =>navigate("/all")}
          />
          <SideBarOptions
            icon="report"
            text="Spam"
            isActive={currentPath === "/spam"}
            onClick={() => navigate("/spam")}
          />
        <SideBarOptions
          icon="delete"
          text="Bin"
          isActive={currentPath === "/bin"}
          onClick={() => navigate("/bin")}
        />
        <div className="LabelsHeader">
          <span className="LabelsHeaderText">Labels</span>
          <IconButton onClick={() => navigate("/create-label")}>
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
            onClick={() => navigate(`/label/${encodeURIComponent(label)}`)}
          />
        ))}
      </div>
    );
}

export default SideBar;