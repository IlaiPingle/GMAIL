import React, { Component } from "react";
import "./SideBar.css";
import IconButton from "../common/IconButton";
import SideBarOptions from "./SideBarOptions";
import { useNavigate, useLocation } from 'react-router-dom';
import Client from "../../services/Client";

// HOC for navigation hooks in class component
function withNavigation(Component) {
    return function ComponentWithNavigation(props) {
        const navigate = useNavigate();
        const location = useLocation();
        return <Component {...props} navigate={navigate} location={location} />;
    };
}

const SYSTEM_LABELS = ['inbox', 'sent', 'drafts', 'bin', 'archive', 'starred'];

class SideBar extends Component {
    constructor(props) {
        super(props);
        this.state = {
            labels: []
        };
    }

    // Fetch labels on mount
    componentDidMount() {
        this.fetchLabels();
    }

    fetchLabels = async () => {
        try {
            const data = await Client.getLabels();
            this.setState({ labels: data });
        } catch (err) {
            console.error('Error fetching labels', err);
        }
    };

    render() {
        const { navigate, location } = this.props;
        const { labels } = this.state;
        
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
              icon="delete"
              text="Bin"
              isActive={currentPath === "/bin"}
              onClick={() => navigate("/bin")}
            />
            <SideBarOptions
              icon="archive"
              text="Archive"
              isActive={currentPath === "/archive"}
              onClick={() =>navigate("/archive")}
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
}

export default withNavigation(SideBar);