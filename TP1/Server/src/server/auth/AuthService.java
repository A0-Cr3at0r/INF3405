package server.auth;

import server.persistence.UserAccountManager;
import common.model.UserAccount;

import java.io.IOException;

public class AuthService {
    private final UserAccountManager accountManager;

    public AuthService(UserAccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public synchronized boolean authenticateOrRegister(String username, String password) throws IOException {
        UserAccount account = accountManager.findByUsername(username);

        if (account != null) {
            return account.getPassword().equals(password);
        }

        UserAccount newAccount = new UserAccount(username, password);
        accountManager.saveAccount(newAccount);
        return true;
    }
}