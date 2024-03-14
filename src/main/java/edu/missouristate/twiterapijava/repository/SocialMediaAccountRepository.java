package edu.missouristate.twiterapijava.repository;

import edu.missouristate.twiterapijava.model.SocialMediaAccount;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SocialMediaAccountRepository extends JpaRepository<SocialMediaAccount, Integer> {
    // Custom query methods can be added here
}
