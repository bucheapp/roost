import React, { useEffect, useState, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { AuthContext } from "../../AuthContext.ts";
import { fetchWithAuth } from "../../utils/fetchWithAuth.ts";
import UserSidebar from "../components/UserSidebar.tsx";
import { useIsMobile } from "../../utils/useIsMobile.ts";
import styles from "./UserProfile.module.css";
import "./UserProfile.css";

type Profile = {
	name: string;
	email: string;
	bio: string;
	iconUUID: string;
	gender: string;
	dateOfBirth: string;
	address: string;
	githubUrl: string;
};

const baseURL = import.meta.env.VITE_API_URL;

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

	const defaultText = isMe ? "(設定されてません)" : "";

	return (
		<div className="layout">
			<UserSidebar
				isOpen={isOpen}
				isMobile={isMobile}
				onClose={() => setIsOpen(false)}
			/>
			{isOpen && isMobile && <div className="overlay" onClick={() => setIsOpen(false)} />}

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
					<div className="profile-header">
						<img
							src={profile.iconUUID ? baseURL + "/icons/" + profile.iconUUID + ".jpg" : baseURL + "/icons/default_icon.jpg"}
							className="profile-avatar"
						/>
						<div>
							<h2>{profile.name ? profile.name : defaultText}</h2>
							{isMe && (
								<>
									<div className="profile-account">
										<p><span>Email:</span> {profile.email || defaultText}</p>
										<p><span>パスワード:</span> ********</p>
									</div>
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

					<div className="profile-section">
						<h3>自己紹介</h3>
						<p>{profile.bio ? profile.bio : defaultText}</p>
					</div>

					<div className="profile-section profile-details">
						<div className={styles.item}>
							<span>性別:</span>
							<span>{formatGender(profile.gender)}</span>
						</div>

						<div className={styles.item}>
							<span>誕生日:</span>
							<span>
								{profile.dateOfBirth
									? new Date(profile.dateOfBirth).toLocaleDateString()
									: defaultText}
							</span>
						</div>

						<div className={styles.item}>
							<span>住所:</span>
							<span>{profile.address || defaultText}</span>
						</div>

						<div className={styles.item}>
							<span>Github:</span>
							<span>
								{profile.githubUrl ? (
									<a href={profile.githubUrl} target="_blank" rel="noopener noreferrer">
										{profile.githubUrl}
									</a>
								) : (
									defaultText
								)}
							</span>
						</div>
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