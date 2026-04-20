import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import styles from "../signup/Signup.module.css";
import axios from "axios";
import { useAuthCheck } from "../../utils/useAuthCheck";
import { FormGroup } from "../../components/FormGroup";

const baseURL = import.meta.env.VITE_API_URL;

const Login: React.FC = () => {
	const navigate = useNavigate();
	const [loading, setLoading] = useState(true);

	const [name, setName] = useState("");
	const [password, setPassword] = useState("");
	const [error, setError] = useState("");

	useAuthCheck(baseURL, setLoading);

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
						<FormGroup
							label="ユーザ名 *"
							type="text"
							placeholder="ユーザ名を入力"
							value={name}
							onChange={(e) => setName(e.target.value)}
							required
						/>

						<FormGroup
							label="パスワード *"
							type="password"
							placeholder="パスワードを入力"
							value={password}
							onChange={(e) => setPassword(e.target.value)}
							required
						/>

						{error && <div className="error">{error}</div>}

						<button className="submit-btn">ログイン</button>
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