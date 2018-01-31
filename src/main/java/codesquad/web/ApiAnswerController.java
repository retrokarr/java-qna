package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionNo}/answers")
public class ApiAnswerController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable long questionNo
            , @Valid @RequestBody AnswerDto answerDto) {
        Answer savedAnswer = qnaService.addAnswer(loginUser, questionNo, answerDto.getContents());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(savedAnswer.generateUrl()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

/*    @GetMapping("")
    public AnswersDto showAll(@PathVariable long questionNo) {
        Answers answers = qnaService.findById(questionNo);
        return answers.toAnswersDto();
    }*/

    @GetMapping("/{answerNo}")
    public AnswerDto show(@PathVariable long answerNo) {
        Answer answer = qnaService.findAnswerById(answerNo);

        return answer.toAnswerDto();
    }
}
