package common;

public class AuthResponse extends AbstractMessage {
    private boolean auth;

    public AuthResponse(boolean auth) {
        this.auth = auth;
    }

    public boolean isAuth() {
        return auth;
    }
}

