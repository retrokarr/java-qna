package codesquad.service;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void createTest() {
        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        Question question = new Question("title", "contents");
        question.writeBy(user);

        when(questionRepository.save(question)).thenReturn(question);
        Question createdQuestion = qnaService.create(user, question);

        assertThat(createdQuestion, is(question));
    }

    @Test
    public void findOneTest() {
        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        Question question = new Question("title", "contents");
        question.writeBy(user);

        when(questionRepository.findOne(question.getId())).thenReturn(question);
        Question foundQuestion = qnaService.findById(question.getId());

        assertThat(foundQuestion, is(question));
    }
}
