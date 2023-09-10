export interface AccessToken {
	token: string;
	expiresIn: number;
	refreshToken: string;
	refreshExpiresIn: number;
	tokenType: string;
	notBeforePolicy: number;
	sessionState: string;
	scope: string;
}
