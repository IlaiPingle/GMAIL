import React, { Component } from "react";
import "./SideBar.css";
import IconButton from "../common/IconButton";
import SideBarOptions from "./SideBarOptions";
import { useNavigate, useLocation } from 'react-router-dom';

// HOC for navigation hooks in class component
function withNavigation(Component) {
    return function ComponentWithNavigation(props) {
        const navigate = useNavigate();
        const location = useLocation();
        return <Component {...props} navigate={navigate} location={location} />;
    };
}

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
            const res = await fetch('http://localhost:8080/api/labels', {
                headers: { 'user-id': '2' }
            });
            if (res.ok) {
                const data = await res.json();
                this.setState({ labels: data });
            }
        } catch (err) {
            console.error('Error fetching labels', err);
        }
    };

    render() {
        const { navigate, location } = this.props;
        const { labels } = this.state;
        
        // Get current path for highlighting active option
        const currentPath = location.pathname;

        return (
            <div className="sideBar">
                <SideBarOptions 
                    id="compose" 
                    icon="edit"
                    text="Compose"
                    isActive={currentPath === '/compose'}
                    onClick={() => navigate('/compose')}
                />
                <SideBarOptions 
                    icon="inbox"
                    text="Inbox"
                    isActive={currentPath === '/'}
                    onClick={() => navigate('/')}
                />
                <SideBarOptions 
                    icon="star_border"
                    text="Starred"
                    isActive={false}
                    onClick={() => console.log("Starred clicked")}
                />
                <SideBarOptions 
                    icon="send"
                    text="Sent"
                    isActive={false}
                    onClick={() => console.log("Sent clicked")}
                />
                <SideBarOptions 
                    icon="draft"
                    text="Drafts"
                    isActive={false}
                    onClick={() => console.log("Drafts clicked")}
                />
                <SideBarOptions 
                    icon="delete"
                    text="Trash"
                    isActive={false}
                    onClick={() => console.log("Trash clicked")}
                />
                <SideBarOptions 
                    icon="archive"
                    text="Archive"
                    isActive={false}
                    onClick={() => console.log("Archive clicked")}
                />
                <div className="LabelsHeader">
                    <span className="LabelsHeaderText">Labels</span>
                    <IconButton onClick={() => navigate('/create-label')} iconType="material">
                        add
                    </IconButton>
                </div>
                {/* Render dynamic labels */}
                {labels.map(label => (
                    <SideBarOptions
                        key={label.id}
                        icon="label"
                        text={label.name}
                        isActive={false}
                        onClick={() => navigate(`/?label=${label.id}`)}
                    />
                ))}
            </div>
        );
    }
}

export default withNavigation(SideBar);