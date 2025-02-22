import { BackgroundKey, STORY_BACKGROUND } from "@/constants";
import { cn } from "@/lib/utils";
import { FC, useEffect, useRef } from "react";

interface IProps {
    value: string,
    background: BackgroundKey,
    setFieldValue: any,
    placeholder?: string
};

const EditableDiv: FC<IProps> = ({ value, background, setFieldValue, placeholder }) => {
    const divRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        if (divRef.current && value !== divRef.current.textContent) {
            divRef.current.textContent = value;
        }
    }, [value]);

    const handleInput = () => {
        if (divRef.current) {
            const content = divRef.current.textContent || "";
            setFieldValue("content", content);
        }
    };

    const handleParentClick = () => {
        if(divRef.current)
            divRef.current.focus()
    }

    return (
        <div className={cn("w-full p-2 min-h-[300px] flex my-4 rounded-md items-center justify-center", STORY_BACKGROUND[background])}>
            <div
                ref={divRef}
                contentEditable
                suppressContentEditableWarning
                onInput={handleInput}
                onClick={handleParentClick}
                className={"w-full h-fit outline-none whitespace-pre-wrap break-words text-white text-center font-semibold text-lg"}
                data-placeholder={placeholder}
            >
            </div>
        </div>
    );
};

export default EditableDiv;