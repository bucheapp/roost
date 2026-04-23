import { useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export const useAuthCheck = (baseURL: string, setLoading: (v: boolean) => void) => {
	const navigate = useNavigate();

	useEffect(() => {
		const checkAuth = async () => {
			try {
				const res = await axios.get(baseURL + "/api/auth/me", {
					withCredentials: true
				});

				if (res.data?.refreshToken) {
					navigate("/");
				}
			} catch (e) {
			} finally {
				setLoading(false);
			}
		};

		checkAuth();
	}, [baseURL, navigate, setLoading]);
};