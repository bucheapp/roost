import React, { useEffect, useState, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { AuthContext } from "../AuthContext";
import { fetchWithAuth } from "../utils/fetchWithAuth";
import UserSidebar from "./components/UserSidebar";
import "./UserProfile.css";

type Profile = {
	name: string;
	email: string;
	bio: string;
	iconUrl: string;
	gender: string;
	dateOfBirth: string;
	address: string;
	githubUrl: string;
};

const baseURL = import.meta.env.VITE_API_URL;

function useIsMobile() {
	const [isMobile, setIsMobile] = useState(
		window.matchMedia("(max-width: 768px)").matches
	);

	useEffect(() => {
		const media = window.matchMedia("(max-width: 768px)");
		const listener = (e: MediaQueryListEvent) => setIsMobile(e.matches);
		media.addEventListener("change", listener);
		return () => media.removeEventListener("change", listener);
	}, []);

	return isMobile;
}

const UserProfile: React.FC = () => {
	const { publicId } = useParams();
	const isMe = !publicId;
	const navigate = useNavigate();

	const auth = useContext(AuthContext);

	const [profile, setProfile] = useState<Profile | null>(null);
	const [isOpen, setIsOpen] = useState(false);
	const [notFound, setNotFound] = useState(false);
	const isMobile = useIsMobile();

	useEffect(() => {
		const urlUser = isMe
			? "/api/users/me"
			: `/api/users/${publicId}`;

		const urlProfile = isMe
			? "/api/users/me/profile"
			: `/api/users/${publicId}/profile`;

		if (isMe && !auth) {
			navigate("/login");
			return;
		}

		const fetcher = auth
			? (url: string) =>
				fetchWithAuth(url, {}, auth.accessToken, auth.setAccessToken)
			: (url: string) => fetch(url);

		fetcher(baseURL + urlProfile)
			.then(res => {
				if (res.status === 404) {
					setNotFound(true);
					return null;
				}
				return res.json();
			})
			.then(profileData => {
				if (!profileData) return;

				fetcher(baseURL + urlUser)
					.then(res => {
						if (res.status === 404) {
							setNotFound(true);
							return null;
						}
						return res.json();
					})
					.then(userData => {
						if (!userData) return;

						setProfile({
							...profileData,
							name: userData.name,
							email: userData.email,
						});
					})
					.catch(err => {
						console.error(err);
						if (isMe) navigate("/login");
					});
			})
			.catch(err => {
				console.error(err);
				if (isMe) navigate("/login");
			});
	}, [auth, navigate, isMe, publicId]);

	if (notFound) {
		return <div>ユーザーが見つかりません</div>;
	}

	if (!profile) return <div>Loading...</div>;

	const formatGender = (gender: string | null | undefined) => {
		switch (gender) {
			case "MALE":
				return "男性";
			case "FEMALE":
				return "女性";
			case "OTHER":
				return "その他";
			default:
				return "(設定されてません)";
		}
	};

	const displayOrDefault = (value: string | null | undefined) =>
		value && value.trim() !== "" ? value : "(設定されてません)";

	return (
		<div className="layout">
			<UserSidebar
				isOpen={isOpen}
				isMobile={isMobile}
				onClose={() => setIsOpen(false)}
			/>
			{isOpen && isMobile && <div className="overlay" onClick={() => setIsOpen(false)} />}

			<div className="main">
				<button
					className="open-btn"
					onClick={() => setIsOpen(true)}
					style={{ display: isMobile && !isOpen ? "block" : "none" }}
				>
					☰
				</button>

				<div className="content">
					<div className="profile-header">
						<img
							src={profile.iconUrl ? baseURL + "/icons/" + profile.iconUrl + ".jpg" : "/default-avatar.jpg"}
							className="avatar"
						/>
						<div>
							<h2>{displayOrDefault(profile.name)}</h2>
							{isMe && (
								<>
									<p>Email: {displayOrDefault(profile.email)}</p>
									<p>パスワード: ********</p>
									<button
										className="edit-btn"
										onClick={() => navigate("/user/me/edit")}
									>
										編集
									</button>
								</>
							)}
						</div>
					</div>

					<div className="section">
						<h3>自己紹介</h3>
						<p>{displayOrDefault(profile.bio)}</p>
					</div>

					<div className="section grid">
						<div>性別: {formatGender(profile.gender)}</div>
						<div>
							誕生日:{" "}
							{profile.dateOfBirth
								? new Date(profile.dateOfBirth).toLocaleDateString()
								: "(設定されてません)"}
						</div>
						<div>住所: {displayOrDefault(profile.address)}</div>
						<div>Github: {displayOrDefault(profile.githubUrl)}</div>
					</div>

					{isMe && (
						<button
							className="edit-btn"
							onClick={() => navigate("/user/me/profile/edit")}
						>
							プロフィールを編集
						</button>
					)}
				</div>
			</div>
		</div>
	);
};

export default UserProfile;