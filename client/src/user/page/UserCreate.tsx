import React, { useEffect, useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../../AuthContext";
import { fetchWithAuth } from "../../utils/fetchWithAuth";
import "./UserCreate.css";
import styles from "./UserCreate.module.css"
import { FormGroup } from "../../components/FormGroup";

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
			.then((data: { permissions: string[] }) => {
				setPermissions(data.permissions);

				if (!data.permissions.includes("CREATE_USER")) {
					navigate("/");
					return;
				}

				if (data.permissions.includes("CREATE_ADMINUSER")) {
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

	const handleSubmit = async () => {
		if (!auth) return;

		const fetcher = (url: string, options: RequestInit) =>
			fetchWithAuth(url, options, auth.accessToken, auth.setAccessToken);

		try {
			let res: Response;

			if (isAdmin) {
				res = await fetcher(baseURL + "/api/users/admin", {
					method: "POST",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify({
						name,
						email: email || null,
						password,
					}),
				});
			} else {
				res = await fetcher(baseURL + "/api/users", {
					method: "POST",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify({
						name,
						email: email || null,
						password,
						permissions: Array.from(selectedPermissions),
					}),
				});
			}

			if (!res.ok) {
				const errText = await res.text();
				throw new Error(errText);
			}

			const data = await res.json();
			console.log(data);

			alert("作成成功");
			navigate(`/user/${data.publicId}/management`);
		} catch (err) {
			console.error(err);
			alert("作成失敗");
		}
	};

	if (loading) return <div>Loading...</div>;

	return (
		<div className="user-create-container">
			<h2 className={styles.title}>ユーザ作成</h2>

			<FormGroup label="ユーザ名 *">
				<input
					type="text"
					value={name}
					required={true}
					placeholder="ユーザ名を入力"
					onChange={e => setName(e.target.value)}
				/>
			</FormGroup>

			<FormGroup label="Email">
				<input
					type="email"
					value={email}
					placeholder="example@example.com"
					onChange={e => setEmail(e.target.value)}
				/>
			</FormGroup>
			<FormGroup label="パスワード *">
				<input
					type="password"
					value={password}
					required={true}
					placeholder="パスワードを入力"
					onChange={e => setPassword(e.target.value)}
				/>
			</FormGroup>

			{canCreateAdmin && (
				<FormGroup label="Admin">
					<input
						type="checkbox"
						checked={isAdmin}
						onChange={handleAdminToggle}
					/>
				</FormGroup>
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