import { faEarthAmericas, faLock, faUserGroup } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Radio, RadioChangeEvent } from "antd";
import { FC, useState } from "react";
import { EPrivacy } from "../../../types";

interface IProps { 
    onPrivacyChange: (value: EPrivacy) => void;
};

const PrivacySelectionModalContent: FC<IProps> = ({ onPrivacyChange }) => {
    const [value, setValue] = useState<EPrivacy | undefined>(undefined);

    const onChange = (e: RadioChangeEvent) => {
        setValue(e.target.value);
        onPrivacyChange(e.target.value);
    };

    const handleOnSelect = (value: EPrivacy) => {
        setValue(value);
        onPrivacyChange(value);
    }

    return <div className="max-h-[80vh] overflow-y-auto no-scrollbar">
        <h2 className="text-lg font-semibold">Ai có thể xem bài viết của bạn?</h2>
        <p className="mt-2 text-gray-600">Tuy đối tượng mặc định là <span className="font-semibold text-gray-600">Chỉ mình tôi</span>, nhưng bạn có thể thay đổi đối tượng của riêng bài viết này.</p>
        <Radio.Group onChange={onChange} value={value} className="w-full">
            <div onClick={() => handleOnSelect(EPrivacy.PUBLIC)} className={`flex cursor-pointer hover:bg-gray-50 gap-x-3 w-full items-center py-2 px-3 mt-3 rounded-md transition-all ${value === EPrivacy.PUBLIC && 'bg-gray-50'}`}>
                <div className="rounded-full w-14 h-14 bg-gray-200 flex items-center justify-center">
                    <FontAwesomeIcon className="text-2xl text-gray-500" icon={faEarthAmericas}/>
                </div>
                <div className="flex-1">
                    <h4 className="text-base font-semibold">Công khai</h4>
                    <p className="text-sm text-gray-500">Bất kỳ ai trên hoặc ngoài Buddy Blend</p>
                </div>
                <Radio value={EPrivacy.PUBLIC}/>
            </div>
            <div onClick={() => handleOnSelect(EPrivacy.ONLY_FRIENDS)} className={`flex cursor-pointer hover:bg-gray-50 gap-x-3 w-full items-center py-2 px-3 mt-2 rounded-md transition-all ${value === EPrivacy.ONLY_FRIENDS && 'bg-gray-50'}`}>
                <div className="rounded-full w-14 h-14 bg-gray-200 flex items-center justify-center">
                    <FontAwesomeIcon className="text-2xl text-gray-500" icon={faUserGroup}/>
                </div>
                <div className="flex-1">
                    <h4 className="text-base font-semibold">Bạn bè</h4>
                    <p className="text-sm text-gray-500">Bạn bè của bạn trên Buddy Blend</p>
                </div>
                <Radio value={EPrivacy.ONLY_FRIENDS}/>
            </div>
            <div onClick={() => handleOnSelect(EPrivacy.PRIVATE)} className={`flex cursor-pointer hover:bg-gray-50 gap-x-3 w-full items-center py-2 px-3 mt-2 rounded-md transition-all ${value === EPrivacy.PRIVATE && 'bg-gray-50'}`}>
                <div className="rounded-full w-14 h-14 bg-gray-200 flex items-center justify-center">
                    <FontAwesomeIcon className="text-2xl text-gray-500" icon={faLock}/>
                </div>
                <div className="flex-1">
                    <h4 className="text-base font-semibold">Chỉ mình tôi</h4>
                    <p className="text-sm text-gray-500">Không ai có thể thấy bài viết này ngoại trừ bạn</p>
                </div>
                <Radio value={EPrivacy.PRIVATE}/>
            </div>
        </Radio.Group>
    </div>
};

export default PrivacySelectionModalContent;