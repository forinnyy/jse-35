package ru.forinnyy.tm.dto.request;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class UserLogoutRequest extends AbstractUserRequest {

    public UserLogoutRequest(String token) {
        super(token);
    }

}
