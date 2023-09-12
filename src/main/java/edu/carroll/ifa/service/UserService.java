package edu.carroll.ifa.service;

import edu.carroll.ifa.jpa.model.User;
import edu.carroll.ifa.web.form.LoginForm;
public interface UserService {
    /**
     * Given a loginForm, determine if the information provided is valid, and the user exists in the system.
     * @param form - Data containing user login information, such as username and password.
     * @return true if data exists and matches what's on record, false otherwise
     * - Nate
     */
    boolean validateUser(LoginForm form);

    boolean saveUser(User user);
    int getUserAge(String username);
}
