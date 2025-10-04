import React,{useEffect,useRef} from "react";
import { useModal } from "../../contexts/ModalContext";
import Client from "../../services/Client";
import './EditLabelModal.css';
function LabelMenu({ anchorRect, labelName, onClose }) {
    const { open } = useModal();
    const ref = useRef();

    useEffect(() => {
        const handleClickOutside = (e) => {
            if (ref.current && !ref.current.contains(e.target)) {
                onClose();
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [onClose]);
    
    const handleDelete = async () => {
        if(!window.confirm(`Delete label "${labelName}"?`)) return;
        try {
            await Client.deleteLabel(labelName);
            window.dispatchEvent(new CustomEvent('label:created', { detail: labelName }));
            onClose();
        } catch (err) {
            alert(err.message);
        }
    };

    return (
        <div 
            className="label-menu-popover"
            ref={ref}
            style={{
                position: "fixed",
                top: anchorRect.top,
                left: anchorRect.right + 8,
            }}>
            <div className="label-menu-item" onClick={()=>{ open("createLabel", { labelToEdit: labelName });}}>
                <p>Edit label</p>
            </div>
            <div className="label-menu-item" onClick={handleDelete}>
                <p>Delete label</p>
            </div>
        </div>
    )

}
export default LabelMenu;