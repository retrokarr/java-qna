package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static codesquad.domain.UserTest.newUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class QuestionTest {
    private Question question;
    private User writer;

    @Before
    public void init() {
        writer = newUser("sanjigi");

        question = new Question("title", "contents");
        question.writeBy(writer);
    }

    @Test
    public void updateTest() {
        question.update(writer, new Question("updated", "updated"));
        assertThat(question.getTitle()).isEqualTo("updated");
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateTest_with_other() {
        question.update(newUser("temporal"), new Question("updated", "updated"));
    }
}
