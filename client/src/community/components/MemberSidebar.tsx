import React, { useEffect, useState } from "react";
import "./MemberSidebar.css";
import { useNavigate } from "react-router-dom";

const baseURL = import.meta.env.VITE_API_URL;

type Member = {
	publicId: BigInt;
	time: string;
};

type UserData = {
	publicId: BigInt;
	name: string;
	iconUrl: string;
};

type Props = {
	members: Member[];
	onClose: () => void;
};

const MemberSidebar: React.FC<Props> = ({ members, onClose }) => {
	const navigate = useNavigate();

	const [users, setUsers] = useState<UserData[]>([]);

    useEffect(() => {
		const handleKey = (e: KeyboardEvent) => {
			if (e.key === "Escape") {
				onClose();
			}
		};
		window.addEventListener("keydown", handleKey);
		return () => window.removeEventListener("keydown", handleKey);
	}, [onClose]);

	useEffect(() => {
		const fetchUsers = async () => {
			const results = await Promise.all(
				members.map(async (m) => {
					try {
						const userRes = await fetch(
							`${baseURL}/api/users/${m.publicId}`
						);
						const userData = await userRes.json();

						const profileRes = await fetch(
							`${baseURL}/api/users/${m.publicId}/profile`
						);
						const profileData = await profileRes.json();

						return {
							publicId: m.publicId,
							name: userData.name,
							iconUrl: profileData.iconUUID
								? `${baseURL}/icons/${profileData.iconUrl}`
								: `${baseURL}/icons/default_icon.jpg`,
						};
					} catch (err) {
						console.error(err);
						return null;
					}
				})
			);

			setUsers(results.filter((u): u is UserData => u !== null));
		};

		if (members.length > 0) {
			fetchUsers();
		}
	}, [members]);

	return (
		<>
			<div className="member-overlay" onClick={onClose} />

			<div className="member-sidebar">
				<div className="member-header">
					<span>メンバー</span>
					<button onClick={onClose}>×</button>
				</div>

				<div className="member-list">
					{users.length === 0 && (
						<div className="empty">メンバーなし</div>
					)}

					{users.map((u) => (
						<div
							key={u.publicId.toString()}
							className="member-item"
							onClick={() =>
								navigate(`/user/${u.publicId}/profile`)
							}
						>
							<img
								src={u.iconUrl}
								className="avatar"
								alt="avatar"
							/>
							<div className="info">
								<div className="name">{u.name}</div>
							</div>
						</div>
					))}
				</div>
			</div>
		</>
	);
};

export default MemberSidebar;