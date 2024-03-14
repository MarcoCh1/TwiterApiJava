package edu.missouristate.twiterapijava.controller;

import edu.missouristate.twiterapijava.model.SocialMediaAccount;
import edu.missouristate.twiterapijava.service.SocialMediaAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SocialMediaAccountController {

    private final SocialMediaAccountService service;

    @Autowired
    public SocialMediaAccountController(SocialMediaAccountService service) {
        this.service = service;
    }

    @PostMapping("/socialMediaAccounts")
    public ResponseEntity<SocialMediaAccount> addSocialMediaAccount(@RequestBody SocialMediaAccount account) {
        try {
            SocialMediaAccount savedAccount = service.saveSocialMediaAccount(account.getUserId(), account.getPlatformName(), account.getAccessToken());
            return ResponseEntity.ok(savedAccount);
        } catch (Exception e) {
            // Placeholder for better error handling
            return ResponseEntity.badRequest().build();
        }
    }
}
