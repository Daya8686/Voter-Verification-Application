package com.voterverification.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.voterverification.application.DTO.AdminRegistrationDto;
import com.voterverification.application.DTO.AllUsersResponseDto;
import com.voterverification.application.DTO.ContactDto;
import com.voterverification.application.DTO.CreateContactDto;
import com.voterverification.application.DTO.ElectionBoardDto;
import com.voterverification.application.DTO.LoginDto;
import com.voterverification.application.DTO.UpdateUserInfoDto;
import com.voterverification.application.DTO.UserRegistrationDto;
import com.voterverification.application.Entity.Contact;
import com.voterverification.application.Entity.ElectionBoard;
import com.voterverification.application.Entity.Role;
import com.voterverification.application.Entity.UserVerificationToken;
import com.voterverification.application.Entity.Users;
import com.voterverification.application.exception.ContactServiceExceptionHandler;
import com.voterverification.application.exception.ElectionBoardExceptionHandler;
import com.voterverification.application.exception.UserServiceExceptionHandler;
import com.voterverification.application.principal.UserPrincipal;
import com.voterverification.application.repository.ContactRepository;
import com.voterverification.application.repository.ElectionBoardRepository;
import com.voterverification.application.repository.UserVerificationTokenRepository;
import com.voterverification.application.repository.UsersRepoistory;
import com.voterverification.application.util.ApiResponseHandler;
import com.voterverification.application.util.JWTTokenResponse;
import com.voterverification.application.util.UserPrincipalObject;

import io.jsonwebtoken.JwtException;
//import io.lettuce.core.RedisConnectionException;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

@Service
public class UserService {
	
	@Autowired
	private UsersRepoistory userRepoistory;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private ElectionBoardRepository electionBoardRepository;
	
	@Autowired
	private UserVerificationTokenRepository userVerificationTokenRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private JWTService jwtService;
	
//	@Autowired
//	private RedisSessionService redisSessionService;
	
	@Value("${app.user-verification-url}")
	private String userVerificationUrl;
	
	
	
	

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	@Transactional
	@Modifying
	public ApiResponseHandler registerAdmin(AdminRegistrationDto adminRegistrationDto) throws MessagingException {
		String username = adminRegistrationDto.getUsername();
		Users byUsername = userRepoistory.findByUsername(username);
		if(byUsername !=null) {
			throw new UserServiceExceptionHandler("Username is already exists!! Please try another username!!", HttpStatus.CONFLICT);
		}
		Contact contactEmail = contactRepository.findByEmail(adminRegistrationDto.getContactDto().getEmail());
		if(contactEmail!=null) {
			throw new ContactServiceExceptionHandler("Email with this ID: '"+contactEmail.getEmail()+"' already exists", HttpStatus.CONFLICT);
		}
		Contact contactByMobile = contactRepository.findByMobile(adminRegistrationDto.getContactDto().getMobile());
		if(contactByMobile!=null) {
			throw new ContactServiceExceptionHandler("Mobile number is already registered with us", HttpStatus.CONFLICT);
		}
		
		Users user = modelMapper.map(adminRegistrationDto, Users.class);
		user.setElectionBoard(null);
		user.setRole(Role.ADMIN);
		user.setUserActive(false);
		user.setUserValid(false);
		user.setVerifiedVotersCount(0);
		user.setFraudDetectedCountByUser(0);
		user.setUserLoggedIn(false);
		user.setPassword(encoder.encode(adminRegistrationDto.getPassword()));
		CreateContactDto contactDto = adminRegistrationDto.getContactDto();
		Contact contact = modelMapper.map(contactDto, Contact.class);
		Contact savedContact = contactRepository.save(contact);
		user.setContact(savedContact);
		Users savedUser = userRepoistory.save(user);
		
		verifyEmail(savedContact.getEmail());
		 
		 AllUsersResponseDto userDto = modelMapper.map(savedUser, AllUsersResponseDto.class);
		 ContactDto savedContactDto = modelMapper.map(savedContact, ContactDto.class);
		 userDto.setContactDto(savedContactDto);
		 return new ApiResponseHandler(userDto, HttpStatus.CREATED.value(), "Success");
		
		
	}
		@Transactional
		@Modifying
		public ApiResponseHandler registerUser( UserRegistrationDto userRegistrationDto) {
			Users userByUsername = userRepoistory.findByUsername(userRegistrationDto.getUsername());
			if(userByUsername!=null) {
				throw new UserServiceExceptionHandler("User with this user name already exist!!", HttpStatus.CONFLICT);
			}
			Contact contactEmail = contactRepository.findByEmail(userRegistrationDto.getContactDto().getEmail());
			
			if(contactEmail!=null) {
				throw new ContactServiceExceptionHandler("Email with this ID: '"+contactEmail.getEmail()+"' already exists", HttpStatus.CONFLICT);
			}
			Contact contactByMobile = contactRepository.findByMobile(userRegistrationDto.getContactDto().getMobile());
			if(contactByMobile!=null) {
				throw new ContactServiceExceptionHandler("Mobile number is already begining used by a user!!", HttpStatus.CONFLICT);
			}
			ElectionBoard electionBoardCode = electionBoardRepository.findByElectionBoardCode(userRegistrationDto.getElectionBoardCode());
			if(electionBoardCode==null) {
				throw new ElectionBoardExceptionHandler("Election Board code is invalid!!", HttpStatus.NOT_FOUND);
			}
			
			Users user = modelMapper.map(userRegistrationDto, Users.class);
			CreateContactDto contactDto = userRegistrationDto.getContactDto();
			Contact contact = modelMapper.map(contactDto, Contact.class);
			user.setElectionBoard(electionBoardCode);
			user.setPassword(encoder.encode(userRegistrationDto.getPassword()));
			Contact savedContact = contactRepository.save(contact);
			user.setContact(savedContact);
			user.setUserActive(false);
			user.setUserValid(false);
			user.setRole(Role.VERIFIER);
			user.setVerifiedVotersCount(0);
			user.setFraudDetectedCountByUser(0);
			user.setUserLoggedIn(false);
			Users savedUser = userRepoistory.save(user);
			
			verifyEmail(contact.getEmail());
			
			ContactDto contactDto2 = modelMapper.map(savedContact, ContactDto.class);
			AllUsersResponseDto userDto = modelMapper.map(savedUser, AllUsersResponseDto.class);
			userDto.setContactDto(contactDto2);
			return new ApiResponseHandler(userDto, HttpStatus.CREATED.value(), "Success");

		}
		
		@Transactional
		public Object login(LoginDto userLoginDTO) {
			Users checkUser = userRepoistory.findByUsername(userLoginDTO.getUsername());
			
			if (checkUser == null) {
				throw new UserServiceExceptionHandler("User not found with this username " + userLoginDTO.getUsername(),
						HttpStatus.BAD_REQUEST);
			}
			UserVerificationToken byUser = userVerificationTokenRepository.findByUser(checkUser);
			
			if(!checkUser.isUserValid()) {
			
				if(byUser!=null) {
					return new ApiResponseHandler("Verification Email is already set to your registered Email! please check it.", HttpStatus.FORBIDDEN.value(), "Check your Email");
				}
		        return verifyEmail(checkUser.getContact().getEmail());	
			}
			
			if (!checkUser.isUserActive()) {
				
				throw new UserServiceExceptionHandler(
						"Username with " + userLoginDTO.getUsername() + " is locked!! Please contact application admin",
						HttpStatus.FORBIDDEN);
			}
			
//			if (redisSessionService.isUserLoggedIn(userLoginDTO.getUsername())) {
//				return new ApiResponseHandler("You are already logged in on another device.", HttpStatus.UNAUTHORIZED.value(), "Tried to login illegaly");
//	            
//	        }
			 Users byUsername = userRepoistory.findByUsername(userLoginDTO.getUsername());
		        if(byUsername.isUserLoggedIn()) {
		        	return new ApiResponseHandler("You are already logged in on another device.", HttpStatus.UNAUTHORIZED.value(), "Tried to login illegaly");
		        }

			try {
			Authentication auth = authManager.authenticate(
					new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword()));
			
//			if (auth.isAuthenticated()) {  //if condition is commented because we are handling by try catch
				UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
				Users user = userPrincipal.getUser();
				String token = jwtService.generateToken(user);
//	            String username2 = jwtService.extractUserName(token);
//	            System.out.println(username2);
				long expirationTime = jwtService.getExpirationTime(token);
//				System.out.println(expirationTime);
//				redisSessionService.storeUserSession(user.getUsername(), token, expirationTime);
				emailService.sendLoginSuccessEmail(user.getContact().getEmail(),user.getFirstName());
				JWTTokenResponse jwtTokenResponse = new JWTTokenResponse(token);
				user.setUserLoggedIn(true);
				userRepoistory.save(user);
				return jwtTokenResponse;
//			}
			}
			catch(BadCredentialsException badCredentialsException){
				
					throw new UserServiceExceptionHandler("Password is invalid!!",HttpStatus.FORBIDDEN );	
			}
			catch(Exception e){
				throw new UserServiceExceptionHandler("Login failed", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		@Transactional
		public ApiResponseHandler logoutUser() {
			try {
			Users user = UserPrincipalObject.getUser();

//			 boolean startsWith = token.startsWith("Bearer ");
//			 String trimedToken=token;
//			 if(startsWith) {
//				  trimedToken = token.substring(7).trim();
//			 }
//			 System.out.println(trimedToken);
//			 redisSessionService.blacklistToken(trimedToken, jwtService.getExpirationTime(trimedToken));
//			 
//			 redisSessionService.removeUserSession(user.getUsername());
			 
			 emailService.sendLogoutSuccessEmail(user.getContact().getEmail(), user.getFirstName());
//			 user.setUserActive(false);
//			 userRepoistory.save(user);
			 user.setUserLoggedIn(false);
			 userRepoistory.save(user);
			 
			 return new ApiResponseHandler("Logged out successfully.", HttpStatus.OK.value(), "Success");
			 } 
//			catch (RedisConnectionException e) {
//			        e.printStackTrace();
//			        throw new UserServiceExceptionHandler("Failed to connect to Redis. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
//			    }
			catch (JwtException e) {
			        e.printStackTrace();
			        throw new UserServiceExceptionHandler("Invalid token or token expiration issue.", HttpStatus.BAD_REQUEST);
			    } catch (Exception ex) {
			        ex.printStackTrace();
			        throw new UserServiceExceptionHandler("Unable to logout and remove user from redis cache!!!", HttpStatus.INTERNAL_SERVER_ERROR);
			    }
		}
		
		@Transactional
		@Modifying
		public ApiResponseHandler userVerification(String token) {
			Optional<UserVerificationToken> tokenInfo = userVerificationTokenRepository.findByToken(token);
			if (tokenInfo.isEmpty() || tokenInfo.get().getExpirationTime().isBefore(LocalDateTime.now())) {
	            return new ApiResponseHandler("Invalid Link or Timeout", HttpStatus.BAD_REQUEST.value(), "Failed");
	        }
			Users user = tokenInfo.get().getUser();
			user.setUserValid(true);
			Users savedUser = userRepoistory.save(user);
			userVerificationTokenRepository.deleteByToken(token);
			try {
				emailService.sendUserVerificationSuccessEmail(savedUser.getContact().getEmail(), savedUser.getFirstName());
			} catch (MessagingException e) {
				throw new UserServiceExceptionHandler("Something went wrong!!", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			return new ApiResponseHandler("User is validated successfully!", HttpStatus.OK.value(), "Success");
			
		}

		
		private ApiResponseHandler verifyEmail(String email) {
			
			String token = UUID.randomUUID().toString();
			
			Optional<Users> userByEmail = userRepoistory.findByEmail(email);
			if(userByEmail.isEmpty()) {
				throw new UserServiceExceptionHandler("User email is not present in user table", HttpStatus.NOT_FOUND);
			}
			UserVerificationToken verificationToken = new UserVerificationToken(userByEmail.get(), token);
			userVerificationTokenRepository.save(verificationToken);

			String verificationLink = userVerificationUrl+"/" + token;
			
			try {
				emailService.sendUserVerificationEmail(email, verificationLink, userByEmail.get().getFirstName());
			} catch (MessagingException e) {
				throw new UserServiceExceptionHandler("Something went wrong!!", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			return new ApiResponseHandler("User Verification link has been sent!!", HttpStatus.OK.value(),"Success");
		}
		
		
		
		private ApiResponseHandler verifyUpdateEmail(String email) {
			
			String token = UUID.randomUUID().toString();
			Users user = UserPrincipalObject.getUser();
			UserVerificationToken byUser = userVerificationTokenRepository.findByUser(user);
			if(byUser !=null) {
				return new ApiResponseHandler("You can only change your email after 24 hrs of account verification!", HttpStatus.BAD_REQUEST.value(),"Previous request is active!!");
			}
			UserVerificationToken verificationToken = new UserVerificationToken(user, token);
			userVerificationTokenRepository.save(verificationToken);

			String verificationLink = userVerificationUrl+"/" + token+"/"+email;
			
			try {
				emailService.sendUserVerificationEmail(email, verificationLink, user.getFirstName());
			} catch (MessagingException e) {
				throw new UserServiceExceptionHandler("Something went wrong!!", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			return new ApiResponseHandler("User Verification link has been sent!!", HttpStatus.OK.value(),"Success");
		}
		
		
		@Transactional
		public ApiResponseHandler getUserInformation() {
			Users user = UserPrincipalObject.getUser();
			System.out.println(user.getFirstName());
			AllUsersResponseDto usersResponseDto = modelMapper.map(user, AllUsersResponseDto.class);
			if(user.getElectionBoard()!=null) {
			ElectionBoardDto electionBoardDto = modelMapper.map(user.getElectionBoard(), ElectionBoardDto.class);
			usersResponseDto.setElectionBoardDto(electionBoardDto);
			}
			
			ContactDto contactDto = modelMapper.map(user.getContact(), ContactDto.class);
			usersResponseDto.setContactDto(contactDto);
			
			return new ApiResponseHandler(usersResponseDto, HttpStatus.OK.value(), "Success");
		}
		@Transactional
		@Modifying
		public ApiResponseHandler updateUserInfo(UpdateUserInfoDto updateUserInfoDto) {
			Users user = UserPrincipalObject.getUser();
			if(!user.getContact().getEmail().equals(updateUserInfoDto.getContactDto().getEmail())) {
			Optional<Users> byEmail = userRepoistory.findByEmail(updateUserInfoDto.getContactDto().getEmail());
			if(byEmail.isPresent()) {
				throw new UserServiceExceptionHandler("User with this Email already present", HttpStatus.CONFLICT);
			}
			ApiResponseHandler verifyUpdateEmail = verifyUpdateEmail(updateUserInfoDto.getContactDto().getEmail());
			if(verifyUpdateEmail.getHttpStatus()==400) {
				throw new UserServiceExceptionHandler(verifyUpdateEmail.getMessage()+"---->" +verifyUpdateEmail.getData(), HttpStatus.BAD_REQUEST);
			}
			Contact contact =user.getContact();
			contact.setMobile(updateUserInfoDto.getContactDto().getMobile());
			Contact savedContact = contactRepository.save(contact);
			user.setContact(savedContact);
			
			}
			Contact contact = user.getContact();
			contact.setMobile(updateUserInfoDto.getContactDto().getMobile());
			Contact savedContact = contactRepository.save(contact);
			user.setContact(savedContact);
			user.setFirstName(updateUserInfoDto.getFirstName());
			user.setLastName(updateUserInfoDto.getLastName());
			user.setGender(updateUserInfoDto.getGender());
			if(!user.getUsername().equals(updateUserInfoDto.getUsername())) {
				Users byUsername = userRepoistory.findByUsername(updateUserInfoDto.getUsername());
				if(byUsername!=null) {
					throw new UserServiceExceptionHandler("User with this name is already present: ", HttpStatus.CONFLICT);
				}
				user.setUsername(updateUserInfoDto.getUsername());
			}
			
			
			Users savedUser = userRepoistory.save(user);
			AllUsersResponseDto responseDto = modelMapper.map(savedUser, AllUsersResponseDto.class);
			ContactDto contactDto = modelMapper.map(savedContact, ContactDto.class);
			responseDto.setContactDto(contactDto);
			ElectionBoardDto electionBoardDto = modelMapper.map(user.getElectionBoard(), ElectionBoardDto.class);
			responseDto.setElectionBoardDto(electionBoardDto);
			return new ApiResponseHandler(responseDto, HttpStatus.OK.value(), "Success");
			
		}
		
		@Transactional
		@Modifying
		public ApiResponseHandler verifyAndUpdateEmail(String token, String email) {
			Optional<UserVerificationToken> byToken = userVerificationTokenRepository.findByToken(token);
			if(byToken.isEmpty() || byToken.get().getExpirationTime().isBefore(LocalDateTime.now())) {
				throw new UserServiceExceptionHandler("Token is invaild or expired!!", HttpStatus.BAD_REQUEST);
			}
			UserVerificationToken userVerificationToken = byToken.get();
			Users user = userVerificationToken.getUser();
			user.setUserValid(true);
			Contact contact = user.getContact();
			contact.setEmail(email);
			Contact savedContact = contactRepository.save(contact);
			user.setContact(savedContact);
			Users savedUser = userRepoistory.save(user);
			userVerificationTokenRepository.deleteByToken(token);
			try {
				emailService.sendUpdateEmailVerificationSuccessEmail(email, user.getFirstName());
			} catch (MessagingException e) {
				throw new UserServiceExceptionHandler("Something went wrong!!!", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			AllUsersResponseDto allUsersResponseDto = modelMapper.map(savedUser, AllUsersResponseDto.class);
			ContactDto contactDto = modelMapper.map(savedContact, ContactDto.class);
			allUsersResponseDto.setContactDto(contactDto);
			return new ApiResponseHandler(allUsersResponseDto, HttpStatus.OK.value(), "Success");
			
			
		}
		@Transactional
		public ApiResponseHandler getAllUsersAssocaitedWithElectionsBoard() {
			ElectionBoard electionBoard = UserPrincipalObject.getUser().getElectionBoard();
			List<Users> byElectionBoard = userRepoistory.findByElectionBoard(electionBoard);
			List<AllUsersResponseDto> listOfUsersDto = byElectionBoard.stream().filter(user->!user.getRole().equals(Role.ADMIN)).map(user->{
				AllUsersResponseDto usersDto = modelMapper.map(user, AllUsersResponseDto.class);
				ContactDto contactDto = modelMapper.map(user.getContact(), ContactDto.class);
				ElectionBoardDto electionBoardDto = modelMapper.map(user.getElectionBoard(), ElectionBoardDto.class);
				usersDto.setContactDto(contactDto);
				usersDto.setElectionBoardDto(electionBoardDto);
				return usersDto;
			}).collect(Collectors.toList());
			
			return new ApiResponseHandler(listOfUsersDto, HttpStatus.OK.value(), "Success");
			
		}
		
		@Transactional
		@Modifying
		public ApiResponseHandler logoutUserByAdminUsingUserId(UUID userId) {
			Optional<Users> userById = userRepoistory.findById(userId);
			if(userById.isEmpty()) {
				throw new UserServiceExceptionHandler("User Id is invalid!!", HttpStatus.NOT_FOUND);
			}
			Users users = userById.get();
			if(!users.isUserLoggedIn()) {
				throw new UserServiceExceptionHandler("User with this Id is already logged out!!", HttpStatus.BAD_REQUEST);
			}
			users.setUserLoggedIn(false);
			Users savedUser = userRepoistory.save(users);
			 try {
				emailService.sendLogoutSuccessEmail(users.getContact().getEmail(), users.getFirstName());
			} catch (MessagingException e) {
				e.printStackTrace();
				throw new UserServiceExceptionHandler("Something went wrong while sending mail!!", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			AllUsersResponseDto usersResponseDto = modelMapper.map(savedUser, AllUsersResponseDto.class);
			return new ApiResponseHandler(usersResponseDto, HttpStatus.OK.value(), "User with Id Logged out!!");
			
		}
		
		
		
		
		
}
