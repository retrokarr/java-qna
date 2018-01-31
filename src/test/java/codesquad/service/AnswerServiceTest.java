package codesquad.service;

import codesquad.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static codesquad.domain.UserTest.newUser;
import static codesquad.service.QuestionServiceTest.newQuestion;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AnswerServiceTest {
    private static final String TEST_ANSWER = "test answer";

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void createTest() {
        User origin = newUser("sanjigi");
        Question question = newQuestion(origin);
        Answer answer = newAnswer(origin, TEST_ANSWER);

        when(questionRepository.findByIdAndDeletedFalse(0)).thenReturn(question);
        when(answerRepository.save(answer)).thenReturn(answer);
        Answer createdAnswer = qnaService.addAnswer(origin, 0, TEST_ANSWER);

        assertThat(createdAnswer.getContents(), is(TEST_ANSWER));
        assertThat(createdAnswer.getWriter(), is(origin));
    }

    private Answer newAnswer(User user, String testAnswer) {
        return new Answer(user, testAnswer);
    }
}
