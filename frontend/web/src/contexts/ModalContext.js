import { createContext , useContext ,useState} from "react";
import LabelMenu from "../components/modals/EditLabelModal";
import CreateLabel from "../components/modals/CreateLabel";
const ModalContext = createContext();

export const useModal = () => useContext(ModalContext);

function ModalProvider({children}) {
    const [modal, setModal] = useState(null);
    console.log("modal state", modal);
    const open = (type, props = {}) => {setModal({type, props});};
    const close = () => {setModal(null);};

    return (
        <ModalContext.Provider value={{open, close }}>
            {children}
            {modal?.type === "createLabel" && (
                <CreateLabel {...modal.props} onClose={close} />
            )}
            {modal?.type === "labelMenu" && (
                <LabelMenu {...modal.props} onClose={close} />
            )}
        </ModalContext.Provider>
    );
}
export default ModalProvider;