import React, { useState, useEffect, useContext } from "react";
import { useNavigate, useParams } from "react-router-dom";
import "./Community.css";
import Sidebar from "../components/Sidebar.tsx";
import { AuthContext } from "../../AuthContext.ts";
import { fetchWithAuth } from "../../utils/fetchWithAuth.ts";
import { useIsMobile } from "../../utils/useIsMobile.ts";
import styles from "./Community.module.css";

import RoomSidebar from "../components/RoomSidebar";
import MemberSidebar from "../components/MemberSidebar";
import ChatArea from "../components/ChatArea";

const baseURL = import.meta.env.VITE_API_URL;

const Community: React.FC = () => {
    const { communityPublicId, roomPublicId } = useParams();

    const [isOpen, setIsOpen] = useState(false);
    const isMobile = useIsMobile();
    const navigate = useNavigate();
    const auth = useContext(AuthContext);

    const [iconUrl, setIconUrl] = useState<string>(
        baseURL + "/icons/default_icon.jpg"
    );
    const [menuOpen, setMenuOpen] = useState(false);
    const [isLoggedIn, setIsLoggedIn] = useState<boolean | null>(null);

    const [community, setCommunity] = useState<any>(null);
    const [rooms, setRooms] = useState<any[]>([]);
    const [members, setMembers] = useState<any[]>([]);
    const [memberOpen, setMemberOpen] = useState(false);

    useEffect(() => {
        const checkAuth = async () => {
            try {
                const res = await fetch(baseURL + "/api/auth/me", {
                    method: "GET",
                    credentials: "include",
                });

                if (!res.ok) {
                    setIsLoggedIn(false);
                    return;
                }

                const text = await res.text();
                const data = text ? JSON.parse(text) : null;

                if (data == null) {
                    setIsLoggedIn(false);
                } else {
                    setIsLoggedIn(true);
                }
            } catch (err) {
                console.error(err);
                setIsLoggedIn(false);
            }
        };

        checkAuth();
    }, [auth]);

    useEffect(() => {
        if (!auth || !isLoggedIn) return;

        const fetcher = (url: string) =>
            fetchWithAuth(url, {}, auth.accessToken, auth.setAccessToken);

        fetcher(baseURL + "/api/users/me/profile")
            .then(res => {
                if (res.status === 404) return null;
                return res.json();
            })
            .then(data => {
                if (!data) return;
                if (data.iconUUID) {
                    setIconUrl(baseURL + "/icons/" + data.iconUUID + ".jpg");
                }
            })
            .catch(err => console.error(err));
    }, [auth, isLoggedIn]);

    useEffect(() => {
        if (!auth || !communityPublicId) return;

        const fetcher = (url: string) =>
            fetchWithAuth(url, {}, auth.accessToken, auth.setAccessToken);

        fetcher(`${baseURL}/api/communities/${communityPublicId}`)
            .then(res => res.json())
            .then(setCommunity)
            .catch(console.error);

        fetcher(`${baseURL}/api/communities/${communityPublicId}/rooms`)
            .then(res => res.json())
            .then(data => setRooms(data.rooms))
            .catch(console.error);

        fetcher(`${baseURL}/api/communities/${communityPublicId}/members`)
            .then(res => res.json())
            .then(data => setMembers(
                data.members.map((m: any) => ({
                    ...m,
                    publicId: BigInt(m.publicId)
                }))
            ))
            .catch(console.error);

    }, [auth, communityPublicId]);

    if (isLoggedIn === null) {
        return <div>Loading...</div>;
    }

    return (
        <div className="layout">
            <Sidebar
                isOpen={isOpen}
                isMobile={isMobile}
                onClose={() => setIsOpen(false)}
            />

            {isOpen && isMobile && (
                <div className="overlay" onClick={() => setIsOpen(false)} />
            )}

            <div className="main">
                {isMobile && !isOpen && (
                    <button
                        className="open-sidebar-btn"
                        onClick={() => setIsOpen(true)}
                    >
                        ☰
                    </button>
                )}

                <div className="content">
                    {!community ? (
                        <div>Loading...</div>
                    ) : (
                        <>
                            <div className="community-header">
                                <h2>{community.name}</h2>

                                <div className="header-actions">
                                    <button onClick={() => setMemberOpen(true)}>
                                        メンバー
                                    </button>

                                    <button
                                        onClick={() =>
                                            navigate(`/community/${communityPublicId}/settings`)
                                        }
                                    >
                                        設定
                                    </button>
                                </div>
                            </div>

                            <div className="community-body">
                                <RoomSidebar
                                    rooms={rooms}
                                    selectedRoomId={roomPublicId}
                                    onSelect={(roomId: number) =>
                                        navigate(
                                            `/community/${communityPublicId}/room/${roomId}`
                                        )
                                    }
                                />

                                <ChatArea roomPublicId={roomPublicId} />
                            </div>
                        </>
                    )}
                </div>

                {memberOpen && (
                    <MemberSidebar
                        members={members}
                        onClose={() => setMemberOpen(false)}
                    />
                )}

                {!memberOpen && (
                    <div
                        className={styles.user_icon_wrapper}
                        onMouseEnter={() => isLoggedIn && setMenuOpen(true)}
                        onMouseLeave={() => setMenuOpen(false)}
                    >
                        <img
                            src={iconUrl}
                            className={styles.avatar}
                            alt="user icon"
                            onClick={() => {
                                if (!isLoggedIn) {
                                    navigate("/login");
                                }
                            }}
                        />

                        {menuOpen && isLoggedIn && (
                            <div className={styles.dropdown}>
                                <div
                                    className={styles.item}
                                    onClick={() => navigate("/user/me/profile")}
                                >
                                    プロフィール
                                </div>
                                <div
                                    className={styles.item}
                                    onClick={() => navigate("/user/me/community")}
                                >
                                    コミュニティ
                                </div>
                                <div
                                    className={styles.item}
                                    onClick={() => navigate("/user/me/securiy")}
                                >
                                    セキュリティ
                                </div>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default Community;