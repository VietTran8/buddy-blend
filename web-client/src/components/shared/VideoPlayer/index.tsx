import { ChangeEvent, FC, memo, useEffect, useRef, useState } from "react";
import { Fullscreen } from "lucide-react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPause, faPlay, faVolumeHigh } from "@fortawesome/free-solid-svg-icons";
import { Spin } from "antd";
import "./VideoPlayer.css";

interface IProps {
    className?: string;
    src: string;
};

const VideoPlayer: FC<IProps> = ({ className, src }) => {
    const [progress, setProgress] = useState<string>('0%');
    const [volume, setVolume] = useState<number>(1);
    const [isMouseDown, setIsMouseDown] = useState<boolean>(false);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [isLoaded, setIsLoaded] = useState<boolean>(false);

    const playerRef = useRef<HTMLDivElement | null>(null);
    const videoRef = useRef<HTMLVideoElement | null>(null);

    const handleLoadStart = () => {
        setIsLoading(true);
    };

    const handleCanPlay = () => {
        setIsLoading(false);
    };

    useEffect(() => {
        const video = videoRef.current;

        if (!video) return;

        const handleUpdate = () => {
            video.addEventListener('timeupdate', handleProgress);
        };

        const handleScroll = () => {
            const rect = video.getBoundingClientRect();
            const isVideoVisible = rect.top >= 0 && rect.bottom <= window.innerHeight;

            if (!isVideoVisible && !video.paused) {
                video.pause();
            }

            if (isVideoVisible) {
                video.play();
            }
        };

        ['pause', 'play'].forEach(event => {
            video.addEventListener(event, handleUpdate);
        });

        window.addEventListener('scroll', handleScroll);

        return () => {
            ['pause', 'play'].forEach(event => {
                video.removeEventListener(event, handleUpdate);
            });
            window.removeEventListener('scroll', handleScroll);
        };
    }, []);

    useEffect(() => {
        const videoElement = videoRef.current;

        if (!videoElement) return;

        const observer = new IntersectionObserver(
            ([entry]) => {
                if (entry.isIntersecting) {
                    setIsLoaded(true);
                    observer.unobserve(videoElement);
                }
            },
            {
                rootMargin: '200px',
                threshold: 0.25,
            }
        );

        observer.observe(videoElement);

        return () => observer.unobserve(videoElement);
    }, []);

    const handleFullscreen = () => {
        if (!playerRef.current) return;

        if (document.fullscreenElement) {
            document.exitFullscreen();
        } else if (playerRef.current.requestFullscreen) {
            playerRef.current.requestFullscreen();
        }
    };

    const togglePlay = () => {
        const video = videoRef.current;
        if (video) {
            const method = video.paused ? 'play' : 'pause';
            video[method]();
        }
    };

    const handleProgress = () => {
        const video = videoRef.current;
        if (video) {
            const percent = (video.currentTime / video.duration) * 100;
            setProgress(`${percent}%`);
        }
    };

    const handleRangeUpdate = (e: ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        const video = videoRef.current;

        if (video) {
            if (name === 'volume') {
                setVolume(parseFloat(value));
                video.volume = parseFloat(value);
            }
        }
    };

    const scrub = (e: React.MouseEvent<HTMLDivElement, MouseEvent>) => {
        const video = videoRef.current;
        const progressDiv = e.currentTarget as HTMLDivElement;

        if (video && progressDiv) {
            const rect = progressDiv.getBoundingClientRect();
            const offsetX = e.clientX - rect.left;
            const scrubTime = (offsetX / rect.width) * video.duration;

            if (!isNaN(scrubTime)) {
                video.currentTime = scrubTime;
            }
        }
    };

    return (
        <div ref={playerRef} className={`player ${className} group`}>
            <video
                className="player__video viewer"
                ref={videoRef}
                src={isLoaded ? src : undefined}
                onClick={togglePlay}
                onLoadStart={handleLoadStart}
                onCanPlay={handleCanPlay}
                onWaiting={handleLoadStart}
            />

            <div className="w-full h-full absolute top-0 left-0 flex items-center justify-center">
                {isLoading ? <Spin size="large" /> : (
                    <div onClick={togglePlay} className={`group-hover:opacity-100 transition-all h-14 w-14 cursor-pointer rounded-full flex items-center justify-center bg-overlay-dark ${videoRef.current && videoRef.current.paused ? 'opacity-100' : 'opacity-0'}`}>
                        {videoRef.current && videoRef.current.paused ? <FontAwesomeIcon className="text-white text-3xl" icon={faPlay} /> : <FontAwesomeIcon className="text-white text-3xl" icon={faPause} />}
                    </div>
                )}
            </div>

            {!isLoading && (
                <div className={`player__controls group-hover:translate-y-0`}>
                    <div
                        className={`progress group-hover:h-[6px]`}
                        onMouseDown={() => setIsMouseDown(true)}
                        onMouseUp={() => setIsMouseDown(false)}
                        onMouseLeave={() => setIsMouseDown(false)}
                        onMouseMove={(e) => isMouseDown && scrub(e)}
                        onClick={scrub}
                    >
                        <div
                            className="progress__filled"
                            style={{ flexBasis: progress }}
                        ></div>
                    </div>

                    <div className="flex items-center gap-x-1">
                        <FontAwesomeIcon icon={faVolumeHigh} className="text-lg text-white" />
                        <input
                            type="range"
                            name="volume"
                            className="player__slider max-w-[80px]"
                            min="0"
                            max="1"
                            step="0.05"
                            value={volume}
                            onChange={handleRangeUpdate}
                        />
                    </div>

                    <div className="flex items-center flex-1 justify-end">
                        <Fullscreen onClick={handleFullscreen} className="text-white ms-3 cursor-pointer hover:text-[--primary-color] transition" />
                    </div>
                </div>
            )}
        </div>
    );
};

export default memo(VideoPlayer);
