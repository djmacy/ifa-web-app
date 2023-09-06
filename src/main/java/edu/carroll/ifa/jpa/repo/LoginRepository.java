package edu.carroll.ifa.jpa.repo;

import java.util.List;

import edu.carroll.ifa.jpa.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login, Integer> {
    // JPA throws an exception if we attempt to return a single object that doesn't exist, so return a list
    // even though we only expect either an empty list of a single element. -Nate
    List<Login> findByUsernameIgnoreCase(String username);
}
