package edu.missouristate.twiterapijava.service;

import edu.missouristate.twiterapijava.model.SocialMediaAccount;
import edu.missouristate.twiterapijava.repository.SocialMediaAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SocialMediaAccountService {

    @Autowired
    private SocialMediaAccountRepository repository;

    public SocialMediaAccount saveSocialMediaAccount(Integer userId, String platformName, String accessToken) {
        SocialMediaAccount account = new SocialMediaAccount();
        account.setUserId(userId);
        account.setPlatformName(platformName);
        account.setAccessToken(accessToken);
        return repository.save(account);
    }
}
