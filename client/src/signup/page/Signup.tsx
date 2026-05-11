import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../../auth/components/Auth.css";
import axios from "axios";
import { useAuthCheck } from "../../utils/useAuthCheck";
import { FormGroup } from "../../components/FormGroup";
import styles from "../../auth/components/Auth.module.css"
import AuthHeader from "../../auth/components/AuthHeader";

const baseURL = import.meta.env.VITE_API_URL;

const Signup: React.FC = () => {
	const navigate = useNavigate();
	const [loading, setLoading] = useState(true);

	const [name, setName] = useState("");
	const [email, setEmail] = useState("");
	const [password, setPassword] = useState("");

	const [error, setError] = useState("");

	useAuthCheck(baseURL, setLoading);

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

			navigate("/user/me/profile");
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
			<AuthHeader />
			<div className={styles.main}>
				<div className="auth-card">
					<h2 className="title">ユーザ登録</h2>

					<form className="form" onSubmit={handleSubmit}>
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

						{error && <div className="error">{error}</div>}
						<button className="submit-btn">登録する</button>
					</form>
				</div>
			</div>
		</div>
	);
};

export default Signup;