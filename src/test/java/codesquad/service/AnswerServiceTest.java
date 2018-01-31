package codesquad.service;

import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import codesquad.dto.AnswerDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static codesquad.domain.UserTest.JAVAJIGI;
import static codesquad.domain.UserTest.SANJIGI;
import static codesquad.domain.UserTest.newUser;
import static codesquad.service.QuestionServiceTest.newQuestion;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AnswerServiceTest {
    private static final String ANSWER_CONETNTS = "test answer";
    private static final User OWNER = SANJIGI;
    private static final User OTHER_USER = JAVAJIGI;
    private static final Answer ANSWER = new Answer(OWNER, ANSWER_CONETNTS);
    private static final AnswerDto UPDATE_ANSWER = new AnswerDto("updated answer");

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void createTest() {
        Question question = newQuestion(OWNER);

        when(questionRepository.findByIdAndDeletedFalse(0)).thenReturn(question);
        when(answerRepository.save(ANSWER)).thenReturn(ANSWER);
        Answer createdAnswer = qnaService.addAnswer(OWNER, 0, ANSWER_CONETNTS);

        assertThat(createdAnswer.getContents(), is(ANSWER_CONETNTS));
        assertThat(createdAnswer.getWriter(), is(OWNER));
    }

    @Test
    public void updateTest() {
        initBeforeUpdateTest();

        Answer updatedAnswer = qnaService.updateAnswer(OWNER, 0, UPDATE_ANSWER);

        assertThat(updatedAnswer.getContents(), is(UPDATE_ANSWER.getContents()));
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateTest_now_owner() {
        initBeforeUpdateTest();

        qnaService.updateAnswer(OTHER_USER, 0, UPDATE_ANSWER);
    }

    public void initBeforeUpdateTest() {
        when(answerRepository.findOne(0L)).thenReturn(ANSWER);
        when(answerRepository.save(ANSWER)).thenReturn(ANSWER);
    }
}
