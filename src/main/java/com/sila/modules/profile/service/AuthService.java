package com.sila.modules.profile.service;

import com.sila.modules.profile.dto.req.CreateUserRequest;
import com.sila.modules.profile.dto.req.LoginRequest;
import com.sila.modules.profile.dto.req.SignUpRequest;
import com.sila.modules.profile.dto.req.UpdatePasswordReq;
import com.sila.modules.profile.dto.res.LoginResponse;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthService {
  ResponseEntity<Map<String, String>> signUp(SignUpRequest request);

  ResponseEntity<LoginResponse> signIn(@RequestBody LoginRequest req);

  ResponseEntity<LoginResponse> refreshToken(String refreshToken);

  ResponseEntity<Map<String, String>> createUser(CreateUserRequest request);

  ResponseEntity<Map<String, String>> updatePassword(UpdatePasswordReq updatePasswordReq);
}
