package com.expensetracker.dto;

import lombok.Data;

/** Request body for creating / updating a user profile. */
@Data
public class ProfileRequest {
    private String displayName;
    private String bio;
    /** Full base64 data-URL string, e.g. "data:image/jpeg;base64,..." */
    private String avatarData;
}
