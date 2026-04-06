import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./Signup.css";
import axios from "axios";

const baseURL = import.meta.env.VITE_API_URL;

const Signup: React.FC = () => {
	const navigate = useNavigate();
	const [loading, setLoading] = useState(true);

	const [name, setName] = useState("");
	const [email, setEmail] = useState("");
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
				baseURL + "/api/auth/signup",
				{
					name,
					email: email || null,
					password
				},
				{
					withCredentials: true
				}
			);

			navigate("/user/me");
		} catch (err) {
			if (axios.isAxiosError(err)) {
				const msg = err.response?.data?.msg || "登録に失敗しました";
				setError(msg);
			} else {
				setError("不明なエラーです");
			}
		}
	};

	return (
		<div className="layout">
			<div className="main">
				<div className="signup-card">
					<h2 className="title">ユーザ登録</h2>

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
							<label>Email</label>
							<input
								type="email"
								placeholder="example@email.com"
								value={email}
								onChange={(e) => setEmail(e.target.value)}
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
						<button className="signup-btn">登録する</button>
					</form>
				</div>
			</div>
		</div>
	);
};

export default Signup;