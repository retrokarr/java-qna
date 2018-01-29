package codesquad.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import codesquad.UnAuthenticationException;
import codesquad.security.HttpSessionUtils;
import com.sun.deploy.net.HttpRequest;
import com.sun.deploy.net.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import codesquad.domain.User;
import codesquad.dto.UserDto;
import codesquad.security.LoginUser;
import codesquad.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Resource(name = "userService")
    private UserService userService;

    @GetMapping("/form")
    public String form() {
        return "/user/form";
    }

    @PostMapping("")
    public String create(UserDto userDto) {
        userService.add(userDto);
        return "redirect:/users";
    }

    @GetMapping("")
    public String list(Model model) {
        List<User> users = userService.findAll();
        log.debug("user size : {}", users.size());
        model.addAttribute("users", users);
        return "/user/list";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("user", userService.findById(loginUser, id));
        return "/user/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, UserDto target) {
        userService.update(loginUser, id, target);
        return "redirect:/users";
    }

    @PostMapping("/login")
    public String tryLogin(HttpSession session, String userId, String password) {
        try {
            User loginUser = userService.login(userId, password);
            HttpSessionUtils.setUserInSession(session, loginUser);
        } catch (UnAuthenticationException e) {
            log.debug("Password not match or user not found");
            return "/user/login_failed";
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String tryLogout(HttpSession session) {
        HttpSessionUtils.logout(session);

        return "home";
    }
}
