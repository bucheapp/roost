import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Signup.css";
import styles from "./Signup.module.css";
import axios from "axios";
import { useAuthCheck } from "../../utils/useAuthCheck";
import { FormGroup } from "../../components/FormGroup";

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
			<div className={styles.main}>
				<div className="signup-card">
					<h2 className="title">ユーザ登録</h2>

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
							label="Eメール"
							type="email"
							placeholder="example@email.com"
							value={email}
							onChange={(e) => setEmail(e.target.value)}
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
						<button className="submit-btn">登録する</button>
					</form>
				</div>
			</div>
		</div>
	);
};

export default Signup;