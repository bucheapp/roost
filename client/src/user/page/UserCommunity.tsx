import React, { useEffect, useState, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { AuthContext } from "../../AuthContext.ts";
import { fetchWithAuth } from "../../utils/fetchWithAuth.ts";
import UserSidebar from "../components/UserSidebar.tsx";
import { useIsMobile } from "../../utils/useIsMobile.ts";
import styles from "./UserCommunity.module.css";
import "./UserCommunity.css";

type Community = {
	publicId: number;
	name: string;
	type: string;
	state: string;
	createdAt: string;
};

type CommunitiesResponse = {
	communities: Community[];
};

const baseURL = import.meta.env.VITE_API_URL;

const UserCommunity: React.FC = () => {
	const { publicId } = useParams();
	const isMe = !publicId;
	const navigate = useNavigate();
	const auth = useContext(AuthContext);

	const [communities, setCommunities] = useState<Community[]>([]);
	const [isOpen, setIsOpen] = useState(false);
	const [loading, setLoading] = useState(true);
	const [isFrozen, setIsFrozen] = useState(false);

	const isMobile = useIsMobile();

	useEffect(() => {
		if (isMe && !auth) {
			navigate("/login");
			return;
		}

		const fetcher = auth
			? (url: string) =>
				fetchWithAuth(url, {}, auth.accessToken, auth.setAccessToken)
			: (url: string) => fetch(url);

		const size = isMe ? 10 : 5;

		const userUrl = isMe
			? `${baseURL}/api/users/me`
			: `${baseURL}/api/users/${publicId}`;

		const communityUrl = isMe
			? `${baseURL}/api/users/me/communities?page=0&size=${size}&sort=createdAt,desc`
			: `${baseURL}/api/users/${publicId}/communities?page=0&size=${size}&sort=createdAt,desc`;

		fetcher(userUrl)
			.then(res => {
				if (!res.ok) throw new Error("user fetch failed");
				return res.json();
			})
			.then(userData => {
				if (!isMe && userData.state === "FROZEN") {
					setIsFrozen(true);
					setLoading(false);
					return;
				}

				return fetcher(communityUrl)
					.then(res => {
						if (!res.ok) throw new Error("community fetch failed");
						return res.json();
					})
					.then((data: CommunitiesResponse) => {
						setCommunities(data.communities ?? []);
					});
			})
			.catch(err => console.error(err))
			.finally(() => setLoading(false));
	}, [auth, isMe, navigate, publicId]);

	if (loading) return <div>Loading...</div>;

	if (isFrozen) {
		return <div>このユーザーの情報は現在閲覧できません</div>;
	}

	return (
		<div className="layout">
			<UserSidebar
				isOpen={isOpen}
				isMobile={isMobile}
				onClose={() => setIsOpen(false)}
			/>

			{isOpen && isMobile && (
				<div className="overlay" onClick={() => setIsOpen(false)} />
			)}

			<div className={styles.main}>
				{isMobile && !isOpen && (
					<button
						className="open-sidebar-btn"
						onClick={() => setIsOpen(true)}
					>
						☰
					</button>
				)}

				<div className={styles.content}>
					<h2 className={styles.title}>所属コミュニティ</h2>

					<div className="community-list">
						{communities.length === 0 ? (
							<p>コミュニティがありません</p>
						) : (
							communities.map((community) => (
								<div
									key={community.publicId}
									className="community-card"
									onClick={() => navigate(`/test/${community.publicId}`)}
								>
									<div className="community-name">
										{community.name}
									</div>
									<div className="community-meta">
										<span>{community.type}</span>
										<span>{community.state}</span>
									</div>
									<div className="community-date">
										{new Date(community.createdAt).toLocaleDateString()}
									</div>
								</div>
							))
						)}
					</div>
				</div>
			</div>
		</div>
	);
};

export default UserCommunity;