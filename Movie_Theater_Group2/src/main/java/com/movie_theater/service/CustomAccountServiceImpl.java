package com.movie_theater.service;

import com.movie_theater.dto.AccountDTO;
import com.movie_theater.entity.Account;
import com.movie_theater.entity.Role;
import com.movie_theater.repository.AccountRepository;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Streamable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.movie_theater.service.AccountSpecification.*;

@Service
public class CustomAccountServiceImpl implements CustomAccountService {

    private final Map<String, String> activateEmailSecret = new HashMap<>();

    private final AccountRepository accountRepository;

    private final TextService textService;

    private final EmailService emailService;

    private final ImageService imageService;

    private final CustomRoleService roleService;

    @Autowired
    public CustomAccountServiceImpl(AccountRepository accountRepository,
                                    EmailService emailService,
                                    ImageService imageService,
                                    CustomRoleService roleService,
                                    TextService textService) {
        this.accountRepository = accountRepository;
        this.emailService = emailService;
        this.imageService = imageService;
        this.roleService = roleService;
        this.textService = textService;
    }

    @Override
    public Page<Account> getAllAccounts(int pageNumber, int pageSize, Map<String, Object> filter) {
        if (pageSize < 1) throw new IllegalArgumentException("Page size must be greater than 0!");
        if (pageNumber < 0) throw new IllegalArgumentException("Page number must be greater than 0!");

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("registerDate").descending());
        Specification<Account> specification = Specification.where(null);
        if (filter.containsKey("fullName")) {
            specification = specification.and(withFullNameContains((String) filter.get("fullName")));
        }
        if (filter.containsKey("status")) {
            specification = specification.and(withStatus((String) filter.get("status")));
        }
        if (filter.containsKey("role")) {
            String roles = (String) filter.get("role");
            Optional<Specification<Account>> result = Arrays.stream(roles.split(","))
                    .map(roleService::getRoleByName)
                    .filter(Optional::isPresent)
                    .map(Optional::get).map(AccountSpecification::withRole).reduce(Specification::or);
            if (result.isPresent()) {
                specification = specification.and(result.get());
            }
        }

        return accountRepository.findAll(specification, pageable);
    }

    @Override
    public Map<String, Object> createAccount(AccountDTO accountDTO) {
        Map<String, Object> result = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        validateFullName(accountDTO.getFullName()).ifPresent(message -> errors.put("fullName", message));

        // validate date of birth
        validateDateOfBirth(accountDTO.getDateOfBirth()).ifPresent(message -> errors.put("dateOfBirth", message));

        // validate unique email
        validateEmail(accountDTO.getEmail()).ifPresent(message -> errors.put("email", message));
        if (accountRepository.exists(withEmail(accountDTO.getEmail()))) {
            errors.put("email", "Email exist!");
        }

        validateAddress(accountDTO.getAddress()).ifPresent(message -> errors.put("address", message));

        validatePhoneNumber(accountDTO.getPhoneNumber()).ifPresent(message -> errors.put("phoneNumber", message));

        if (accountDTO.getImgFile() != null && !accountDTO.getImgFile().isEmpty()) {
            imageService.uploadImage(accountDTO.getImgFile()).ifPresentOrElse(accountDTO::setImage,
                    () -> errors.put("image", "Cannot upload!"));
        }

        // validate role
        if (accountDTO.getRole() == null) {
            accountDTO.setRole("ROLE_EMPLOYEE");
        } else {
            if (!roleService.existRoleName(accountDTO.getRole())) {
                errors.put("image", "Role not exist!");
            }
        }

        // validate fail
        if (!errors.isEmpty()) {
            result.put("error", errors);
            return result;
        }

        String uniqueUsername = generateUniqueUsername(accountDTO.getFullName());
        accountDTO.setUsername(uniqueUsername);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String randomPassword = textService.generateRandomPassword();
        accountDTO.setPassword(bCryptPasswordEncoder.encode(randomPassword));
        accountDTO.setRegisterDate(LocalDate.now());
        accountDTO.setScore(0);

        Account account = convertDTOtoAccount(accountDTO);

        // deactivate by default
        account.setDeleted(true);

        try {
            account = accountRepository.save(account);
            String secret = UUID.randomUUID().toString();
            activateEmailSecret.put(secret, account.getEmail());
            emailService.sendActivateEmail(account.getEmail(), account.getUsername(), secret, randomPassword);
            result.put("data", account);
        } catch (Exception e) {
            errors.put("message", e.getMessage());
        }
        if (!errors.isEmpty()) {
            result.put("error", errors);
        }
        return result;
    }

    private String generateUniqueUsername(String fullname) {
        String normalize = textService.normalizeFullname(fullname);
        int index = 0;
        String suffix = "";
        while (accountRepository.exists(withUsername(textService.normalizeFullname(normalize + suffix)))) {
            suffix = String.valueOf(index++);
        }
        return normalize + suffix;
    }

    private Optional<String> validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            return Optional.of("Username cannot be null!");
        }
        if (accountRepository.exists(withUsername(username))) {
            return Optional.of("Username already exist!");
        }
        if (username.length() < 4) {
            return Optional.of("Username length > 4!");
        }
        Pattern p = Pattern.compile("[\\p{Alpha}]*[\\p{Punct}][\\p{Alpha}]*");
        Matcher m = p.matcher(username);
        if (m.matches()) {
            System.out.println("Username cannot contains special character!");
        }
        return Optional.empty();
    }

    private Optional<String> validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return Optional.of("Phone number cannot be null!");
        }
        if (!phoneNumber.matches("\\d+")) {
            return Optional.of("Invalid phone number!");
        }
        return Optional.empty();
    }

    private Optional<String> validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return Optional.of("Password cannot be null!");
        }
        if (password.length() < 8) {
            return Optional.of("Password length >= 8!");
        }
        return Optional.empty();
    }

    private Optional<String> validateAddress(String address) {
        if (address == null || address.isEmpty()) {
            return Optional.of("Address cannot be null!");
        }
        return Optional.empty();
    }

    private Optional<String> validateFullName(String fullname) {
        if (fullname == null || fullname.isEmpty()) {
            return Optional.of("Full name cannot be null!");
        }
        return Optional.empty();
    }

    private Optional<String> validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return Optional.of("Email cannot be null!");
        }
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            return Optional.of("Invalid email address!");
        }
        if (accountRepository.exists(withEmail(email))) {
            return Optional.of("Email exist!");
        }
        return Optional.empty();
    }

    private Optional<String> validateDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return Optional.of("Date of birth cannot be null!");
        }
        if (dateOfBirth.isAfter(LocalDate.now())) {
            return Optional.of("Invalid date of birth!");
        }
        return Optional.empty();
    }

    @Override
    public List<AccountDTO> convertAccountsToDTO(Streamable<Account> accounts) {
        return accounts.stream().map(item -> AccountDTO.builder()
                        .accountId(item.getAccountId())
                        .fullName(item.getFullName())
                        .dateOfBirth(item.getDateOfBirth())
                        .email(item.getEmail())
                        .phoneNumber(item.getPhoneNumber())
                        .image(item.getImage())
                        .address(item.getAddress())
                        .username(item.getUsername())
                        .registerDate(item.getRegisterDate())
                        .gender(item.getGender())
                        .deleted(item.getDeleted())
                        .role(item.getRole().getRoleName())
                        .score(item.getScore())
                        .build())
                .toList();
    }

    @Override
    public Account convertDTOtoAccount(AccountDTO accountDTO) {
        return Account.builder()
                .accountId(accountDTO.getAccountId())
                .username(accountDTO.getUsername())
                .password(accountDTO.getPassword())
                .fullName(accountDTO.getFullName())
                .gender(accountDTO.getGender())
                .dateOfBirth(accountDTO.getDateOfBirth())
                .email(accountDTO.getEmail())
                .address(accountDTO.getAddress())
                .phoneNumber(accountDTO.getPhoneNumber())
                .image(accountDTO.getImage())
                .role(roleService.getRoleByName(accountDTO.getRole()).get())
                .registerDate(accountDTO.getRegisterDate())
                .score(accountDTO.getScore())
                .build();
    }

    @Override
    public String activateAccount(String secret) {
        if (!activateEmailSecret.containsKey(secret)) {
            return "Invalid Secret!";
        }
        String email = activateEmailSecret.get(secret);
        Optional<Account> accountOptional = accountRepository.findOne(withEmail(email));
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setDeleted(false);
            accountRepository.save(account);
            activateEmailSecret.remove(secret);
            return "Activate Account with Email " + email + " Successfully!";
        } else {
            return "No such account!";
        }
    }

    @Override
    public Map<String, Object> updateAccount(AccountDTO accountDTO, String currentUsername) {
        Map<String, Object> result = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        Account account = accountRepository.findById(accountDTO.getAccountId()).orElse(null);
        if (account == null) {
            errors.put("message", "Account does not exist!");
            result.put("errors", errors);
            return result;
        }

        validateFullName(accountDTO.getFullName()).ifPresent(message -> errors.put("fullName", message));
        // validate date of birth
        validateDateOfBirth(accountDTO.getDateOfBirth()).ifPresent(message -> errors.put("dateOfBirth", message));

        validateAddress(accountDTO.getAddress()).ifPresent(message -> errors.put("address", message));

        validatePhoneNumber(accountDTO.getPhoneNumber()).ifPresent(message -> errors.put("phoneNumber", message));

        if (accountDTO.getImgFile() != null && !accountDTO.getImgFile().isEmpty()) {
            imageService.uploadImage(accountDTO.getImgFile()).ifPresentOrElse(accountDTO::setImage,
                    () -> errors.put("image", "Cannot upload!"));
        }

        // validate role
        if (accountDTO.getRole() == null) {
            accountDTO.setRole("ROLE_MEMBER");
        } else {
            if (!roleService.existRoleName(accountDTO.getRole())) {
                errors.put("image", "Role not exist!");
            }
        }

        // validate fail
        if (!errors.isEmpty()) {
            result.put("error", errors);
            return result;
        }

        if (account.getUsername().equals(currentUsername) &&
                !account.getRole().getRoleName().equals(accountDTO.getRole())) {
            errors.put("role", "Cannot edit self role!");
            result.put("error", errors);
            return result;
        }

        account.setFullName(accountDTO.getFullName());
        account.setDateOfBirth(accountDTO.getDateOfBirth());
        account.setPhoneNumber(accountDTO.getPhoneNumber());
        account.setGender(accountDTO.getGender());
        account.setAddress(accountDTO.getAddress());
        account.setImage(accountDTO.getImage());
        account.setRole(roleService.getRoleByName(accountDTO.getRole()).get());

        try {
            account = accountRepository.save(account);
            result.put("data", account);
        } catch (Exception e) {
            errors.put("message", e.getMessage());
        }
        if (!errors.isEmpty()) {
            result.put("error", errors);
        }
        return result;
    }

    @Override
    public AccountDTO convertAccountToDTO(Account account) {
        return AccountDTO.builder()
                .accountId(account.getAccountId())
                .fullName(account.getFullName())
                .dateOfBirth(account.getDateOfBirth())
                .email(account.getEmail())
                .phoneNumber(account.getPhoneNumber())
                .address(account.getAddress())
                .username(account.getUsername())
                .registerDate(account.getRegisterDate())
                .gender(account.getGender())
                .image(account.getImage())
                .role(account.getRole().getRoleName())
                .build();
    }

    @Override
    public Account getAccountById(Integer id) {
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public void deactivateAccount(Integer id, String currentUsername) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) throw new RuntimeException("No such account!");
        if (account.getUsername().equals(currentUsername)) throw new RuntimeException("Cannot deactivate self!");
        account.setDeleted(true);
        accountRepository.save(account);
    }

    @Override
    public void activateAccount(Integer id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) throw new RuntimeException("No such account!");
        account.setDeleted(false);
        accountRepository.save(account);
    }

    @Override
    public String generateUsername(String fullName) {
        String result = fullName;
        result = textService.normalizeFullname(result);
        int index = 0;
        String tmp = result;
        while (accountRepository.exists(withUsername(tmp))) {
            tmp = result + index;
            index++;
        }
        result = tmp;
        return result;
    }
}
