import React, { useEffect, useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../../AuthContext";
import { fetchWithAuth } from "../../utils/fetchWithAuth";
import { useUnsavedChanges } from "../../utils/useUnsavedChanges"
import "./UserProfile.css"
import styles from "./UserProfileEdit.module.css"
import { FormGroup } from "../../components/FormGroup";
import { ButtonGroup } from "../components/ButtonGroup";

type User = {
    name: string;
    email: string;
};

const baseURL = import.meta.env.VITE_API_URL;

const UserEdit: React.FC = () => {
    const navigate = useNavigate();
    const auth = useContext(AuthContext);

    const [user, setUser] = useState<User | null>(null);
    const [initial, setInitial] = useState<User | null>(null);

    const [name, setName] = useState("");
    const [email, setEmail] = useState("");

    useEffect(() => {
        if (!auth) return;

        fetchWithAuth(baseURL + "/api/users/me", {}, auth.accessToken, auth.setAccessToken)
            .then(res => res.json())
            .then(data => {
                setUser(data);
                setInitial(data);
                setName(data.name || "");
                setEmail(data.email || "");
            })
            .catch(err => {
                console.error(err);
                navigate("/login");
            });
    }, [auth, navigate]);

    const isChanged = () => {
        if (!initial) return false;
        return (
            name !== (initial.name || "") ||
            email !== (initial.email || "")
        );
    };

    const handleClose = () => {
        const { confirmClose } = useUnsavedChanges(isChanged);
        if (!confirmClose()) return;
        navigate(-1);
    };

    const handleSubmit = async () => {
        if (!auth || !initial) return;

        try {
            const res = await fetchWithAuth(
                baseURL + "/api/users/me",
                {
                    method: "PATCH",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        name: name !== initial.name ? name : null,
                        email: email !== initial.email ? email : null,
                    }),
                },
                auth.accessToken,
                auth.setAccessToken
            );

            if (!res.ok) throw new Error();

            navigate("/user/me/profile");
        } catch (err) {
            console.error(err);
            alert("変更に失敗しました");
        }
    };

    if (!user) return <div>Loading...</div>;

    return (
        <div className={styles.layout}>
            <div className={styles.content}>
                <h2>ユーザー編集 {isChanged() && <span className={styles.edited}>*</span>}</h2>
                <FormGroup
                    label="ユーザ名"
                    edited={name !== (initial?.name || "")}
                >
                    <input
                        type="text"
                        value={name}
                        onChange={e => setName(e.target.value)}
                    />
                </FormGroup>

                <FormGroup
                    label="Email"
                    edited={email !== (initial?.email || "")}
                >
                    <input
                        type="email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                    />
                </FormGroup>

                <FormGroup label="パスワード">
                    <div className="password-center">
                        <button
                            className="link-btn"
                            onClick={() => navigate("/user/me/password/edit")}
                        >
                            パスワード変更
                        </button>
                    </div>
                </FormGroup>
                <ButtonGroup
                    onSubmit={handleSubmit}
                    onClose={handleClose}
                    isDisabled={!isChanged()}
                />
            </div>
        </div>
    );
};

export default UserEdit;