package edu.carroll.ifa.jpa.repo;

import java.util.List;

import edu.carroll.ifa.jpa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * An interface that extends JpaRepository and allows us to use JPA methods
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // JPA throws an exception if we attempt to return a single object that doesn't exist (which is way more expensive),
    // so return a list even though we only expect either an empty list of a single element.

    /**
     * Given a username, it returns the User in a list
     * @param username - Username associated with the User object
     * @return one User in a list if found, otherwise an empty list.
     */
    List<User> findByUsernameIgnoreCase(String username);
}
