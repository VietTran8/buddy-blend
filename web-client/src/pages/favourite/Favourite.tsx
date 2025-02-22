import { FC } from "react";
import { CollectionGrid } from "../../components";

interface IProps { };

const Favourite: FC<IProps> = ({ }) => {
    return (
        <div className="pb-10">
            <div className="p-5 bg-white rounded-md">
                <h1 className="font-bold lg:text-lg text-base">Bộ sưu tập yêu thích của bạn</h1>
            </div>
            <div className="mt-4 flex flex-col gap-y-3">
                <CollectionGrid />
            </div>
        </div>
    );
};

export default Favourite;