import React, { useEffect, useState, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { AuthContext } from "../../AuthContext";
import { fetchWithAuth } from "../../utils/fetchWithAuth";
import "./UserManagement.css";
import styles from "./UserCommunity.module.css"

const baseURL = import.meta.env.VITE_API_URL;

type User = {
	name: string;
	publicId: string;
	state: "ACTIVE" | "FROZEN";
	permissions: string[];
};

const UserManagement: React.FC = () => {
	const { publicId } = useParams();
	const navigate = useNavigate();
	const auth = useContext(AuthContext);

	const [user, setUser] = useState<User | null>(null);
	const [myPermissions, setMyPermissions] = useState<string[]>([]);
	const [selectedPermissions, setSelectedPermissions] = useState<Set<string>>(new Set());

	const [loading, setLoading] = useState(true);

	const canUpdateState = myPermissions.includes("UPDATE_USERSTATE");
	const canGrant = myPermissions.includes("GRANT_PERMISSION");
	const canRevoke = myPermissions.includes("REVOKE_PERMISSION");

	useEffect(() => {
		if (!auth) {
			navigate("/login");
			return;
		}

		const fetcher = (url: string) =>
			fetchWithAuth(url, {}, auth.accessToken, auth.setAccessToken);

		Promise.all([
			fetcher(baseURL + "/api/users/me/permissions").then(res => res.json()),
			fetcher(baseURL + `/api/users/${publicId}`).then(res => res.json()),
			fetcher(baseURL + `/api/users/${publicId}/permissions`).then(res => res.json()),
		])
			.then(([myPerms, userData, userPerms]) => {
				setMyPermissions(myPerms.permissions);

				setUser({
					name: userData.name,
					publicId: userData.publicId,
					state: userData.state,
					permissions: userPerms.permissions,
				});

				setSelectedPermissions(new Set(userPerms.permissions));
			})
			.catch(err => {
				console.error(err);
				navigate("/");
			})
			.finally(() => setLoading(false));
	}, [auth, navigate, publicId]);

	const togglePermission = (perm: string) => {
		const newSet = new Set(selectedPermissions);

		if (newSet.has(perm)) {
			newSet.delete(perm);
		} else {
			newSet.add(perm);
		}

		setSelectedPermissions(newSet);
	};

	const updatePermissions = async () => {
		if (!auth || !user) return;

		const fetcher = (url: string, options: RequestInit) =>
			fetchWithAuth(url, options, auth.accessToken, auth.setAccessToken);

		const current = new Set(user.permissions);
		const next = selectedPermissions;

		const toGrant = Array.from(next).filter(p => !current.has(p));
		const toRevoke = Array.from(current).filter(p => !next.has(p));

		try {
			let res1: Response | null = null;
			let res2: Response | null = null;

			if (toGrant.length > 0 && canGrant) {
				res1 = await fetcher(baseURL + `/api/users/${user.publicId}/permissions`, {
					method: "POST",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify({ permissions: toGrant }),
				});
			}

			if (toRevoke.length > 0 && canRevoke) {
				res2 = await fetcher(baseURL + `/api/users/${user.publicId}/permissions`, {
					method: "DELETE",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify({ permissions: toRevoke }),
				});
			}

			if (res1 && !res1.ok) {
				const errText = await res1.text();
				throw new Error(errText);
			}

			if (res2 && !res2.ok) {
				const errText = await res2.text();
				throw new Error(errText);
			}

			alert("権限更新成功");
			setUser({ ...user, permissions: Array.from(next) });
		} catch (err) {
			console.error(err);
			alert("更新失敗");
		}
	};

	const updateState = async (state: "ACTIVE" | "FROZEN") => {
		if (!auth || !canUpdateState) return;

		const fetcher = (url: string, options: RequestInit) =>
			fetchWithAuth(url, options, auth.accessToken, auth.setAccessToken);

		try {
			await fetcher(baseURL + `/api/users/${publicId}/state`, {
				method: "PUT",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify({ state }),
			});

			setUser(prev => prev ? { ...prev, state } : prev);
			alert("状態更新成功");
		} catch (err) {
			console.error(err);
			alert("状態更新失敗");
		}
	};

	if (loading) return <div>Loading...</div>;
	if (!user) return <div>ユーザが見つかりません</div>;

	return (
		<div className="user-management-container">
			<h2 className={styles.title}>ユーザ管理</h2>

			<div className="user-management-info">
				<p>ユーザ名: {user.name}</p>
				<p>パブリックID: {user.publicId}</p>
			</div>

			<div className="permissions">
				<h3>権限一覧</h3>
				{user.permissions.map(perm => (
					<label key={perm} className="permission-item">
						<input
							type="checkbox"
							checked={selectedPermissions.has(perm)}
							onChange={() => togglePermission(perm)}
							disabled={(!canGrant && !canRevoke)}
						/>
						{perm}
					</label>
				))}
			</div>

			{(canGrant || canRevoke) && (
				<button className="user-management-btn" onClick={updatePermissions}>
					権限更新
				</button>
			)}

			{canUpdateState && (
				<div className="user-management-state">
					<h3>ユーザ状態</h3>
					<p>現在: {user.state}</p>

					<button onClick={() => updateState("ACTIVE")}>
						ACTIVE
					</button>
					<button onClick={() => updateState("FROZEN")}>
						FROZEN
					</button>
				</div>
			)}
		</div>
	);
};

export default UserManagement;