import React, { useEffect, useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../../AuthContext";
import { fetchWithAuth } from "../../utils/fetchWithAuth";
import { useUnsavedChanges } from "../../utils/useUnsavedChanges";
import styles from "./UserProfileEdit.module.css";
import { FormGroup } from "../../components/FormGroup";
import { ButtonGroup } from "../components/ButtonGroup";

type Profile = {
	bio: string;
	iconUrl: string;
	gender: string;
	dateOfBirth: string;
	address: string;
	githubUrl: string;
};

const baseURL = import.meta.env.VITE_API_URL;

const UserProfileEdit: React.FC = () => {
	const navigate = useNavigate();
	const auth = useContext(AuthContext);

	const [profile, setProfile] = useState<Profile | null>(null);
	const [initial, setInitial] = useState<Profile | null>(null);

	const [bio, setBio] = useState("");
	const [iconFile, setIconFile] = useState<File | null>(null);
	const [previewUrl, setPreviewUrl] = useState<string | null>(null);

	const [gender, setGender] = useState("");
	const [dateOfBirth, setDateOfBirth] = useState("");
	const [address, setAddress] = useState("");
	const [githubUrl, setGithubUrl] = useState("");

	const [error, setError] = useState("");

	useEffect(() => {
		if (!auth) return;

		fetchWithAuth(
			baseURL + "/api/users/me/profile",
			{},
			auth.accessToken,
			auth.setAccessToken
		)
			.then(res => res.json())
			.then(data => {
				setProfile(data);
				setInitial(data);

				setBio(data.bio || "");
				setGender(data.gender || "");
				setDateOfBirth(data.dateOfBirth || "");
				setAddress(data.address || "");
				setGithubUrl(data.githubUrl || "");
			})
			.catch(err => {
				console.error(err);
				navigate("/login");
			});
	}, [auth, navigate]);

	const isChanged = () => {
		if (!initial) return false;
		return (
			bio !== (initial.bio || "") ||
			gender !== (initial.gender || "") ||
			dateOfBirth !== (initial.dateOfBirth || "") ||
			address !== (initial.address || "") ||
			githubUrl !== (initial.githubUrl || "") ||
			iconFile !== null
		);
	};

	const handleClose = () => {
		const { confirmClose } = useUnsavedChanges(isChanged);
		if (!confirmClose()) return;
		navigate(-1);
	};

	const handleSubmit = async () => {
		if (!auth || !initial) return;

		setError("");

		const formData = new FormData();

		if (bio !== (initial.bio || "")) formData.append("bio", bio);
		if (gender !== (initial.gender || "")) formData.append("gender", gender);
		if (dateOfBirth !== (initial.dateOfBirth || "")) formData.append("dateOfBirth", dateOfBirth);
		if (address !== (initial.address || "")) formData.append("address", address);
		if (githubUrl !== (initial.githubUrl || "")) formData.append("githubUrl", githubUrl);

		if (iconFile) {
			formData.append("iconFile", iconFile);
		}

		try {
			const res = await fetchWithAuth(
				baseURL + "/api/users/me/profile",
				{
					method: "PATCH",
					body: formData,
				},
				auth.accessToken,
				auth.setAccessToken
			);

			const data = await res.json().catch(() => null);

			if (!res.ok) {
				setError(data?.msg || "保存に失敗しました");
				return;
			}

			navigate("/user/me/profile");
		} catch (err) {
			console.error(err);
			setError("不明なエラーです");
		}
	};

	if (!profile) return <div>Loading...</div>;

	const iconSrc = previewUrl
		? previewUrl
		: profile.iconUrl
			? baseURL + "/icons/" + profile.iconUrl
			: baseURL + "/icons/default_icon.jpg";

	return (
		<div className={styles.layout}>
			<div className={styles.content}>
				<h2 className={styles.title}>
					プロフィール編集 {isChanged() && <span className={styles.edited}>*</span>}
				</h2>

				<FormGroup label="アイコン" edited={!!iconFile}>
					<div className={styles.icon_section}>
						<img
							src={iconSrc}
							className={styles.icon_preview}
							onClick={() =>
								document.getElementById("iconInput")?.click()
							}
						/>

						<input
							id="iconInput"
							type="file"
							accept="image/*"
							style={{ display: "none" }}
							onChange={e => {
								const file = e.target.files?.[0] || null;
								setIconFile(file);

								if (file) {
									const url = URL.createObjectURL(file);
									setPreviewUrl(url);
								}
							}}
						/>
					</div>
				</FormGroup>

				<FormGroup label="自己紹介" edited={bio !== (initial?.bio || "")}>
					<textarea value={bio} onChange={e => setBio(e.target.value)} />
				</FormGroup>

				<FormGroup label="性別" edited={gender !== (initial?.gender || "")}>
					<select value={gender} onChange={e => setGender(e.target.value)}>
						<option value="">未設定</option>
						<option value="MALE">男性</option>
						<option value="FEMALE">女性</option>
						<option value="OTHER">その他</option>
					</select>
				</FormGroup>

				<FormGroup label="誕生日" edited={dateOfBirth !== (initial?.dateOfBirth || "")}>
					<input
						type="date"
						value={dateOfBirth ? dateOfBirth.substring(0, 10) : ""}
						onChange={e => setDateOfBirth(e.target.value)}
					/>
				</FormGroup>

				<FormGroup label="住所" edited={address !== (initial?.address || "")}>
					<input
						type="text"
						value={address}
						onChange={e => setAddress(e.target.value)}
					/>
				</FormGroup>

				<FormGroup label="Github" edited={githubUrl !== (initial?.githubUrl || "")}>
					<input
						type="text"
						value={githubUrl}
						onChange={e => setGithubUrl(e.target.value)}
					/>
				</FormGroup>

				{error && <div className="error">{error}</div>}

				<ButtonGroup onSubmit={handleSubmit} onClose={handleClose} />
			</div>
		</div>
	);
};

export default UserProfileEdit;