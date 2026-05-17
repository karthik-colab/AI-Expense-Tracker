package com.expensetracker.controller;

import com.expensetracker.dto.ProfileRequest;
import com.expensetracker.model.UserProfile;
import com.expensetracker.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserProfileController {

    private final UserProfileRepository profileRepository;

    /** GET /api/profile?userId=xxx */
    @GetMapping
    public ResponseEntity<UserProfile> getProfile(@RequestParam UUID userId) {
        return profileRepository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** PUT /api/profile?userId=xxx  (upsert — create or update) */
    @PutMapping
    public ResponseEntity<UserProfile> saveProfile(
            @RequestParam UUID userId,
            @RequestBody ProfileRequest req) {

        UserProfile profile = profileRepository.findByUserId(userId)
                .orElse(UserProfile.builder().userId(userId).build());

        if (req.getDisplayName() != null) profile.setDisplayName(req.getDisplayName());
        if (req.getBio()         != null) profile.setBio(req.getBio());
        if (req.getAvatarData()  != null) profile.setAvatarData(req.getAvatarData());

        return ResponseEntity.ok(profileRepository.save(profile));
    }
}
