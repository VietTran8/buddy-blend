import { faSpinner } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { FC, HTMLAttributes, ReactNode } from "react";

interface IProps {
    children?: ReactNode;
    isLoading?: boolean;
    type?: "button" | "submit" | "reset"
};

const Button:FC<IProps & HTMLAttributes<HTMLButtonElement>> = ({ children, type, isLoading = false, ...props }) => {
    return <button {...props} type={type} disabled={isLoading} className="btn-primary-lg w-full mt-10">
        {isLoading && <FontAwesomeIcon icon={faSpinner} spin className="text-lg me-2"/>}
        {children}
    </button>
};

export default Button;