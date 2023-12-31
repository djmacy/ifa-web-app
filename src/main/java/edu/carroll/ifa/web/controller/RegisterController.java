package edu.carroll.ifa.web.controller;

import edu.carroll.ifa.jpa.model.User;
import edu.carroll.ifa.service.UserService;
import edu.carroll.ifa.web.form.RegisterOrUpdateForm;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class for the register page. It makes sure that the user has valid input and then redirects them to login
 * after successfully registering.
 */
@Controller
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private final UserService userService;

    /**
     * Constructs a LoginController instance with the UserService dependency.
     * @param userService - UserService implementation used in the LoginController
     */
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles the GET request for the /register page. It also initializes the register form model and displays the page.
     * @param model - Model object for storing attributes associated with logging in
     * @return register page
     */
    @GetMapping("/register")
    public String registerGet(Model model) {
        // creates a new registration form and adds it to the model
        model.addAttribute("registerOrUpdateForm", new RegisterOrUpdateForm());
        logger.debug("New user visited the register page");
        return "register";
    }

    /**
     * Handles the POST request for the /register page. It also processes user registration.
     * @param registerOrUpdateForm - RegisterForm contains the information submitted by the user
     * @param result - BindingResult validates form information
     * @param model - Model object for storing attributes
     * @return register page if user gives invalid or incomplete information, otherwise login page
     */
    @PostMapping("/register")
    public String registerPost(@Valid @ModelAttribute RegisterOrUpdateForm registerOrUpdateForm,
                               BindingResult result,
                               HttpSession session,
                               Model model) {
        // check if there were any errors when the user submitted the registration
        if (result.hasErrors()) {
            logger.debug("There were {} errors", result.getErrorCount());
            return "register";
        }
        // create a new user after the user filled out the registration form
        User newUser = new User();
        newUser.setUsername(registerOrUpdateForm.getUsername());
        newUser.setHashedPassword(registerOrUpdateForm.getPassword());
        newUser.setFirstName(registerOrUpdateForm.getFirstName());
        newUser.setLastName(registerOrUpdateForm.getLastName());
        newUser.setAge(registerOrUpdateForm.getAge());
        // checks if the username is already in the database
        User preExistingUserCheckUser = userService.getUserByUserName(newUser.getUsername());
        // display an error if the username is already in the database
        if (preExistingUserCheckUser != null) {
            result.addError(new ObjectError("globalError", "Username already exists"));
            logger.info("The username '{}' already exists", newUser.getUsername());
            return "register";
        }
        // save the new user to the database
        userService.registerUser(newUser);
        logger.info("The user '{}' completed registration", newUser.getUsername());

        // Set the username up in the session
        session.setAttribute("username", newUser.getUsername());
        //ensure that the proper username is displayed after login
        model.addAttribute("username", newUser.getUsername());

        return "loginSuccess";
    }
}
