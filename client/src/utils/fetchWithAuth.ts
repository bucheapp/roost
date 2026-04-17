let isRefreshing = false;
let refreshPromise: Promise<string | null> | null = null;

const baseURL = import.meta.env.VITE_API_URL;

export const fetchWithAuth = async (
	url: string,
	options: RequestInit = {},
	accessToken: string | null,
	setAccessToken: (token: string | null) => void
): Promise<Response> => {

	const doFetch = (token: string | null) => {
		return fetch(url, {
			...options,
			headers: {
				...options.headers,
				...(token ? { Authorization: `Bearer ${token}` } : {}),
			},
			credentials: "include",
		});
	};

	if (!accessToken) {
		if (!isRefreshing) {
			isRefreshing = true;

			refreshPromise = fetch(baseURL + "/api/auth/refresh", {
				method: "GET",
				credentials: "include",
			})
				.then(async (r) => {
					if (!r.ok) throw new Error("refresh failed");
					const data = await r.json();
					setAccessToken(data.accessToken);
					return data.accessToken;
				})
				.catch(() => {
					setAccessToken(null);
					return null;
				})
				.finally(() => {
					isRefreshing = false;
				});
		}

		const newToken = await refreshPromise;

		if (!newToken) throw new Error("認証切れ");

		accessToken = newToken;
	}

	let res = await doFetch(accessToken);

	if (res.status === 401 || res.status === 403) {
		if (!isRefreshing) {
			isRefreshing = true;

			refreshPromise = fetch(baseURL + "/api/auth/refresh", {
				method: "GET",
				credentials: "include",
			})
				.then(async (r) => {
					if (!r.ok) throw new Error("refresh failed");
					const data = await r.json();
					setAccessToken(data.accessToken);
					return data.accessToken;
				})
				.catch(() => {
					setAccessToken(null);
					return null;
				})
				.finally(() => {
					isRefreshing = false;
				});
		}

		const newToken = await refreshPromise;

		if (!newToken) throw new Error("認証切れ");

		res = await doFetch(newToken);
	}

	return res;
};