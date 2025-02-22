import { FC, useCallback, useContext, useState } from 'react';
import FilerobotImageEditor, {
    TABS,
    TOOLS,
} from 'react-filerobot-image-editor';
import { CreateImageStoryForm, CreateTextStoryForm, Header } from '../../components';
import { Avatar, Breadcrumb } from 'antd';
import { Link } from 'react-router-dom';
import { X } from 'lucide-react';
import { AuthContext } from '../../context';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faImage, faTextWidth } from '@fortawesome/free-solid-svg-icons';
import { savedImageDataToFile } from '../../utils';
import { EStoryFont } from '../../types';
import { FILE_EDITOR_TRANSLATIONS, STORY_FONTS } from '../../constants';

interface IProps { };

const breadcrumbItems = [
    {
        title: <Link to="/">Trang chủ</Link>,
    },
    {
        title: <Link to="/story/create">Tạo tin</Link>,
    }
]

const CreateStory: FC<IProps> = ({ }) => {
    const { user } = useContext(AuthContext);
    const [selectedFile, setSelectedFile] = useState<File>();
    const [editedFile, setEditedFile] = useState<File | null>(null);
    const [storyOption, setStoryOption] = useState<undefined | "image" | "text">(undefined);
    const [storyProp, setStoryProp] = useState<{
        content: string;
        font: EStoryFont;
        background: string;
    }>({
        content: "Bắt đầu nhập",
        font: EStoryFont.FONT_NORMAL,
        background: 'bg-gradient-to-r from-red-400 to-pink-500'
    });

    const handleContentChange = useCallback((content: string) => {
        setStoryProp(prev => ({
            ...prev,
            content
        }));
    }, []);

    const handleFontChange = useCallback((font: EStoryFont) => {
        setStoryProp(prev => ({
            ...prev,
            font
        }));
    }, []);

    const handleBackgroundChange = useCallback((background: string) => {
        setStoryProp(prev => ({
            ...prev,
            background
        }));
    }, []);

    return (
        <div className="h-screen w-full">
            <div className="flex flex-col h-[100vh]">
                <Header />
                <div className="grid grid-cols-12 gap-4 h-full overflow-y-auto">
                    <div className="md:col-span-4 col-span-full p-5 bg-white md:block hidden">
                        <Breadcrumb items={breadcrumbItems} className="mb-2" />
                        <div className="flex items-center gap-x-3">
                            <Link to="/">
                                <div className="h-12 w-12 flex items-center bg-gray-100 hover:bg-gray-200 cursor-pointer transition-all justify-center rounded-full">
                                    <X size={20} className="text-gray-500" />
                                </div>
                            </Link>
                            <h1 className="lg:text-xl text-lg font-bold">Tạo tin mới</h1>
                        </div>
                        <div className="flex items-center gap-x-3 mt-4">
                            <Avatar size="large" src={user?.profilePicture || "/images/default-user.png"} />
                            <h1 className="font-semibold text-base -mb-1">{user?.userFullName}</h1>
                        </div>
                        {storyOption === "text" ? <CreateTextStoryForm
                            onBackgroundChange={handleBackgroundChange}
                            onFontChange={handleFontChange}
                            onContentChange={handleContentChange}
                            className='mt-5' /> : storyOption === "image" ? <CreateImageStoryForm editedFile={editedFile} className='mt-5' /> : <></>}
                    </div>
                    <div className="md:col-span-8 col-span-full overflow-y-auto">
                        {!storyOption ? <div className="flex justify-center gap-x-2 items-center w-full h-full">
                            <input accept='image/*' type='file' id='file' hidden onChange={(e) => {
                                if (e.target.files) {
                                    setSelectedFile(e.target.files[0]);
                                    setStoryOption("image");
                                }
                            }} />
                            <label htmlFor="file">
                                <div className="flex group items-center justify-center cursor-pointer rounded-md h-[400px] w-[260px] bg-gradient-to-r from-pink-500 to-yellow-500">
                                    <div className='flex flex-col gap-y-2 justify-center items-center'>
                                        <div className="h-14 w-14 group-hover:scale-110 transition-all shadow rounded-full bg-gray-200 flex items-center justify-center">
                                            <FontAwesomeIcon className='text-base text-gray-400' icon={faImage} />
                                        </div>
                                        <h1 className='text-white font-semibold text-base'>Tạo tin dạng ảnh</h1>
                                    </div>
                                </div>
                            </label>
                            <div onClick={() => {
                                setStoryOption("text");
                                setSelectedFile(undefined);
                            }} className="flex group items-center justify-center cursor-pointer rounded-md h-[400px] w-[260px] bg-gradient-to-b from-blue-300 to-blue-800">
                                <div className='flex flex-col gap-y-2 justify-center items-center'>
                                    <div className="h-14 w-14 group-hover:scale-110 transition-all shadow rounded-full bg-gray-200 flex items-center justify-center">
                                        <FontAwesomeIcon className='text-base text-gray-400' icon={faTextWidth} />
                                    </div>
                                    <h1 className='text-white font-semibold text-base'>Tạo tin dạng văn bản</h1>
                                </div>
                            </div>
                        </div> : <>
                            {storyOption === "image" ? !!selectedFile && !!!editedFile ? <FilerobotImageEditor
                                source={URL.createObjectURL(selectedFile)}
                                onSave={(editedImageObject) =>
                                    setEditedFile(savedImageDataToFile(editedImageObject))
                                }
                                onBeforeSave={() => true}
                                previewPixelRatio={4}
                                savingPixelRatio={4}
                                translations={FILE_EDITOR_TRANSLATIONS}
                                annotationsCommon={{
                                    fill: '#fff',
                                }}
                                Text={{ text: 'Bạn đang nghĩ gì...', fontSize: 25 }}
                                Rotate={{ angle: 90, componentType: 'slider' }}
                                tabsIds={[TABS.ADJUST, TABS.ANNOTATE, TABS.FILTERS, TABS.FINETUNE]}
                                defaultTabId={TABS.ANNOTATE}
                                defaultToolId={TOOLS.CROP}
                                theme={{
                                    palette: {
                                        'bg-primary-active': '#54b9ec',
                                        'bg-primary': '#039BE5',
                                        'icons-primary': "#039BE5",
                                        'borders-primary': "#039BE5",
                                        'borders-strong': "#039BE5",
                                        'accent-primary-active': "#fff",
                                        'accent-primary': '#039BE5'
                                    }
                                }}
                            /> : <div className='container'>
                                <div className="w-full bg-white p-5 h-[80vh] flex flex-col md:mt-10 shadow-sm rounded-md">
                                    <h1 className="font-semibold text-base mb-5">Xem trước</h1>
                                    <div className="rounded-lg flex items-center justify-center border flex-1 overflow-y-auto bg-zinc-900 custom-scroll">
                                        <img src={URL.createObjectURL(editedFile!)} className={`w-[30%] object-cover rounded-md flex items-center justify-center h-[95%] max-h-[550px]`}>
                                        </img>
                                    </div>
                                </div>
                            </div> :
                                <div className='container'>
                                    <div className="w-full bg-white p-5 h-[80vh] flex flex-col md:mt-10 shadow-sm rounded-md">
                                        <h1 className="font-semibold text-base mb-5">Xem trước</h1>
                                        <div className="rounded-lg flex items-center justify-center border flex-1 overflow-y-auto bg-zinc-900 custom-scroll">
                                            <div className={`w-[30%] rounded-md flex items-center justify-center h-[95%] max-h-[550px] ${storyProp.background}`}>
                                                <h1 className={`text-white text-lg w-[80%] text-center ${STORY_FONTS[storyProp.font]} text-wrap`}>{!!storyProp.content ? storyProp.content : "Bắt đầu nhập..."}</h1>
                                            </div>
                                        </div>
                                    </div>
                                </div>}
                        </>}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CreateStory;


