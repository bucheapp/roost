import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import styles from "../signup/Signup.module.css";
import axios from "axios";

const baseURL = import.meta.env.VITE_API_URL;

const Login: React.FC = () => {
	const navigate = useNavigate();
	const [loading, setLoading] = useState(true);

	const [name, setName] = useState("");
	const [password, setPassword] = useState("");
	const [error, setError] = useState("");

	useEffect(() => {
		const checkAuth = async () => {
			try {
				const res = await axios.get(baseURL + "/api/auth/me", {
					withCredentials: true
				});

				if (res.data?.refreshToken) {
					navigate("/");
				}
			} catch {
			} finally {
				setLoading(false);
			}
		};

		checkAuth();
	}, []);

	if (loading) return <div>Loading...</div>;

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault();

		try {
			await axios.post(
				baseURL + "/api/auth/login",
				{
					name,
					password
				},
				{
					withCredentials: true
				}
			);

			navigate("/user/me/profile");
		} catch (err) {
			if (axios.isAxiosError(err)) {
				const msg = err.response?.data?.msg || "ログインに失敗しました";
				setError(msg);
			} else {
				setError("不明なエラーです");
			}
		}
	};

	return (
		<div className="layout">
			<div className={styles.main}>
				<div className="signup-card">
					<h2 className="title">ログイン</h2>

					<form className="form" onSubmit={handleSubmit}>
						<div className="form-group">
							<label>ユーザ名 *</label>
							<input
								type="text"
								placeholder="ユーザ名を入力"
								required
								value={name}
								onChange={(e) => setName(e.target.value)}
							/>
						</div>

						<div className="form-group">
							<label>パスワード *</label>
							<input
								type="password"
								placeholder="パスワードを入力"
								required
								value={password}
								onChange={(e) => setPassword(e.target.value)}
							/>
						</div>

						{error && <div className="error">{error}</div>}

						<button className="signup-btn">ログイン</button>
					</form>

					<div className="footer-link">
						新規登録は <Link to="/signup">こちら</Link>
					</div>
				</div>
			</div>
		</div>
	);
};

export default Login;