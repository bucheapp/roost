import React, { useState } from "react";
import { AuthContext } from "./AuthContext";

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
	const [accessToken, setAccessToken] = useState<string | null>(null);

	return (
		<AuthContext.Provider value={{ accessToken, setAccessToken }}>
			{children}
		</AuthContext.Provider>
	);
};