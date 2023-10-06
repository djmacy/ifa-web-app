package edu.carroll.ifa.service;

import edu.carroll.ifa.jpa.model.User;
import edu.carroll.ifa.jpa.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.util.AssertionErrors.*;

/**
 * Unit test for the UserServiceImpl class to make sure we are interacting with the database correctly.
 */
@SpringBootTest
@Transactional
public class UserServiceImplTest {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String username1 = "bob_johnson";
    private static final String username2 = "ryan_daniels";
    private static final String icelandicName = "Davíð";
    private static final String arabicName = "ديفيد";
    private static final String mandarinName = "大衛";
    private static final String password1 = "1234";
    private static final String password2 = "password";
    private static final String fname1 = "Bob";
    private static final String fname2 = "Ryan";
    private static final String lname1 = "Johnson";
    private static final String lname2 = "Daniels";
    private static final Integer age1 = 17;
    private static final Integer age2 = 14;
    private final User fakeUser1 = new User(username1, password1, fname1, lname1, age1);
    private final User fakeUser2 = new User(username2, password2, fname2, lname2, age2);
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    /**
     * Before each test is run we ensure that the UserRepository and UserService have been injected and that fakeUser is
     * in the database.
     */
    @BeforeEach
    public void beforeTest() {
        assertNotNull("userService must be injected", userService);

        //Ensure fake records are in DB
        //It is actually not a hashed password until after the user is saved.
        boolean exists = userService.validateUser(fakeUser1.getUsername(), fakeUser1.getHashedPassword());
        if (!exists)
            userService.saveUser(fakeUser1);

        exists = userService.validateUser(fakeUser2.getUsername(), fakeUser2.getHashedPassword());
        if (!exists)
            userService.saveUser(fakeUser2);
    }

    /**
     * This unit test checks to see if a user can successfully be validated in the database provided its raw password and
     * username.
     */
    @Test
    public void validateUserSuccessTest() {
        assertTrue("validateUserSuccessTest: should succeed using the same user/pass info", userService.validateUser(username1, password1));
    }

    /**
     * This unit test checks to see that a user cannot be successfully validated in the database provided an incorrect
     * username and a valid password.
     */
    @Test
    public void validateUserInvalidUserValidPasswordTest() {
        assertFalse("validateUserInvalidUserValidPasswordTest: should fail using an invalid user, but a valid password", userService.validateUser(username1 + "not", password1));
    }

    /**
     * This unit test checks to see that a user cannot be successfully validated in the database provided a valid
     * username and an incorrect password.
     */
    @Test
    public void validateUserValidUserInvalidPasswordTest() {
        assertFalse("validateUserValidUserInvalidPasswordTest: should fail using a valid user, and an invalid password", userService.validateUser(username1, password1+"not"));
    }

    /**
     * This unit test checks to see that a user cannot be successfully validated in the database provided an incorrect
     * password and an incorrect username
     */
    @Test
    public void validateUserInvalidUserInvalidPasswordTest() {
        assertFalse("validateUserInvalidUserInvalidPasswordTest: should fail using an invalid user, valid pass", userService.validateUser(username1 + "not", password1 + "extra"));
    }

    /**
     * This unit test checks to see that a null user cannot successfully be validated in the database provided a password that exists in the db.
     */
    @Test
    public void validateUserNullUserInvalidPasswordTest() {
        assertFalse("validateUserNullUserInvalidPasswordTest: should fail using a null user, and a valid password", userService.validateUser(null, password1));
    }

    /**
     * This unit test checks to see that a valid user cannot successfully be validated in the database with a valid username and a null password.
     */
    @Test
    public void validateUserValidUserNullPasswordTest() {
        assertFalse("validateUserValidUserNullPasswordTest: should fail using a valid username, and a null password", userService.validateUser(username1, null));
    }

    /**
     * This unit test checks to see that a user cannot log in with their valid username and someone elses valid password.
     */
    @Test
    public void validateUserValidUser1ValidPassword2Test() {
        assertFalse("validateUserValidUser1ValidPassword2Test: should fail using a valid username from fakeUser1, and a valid password from fakeUser2", userService.validateUser(username1, password2));
    }

    /**
     * This unit test checks to see that Icelandic characters can be used as a username and validated in the db
     */
    @Test
    public void validateUserIcelandicUserValidPassword() {
        User icelandicUser = new User(icelandicName, password1, fname1, lname1, age1);
        userService.saveUser(icelandicUser);
        assertTrue("validateUserNonIcelandicUserValidPassword: should be able to validate the icelandic user: ", userService.validateUser(icelandicName, password1));
    }

    /**
     * This unit test checks to see that Arabic characters can be used as a username and validated in the db
     */
    @Test
    public void validateUserArabicUserValidPassword() {
        User arabicUser = new User(arabicName, password1, fname1, lname1, age1);
        userService.saveUser(arabicUser);
        assertTrue("validateUserNonArabicUserValidPassword: should be able to validate the arabic user: ", userService.validateUser(arabicName, password1));
    }

    /**
     * This unit test checks to see that Mandarin characters can be used as a username and validated in the db
     */
    @Test
    public void validateUserMandarinUserValidPassword() {
        User mandarinUser = new User(mandarinName, password2, fname1, lname1, age1);
        userService.saveUser(mandarinUser);
        assertTrue("validateUserNonChineseUserValidPassword: should be able to validate the arabic user: ", userService.validateUser(mandarinName, password2));
    }

    /**
     * This unit test checks to see that a user cannot be saved into the database if they already exist in the database.
     */
    @Test
    public void saveUserExistingUserTest() {
        assertFalse("saveUserExistingUserTest: should fail using a user already in db", userService.saveUser(fakeUser1));
    }

    /**
     * This unit test checks to see that a new user can be saved into the database if they do not already exist in the database.
     */
    @Test
    public void saveUserNewUserTest() {
        User newUser = new User("new" + username1, password1, fname1, lname1, age1);
        assertTrue("saveUserNewUserTest: should succeed using a new user", userService.saveUser(newUser));
    }

    /**
     * This unit test checks to see that a null value cannot be saved to the database
     */
    @Test
    public void saveUserNullTest() {
        assertFalse("saveUserNullTest: should fail to add a null value", userService.saveUser(null));
    }

    /**
     * This unit test checks to see that a new User with no values cannot be saved into the database
     */
    @Test
    public void saveUserNullUserTest() {
        assertFalse("saveUserNullUserTest: should fail to add a null user", userService.saveUser(new User()));
    }

    /**
     * This unit test checks to see that a new User with no username cannot be saved into the database
     */
    @Test
    public void saveUserNullUsernameTest() {
        User noUsername = new User(null, "new"+ password1, "new"+fname1, "new"+lname1, age1);
        assertFalse("saveUserNullUsernameTest: should fail to add a user with no username", userService.saveUser(noUsername));
    }

    /**
     * This unit test checks to see that a new User with no password cannot be saved into the database
     */
    @Test
    public void saveUserNullPasswordTest() {
        User noPassword = new User("newUsername"+username1, null, "new"+fname1, "new"+lname1, age1);
        assertFalse("saveUserNullPasswordTest: should fail to add a user with no password", userService.saveUser(noPassword));
    }

    /**
     * This unit test checks to see that a new User with no first name cannot be saved into the database
     */
    @Test
    public void saveUserNullFnameTest() {
        User noFname = new User("newUsername"+username1, "new"+password1, null, "new"+lname1, age1);
        assertFalse("saveUserNullFnameTest: should fail to add a user with no first name", userService.saveUser(noFname));
    }

    /**
     * This unit test checks to see that a new User with no last name cannot be saved into the database
     */
    @Test
    public void saveUserNullLnameTest() {
        User noLname = new User("newUsername"+username1, "new"+password1, "new"+fname1, null, age1);
        assertFalse("saveUserNullLnameTest: should fail to add a user with no last name", userService.saveUser(noLname));
    }

    /**
     * This unit test checks to see that a new User with no age cannot be added to the database
     */
    @Test
    public void saveUserNullAgeTest() {
        User noAge = new User("newUsername"+username1, "new"+password1, "new"+fname1, "new"+lname1, null);
        assertFalse("saveUserNullAgeTest: should fail to add a user with no last name", userService.saveUser(noAge));
    }

    /**
     * This unit test checks to see if a new User with a negative age cannot be added to the database
     */
    @Test
    public void saveUserNegativeAgeTest() {
        User negativeAge = new User("newUsername"+username1, "new"+password1, "new"+fname1, "new"+lname1, Integer.MIN_VALUE);
        assertFalse("saveUserNegativeAgeTest: should fail to add a user with negative age", userService.saveUser(negativeAge));
    }

    /**
     * This unit test checks to see if a new user with an age of 1 can be added to the database since 1-125 is the cutoff
     */
    @Test
    public void saveUser1AgeTest() {
        User age1 = new User("newUsername"+username1, "new"+password1, "new"+fname1, "new"+lname1, 1);
        assertTrue("saveUser1AgeTest: should succeed to add a user with an age of 1", userService.saveUser(age1));
    }

    /**
     * This unit test checks to see if a new user with an age of 125 can be added to the database since 1-125 is the cutoff
     */
    @Test
    public void saveUser125AgeTest() {
        User age125 = new User("newUsername"+username1, "new"+password1, "new"+fname1, "new"+lname1, 125);
        assertTrue("saveUser125AgeTest: should succeed to add a user with an age of 125", userService.saveUser(age125));
    }

    /**
     * This unit test checks to see if a new user with an age of 0 cannot be added to the database
     */
    @Test
    public void saveUser0AgeTest() {
        User age0 = new User("newUsername"+username1, "new"+password1, "new"+fname1, "new"+lname1, 0);
        assertFalse("saveUser0AgeTest: should fail to add a user with an age of 0", userService.saveUser(age0));
    }

    /**
     * This unit test checks to see if a new user with an age of Integer.MAX_VALUE cannot be added to the database
     */
    @Test
    public void saveUserMaxAgeTest() {
        User ageMax = new User("newUsername"+username1, "new"+password1, "new"+fname1, "new"+lname1, Integer.MAX_VALUE);
        assertFalse("saveUserMaxAgeTest: should fail to add a user with an age of Integer.MAX_VALUE", userService.saveUser(ageMax));
    }

    /*
    @Test
    public void saveUserAgeTest() {
        Integer newAge = 11;
        User newUser = new User("new" + username, password, fname, lname, age);
        assertTrue("saveUserAgeTest: should succeed saving a new age with user in db", userService.saveUserAge(newUser, newAge));
        //change age back to original age so the test will pass if rerun
        userService.saveUserAge(newUser, age);
    }
     */

    /**
     * This unit test checks to see that the user age matches the expected age of the user in the database
     */
    @Test
    public void getUserAgeTestExistingUserTest() {
        assertEquals("getUserAgeTestExistingUserTest: should equal the fakeUsers age, 17", age1, userService.getUserAge(fakeUser1.getUsername()));
    }

    /**
     * This unit test checks to see that an age of -1 is returned if there is no user in the database.
     */
    @Test
    public void getUserAgeTestNonUserTest() {
        User newUser = new User("new1" + username1, password1, fname1, lname1, age1);
        assertEquals("getUserAgeTestNonUserTest: should equal -1 since user is not in db", -1, userService.getUserAge(newUser.getUsername()));
    }
}
