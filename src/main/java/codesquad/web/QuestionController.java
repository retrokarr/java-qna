package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Controller
@RequestMapping("questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @Resource
    private Validator validator;

    @GetMapping("/form")
    public String form(@LoginUser User loginUser) {
        return "/qna/form";
    }

    @PostMapping("")
    public String form(@LoginUser User loginUser, String title, String contents) {
        if(!isInvalidInput(new QuestionDto(title, contents)))
            return "redirect:/qna/form";

        Question question = qnaService.create(loginUser, new Question(title, contents));
        log.debug("question has been created redirect to question page");

        return "redirect:" + question.generateUrl();
    }

    @GetMapping("/{postNo}")
    public String getPost(@PathVariable long postNo, Model model) {
        Question question = qnaService.findById(postNo);

        if(question == null)
            return "redirect:/";

        model.addAttribute("question", question);
        return "/qna/show";
    }

    @GetMapping("/{postNo}/form")
    public String getPost(@LoginUser User loginUser, @PathVariable long postNo, Model model) {
        Question question = qnaService.findById(postNo);

        if(!question.isOwner(loginUser))
            return "redirect:/";

        model.addAttribute("question", question.toQuestionDto());
        return "/qna/updateForm";
    }

    @PostMapping("/{postNo}/update")
    public String updateQuestion(@LoginUser User loginUser, @PathVariable long postNo,
                                 String title, String contents, Model model) {
        QuestionDto questionDto = new QuestionDto(title, contents);
        if(!isInvalidInput(questionDto))
            return "redirect:/";

        Question question = null;
        try {
            question = qnaService.update(loginUser, postNo, questionDto.toQuestion());
        } catch(UnAuthorizedException | NullPointerException e) {
            return "redirect:/";
        }

        model.addAttribute("question", question);
        return "/qna/show";
    }

    @PostMapping("/{postNo}")
    public String deleteQuestion(@LoginUser User loginUser, @PathVariable long postNo) {
        try {
            qnaService.deleteQuestion(loginUser, postNo);
        } catch (CannotDeleteException e) {
            return "redirect:/";
        }

        return "redirect:/";
    }

    private boolean isInvalidInput(QuestionDto question) {
        Set<ConstraintViolation<QuestionDto>> constraintViolations = validator.validate(question);

        return constraintViolations.isEmpty();
    }
}
