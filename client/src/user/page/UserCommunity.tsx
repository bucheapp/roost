import React, { useEffect, useState, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { AuthContext } from "../../AuthContext.ts";
import { fetchWithAuth } from "../../utils/fetchWithAuth.ts";
import UserSidebar from "../components/UserSidebar.tsx";
import { useIsMobile } from "../../utils/useIsMobile.ts";
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

		const url = isMe
			? `${baseURL}/api/users/me/communities?page=0&size=${size}&sort=createdAt,desc`
			: `${baseURL}/api/users/${publicId}/communities?page=0&size=${size}&sort=createdAt,desc`;

		fetcher(url)
			.then((res) => {
				if (!res.ok) throw new Error("failed");
				return res.json();
			})
			.then((data: CommunitiesResponse) => {
				setCommunities(data.communities ?? []);
			})
			.catch((err) => console.error(err))
			.finally(() => setLoading(false));
	}, [auth, isMe, navigate, publicId]);

	if (loading) return <div>Loading...</div>;

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

			<div className="main">
				{isMobile && !isOpen && (
					<button
						className="open-btn"
						onClick={() => setIsOpen(true)}
					>
						☰
					</button>
				)}

				<div className="content">
					<h2 className="title">所属コミュニティ</h2>

					<div className="community-list">
						{communities.length === 0 ? (
							<p>コミュニティがありません</p>
						) : (
							communities.map((c) => (
								<div
									key={c.publicId}
									className="community-card"
									onClick={() => navigate(`/test/${c.publicId}`)}
								>
									<div className="community-name">{c.name}</div>
									<div className="community-meta">
										<span>{c.type}</span>
										<span>{c.state}</span>
									</div>
									<div className="community-date">
										{new Date(c.createdAt).toLocaleDateString()}
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