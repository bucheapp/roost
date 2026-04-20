import React, { useEffect, useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../AuthContext";
import { fetchWithAuth } from "../utils/fetchWithAuth";
import "./UserCreate.css";

const baseURL = import.meta.env.VITE_API_URL;

const UserCreate: React.FC = () => {
	const navigate = useNavigate();
	const auth = useContext(AuthContext);

	const [name, setName] = useState("");
	const [email, setEmail] = useState("");
	const [password, setPassword] = useState("");

	const [permissions, setPermissions] = useState<string[]>([]);
	const [selectedPermissions, setSelectedPermissions] = useState<Set<string>>(new Set());

	const [canCreateAdmin, setCanCreateAdmin] = useState(false);
	const [isAdmin, setIsAdmin] = useState(false);

	const [loading, setLoading] = useState(true);

	useEffect(() => {
		if (!auth) {
			navigate("/login");
			return;
		}

		const fetcher = (url: string) =>
			fetchWithAuth(url, {}, auth.accessToken, auth.setAccessToken);

		fetcher(baseURL + "/api/users/me/permissions")
			.then(res => res.json())
			.then((data: string[]) => {
				setPermissions(data);

				if (!data.includes("CREATE_USER")) {
					alert("権限がありません");
					navigate("/");
					return;
				}

				if (data.includes("CREATE_ADMINUSER")) {
					setCanCreateAdmin(true);
				}
			})
			.catch(err => {
				console.error(err);
				navigate("/login");
			})
			.finally(() => setLoading(false));
	}, [auth, navigate]);

	const togglePermission = (perm: string) => {
		const newSet = new Set(selectedPermissions);
		if (newSet.has(perm)) {
			newSet.delete(perm);
		} else {
			newSet.add(perm);
		}
		setSelectedPermissions(newSet);
	};

	const handleAdminToggle = () => {
		const next = !isAdmin;
		setIsAdmin(next);

		if (next) {
			setSelectedPermissions(new Set(permissions));
		} else {
			setSelectedPermissions(new Set());
		}
	};

	// 作成処理
	const handleSubmit = async () => {
		if (!auth) return;

		const fetcher = (url: string, options: RequestInit) =>
			fetchWithAuth(url, options, auth.accessToken, auth.setAccessToken);

		try {
			if (isAdmin) {
				await fetcher(baseURL + "/api/users/admin", {
					method: "POST",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify({
						name,
						email,
						password,
					}),
				});
			} else {
				await fetcher(baseURL + "/api/users", {
					method: "POST",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify({
						name,
						email,
						password,
						permissions: Array.from(selectedPermissions),
					}),
				});
			}

			alert("作成成功");
			navigate("/user/management");
		} catch (err) {
			console.error(err);
			alert("作成失敗");
		}
	};

	if (loading) return <div>Loading...</div>;

	return (
		<div className="user-create-container">
			<h2>ユーザ作成</h2>

			<div className="form-group">
				<label>ユーザ名</label>
				<input value={name} onChange={e => setName(e.target.value)} />
			</div>

			<div className="form-group">
				<label>Email</label>
				<input value={email} onChange={e => setEmail(e.target.value)} />
			</div>

			<div className="form-group">
				<label>パスワード</label>
				<input
					type="password"
					value={password}
					onChange={e => setPassword(e.target.value)}
				/>
			</div>

			{canCreateAdmin && (
				<div className="form-group">
					<label>Admin</label>
					<input
						type="checkbox"
						checked={isAdmin}
						onChange={handleAdminToggle}
					/>
				</div>
			)}

			<div className="permissions">
				<h3>権限一覧</h3>
				{permissions.map(perm => (
					<label key={perm} className="permission-item">
						<input
							type="checkbox"
							checked={selectedPermissions.has(perm)}
							onChange={() => togglePermission(perm)}
							disabled={isAdmin}
						/>
						{perm}
					</label>
				))}
			</div>

			<button className="submit-btn" onClick={handleSubmit}>
				作成
			</button>
		</div>
	);
};

export default UserCreate;