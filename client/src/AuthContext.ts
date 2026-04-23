import { createContext } from "react";

export type AuthContextType = {
	accessToken: string | null;
	setAccessToken: (token: string | null) => void;
};

export const AuthContext = createContext<AuthContextType | null>(null);