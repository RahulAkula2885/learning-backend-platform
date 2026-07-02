package in.rahul.learning.service;

import in.rahul.learning.commons.BaseResponse;
import in.rahul.learning.model.entity.User;
import in.rahul.learning.model.request.LoginRequest;
import in.rahul.learning.model.request.UserRequest;
import in.rahul.learning.model.response.UserResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IUserService {

    ResponseEntity<BaseResponse> createUser(UserRequest request);

    List<UserResponse> getUserDetails();

    ResponseEntity<BaseResponse> updateUser(User request);

    UserResponse getUserDetailsById(Long id);

    ResponseEntity<BaseResponse> login(LoginRequest request);

    ResponseEntity<BaseResponse> deleteUser(User request);
}
