import React, { useEffect, useState, useContext } from "react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
import { AuthContext } from "../../AuthContext";
import { fetchWithAuth } from "../../utils/fetchWithAuth";
import "./UserSidebar.css";

type Props = {
	isOpen: boolean;
	isMobile: boolean;
	onClose: () => void;
};

const baseURL = import.meta.env.VITE_API_URL;

const UserSidebar: React.FC<Props> = ({ isOpen, isMobile, onClose }) => {
	const navigate = useNavigate();
	const { publicId } = useParams();
	const location = useLocation();
	const [hasCreateUserPermission, setHasCreateUserPermission] = useState(false);

	const isMe = !publicId;

	const auth = useContext(AuthContext);
	if (isMe && auth) {
		const fetcher = (url: string) =>
			fetchWithAuth(url, {}, auth.accessToken, auth.setAccessToken);

		useEffect(() => {
			fetcher(baseURL + "/api/users/me/permissions")
				.then(res => res.json())
				.then((data: { permissions: string[] }) => {
					if (data.permissions.includes("CREATE_USER")) {
						setHasCreateUserPermission(true);
					}
				})
		});
	}

	const menu = [
		{
			label: "プロフィール",
			path: isMe
				? "/user/me/profile"
				: `/user/${publicId}/profile`
		},
		{
			label: "所属コミュニティ",
			path: isMe
				? "/user/me/community"
				: `/user/${publicId}/community`
		}
	];

	if(isMe) {
		menu.push({
			label: "セキュリティ",
			path: "/user/me/security",
		});
	}
	
	if (hasCreateUserPermission) {
		menu.push({
			label: "ユーザ作成",
			path: "/user/create",
		});
	}

	return (
		<div className={`user-sidebar ${isOpen ? "open" : ""}`}>
			{isMobile && isOpen && (
				<button
					className="close-sidebar-btn"
					onClick={onClose}
				>
					×
				</button>
			)}

			<div className="user-sidebar-header">ユーザー</div>
			<hr />

			<div className="user-sidebar-menu">
				{menu.map((item) => (
					<div
						key={item.label}
						className={`user-sidebar-item ${location.pathname === item.path
							? "active"
							: ""
							}`}
						onClick={() => {
							navigate(item.path);
							if (isMobile) onClose();
						}}
					>
						{item.label}
					</div>
				))}
			</div>
		</div>
	);
};

export default UserSidebar;